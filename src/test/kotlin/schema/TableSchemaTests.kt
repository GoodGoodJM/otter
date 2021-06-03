package schema

import com.goodgoodman.otter.schema.ColumnSchema
import com.goodgoodman.otter.schema.Constraint
import com.goodgoodman.otter.schema.TableSchema
import org.junit.Test
import kotlin.test.assertEquals

class TableSchemaTests {
    @Test
    fun `ColumnScheme 생성`() {
        val columnSchema = ColumnSchema().apply {
            name = "column-name"
            type = "column-type"
            setConstraint(Constraint.PRIMARY, Constraint.NOT_NULL)
        }
        val tableSchema = TableSchema().apply {
            column {
                name = columnSchema.name
                type = columnSchema.type
                setConstraint(*columnSchema.constraints.toTypedArray())
            }
        }

        assertEquals(columnSchema.toString(), tableSchema.columnSchemas.first().toString())
    }
}