package com.owlmaddie.ui;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.owlmaddie.chat.ChatDataManager;
import com.owlmaddie.chat.EntityChatData;
import com.owlmaddie.chat.PlayerData;
import com.owlmaddie.utils.EntityHeights;
import com.owlmaddie.utils.TextureLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.owlmaddie.ui.BubbleRenderer.*;

public abstract class BubbleEntityRenderer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends FeatureRenderer<S, M> {
    public static final Logger LOGGER = LoggerFactory.getLogger("creaturechat");
    protected static TextureLoader TEXTURES = new TextureLoader();
    public static int DISPLAY_PADDING = 2;
    public static int ANIMATION_FRAME = 0;
    public static long LAST_TICK = 0;
    public static int LIGHT = 15728880;
    public static int OVERLAY = OverlayTexture.DEFAULT_UV;
    public static List<Identifier> WHITELIST = new ArrayList<>();
    public static List<Identifier> BLACKLIST = new ArrayList<>();
    private static int QUERY_ENTITY_DATA_COUNT = 0;
    private static List<LivingEntity> RELEVANT_ENTITIES;
    public BubbleEntityRenderer(M model) {
        super(new BubbleRendererContext<S,M>() {
            @Override
            public M getModel() {
                return model;
            }
        });
    }

    public static void drawIcon(String ui_icon_name, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, float x, float y, float width, float height) {
        // Draw button icon

        matrices.push();
        GpuTexture button_texture = TEXTURES.GetUI(ui_icon_name);
        System.out.println("Got button texture: " + button_texture.getLabel());
        // Set shader & texture
        RenderSystem.setShaderTexture(0, button_texture);

        // Enable depth test and blending
        GlStateManager._enableBlend();
        //GlStateManager.defaultBlendFunc();
        GlStateManager._enableDepthTest();
        GlStateManager._depthMask(true);

        // Prepare the tessellator and buffer
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);

        // Get the current matrix position
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        // Begin drawing quads with the correct vertex format

        buffer.vertex(matrix4f, x, y + height, 0.0F).color(255, 255, 255, 255).texture(0, 1).light(LIGHT)
                .overlay(OVERLAY); // bottom left
        buffer.vertex(matrix4f, x + width, y + height, 0.0F).color(255, 255, 255, 255).texture(1, 1).light(LIGHT)
                .overlay(OVERLAY); // bottom right
        buffer.vertex(matrix4f, x + width, y, 0.0F).color(255, 255, 255, 255).texture(1, 0).light(LIGHT)
                .overlay(OVERLAY); // top right
        buffer.vertex(matrix4f, x, y, 0.0F).color(255, 255, 255, 255).texture(0, 0).light(LIGHT).overlay(OVERLAY); // top left

        RenderLayer.getSolid().draw(buffer.end());


        // Disable blending and depth test
        GlStateManager._disableBlend();
        GlStateManager._disableDepthTest();
    }


    protected void renderEntity(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, S entityRenderState) {
        float lineSpacing = 1F;
        float textHeaderHeight = 40F;
        float textFooterHeight = 5F;
        int fullBright = 0xF000F0;
        double renderDistance = 11.0;
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer fontRenderer = client.textRenderer;
        EntityType<?> entityType = entityRenderState.entityType;

        // Push a new matrix onto the stack.
        matrices.push();

        // Get entity height (adjust for specific classes)
        float entityHeight = EntityHeights.getAdjustedEntityHeight(entityType);

        // Interpolate entity position (smooth motion)
        double paddingAboveEntity = 0.4D;
        Vec3d interpolatedEntityPos = new Vec3d(
                MathHelper.lerp(partialTicks, entity.lastX, entityRenderState.x),
                MathHelper.lerp(partialTicks, entity.lastY, entityRenderState.y),
                MathHelper.lerp(partialTicks, entity.lastZ, entityRenderState.z));

        // Determine the chat bubble position
        Vec3d bubblePosition;
        if (entityType == EntityType.ENDER_DRAGON) {
            // Ender dragons a unique, and we must use the Head for position
            EnderDragonEntity dragon = (EnderDragonEntity) entity;
            e
            EnderDragonPart head = dragon.head;

            // Interpolate the head position
            Vec3d headPos = new Vec3d(
                    MathHelper.lerp(partialTicks, head.lastX, head.getX()),
                    MathHelper.lerp(partialTicks, head.lastY, head.getY()),
                    MathHelper.lerp(partialTicks, head.lastZ, head.getZ()));

            // Just use the head's interpolated position directly
            bubblePosition = headPos.add(0, entityHeight + paddingAboveEntity, 0);
        } else {
            // Calculate the forward offset based on the entity's yaw
            float entityYawRadians = (float) Math.toRadians(entity.getYaw(partialTicks));
            Vec3d forwardOffset = new Vec3d(-Math.sin(entityYawRadians), 0.0, Math.cos(entityYawRadians));

            // Calculate the forward offset based on the entity's yaw, scaled to 80% towards
            // the front edge
            Vec3d scaledForwardOffset = forwardOffset.multiply(entity.getWidth() / 2.0 * 0.8);

            // Calculate the position of the chat bubble: above the head and 80% towards the
            // front
            bubblePosition = interpolatedEntityPos.add(scaledForwardOffset)
                    .add(0, entityHeight + paddingAboveEntity, 0);
        }

        // Translate to the chat bubble's position
        matrices.translate(bubblePosition.x - interpolatedCameraPos.x,
                bubblePosition.y - interpolatedCameraPos.y,
                bubblePosition.z - interpolatedCameraPos.z);

        // Calculate the difference vector (from entity + padding above to camera)
        Vec3d difference = interpolatedCameraPos.subtract(new Vec3d(interpolatedEntityPos.x,
                interpolatedEntityPos.y + entityHeight + paddingAboveEntity, interpolatedEntityPos.z));

        // Calculate the yaw angle
        double yaw = -(Math.atan2(difference.z, difference.x) + Math.PI / 2D);

        // Convert yaw to Quaternion
        float halfYaw = (float) yaw * 0.5f;
        double sinHalfYaw = MathHelper.sin(halfYaw);
        double cosHalfYaw = MathHelper.cos(halfYaw);
        Quaternionf yawRotation = new Quaternionf(0, sinHalfYaw, 0, cosHalfYaw);

        // Apply the yaw rotation to the matrix stack
        matrices.multiply(yawRotation);

        // Obtain the horizontal distance to the entity
        double horizontalDistance = Math.sqrt(difference.x * difference.x + difference.z * difference.z);
        // Calculate the pitch angle based on the horizontal distance and the y
        // difference
        double pitch = Math.atan2(difference.y, horizontalDistance);

        // Convert pitch to Quaternion
        float halfPitch = (float) pitch * 0.5f;
        double sinHalfPitch = MathHelper.sin(halfPitch);
        double cosHalfPitch = MathHelper.cos(halfPitch);
        Quaternionf pitchRotation = new Quaternionf(sinHalfPitch, 0, 0, cosHalfPitch);

        // Apply the pitch rotation to the matrix stack
        matrices.multiply(pitchRotation);

        // Get position matrix
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Get the player
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        // Get chat message (if any)
        EntityChatData chatData = null;
        PlayerData playerData = null;
        if (entity instanceof MobEntity) {
            chatData = ChatDataManager.getClientInstance().getOrCreateChatData(entity.getUuid());
            if (chatData != null) {
                playerData = chatData.getPlayerData(player.getUuid());
            }
        } else if (entity instanceof PlayerEntity) {
            chatData = PlayerMessageManager.getMessage(entity.getUuid());
            playerData = new PlayerData(); // no friendship needed for player messages
        }

        float minTextHeight = (ChatDataManager.DISPLAY_NUM_LINES * (fontRenderer.fontHeight + lineSpacing))
                + (DISPLAY_PADDING * 2);
        float scaledTextHeight = 0;

        if (chatData != null) {
            // Set the range of lines to display
            List<String> lines = chatData.getWrappedLines();
            float linesDisplayed = 0;
            int starting_line = chatData.currentLineNumber;
            int ending_line = Math.min(chatData.currentLineNumber + ChatDataManager.DISPLAY_NUM_LINES,
                    lines.size());

            // Determine max line length
            linesDisplayed = ending_line - starting_line;

            // Calculate size of text scaled to world
            scaledTextHeight = linesDisplayed * (fontRenderer.fontHeight + lineSpacing);
            scaledTextHeight = Math.max(scaledTextHeight, minTextHeight);

            // Update Bubble Data for Click Handling using UUID (account for scaling)
            BubbleLocationManager.updateBubbleData(entity.getUuid(), bubblePosition,
                    128F / (1 / 0.02F), (scaledTextHeight + 25F) / (1 / 0.02F), yaw, pitch);

            // Scale down before rendering textures (otherwise font is huge)
            matrices.scale(-0.02F, -0.02F, 0.02F);

            // Translate above the entity
            matrices.translate(0F, -scaledTextHeight - textHeaderHeight - textFooterHeight, 0F);

            // Check if conversation has started
            if (chatData.status == ChatDataManager.ChatStatus.NONE) {
                // AAA if chatData.status == ChatDataManage.ChatStatus.None
                // Draw 'start chat' button
                drawIcon("button-chat", matrices, -16, textHeaderHeight, 32, 17);

                // Draw Entity (Custom Name)
                drawEntityName(entity, matrix, vertexConsumerProvider, fullBright, 24F + DISPLAY_PADDING, true);

            } else if (chatData.status == ChatDataManager.ChatStatus.PENDING) {
                // Draw 'pending' button
                drawIcon("button-dot-" + ANIMATION_FRAME, matrices, -16, textHeaderHeight, 32, 17);

            } else if (chatData.sender == ChatDataManager.ChatSender.ASSISTANT
                    && chatData.status != ChatDataManager.ChatStatus.HIDDEN) {
                // Draw Entity (Custom Name)
                drawEntityName(entity, matrix, vertexConsumerProvider, fullBright, 24F + DISPLAY_PADDING, true);

                // Draw text background (no smaller than 50F tall)
                drawTextBubbleBackground("text-top", matrices, -64, 0, 128, scaledTextHeight,
                        playerData.friendship);

                // Draw face icon of entity
                drawEntityIcon(matrices, entity, -82, 7, 32, 32);

                // Draw Friendship status
                drawFriendshipStatus(matrices, 51, 18, 31, 21, playerData.friendship);

                // Draw 'arrows' & 'keyboard' buttons
                if (chatData.currentLineNumber > 0) {
                    drawIcon("arrow-left", matrices, vertexConsumerProvider,-63, scaledTextHeight + 29, 16, 16);
                }
                if (!chatData.isEndOfMessage()) {
                    drawIcon("arrow-right", matrices, vertexConsumerProvider,47, scaledTextHeight + 29, 16, 16);
                } else {
                    drawIcon("keyboard", matrices, vertexConsumerProvider,47, scaledTextHeight + 28, 16, 16);
                }

                // Render each line of the text
                drawMessageText(matrix, lines, starting_line, ending_line, immediate, lineSpacing, fullBright,
                        40.0F + DISPLAY_PADDING);

            } else if (chatData.sender == ChatDataManager.ChatSender.ASSISTANT
                    && chatData.status == ChatDataManager.ChatStatus.HIDDEN) {
                // Draw Entity (Custom Name)
                drawEntityName(entity, matrix, immediate, fullBright, 24F + DISPLAY_PADDING, false);

                // Draw 'resume chat' button
                if (playerData.friendship == 3) {
                    // Friend chat bubble
                    drawIcon("button-chat-friend", matrices, -16, textHeaderHeight, 32, 17);
                } else if (playerData.friendship == -3) {
                    // Enemy chat bubble
                    drawIcon("button-chat-enemy", matrices, -16, textHeaderHeight, 32, 17);
                } else {
                    // Normal chat bubble
                    drawIcon("button-chat", matrices, -16, textHeaderHeight, 32, 17);
                }

            } else if (chatData.sender == ChatDataManager.ChatSender.USER
                    && chatData.status == ChatDataManager.ChatStatus.DISPLAY) {
                // Draw Player Name
                drawEntityName(entity, matrix, immediate, fullBright, 24F + DISPLAY_PADDING, true);

                // Draw text background
                drawTextBubbleBackground("text-top-player", matrices, -64, 0, 128, scaledTextHeight,
                        playerData.friendship);

                // Draw face icon of player
                drawPlayerIcon(matrices, entity, -75, 14, 18, 18);

                // Render each line of the player's text
                drawMessageText(matrix, lines, starting_line, ending_line, immediate, lineSpacing, fullBright,
                        40.0F + DISPLAY_PADDING);
            }

        } else if (entity instanceof PlayerEntity) {
            // Scale down before rendering textures (otherwise font is huge)
            matrices.scale(-0.02F, -0.02F, 0.02F);

            boolean showPendingIcon = false;
            if (PlayerMessageManager.isChatUIOpen(entity.getUuid())) {
                showPendingIcon = true;
                scaledTextHeight += minTextHeight; // raise height of player name and icon
            } else {
                scaledTextHeight -= 15; // lower a bit more (when no pending icon is visible)
            }

            // Translate above the player
            matrices.translate(0F, -scaledTextHeight - textHeaderHeight - textFooterHeight, 0F);

            // Draw Player Name (if not self and HUD is visible)
            if (!MinecraftClient.getInstance().options.hudHidden) {
                drawEntityName(entity, matrices.peek().getPositionMatrix(), vertexConsumerProvider, fullBright,
                        24F + DISPLAY_PADDING, true);

                if (showPendingIcon) {
                    // Draw 'pending' button (when Chat UI is open)
                    drawIcon("button-dot-" + ANIMATION_FRAME, matrices, -16, textHeaderHeight, 32, 17);
                }
            }
        }

        // Calculate animation frames (0-8) every X ticks
        if (LAST_TICK != tick && tick % 5 == 0) {
            LAST_TICK = tick;
            ANIMATION_FRAME++;
        }
        if (ANIMATION_FRAME > 8) {
            ANIMATION_FRAME = 0;
        }

        // Pop the matrix to return to the original state.
        matrices.pop();
    }

    private static void drawEntityName(Entity entity, Matrix4f matrix, VertexConsumerProvider immediate,
                                       int fullBright, float yOffset, boolean truncate) {
        TextRenderer fontRenderer = MinecraftClient.getInstance().textRenderer;

        // Get Name of entity
        String nameText = "";
        if (entity instanceof MobEntity) {
            // Custom Name Tag (MobEntity)
            if (entity.getCustomName() != null) {
                nameText = entity.getCustomName().getString();
            }
        } else if (entity instanceof PlayerEntity) {
            // Player Name
            nameText = entity.getName().getString();
        }

        // Truncate long names
        if (nameText.length() > 14 && truncate) {
            nameText = nameText.substring(0, 14) + "...";
        }

        fontRenderer.draw(nameText, -fontRenderer.getWidth(nameText) / 2f, yOffset, 0xffffff,
                false, matrix, immediate, TextRenderer.TextLayerType.NORMAL, 0, fullBright);
    }
}
