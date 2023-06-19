package birsy.shadedcitadels.common.entity.monster;

import birsy.shadedcitadels.core.ShadedCitadelsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SecretaryAI {
    public static class LongWanderGoal extends Goal {
        private final Secretary mob;
        private Vec3 movementVector;

        public LongWanderGoal(Secretary mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return mob.state == Secretary.InvestigationState.WANDER;
        }

        @Override
        public boolean canContinueToUse() {
            return mob.state == Secretary.InvestigationState.WANDER;
        }

        @Override
        public void start() {
            super.start();
            movementVector = new Vec3((mob.getRandom().nextFloat() * 2.0F) - 1.0F, 0, (mob.getRandom().nextFloat() * 2.0F) - 1.0F).normalize();
        }

        @Override
        public void tick() {
            super.tick();

            float wanderStrength = 0.1F;
            this.movementVector = this.movementVector.add(new Vec3((mob.getRandom().nextFloat() * 2.0F) - 1.0F, 0, (mob.getRandom().nextFloat() * 2.0F) - 1.0F).scale(wanderStrength));

            PathRayResult openVector = findFirstOpenVector(32, 1.5F);
            this.movementVector = openVector.vector;

            PathRayResult wallAvoidanceVector = findWallAvoidanceVector(16, 1.5F);
            this.movementVector = this.movementVector.lerp(wallAvoidanceVector.vector, Math.pow(wallAvoidanceVector.closeness, 3));//wallAvoidanceVector.closeness != 0 ? wallAvoidanceVector.vector : movementVector;

            movementVector = movementVector.normalize();

            jumpIfNeedBe();
            this.mob.getMoveControl().setWantedPosition(this.mob.getX() + this.movementVector.x(), this.mob.getY() + this.movementVector.y(), this.mob.getZ() + this.movementVector.z(), 1.0F);
        }

        private void jumpIfNeedBe() {
            if (!this.mob.isOnGround()) return;

            Vec3 castPosition = mob.getPosition(1.0F).add(0, mob.getStepHeight(), 0);
            HitResult jumpCheckBottom = mob.level.clip(new ClipContext(castPosition, castPosition.add(movementVector.scale(mob.getBbWidth() / 2.0F + 0.2F)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, mob));

            castPosition = mob.getPosition(1.0F).add(0, mob.getStepHeight() + 1.0F, 0);
            HitResult jumpCheckTop = mob.level.clip(new ClipContext(castPosition, castPosition.add(movementVector.scale(mob.getBbWidth() / 2.0F + 0.2F)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, mob));

            if (jumpCheckBottom.getType() == HitResult.Type.BLOCK && jumpCheckTop.getType() == HitResult.Type.MISS) {
                this.mob.jumpFromGround();
            }
        }

        // Steers away from nearby walls.
        // Returns the direction to steer towards, and a weight.
        private PathRayResult findWallAvoidanceVector(int vectorCount, float distance) {
            float angleI = (float) Math.toRadians(360.0F / vectorCount);
            Vec3 nMovementVec = movementVector.normalize();
            float startingAngle = (float) Math.atan2(nMovementVec.z, nMovementVec.x);
            Vec3 castPosition = mob.getEyePosition();

            for (int i = 0; i < vectorCount / 2; i++) {
                int sign = 1;
                for (int j = 0; j < 2; j++) {
                    float angleOffset = i * sign * angleI;
                    float angle = startingAngle + angleOffset;
                    Vec3 vector = new Vec3(Mth.cos(angle), 0, Mth.sin(angle));

                    HitResult result = mob.level.clip(new ClipContext(castPosition, castPosition.add(vector.scale(distance)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, mob));
                    float closeness = (float) Mth.clampedMap(result.getLocation().distanceTo(mob.getEyePosition()), mob.getBbWidth() / 2.0F, distance, 1.0, 0.0);
                    if (result.getType() != HitResult.Type.MISS) {
                        return new PathRayResult(vector.reverse(), closeness);
                    }

                    sign *= -1;
                }
            }
            return new PathRayResult(this.movementVector, 0.0F);

        }

        // Loops through vectors of increasing angle to the current one to find the first vector where it isn't blocked. Used to avoid walls.
        // Returns the nearest open vector, and the closeness (% of vector length) to the wall.
        private PathRayResult findFirstOpenVector(int vectorCount, float distance) {
            float angleI = (float) Math.toRadians(360.0F / vectorCount);
            Vec3 nMovementVec = movementVector.normalize();
            float startingAngle = (float) Math.atan2(nMovementVec.z, nMovementVec.x);
            // Gets a position just above the step height.
            // Used to check if this vector is not blocked but a raycast at the feet is, it should jump.
            Vec3 castPosition = mob.getEyePosition();


            PathRayResult lowestCloseness = new PathRayResult(movementVector, 1.0F);
            for (int i = 0; i < vectorCount / 2; i++) {
                int sign = 1;
                for (int j = 0; j < 2; j++) {
                    float angleOffset = i * sign * angleI;
                    float angle = startingAngle + angleOffset;
                    Vec3 vector = new Vec3(Mth.cos(angle), 0, Mth.sin(angle));

                    HitResult result = mob.level.clip(new ClipContext(castPosition, castPosition.add(vector.scale(distance)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, mob));
                    float closeness = (float)Mth.clampedMap(result.getLocation().distanceTo(mob.getEyePosition()), mob.getBbWidth() / 2.0F, distance, 1.0, 0.0);
                    if (result.getType() == HitResult.Type.MISS) {
                        return new PathRayResult(vector, closeness);
                    } else if (closeness < lowestCloseness.closeness) {
                        lowestCloseness = new PathRayResult(vector, closeness);
                    }

                    sign *= -1;
                }
            }

            return lowestCloseness;
        }

        private record PathRayResult(Vec3 vector, float closeness) {}
    }

    public static class InvestigateGoal extends Goal {
        private final Secretary mob;
        private BlockPos investigationPos;
        private boolean recognizedTarget = false;
        private int checks = 0;
        private int ticksFromSearchStart = 0;
        public InvestigateGoal(Secretary mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return mob.state == Secretary.InvestigationState.INVESTIGATE;
        }

        @Override
        public void start() {
            super.start();
            investigationPos = mob.investigateTarget;
            this.mob.getNavigation().stop();
            this.checks = 0;
            this.recognizedTarget = false;
            mob.getNavigation().moveTo(this.investigationPos.getX(), this.investigationPos.getY(), this.investigationPos.getZ(), 1.0F);
        }

        @Override
        public void tick() {
            super.tick();
            ticksFromSearchStart++;

            if (this.mob.getNavigation().isDone() && ticksFromSearchStart > 40) {
                checks++;
                this.mob.getNavigation().moveTo(this.investigationPos.getX(), this.investigationPos.getY(), this.investigationPos.getZ(), 1.0F);
            }

            if (this.mob.investigateTarget != this.investigationPos) {
                investigationPos = mob.investigateTarget;
                this.mob.getNavigation().stop();
                mob.getNavigation().moveTo(this.investigationPos.getX(), this.investigationPos.getY(), this.investigationPos.getZ(), 1.0F);
                checks = 0;
                ticksFromSearchStart = 0;
                return;
            }

            boolean recogTarget = this.canRecognizeTarget();
            if (recogTarget) {
                this.recognizedTarget = true;
                this.mob.preCheckedPositions.put(this.investigationPos, 200);
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (recognizedTarget || this.mob.state != Secretary.InvestigationState.INVESTIGATE || this.checks > 3) {
                ShadedCitadelsMod.LOGGER.info("recognized target: " + recognizedTarget + "\nmob.state: " + mob.state.toString() + "\nchecks: " + checks);
                return false;
            }
            return super.canContinueToUse();
        }

        @Override
        public void stop() {
            ShadedCitadelsMod.LOGGER.info("Finished investigating " + this.investigationPos);
            this.mob.state = Secretary.InvestigationState.WANDER;
            this.mob.getNavigation().stop();
            this.checks = 0;
            this.recognizedTarget = false;

            this.mob.getEntityData().set(Secretary.EXCITEMENT, Math.max(this.mob.getEntityData().get(Secretary.EXCITEMENT) - 0.5F, 0));

            super.stop();
        }

        public boolean canRecognizeTarget() {
            float horizontalDistance = Mth.sqrt((float) ((investigationPos.getX() - mob.getX()) * (investigationPos.getX() - mob.getX()) + (investigationPos.getZ() - mob.getZ()) * (investigationPos.getZ() - mob.getZ())));
            if (horizontalDistance < 3 && Math.abs(investigationPos.getY() - mob.getEyePosition().y()) < 5) {
                Vec3 investTarget = new Vec3(this.investigationPos.getX(), this.investigationPos.getY(), this.investigationPos.getZ());

                BlockHitResult canSeeTarget = mob.level.clip(new ClipContext(mob.getEyePosition(), investTarget.add(0.5, 0.5, 0.5), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));

                if (canSeeTarget.getType() == HitResult.Type.MISS || (canSeeTarget.getType() != HitResult.Type.MISS && Math.sqrt(canSeeTarget.getBlockPos().distSqr(this.investigationPos)) < 1.5)) {
                    return true;
                }
            }
            return false;
        }
    }

}
