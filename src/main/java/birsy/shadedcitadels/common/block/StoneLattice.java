package birsy.shadedcitadels.common.block;

import birsy.shadedcitadels.core.ShadedCitadelsMod;
import birsy.shadedcitadels.core.registry.ShadedCitadelsBlocks;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class StoneLattice extends Block implements SimpleWaterloggedBlock {
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), (p_55164_) -> {
        p_55164_.put(Direction.NORTH, NORTH);
        p_55164_.put(Direction.EAST, EAST);
        p_55164_.put(Direction.SOUTH, SOUTH);
        p_55164_.put(Direction.WEST, WEST);
        p_55164_.put(Direction.UP, UP);
        p_55164_.put(Direction.DOWN, DOWN);
    }));
    private final VoxelShape[] shapeByIndex;
    private final VoxelShape latticeShape;

    public StoneLattice(Properties pProperties) {
        super(pProperties);
        this.shapeByIndex = makePlugShapes();
        this.latticeShape = makeLatticeShape();
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    private VoxelShape[] makePlugShapes() {
        float radius = 3.0F / 16.0F;
        float min = 0.5F - radius;
        float max = 0.5F + radius;
        VoxelShape[] avoxelshape = new VoxelShape[DIRECTIONS.length];

        for(int i = 0; i < DIRECTIONS.length; ++i) {
            Direction direction = DIRECTIONS[i];
            avoxelshape[i] = Shapes.box(0.5D + Math.min(-radius, direction.getStepX() * 0.5D),
                    0.5D + Math.min(-radius, direction.getStepY() * 0.5D),
                    0.5D + Math.min(-radius, direction.getStepZ() * 0.5D),
                    0.5D + Math.max(radius,  direction.getStepX() * 0.5D),
                    0.5D + Math.max(radius,  direction.getStepY() * 0.5D),
                    0.5D + Math.max(radius,  direction.getStepZ() * 0.5D));
        }
        VoxelShape[] avoxelshape1 = new VoxelShape[64];

        for(int k = 0; k < 64; ++k) {
            VoxelShape voxelshape1 = Shapes.empty();
            for(int j = 0; j < DIRECTIONS.length; ++j) {
                if ((k & 1 << j) != 0) {
                    voxelshape1 = Shapes.or(voxelshape1, avoxelshape[j]);
                }
            }
            avoxelshape1[k] = voxelshape1;
        }

        return avoxelshape1;
    }
    private VoxelShape makeLatticeShape() {
        VoxelShape shape = Shapes.empty();

        shape = Shapes.or(shape, b(0, 0, 0, 5, 16, 5));
        shape = Shapes.or(shape, b(11,0, 0, 16,16, 5));
        shape = Shapes.or(shape, b(11,0, 11,16,16, 16));
        shape = Shapes.or(shape, b(0, 0, 11,5, 16, 16));

        shape = Shapes.or(shape, b(5, 0, 0, 11,5, 5));
        shape = Shapes.or(shape, b(5, 0, 11,11,5, 16));
        shape = Shapes.or(shape, b(0, 0, 5, 5, 5, 11));
        shape = Shapes.or(shape, b(11,0, 5, 16,5, 11));

        shape = Shapes.or(shape, b(5, 11, 0, 11,16, 5));
        shape = Shapes.or(shape, b(5, 11, 11,11,16, 16));
        shape = Shapes.or(shape, b(0, 11, 5, 5, 16, 11));
        shape = Shapes.or(shape, b(11,11, 5, 16,16, 11));

        return shape;
    }

    private static VoxelShape b(double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ) {
        return Shapes.box(pMinX / 16.0, pMinY / 16.0, pMinZ / 16.0, pMaxX / 16.0, pMaxY / 16.0, pMaxZ / 16.0);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = updateBlockState(super.getStateForPlacement(pContext), pContext.getLevel(), pContext.getClickedPos());
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());

        return state.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        BlockState state = updateBlockState(pState, pLevel, pCurrentPos);

        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return state;
    }

    public int getLightBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 2;
    }

    private BlockState updateBlockState(BlockState state, LevelAccessor level, BlockPos position) {
        for (Direction direction : DIRECTIONS) {
            BlockPos pos = position.offset(direction.getNormal());
            BlockState s = level.getBlockState(pos);
            if (s.is(ShadedCitadelsBlocks.STONE_LATTICE.get())) {
                state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), false);
                continue;
            }

            boolean sturdy = s.isFaceSturdy(level, pos, direction, SupportType.FULL);
            sturdy = sturdy && s.isFaceSturdy(level, pos, direction, SupportType.CENTER);
            state = sturdy ? state.setValue(PROPERTY_BY_DIRECTION.get(direction), true) : state.setValue(PROPERTY_BY_DIRECTION.get(direction), false);
        }

        return state;
    }

    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.or(this.shapeByIndex[this.getVoxelShapeIndex(pState)], latticeShape);
    }
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.block();
    }

    private static int getVoxelShapeIndex(BlockState pState) {
        int i = 0;
        for(int j = 0; j < DIRECTIONS.length; ++j) {
            if (pState.getValue(PROPERTY_BY_DIRECTION.get(DIRECTIONS[j]))) {
                i |= 1 << j;
            }
        }
        return i;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}
