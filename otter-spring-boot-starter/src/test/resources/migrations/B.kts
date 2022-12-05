import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint.AUTO_INCREMENT
import io.github.goodgoodjm.otter.core.dsl.Constraint.PRIMARY
import io.github.goodgoodjm.otter.core.dsl.createtable.and
import io.github.goodgoodjm.otter.core.dsl.createtable.constraints
import io.github.goodgoodjm.otter.core.dsl.createtable.foreignKey
import io.github.goodgoodjm.otter.core.dsl.type.Type.INT
import io.github.goodgoodjm.otter.core.dsl.type.Type.int

object : Migration() {
    override val comment = "Create user"

    override fun up() {
        createTable("user") {
            "id" - INT constraints PRIMARY and AUTO_INCREMENT
            "person_id" - INT foreignKey "person(id)"
        }
    }

    override fun down() {
        dropTable("user")
    }
}

