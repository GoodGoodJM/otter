import com.goodgoodman.otter.Migration
import com.goodgoodman.otter.OtterConfig
import com.goodgoodman.otter.schema.Constraint
import org.junit.Test
import kotlin.test.assertEquals

class MigrationTests {
    val config = OtterConfig("", "", "", "")

    @Test
    fun `Up 실행 시 Schema 들을 처리하여 Query 로 만든뒤 저장해야함`() {
        val migration = object : Migration(config) {
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

        migration.up()
        assertEquals(2, migration.reservedQueries.size)
    }

    @Test
    fun `Down 실행 시 Schema 들을 처리하여 Query 로 만든뒤 저장해야함`() {
        val migration = object : Migration(config) {
            override fun up() {
            }

            override fun down() {
                dropTable("post")
                dropTable("person")
            }
        }

        migration.down()
        println(migration.reservedQueries)
        assertEquals(2, migration.reservedQueries.size)
    }
}