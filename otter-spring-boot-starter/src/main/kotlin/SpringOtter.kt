package io.github.goodgoodjm.otter

import io.github.goodgoodjm.otter.core.Logger
import io.github.goodgoodjm.otter.core.Otter
import io.github.goodgoodjm.otter.core.OtterConfig
import org.springframework.beans.factory.InitializingBean


class SpringOtter(
    otterConfig: OtterConfig,
) : InitializingBean {
    companion object : Logger

    private val otter: Otter = Otter.from(otterConfig)

    override fun afterPropertiesSet() {
        logger.info("Otter migration start.")
        otter.up()
        logger.info("Otter migration end.")
    }
}