package com.goodgoodman.otter.core.dsl.createtable.context

import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.SchemaMaker
import java.util.*

class CreateTableContext(tableSchema: TableSchema) {
    val name = tableSchema.name

    val columnContexts: List<ColumnContext> get() = _columnContexts
    private val _columnContexts = mutableListOf<ColumnContext>()

    private fun registerColumn(columnContext: ColumnContext) {
        _columnContexts.add(columnContext)
    }

    @SchemaMaker
    fun column(name: String, block: ColumnSchema.() -> Unit): ColumnContext {
        val columnSchema = ColumnSchema(name).apply(block)
        return ColumnContext(columnSchema).also {
            registerColumn(it)
        }
    }

    class ForeignKeyContext(foreignKeySchema: ForeignKeySchema) {
        val key: String = foreignKeySchema.key
        val reference: String = foreignKeySchema.reference
    }

    class ColumnContext(columnSchema: ColumnSchema) {
        val name = columnSchema.name
        val type = columnSchema.type
        val comment = columnSchema.comment

        var foreignKeyContext: ForeignKeyContext? = null

        val constraints: EnumSet<Constraint> get() = _constraints
        private val _constraints = EnumSet.noneOf(Constraint::class.java)

        @SchemaMaker
        infix fun constraints(constraints: List<Constraint>): ColumnContext {
            constraints.forEach(this::constraints)
            return this
        }

        @SchemaMaker
        infix fun constraints(constraint: Constraint): ColumnContext {
            _constraints.add(constraint)
            return this
        }

        @SchemaMaker
        infix fun foreignKey(block: ForeignKeySchema.() -> Unit): ColumnContext {
            val foreignKeySchema = ForeignKeySchema().apply(block)
            this.foreignKeyContext = ForeignKeyContext(foreignKeySchema)
            return this
        }
    }
}