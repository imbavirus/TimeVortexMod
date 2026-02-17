package za.co.infernos.vortexmod.entities.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.entities.client.models.DalekModel;
import za.co.infernos.vortexmod.entities.client.ModModelLayers;
import za.co.infernos.vortexmod.entities.custom.DalekEntity;
public class DalekRenderer extends MobRenderer<DalekEntity, DalekModel<DalekEntity>> {
    public DalekRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new DalekModel<>(pContext.bakeLayer(ModModelLayers.DALEK_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(DalekEntity pEntity) {

        switch (pEntity.dalekType) {
            case GOLD_DALEK -> {
                return ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "textures/entity/gold_dalek.png");
            }
            case SILVER_DALEK -> {
                return ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "textures/entity/silver_dalek.png");
            }
            case BLACK_DALEK -> {
                return ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "textures/entity/black_dalek.png");
            }
            case SILVER_BLACK_DALEK -> {
                return ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "textures/entity/silver_black_dalek.png");
            }
        }
        return ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "textures/entity/gold_dalek.png");
    }

    @Override
    public void render(DalekEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
                       MultiBufferSource pBuffer, int pPackedLight) {

        pMatrixStack.scale(0.7f,0.7f,0.7f);

        if(pEntity.isBaby()) {
            pMatrixStack.scale(0.3f, 0.3f, 0.3f);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}