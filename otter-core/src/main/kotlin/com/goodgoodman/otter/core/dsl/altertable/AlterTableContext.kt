package com.goodgoodman.otter.core.dsl.altertable

import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.SchemaContext
import com.goodgoodman.otter.core.dsl.SchemaMaker

class AlterTableContext(tableSchema: TableSchema) : SchemaContext {
    val name = tableSchema.name
    lateinit var columnContext: ColumnContext

    @SchemaMaker
    fun addColumn(name: String, block: AddColumnSchema.() -> Unit): AlterTableContext {
        return this
    }

    @SchemaMaker
    fun dropColumn(name: String) {

    }

    @SchemaMaker
    infix fun constraints(constraints: List<Constraint>): AlterTableContext {
        return this
    }

    @SchemaMaker
    infix fun constraints(constraint: Constraint): AlterTableContext {
        return this
    }

    class ColumnContext(columnSchema: AddColumnSchema) {
        val name = columnSchema.name
    }
}