package birsy.shadedcitadels.client.render.entity.model;

import birsy.shadedcitadels.client.render.entity.animation.AnimFunctions;
import birsy.shadedcitadels.common.entity.monster.Secretary;
import birsy.shadedcitadels.core.ShadedCitadelsMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SecretaryModel<T extends Secretary> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(ShadedCitadelsMod.MODID, "secretary"), "main");
	private final ModelPart secretaryRoot;
	private final ModelPart secretaryBody;
	private final ModelPart secretaryHead;
	private final ModelPart secretaryRightEye;
	private final ModelPart secretaryLeftEye;
	private final ModelPart secretaryRightArm;
	private final ModelPart secretaryUpperRightArm;
	private final ModelPart secretaryLeftArm;
	private final ModelPart secretaryUpperLeftArm;
	private final ModelPart secretaryRightLeg;
	private final ModelPart secretaryLeftLeg;

	public float alpha = 1.0F;

	public SecretaryModel(ModelPart root) {
		this.secretaryRoot = root.getChild("SecretaryRoot");
		this.secretaryBody = this.secretaryRoot.getChild("SecretaryBody");
		this.secretaryHead = this.secretaryBody.getChild("SecretaryHead");
		this.secretaryRightEye = this.secretaryHead.getChild("SecretaryRightEye");
		this.secretaryLeftEye = this.secretaryHead.getChild("SecretaryLeftEye");
		this.secretaryRightArm = this.secretaryBody.getChild("SecretaryRightArm");
		this.secretaryUpperRightArm = this.secretaryRightArm.getChild("SecretaryUpperRightArm");
		this.secretaryLeftArm = this.secretaryBody.getChild("SecretaryLeftArm");
		this.secretaryUpperLeftArm = this.secretaryLeftArm.getChild("SecretaryUpperLeftArm");
		this.secretaryRightLeg = this.secretaryBody.getChild("SecretaryRightLeg");
		this.secretaryLeftLeg = this.secretaryBody.getChild("SecretaryLeftLeg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition SecretaryRoot = partdefinition.addOrReplaceChild("SecretaryRoot", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition SecretaryBody = SecretaryRoot.addOrReplaceChild("SecretaryBody", CubeListBuilder.create().texOffs(0, 29).addBox(-6.0F, -21.0F, -5.0F, 12.0F, 21.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, 0.0F));

		PartDefinition SecretaryHead = SecretaryBody.addOrReplaceChild("SecretaryHead", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -22.0F, -3.5F, 10.0F, 22.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -21.0F, 0.0F));

		PartDefinition SecretaryRightEye = SecretaryHead.addOrReplaceChild("SecretaryRightEye", CubeListBuilder.create().texOffs(34, 0).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -16.0F, -0.5F));

		PartDefinition SecretaryLeftEye = SecretaryHead.addOrReplaceChild("SecretaryLeftEye", CubeListBuilder.create().texOffs(34, 0).mirror().addBox(0.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -15.0F, -0.5F));

		PartDefinition SecretaryRightArm = SecretaryBody.addOrReplaceChild("SecretaryRightArm", CubeListBuilder.create().texOffs(22, 74).addBox(-6.0F, -2.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -15.5F, 0.0F));

		PartDefinition SecretaryUpperRightArm = SecretaryRightArm.addOrReplaceChild("SecretaryUpperRightArm", CubeListBuilder.create().texOffs(22, 60).addBox(0.0F, -10.0F, 0.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 2.0F, -2.0F));

		PartDefinition SecretaryLeftArm = SecretaryBody.addOrReplaceChild("SecretaryLeftArm", CubeListBuilder.create().texOffs(22, 74).mirror().addBox(0.0F, -2.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(6.0F, -14.5F, 0.0F));

		PartDefinition SecretaryUpperLeftArm = SecretaryLeftArm.addOrReplaceChild("SecretaryUpperLeftArm", CubeListBuilder.create().texOffs(22, 60).addBox(-4.0F, -10.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 2.0F, 0.0F));

		PartDefinition SecretaryRightLeg = SecretaryBody.addOrReplaceChild("SecretaryRightLeg", CubeListBuilder.create().texOffs(0, 60).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 0.0F, -1.0F));

		PartDefinition SecretaryLeftLeg = SecretaryBody.addOrReplaceChild("SecretaryLeftLeg", CubeListBuilder.create().texOffs(0, 60).mirror().addBox(-2.5F, 0.0F, -3.0F, 5.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.5F, 0.0F, 1.0F));

		return LayerDefinition.create(meshdefinition, 64, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.secretaryRoot.getAllParts().forEach(modelPart -> modelPart.loadPose(modelPart.getInitialPose()));

		float partialTicks = Mth.frac(ageInTicks);
		this.secretaryHead.xRot += entity.getRotation(0, partialTicks) * 0.1;
		this.secretaryHead.yRot += entity.getRotation(3, partialTicks) * 0.1;
		this.secretaryHead.zRot += entity.getRotation(7, partialTicks) * 0.1;

		this.secretaryLeftArm.yRot += entity.getRotation(1, partialTicks);
		this.secretaryLeftArm.zRot += entity.getRotation(2, partialTicks);
		this.secretaryUpperLeftArm.zRot += (entity.getRotation(4, partialTicks) + 0.7854) * 0.3;
		this.secretaryRightArm.yRot += entity.getRotation(5, partialTicks);
		this.secretaryRightArm.zRot += entity.getRotation(6, partialTicks);
		this.secretaryUpperRightArm.zRot += (entity.getRotation(8, partialTicks) - 0.7854F) * 0.3;

		this.secretaryRoot.xRot += entity.getRotation(1, partialTicks) * 0.1;
		this.secretaryRoot.yRot += entity.getRotation(4, partialTicks) * 0.1;
		this.secretaryRoot.zRot += entity.getRotation(6, partialTicks) * 0.1;

		float speed = 0.7f;
		float degree = 1.0f;
		float lS = limbSwing;
		float lSA = limbSwingAmount * 1.0F;

		float legMovementDegree = degree * 5F;
		AnimFunctions.bob(this.secretaryLeftLeg, speed, legMovementDegree * 0.2F, false, 0, lS, lSA);
		this.secretaryLeftLeg.z -= 3.0 * lSA;
		this.secretaryLeftLeg.z -= (float) (Math.cos(lS * speed + 0) * lSA * legMovementDegree - lSA * legMovementDegree);

		AnimFunctions.bob(this.secretaryRightLeg, speed, legMovementDegree * 0.2F, false, Mth.PI, lS, lSA);
		this.secretaryRightLeg.z -= 3.0 * lSA;
		this.secretaryRightLeg.z -= (float) (Math.cos(lS * speed + Mth.PI) * lSA * legMovementDegree - lSA * legMovementDegree);
		AnimFunctions.swingLimbs(this.secretaryLeftLeg, this.secretaryRightLeg, speed, degree * 1.2f, 0, 0.2F, lS, lSA);

		float bounceDegree = degree * 0.2F;
		AnimFunctions.bob(this.secretaryLeftArm, speed * 2.0F, bounceDegree, false, -1.3F, lS, lSA);
		AnimFunctions.bob(this.secretaryRightArm, speed * 2.0F, bounceDegree, false, 0, lS, lSA);
		AnimFunctions.bob(this.secretaryRoot, speed * 2.0F, bounceDegree * 8F, false, 0, lS, lSA);
		AnimFunctions.bob(this.secretaryHead, speed * 2.0F, bounceDegree * 0.5F, false, 0, lS, lSA);
		this.secretaryHead.y += 2.0 * lSA;
		AnimFunctions.bob(this.secretaryLeftEye, speed * 2.0F, bounceDegree, false, -1.3F, lS, lSA);
		AnimFunctions.bob(this.secretaryRightEye, speed * 2.0F, bounceDegree, false, 0, lS, lSA);

		AnimFunctions.swing(this.secretaryRoot, speed * 2.0F, degree * 0.05F, false, 0, 0, lS, lSA, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.secretaryLeftArm, speed * 2.0F, degree * 0.05F, false, -1, 0, lS, lSA, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.secretaryRightArm, speed * 2.0F, degree * 0.05F, false, -1, 0, lS, lSA, AnimFunctions.Axis.X);
		AnimFunctions.swing(this.secretaryHead, speed * 2.0F, degree * 0.05F, false, -1, 0, lS, lSA, AnimFunctions.Axis.X);

		AnimFunctions.look(this.secretaryHead, netHeadYaw, headPitch, 1, 1);
		AnimFunctions.look(this.secretaryLeftArm, netHeadYaw, headPitch, 1, Float.MAX_VALUE);
		AnimFunctions.look(this.secretaryRightArm, netHeadYaw, headPitch, 1, Float.MAX_VALUE);
		AnimFunctions.look(this.secretaryLeftEye, netHeadYaw, headPitch, 1, Float.MAX_VALUE);
		AnimFunctions.look(this.secretaryRightEye, netHeadYaw, headPitch, 1, Float.MAX_VALUE);
	}

	@Override
	public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
		super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.secretaryRoot;
	}
}