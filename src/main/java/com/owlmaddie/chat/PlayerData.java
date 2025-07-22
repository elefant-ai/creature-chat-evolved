package com.owlmaddie.chat;

import com.google.gson.annotations.Expose;


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