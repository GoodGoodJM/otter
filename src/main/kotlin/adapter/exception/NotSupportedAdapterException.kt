package com.goodgoodman.otter.adapter.exception

class NotSupportedAdapterException(driverClassName: String) : Exception("[$driverClassName] is not supported.")