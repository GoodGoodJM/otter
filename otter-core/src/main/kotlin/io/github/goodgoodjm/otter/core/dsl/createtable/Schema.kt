package io.github.goodgoodjm.otter.core.dsl.createtable

import io.github.goodgoodjm.otter.core.dsl.AlterColumnSchema
import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.SchemaMaker
import io.github.goodgoodjm.otter.core.dsl.type.CustomColumnType
import org.jetbrains.exposed.sql.ColumnType

class TableSchema(val name: String) {
    val columnSchemaMap: Map<String, ColumnSchema> get() = _columnSchemaMap
    private val _columnSchemaMap = mutableMapOf<String, ColumnSchema>()

    @Deprecated("Use minus operator")
    operator fun set(key: String, value: ColumnSchema) {
        require(key.isNotEmpty())
        pair(key, value)
    }

    private fun pair(key: String, value: ColumnSchema) {
        _columnSchemaMap[key] = value
    }

    operator fun String.minus(value: ColumnSchema): ColumnSchema {
        pair(this, value)
        return value
    }
}

data class ColumnSchema internal constructor(
    val columnType: ColumnType,
    var constraints: List<Constraint> = listOf(),
    var foreignKey: String? = null,
    var defaultValue: Comparable<*>? = null,
) {
    constructor (type: String, constraints: List<Constraint> = listOf()) : this(CustomColumnType(type), constraints)
}

@SchemaMaker
infix fun ColumnSchema.constraints(constraint: Constraint): ColumnSchema {
    constraints += constraint
    return this
}

@SchemaMaker
infix fun ColumnSchema.and(constraint: Constraint): ColumnSchema = constraints(constraint)

@SchemaMaker
infix fun ColumnSchema.foreignKey(value: String): ColumnSchema {
    require(value.isNotEmpty())
    foreignKey = value
    return this
}

@SchemaMaker
infix fun ColumnSchema.default(typeDefault: Comparable<*>): ColumnSchema {
    defaultValue = typeDefault
    return this
}