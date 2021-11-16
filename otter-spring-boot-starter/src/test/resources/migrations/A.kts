import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.and

object : Migration() {
    override val comment = "Create person"

    override fun up() {
        createTable("person") {
            column("id") {
                type = "INT"
            } constraints (Constraint.PRIMARY and Constraint.NOT_NULL and Constraint.AUTO_INCREMENT)

            column("name") {
                type = "VARCHAR(255)"
            }

            column("age") {
                type = "INT"
            } constraints (Constraint.UNIQUE)
        }
    }

    override fun down() {
        TODO("Not yet implemented")
    }
}