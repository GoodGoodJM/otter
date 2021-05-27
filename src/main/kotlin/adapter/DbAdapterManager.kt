package com.goodgoodman.otter.adapter

import com.goodgoodman.otter.adapter.exception.NotSupportedAdapterException

object DbAdapterManager {
    fun getAdapterByDriverClassName(driverClassName: String): DbAdapter {
        return when (driverClassName) {
            "com.mysql.cj.jdbc.Driver",
            "org.mariadb.jdbc.Driver" ->
               MySqlAdapter()
            else ->
                throw NotSupportedAdapterException(driverClassName)
        }
    }
}