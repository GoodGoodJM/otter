package com.goodgoodman.otter.core.querygenerator

import com.goodgoodman.otter.core.querygenerator.exception.UnsupportedAlterColumnTypeException
import com.goodgoodman.otter.core.schema.*
import java.util.*

abstract class QueryGenerator {
    fun resolveColumns(columnSchemas: List<ColumnSchema>): String =
        columnSchemas.joinToString(",\n    ", transform = this::resolveColumn).trimIndent()

    private fun resolveColumn(columnSchema: ColumnSchema): String = with(columnSchema) {
        """
            |$name $type ${resolveConstraints(constraints)}
        """.trimMargin()
    }

    fun resolveConstraints(constraints: EnumSet<Constraint>): String {
        return constraints.joinToString(" ", transform = this::resolveConstraint)
    }

    private fun resolveConstraint(constraint: Constraint): String = when (constraint) {
        Constraint.UNIQUE -> "UNIQUE"
        Constraint.PRIMARY -> "PRIMARY KEY"
        Constraint.NOT_NULL -> "NOT NULL"
        Constraint.AUTO_INCREMENT -> "AUTO_INCREMENT"
        else -> ""
    }

    fun resolveReferences(referenceSchema: List<ReferenceSchema>): String {
        return referenceSchema.joinToString("\n,    ", transform = this::resolveReference)
    }

    private fun resolveReference(referenceSchema: ReferenceSchema): String = with(referenceSchema) {
        """
            |CONSTRAINT $key FOREIGN KEY ($fromColumn)
            |    REFERENCES $toTable($toColumn)
        """.trimMargin()
    }

    fun resolveTable(tableSchema: TableSchema): String = with(tableSchema) {
        var body = resolveColumns(columnSchemas)
        val references = columnSchemas.flatMap { it.referenceSchemas }
        if (references.isNotEmpty()) {
            body = """
                |$body,
                |    ${resolveReferences(references)}
            """.trimMargin()
        }
        """
            |CREATE TABLE $name (
            |    $body
            |)
        """.trimMargin()
    }

    fun dropTable(tableName: String): String = "DROP TABLE $tableName"

    fun resolveAlterColumn(alterColumnSchema: AlterColumnSchema): String = when (alterColumnSchema.alterType) {
        AlterColumnSchema.Type.ADD -> {
            val constraints = alterColumnSchema.constraints
            val constraintQuery = if (constraints.isNotEmpty()) " ${resolveConstraints(constraints)}" else ""
            """
            |ALTER TABLE ${alterColumnSchema.table}
            |ADD ${alterColumnSchema.name} ${alterColumnSchema.type}$constraintQuery
            """.trimMargin()
        }
        AlterColumnSchema.Type.DROP -> """
            |ALTER TABLE ${alterColumnSchema.table}
            |DROP COLUMN ${alterColumnSchema.name}
        """.trimMargin()
        else -> throw UnsupportedAlterColumnTypeException(alterColumnSchema)
    }
}