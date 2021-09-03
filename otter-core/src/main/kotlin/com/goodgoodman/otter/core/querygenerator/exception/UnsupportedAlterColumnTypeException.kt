package com.goodgoodman.otter.core.querygenerator.exception

import com.goodgoodman.otter.core.dsl.AlterColumnSchema

data class UnsupportedAlterColumnTypeException(val alterColumnSchema: AlterColumnSchema) :
    Exception("AlterColumnType(${alterColumnSchema.alterType}) is not supported to this QueryGenerator")