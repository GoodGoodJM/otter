package io.github.goodgoodjm.otter

import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.and
import org.junit.Test
import kotlin.test.assertEquals

class MigrationTests {
    @Test
    fun `Up 실행 시 Schema 들을 처리하여 Query 로 만든뒤 저장해야함`() {
        val migration = object : Migration() {
            override fun up() {
                createTable("person") {
                    column("id") {
                        type = "INT UNSIGNED"
                    } constraints (Constraint.PRIMARY and Constraint.AUTO_INCREMENT)
                }
                createTable("post") {
                    column("id") {
                        type = "INT UNSIGNED"
                    } constraints (Constraint.PRIMARY and Constraint.AUTO_INCREMENT)
                    column("person_id") {
                        type = "INT UNSIGNED"
                    } foreignKey { reference = "person(id)" }
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