package com.goodgoodman.otter.core.querygenerator

import com.goodgoodman.otter.core.dsl.AlterColumnSchema
import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.createtable.context.CreateTableContext
import com.goodgoodman.otter.core.querygenerator.exception.UnsupportedAlterColumnTypeException
import java.util.*

abstract class QueryGenerator {

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

    fun generateCreateTable(tableContext: CreateTableContext): String {
        var body = tableContext.columnContexts.joinToString(",\n    ") {
            "${it.name} ${it.type} ${resolveConstraints(it.constraints)}"
        }
        val foreignKeys = tableContext.columnContexts
            .filter { it.foreignKeyContext != null }
            .map { it.foreignKeyContext }
        if (foreignKeys.isNotEmpty()) {
            val foreignKeyBody = foreignKeys.joinToString(",\n    ") {
                """""".trimMargin()
            }
            body = """
                |$body,
                |    $foreignKeyBody
            """.trimMargin()
        }

        return """
            |CREATE TABLE ${tableContext.name} (
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