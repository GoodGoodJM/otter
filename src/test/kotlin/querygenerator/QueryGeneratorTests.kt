package querygenerator

import com.goodgoodman.otter.querygenerator.QueryGenerator
import com.goodgoodman.otter.schema.ColumnSchema
import com.goodgoodman.otter.schema.Constraint
import com.goodgoodman.otter.schema.ReferenceSchema
import com.goodgoodman.otter.schema.TableSchema
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class QueryGeneratorTests {

    @Test
    fun `Resolve a column schema`() {
        val queryGenerator = object : QueryGenerator() {}
        val columnSchema = ColumnSchema().apply {
            name = "id"
            type = "INT"
            setConstraint(Constraint.NOT_NULL, Constraint.UNIQUE, Constraint.PRIMARY, Constraint.AUTO_INCREMENT)
        }

        val expectedQuery = """
            |${columnSchema.name} ${columnSchema.type} ${queryGenerator.resolveConstraints(columnSchema.constraints)}
        """.trimMargin()
        assertEquals(expectedQuery, queryGenerator.resolveColumns(listOf(columnSchema)))
    }

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
    fun `Resolve a reference schema`() {
        val queryGenerator = object : QueryGenerator() {}

        val referenceSchema = ReferenceSchema().apply {
            fromTable = "fromTable"
            fromColumn = "fromColumn"
            toTable = "toTable"
            toColumn = "toColumn"
            key = "fk_${fromTable}_${toTable}"
        }
        val expectedQuery = """
            |CONSTRAINT ${referenceSchema.key} FOREIGN KEY (${referenceSchema.fromColumn})
            |    REFERENCES ${referenceSchema.toTable}(${referenceSchema.toColumn})
        """.trimMargin()
        assertEquals(expectedQuery, queryGenerator.resolveReferences(listOf(referenceSchema)))
    }

    @Test
    fun `Resolve a table which has a single column schema`() {
        val queryGenerator = object : QueryGenerator() {}

        val tableSchema = TableSchema().apply {
            name = "table"
            column {
                name = "id"
                type = "INT UNSIGNED"
                setConstraint(Constraint.PRIMARY, Constraint.AUTO_INCREMENT)
            }
        }

        val expectedQuery = """
                |CREATE TABLE ${tableSchema.name} (
                |    ${queryGenerator.resolveColumns(tableSchema.columnSchemas)}
                |)
            """.trimMargin()
        assertEquals(expectedQuery, queryGenerator.resolveTable(tableSchema))
    }

    @Test
    fun `Resolve a table which has multiple column schemas`() {
        val queryGenerator = object : QueryGenerator() {}

        val tableSchema = TableSchema().apply {
            name = "table"
            column {
                name = "id"
                type = "INT UNSIGNED"
                setConstraint(Constraint.PRIMARY, Constraint.AUTO_INCREMENT)
            }
            column {
                name = "temp_id"
                type = "INT UNSIGNED"
                setConstraint(Constraint.UNIQUE, Constraint.NOT_NULL)
            }
        }

        val expectedQuery = """
                |CREATE TABLE ${tableSchema.name} (
                |    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                |    temp_id INT UNSIGNED NOT NULL UNIQUE
                |)
            """.trimMargin()
        assertEquals(expectedQuery, queryGenerator.resolveTable(tableSchema))
    }

    @Test
    fun `Resolve a table with foreign key`() {
        val queryGenerator = object : QueryGenerator() {}
        val referenceSchema = ReferenceSchema().apply {
            fromTable = "test_table"
            fromColumn = "temp_id"
            toTable = "temp"
            toColumn = "id"
            key = "fk_test_table_temp_id"
        }
        val tableSchema = TableSchema().apply {
            name = referenceSchema.fromTable
            column {
                name = "id"
                type = "INT UNSIGNED"
                setConstraint(Constraint.PRIMARY, Constraint.AUTO_INCREMENT)
            }
            column {
                name = referenceSchema.fromColumn
                type = "INT UNSIGNED"
                setConstraint(Constraint.NOT_NULL)
                reference {
                    toTable = referenceSchema.toTable
                    toColumn = referenceSchema.toColumn
                    key = referenceSchema.key
                }
            }
        }
        val expectedQuery = """
            |CREATE TABLE ${tableSchema.name} (
            |    ${queryGenerator.resolveColumns(tableSchema.columnSchemas)},
            |    CONSTRAINT ${referenceSchema.key} FOREIGN KEY (${referenceSchema.fromColumn})
            |    REFERENCES ${referenceSchema.toTable}(${referenceSchema.toColumn})
            |)
        """.trimMargin()
        val actualQuery = queryGenerator.resolveTable(tableSchema)
        println(actualQuery)
        assertEquals(expectedQuery, actualQuery)
    }
}