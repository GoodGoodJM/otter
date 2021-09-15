package com.goodgoodman.otter

import com.goodgoodman.otter.core.Migration
import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.querygenerator.QueryGeneratorManager
import org.junit.Test
import kotlin.test.assertEquals

class MigrationTests {
    @Test
    fun `Up 실행 시 Schema 들을 처리하여 Query 로 만든뒤 저장해야함`() {
        val migration = object : Migration() {
            override fun up() {
                createTable {
                    name = "person"
                    column {
                        name = "id"
                        type = "INT UNSIGNED"
                        setConstraint(Constraint.PRIMARY, Constraint.AUTO_INCREMENT)
                    }
                }
                createTable {
                    name = "post"
                    column {
                        name = "id"
                        type = "INT UNSIGNED"
                        setConstraint(Constraint.PRIMARY, Constraint.AUTO_INCREMENT)
                    }
                    column {
                        name = "person_id"
                        type = "INT UNSIGNED"
                        reference {
                            toTable = "person"
                            toColumn = "id"
                            key = "fk_post_person_id"
                        }
                    }
                }
            }

            override fun down() {
            }
        }

        migration.queryGenerator = QueryGeneratorManager.getQueryGeneratorByDriverClassName("")
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

        migration.queryGenerator = QueryGeneratorManager.getQueryGeneratorByDriverClassName("")
        migration.down()
        println(migration.contexts)
        assertEquals(2, migration.contexts.size)
    }
}