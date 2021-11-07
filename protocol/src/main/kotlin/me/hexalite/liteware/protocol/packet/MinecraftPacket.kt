package me.hexalite.liteware.protocol.packet

import me.hexalite.liteware.protocol.codec.MinecraftPacketCodec

sealed interface MinecraftPacket {
    val id: Int

    val codec: MinecraftPacketCodec

    abstract class Inbound: MinecraftPacket

    abstract class Outbound: MinecraftPacket
}

