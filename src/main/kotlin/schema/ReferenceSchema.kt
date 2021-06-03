package com.goodgoodman.otter.schema

class ReferenceSchema {
    var key = ""
    var table = ""
    var column = ""

    override fun toString(): String {
        return "ReferenceSchema(key='$key', table='$table', column='$column')"
    }
}