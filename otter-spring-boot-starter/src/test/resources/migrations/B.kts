import com.goodgoodman.otter.core.Migration
import com.goodgoodman.otter.core.dsl.Constraint

object : Migration() {
    override fun up() {
        createTable("user") {

            column("id") {
                type = "INT"
            } constraints Constraint.PRIMARY

            column("person_id") {
                type = "INT"
            } foreignKey { reference = "person(id)" }
        }
    }

    override fun down() {
        dropTable("user")
    }
}

