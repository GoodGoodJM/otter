package schema

import com.goodgoodman.otter.schema.ColumnSchema
import com.goodgoodman.otter.schema.Constraint
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColumnSchemaTests {
    @Test
    fun `Constraint 지정`() {
        val columnSchema = ColumnSchema()
        columnSchema.setConstraint(Constraint.NOT_NULL, Constraint.PRIMARY)
        assertTrue(columnSchema.constraints.containsAll(listOf(Constraint.NOT_NULL, Constraint.PRIMARY)))
    }

    @Test
    fun `Constraint NONE 입력시 무시처리`() {
        val columnSchema = ColumnSchema()
        columnSchema.setConstraint(Constraint.NONE)
        assertEquals(0, columnSchema.constraints.size)
    }

    @Test fun `Constraint 지정 중복호출시 마지막만 적용`() {
        val columnSchema = ColumnSchema()
        columnSchema.setConstraint(Constraint.NOT_NULL, Constraint.PRIMARY)
        columnSchema.setConstraint(Constraint.PRIMARY)
        assertEquals(listOf(Constraint.PRIMARY), columnSchema.constraints.toList())
    }
}