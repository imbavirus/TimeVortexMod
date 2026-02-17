package za.co.infernos.vortexmod.entities.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.entities.client.models.LaserModel;
import za.co.infernos.vortexmod.entities.client.ModModelLayers;
import za.co.infernos.vortexmod.entities.custom.LaserEntity;

public class LaserRenderer extends EntityRenderer<LaserEntity> {
    private static final ResourceLocation LASER_LOCATION = ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "textures/entity/laser.png");
    private final LaserModel<LaserEntity> model;

    public LaserRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new LaserModel<>(pContext.bakeLayer(ModModelLayers.LASER_LAYER));
    }

    public void render(LaserEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, 0.15F, 0.0F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot())));
        this.model.setupAnim(pEntity, pPartialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = pBuffer.getBuffer(this.model.renderType(LASER_LOCATION));
        this.model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(LaserEntity pEntity) {
        return LASER_LOCATION;
    }
}