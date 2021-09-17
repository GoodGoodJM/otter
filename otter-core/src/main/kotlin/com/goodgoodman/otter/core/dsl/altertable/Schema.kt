package com.goodgoodman.otter.core.dsl.altertable

data class TableSchema(
    val name: String,
)

data class AddColumnSchema(
    val name: String,
    var type: String,
)
