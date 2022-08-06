package io.github.goodgoodjm.otter.core.dsl.type

import io.github.goodgoodjm.otter.core.dsl.createtable.ColumnSchema
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.*

class CustomColumnType(val type: String) : ColumnType() {
    override fun sqlType(): String = type
}

object Type {
    fun custom(type: String): ColumnSchema = ColumnSchema(type)

    fun byte(): ColumnSchema = ColumnSchema(ByteColumnType())

    @ExperimentalUnsignedTypes
    fun ubyte(): ColumnSchema = ColumnSchema(UByteColumnType())

    fun short(): ColumnSchema = ColumnSchema(ShortColumnType())

    @ExperimentalUnsignedTypes
    fun ushort(): ColumnSchema = ColumnSchema(UShortColumnType())

    fun int(): ColumnSchema = ColumnSchema(IntegerColumnType())

    @ExperimentalUnsignedTypes
    fun uint(): ColumnSchema = ColumnSchema(UIntegerColumnType())

    fun long(): ColumnSchema = ColumnSchema(LongColumnType())

    @ExperimentalUnsignedTypes
    fun ulong(): ColumnSchema = ColumnSchema(ULongColumnType())

    fun float(): ColumnSchema = ColumnSchema(FloatColumnType())

    fun double(): ColumnSchema = ColumnSchema(DoubleColumnType())

    fun decimal(precision: Int, scale: Int): ColumnSchema = ColumnSchema(DecimalColumnType(precision, scale))

    fun char(): ColumnSchema = ColumnSchema(CharacterColumnType())

    fun char(length: Int, collate: String? = null): ColumnSchema =
        ColumnSchema(CharColumnType(length, collate))

    fun varchar(length: Int = 255, collate: String? = null): ColumnSchema =
        ColumnSchema(VarCharColumnType(length, collate))

    fun text(collate: String? = null, eagerLoading: Boolean = false): ColumnSchema =
        ColumnSchema(TextColumnType(collate, eagerLoading))

    fun binary(): ColumnSchema = ColumnSchema(BasicBinaryColumnType())

    fun binary(length: Int): ColumnSchema = ColumnSchema(BinaryColumnType(length))

    fun blob(): ColumnSchema = ColumnSchema(BlobColumnType())

    fun uuid(): ColumnSchema = ColumnSchema(UUIDColumnType())

    fun bool(): ColumnSchema = ColumnSchema(BooleanColumnType())

    fun date(): ColumnSchema = ColumnSchema(JavaLocalDateColumnType())

    fun datetime(): ColumnSchema = ColumnSchema(JavaLocalDateTimeColumnType())

    fun time(): ColumnSchema = ColumnSchema(JavaLocalTimeColumnType())

    fun timestamp(): ColumnSchema = ColumnSchema(JavaInstantColumnType())

    fun duration(): ColumnSchema = ColumnSchema(JavaDurationColumnType())
}

