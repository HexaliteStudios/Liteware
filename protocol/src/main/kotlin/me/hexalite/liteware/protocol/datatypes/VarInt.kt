package me.hexalite.liteware.protocol.datatypes

import io.ktor.utils.io.core.*
import me.hexalite.liteware.protocol.exception.InvalidDataTypeException
import kotlin.experimental.and

interface VarInt {
    val integer: Int

    fun toInt() = integer
}

@JvmInline
value class SignedVarInt(override val integer: Int) : VarInt

@JvmInline
value class UnsignedVarInt(override val integer: Int) : VarInt

fun VarInt(int: Int): VarInt {
    if(int < 0) {
        if(int < -2147483648) {
            throw InvalidDataTypeException("Invalid VarInt (must be in -2147483648..4294967295).")
        }
        return SignedVarInt(int)
    }
    if (int > 4294967295) {
        throw InvalidDataTypeException("Invalid VarInt (must be in -2147483648..4294967295).")
    }
    return UnsignedVarInt(int)
}

@OptIn(ExperimentalIoApi::class)
fun BytePacketBuilder.writeVarInt(varInt: VarInt) {
    var value = varInt.integer

    while (true) {
        if ((value and 0xFFFFFF80.toInt()) == 0) {
            writeByte(value.toByte())
            return
        }
        writeByte(((value and 0x7F) or 0x80).toByte())
        value = value ushr 7
    }
}

fun ByteReadPacket.readVarInt(): VarInt {
    var value = 0
    var offset = 0

    while (true) {
        if (offset == 35) {
            throw InvalidDataTypeException("Invalid (un)signed VarInt (must be in -2147483648..2147483647).")
        }
        val b = readByte()
        value = value or ((b and 0x7F).toInt() shl offset)
        if ((b and 0x80.toByte()) == 0.toByte()) {
            return VarInt(value)
        }
        offset += 7
    }
}
