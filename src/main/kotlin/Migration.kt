package com.goodgoodman.otter

import com.goodgoodman.otter.querygenerator.QueryGeneratorManager

abstract class Migration(
    connection: Connection
) {
    companion object : Logger

    private val queryGenerator = QueryGeneratorManager.getQueryGeneratorByDriverClassName(connection.driverClassName)

    abstract fun up()
    abstract fun down()
}
