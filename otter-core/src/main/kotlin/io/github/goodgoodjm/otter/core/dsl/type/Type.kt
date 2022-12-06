package io.github.goodgoodjm.otter.core.dsl.type

import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.createtable.ColumnSchema
import io.github.goodgoodjm.otter.core.dsl.createtable.and
import io.github.goodgoodjm.otter.core.dsl.createtable.constraints
import io.github.goodgoodjm.otter.core.dsl.type.Type.INT
import io.github.goodgoodjm.otter.core.dsl.type.Type.LONG
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.*

class CustomColumnType(val type: String) : ColumnType() {
    override fun sqlType(): String = type
}

object Type {
    fun custom(type: String): ColumnSchema = ColumnSchema(type)

    fun byte(): ColumnSchema = ColumnSchema(ByteColumnType())
    val BYTE get() = byte()

    @ExperimentalUnsignedTypes
    fun ubyte(): ColumnSchema = ColumnSchema(UByteColumnType())
    val UBYTE get() = ubyte()

    fun short(): ColumnSchema = ColumnSchema(ShortColumnType())
    val SHORT get() = short()

    @ExperimentalUnsignedTypes
    fun ushort(): ColumnSchema = ColumnSchema(UShortColumnType())
    val USHORT get() = ushort()

    fun int(): ColumnSchema = ColumnSchema(IntegerColumnType())
    val INT get() = int()

    @ExperimentalUnsignedTypes
    fun uint(): ColumnSchema = ColumnSchema(UIntegerColumnType())
    val UINT get() = uint()

    fun long(): ColumnSchema = ColumnSchema(LongColumnType())
    val LONG get() = long()

    @ExperimentalUnsignedTypes
    fun ulong(): ColumnSchema = ColumnSchema(ULongColumnType())
    val ULONG get() = ulong()

    fun float(): ColumnSchema = ColumnSchema(FloatColumnType())
    val FLOAT get() = float()

    fun double(): ColumnSchema = ColumnSchema(DoubleColumnType())
    val DOUBLE get() = double()

    fun decimal(precision: Int, scale: Int): ColumnSchema = ColumnSchema(DecimalColumnType(precision, scale))
    fun DECIMAL(precision: Int, scale: Int) = decimal(precision, scale)

    fun char(): ColumnSchema = ColumnSchema(CharacterColumnType())
    val CHAR get() = char()

    fun char(length: Int, collate: String? = null): ColumnSchema =
        ColumnSchema(CharColumnType(length, collate))

    fun CHAR(length: Int, collate: String? = null): ColumnSchema = char(length, collate)

    fun varchar(length: Int = 255, collate: String? = null): ColumnSchema =
        ColumnSchema(VarCharColumnType(length, collate))

    val VARCHAR get() = varchar()
    fun VARCHAR(length: Int = 255, collate: String? = null) = varchar(length, collate)

    fun text(collate: String? = null, eagerLoading: Boolean = false): ColumnSchema =
        ColumnSchema(TextColumnType(collate, eagerLoading))

    fun TEXT(collate: String? = null, eagerLoading: Boolean = false) = text(collate, eagerLoading)

    fun binary(): ColumnSchema = ColumnSchema(BasicBinaryColumnType())
    val BINARY get() = binary()

    fun binary(length: Int): ColumnSchema = ColumnSchema(BinaryColumnType(length))

    fun blob(): ColumnSchema = ColumnSchema(BlobColumnType())
    val BLOB get() = blob()

    fun uuid(): ColumnSchema = ColumnSchema(UUIDColumnType())
    val UUID get() = uuid()

    fun bool(): ColumnSchema = ColumnSchema(BooleanColumnType())
    val BOOL get() = bool()

    fun date(): ColumnSchema = ColumnSchema(JavaLocalDateColumnType())
    val DATE get() = date()

    fun datetime(): ColumnSchema = ColumnSchema(JavaLocalDateTimeColumnType())
    val DATETIME get() = datetime()

    fun time(): ColumnSchema = ColumnSchema(JavaLocalTimeColumnType())
    val TIME get() = time()

    fun timestamp(): ColumnSchema = ColumnSchema(JavaInstantColumnType())
    val TIMESTAMP get() = timestamp()

    fun duration(): ColumnSchema = ColumnSchema(JavaDurationColumnType())
    val DURATION get() = duration()
}

object TypeUtils {
    val ID get() = INT constraints Constraint.PRIMARY and Constraint.AUTO_INCREMENT
    val LONG_ID get() = LONG constraints Constraint.PRIMARY and Constraint.AUTO_INCREMENT
}