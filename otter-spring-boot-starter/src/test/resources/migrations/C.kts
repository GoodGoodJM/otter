import io.github.goodgoodjm.otter.core.Migration

object : Migration() {
    override val comment = "Raw query test"

    override fun up() {
        rawQuery("INSERT INTO person (name, age) VALUES ('ggm0', 20)")
        rawQuery("INSERT INTO person (name, age) VALUES ('ggm1', 20)")
        rawQuery("INSERT INTO person (name, age) VALUES ('ggm2', 20)")
        rawQuery("INSERT INTO person (name, age) VALUES ('ggm3', 20)")
        rawQuery("INSERT INTO person (name, age) VALUES ('ggm4', 20)")
    }

    override fun down() {
    }
}

