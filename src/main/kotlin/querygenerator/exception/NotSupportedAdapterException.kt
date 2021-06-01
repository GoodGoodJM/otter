package com.goodgoodman.otter.querygenerator.exception

class NotSupportedAdapterException(driverClassName: String) : Exception("[$driverClassName] is not supported.")