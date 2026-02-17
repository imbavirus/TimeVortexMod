package za.co.infernos.vortexmod.block.entity.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import za.co.infernos.vortexmod.block.custom.BiometricBlock;
import za.co.infernos.vortexmod.block.custom.FaceAttachedHorizontalDirectionalBlockEntity;
import za.co.infernos.vortexmod.block.entity.BiometricBlockEntity;
import org.joml.Matrix4f;

import java.util.*;

public class BioscannerBlockEntityRenderer implements BlockEntityRenderer<BiometricBlockEntity> {
    private static GameProfile displayProfile;
    private static int last_tick = 0;

    public BioscannerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BiometricBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState pState = blockEntity.getBlockState();
        poseStack.pushPose();

        if (pState.getValue(BiometricBlock.FACE) == AttachFace.FLOOR) {
            poseStack.translate(0.5f, 0.126f, 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            if (pState.getValue(BiometricBlock.FACING) == Direction.NORTH) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            } else if (pState.getValue(BiometricBlock.FACING) == Direction.EAST) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            } else if (pState.getValue(BiometricBlock.FACING) == Direction.WEST) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
            }
        } else if (pState.getValue(BiometricBlock.FACE) == AttachFace.CEILING) {
            poseStack.translate(0.5f, 0.874f, 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            if (pState.getValue(BiometricBlock.FACING) == Direction.NORTH) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            } else if (pState.getValue(BiometricBlock.FACING) == Direction.EAST) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
            } else if (pState.getValue(BiometricBlock.FACING) == Direction.WEST) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            }
        } else if (pState.getValue(BiometricBlock.FACE) == AttachFace.WALL) {
            poseStack.translate(0.5f, 0.5f, 0.5f);
            if (pState.getValue(BiometricBlock.FACING) == Direction.NORTH) {
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.translate(0, 0, -0.374f);
            } else if (pState.getValue(BiometricBlock.FACING) == Direction.EAST) {
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.translate(0, 0, -0.374f);
            } else if (pState.getValue(BiometricBlock.FACING) == Direction.SOUTH) {
                poseStack.translate(0, 0, -0.374f);
            } else if (pState.getValue(BiometricBlock.FACING) == Direction.WEST) {
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.translate(0, 0, -0.374f);
            }
        }

        Random random = new Random();

        int tickspeed = Minecraft.getInstance().level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING) * 8;
        if (Minecraft.getInstance().level.getGameTime() > last_tick || Minecraft.getInstance().level.getGameTime() < last_tick) {
            if (displayProfile == null || Minecraft.getInstance().level.getGameTime() % tickspeed == 0) {
                Collection<PlayerInfo> playerInfos = Minecraft.getInstance().getConnection().getOnlinePlayers();
                List<GameProfile> gameProfiles = new ArrayList<>();

                for (PlayerInfo playerInfo : playerInfos) {
                    gameProfiles.add(playerInfo.getProfile());
                }
                displayProfile = gameProfiles.get(random.nextInt(gameProfiles.size()));
            }
        }
        ResourceLocation texture = Minecraft.getInstance().getSkinManager().getInsecureSkin(displayProfile).texture();

        RenderSystem.setShaderTexture(0, texture); // Bind the texture
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Assuming you want to render a simple quad
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entitySolid(texture));
        Matrix4f pose = poseStack.last().pose();
        float minU = 0.125f; // Face texture UV coordinates
        float maxU = 0.25f;
        float minV = 0.125f;
        float maxV = 0.25f;

        int lightU = packedLight & 0xFFFF;
        int lightV = (packedLight >> 16) & 0xFFFF;

        vertexConsumer.addVertex(pose, -0.225f, -0.225f, 0.0f).setColor(255, 255, 255, 50).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(lightU, lightV).setNormal(1, 0, 0);
        vertexConsumer.addVertex(pose, 0.225f, -0.225f, 0.0f).setColor(255, 255, 255, 50).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(lightU, lightV).setNormal(1, 0, 0);
        vertexConsumer.addVertex(pose, 0.225f, 0.225f, 0.0f).setColor(255, 255, 255, 50).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(lightU, lightV).setNormal(1, 0, 0);
        vertexConsumer.addVertex(pose, -0.225f, 0.225f, 0.0f).setColor(255, 255, 255, 50).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(lightU, lightV).setNormal(1, 0, 0);


        RenderSystem.disableBlend();
        poseStack.popPose();
        last_tick = (int) Minecraft.getInstance().level.getGameTime();
    }
}
