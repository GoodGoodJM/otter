import com.goodgoodman.otter.OtterAutoConfiguration
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
                .extracting { it.getConfig().driverClassName }
                .isEqualTo("asd")
        }
    }

    @Test
    fun TEMP() {
        contextRunner.withPropertyValues(
            "otter.driverClassName=org.mariadb.jdbc.Driver",
            "otter.url=jdbc:mariadb://localhost:13306/temp",
            "otter.username=root",
            "otter.password=good9006())^",
            "otter.migrationPath=migrations",
            "otter.showSql=true",
        ).run { context ->
            assertThat(context).getBean(OtterAutoConfiguration::class.java)
                .extracting { it.getConfig().driverClassName }
                .isEqualTo("org.mariadb.jdbc.Driver")
        }
    }
}