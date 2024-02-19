package io.github.goodgoodjm.otter.core

import io.github.goodgoodjm.otter.core.dsl.AlterColumnSchema
import io.github.goodgoodjm.otter.core.dsl.SchemaContext
import io.github.goodgoodjm.otter.core.dsl.SchemaMaker
import io.github.goodgoodjm.otter.core.dsl.altertable.AlterTableSchema
import io.github.goodgoodjm.otter.core.dsl.createtable.CreateTableContext
import io.github.goodgoodjm.otter.core.dsl.createtable.TableSchema
import org.jetbrains.exposed.sql.Table

abstract class Migration {
    companion object : Logger

    val contexts: List<SchemaContext> get() = _contexts
    private val _contexts = mutableListOf<SchemaContext>()

    open val comment: String = ""

    abstract fun up()
    abstract fun down()


    @SchemaMaker
    fun createTable(name: String, block: TableSchema.() -> Unit) {
        val tableSchema = TableSchema(name).apply(block)
        val table = CreateTableContext(tableSchema)
        _contexts.add(table)
    }

    @SchemaMaker
    fun alter(name: String, block: AlterTableSchema.() -> Unit) {
        var tableSchema = AlterTableSchema(name).apply(block)
        //TODO: Not implement yet
    }

    @SchemaMaker
    @Deprecated("", ReplaceWith("createTable(name, block)"))
    fun createTable_(name: String, block: (TableSchema) -> Unit) {
        createTable(name, block)
    }

    fun dropTable(name: String) {
        _contexts.add(object : SchemaContext {
            override fun resolve(): List<String> = Table(name).dropStatement()
        })
    }

    fun rawQuery(sql: String) {
        _contexts.add(object : SchemaContext {
            override fun resolve(): List<String> = listOf(sql)
        })
    }
}