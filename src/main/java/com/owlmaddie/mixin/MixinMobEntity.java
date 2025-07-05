// SPDX-FileCopyrightText: 2025 owlmaddie LLC
// SPDX-License-Identifier: GPL-3.0-or-later
// Assets CC-BY-NC-SA-4.0; CreatureChat™ trademark © owlmaddie LLC - unauthorized use prohibited
package com.owlmaddie.mixin;

import com.owlmaddie.chat.ChatDataManager;
import com.owlmaddie.chat.EntityChatData;
import com.owlmaddie.chat.PlayerData;
import com.owlmaddie.network.ServerPackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The {@code MixinMobEntity} mixin class exposes the goalSelector field from the MobEntity class.
 */
@Mixin(Mob.class)
public class MixinMobEntity {

    @Inject(method = "interact", at = @At(value = "RETURN"))
    private void onItemGiven(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        // Only process interactions on the server side
        if (player.level().isClientSide()) {
            return;
        }

        // Only process interactions for the main hand
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack itemStack = player.getItemInHand(hand);
        Mob thisEntity = (Mob) (Object) this;

        // Don't interact with Villagers (avoid issues with trade UI) OR Tameable (i.e. sit / no-sit)
        if (thisEntity instanceof Villager || thisEntity instanceof TamableAnimal) {
            return;
        }

        // Determine if the item is a bucket
        // We don't want to interact on buckets
        Item item = itemStack.getItem();
        if (item == Items.BUCKET ||
                item == Items.WATER_BUCKET ||
                item == Items.LAVA_BUCKET ||
                item == Items.POWDER_SNOW_BUCKET ||
                item == Items.MILK_BUCKET ||
                item == Items.PUFFERFISH_BUCKET ||
                item == Items.SALMON_BUCKET ||
                item == Items.COD_BUCKET ||
                item == Items.TROPICAL_FISH_BUCKET ||
                item == Items.AXOLOTL_BUCKET ||
                item == Items.TADPOLE_BUCKET) {
            return;
        }

        // Get chat data for entity
        ChatDataManager chatDataManager = ChatDataManager.getServerInstance();
        EntityChatData entityData = chatDataManager.getOrCreateChatData(thisEntity.getStringUUID());
        PlayerData playerData = entityData.getPlayerData(player.getDisplayName().getString());

        // Check if the player successfully interacts with an item
        if (player instanceof ServerPlayer) {
            // Player has item in hand
            if (!itemStack.isEmpty()) {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                String itemName = itemStack.getItem().getDescription().getString();
                int itemCount = itemStack.getCount();

                // Decide verb
                String action_verb = " shows ";
                if (cir.getReturnValue().consumesAction()) {
                    action_verb = " gives ";
                }

                // Prepare a message about the interaction
                String giveItemMessage = "<" + serverPlayer.getName().getString() +
                        action_verb + "you " + itemCount + " " + itemName + ">";

                if (!entityData.characterSheet.isEmpty() && entityData.auto_generated < chatDataManager.MAX_AUTOGENERATE_RESPONSES) {
                    ServerPackets.generate_chat("N/A", entityData, serverPlayer, thisEntity, giveItemMessage, true);
                }

            } else if (itemStack.isEmpty() && playerData.friendship == 3) {
                // Player's hand is empty, Ride your best friend!
                player.startRiding(thisEntity, true);
            }
        }
    }
}