package io.github.goodgoodjm.otter.core

class OtterConfig(
    val migrationPath: String,
    val driverClassName: String,
    val url: String,
    val user: String,
    val password: String,
    val showSql: Boolean,
)