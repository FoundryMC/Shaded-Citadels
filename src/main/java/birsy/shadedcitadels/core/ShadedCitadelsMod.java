package birsy.shadedcitadels.core;

import birsy.shadedcitadels.core.registry.ShadedCitadelsBlocks;
import birsy.shadedcitadels.core.registry.ShadedCitadelsEntities;
import birsy.shadedcitadels.core.registry.ShadedCitadelsItems;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

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

        MinecraftForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    public static ResourceLocation name(String resourceName) {
        return new ResourceLocation(MODID, resourceName);
    }
}
