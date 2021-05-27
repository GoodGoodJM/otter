package com.goodgoodman.otter

import com.goodgoodman.otter.adapter.DbAdapter
import com.goodgoodman.otter.adapter.DbAdapterManager

abstract class Migration(
    connection: Connection
) {
    private val adapter: DbAdapter = DbAdapterManager.getAdapterByDriverClassName(connection.driverClassName)

    abstract fun up()
    abstract fun down()
}
