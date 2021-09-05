package com.goodgoodman.otter.core.querygenerator

import com.goodgoodman.otter.core.dsl.AlterColumnSchema
import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.altertable.AlterTableContext
import com.goodgoodman.otter.core.dsl.createtable.CreateTableContext
import com.goodgoodman.otter.core.querygenerator.exception.UnsupportedAlterColumnTypeException
import java.util.*

abstract class QueryGenerator {
    companion object {
        val REGEX = """([\w]+)\([\w]+\)""".toRegex()
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

    fun generateCreateTable(tableContext: CreateTableContext): String {
        var body = tableContext.columnContexts.joinToString(",\n    ") {
            var columnQuery = "${it.name} ${it.type}"
            if (it.constraints.isNotEmpty()) {
                columnQuery = "$columnQuery ${resolveConstraints(it.constraints)}"
            }
            return@joinToString columnQuery
        }

        val foreignKeyBody = tableContext.columnContexts
            .filter { it.foreignKeyContext != null }
            .joinToString(",\n    ") {
                val foreignKeyContext = it.foreignKeyContext!!
                val key = foreignKeyContext.key.ifEmpty {
                    val (targetTable) = REGEX.find(foreignKeyContext.reference)!!.destructured
                    "fk_${tableContext.name}_${targetTable}_${it.name}"
                }

                """
                   |CONSTRAINT $key FOREIGN KEY (${it.name})
                   |    REFERENCES ${foreignKeyContext.reference}
                """.trimMargin()
            }
        if (foreignKeyBody.isNotEmpty()) {
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

    fun resolveAlterColumn(alterTableContext: AlterTableContext): String {
        val columnContext = alterTableContext.columnContext
        return """
            |ALTER TABLE ${alterTableContext.name}
            |ADD ${columnContext.name}
        """.trimMargin()
    }
}