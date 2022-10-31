package birsy.shadedcitadels.core.registry;

import birsy.shadedcitadels.common.block.CarvedDeepslate;
import birsy.shadedcitadels.common.block.Pot;
import birsy.shadedcitadels.core.ShadedCitadelsMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ShadedCitadelsBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ShadedCitadelsMod.MODID);

    public static final RegistryObject<Block> CARVED_DEEPSLATE = createBlock("carved_deepslate", () -> new CarvedDeepslate(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final RegistryObject<Block> POT = createBlock("pot", () -> new Pot(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE).sound(SoundType.GLASS).noOcclusion()), CreativeModeTab.TAB_DECORATIONS);

    public static RegistryObject<Block> createBlock(String name, final Supplier<? extends Block> supplier, @Nullable CreativeModeTab group) {
        RegistryObject<Block> block = BLOCKS.register(name, supplier);
        if (group != null) {
            ShadedCitadelsItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(group)));
        }
        return block;
    }
}
