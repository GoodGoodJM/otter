package com.goodgoodman.otter

import com.goodgoodman.otter.querygenerator.QueryGeneratorManager
import com.goodgoodman.otter.schema.SchemaMaker
import com.goodgoodman.otter.schema.TableSchema

abstract class Migration(
    config: OtterConfig
) {
    companion object : Logger

    private val queryGenerator = QueryGeneratorManager.getQueryGeneratorByDriverClassName(config.driverClassName)

    val reservedQueries: List<String> get() = _reservedQueries
    private val _reservedQueries = mutableListOf<String>()

    abstract fun up()
    abstract fun down()

    @SchemaMaker
    fun createTable(block: TableSchema.() -> Unit) {
        val tableSchema = TableSchema()
        tableSchema.block()
        _reservedQueries.add(queryGenerator.resolveTable(tableSchema))
    }

    fun dropTable(name: String) {
        _reservedQueries.add(queryGenerator.dropTable(name))
    }
}