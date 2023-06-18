package birsy.shadedcitadels.common.entity.monster;

import birsy.shadedcitadels.core.ShadedCitadelsMod;
import com.mojang.serialization.Dynamic;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class Secretary extends Monster implements VibrationListener.VibrationListenerConfig {
    private final DynamicGameEventListener<VibrationListener> dynamicGameEventListener;
    public InvestigationState state = InvestigationState.WANDER;
    protected BlockPos investigateTarget = BlockPos.ZERO;
    // If it's already checked a position recently, it'll ignore new sounds coming from there for a short while.
    public Map<BlockPos, Integer> preCheckedPositions = new HashMap<>();
    private int vibrationCooldown = 0;

    public List<Pair<Vec3, Integer>> suspectedPlayerPositions = new ArrayList<>();

    private static final EntityDataAccessor<BlockPos> TENDRIL_END_LEFT = SynchedEntityData.defineId(Secretary.class, EntityDataSerializers.BLOCK_POS);
    private float previousTendrilLeftProgress = 0.0F;
    boolean retractingLeftTendril = true;
    private static final EntityDataAccessor<Float> TENDRIL_LEFT_PROGRESS = SynchedEntityData.defineId(Secretary.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<BlockPos> TENDRIL_END_RIGHT = SynchedEntityData.defineId(Secretary.class, EntityDataSerializers.BLOCK_POS);
    private float previousTendrilRightProgress = 0.0F;
    boolean retractingRightTendril = true;
    private static final EntityDataAccessor<Float> TENDRIL_RIGHT_PROGRESS = SynchedEntityData.defineId(Secretary.class, EntityDataSerializers.FLOAT);

    public Secretary(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPersistenceRequired();
        this.setCustomNameVisible(true);
        this.dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationListener(new EntityPositionSource(this, this.getEyeHeight()), 32, this, null, 0.0F, 0));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(TENDRIL_END_LEFT, BlockPos.ZERO);
        this.getEntityData().define(TENDRIL_LEFT_PROGRESS, 0.0F);
        this.getEntityData().define(TENDRIL_END_RIGHT, BlockPos.ZERO);
        this.getEntityData().define(TENDRIL_RIGHT_PROGRESS, 0.0F);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        VibrationListener.codec(this).encodeStart(NbtOps.INSTANCE, this.dynamicGameEventListener.getListener()).resultOrPartial(ShadedCitadelsMod.LOGGER::error).ifPresent((p_219418_) -> {
            pCompound.put("listener", p_219418_);
        });
        pCompound.putInt("investigationState", state.ordinal());
        pCompound.putIntArray("investigationTarget", new int[]{(int) investigateTarget.getX(), (int) investigateTarget.getY(), (int) investigateTarget.getZ()});
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("listener", 10)) {
            VibrationListener.codec(this).parse(new Dynamic<>(NbtOps.INSTANCE, pCompound.getCompound("listener"))).resultOrPartial(ShadedCitadelsMod.LOGGER::error).ifPresent((p_219408_) -> {
                this.dynamicGameEventListener.updateListener(p_219408_, this.level);
            });
        }
        this.state = InvestigationState.values()[pCompound.getInt("investigationState")];
        int[] investigateTargetArray = pCompound.getIntArray("investigationTarget");
        if (investigateTargetArray.length > 0) {
            this.investigateTarget = new BlockPos(investigateTargetArray[0], investigateTargetArray[1], investigateTargetArray[2]);
        }
    }

    protected void registerGoals() {
        //this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new SecretaryAI.InvestigateGoal(this));
        this.goalSelector.addGoal(4, new SecretaryAI.LongWanderGoal(this));
        //this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        //this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }


    public Vec3 getTendrilEndLocation(boolean side) {
        BlockPos endLocation = this.getEntityData().get(side ? TENDRIL_END_LEFT : TENDRIL_END_RIGHT);
        return new Vec3(endLocation.getX(), endLocation.getY(), endLocation.getZ()).add(0.5, 0.5, 0.5);
    }

    public float getTendrilProgress(boolean side, float partialTicks) {
        float tendrilProgress = this.getEntityData().get(side ? TENDRIL_LEFT_PROGRESS : TENDRIL_RIGHT_PROGRESS);
        float pTendrilProgress = side ? previousTendrilLeftProgress : previousTendrilRightProgress;
        boolean retractingTentacle = side ? retractingLeftTendril : retractingRightTendril;
        return Mth.clamp(Mth.lerp(partialTicks, retractingTentacle ? pTendrilProgress : tendrilProgress, retractingTentacle ? tendrilProgress : pTendrilProgress), 0.0F, 1.0F);
    }

    public void updateTendrilProgress() {
        this.previousTendrilLeftProgress = this.entityData.get(TENDRIL_LEFT_PROGRESS);
        this.previousTendrilRightProgress = this.entityData.get(TENDRIL_RIGHT_PROGRESS);
        this.entityData.set(TENDRIL_LEFT_PROGRESS,  this.previousTendrilLeftProgress + (retractingLeftTendril ? -0.1F : 0.2F));
        this.entityData.set(TENDRIL_RIGHT_PROGRESS, this.previousTendrilRightProgress + (retractingRightTendril ? -0.2F : 0.2F));
        if (this.entityData.get(TENDRIL_LEFT_PROGRESS) >= 1.2F) {
            retractingLeftTendril = true;
        }
        if (this.entityData.get(TENDRIL_RIGHT_PROGRESS) >= 1.2F) {
            retractingRightTendril = true;
        }
    }



    @Override
    public void tick() {
        super.tick();
        updateTendrilProgress();
        if (level instanceof ServerLevel serverlevel) {
            this.dynamicGameEventListener.getListener().tick(serverlevel);

            vibrationCooldown--;
            preCheckedPositions.forEach((pos, time) -> {
                time--;
                if (time < 0) {
                    preCheckedPositions.remove(pos);
                }
            });

            this.setCustomNameVisible(true);
            Component component = null;
            float count = ((this.tickCount % 40.0F) / 40.0F) * 3.0F;
           // ShadedCitadelsMod.LOGGER.info("" + count);
            switch (this.state) {
                case WANDER -> component = Component.literal("Wandering").withStyle(ChatFormatting.GREEN);
                case INVESTIGATE -> component = Component.literal(count < 1 ? "Investigating." : count < 2 ? "Investigating.." : "Investigating...").withStyle(ChatFormatting.YELLOW);
                case SEARCH -> component = Component.literal(count < 1 ? "Searching." : count < 2 ? "Searching.." : "Searching...").withStyle(ChatFormatting.GOLD);
                case ATTACK -> component = Component.literal("Attacking!").withStyle(ChatFormatting.DARK_RED);
            }
            this.setCustomName(component);
        }
    }

    public void jumpFromGround() {
        super.jumpFromGround();
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> pListenerConsumer) {
        Level level = this.level;
        if (level instanceof ServerLevel serverlevel) {
            pListenerConsumer.accept(this.dynamicGameEventListener, serverlevel);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public boolean shouldListen(ServerLevel pLevel, GameEventListener pListener, BlockPos pPos, GameEvent pGameEvent, GameEvent.Context pContext) {
        return !this.isNoAi() && !this.isDeadOrDying() && pLevel.getWorldBorder().isWithinBounds(pPos) && !this.isRemoved() && this.level == pLevel && !(pContext.sourceEntity() instanceof Secretary) && vibrationCooldown <= 0;
    }

    @Override
    public void onSignalReceive(ServerLevel pLevel, GameEventListener pListener, BlockPos pSourcePos, GameEvent pGameEvent, @Nullable Entity pSourceEntity, @Nullable Entity pProjectileOwner, float pDistance) {
        pLevel.broadcastEntityEvent(this, (byte)61);
        this.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 5.0F, this.getVoicePitch());
        AtomicBoolean hasCheckedSignal = new AtomicBoolean(false);
        preCheckedPositions.forEach((pos, timer) -> {
            if (Mth.sqrt((float) pSourcePos.distSqr(pos)) < 3.0F) {
                hasCheckedSignal.set(true);
            }
        });
        if (!hasCheckedSignal.get()) {
            this.investigateTarget = pSourcePos;
            this.state = InvestigationState.INVESTIGATE;
            ShadedCitadelsMod.LOGGER.info("Investigating " + pSourcePos);
        }

        this.entityData.set(TENDRIL_LEFT_PROGRESS, 0.0F);
        this.entityData.set(TENDRIL_RIGHT_PROGRESS, 0.0F);
        this.getEntityData().set(TENDRIL_END_LEFT, pSourcePos);
        this.getEntityData().set(TENDRIL_END_RIGHT, pSourcePos);
        retractingLeftTendril = false;
        retractingRightTendril = false;

        if (Math.sqrt(pSourcePos.distSqr(this.blockPosition())) < 5) {
            if (pSourceEntity instanceof LivingEntity || pProjectileOwner instanceof LivingEntity) {
                if (pProjectileOwner != null) {
                    suspectedPlayerPositions.add(Pair.of(pProjectileOwner.getPosition(1.0F), 0));
                } else {
                    suspectedPlayerPositions.add(Pair.of(pSourceEntity.getPosition(1.0F), 0));
                }
            }
        }
        this.vibrationCooldown = 20;
    }

    protected enum InvestigationState {
        WANDER, INVESTIGATE, SEARCH, ATTACK;
    }
}
