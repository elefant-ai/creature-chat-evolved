package com.owlmaddie.network.S2C;

import com.owlmaddie.network.NetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.List;

public record WhitelistPayload(List<Identifier> whitlelist, List<Identifier> blacklist) implements CustomPayload {
    public static final CustomPayload.Id<WhitelistPayload> ID = new CustomPayload.Id<>(NetworkingConstants.PACKET_S2C_WHITELIST);
    public static final PacketCodec<RegistryByteBuf, WhitelistPayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC.collect(PacketCodecs.toList()), WhitelistPayload::whitlelist,
            Identifier.PACKET_CODEC.collect(PacketCodecs.toList()), WhitelistPayload::blacklist,
            WhitelistPayload::new
    );



    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
