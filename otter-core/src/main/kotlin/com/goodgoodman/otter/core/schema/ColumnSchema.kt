package com.goodgoodman.otter.core.schema

import java.util.*

class ColumnSchema {
    var tableName = ""
    var name = ""
    var type = ""
    val constraints: EnumSet<Constraint> get() = _constraints
    private val _constraints = EnumSet.noneOf(Constraint::class.java)

    val referenceSchemas: List<ReferenceSchema> get() = _referenceSchemas
    private val _referenceSchemas = mutableListOf<ReferenceSchema>()

    fun setConstraint(vararg constraints: Constraint) {
        val items = constraints.filter { it !== Constraint.NONE }.toList()
        _constraints.clear()
        _constraints.addAll(items)
    }

    @SchemaMaker
    fun reference(block: ReferenceSchema.() -> Unit) {
        val referenceSchema = ReferenceSchema()
        referenceSchema.block()
        referenceSchema.apply {
            fromTable = tableName
            fromColumn = name
        }
        _referenceSchemas.add(referenceSchema)
    }

    override fun toString(): String {
        return "ColumnSchema(tableName='$tableName', name='$name', type='$type', _constraints=$_constraints, _referenceSchemas=$_referenceSchemas)"
    }
}