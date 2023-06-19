package birsy.shadedcitadels.client.render.entity.animation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class AnimFunctions {
    /**
     * Bobs a part up and down.
     *
     * @param box               the box to bob
     * @param speed             the speed of the bobbing
     * @param degree            the amount to bob
     * @param bounce            gives the bob a subtle bounce
     * @param limbSwingAmount   the amount the legs move
     * @param limbSwingSpeed    the speed the legs move
     */
    public static void bob(ModelPart box, float speed, float degree, boolean bounce, float limbSwingAmount, float limbSwingSpeed) {
        float bob = (float) (Math.sin(limbSwingAmount * speed) * limbSwingSpeed * degree - limbSwingSpeed * degree);
        if (bounce) {
            bob = (float) -Math.abs(Math.sin(limbSwingAmount * (speed * 0.5F)) * limbSwingSpeed * degree);
        }

        box.y += bob;
    }

    public static void bob(ModelPart box, float speed, float degree, boolean bounce, float offset, float limbSwingAmount, float limbSwingSpeed) {
        float bob = (float) (Math.sin(limbSwingAmount * speed + offset) * limbSwingSpeed * degree - limbSwingSpeed * degree);
        if (bounce) {
            bob = (float) -Math.abs(Math.sin(limbSwingAmount * (speed * 0.5F) + offset) * limbSwingSpeed * degree);
        }

        box.y += bob;
    }

    /**
     * Rotates the given boxes to face a target.
     *
     * @param box             the box to face the target
     * @param netHeadYaw      the yaw of the target
     * @param headPitch       the pitch of the target
     * @param yawDivisor      the amount to divide the yaw by. good to make it the amount of parts.
     * @param pitchDivisor    the amount to divide the pitch by. good to make it the amount of parts.
     */
    public static void look(ModelPart box, float netHeadYaw, float headPitch, float yawDivisor, float pitchDivisor) {
        box.yRot += (netHeadYaw * ((float)Math.PI / 180F))/yawDivisor;
        box.xRot += (headPitch * ((float)Math.PI / 180F))/pitchDivisor;
    }


    /**
     * Swings boxes. Good for walk cycles or wind effects.
     *
     * @param box              the box to swing
     * @param speed            the speed to swing this at
     * @param degree           the amount to rotate this by
     * @param invert           the animation's invertedness
     * @param offset           the offset of the swing
     * @param weight           the strength of the swing
     * @param swing            the swing rotation
     * @param limbSwingAmount  the swing amount
     * @param axis             the axis to rotate on
     */
    public static float swing(ModelPart box, float speed, float degree, boolean invert, float offset, float weight, float swing, float limbSwingAmount, Axis axis) {
        float rotation = calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
        if (box != null) {
            switch (axis) {
                case X:
                    box.xRot += rotation;
                    break;
                case Y:
                    box.yRot += rotation;
                    break;
                case Z:
                    box.zRot += rotation;
                    break;
                default:
                    box.xRot += rotation;
                    throw new RuntimeException(box + "had no axis assigned for their swing. Defaulting to X!");
            }

        }
        return rotation;
    }

    public static void swingLimbs(ModelPart left, ModelPart right, float speed, float degree, float offset, float weight, float swing, float limbSwingAmount)
    {
        swing(left, speed, degree, true, offset, weight, swing, limbSwingAmount, Axis.X);
        swing(right, speed, degree, false, offset, weight, swing, limbSwingAmount, Axis.X);
    }

    private static float calculateRotation(float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
        float rotation = (Mth.cos(f * (speed) + offset) * (degree) * f1) + (weight * f1);
        return invert ? -rotation : rotation;
    }

    public static void setOffset(ModelPart part, float xOffset, float yOffset, float zOffset) {
        part.setPos(xOffset, yOffset, zOffset);
    }

    public static void setOffsetAndRotation(ModelPart part, float xOffset, float yOffset, float zOffset, float xRotation, float yRotation, float zRotation) {
        part.setPos(xOffset, yOffset, zOffset);
        part.setRotation(xRotation, yRotation, zRotation);
    }

    //Prevents mobs animations from appearing synced on world load. Makes creatures look slightly more natural.
    public static void desyncAnimations(Entity entity, float ageInTicks) {
        ageInTicks += entity.getId();
    }

    public enum Axis {
        X,
        Y,
        Z
    }
}
