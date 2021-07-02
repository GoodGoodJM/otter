package com.goodgoodman.otter.core

import java.sql.DriverManager
import java.sql.ResultSet

class Connection(
    val driverClassName: String,
    private val url: String,
    private val user: String,
    private val password: String,
    private val showSql: Boolean,
) : AutoCloseable {
    companion object : Logger

    private val connection: java.sql.Connection

    init {
        Class.forName(driverClassName)
        connection = DriverManager.getConnection(
            url,
            user,
            password,
        ).apply {
            autoCommit = false
        }

        execute("SELECT 1;")
    }

    fun logSql(sql: String) {
        if (!showSql) return
        logger.debug(sql)
    }

    fun execute(sql: String): Boolean {
        logSql(sql)
        return connection.createStatement().use { statement ->
            statement.execute(sql)
        }
    }

    fun executeQuery(sql: String): ResultSet {
        logSql(sql)
        return connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(sql)
            resultSet.next()
            resultSet
        }

    }

    fun checkTableExist(tableName: String): Boolean {
        return connection.metaData.getTables(null, null, tableName, null).use {
            it.next()
        }
    }

    fun transaction(block: Connection.() -> Unit) {
        try {
            block()
            connection.commit()
        } catch (e: Exception) {
            connection.rollback()
            connection.close()
            throw e
        }
    }

    override fun close() {
        connection.commit()
    }
}