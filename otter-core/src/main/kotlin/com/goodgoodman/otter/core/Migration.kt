package com.goodgoodman.otter.core

import com.goodgoodman.otter.core.dsl.AlterColumnSchema
import com.goodgoodman.otter.core.dsl.SchemaContext
import com.goodgoodman.otter.core.dsl.SchemaMaker
import com.goodgoodman.otter.core.dsl.createtable.CreateTableContext
import com.goodgoodman.otter.core.dsl.createtable.TableSchema

abstract class Migration {
    companion object : Logger

    val contexts: List<SchemaContext> get() = _contexts
    private val _contexts = mutableListOf<SchemaContext>()

    abstract fun up()
    abstract fun down()


    @SchemaMaker
    fun createTable(name: String, block: CreateTableContext.() -> Unit) {
        val tableSchema = TableSchema(name)
        val tableContext = CreateTableContext(tableSchema).apply(block)
        _contexts.add(tableContext)
    }

    fun dropTable(name: String) {
        // _contexts.add(queryGenerator.dropTable(name))
    }

    fun rawQuery(sql: String) {
        // _contexts.add(sql)
    }

    fun addColumn(block: AlterColumnSchema.() -> Unit) = alterColumn(AlterColumnSchema.Type.ADD, block)

    fun dropColumn(block: AlterColumnSchema.() -> Unit) = alterColumn(AlterColumnSchema.Type.DROP, block)

    private fun alterColumn(type: AlterColumnSchema.Type, block: AlterColumnSchema.() -> Unit) {
        val alterColumnSchema = AlterColumnSchema()
        alterColumnSchema.alterType = type
        alterColumnSchema.block()
        // _contexts.add(alterColumnSchema)
    }
}