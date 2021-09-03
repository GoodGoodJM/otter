package com.goodgoodman.otter.core.dsl

import java.util.*

class AlterColumnSchema {
    enum class Type {
        NONE, ADD, DROP, MODIFY
    }

    var alterType: Type = Type.NONE
    var table: String = ""
    var name: String = ""
    var type: String = ""

    val constraints: EnumSet<Constraint> get() = _constraints
    private val _constraints = EnumSet.noneOf(Constraint::class.java)

    fun setConstraint(vararg constraints: Constraint) {
        val items = constraints.filter { it !== Constraint.NONE }.toList()
        _constraints.clear()
        _constraints.addAll(items)
    }

    override fun toString(): String {
        return "AlterColumnSchema(alterType=$alterType, table='$table', name='$name', type='$type', _constraints=$_constraints)"
    }
}