package com.owlmaddie.chat;

import static com.owlmaddie.network.ServerPackets.BroadcastEntityMessage;
import static com.owlmaddie.network.ServerPackets.serverInstance;

import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlmaddie.chat.ChatDataManager.ChatSender;
import com.owlmaddie.chat.ChatDataManager.ChatStatus;
import com.owlmaddie.message.MessageParser;
import com.owlmaddie.message.ParsedMessage;
import com.owlmaddie.network.ServerPackets;
import com.owlmaddie.utils.Randomizer;
import com.owlmaddie.utils.ServerEntityFinder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

// side effects that are broadcast to client or modify chatData
public class ClientSideEffects {
    public static final Logger LOGGER = LoggerFactory.getLogger("creaturechat");

    private static EntityChatData getChatData(UUID entityId) {
        return ChatDataManager.getServerInstance().getOrCreateChatData(entityId);
    }

    public static void onEntityGeneratedMessage(UUID entityId,
            String uncleanEntityMessageResponse, ServerPlayerEntity player) {
        ParsedMessage result = MessageParser.parseMessage(uncleanEntityMessageResponse.replace("\n", " "));
        PlayerData playerData = getChatData(entityId).getPlayerData(player.getUuid());
        BehaviorApplier.apply(result.getBehaviors(), player, entityId, playerData);
        String cleanedMessage = result.getCleanedMessage();

        if (cleanedMessage.isEmpty()) {
            getChatData(entityId).addMessage("...", ChatDataManager.ChatSender.ASSISTANT, player);
            BroadcastEntityMessage(new EntityChatDataLight(entityId, "...", 0, ChatStatus.DISPLAY, ChatSender.USER,
                    getChatData(entityId).characterSheet, getChatData(entityId).players));
        } else {
            getChatData(entityId).addMessage(uncleanEntityMessageResponse, ChatDataManager.ChatSender.ASSISTANT,
                    player);
            BroadcastEntityMessage(new EntityChatDataLight(entityId, cleanedMessage, 0, ChatStatus.DISPLAY,
                    ChatSender.USER, getChatData(entityId).characterSheet, getChatData(entityId).players));
        }
    }

    // note that this includes greeting, only sends if shouldGreet is true
    public static void onCharacterSheetGenerated(UUID entityId, String characterSheet,
            boolean shouldGreet, ServerPlayerEntity player) {

        getChatData(entityId).characterSheet = characterSheet;
        String characterName = Optional.ofNullable(getChatData(entityId).getCharacterProp("name"))
                .filter(s -> !s.isEmpty())
                .orElse("N/A");
        if (characterName.equals("N/A")) {
            throw new RuntimeException(
                    "Generated \"\" or \"N/A\" as a character name");
        }
        String shortGreeting = Optional.ofNullable(getChatData(entityId).getCharacterProp("short greeting"))
                .filter(s -> !s.isEmpty())
                .orElse(Randomizer.getRandomMessage(Randomizer.RandomType.NO_RESPONSE))
                .replace("\n", " ");
        setNameOfEntity(entityId, characterName);
        if (shouldGreet) {
            getChatData(entityId).addMessage(shortGreeting, ChatDataManager.ChatSender.ASSISTANT, player);
            sendChatAsEntity(entityId, shortGreeting);
        }
    }

    private static void setNameOfEntity(UUID entityId, String characterName) {
        for (ServerWorld world : serverInstance.getWorlds()) {
            // Find Entity by UUID and update custom name.
            // Also sends to clients I think?
            MobEntity entity = (MobEntity) ServerEntityFinder.getEntityByUUID(world, entityId);
            if (entity != null) {
                if (!characterName.isEmpty() && !characterName.equals("N/A") && entity.getCustomName() == null) {
                    LOGGER.debug("Setting MC Server Entity name to " + characterName + " for " + entityId);
                    entity.setCustomName(Text.literal(characterName));
                    entity.setCustomNameVisible(true);
                    entity.setPersistent();
                }
            }
        }
    }

    public static void onLLMGenerateError(UUID entityId, String errMsg, ServerPlayerEntity player) {
        LOGGER.error("Side effect: onLLMGenerateError, clearing msg. Error:", errMsg);
        String errorMessage = "Error: ";
        errorMessage += EntityChatData.truncateString(errMsg, 55) + "\n";
        errorMessage += "Help is available at elefant.gg/discord";
        sendChatAsEntity(entityId, errorMessage);
        ServerPackets.SendClickableError(player, errorMessage, "https://elefant.gg/discord");
    }

    public static void sendChatAsEntity(UUID entityId, String message) {
        ServerPackets.BroadcastEntityMessage(new EntityChatDataLight(entityId, message, 0, ChatStatus.DISPLAY,
                ChatSender.ASSISTANT, getChatData(entityId).characterSheet, getChatData(entityId).players));
    }

    public static void setPending(UUID entityId) {
        LOGGER.info("Side effect: Set pending " + entityId.toString());
        if (getChatData(entityId).previousMessages.size() == 0) {
            ServerPackets.BroadcastEntityMessage(new EntityChatDataLight(entityId, "", 0, ChatStatus.PENDING,
                    ChatSender.USER, getChatData(entityId).characterSheet, getChatData(entityId).players));
            return;
        }
        setStatusUsingParamsFromChatData(entityId, ChatStatus.PENDING);
    }

    public static void setStatusUsingParamsFromChatData(UUID entityId, ChatStatus status) {
        if (getChatData(entityId).previousMessages.size() == 0) {
            throw new RuntimeException("Only call setStatusUsingParamsFromChatData when msgs > 0");
        }
        ChatMessage topMessage = getChatData(entityId).previousMessages
                .get(getChatData(entityId).previousMessages.size() - 1);

        // update chat data
        getChatData(entityId).status = status;

        // broadcast
        ServerPackets.BroadcastEntityMessage(
                new EntityChatDataLight(entityId, topMessage.message, getChatData(entityId).currentLineNumber, status,
                        topMessage.sender, getChatData(entityId).characterSheet, getChatData(entityId).players));
    }

    public static void updateUUID(UUID oldUUID, UUID newUUID) {
        throw new RuntimeException("implement this");
    }

    public static void setLineNumberUsingParamsFromChatData(UUID entityId, int lineNumber) {
        // // Ensure the lineNumber is within the valid range
        int totalLines = getChatData(entityId).getWrappedLines().size();

        // update chat data
        getChatData(entityId).currentLineNumber = Math.min(Math.max(lineNumber, 0), totalLines);

        // broadcast
        ChatMessage topMessage = getChatData(entityId).previousMessages
                .get(getChatData(entityId).previousMessages.size() - 1);
        ServerPackets.BroadcastEntityMessage(new EntityChatDataLight(entityId, topMessage.message,
                getChatData(entityId).currentLineNumber, getChatData(entityId).status, topMessage.sender,
                getChatData(entityId).characterSheet, getChatData(entityId).players));
    }
}
