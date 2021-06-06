package com.goodgoodman.otter.querygenerator.exception

import com.goodgoodman.otter.schema.AlterColumnSchema

data class UnsupportedAlterColumnTypeException(val alterColumnSchema: AlterColumnSchema) :
    Exception("AlterColumnType(${alterColumnSchema.alterType}) is not supported to this QueryGenerator")