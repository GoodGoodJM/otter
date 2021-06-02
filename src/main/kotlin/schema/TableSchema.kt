package com.goodgoodman.otter.schema

@SchemaMaker
class TableSchema {
    var name: String = ""
    var comment: String = ""

    val columnSchema: List<ColumnSchema> get() = _columnSchemas
    private val _columnSchemas = mutableListOf<ColumnSchema>()

    @SchemaMaker
    fun column(block: ColumnSchema.() -> Unit) {
        val columnSchema = ColumnSchema()
        columnSchema.block()
        _columnSchemas.add(columnSchema)
    }

    override fun toString(): String {
        return "TableSchema(name='$name', comment='$comment', columnSchema=$columnSchema)"
    }
}