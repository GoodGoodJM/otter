package io.github.goodgoodjm.otter.core.dsl.createtable

import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.Constraint.*
import io.github.goodgoodjm.otter.core.dsl.SchemaMaker
import io.github.goodgoodjm.otter.core.dsl.type.Type.int

class v2 {
}

class Table @SchemaMaker constructor(val name: String, block: (TableSchem) -> Unit) {
    operator fun set(key: String, value: String) {}
}

fun temp() {
    Table("name") {
        it["id"] = int() constraints PRIMARY and AUTO_INCREMENT
        it["address_id"] = int() foreignKey "address(id)"
    }

}

class TableSchem() {
    operator fun set(s: String, value: ColumnSchem) {

    }

    val map = mutableMapOf<String, String>()

}


data class ColumnSchem constructor(
    val type: String,
    val constraints: List<Constraint> = listOf(),
) {
}


@SchemaMaker
infix fun ColumnSchem.constraints(ctx: Constraint): ColumnSchem {
    return copy(constraints = constraints + ctx)
    TODO()
}

@SchemaMaker
infix fun ColumnSchem.and(constraint: Constraint): ColumnSchem {
    TODO()
}

@SchemaMaker
infix fun ColumnSchem.foreignKey(value: String): ColumnSchem {
    TODO()
}
