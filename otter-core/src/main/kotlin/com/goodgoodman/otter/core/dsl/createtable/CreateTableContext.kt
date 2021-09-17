package com.goodgoodman.otter.core.dsl.createtable

import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.SchemaContext
import com.goodgoodman.otter.core.dsl.SchemaMaker
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import java.util.*

class CreateTableContext(tableSchema: TableSchema) : SchemaContext {
    companion object {
        val REGEX = """([\w]+)\(([\w]+)\)""".toRegex()
    }

    val name = tableSchema.name

    val columnContexts: List<ColumnContext> get() = _columnContexts
    private val _columnContexts = mutableListOf<ColumnContext>()


    override fun resolve(): List<String> {
        val table = Table(name)
        with(table) {
            columnContexts.forEach { columnContext ->
                val columnType = when (val type: Any = columnContext.type) {
                    is String -> object : ColumnType() {
                        override var nullable: Boolean = true
                        override fun sqlType(): String = type
                    }
                    else -> throw Exception("ColumnType($type) is not supported")
                }
                var column = table.registerColumn<Comparable<Any>>(columnContext.name, columnType)
                columnContext.constraints.forEach { constraint ->
                    column = when (constraint) {
                        Constraint.PRIMARY -> column.apply { indexInPK = columns.count { it.indexInPK != null } + 1 }
                        Constraint.NOT_NULL -> column.apply { columnType.nullable = false }
                        Constraint.AUTO_INCREMENT -> column.autoIncrement()
                        Constraint.UNIQUE -> column.uniqueIndex()
                        else -> throw Exception("Constraint($constraint) is not supported")
                    }
                }

                if (columnContext.foreignKeyContext != null) {
                    val foreignKeyContext = columnContext.foreignKeyContext!!
                    val (targetTableName, targetColumnName) = REGEX.find(foreignKeyContext.reference)!!.destructured
                    val targetColumn = Table(targetTableName)
                        .registerColumn<Comparable<Any>>(targetColumnName, column.columnType)
                    column.references(targetColumn, fkName = foreignKeyContext.key.ifEmpty { null })
                }
            }
        }
        return table.ddl
    }

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