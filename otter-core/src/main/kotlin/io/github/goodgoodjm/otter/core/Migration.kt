package io.github.goodgoodjm.otter.core

import io.github.goodgoodjm.otter.core.dsl.AlterColumnSchema
import io.github.goodgoodjm.otter.core.dsl.SchemaContext
import io.github.goodgoodjm.otter.core.dsl.SchemaMaker
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
    fun createTable(name: String, block: CreateTableContext.() -> Unit) {
        val tableSchema = TableSchema(name)
        val tableContext = CreateTableContext(tableSchema).apply(block)
        _contexts.add(tableContext)
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

    fun addColumn(block: AlterColumnSchema.() -> Unit) = alterColumn(AlterColumnSchema.Type.ADD, block)

    fun dropColumn(block: AlterColumnSchema.() -> Unit) = alterColumn(AlterColumnSchema.Type.DROP, block)

    private fun alterColumn(type: AlterColumnSchema.Type, block: AlterColumnSchema.() -> Unit) {
        val alterColumnSchema = AlterColumnSchema()
        alterColumnSchema.alterType = type
        alterColumnSchema.block()
        // _contexts.add(alterColumnSchema)
    }
}