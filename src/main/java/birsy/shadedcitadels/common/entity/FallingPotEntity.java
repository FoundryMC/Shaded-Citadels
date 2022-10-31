package birsy.shadedcitadels.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class FallingPotEntity extends FallingBlockEntity {
    public FallingPotEntity(EntityType<? extends FallingBlockEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private FallingPotEntity(Level pLevel, double pX, double pY, double pZ, BlockState pState) {
        this(EntityType.FALLING_BLOCK, pLevel);
        this.blockState = pState;
        this.blocksBuilding = true;
        this.setPos(pX, pY, pZ);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
        this.setStartPos(this.blockPosition());
    }

    public static FallingPotEntity fall(Level pLevel, BlockPos pPos, BlockState pBlockState) {
        FallingPotEntity pot = new FallingPotEntity(pLevel,
                (double)pPos.getX() + 0.5D,
                (double)pPos.getY(),
                (double)pPos.getZ() + 0.5D,
                pBlockState.hasProperty(BlockStateProperties.WATERLOGGED) ? pBlockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)) : pBlockState);
        pLevel.setBlock(pPos, pBlockState.getFluidState().createLegacyBlock(), 3);
        pLevel.addFreshEntity(pot);
        return pot;
    }
}
