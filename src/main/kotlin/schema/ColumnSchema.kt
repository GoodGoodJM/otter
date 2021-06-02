package com.goodgoodman.otter.schema

import java.util.*

class ColumnSchema {
    var name = ""
    var comment = ""
    var type = ""
    val constraints: EnumSet<Constraint> get() = _constraints
    private val _constraints = EnumSet.noneOf(Constraint::class.java)

    fun setConstraint(vararg constraints: Constraint) {
        val items = constraints.filter { it !== Constraint.NONE }.toList()
        _constraints.clear()
        _constraints.addAll(items)
    }

    override fun toString(): String {
        return "ColumnSchema(name='$name', comment='$comment', type='$type', constraints=$constraints)"
    }
}
