package com.goodgoodman.otter.core

import com.goodgoodman.otter.core.querygenerator.QueryGenerator
import com.goodgoodman.otter.core.dsl.AlterColumnSchema
import com.goodgoodman.otter.core.dsl.SchemaMaker
import com.goodgoodman.otter.core.dsl.createtable.context.schema.TableSchema_

abstract class Migration {
    companion object : Logger

    internal lateinit var queryGenerator: QueryGenerator

    val reservedQueries: List<String> get() = _reservedQueries
    private val _reservedQueries = mutableListOf<String>()

    abstract fun up()
    abstract fun down()


    @SchemaMaker
    fun createTable(block: TableSchema_.() -> Unit) {
        val tableSchema = TableSchema_()
        tableSchema.block()
        // _reservedQueries.add(queryGenerator.resolveTable(tableSchema))
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