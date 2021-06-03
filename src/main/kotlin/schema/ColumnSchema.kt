package com.goodgoodman.otter.schema

import java.util.*

class ColumnSchema {
    var name = ""
    var comment = ""
    var type = ""
    val constraints: EnumSet<Constraint> get() = _constraints
    private val _constraints = EnumSet.noneOf(Constraint::class.java)

    val referenceSchema: List<ReferenceSchema> get() = _referenceSchemas
    private val _referenceSchemas = mutableListOf<ReferenceSchema>()

    fun setConstraint(vararg constraints: Constraint) {
        val items = constraints.filter { it !== Constraint.NONE }.toList()
        _constraints.clear()
        _constraints.addAll(items)
    }

    fun reference(block: ReferenceSchema.() -> Unit) {
        val referenceSchema = ReferenceSchema()
        referenceSchema.block()
        _referenceSchemas.add(referenceSchema)
    }

    override fun toString(): String {
        return "ColumnSchema(name='$name', comment='$comment', type='$type', _constraints=$_constraints, _referenceSchemas=$_referenceSchemas)"
    }
}