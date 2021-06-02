package com.goodgoodman.otter

import com.goodgoodman.otter.querygenerator.QueryGeneratorManager
import com.goodgoodman.otter.querygenerator.schema.Constraint
import com.goodgoodman.otter.querygenerator.schema.SchemaMaker
import com.goodgoodman.otter.querygenerator.schema.TableSchema

abstract class Migration(
    config: OtterConfig
) {
    companion object : Logger

    private val queryGenerator = QueryGeneratorManager.getQueryGeneratorByDriverClassName(config.driverClassName)

    val tableSchemas: List<TableSchema> get() = _tableSchemas
    private val _tableSchemas = mutableListOf<TableSchema>()

    abstract fun up()
    abstract fun down()

    @SchemaMaker
    fun createTable(block: TableSchema.() -> Unit) {
        val tableSchema = TableSchema()
        tableSchema.block()
        _tableSchemas.add(tableSchema)
    }

    fun dropTable(name: String) {

    }
}

fun temp() {
    val temp = object : Migration(OtterConfig("org.mariadb.jdbc.Driver", "", "", "")) {
        override fun up() {
            createTable {
                name = "person"
                comment = "users of application"
                column {
                    name = "id"
                    comment = "unique index"
                    type = "VARCHAR(255)"
                    setConstraint(Constraint.NOT_NULL, Constraint.PRIMARY, Constraint.UNIQUE)
                }
            }
        }

        override fun down() {
            dropTable("person")
        }
    }

    val tableSchema = TableSchema()
    tableSchema.apply {
        name = "table"
        comment = "table-comment"
        column {
            name = "id"
        }
    }
    temp.up()
    println(temp.tableSchemas)
}


fun main() {
    temp()
}