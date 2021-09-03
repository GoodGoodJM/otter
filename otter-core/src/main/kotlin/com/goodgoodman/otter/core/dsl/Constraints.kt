package com.goodgoodman.otter.core.dsl

enum class Constraint {
    NONE,
    AUTO_INCREMENT,
    NOT_NULL,
    UNIQUE,
    PRIMARY,
}

infix fun Constraint.and(constraint: Constraint): List<Constraint> {
    return listOf(this, constraint)
}

infix fun List<Constraint>.and(constraint: Constraint): List<Constraint> {
    return listOf(*this.toTypedArray(), constraint)
}