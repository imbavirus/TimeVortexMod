package za.co.infernos.vortexmod.entities.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.entities.client.ModModelLayers;
import za.co.infernos.vortexmod.entities.client.models.AngelModel;
import za.co.infernos.vortexmod.entities.client.models.TardisModel;
import za.co.infernos.vortexmod.entities.custom.AngelEntity;
import za.co.infernos.vortexmod.entities.custom.TardisEntity;
import za.co.infernos.vortexmod.network.PacketHandler;
import za.co.infernos.vortexmod.network.ServerboundAngelSeenPacket;

public class AngelRenderer extends MobRenderer<AngelEntity, AngelModel<AngelEntity>> {
    public AngelRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new AngelModel<>(pContext.bakeLayer(ModModelLayers.ANGEL_LAYER)), 0.5f);
    }

    @Override
    public void render(AngelEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
//        if (!Minecraft.getInstance().gameMode.getPlayerMode().isCreative()) {
//            PacketHandler.sendToServer(new ServerboundAngelSeenPacket(pEntity.getUUID(), pEntity.level().dimension().location().getPath()));
//        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AngelEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "textures/entity/angel_texture.png");
    }
}
