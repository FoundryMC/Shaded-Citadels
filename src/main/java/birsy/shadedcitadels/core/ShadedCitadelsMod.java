package birsy.shadedcitadels.core;

import birsy.shadedcitadels.core.registry.ShadedCitadelsBlocks;
import birsy.shadedcitadels.core.registry.ShadedCitadelsItems;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
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

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    public static ResourceLocation name(String resourceName) {
        return new ResourceLocation(MODID, resourceName);
    }
}
