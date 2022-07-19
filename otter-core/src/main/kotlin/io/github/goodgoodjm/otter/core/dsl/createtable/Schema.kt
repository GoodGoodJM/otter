package io.github.goodgoodjm.otter.core.dsl.createtable

import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.SchemaMaker
import io.github.goodgoodjm.otter.core.dsl.type.CustomColumnType
import org.jetbrains.exposed.sql.ColumnType

class TableSchema(val name: String) {
    val columnSchemaMap: Map<String, ColumnSchema> get() = _columnSchemaMap
    private val _columnSchemaMap = mutableMapOf<String, ColumnSchema>()
    operator fun set(key: String, value: ColumnSchema) {
        require(key.isNotEmpty())
        _columnSchemaMap[key] = value
    }

}

data class ColumnSchema internal constructor(
    val columnType: ColumnType,
    val constraints: List<Constraint> = listOf(),
    val foreignKey: String? = null,
) {
    constructor (type: String, constraints: List<Constraint> = listOf()) : this(CustomColumnType(type), constraints)
}


@SchemaMaker
infix fun ColumnSchema.constraints(constraint: Constraint): ColumnSchema =
    copy(constraints = constraints + constraint)

@SchemaMaker
infix fun ColumnSchema.and(constraint: Constraint): ColumnSchema = constraints(constraint)

@SchemaMaker
infix fun ColumnSchema.foreignKey(value: String): ColumnSchema {
    require(value.isNotEmpty())
    return copy(foreignKey = value)
}
