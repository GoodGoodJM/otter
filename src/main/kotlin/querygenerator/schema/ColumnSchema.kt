package com.goodgoodman.otter.querygenerator.schema

import java.util.*

class ColumnSchema {
    var name = ""
    var comment = ""
    var type = ""
    val constraints: EnumSet<Constraint> get() = _constraints
    private val _constraints = EnumSet.noneOf(Constraint::class.java)

    fun setConstraint(vararg constraint: Constraint) {
        constraints.addAll(constraint)
    }

    override fun toString(): String {
        return "ColumnSchema(name='$name', comment='$comment', type='$type', constraints=$constraints)"
    }
}
