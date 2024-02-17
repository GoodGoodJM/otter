import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint.*
import io.github.goodgoodjm.otter.core.dsl.createtable.and
import io.github.goodgoodjm.otter.core.dsl.createtable.constraints
import io.github.goodgoodjm.otter.core.dsl.type.Type.INT
import io.github.goodgoodjm.otter.core.dsl.type.Type.VARCHAR

object : Migration() {
    override val comment = "Create Test"

    override fun up() {
        createTable("Test") {
            "id" - INT constraints PRIMARY and AUTO_INCREMENT
            "name" - VARCHAR constraints UNIQUE
            "age" - INT
        }
    }

    override fun down() {
        dropTable("Test")
    }
}