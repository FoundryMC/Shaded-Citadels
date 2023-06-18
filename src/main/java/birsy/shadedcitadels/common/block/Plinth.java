package birsy.shadedcitadels.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class Plinth extends DirectionalBlock {
    private final boolean upsideDown;

    public Plinth(Properties properties, boolean upsideDown) {
        super(properties);
        this.upsideDown = upsideDown;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction facing = upsideDown ? pContext.getNearestLookingDirection().getOpposite() : pContext.getNearestLookingDirection();
        if (pContext.getPlayer().isShiftKeyDown()) facing = facing.getOpposite();

        return this.defaultBlockState().setValue(FACING, facing);
    }
}
