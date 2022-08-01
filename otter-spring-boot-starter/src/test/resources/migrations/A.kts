import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint.*
import io.github.goodgoodjm.otter.core.dsl.createtable.and
import io.github.goodgoodjm.otter.core.dsl.createtable.constraints
import io.github.goodgoodjm.otter.core.dsl.type.Type.int
import io.github.goodgoodjm.otter.core.dsl.type.Type.varchar

object : Migration() {
    override val comment = "Create person"

    override fun up() {
        createTable("person") {
            it["id"] = int() constraints PRIMARY and AUTO_INCREMENT
            it["name"] = varchar() constraints UNIQUE
            it["age"] = int()
        }
    }

    override fun down() {
        dropTable("person")
    }
}