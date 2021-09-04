import com.goodgoodman.otter.core.Migration
import com.goodgoodman.otter.core.dsl.Constraint

object : Migration() {
    override fun up() {
        createTable("person") {
            column("id") {
                type = "INT"
            } constraints Constraint.PRIMARY

            column("name") {
                type = "VARCHAR(255)"
            }
        }
    }

    override fun down() {
        TODO("Not yet implemented")
    }
}