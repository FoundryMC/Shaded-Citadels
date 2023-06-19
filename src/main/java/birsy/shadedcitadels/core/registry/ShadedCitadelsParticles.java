package birsy.shadedcitadels.core.registry;

import birsy.shadedcitadels.client.particle.SculkSpeckParticle;
import birsy.shadedcitadels.common.world.biome.biomemodifier.PlantColorOverrideBiomeModifier;
import birsy.shadedcitadels.core.ShadedCitadelsMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ShadedCitadelsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShadedCitadelsParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ShadedCitadelsMod.MODID);

    public static void init() {
        PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<SimpleParticleType> SCULK_SPECK = createParticle("sculk_speck");

    public static RegistryObject<SimpleParticleType> createParticle(String name) {
        RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, () -> new SimpleParticleType(false));
        return particle;
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.register(SCULK_SPECK.get(), SculkSpeckParticle.Provider::new);
    }
}
