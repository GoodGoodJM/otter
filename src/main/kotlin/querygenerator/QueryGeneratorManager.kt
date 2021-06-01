package com.goodgoodman.otter.querygenerator

import com.goodgoodman.otter.querygenerator.exception.NotSupportedAdapterException

object QueryGeneratorManager {
    fun getQueryGeneratorByDriverClassName(driverClassName: String): QueryGenerator {
        return when (driverClassName) {
            "org.h2.Driver" ->
                H2QueryGenerator()
            "com.mysql.cj.jdbc.Driver",
            "org.mariadb.jdbc.Driver" ->
                MySqlQueryGenerator()
            else ->
                throw NotSupportedAdapterException(driverClassName)
        }
    }
}