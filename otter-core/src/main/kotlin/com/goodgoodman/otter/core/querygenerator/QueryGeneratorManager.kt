package com.goodgoodman.otter.core.querygenerator

object QueryGeneratorManager {
    fun getQueryGeneratorByDriverClassName(driverClassName: String): QueryGenerator {
        return when (driverClassName) {
            "org.h2.Driver" ->
                object : QueryGenerator() {}
            "com.mysql.cj.jdbc.Driver",
            "org.mariadb.jdbc.Driver" ->
                object : QueryGenerator() {}
            else ->
                object : QueryGenerator() {}
        }
    }
}