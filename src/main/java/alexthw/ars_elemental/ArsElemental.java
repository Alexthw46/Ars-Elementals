package alexthw.ars_elemental;

import alexthw.ars_elemental.client.ClientEvents;
import alexthw.ars_elemental.client.SpellFocusRenderer;
import alexthw.ars_elemental.registry.ModAdvTriggers;
import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.registry.ModPotions;
import alexthw.ars_elemental.registry.ModRegistry;
import alexthw.ars_elemental.util.CompatUtils;
import alexthw.ars_elemental.world.TerrablenderAE;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.ComponentItemHandler;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.util.UUID;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ArsElemental.MODID)
public class ArsElemental {

    public static final String MODID = "ars_elemental";

    public static final UUID Dev = UUID.fromString("0e918660-22bf-4bed-8426-ece3b4bbd01d");
    public static boolean terrablenderLoaded = false;

    public ArsElemental(IEventBus modEventBus, ModContainer modContainer) {
        terrablenderLoaded = ModList.get().isLoaded("terrablender");

        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
        ModRegistry.registerRegistries(modEventBus);
        ArsNouveauRegistry.init();
        modEventBus.addListener(this::setup);
        //modEventBus.addListener(this::sendImc);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::attachCaps);
        NeoForge.EVENT_BUS.register(ModPotions.class);
        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(new ClientEvents()::openBackpackGui);
            modEventBus.addListener(this::doClientStuff);
        }
        ModAdvTriggers.init();
        ArsNouveauAPI.ENABLE_DEBUG_NUMBERS = !FMLEnvironment.production;

    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ArsNouveauRegistry.postInit();
            CompatUtils.checkCompats();
        });
        if (terrablenderLoaded && ConfigHandler.Common.EXTRA_BIOMES.get() > 0) {
            event.enqueueWork(TerrablenderAE::registerBiomes);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doClientStuff(final FMLClientSetupEvent event) {
        if (!ConfigHandler.Client.EnableSFRendering.get()) return;
        CuriosRendererRegistry.register(ModItems.FIRE_FOCUS.get(), SpellFocusRenderer::new);
        CuriosRendererRegistry.register(ModItems.WATER_FOCUS.get(), SpellFocusRenderer::new);
        CuriosRendererRegistry.register(ModItems.AIR_FOCUS.get(), SpellFocusRenderer::new);
        CuriosRendererRegistry.register(ModItems.EARTH_FOCUS.get(), SpellFocusRenderer::new);
        CuriosRendererRegistry.register(ModItems.NECRO_FOCUS.get(), SpellFocusRenderer::new);
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            FlowerPotBlock potBlock = (FlowerPotBlock) Blocks.FLOWER_POT;

            potBlock.addPlant(prefix("yellow_archwood_sapling"), ModItems.POT_FLASHING_SAPLING);
            potBlock.addPlant(prefix("sparkflower"), ModItems.POT_SPARKFLOWER);
        });
    }

    public void attachCaps(final RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, 27 * 2), ModItems.CASTER_BAG.get());
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, 27), ModItems.CURIO_BAG.get());
    }

}
