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
        MigrationProcess(this, config.migrationPath, config.showSql, config.version).exec()
    }
}

object MigrationTable : IntIdTable("otter_migration") {
    val filename = varchar("filename", 255).uniqueIndex()
    val comment = varchar("comment", 255)
    private val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    fun last() = selectAll().orderBy(createdAt to SortOrder.DESC).firstOrNull()
}

object LockTable : IntIdTable("otter_lock") {
    val isLocked = bool("is_locked")
    val grantedAt = datetime("granted_at").nullable()
    val lockedBy = varchar("locked_by", 255)
}

class MigrationProcess(
    private val transaction: Transaction,
    private val migrationPath: String,
    private val showSql: Boolean,
    private val version: String,
) {
    private var hasLock: Boolean = false

    companion object : Logger

    fun exec() {
        createMigrationTable()
        lock {
            migration()
        }
    }

    private fun lock(block: () -> Unit) {
        waitForLock()
        runCatching(block)
            .also { releaseLock() }
    }

    private fun createMigrationTable() {
        with(LockTable) {
            if (!exists()) {
                SchemaUtils.create(this)
                if (selectAll().count().toInt() == 0) {
                    try {
                        transaction {
                            insert {
                                it[id] = 1
                                it[isLocked] = false
                                it[lockedBy] = ""
                                it[grantedAt] = null
                            }
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
        if (!hasLock) throw LockException("Unlocked process try to release lock")

        with(LockTable) {
            update({ id eq 1 }) {
                it[isLocked] = false
                it[lockedBy] = ""
                it[grantedAt] = null
            }
        }
    }

    private fun acquireLock(): Boolean {
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
        val latestFilename = latestAppliedMigration?.get(MigrationTable.filename) ?: "".also {
            logger.debug("There is no applied migrations. All migrations would be applied.")
        }

        val migrations = loadMigrations()
        migrations.forEach { (name, migration) ->
            when {
                shouldRollback(name, latestFilename) -> handleRollback(name, migration)
                shouldMigrate(name, latestFilename) -> handleMigration(name, migration)
                else -> logger.debug("$name is already migrated or isn't included in the target version, will be skipped.")
            }
        }
    }

    private fun shouldRollback(name: String, latestFilename: String): Boolean {
        if(version.isNullOrEmpty())
            return name <= latestFilename
        return name > version && name <= latestFilename
    }

    private fun shouldMigrate(name: String, latestFilename: String): Boolean {
        if(version.isNullOrEmpty())
            return name > latestFilename
        return name <= version && name > latestFilename
    }

    private fun handleRollback(name: String, migration: Migration) {
        logger.debug("$name is already migrated but is not suitable for the set version, will be rollback.")

        migration.down()
        migration.contexts.flatMap { it.resolve() }.forEach {
            if (showSql) logger.info(it)
            transaction.exec(it)
        }
        transaction.commit()

        MigrationTable.deleteWhere { MigrationTable.filename eq name }
    }

    private fun handleMigration(name: String, migration: Migration) {
        migration.up()
        migration.contexts.flatMap { it.resolve() }.forEach {
            if (showSql) logger.info(it)
            transaction.exec(it)
        }
        transaction.commit()

        MigrationTable.insert {
            it[filename] = name
            it[comment] = migration.comment
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

