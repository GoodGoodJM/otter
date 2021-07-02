package com.goodgoodman.otter

import com.goodgoodman.otter.core.Logger
import com.goodgoodman.otter.core.Otter
import com.goodgoodman.otter.core.OtterConfig
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