package za.co.infernos.vortexmod.mixins.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import za.co.infernos.vortexmod.clientutil.SkyboxUtil;
import za.co.infernos.vortexmod.worldgen.dimension.ModDimensions;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A mixin for rendering a custom skybox
 *
 * @author duzo
 */
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject(method="renderSky", at = @At("HEAD"), cancellable = true)
    public void renderSky(Matrix4f pProjectionMatrix, Matrix4f pPoseMatrix, float pPartialTick, Camera pCamera, boolean pIsFoggy, Runnable pSkyFogSetup, CallbackInfo ci) {
        ClientLevel world = Minecraft.getInstance().level;
        if(world == null) return;

        // If in TARDIS dimension, render the custom TARDIS sky :)
        if(ModDimensions.tardisDIM_LEVEL_KEY.equals(world.dimension())) {
            // Create a PoseStack from the pose matrix for compatibility with existing rendering code
            PoseStack poseStack = new PoseStack();
            poseStack.last().pose().set(pPoseMatrix);
            SkyboxUtil.renderTardisSky(poseStack);
            ci.cancel();
        }
    }
}