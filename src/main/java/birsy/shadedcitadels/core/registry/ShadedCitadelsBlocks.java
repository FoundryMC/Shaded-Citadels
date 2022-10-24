package birsy.shadedcitadels.core.registry;

import birsy.shadedcitadels.core.ShadedCitadelsMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ShadedCitadelsBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ShadedCitadelsMod.MODID);

    public static RegistryObject<Block> createBlock(String name, final Supplier<? extends Block> supplier, @Nullable CreativeModeTab group) {
        RegistryObject<Block> block = BLOCKS.register(name, supplier);
        if (group != null) {
            ShadedCitadelsItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(group)));
        }
        return block;
    }
}
