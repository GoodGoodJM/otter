package com.goodgoodman.otter

import com.goodgoodman.otter.querygenerator.QueryGeneratorManager
import com.goodgoodman.otter.schema.AlterColumnSchema
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

    fun rawQuery(sql: String) {
        _reservedQueries.add(sql)
    }

    fun addColumn(block: AlterColumnSchema.() -> Unit) = alterColumn(AlterColumnSchema.Type.ADD, block)

    fun dropColumn(block: AlterColumnSchema.() -> Unit) = alterColumn(AlterColumnSchema.Type.DROP, block)

    private fun alterColumn(type: AlterColumnSchema.Type, block: AlterColumnSchema.() -> Unit) {
        val alterColumnSchema = AlterColumnSchema()
        alterColumnSchema.alterType = type
        alterColumnSchema.block()
        _reservedQueries.add(queryGenerator.resolveAlterColumn(alterColumnSchema))
    }
}