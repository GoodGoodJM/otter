import com.goodgoodman.otter.core.Migration
import com.goodgoodman.otter.core.dsl.Constraint

object : Migration() {
    override fun up() {
        createTable {
            name = "user"
            column {
                name = "id"
                type = "INT"
                setConstraint(Constraint.PRIMARY)
            }
            column {
                name = "person_id"
                type = " INT"
                reference {
                    toTable = "person"
                    toColumn = "id"
                    key = "fk_user_person"
                }
            }
        }
    }

    override fun down() {
        TODO("Not yet implemented")
    }

}