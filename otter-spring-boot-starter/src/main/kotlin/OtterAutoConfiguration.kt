package io.github.goodgoodjm.otter

import io.github.goodgoodjm.otter.core.OtterConfig
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.*
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = ["otter.enable"], havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class)
@Import(DatabaseInitializationDependencyConfigurer::class)
open class OtterAutoConfiguration(
) {
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(OtterProperties::class)
    open class OtterConfiguration(
        properties: OtterProperties,
    ) {
        private val config: OtterConfig = OtterConfig(
            migrationPath = properties.migrationPath,
            driverClassName = properties.driverClassName,
            url = properties.url,
            user = properties.username,
            password = properties.password,
            showSql = properties.showSql,
        )

        @Bean
        open fun config(): OtterConfig = config

        @Bean
        open fun otter(): SpringOtter = SpringOtter(config)
    }
}
