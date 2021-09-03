package com.goodgoodman.otter.schema

import com.goodgoodman.otter.core.dsl.createtable.context.schema.ColumnSchema_
import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.createtable.context.schema.TableSchema_
import org.junit.Test
import kotlin.test.assertEquals

class TableSchemaTests {
    @Test
    fun `ColumnScheme 생성`() {
        val columnSchema = ColumnSchema_().apply {
            name = "column-name"
            type = "column-type"
            setConstraint(Constraint.PRIMARY, Constraint.NOT_NULL)
        }
        val tableSchema = TableSchema_().apply {
            column {
                name = columnSchema.name
                type = columnSchema.type
                setConstraint(*columnSchema.constraints.toTypedArray())
            }
        }

        assertEquals(columnSchema.toString(), tableSchema.columnSchemas.first().toString())
    }
}