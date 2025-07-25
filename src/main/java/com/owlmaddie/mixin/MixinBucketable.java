package com.owlmaddie.mixin;

import com.owlmaddie.chat.ChatDataManager;
import com.owlmaddie.chat.EventQueueData;
import com.owlmaddie.chat.EventQueueManager;
import com.owlmaddie.components.Components;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * The {@code MixinBucketable} mixin class handles entities that are placed into a bucket, despawned, respawned
 * and updates our chat history for the newly spawned entity.
 */
@Mixin(Bucketable.class)
public interface MixinBucketable {


    //
    @Inject(method = "copyDataToStack(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private static void addCCUUIDToStack(MobEntity entity, ItemStack stack, CallbackInfo ci) {
        Logger LOGGER = LoggerFactory.getLogger("creaturepals");
        UUID originalUUID = entity.getUuid();
        LOGGER.info("Saving original UUID of bucketed entity: " + originalUUID);

        // Add the original UUID to the ItemStack NBT as "CCUUID"
        stack.set(Components.ChatUUID, originalUUID.toString());
    }

    // New method to read CCUUID from NBT
    @Inject(method = "copyDataFromNbt(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private static void readCCUUIDFromNbt(MobEntity entity, NbtCompound nbt, CallbackInfo ci) {
        Logger LOGGER = LoggerFactory.getLogger("creaturepals");
        UUID newUUID = entity.getUuid();
        if (nbt.contains("CCUUID")) {

            String originalUUIDString = nbt.getString("CCUUID");
            UUID originalUUID = UUID.fromString(originalUUIDString);
            LOGGER.info("Duplicating bucketed chat data for original UUID (" + originalUUID + ") to cloned entity: (" + newUUID + ")");
            ChatDataManager.getServerInstance().updateUUID(originalUUID, newUUID);
            EventQueueManager.updateUUID(originalUUID, newUUID, entity);
        }
    }
}