package io.github.goodgoodjm.otter

import io.github.goodgoodjm.otter.core.Logger
import io.github.goodgoodjm.otter.core.Otter
import io.github.goodgoodjm.otter.core.OtterConfig
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component


@Component
class SpringOtter(
    otterConfig: OtterConfig,
    private val enabled: Boolean,
) : InitializingBean {
    companion object : Logger

    private val otter: Otter = Otter.from(otterConfig)

    override fun afterPropertiesSet() {
        if (!enabled) {
            logger.info("Otter is not enabled, Stop to load migrations.")
            return
        }
        otter.up()
    }
}