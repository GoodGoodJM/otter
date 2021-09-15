package com.goodgoodman.otter.core.dsl.createtable

import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.SchemaContext
import com.goodgoodman.otter.core.dsl.SchemaMaker
import com.goodgoodman.otter.core.querygenerator.QueryGenerator
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.ForeignKeyConstraint
import org.jetbrains.exposed.sql.Table
import java.util.*

class CreateTableContext(tableSchema: TableSchema) : SchemaContext {
    companion object {
        val REGEX = """([\w]+)\([\w]+\)""".toRegex()
    }

    val table = Table(tableSchema.name)

    private fun registerColumn(columnContext: ColumnContext) {
        val columnType = when (val type: Any = columnContext.type) {
            is String -> object : ColumnType() {
                override fun sqlType(): String = type
            }
            else -> throw Exception("ColumnType($type) is not supported")
        }
        val column = table.registerColumn<Any>(columnContext.name, columnType)
        if (columnContext.foreignKeyContext != null) {
            val foreignKeyContext = columnContext.foreignKeyContext!!
            val (targetTable, targetColumnName) = QueryGenerator.REGEX.find(foreignKeyContext.reference)!!.destructured
            val targetColumn = Table(targetTable).registerColumn<Any>(targetColumnName, column.columnType)
            column.foreignKey = ForeignKeyConstraint(
                target = targetColumn,
                from = column,
                onUpdate = null,
                onDelete = null,
                name = foreignKeyContext.key.ifEmpty { null },
            )
        }
        columnContext.constraints.forEach {
            when(it) {
                Constraint.NOT_NULL -> column.nullb
                else -> throw Exception("??")
            }
        }
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