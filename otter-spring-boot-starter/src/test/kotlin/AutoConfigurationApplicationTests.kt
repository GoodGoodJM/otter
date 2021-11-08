import io.github.goodgoodjm.otter.OtterAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner


class AutoConfigurationApplicationTests {
    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(OtterAutoConfiguration::class.java))

    @Test
    fun otterConfigExists() {
        contextRunner.run {
            assertThat(it).hasSingleBean(OtterAutoConfiguration::class.java)
        }
    }

    @Test
    fun otterConfigProperties() {
        contextRunner.withPropertyValues("otter.driverClassName=asd").run { context ->
            assertThat(context).getBean(OtterAutoConfiguration::class.java)
                .isEqualTo("asd")
        }
    }

    @Test
    fun TEMP() {
        contextRunner.withPropertyValues(
            "otter.driverClassName=org.h2.Driver",
            "otter.url=jdbc:h2:mem:test",
            "otter.username=root",
            "otter.password=",
            "otter.migrationPath=migrations",
            "otter.showSql=true",
        ).run { context ->
            assertThat(context).getBean(OtterAutoConfiguration::class.java)
        }
    }
}