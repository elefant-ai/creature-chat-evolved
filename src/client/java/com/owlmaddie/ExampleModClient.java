package com.owlmaddie;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;

import net.minecraft.util.math.RotationCalculator;
import org.joml.Matrix3f;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import net.minecraft.world.World;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import java.util.List;
import org.joml.Matrix4f;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import com.google.gson.Gson;


public class ExampleModClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mobgpt");
    private static String funnyGreeting = "Greetings!";  // Default greeting. This will be overwritten by ChatGPT response.

    private static final Identifier PIG = new Identifier("mobgpt", "textures/pig.png");
    private static final Identifier COW = new Identifier("mobgpt", "textures/cow.png");
    private static final Identifier WOLF = new Identifier("mobgpt", "textures/wolf.png");
    private static final Identifier CHICKEN = new Identifier("mobgpt", "textures/chicken.png");
    private static final Identifier ARROW1 = new Identifier("mobgpt", "textures/arrow1.png");
    private static final Identifier ARROW2 = new Identifier("mobgpt", "textures/arrow2.png");
    private static final Identifier TEXT_TOP = new Identifier("mobgpt", "textures/text-top.png");
    private static final Identifier TEXT_MIDDLE = new Identifier("mobgpt", "textures/text-middle.png");
    private static final Identifier TEXT_BOTTOM = new Identifier("mobgpt", "textures/text-bottom.png");
    private static final Identifier KEYBOARD = new Identifier("mobgpt", "textures/keyboard.png");
    private static final Identifier DOTDOT = new Identifier("mobgpt", "textures/dotdot.png");

	@Override
    public void onInitializeClient() {
        ClientEventHandlers.register();
        fetchGreetingFromChatGPT();

        WorldRenderEvents.BEFORE_ENTITIES.register((context) -> {
            drawTextAboveEntities(context);
        });
    }

    public class ClientEventHandlers {
        public static void register() {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {

                // Check if the right-click action is being pressed
                if (client.options.useKey.wasPressed()) {
                    LOGGER.info("RIGHT CLICK DETECTED");

                    // Get Nearby Entities
                    Camera camera = client.gameRenderer.getCamera();
                    Entity cameraEntity = camera.getFocusedEntity();
                    if (cameraEntity == null) return;

                    World world = cameraEntity.getEntityWorld();
                    double renderDistance = 7.0;

                    // Calculate radius of entities
                    Vec3d pos = cameraEntity.getPos();
                    Box area = new Box(pos.x - renderDistance, pos.y - renderDistance, pos.z - renderDistance,
                            pos.x + renderDistance, pos.y + renderDistance, pos.z + renderDistance);

                    // Get all entities
                    List<Entity> nearbyEntities = world.getOtherEntities(null, area);

                    // Filter out living entities
                    List<LivingEntity> nearbyCreatures = nearbyEntities.stream()
                            .filter(entity -> entity instanceof LivingEntity)
                            .map(entity -> (LivingEntity) entity)
                            .collect(Collectors.toList());

                    // Get the player from the client
                    ClientPlayerEntity player = client.player;

                    // Define the start and end points of the raycast based on the player's view
                    Vec3d startRay = player.getEyePos();
                    Vec3d endRay = startRay.add(player.getRotationVector().multiply(5));  // 5 blocks in the direction player is looking

                    // Iterate through the entities with icons to check for hits
                    for (Entity entity : nearbyCreatures) {
                        LOGGER.info("CHECK FOR CLICK ON ENTITY");
                        Vec3d iconCenter = entity.getPos().add(0, entity.getHeight() + 0.5, 0);
                        Box iconBox = new Box(
                                iconCenter.add(-0.25, -0.25, -0.25),
                                iconCenter.add(0.25, 0.25, 0.25)
                        );

                        if (iconBox.raycast(startRay, endRay).isPresent()) {
                            // Handle icon click, for instance, by sending a packet to the server
                            //CustomPacketHandler.sendEntityClickPacket(player, entity.getEntityId());
                            LOGGER.info("CLICKED ON ICON");
                            break; // Exit loop if an icon was clicked to avoid multi-hits
                        }
                    }
                }
            });
        }
    }

    
    public void fetchGreetingFromChatGPT() {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL("https://api.openai.com/v1/chat/completions");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer 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");
                connection.setDoOutput(true);

                String jsonInputString = "{"
                        + "\"model\": \"gpt-3.5-turbo\","
                        + "\"messages\": ["
                        + "{ \"role\": \"system\", \"content\": \"You are a silly Minecraft entity who speaks to the player in short riddles.\" },"
                        + "{ \"role\": \"user\", \"content\": \"Hello!\" }"
                        + "]"
                        + "}";
                LOGGER.info(jsonInputString);

                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);           
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                Gson gson = new Gson();
                ChatGPTResponse chatGPTResponse = gson.fromJson(response.toString(), ChatGPTResponse.class);
                if(chatGPTResponse != null && chatGPTResponse.choices != null && !chatGPTResponse.choices.isEmpty()) {
                    // Save the greeting globally
                    LOGGER.info(chatGPTResponse.choices.get(0).message.content.replace("\n", " "));
                    funnyGreeting = chatGPTResponse.choices.get(0).message.content.replace("\n", " ");
                }

            } catch (Exception e) {
                LOGGER.error("Failed to fetch greeting from ChatGPT", e);
            }
        });
        thread.start();
    }

    public void drawTextBubbleBackground(MatrixStack matrices, Entity entity, float x, float y, float width, float height) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float z = 0.01F;

        // Draw UI text background
        RenderSystem.setShaderTexture(0, TEXT_TOP);
        drawTexturePart(matrices, buffer, x, y, z, width, 40);

        RenderSystem.setShaderTexture(0, TEXT_MIDDLE);
        drawTexturePart(matrices, buffer, x, y + 40, z, width, height);

        RenderSystem.setShaderTexture(0, TEXT_BOTTOM);
        drawTexturePart(matrices, buffer, x, y + 40 + height, z, width, 5);


        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
    }

    private void drawTexturePart(MatrixStack matrices, BufferBuilder buffer, float x, float y, float z, float width, float height) {
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrices.peek().getPositionMatrix(), x, y + height, z).texture(0, 1).next();  // bottom left
        buffer.vertex(matrices.peek().getPositionMatrix(), x + width, y + height, z).texture(1, 1).next();   // bottom right
        buffer.vertex(matrices.peek().getPositionMatrix(), x + width, y, z).texture(1, 0).next();  // top right
        buffer.vertex(matrices.peek().getPositionMatrix(), x, y, z).texture(0, 0).next(); // top left
        Tessellator.getInstance().draw();
    }

    private void drawEntityIcon(MatrixStack matrices, Entity entity, float x, float y, float width, float height) {
        // Draw face icon
        switch (entity.getType().getUntranslatedName().toLowerCase(Locale.ROOT)) {
            case "pig":
                RenderSystem.setShaderTexture(0, PIG);
                break;
            case "chicken":
                RenderSystem.setShaderTexture(0, CHICKEN);
                break;
            case "wolf":
                RenderSystem.setShaderTexture(0, WOLF);
                break;
            case "cow":
                RenderSystem.setShaderTexture(0, COW);
                break;
        }
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        float z = -0.01F;
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), x, y + height, z).texture(0, 1).next();  // bottom left
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), x + width, y + height, z).texture(1, 1).next();   // bottom right
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), x + width, y, z).texture(1, 0).next();  // top right
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), x, y, z).texture(0, 0).next(); // top left
        tessellator.draw();

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
    }


    private void drawTextAboveEntities(WorldRenderContext context) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(PIG);
        MinecraftClient.getInstance().getTextureManager().bindTexture(CHICKEN);
        MinecraftClient.getInstance().getTextureManager().bindTexture(WOLF);
        MinecraftClient.getInstance().getTextureManager().bindTexture(COW);
        MinecraftClient.getInstance().getTextureManager().bindTexture(ARROW1);
        MinecraftClient.getInstance().getTextureManager().bindTexture(ARROW2);


        Camera camera = context.camera();
        Entity cameraEntity = camera.getFocusedEntity();
        if (cameraEntity == null) return;
        
        World world = cameraEntity.getEntityWorld();
        double renderDistance = 7.0;
        TextRenderer fontRenderer = MinecraftClient.getInstance().textRenderer;

        // Calculate radius of entities
        Vec3d pos = cameraEntity.getPos();
        Box area = new Box(pos.x - renderDistance, pos.y - renderDistance, pos.z - renderDistance,
                           pos.x + renderDistance, pos.y + renderDistance, pos.z + renderDistance);
        
        // Get all entities
        List<Entity> nearbyEntities = world.getOtherEntities(null, area);

        // Filter out living entities
        List<LivingEntity> nearbyCreatures = nearbyEntities.stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .collect(Collectors.toList());

        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider immediate = context.consumers();

        for (Entity entity : nearbyCreatures) {
            if (entity.getType() == EntityType.PLAYER) {
                // Skip Player
                continue;
            }
            
            String baseText = funnyGreeting + " - " + entity.getType().getName().getString();
            List<OrderedText> lines = fontRenderer.wrapLines(StringVisitable.plain(baseText), 20 * fontRenderer.getWidth("W"));

            // Push a new matrix onto the stack.
            matrices.push();

            // Translate to the entity's position.
            matrices.translate(entity.getPos().x - cameraEntity.getPos().x,
                               entity.getPos().y - cameraEntity.getPos().y + (entity.getHeight() - 1.0F),
                               entity.getPos().z - cameraEntity.getPos().z);



            // Calculate the difference vector (from entity to camera)
            Vec3d difference = cameraEntity.getPos().subtract(entity.getPos());

            // Calculate the yaw angle (just like before)
            float yaw = -((float) Math.atan2(difference.z, difference.x) + (float) Math.PI / 2F);

            // Calculate the pitch difference (using y component)
            float pitch = (float) Math.atan2(difference.y, Math.sqrt(difference.x * difference.x + difference.z * difference.z));

            // Clamp the pitch to the desired range (in this case, ±X degrees converted to radians)
            pitch = (float) MathHelper.clamp(pitch, -Math.toRadians(20), Math.toRadians(20));

            // Convert yaw and pitch to Quaternionf
            float halfYaw = yaw * 0.5f;
            float sinHalfYaw = MathHelper.sin(halfYaw);
            float cosHalfYaw = MathHelper.cos(halfYaw);

            float halfPitch = pitch * 0.5f;
            float sinHalfPitch = MathHelper.sin(halfPitch);
            float cosHalfPitch = MathHelper.cos(halfPitch);

            // Constructing the Quaternionf for yaw (around Y axis) and pitch (around X axis)
            Quaternionf yawRotation = new Quaternionf(0, sinHalfYaw, 0, cosHalfYaw);
            Quaternionf pitchRotation = new Quaternionf(sinHalfPitch, 0, 0, cosHalfPitch);

            // Combine the rotations
            yawRotation.mul(pitchRotation);

            // Now when you want to render, apply the combined rotation:
            matrices.multiply(yawRotation);


            // Rotate the label to always face the player.
            //matrices.multiply(camera.getRotation());

            // Determine max line length
            int maxLineLength = 0;
            float lineSpacing = 1F;
            float textHeaderHeight = 40F;
            float textFooterHeight = 5F;
            for (OrderedText lineText : lines) {
                int lineLength = fontRenderer.getWidth(lineText);
                if (lineLength > maxLineLength) {
                    maxLineLength = lineLength;
                }
            }

            // Calculate size of text scaled to world
            float scaledTextHeight = (float) lines.size() * (fontRenderer.fontHeight + lineSpacing);
            scaledTextHeight = Math.max(scaledTextHeight, 50F);

            // Scale down before rendering textures (otherwise font is huge)
            matrices.scale(-0.02F, -0.02F, 0.02F);

            // Translate above the entity
            matrices.translate(0F, -scaledTextHeight + -textHeaderHeight + -textFooterHeight, 0F);

            // Draw text background (no smaller than 50F tall)
            drawTextBubbleBackground(matrices, entity, -64, 0, 128, scaledTextHeight);

            // Draw face of entity
            drawEntityIcon(matrices, entity, -60, 7, 32, 32);


            // Render each line of the text
            int fullBright = 0xF000F0;
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            float yOffset = 42.0F;
            for (OrderedText lineText : lines) {
                fontRenderer.draw(lineText, -fontRenderer.getWidth(lineText) / 2f, yOffset, 0xffffff,
                        false, matrix, immediate, TextLayerType.NORMAL, 0, fullBright);
                yOffset += fontRenderer.fontHeight + lineSpacing;
            }

            // Pop the matrix to return to the original state.
            matrices.pop();
        }
    }
    
}
