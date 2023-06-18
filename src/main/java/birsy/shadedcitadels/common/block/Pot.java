package birsy.shadedcitadels.common.block;

import birsy.shadedcitadels.core.ShadedCitadelsMod;
import birsy.shadedcitadels.core.registry.ShadedCitadelsBlocks;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Pot extends Block implements Fallable {
    public static final EnumProperty POT_SEGMENT = EnumProperty.create("pot_segment", PotSegment.class);
    public static final BooleanProperty LID = BooleanProperty.create("lid");

    private static final VoxelShape INSIDE = box(4.0D, 5.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private static final VoxelShape POT = Shapes.or(box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D), box(1.0D, 2.0D, 1.0D, 15.0D, 13.0D, 15.0D));
    private static final VoxelShape POT_OPEN = Shapes.join(POT, INSIDE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape POT_TOP = Shapes.or(box(3.0D, 0.0D, 3.0D, 13.0D, 11.0D, 13.0D), box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D));
    private static final VoxelShape POT_TOP_OPEN = Shapes.join(POT_TOP, INSIDE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape POT_MIDDLE = box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    private static final VoxelShape POT_LOWER = Shapes.or(box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D), box(1.0D, 2.0D, 1.0D, 15.0D, 16.0D, 15.0D));

    public Pot(Properties pProperties) {
        super(pProperties);
        registerDefaultState(this.getStateDefinition().any().setValue(LID, false).setValue(POT_SEGMENT, PotSegment.FULL));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        boolean lid = pState.getValue(LID);
        switch ((PotSegment)pState.getValue(POT_SEGMENT)) {
            case FULL:
                return POT;
            case TOP:
                return POT_TOP;
            case MIDDLE:
                return POT_MIDDLE;
            case BOTTOM:
                return POT_LOWER;
        }

        return POT;
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        if (pContext.getLevel().getBlockState(pContext.getClickedPos().below()).is(ShadedCitadelsBlocks.POT.get())) {
            return this.defaultBlockState().setValue(POT_SEGMENT, PotSegment.TOP);
        } else if (!pContext.getLevel().getBlockState(pContext.getClickedPos().below()).isFaceSturdy(pContext.getLevel(), pContext.getClickedPos(), Direction.UP, SupportType.CENTER)) {
            if (!pContext.getLevel().isClientSide()) {
                ShadedCitadelsMod.LOGGER.info("fall");
                pContext.getLevel().scheduleTick(pContext.getClickedPos(), ShadedCitadelsBlocks.POT.get(), 1);
            }
        }
        return this.defaultBlockState().setValue(POT_SEGMENT, PotSegment.FULL);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);
        BlockState belowState = pLevel.getBlockState(pPos.below());
        if (shouldFall(pLevel, pPos, pState, belowState)) {
            spawnFallingPot(pState, pLevel, pPos);
        }
    }

    private static boolean validPotPlacement(LevelAccessor level, BlockPos pos, BlockState state, BlockState belowBlockState, BlockState aboveBlockState) {
        switch ((PotSegment) state.getValue(POT_SEGMENT)) {
            case FULL: return belowBlockState.isFaceSturdy(level, pos, Direction.UP, SupportType.RIGID);
            case TOP: return isPot(belowBlockState);
            case MIDDLE: return isPot(belowBlockState) && isPot(aboveBlockState);
            case BOTTOM: return belowBlockState.isFaceSturdy(level, pos, Direction.UP, SupportType.RIGID) && isPot(aboveBlockState);
        }
        return true;
    }
    private static boolean shouldFall(ServerLevel level, BlockPos pos, BlockState state, BlockState belowBlockState) {
        return !belowBlockState.isFaceSturdy(level, pos, Direction.UP, SupportType.RIGID) && isBottom(state);
    }
    private static void spawnFallingPot(BlockState pState, ServerLevel pLevel, BlockPos pPos) {
        BlockPos.MutableBlockPos position = pPos.mutable();

        for(BlockState blockstate = pState; isPot(blockstate); blockstate = pLevel.getBlockState(position)) {
            FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(pLevel, position, blockstate);
            fallingblockentity.setHurtsEntities(2, 40);
            fallingblockentity.cancelDrop = true;
            if (isTop(blockstate)) {
                break;
            }

            position.move(Direction.UP);
        }

    }

    @Override
    public void onLand(Level pLevel, BlockPos pPos, BlockState pState, BlockState pReplaceableState, FallingBlockEntity pFallingBlock) {
        Fallable.super.onLand(pLevel, pPos, pState, pReplaceableState, pFallingBlock);
        pLevel.destroyBlock(pPos, true);
    }

    @Override
    public void onBrokenAfterFall(Level pLevel, BlockPos pPos, FallingBlockEntity pFallingBlock) {
        pLevel.playSound(null, pPos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
        ParticleOptions particleoptions = new BlockParticleOption(ParticleTypes.BLOCK, pFallingBlock.getBlockState());
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 4; z++) {
                    pLevel.addParticle(particleoptions, true, pPos.getX() + x, pPos.getY() + y, pPos.getZ() + z, 0, 0, 0);
                }
            }
        }
    }

    private static boolean isBottom(BlockState state) {
        return state.getValue(POT_SEGMENT) == PotSegment.FULL || state.getValue(POT_SEGMENT) == PotSegment.BOTTOM;
    }
    private static boolean isTop(BlockState state) {
        return state.getValue(POT_SEGMENT) == PotSegment.FULL || state.getValue(POT_SEGMENT) == PotSegment.TOP;
    }

    public static boolean isPot(BlockState state) {
        return state.is(ShadedCitadelsBlocks.POT.get());
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pDirection == Direction.UP) {
            if (isPot(pNeighborState)) {
                if (isPot(pLevel.getBlockState(pCurrentPos.below()))) {
                    return pState.setValue(POT_SEGMENT, PotSegment.MIDDLE);
                } else {
                    return pState.setValue(POT_SEGMENT, PotSegment.BOTTOM);
                }
            }
        }
        if (pDirection == Direction.UP || pDirection == Direction.DOWN) {
            BlockState belowState = pLevel.getBlockState(pCurrentPos.below());
            BlockState aboveState = pLevel.getBlockState(pCurrentPos.above());
            if (!validPotPlacement(pLevel, pCurrentPos, pState, belowState, aboveState)) {
                pLevel.destroyBlock(pCurrentPos, true);

                BlockPos.MutableBlockPos position = pCurrentPos.mutable();
                boolean updateAbove = pState.getValue(POT_SEGMENT) == PotSegment.BOTTOM || pState.getValue(POT_SEGMENT) == PotSegment.MIDDLE;
                if (updateAbove) {
                    for (int i = 0; i < 256; i++) {
                        position.move(Direction.UP);
                        BlockState state = pLevel.getBlockState(position);
                        if (isPot(state)) {
                            pLevel.destroyBlock(position, true);
                        } else break;
                        if (isTop(state)) break;
                    }}

                boolean updateBelow = pState.getValue(POT_SEGMENT) == PotSegment.TOP || pState.getValue(POT_SEGMENT) == PotSegment.MIDDLE;
                if (updateBelow) {
                    for (int i = 0; i < 256; i++) {
                        position.move(Direction.DOWN);
                        BlockState state = pLevel.getBlockState(position);
                        if (isPot(state)) {
                            pLevel.destroyBlock(position, true);
                        } else break;
                        if (isBottom(state)) break;
                    }}
            }
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }


    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LID, POT_SEGMENT);
    }

    public enum PotSegment implements StringRepresentable {
        FULL("full"),
        BOTTOM("bottom"),
        MIDDLE("middle"),
        TOP("top");

        private final String name;
        PotSegment(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }

}
