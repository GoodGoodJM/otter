package com.goodgoodman.otter.querygenerator.schema

class ColumnSchema(
    val name: String,
    val comment: String = "",
    val type: String,
    val constraints: List<Constraint> = listOf()
)
