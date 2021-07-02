package com.goodgoodman.otter

import com.goodgoodman.otter.core.Logger
import com.goodgoodman.otter.core.OtterConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OtterProperties::class)
open class OtterAutoConfiguration(
    properties: OtterProperties,
) {
    companion object : Logger

    private val enabled: Boolean = properties.enabled

    private val config: OtterConfig = OtterConfig(
        migrationPath = properties.migrationPath,
        driverClassName = properties.driverClassName,
        url = properties.url,
        user = properties.username,
        password = properties.password,
        showSql = properties.showSql,
    )

    init {
        logger.info("Configure otter by $properties")
    }

    @Bean
    open fun getConfig(): OtterConfig = config

    @Bean
    open fun getSpringOtter(): SpringOtter = SpringOtter(config, enabled)
}