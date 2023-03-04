package birsy.shadedcitadels.core.datagen;

import birsy.shadedcitadels.core.registry.ShadedCitadelsBlocks;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ShadedCitadelsRecipeProvider extends RecipeProvider {
    private static ItemLike[] FULL_DEEPSLATE_BLOCKS = {Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES, Blocks.CHISELED_DEEPSLATE, ShadedCitadelsBlocks.SMOOTH_DEEPSLATE.get(), ShadedCitadelsBlocks.DEEPSLATE_BLOCKS.get(), ShadedCitadelsBlocks.CARVED_DEEPSLATE.get(), ShadedCitadelsBlocks.DEEPSLATE_LATTICE.get(), ShadedCitadelsBlocks.DEEPSLATE_PILLAR.get(), ShadedCitadelsBlocks.DEEPSLATE_PLINTH.get()};
    private static ItemLike[] FULL_STONE_BLOCKS = {Blocks.STONE, Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS, Blocks.SMOOTH_STONE};

    public ShadedCitadelsRecipeProvider(DataGenerator generatorIn) { super(generatorIn); }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        //stone
        ShapedRecipeBuilder.shaped(ShadedCitadelsBlocks.STONE_LATTICE.get(), 8)
                .pattern("xxx")
                .pattern("x x")
                .pattern("xxx")
                .define('x', Blocks.STONE)
                .unlockedBy("has_stone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.STONE))
                .save(consumer);
        reversableStonecutterRecipes(consumer, FULL_STONE_BLOCKS);

        //deepslate
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.DEEPSLATE), ShadedCitadelsBlocks.SMOOTH_DEEPSLATE.get(),
                0.1F, 100)
                .unlockedBy("has_deepslate", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DEEPSLATE))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ShadedCitadelsBlocks.CARVED_DEEPSLATE.get(), 4)
                .pattern("xz")
                .pattern("zx")
                .define('x', Blocks.CHISELED_DEEPSLATE)
                .define('z', Blocks.DEEPSLATE_TILES)
                .unlockedBy("has_deepslate", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DEEPSLATE))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ShadedCitadelsBlocks.DEEPSLATE_BLOCKS.get(), 4)
                .pattern("xx")
                .pattern("xx")
                .define('x', Blocks.POLISHED_DEEPSLATE)
                .unlockedBy("has_deepslate", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DEEPSLATE))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ShadedCitadelsBlocks.DEEPSLATE_LATTICE.get(), 5)
                .pattern("x x")
                .pattern(" x ")
                .pattern("x x")
                .define('x', ShadedCitadelsBlocks.SMOOTH_DEEPSLATE.get())
                .unlockedBy("has_deepslate", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DEEPSLATE))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ShadedCitadelsBlocks.DEEPSLATE_PILLAR.get(), 2)
                .pattern("x")
                .pattern("x")
                .define('x', Blocks.DEEPSLATE)
                .unlockedBy("has_deepslate", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DEEPSLATE))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ShadedCitadelsBlocks.DEEPSLATE_PLINTH.get(), 3)
                .pattern("x")
                .pattern("z")
                .pattern("x")
                .define('x', ShadedCitadelsBlocks.SMOOTH_DEEPSLATE.get())
                .define('z', Blocks.DEEPSLATE_TILES)
                .unlockedBy("has_deepslate", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DEEPSLATE))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ShadedCitadelsBlocks.ANTIQUE_GRATES.get(), 4)
                .requires(Blocks.DEEPSLATE)
                .requires(Blocks.IRON_BARS, 3)
                .unlockedBy("has_deepslate", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DEEPSLATE))
                .save(consumer);
        stonecutterResultFromBase(consumer, ShadedCitadelsBlocks.ANTIQUE_GRATES.get(),
                Blocks.IRON_BARS);
        stonecutterResultFromBase(consumer, Blocks.IRON_BARS,
                ShadedCitadelsBlocks.ANTIQUE_GRATES.get());

        reversableStonecutterRecipes(consumer, FULL_DEEPSLATE_BLOCKS);
    }

    protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pResult, ItemLike... pMaterials) {
        stonecutterResultFromBase(pFinishedRecipeConsumer, pResult, 1, pMaterials);
    }

    protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pResult, int pResultCount, ItemLike... pMaterials) {
        for (ItemLike pMaterial : pMaterials) {
            SingleItemRecipeBuilder.stonecutting(Ingredient.of(pMaterial), pResult, pResultCount)
                    .unlockedBy(getHasName(pMaterial), has(pMaterial))
                    .save(pFinishedRecipeConsumer, getConversionRecipeName(pResult, pMaterial) + "_stonecutting");
        }
    }

    protected static void reversableStonecutterRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike... pMaterials) {
        for (ItemLike a : pMaterials) {
            for (ItemLike b : pMaterials) {
                if (a != b) {
                    SingleItemRecipeBuilder.stonecutting(Ingredient.of(a), b, 1)
                            .unlockedBy(getHasName(a), has(a))
                            .save(pFinishedRecipeConsumer, getConversionRecipeName(a, b) + "_stonecutting");
                }
            }
        }
    }
}
