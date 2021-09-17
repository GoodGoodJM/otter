import com.goodgoodman.otter.core.Migration
import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.and

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
        }
    }

    override fun down() {
        TODO("Not yet implemented")
    }
}