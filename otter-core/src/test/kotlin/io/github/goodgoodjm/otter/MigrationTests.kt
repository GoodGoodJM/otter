package io.github.goodgoodjm.otter

import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint.*
import io.github.goodgoodjm.otter.core.dsl.createtable.and
import io.github.goodgoodjm.otter.core.dsl.createtable.constraints
import io.github.goodgoodjm.otter.core.dsl.createtable.foreignKey
import io.github.goodgoodjm.otter.core.dsl.type.Type.bool
import io.github.goodgoodjm.otter.core.dsl.type.Type.int
import io.github.goodgoodjm.otter.core.dsl.type.Type.long
import io.github.goodgoodjm.otter.core.dsl.type.Type.varchar
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MigrationTests {
    @Test
    fun `Up 실행 시 Schema 들을 처리하여 Query 로 만든뒤 저장해야함`() {
        val migration = object : Migration() {
            override fun up() {
                createTable("person") {
                    "id" - int() constraints PRIMARY and AUTO_INCREMENT
                    "name" - varchar(30)
                    "message" - varchar()
                    "address_id" - int() foreignKey "address(id)"
                    "lat" - long() constraints UNIQUE
                    "nullable" - bool() constraints NULLABLE
                }

                createTable("post") {
                    "id" - int() constraints PRIMARY and AUTO_INCREMENT
                    "person_id" - int() foreignKey "person(id)"
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

