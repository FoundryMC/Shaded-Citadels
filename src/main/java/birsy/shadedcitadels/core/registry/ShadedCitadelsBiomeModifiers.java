package birsy.shadedcitadels.core.registry;

import birsy.shadedcitadels.common.world.biome.biomemodifier.PlantColorOverrideBiomeModifier;
import birsy.shadedcitadels.core.ShadedCitadelsMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ShadedCitadelsBiomeModifiers {
    public static DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ShadedCitadelsMod.MODID);

    public static RegistryObject<Codec<PlantColorOverrideBiomeModifier>> PLANT_COLOR_OVERRIDE_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("plant_color_override", () ->
            RecordCodecBuilder.create(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(PlantColorOverrideBiomeModifier::biomes),
                    Codec.INT.optionalFieldOf("grass_color").forGetter(PlantColorOverrideBiomeModifier::grassColor),
                    Codec.INT.optionalFieldOf("foliage_color").forGetter(PlantColorOverrideBiomeModifier::foliageColor)
            ).apply(builder, PlantColorOverrideBiomeModifier::new)));
}
