package com.goodgoodman.otter.core.dsl.createtable.context.schema

import com.goodgoodman.otter.core.dsl.Constraint
import com.goodgoodman.otter.core.dsl.SchemaMaker
import java.util.*

class ColumnSchema_ {
    var tableName = ""
    var name = ""
    var type = ""
    val constraints: EnumSet<Constraint> get() = _constraints
    private val _constraints = EnumSet.noneOf(Constraint::class.java)

    val referenceSchemas: List<RefeenceSchema_> get() = _referenceSchemas
    private val _referenceSchemas = mutableListOf<RefeenceSchema_>()

    fun primary() {
        _constraints.add(Constraint.PRIMARY)
    }

    fun setConstraint(vararg constraints: Constraint) {
        val items = constraints.filter { it !== Constraint.NONE }.toList()
        _constraints.clear()
        _constraints.addAll(items)
    }

    @SchemaMaker
    fun reference(block: RefeenceSchema_.() -> Unit) {
        val referenceSchema = RefeenceSchema_()
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

