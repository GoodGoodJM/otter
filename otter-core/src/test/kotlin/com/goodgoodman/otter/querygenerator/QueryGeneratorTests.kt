package com.goodgoodman.otter.querygenerator

import com.goodgoodman.otter.core.dsl.AlterColumnSchema
import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.and
import com.goodgoodman.otter.core.dsl.createtable.context.CreateTableContext
import com.goodgoodman.otter.core.dsl.createtable.context.TableSchema
import com.goodgoodman.otter.core.querygenerator.QueryGenerator
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class QueryGeneratorTests {
    @Test
    fun `Resolve constraints`() {
        val queryGenerator = object : QueryGenerator() {}
        val constraints = EnumSet.of(Constraint.NOT_NULL, Constraint.UNIQUE)
        val expectedQuery = """
            |NOT NULL UNIQUE
        """.trimMargin()
        assertEquals(expectedQuery, queryGenerator.resolveConstraints(constraints))
    }

    @Test
    fun `Resolve a table which has multiple column schemas`() {
        val queryGenerator = object : QueryGenerator() {}

        val tableContext = CreateTableContext(TableSchema("table")).apply {
            column("id") {
                type = "INT UNSIGNED"
            } constraints (Constraint.PRIMARY and Constraint.AUTO_INCREMENT)

            column("temp_id") {
                type = "INT UNSIGNED"
            } constraints (Constraint.UNIQUE and Constraint.NOT_NULL)
        }

        val expectedQuery = """
                |CREATE TABLE table (
                |    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                |    temp_id INT UNSIGNED NOT NULL UNIQUE
                |)
            """.trimMargin()
        val actualQuery = queryGenerator.generateCreateTable(tableContext)
        assertEquals(expectedQuery, actualQuery)
    }


    @Test
    fun `Drop table`() {
        val queryGenerator = object : QueryGenerator() {}
        val targetTable = "test"
        val expectedQuery = "DROP TABLE $targetTable"
        assertEquals(expectedQuery, queryGenerator.dropTable(targetTable))
    }

    @Test
    fun `Add column`() {
        val queryGenerator = object : QueryGenerator() {}
        val addColumnSchema = AlterColumnSchema().apply {
            alterType = AlterColumnSchema.Type.ADD
            table = "test"
            name = "hello_id"
            type = "INT UNSIGNED"
            setConstraint(Constraint.NOT_NULL, Constraint.UNIQUE)
        }

        val expectedQuery = """
            |ALTER TABLE ${addColumnSchema.table}
            |ADD ${addColumnSchema.name} ${addColumnSchema.type} ${queryGenerator.resolveConstraints(addColumnSchema.constraints)}
        """.trimMargin()
        val actualQuery = queryGenerator.resolveAlterColumn(addColumnSchema)
        println(actualQuery)
        assertEquals(expectedQuery, actualQuery)
    }

    @Test
    fun `Drop column`() {
        val queryGenerator = object : QueryGenerator() {}
        val dropColumnSchema = AlterColumnSchema().apply {
            alterType = AlterColumnSchema.Type.DROP
            table = "test"
            name = "hello_id"
        }
        val expectedQuery = """
            |ALTER TABLE ${dropColumnSchema.table}
            |DROP COLUMN ${dropColumnSchema.name}
        """.trimMargin()
        val actualQuery = queryGenerator.resolveAlterColumn(dropColumnSchema)
        println(actualQuery)
        assertEquals(expectedQuery, actualQuery)
    }
}