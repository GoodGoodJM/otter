package io.github.goodgoodjm.otter.core.dsl.altertable

import io.github.goodgoodjm.otter.core.dsl.AlterColumnSchema
import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.SchemaContext
import io.github.goodgoodjm.otter.core.dsl.SchemaMaker

class AlterTableContext(tableSchema: TableSchema) : SchemaContext {
    val name = tableSchema.name
    lateinit var columnContext: ColumnContext

    @SchemaMaker
    fun addColumn(name: String, block: AlterColumnSchema.() -> Unit): AlterTableContext {
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

    class ColumnContext(columnSchema: AlterColumnSchema) {
        val name = columnSchema.name
    }

    override fun resolve(): List<String> {
        TODO("Not yet implemented")
    }
}