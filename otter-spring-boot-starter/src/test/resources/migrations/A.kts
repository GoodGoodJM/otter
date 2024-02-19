import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint.*
import io.github.goodgoodjm.otter.core.dsl.and
import io.github.goodgoodjm.otter.core.dsl.constraints
import io.github.goodgoodjm.otter.core.dsl.createtable.and
import io.github.goodgoodjm.otter.core.dsl.createtable.constraints
import io.github.goodgoodjm.otter.core.dsl.createtable.default
import io.github.goodgoodjm.otter.core.dsl.default
import io.github.goodgoodjm.otter.core.dsl.foreignKey
import io.github.goodgoodjm.otter.core.dsl.type.Type.INT
import io.github.goodgoodjm.otter.core.dsl.type.Type.VARCHAR

object : Migration() {
    override val comment = "Create Test"

    override fun up() {
        createTable("Test") {
            "id" - INT constraints PRIMARY and AUTO_INCREMENT
            "name" - VARCHAR constraints UNIQUE
            "age" - INT default 1 constraints UNIQUE
        }

        alter("test"){
            modify("test_column") - INT constraints PRIMARY and AUTO_INCREMENT
            add("add_column") - VARCHAR constraints UNIQUE default "" foreignKey "member(id)"
            drop("drop_column")
            rename("old_column_name") - "new_column_name"
        }
    }

    override fun down() {
        dropTable("Test")
    }
}