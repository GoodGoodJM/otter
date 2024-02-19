package io.github.goodgoodjm.otter.core.dsl.altertable

import io.github.goodgoodjm.otter.core.dsl.AlterColumnSchema
import io.github.goodgoodjm.otter.core.dsl.AlterType
import io.github.goodgoodjm.otter.core.dsl.createtable.ColumnSchema


data class TableSchema(
    val name: String,
)

data class AlterTableSchema(
    val name: String
) {
    data class AlterSchemaInfo(
        val tableName: String,
        val columnName: String,
        val alterType: AlterType,
    )

    val columnSchemaMap: Map<AlterType, AlterColumnSchema> get() = _columnSchemaMap
    private val _columnSchemaMap = mutableMapOf<AlterType, AlterColumnSchema>()

    operator fun AlterSchemaInfo.minus(value: ColumnSchema): AlterColumnSchema {
        val alterColumnSchema = AlterColumnSchema(this.tableName, this.columnName, this.alterType, value)
        _columnSchemaMap[this.alterType] = (alterColumnSchema)
        return alterColumnSchema
    }

    operator fun AlterSchemaInfo.minus(value: String): AlterColumnSchema {
        val alterColumnSchema = AlterColumnSchema(this.tableName, this.columnName, this.alterType, null)
            .apply { rename = value }
        _columnSchemaMap[this.alterType] = (alterColumnSchema)
        return alterColumnSchema
    }

    fun modify(columnName: String): AlterSchemaInfo = AlterSchemaInfo(
        tableName = this.name,
        columnName = columnName,
        alterType = AlterType.MODIFY
    )

    fun add(columnName: String): AlterSchemaInfo = AlterSchemaInfo(
        tableName = this.name,
        columnName = columnName,
        alterType = AlterType.ADD
    )

    fun drop(columnName: String) {
        val alterSchemaInfo = AlterSchemaInfo(
            tableName = this.name,
            columnName = columnName,
            alterType = AlterType.DROP
        )
        _columnSchemaMap[AlterType.DROP] =
            AlterColumnSchema(alterSchemaInfo.tableName, alterSchemaInfo.columnName, alterSchemaInfo.alterType, null)
    }

    fun rename(columnName: String): AlterSchemaInfo = AlterSchemaInfo(
        tableName = this.name,
        columnName = columnName,
        alterType = AlterType.DROP
    )
}
