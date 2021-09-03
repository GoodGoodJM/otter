package com.goodgoodman.otter.core

import com.goodgoodman.otter.core.querygenerator.QueryGenerator
import com.goodgoodman.otter.core.querygenerator.QueryGeneratorManager
import java.io.File
import java.io.Reader
import java.nio.file.Paths
import javax.script.ScriptEngineManager

class Otter(
    private val migrationPath: String,
    private val queryGenerator: QueryGenerator,
    private val connection: Connection,
) {
    companion object : Logger {
        private const val migrationTableName: String = "otter_migration"

        fun from(config: OtterConfig) = Otter(
            config.migrationPath,
            QueryGeneratorManager.getQueryGeneratorByDriverClassName(config.driverClassName),
            Connection(
                config.driverClassName,
                config.url,
                config.user,
                config.password,
                config.showSql,
            )
        )
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
        val migration = engine.eval(reader) as Migration
        migration.queryGenerator = queryGenerator
        return migration
    }

    private fun loadMigrationFiles(): Array<File> {
        val directoryURL = this::class.java.classLoader.getResource(migrationPath)!!.toURI()
        val directory = Paths.get(directoryURL).toFile()
        return directory.listFiles()!!
    }

    private fun createMigrationTableIfNotExist() {
        if (!connection.checkTableExist(migrationTableName)) {
            connection.execute(
                """
                CREATE TABLE $migrationTableName (
                    version VARCHAR(255)
                )
            """.trimIndent()
            )
        }
    }

    private fun checkVersionAlreadyMigrated(name: String): Boolean {
        val resultSet = connection.executeQuery("SELECT COUNT(1) FROM $migrationTableName WHERE version='$name'")
        return resultSet.getLong(1) > 0
    }

    fun up() {
        logger.info("Start migration.")
        createMigrationTableIfNotExist()
        val migrations = loadMigrations()
        for ((name, migration) in migrations) {
            if (checkVersionAlreadyMigrated(name)) {
                logger.debug("$name is already migrated, will be skipped.")
                continue
            }
            migration.up()
            connection.transaction {
                migration.reservedQueries.forEach(connection::execute)
                connection.execute("INSERT INTO $migrationTableName(version) VALUES('$name')")
            }
            logger.debug("Success to migrate item($name)")
        }

        logger.info("Success migration.")
    }
}