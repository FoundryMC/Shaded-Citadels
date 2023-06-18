package birsy.shadedcitadels.core.registry;

import birsy.shadedcitadels.client.render.entity.SecretaryRenderer;
import birsy.shadedcitadels.client.render.entity.model.PlaceholderModel;
import birsy.shadedcitadels.common.entity.monster.Secretary;
import birsy.shadedcitadels.core.ShadedCitadelsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ShadedCitadelsMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShadedCitadelsEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ShadedCitadelsMod.MODID);

    public static final RegistryObject<EntityType<Secretary>> SECRETARY = createEntity("secretary", EntityType.Builder.of(Secretary::new, MobCategory.CREATURE).sized(1.0f, 1.8f));

    @SubscribeEvent
    public static void registerEntityAttribute(EntityAttributeCreationEvent event) {
        event.put(SECRETARY.get(), Secretary.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ShadedCitadelsEntities.SECRETARY.get(), SecretaryRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PlaceholderModel.LAYER_LOCATION, PlaceholderModel::createBodyLayer);
    }

    private static RegistryObject<EntityType<Secretary>> createEntity (String name, EntityType.Builder entityBuilder) {
        return ENTITIES.register(name, () -> entityBuilder.build(new ResourceLocation(ShadedCitadelsMod.MODID, name).toString()));
    }
}
