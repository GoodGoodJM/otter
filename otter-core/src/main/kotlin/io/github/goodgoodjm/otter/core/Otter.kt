package io.github.goodgoodjm.otter.core

import io.github.goodgoodjm.otter.core.resourceresolver.ResourceResolver
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.Reader
import java.net.InetAddress
import java.time.LocalDateTime
import javax.script.ScriptEngineManager

class Otter(
    private val config: OtterConfig,
) {
    companion object : Logger {
        fun from(config: OtterConfig) = Otter(config)
    }

    private fun migrationScope(block: Transaction.() -> Unit) {
        logger.info("Start migration.")
        val database = Database.connect(
            config.url,
            config.driverClassName,
            config.user,
            config.password
        )

        transaction {
            block()
        }

        logger.info("Success migration.")
        TransactionManager.closeAndUnregister(database)
    }

    fun up() = migrationScope {
        MigrationProcess(this, config.migrationPath, config.showSql).exec()
    }
}

object MigrationTable : IntIdTable("otter_migration") {
    val filename = varchar("filename", 255).uniqueIndex()
    val comment = varchar("comment", 255)
    private val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())

    fun last() = selectAll().orderBy(createdAt to SortOrder.DESC).firstOrNull()
}

object LockTable : IntIdTable("otter_lock") {
    val isLocked = bool("is_locked")
    val grantedAt = datetime("granted_at")
    val lockedBy = varchar("locked_by", 255)
}

class MigrationProcess(
    private val transaction: Transaction,
    private val migrationPath: String,
    private val showSql: Boolean,
) {
    private var hasLock: Boolean = false

    companion object : Logger

    fun exec() {
        createMigrationTable()
        waitForLock()
        migration()
        releaseLock()
    }

    private fun createMigrationTable() {
        with(LockTable) {
            if (!exists()) {
                SchemaUtils.create(this)
                if (selectAll().count() == 0L) {
                    try {
                        transaction {
                            insert {
                                it[id] = 1
                                it[isLocked] = false
                                it[lockedBy] = ""
                                it[grantedAt] = LocalDateTime.MIN
                            }
                            this.commit()
                        }
                    } catch (e: ExposedSQLException) {
                        logger.error("Lock table initialize error - ", e)
                    }
                }
            }
        }

        if (!MigrationTable.exists()) {
            SchemaUtils.create(MigrationTable)
        }
    }

    private fun waitForLock() {
        var hasLock = false
        runBlocking {
            val loopLimit = 60
            var count = 0
            while (!hasLock && count < loopLimit) {
                count++
                hasLock = acquireLock()
                if (!hasLock) {
                    logger.info("Waiting for lock...(${count})")
                    delay(1_000)
                }
            }
        }
        if (!hasLock) {
            val lockedBy = with(LockTable) { select { isLocked eq true }.first()[lockedBy] }
            throw LockException("Could not get a database lock. Currently locked by $lockedBy")
        }
    }

    private fun releaseLock() {
        with(LockTable) {
            update({ id eq 1 }) {
                it[isLocked] = false
                it[lockedBy] = ""
                it[grantedAt] = LocalDateTime.MIN
            }
        }
    }

    private fun acquireLock(): Boolean {
        if (hasLock) {
            return true
        }

        val isLocked = LockTable.select { LockTable.id eq 1 }.first()[LockTable.isLocked]

        if (isLocked) return false
        val affectedCount = with(LockTable) {
            update({ (id eq 1) and (this@with.isLocked eq false) }) {
                it[this.isLocked] = true
                it[grantedAt] = CurrentDateTime
                it[lockedBy] = InetAddress.getLocalHost().run { "$hostName ($hostAddress)" }
            }
        }
        hasLock = affectedCount != 0
        return hasLock
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
            if (latestFilename >= name) {
                logger.debug("$name is already migrated, will be skipped.")
                continue
            }

            migration.up()
            migration.contexts.flatMap { it.resolve() }.forEach {
                if (showSql) logger.info(it)
                transaction.exec(it)
            }

            MigrationTable.insert {
                it[filename] = name
                it[comment] = migration.comment
            }

            transaction.commit()
        }
    }

    private fun loadMigrations(): Map<String, Migration> = ResourceResolver().resolveEntries(migrationPath)
        .sortedBy { it }
        .also { logger.debug("Target files : $it") }
        .associateWith { this::class.java.classLoader.getResource(it) }
        .filterValues { it != null }
        .mapKeys { it.key.split("/").last() }
        .mapValues { evalMigration(it.value!!.openStream().reader()) }

    private fun evalMigration(reader: Reader): Migration {
        val engine = ScriptEngineManager().getEngineByExtension("kts")
        return engine.eval(reader) as Migration
    }
}

