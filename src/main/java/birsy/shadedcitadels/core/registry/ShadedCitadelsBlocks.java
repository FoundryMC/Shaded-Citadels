package birsy.shadedcitadels.core.registry;

import birsy.shadedcitadels.common.block.CarvedDeepslate;
import birsy.shadedcitadels.common.block.Plinth;
import birsy.shadedcitadels.common.block.Pot;
import birsy.shadedcitadels.common.block.StoneLattice;
import birsy.shadedcitadels.core.ShadedCitadelsMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ShadedCitadelsBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ShadedCitadelsMod.MODID);

    public static final RegistryObject<Block> CARVED_DEEPSLATE = createBlock("carved_deepslate", () -> new CarvedDeepslate(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final RegistryObject<Block> DEEPSLATE_BLOCKS = createBlock("deepslate_blocks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final RegistryObject<Block> DEEPSLATE_LATTICE = createBlock("deepslate_lattice", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final RegistryObject<Block> SMOOTH_DEEPSLATE = createBlock("smooth_deepslate", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final RegistryObject<Block> DEEPSLATE_PILLAR = createBlock("deepslate_pillar", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final RegistryObject<Block> DEEPSLATE_PLINTH = createBlock("deepslate_plinth", () -> new Plinth(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static final RegistryObject<Block> POT = createBlock("pot", () -> new Pot(BlockBehaviour.Properties.copy(Blocks.GLASS).sound(SoundType.GLASS).noOcclusion()), CreativeModeTab.TAB_DECORATIONS);
    public static final RegistryObject<Block> ANTIQUE_GRATES = createBlock("antique_grates", () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.CHAIN)), CreativeModeTab.TAB_DECORATIONS);

    public static final RegistryObject<Block> STONE_LATTICE = createBlock("stone_lattice", () -> new StoneLattice(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).noOcclusion()), CreativeModeTab.TAB_BUILDING_BLOCKS);


    public static RegistryObject<Block> createBlock(String name, final Supplier<? extends Block> supplier, @Nullable CreativeModeTab group) {
        RegistryObject<Block> block = BLOCKS.register(name, supplier);
        if (group != null) {
            ShadedCitadelsItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(group)));
        }
        return block;
    }
}
