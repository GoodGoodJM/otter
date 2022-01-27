package io.github.goodgoodjm.otter

import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.createtable.CreateTableContext
import io.github.goodgoodjm.otter.core.dsl.createtable.TableSchema
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import kotlin.test.assertNotEquals

class CreateTableContextTests {
    @Test
    fun `Resolve CreateTableContext`() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        transaction {
            val tableContext = CreateTableContext(TableSchema("person")).apply {
                column("id") {
                    type = "int"
                } constraints (Constraint.PRIMARY)
                column("name") {
                    type = "varchar(255)"
                } constraints (Constraint.NOT_NULL)
                column("address") {
                    type = "varchar(255)"
                }
                column("master_id") {
                    type = "int"
                } foreignKey { reference = "person(id)" }
            }
            val table = tableContext.resolve()
            assertNotEquals(0, table.size)
        }
    }
}