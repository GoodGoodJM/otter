package com.goodgoodman.otter.core.querygenerator.exception

import com.goodgoodman.otter.core.schema.AlterColumnSchema

data class UnsupportedAlterColumnTypeException(val alterColumnSchema: AlterColumnSchema) :
    Exception("AlterColumnType(${alterColumnSchema.alterType}) is not supported to this QueryGenerator")