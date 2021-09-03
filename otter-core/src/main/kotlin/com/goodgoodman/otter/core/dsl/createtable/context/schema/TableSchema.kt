package com.goodgoodman.otter.core.dsl.createtable.context.schema

import com.goodgoodman.otter.core.dsl.SchemaMaker

@SchemaMaker
class TableSchema_ {
    var name: String = ""

    val columnSchemas: List<ColumnSchema_> get() = _columnSchemas
    private val _columnSchemas = mutableListOf<ColumnSchema_>()

    @SchemaMaker
    fun column(block: ColumnSchema_.() -> Unit): ColumnSchema_ {
        val columnSchema = ColumnSchema_()
        columnSchema.block()
        columnSchema.tableName = name
        _columnSchemas.add(columnSchema)
        return columnSchema
    }

    override fun toString(): String {
        return "TableSchema(name='$name', _columnSchemas=$_columnSchemas)"
    }
}