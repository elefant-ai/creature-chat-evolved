// SPDX-License-Identifier: GPL-3.0-or-later | CreatureChat™ © owlmaddie LLC
// Code: GPLv3 | Assets: CC BY-NC 3.0; See LICENSE.md & LICENSE-ASSETS.md.
package com.owlmaddie;

import com.owlmaddie.commands.CreatureChatCommands;
import com.owlmaddie.network.ServerPackets;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code ModInit} class initializes this mod on the server and defines all the server message
 * identifiers. It also listens for messages from the client, and has code to send
 * messages to the client.
 */
public class ModInit implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("creaturechat");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// Register server commands
		CreatureChatCommands.register();

		// Register events
		ServerPackets.register();

		LOGGER.info("CreatureChat MOD Initialized!");
	}
}