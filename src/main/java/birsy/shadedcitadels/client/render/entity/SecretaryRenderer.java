package birsy.shadedcitadels.client.render.entity;

import birsy.shadedcitadels.client.render.entity.model.PlaceholderModel;
import birsy.shadedcitadels.client.render.entity.model.SecretaryModel;
import birsy.shadedcitadels.common.entity.monster.Secretary;
import birsy.shadedcitadels.core.ShadedCitadelsMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SecretaryRenderer extends MobRenderer<Secretary, SecretaryModel<Secretary>> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(ShadedCitadelsMod.MODID, "textures/entity/secretary.png");

    public SecretaryRenderer(EntityRendererProvider.Context context) {
        super(context, new SecretaryModel<>(context.bakeLayer(SecretaryModel.LAYER_LOCATION)), 0.0F);
    }

    @Override
    public void render(Secretary pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        float ticksExisted = pEntity.tickCount + pPartialTicks;
        this.model.alpha = 1.0F;//0.95F + ((Mth.sin(ticksExisted * 0.1F) + 1.0F) * 0.5F) * 0.05F;
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);

        Vec3 a = pEntity.getEyePosition(pPartialTicks);
        Vec3 b = pEntity.getViewVector(pPartialTicks).scale(2).add(a);
        Vec3 d = pEntity.getTendrilEndLocation(true);
        Vec3 c = d.add(0, d.distanceTo(a) * 0.5, 0);

        VertexConsumer lineRenderer = pBuffer.getBuffer(RenderType.LINES);
        int points = 32;
        float tendrilProgress = pEntity.getTendrilProgress(true, pPartialTicks);
        tendrilProgress *=tendrilProgress;
        for (int i = 1; i < points; i++) {
            float t1 = (float)(i - 1) / (float)points * tendrilProgress;
            Vec3 position1 = bezierCurve(a, b, c, d, t1).subtract(pEntity.getPosition(pPartialTicks));
            float t2 = (float)i / (float)points * tendrilProgress;
            Vec3 position2 = bezierCurve(a, b, c, d, t2).subtract(pEntity.getPosition(pPartialTicks));
            float br = t1 * t1;
            renderLine(pMatrixStack, lineRenderer, position1.x(), position1.y(), position1.z(), position2.x(), position2.y(), position2.z(), br, br, 0.0F, 1.0F);
        }
    }

    @Nullable
    @Override
    protected RenderType getRenderType(Secretary pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        return RenderType.entityTranslucentCull(this.getTextureLocation(pLivingEntity));
    }

    private static Vec3 bezierCurve(Vec3 a, Vec3 b, Vec3 c, Vec3 d, float t) {
        float t3 = t * t * t;
        float t2 = t * t;
        return a.scale(-t3 + 3 * t2 - 3 * t + 1).add(
               b.scale(3 * t3 - 6 * t2 + 3 * t)).add(
               c.scale(-3 * t3 + 3 * t2)).add(
               d.scale(t3));
    }
    private static Vec3 bezierDerivative(Vec3 a, Vec3 b, Vec3 c, Vec3 d, float t) {
        float t3 = 3 * t * t;
        float t2 = 2 * t;
        t = 1;
        return a.scale(-t3 + 3 * t2 - 3 * t).add(
                b.scale(3 * t3 - 6 * t2 + 3 * t)).add(
                c.scale(-3 * t3 + 3 * t2)).add(
                d.scale(t3));
    }

    public static void renderLine(PoseStack pPoseStack, VertexConsumer pConsumer, double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ, float pRed, float pGreen, float pBlue, float pAlpha) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        Matrix3f matrix3f = Matrix3f.createScaleMatrix(1.0F, 1.0F, 1.0F);
        float minX = (float)pMinX;
        float minY = (float)pMinY;
        float minZ = (float)pMinZ;
        float maxX = (float)pMaxX;
        float maxY = (float)pMaxY;
        float maxZ = (float)pMaxZ;

        pConsumer.vertex(matrix4f, minX, minY, minZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, minX, minY, minZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(Secretary entity) {
        return TEXTURE_LOCATION;
    }
}
