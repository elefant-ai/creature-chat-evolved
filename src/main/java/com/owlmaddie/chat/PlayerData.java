package com.owlmaddie.chat;

import com.google.gson.annotations.Expose;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

/**
 * The {@code PlayerData} class represents data associated with a player,
 * specifically tracking their friendship level.
 */
public class PlayerData {

    @Expose
    public int friendship;

    public PlayerData() {
        this.friendship = 0;
    }
}