package birsy.shadedcitadels.core.datagen.loot;

import birsy.shadedcitadels.common.block.CarvedDeepslate;
import birsy.shadedcitadels.common.block.Plinth;
import birsy.shadedcitadels.common.block.Pot;
import birsy.shadedcitadels.common.block.StoneLattice;
import birsy.shadedcitadels.core.registry.ShadedCitadelsBlocks;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class ShadedCitadelsBlockLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
    private static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
    private static final LootItemCondition.Builder HAS_NO_SILK_TOUCH = HAS_SILK_TOUCH.invert();
    private static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
    private static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
    private static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
    private final Map<ResourceLocation, LootTable.Builder> map = Maps.newHashMap();

    public static LootTable.Builder noDrop() {
        return LootTable.lootTable();
    }

    protected void addTables() {
        this.dropSelf(ShadedCitadelsBlocks.CARVED_DEEPSLATE.get());
        this.dropSelf(ShadedCitadelsBlocks.DEEPSLATE_BLOCKS.get());
        this.dropSelf(ShadedCitadelsBlocks.DEEPSLATE_LATTICE.get());
        this.dropSelf(ShadedCitadelsBlocks.SMOOTH_DEEPSLATE.get());
        this.dropSelf(ShadedCitadelsBlocks.DEEPSLATE_PILLAR.get());
        this.dropSelf(ShadedCitadelsBlocks.DEEPSLATE_PLINTH.get());

        this.dropWhenSilkTouch(ShadedCitadelsBlocks.POT.get());
        this.dropSelf(ShadedCitadelsBlocks.ANTIQUE_GRATES.get());

        this.dropSelf(ShadedCitadelsBlocks.STONE_LATTICE.get());
        this.dropSelf(ShadedCitadelsBlocks.STONE_PILLAR.get());
        this.dropSelf(ShadedCitadelsBlocks.STONE_PLINTH.get());
        this.dropSelf(ShadedCitadelsBlocks.CARVED_STONE.get());
        this.dropSelf(ShadedCitadelsBlocks.SMOOTH_DEEPSLATE.get());

    }

    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_124179_) {
        this.addTables();
        Set<ResourceLocation> set = Sets.newHashSet();

        for(RegistryObject<Block> b : ShadedCitadelsBlocks.BLOCKS.getEntries()) {
            Block block = b.get();
            ResourceLocation resourcelocation = block.getLootTable();
            if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
                LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
                if (loottable$builder == null) {
                    throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourcelocation, Registry.BLOCK.getKey(block)));
                }

                p_124179_.accept(resourcelocation, loottable$builder);
            }
        }

        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
        }
    }

    protected static <T extends ConditionUserBuilder<T>> T applyExplosionCondition(ItemLike pItem, ConditionUserBuilder<T> pCondition) {
        return pCondition.when(ExplosionCondition.survivesExplosion());
    }

    public void otherWhenSilkTouch(Block pBlock, Block pSilkTouchDrop) {
        this.add(pBlock, LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(pBlock))));
    }

    public void dropOther(Block pBlock, ItemLike pDrop) {
        this.add(pBlock, LootTable.lootTable().withPool(applyExplosionCondition(pDrop, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(pDrop)))));
    }

    public void dropWhenSilkTouch(Block pBlock) {
        this.otherWhenSilkTouch(pBlock, pBlock);
    }

    public void dropSelf(Block pBlock) {
        this.dropOther(pBlock, pBlock);
    }

    protected void add(Block pBlock, Function<Block, LootTable.Builder> pFactory) {
        this.add(pBlock, pFactory.apply(pBlock));
    }

    protected void add(Block pBlock, LootTable.Builder pLootTableBuilder) {
        this.map.put(pBlock.getLootTable(), pLootTableBuilder);
    }
}
