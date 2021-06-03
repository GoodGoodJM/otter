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

        with(columnSchema) {
            val expectedQuery = """
                $name $type ${queryGenerator.resolveConstraints(constraints)}
            """.trimIndent()
            assertEquals(expectedQuery, queryGenerator.resolveColumn(this))
        }
    }

    @Test
    fun `Resolve constraints`() {
        val queryGenerator = object : QueryGenerator() {}
        val constraints = EnumSet.of(Constraint.NOT_NULL, Constraint.UNIQUE)
        val expectedQuery = """
            NOT NULL UNIQUE
        """.trimIndent()
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
            CONSTRAINT ${referenceSchema.key} FOREIGN KEY (${referenceSchema.fromColumn})
            REFERENCES ${referenceSchema.toTable}(${referenceSchema.toColumn})
        """.trimIndent()
        assertEquals(expectedQuery, queryGenerator.resolveReference(referenceSchema))
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

        with(tableSchema) {
            val expectedQuery = """
                CREATE TABLE $name (
                    ${queryGenerator.resolveColumn(columnSchemas.first())}
                )
            """.trimIndent()
            assertEquals(expectedQuery, queryGenerator.resolveTable(tableSchema))
        }
    }
}