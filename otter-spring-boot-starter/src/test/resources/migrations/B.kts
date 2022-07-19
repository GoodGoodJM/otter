import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint.*
import io.github.goodgoodjm.otter.core.dsl.createtable.and
import io.github.goodgoodjm.otter.core.dsl.createtable.constraints
import io.github.goodgoodjm.otter.core.dsl.createtable.foreignKey
import io.github.goodgoodjm.otter.core.dsl.type.Type.int

object : Migration() {
    override val comment = "Create user"

    override fun up() {
        createTable("user") {
            it["id"] = int() constraints PRIMARY and AUTO_INCREMENT
            it["person_id"] = int() foreignKey "person(id)"
        }
    }

    override fun down() {
        dropTable("user")
    }
}

