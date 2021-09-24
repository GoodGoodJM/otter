package io.github.goodgoodjm.otter.core.dsl.createtable

data class TableSchema(
    val name: String,
)

data class ColumnSchema(
    val name: String,
    var type: String = "",
    var comment: String = "",
)

data class ForeignKeySchema(
    var reference: String = "",
    var key: String = "",
)
