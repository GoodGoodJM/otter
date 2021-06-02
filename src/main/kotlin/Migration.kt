package com.goodgoodman.otter

import com.goodgoodman.otter.querygenerator.QueryGeneratorManager
import com.goodgoodman.otter.schema.SchemaMaker
import com.goodgoodman.otter.schema.TableSchema

abstract class Migration(
    config: OtterConfig
) {
    companion object : Logger

    private val queryGenerator = QueryGeneratorManager.getQueryGeneratorByDriverClassName(config.driverClassName)

    val tableSchemas: List<TableSchema> get() = _tableSchemas
    private val _tableSchemas = mutableListOf<TableSchema>()

    abstract fun up()
    abstract fun down()

    @SchemaMaker
    fun createTable(block: TableSchema.() -> Unit) {
        val tableSchema = TableSchema()
        tableSchema.block()
        _tableSchemas.add(tableSchema)
    }

    fun dropTable(name: String) {

    }
}