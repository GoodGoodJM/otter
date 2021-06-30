package com.goodgoodman.otter.core.schema

@SchemaMaker
class TableSchema {
    var name: String = ""

    val columnSchemas: List<ColumnSchema> get() = _columnSchemas
    private val _columnSchemas = mutableListOf<ColumnSchema>()

    @SchemaMaker
    fun column(block: ColumnSchema.() -> Unit) {
        val columnSchema = ColumnSchema()
        columnSchema.block()
        columnSchema.tableName = name
        _columnSchemas.add(columnSchema)
    }

    override fun toString(): String {
        return "TableSchema(name='$name', _columnSchemas=$_columnSchemas)"
    }
}