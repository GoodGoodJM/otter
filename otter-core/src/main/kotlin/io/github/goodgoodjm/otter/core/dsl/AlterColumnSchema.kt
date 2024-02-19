package io.github.goodgoodjm.otter.core.dsl

import io.github.goodgoodjm.otter.core.dsl.createtable.ColumnSchema
enum class AlterType {
    NONE, ADD, DROP, MODIFY, RENAME
}

class AlterColumnSchema internal constructor(
    tableName: String,
    name: String,
    alterType: AlterType,
    columnSchema: ColumnSchema?,
) {
    val columnSchema: ColumnSchema? = columnSchema
    var alterType: AlterType = alterType
    var table: String = tableName
    var name: String = name
    var rename: String = ""

    override fun toString(): String {
        require(columnSchema != null)
        return "AlterColumnSchema(alterType=$alterType, table='$table', name='$name', type='${columnSchema.columnType}', _constraints=${columnSchema.constraints})"
    }
}

@SchemaMaker
infix fun AlterColumnSchema.constraints(constraint: Constraint): AlterColumnSchema {
    require(columnSchema != null)
    this.columnSchema.constraints += constraint
    return this
}

@SchemaMaker
infix fun AlterColumnSchema.and(constraint: Constraint): AlterColumnSchema {
    constraints(constraint)
    return this
}

@SchemaMaker
infix fun AlterColumnSchema.foreignKey(value: String): AlterColumnSchema {
    require(value.isNotEmpty() && columnSchema != null)
    this.columnSchema.foreignKey = value
    return this
}

@SchemaMaker
infix fun AlterColumnSchema.default(typeDefault: Comparable<*>): AlterColumnSchema {
    require(columnSchema != null)
    this.columnSchema.defaultValue = typeDefault
    return this
}