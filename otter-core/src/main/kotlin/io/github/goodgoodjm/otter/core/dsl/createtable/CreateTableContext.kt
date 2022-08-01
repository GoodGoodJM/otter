package io.github.goodgoodjm.otter.core.dsl.createtable

import io.github.goodgoodjm.otter.core.Logger
import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.SchemaContext
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

class CreateTableContext constructor(val tableSchema: TableSchema) : SchemaContext {
    override fun resolve(): List<String> {
        return DynamicPrimaryKeyTable.with(tableSchema).resolve()
    }
}

class DynamicPrimaryKeyTable(name: String) : Table(name) {
    companion object : Logger {
        val REGEX = """([\w]+)\(([\w]+)\)""".toRegex()

        fun with(tableSchema: TableSchema): DynamicPrimaryKeyTable = DynamicPrimaryKeyTable(tableSchema.name).apply {
            tableSchema.columnSchemaMap.map { (key, value) -> addColumn(key, value) }
        }
    }

    private var primaryKeys: Array<Column<*>> = arrayOf()

    override val primaryKey: PrimaryKey?
        get() = if (primaryKeys.isEmpty()) {
            null
        } else {
            PrimaryKey(primaryKeys)
        }


    private fun addColumn(name: String, columnSchema: ColumnSchema) {
        val column = registerColumn<Comparable<Any>>(name, columnSchema.columnType)
        columnSchema.constraints.forEach { constraint ->
            when (constraint) {
                Constraint.PRIMARY -> primaryKeys += column
                Constraint.NULLABLE -> column.columnType.nullable = true
                Constraint.AUTO_INCREMENT -> column.autoIncrement()
                Constraint.UNIQUE -> column.uniqueIndex()
                else -> throw Exception("Constraint($constraint) is not supported")
            }
        }

        columnSchema.foreignKey?.let { expression ->
            val result = REGEX.find(expression) ?: throw Exception("Wrong foreignKey expression.")
            val (tableName, columnName) = result.destructured
            val target = Table(tableName).registerColumn<Comparable<Any>>(columnName, column.columnType)
            column.references(target)
        }
    }

    fun resolve(): List<String> = ddl + indices.flatMap { it.createStatement() }
}