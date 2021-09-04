package com.goodgoodman.otter.core.dsl.createtable.context

data class TableSchema(
    val name: String,
)

data class ColumnSchema(
    var name: String,
    var type: String = "",
    var comment: String = "",
)

data class ForeignKeySchema(
    var reference: String = "",
    var key: String = "",
)
