package birsy.shadedcitadels.common.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PotBlockEntity extends BlockEntity implements Container {
    private NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);

    protected PotBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public int getContainerSize() {
        return this.getItems().size();
    }

    public boolean isEmpty() {
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.getItems().get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), pIndex, pCount);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    public ItemStack removeItemNoUpdate(int pIndex) {
        return ContainerHelper.takeItem(this.getItems(), pIndex);
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        this.getItems().set(pIndex, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 8;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
        this.getItems().clear();
    }
}
