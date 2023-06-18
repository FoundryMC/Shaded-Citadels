package birsy.shadedcitadels.common.world.biome.biomemodifier;

import birsy.shadedcitadels.core.ShadedCitadelsMod;
import birsy.shadedcitadels.core.registry.ShadedCitadelsBiomeModifiers;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.Optional;

public record PlantColorOverrideBiomeModifier(HolderSet<Biome> biomes, Optional<Integer> grassColor, Optional<Integer> foliageColor) implements BiomeModifier {
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.MODIFY && biomes.contains(biome)) {
            ShadedCitadelsMod.LOGGER.info("Running Plant Color Override Biome Modifier");

            grassColor.ifPresent((color -> {
                builder.getSpecialEffects().grassColorOverride(color);
                ShadedCitadelsMod.LOGGER.info("Changed Grass Color to " + color);
            }));

            foliageColor.ifPresent((color -> {
                builder.getSpecialEffects().foliageColorOverride(color);
                ShadedCitadelsMod.LOGGER.info("Changed Foliage Color to " + color);
            }));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ShadedCitadelsBiomeModifiers.PLANT_COLOR_OVERRIDE_BIOME_MODIFIER_TYPE.get();
    }
}
