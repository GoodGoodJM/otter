package com.goodgoodman.otter.core

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.CurrentDateTime
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.Reader
import java.nio.file.Paths
import javax.script.ScriptEngineManager

class Otter(
    private val config: OtterConfig,
) {
    companion object : Logger {
        fun from(config: OtterConfig) = Otter(config)
    }

    fun up() {
        logger.info("Start migration.")
        val database = Database.connect(
            config.url,
            config.driverClassName,
            config.user,
            config.password
        )

        transaction {
            MigrationProcess(this, config.migrationPath).exec()
        }

        logger.info("Success migration.")
        TransactionManager.closeAndUnregister(database)
    }
}

object MigrationTable : IntIdTable("otter_migration") {
    val filename = varchar("filename", 255).uniqueIndex()
    val comment = varchar("comment", 255)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())

    fun last() = selectAll().orderBy(createdAt to SortOrder.DESC).firstOrNull()
}

class MigrationProcess(
    private val transaction: Transaction,
    private val migrationPath: String,
) {
    companion object : Logger

    fun exec() {
        createMigrationTable()
        migration()
    }

    private fun createMigrationTable() {
        if (MigrationTable.exists()) {
            return
        }
        SchemaUtils.create(MigrationTable)
    }

    private fun migration() {
        val latestAppliedMigration = MigrationTable.last()
        val latestFilename = if (latestAppliedMigration == null) {
            "".also {
                logger.debug("There is no applied migrations. All migrations would be applied.")
            }
        } else {
            latestAppliedMigration[MigrationTable.filename].also {
                logger.debug("There are applied migrations(last=$it).")
            }
        }

        val migrations = loadMigrations()
        for ((name, migration) in migrations) {
            if (latestFilename > name) {
                logger.debug("$name is already migrated, will be skipped.")
                continue
            }

            migration.up()
            // migration.contexts.forEach(transaction::exec)
            MigrationTable.insert {
                it[filename] = name
                it[comment] = ""
            }

            transaction.commit()
        }
    }

    private fun loadMigrations(): Map<String, Migration> {
        return loadMigrationFiles()
            .sortedBy { it.name }
            .associate {
                it.name to evalMigration(it.reader())
            }
    }

    private fun evalMigration(reader: Reader): Migration {
        val engine = ScriptEngineManager().getEngineByExtension("kts")
        return engine.eval(reader) as Migration
    }

    private fun loadMigrationFiles(): Array<File> {
        val directoryURL = this::class.java.classLoader.getResource(migrationPath)!!.toURI()
        val directory = Paths.get(directoryURL).toFile()
        return directory.listFiles()!!
    }
}