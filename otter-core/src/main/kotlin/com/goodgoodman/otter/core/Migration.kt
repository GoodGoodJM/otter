package com.goodgoodman.otter.core

import com.goodgoodman.otter.core.querygenerator.QueryGenerator
import com.goodgoodman.otter.core.dsl.AlterColumnSchema
import com.goodgoodman.otter.core.dsl.SchemaMaker
import com.goodgoodman.otter.core.dsl.createtable.context.CreateTableContext
import com.goodgoodman.otter.core.dsl.createtable.context.TableSchema

abstract class Migration {
    companion object : Logger

    internal lateinit var queryGenerator: QueryGenerator

    val reservedQueries: List<String> get() = _reservedQueries
    private val _reservedQueries = mutableListOf<String>()

    abstract fun up()
    abstract fun down()


    @SchemaMaker
    fun createTable(name: String, block: CreateTableContext.() -> Unit) {
        val tableSchema = TableSchema(name)
        val tableContext = CreateTableContext(tableSchema).apply(block)
        _reservedQueries.add(queryGenerator.generateCreateTable(tableContext))
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