package io.github.goodgoodjm.otter

import io.github.goodgoodjm.otter.core.dsl.Constraint.*
import io.github.goodgoodjm.otter.core.dsl.createtable.*
import io.github.goodgoodjm.otter.core.dsl.type.Type.BOOL
import io.github.goodgoodjm.otter.core.dsl.type.Type.INT
import io.github.goodgoodjm.otter.core.dsl.type.Type.LONG
import io.github.goodgoodjm.otter.core.dsl.type.Type.VARCHAR
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreateTableContextTests {
    private lateinit var transaction: Transaction

    companion object {
        private lateinit var db: Database

        @BeforeAll
        @JvmStatic
        fun setup() {
            db = Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @BeforeEach
    fun init() {
        transaction = db.transactionManager.newTransaction()
    }

    @AfterEach
    fun tearDown() {
        transaction.commit()
    }

    @Test
    fun `V2 test`() {
        val tableName = "person"

        val table = CreateTableContext(TableSchema(tableName).apply {
            "id" - INT constraints PRIMARY and AUTO_INCREMENT
            "name" - VARCHAR
            "address_id" - INT foreignKey "address(id)"
            "lat" - LONG constraints UNIQUE
            "nullable" - BOOL constraints NULLABLE
        })

        table.tableSchema.also {
            assertEquals(tableName, it.name)
            assertEquals(5, it.columnSchemaMap.size)
        }

        table.tableSchema.columnSchemaMap.also {
            val idKey = "id"
            val idColumn = it[idKey]
            assertNotNull(idColumn)
            assertEquals(2, idColumn.constraints.size)
            assertContains(idColumn.constraints, PRIMARY)
            assertContains(idColumn.constraints, AUTO_INCREMENT)
        }

        val temp = table.resolve()
    }
}