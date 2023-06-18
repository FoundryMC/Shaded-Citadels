package birsy.shadedcitadels.core;

import birsy.shadedcitadels.core.registry.ShadedCitadelsBiomeModifiers;
import birsy.shadedcitadels.core.registry.ShadedCitadelsBlocks;
import birsy.shadedcitadels.core.registry.ShadedCitadelsEntities;
import birsy.shadedcitadels.core.registry.ShadedCitadelsItems;
import com.mojang.logging.LogUtils;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ShadedCitadelsMod.MODID)
public class ShadedCitadelsMod
{
    public static final String MODID = "shaded_citadels";

    public static final Logger LOGGER = LogUtils.getLogger();

    public ShadedCitadelsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ShadedCitadelsBlocks.BLOCKS.register(modEventBus);
        ShadedCitadelsItems.ITEMS.register(modEventBus);
        ShadedCitadelsEntities.ENTITIES.register(modEventBus);
        ShadedCitadelsBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CaveFeatures.MOSS_VEGETATION = FeatureUtils.register("moss_vegetation", Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(
                new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                        .add(Blocks.FLOWERING_AZALEA.defaultBlockState(), 4)
                        .add(Blocks.AZALEA.defaultBlockState(), 7)
                        .add(Blocks.MOSS_CARPET.defaultBlockState(), 25)
                        .add(Blocks.GRASS.defaultBlockState(), 50)
                        .add(Blocks.TALL_GRASS.defaultBlockState(), 10))));
    }

    public static ResourceLocation name(String resourceName) {
        return new ResourceLocation(MODID, resourceName);
    }
}
