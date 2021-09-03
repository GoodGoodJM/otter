import com.goodgoodman.otter.core.Migration

object : Migration() {
    override fun up() {
        createTable {
            name = "person"
            val col = column {
                name = "id"
                type = "INT"
                primary()
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