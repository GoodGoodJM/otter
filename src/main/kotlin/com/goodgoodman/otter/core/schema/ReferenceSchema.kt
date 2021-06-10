package com.goodgoodman.otter.core.schema

class ReferenceSchema {
    var key = ""
    var fromTable = ""
    var fromColumn = ""
    var toTable = ""
    var toColumn = ""

    override fun toString(): String {
        return "ReferenceSchema(key='$key', fromTable='$fromTable', fromColumn='$fromColumn', toTable='$toTable', toColumn='$toColumn')"
    }
}