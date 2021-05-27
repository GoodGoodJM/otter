package com.goodgoodman.otter

import org.slf4j.LoggerFactory

interface Logger {
    val logger: org.slf4j.Logger get() = LoggerFactory.getLogger(this.javaClass)
}
