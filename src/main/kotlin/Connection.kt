package com.goodgoodman.otter

import java.sql.DriverManager

class Connection(
    private val config: OtterConfig
) : AutoCloseable {
    private val connection: java.sql.Connection

    init {
        Class.forName(config.driverClassName)
        connection = DriverManager.getConnection(
            config.url,
            config.user,
            config.password,
        ).apply {
            autoCommit = false
        }

        execute("SELECT 1;")
    }

    val driverClassName get() = config.driverClassName

    fun execute(sql: String): Boolean {
        return connection.createStatement().use { statement ->
            statement.execute(sql)
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