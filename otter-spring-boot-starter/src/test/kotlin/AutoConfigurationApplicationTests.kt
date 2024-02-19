import io.github.goodgoodjm.otter.OtterAutoConfiguration
import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.createtable.and
import io.github.goodgoodjm.otter.core.dsl.createtable.constraints
import io.github.goodgoodjm.otter.core.dsl.createtable.foreignKey
import io.github.goodgoodjm.otter.core.dsl.type.Type
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import kotlin.test.assertEquals


class AutoConfigurationApplicationTests {
    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(OtterAutoConfiguration::class.java))

    @Test
    fun TEMP() {
        contextRunner.withPropertyValues(
            "otter.driverClassName=org.h2.Driver",
            "otter.url=jdbc:h2:mem:test;",
            "otter.username=root",
            "otter.password=",
            "otter.migrationPath=migrations",
            "otter.showSql=true"
        ).run { context ->
            assertThat(context).getBean(OtterAutoConfiguration::class.java)
        }

        contextRunner.withPropertyValues(
            "otter.driverClassName=org.h2.Driver",
            "otter.url=jdbc:h2:mem:test",
            "otter.username=root",
            "otter.password=",
            "otter.migrationPath=migrations",
            "otter.showSql=true",
            "otter.version=A.kts"
        ).run { context ->
            assertThat(context).getBean(OtterAutoConfiguration::class.java)
        }
    }
}