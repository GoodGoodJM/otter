package com.goodgoodman.otter.querygenerator.schema

class TableSchema(
    val name: String,
    val comment: String = "",
) {
    val columnSchema get() = _columnSchemas
    private var _columnSchemas = mutableListOf<ColumnSchema>()
}