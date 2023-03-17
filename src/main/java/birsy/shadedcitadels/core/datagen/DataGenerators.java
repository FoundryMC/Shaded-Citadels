package birsy.shadedcitadels.core.datagen;

import birsy.shadedcitadels.core.datagen.loot.ShadedCitadelsLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new ShadedCitadelsRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new ShadedCitadelsLootTableProvider(generator));
    }
}