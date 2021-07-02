import com.goodgoodman.otter.core.Migration
import com.goodgoodman.otter.core.schema.Constraint

object : Migration() {
    override fun up() {
        createTable {
            name = "person"
            column {
                name = "id"
                type = "INT"
                setConstraint(Constraint.PRIMARY)
            }
            column {
                name = "name"
                type = "VARCHAR(255)"
            }
        }
    }

    override fun down() {
        TODO("Not yet implemented")
    }

}