import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint

object : Migration() {
    override val comment = "Raw query test"

    override fun up() {
        rawQuery("INSERT INTO person (name) VALUES ('ggm0')")
        rawQuery("INSERT INTO person (name) VALUES ('ggm1')")
        rawQuery("INSERT INTO person (name) VALUES ('ggm2')")
        rawQuery("INSERT INTO person (name) VALUES ('ggm3')")
        rawQuery("INSERT INTO person (name) VALUES ('ggm4')")
    }

    override fun down() {
    }
}

