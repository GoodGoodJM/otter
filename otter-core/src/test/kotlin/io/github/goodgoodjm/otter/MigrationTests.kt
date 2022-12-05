package io.github.goodgoodjm.otter

import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint.*
import io.github.goodgoodjm.otter.core.dsl.createtable.and
import io.github.goodgoodjm.otter.core.dsl.createtable.constraints
import io.github.goodgoodjm.otter.core.dsl.createtable.foreignKey
import io.github.goodgoodjm.otter.core.dsl.type.Type.BOOL
import io.github.goodgoodjm.otter.core.dsl.type.Type.INT
import io.github.goodgoodjm.otter.core.dsl.type.Type.LONG
import io.github.goodgoodjm.otter.core.dsl.type.Type.VARCHAR
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MigrationTests {
    @Test
    fun `Up 실행 시 Schema 들을 처리하여 Query 로 만든뒤 저장해야함`() {
        val migration = object : Migration() {
            override fun up() {
                createTable("person") {
                    "id" - INT constraints PRIMARY and AUTO_INCREMENT
                    "name" - VARCHAR(30)
                    "message" - VARCHAR
                    "address_id" - INT foreignKey "address(id)"
                    "lat" - LONG constraints UNIQUE
                    "nullable" - BOOL constraints NULLABLE
                }

                createTable("post") {
                    "id" - INT constraints PRIMARY and AUTO_INCREMENT
                    "person_id" - INT foreignKey "person(id)"
                }
            }

            override fun down() {
            }
        }

        migration.up()
        assertEquals(2, migration.contexts.size)
    }

    @Test
    fun `Down 실행 시 Schema 들을 처리하여 Query 로 만든뒤 저장해야함`() {
        val migration = object : Migration() {
            override fun up() {
            }

            override fun down() {
                dropTable("post")
                dropTable("person")
            }
        }

        migration.down()
        println(migration.contexts)
        assertEquals(2, migration.contexts.size)
    }
}

