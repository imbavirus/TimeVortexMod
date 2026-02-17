package za.co.infernos.vortexmod.clientutil.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderUtil {
    public static void drawLine(MultiBufferSource pBuffer, PoseStack pPoseStack, Vector3f startPoint, Vector3f endPoint, int width, int alpha, int r, int g, int b) {
        VertexConsumer vertexBuilder = pBuffer.getBuffer(ModRenderType.getLineOfWidth(width));
        Matrix4f positionMatrix = pPoseStack.last().pose();

        vertexBuilder.addVertex(positionMatrix, startPoint.x(), startPoint.y(), startPoint.z())
                .setColor(r, g, b, alpha)
                .setNormal(1, 0, 0); // Adjusted normal for clarity

        vertexBuilder.addVertex(positionMatrix, endPoint.x(), endPoint.y(), endPoint.z())
                .setColor(r, g, b, alpha)
                .setNormal(1, 0, 0); // Adjusted normal for clarity
    }
}
