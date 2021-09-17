package com.goodgoodman.otter.core.dsl.createtable

data class TableSchema(
    val name: String,
)

data class ColumnSchema(
    val name: String,
    var type: String = "",
)

data class ForeignKeySchema(
    var reference: String = "",
    var key: String = "",
)
