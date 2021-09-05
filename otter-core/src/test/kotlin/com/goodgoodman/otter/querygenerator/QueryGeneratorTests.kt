package com.goodgoodman.otter.querygenerator

import com.goodgoodman.otter.core.dsl.AlterColumnSchema
import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.altertable.AlterTableContext
import com.goodgoodman.otter.core.dsl.altertable.TableSchema as AlterTableSchema
import com.goodgoodman.otter.core.dsl.and
import com.goodgoodman.otter.core.dsl.createtable.CreateTableContext
import com.goodgoodman.otter.core.dsl.createtable.TableSchema as CreateTableSchema
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

        val tableContext = CreateTableContext(CreateTableSchema("table_name")).apply {
            column("id") {
                type = "INT UNSIGNED"
            } constraints (Constraint.PRIMARY and Constraint.AUTO_INCREMENT)

            column("temp_id") {
                type = "INT UNSIGNED"
            } constraints (Constraint.UNIQUE and Constraint.NOT_NULL)
        }

        val expectedQuery = """
                |CREATE TABLE table_name (
                |    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                |    temp_id INT UNSIGNED NOT NULL UNIQUE
                |)
            """.trimMargin()
        val actualQuery = queryGenerator.generateCreateTable(tableContext)
        assertEquals(expectedQuery, actualQuery)
    }

    @Test
    fun `Resolve a table which has a reference`() {
        val queryGenerator = object : QueryGenerator() {}

        val tableContext = CreateTableContext(CreateTableSchema("table_name")).apply {
            column("id") {
                type = "INT UNSIGNED"
            } constraints (Constraint.PRIMARY and Constraint.AUTO_INCREMENT)

            column("temp_id") {
                type = "INT UNSIGNED"
            } foreignKey {
                key = "fk_temp"
                reference = "temp(id)"
            }
        }

        val expectedQuery = """
                |CREATE TABLE table_name (
                |    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                |    temp_id INT UNSIGNED,
                |    CONSTRAINT fk_temp FOREIGN KEY (temp_id)
                |    REFERENCES temp(id)
                |)
            """.trimMargin()
        val actualQuery = queryGenerator.generateCreateTable(tableContext)
        assertEquals(expectedQuery, actualQuery)
    }

    @Test
    fun `Resolve a table which has references`() {
        val queryGenerator = object : QueryGenerator() {}

        val tableContext = CreateTableContext(CreateTableSchema("table_name")).apply {
            column("id") {
                type = "INT UNSIGNED"
            } constraints (Constraint.PRIMARY and Constraint.AUTO_INCREMENT)

            column("temp_id") {
                type = "INT UNSIGNED"
            } foreignKey {
                key = "fk_temp"
                reference = "temp(id)"
            }

            column("temptemp_id") {
                type = "INT UNSIGNED"
            } foreignKey { reference = "temptemp(id)" }
        }

        val expectedQuery = """
                |CREATE TABLE table_name (
                |    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                |    temp_id INT UNSIGNED,
                |    temptemp_id INT UNSIGNED,
                |    CONSTRAINT fk_temp FOREIGN KEY (temp_id)
                |    REFERENCES temp(id),
                |    CONSTRAINT fk_table_name_temptemp_temptemp_id FOREIGN KEY (temptemp_id)
                |    REFERENCES temptemp(id)
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
    fun `Add column1`() {
        val queryGenerator = object : QueryGenerator() {}
        val addColumnContext = AlterTableContext(AlterTableSchema("person")).addColumn("name") {
            type = "varchar(255)"
        } constraints Constraint.NOT_NULL

        val expectedQuery = """
            |ALTER TABLE person
            |ADD name varchar(255) NOT NULL
        """.trimMargin()
        val actualQuery = queryGenerator.resolveAlterColumn(addColumnContext)
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