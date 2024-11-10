package alexthw.ars_elemental.registry;

import alexthw.ars_elemental.ArsNouveauRegistry;
import alexthw.ars_elemental.client.ElementalTurretRenderer;
import alexthw.ars_elemental.client.PrismRenderer;
import alexthw.ars_elemental.common.blocks.ElementalTurret;
import alexthw.ars_elemental.common.blocks.EverfullUrnBlock;
import alexthw.ars_elemental.common.blocks.SporeBlossomGround;
import alexthw.ars_elemental.common.blocks.mermaid_block.MermaidRock;
import alexthw.ars_elemental.common.blocks.prism.*;
import alexthw.ars_elemental.common.blocks.upstream.AirUpstreamTile;
import alexthw.ars_elemental.common.blocks.upstream.MagmaUpstreamTile;
import alexthw.ars_elemental.common.blocks.upstream.UpstreamBlock;
import alexthw.ars_elemental.common.items.*;
import alexthw.ars_elemental.common.items.armor.ArmorSet;
import alexthw.ars_elemental.common.items.bangles.*;
import alexthw.ars_elemental.common.items.caster_tools.ElementalCasterTome;
import alexthw.ars_elemental.common.items.caster_tools.SpellHorn;
import alexthw.ars_elemental.common.items.foci.ElementalFocus;
import alexthw.ars_elemental.common.items.foci.GreaterElementalFocus;
import alexthw.ars_elemental.common.items.foci.NecroticFocus;
import alexthw.ars_elemental.datagen.AETagsProvider;
import alexthw.ars_elemental.world.ModWorldgen;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.block.ArchfruitPod;
import com.hollingsworth.arsnouveau.common.block.MagicLeaves;
import com.hollingsworth.arsnouveau.common.block.StrippableLog;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.items.RendererBlockItem;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTree;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static alexthw.ars_elemental.ArsElemental.MODID;
import static alexthw.ars_elemental.registry.ModPotions.LIGHTNING_LURE;
import static com.hollingsworth.arsnouveau.setup.registry.BlockRegistry.LOG_PROP;
import static com.hollingsworth.arsnouveau.setup.registry.BlockRegistry.SAP_PROP;

@SuppressWarnings("SameParameterValue")
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> MERMAID_ROCK;
    public static final RegistryObject<Block> WATER_URN;
    public static final RegistryObject<Block> FLASHING_ARCHWOOD_LOG;
    public static final RegistryObject<Block> FLASHING_ARCHWOOD_LOG_STRIPPED;
    public static final RegistryObject<Block> FLASHING_ARCHWOOD_STRIPPED;
    public static final RegistryObject<Block> FLASHING_ARCHWOOD;
    public static final RegistryObject<Block> FLASHING_SAPLING;
    public static final RegistryObject<Block> FLASHING_LEAVES;
    public static final RegistryObject<Block> FLASHING_POD;
    public static final RegistryObject<Block> POT_FLASHING_SAPLING;


    public static final RegistryObject<Block> GROUND_BLOSSOM;

    public static final RegistryObject<Block> WATER_UPSTREAM_BLOCK;
    public static final RegistryObject<Block> LAVA_UPSTREAM_BLOCK;
    public static final RegistryObject<Block> AIR_UPSTREAM_BLOCK;
    public static final RegistryObject<Block> FIRE_TURRET;
    public static final RegistryObject<Block> WATER_TURRET;
    public static final RegistryObject<Block> AIR_TURRET;
    public static final RegistryObject<Block> EARTH_TURRET;
    public static final RegistryObject<Block> SHAPING_TURRET;

    public static final RegistryObject<Block> ADVANCED_PRISM;
    public static final RegistryObject<Block> SPELL_MIRROR;


    public static final RegistryObject<Item> FIRE_FOCUS;
    public static final RegistryObject<Item> AIR_FOCUS;
    public static final RegistryObject<Item> WATER_FOCUS;
    public static final RegistryObject<Item> EARTH_FOCUS;
    public static final RegistryObject<Item> NECRO_FOCUS;

    public static final RegistryObject<Item> LESSER_FIRE_FOCUS;
    public static final RegistryObject<Item> LESSER_AIR_FOCUS;
    public static final RegistryObject<Item> LESSER_WATER_FOCUS;
    public static final RegistryObject<Item> LESSER_EARTH_FOCUS;

    public static final ArmorSet FIRE_ARMOR = new ArmorSet("fire", SpellSchools.ELEMENTAL_FIRE);
    public static final ArmorSet AIR_ARMOR = new ArmorSet("air", SpellSchools.ELEMENTAL_AIR);
    public static final ArmorSet EARTH_ARMOR = new ArmorSet("earth", SpellSchools.ELEMENTAL_EARTH);
    public static final ArmorSet WATER_ARMOR = new ArmorSet("aqua", SpellSchools.ELEMENTAL_WATER);

    public static final RegistryObject<Item> FIRE_CTOME;
    public static final RegistryObject<Item> AIR_CTOME;
    public static final RegistryObject<Item> WATER_CTOME;
    public static final RegistryObject<Item> EARTH_CTOME;
    public static final RegistryObject<Item> NECRO_CTOME;
    public static final RegistryObject<Item> SHAPERS_CTOME;

    public static final RegistryObject<Item> ENCHANTER_BANGLE;
    public static final RegistryObject<Item> FIRE_BANGLE;
    public static final RegistryObject<Item> WATER_BANGLE;
    public static final RegistryObject<Item> AIR_BANGLE;
    public static final RegistryObject<Item> EARTH_BANGLE;
    public static final RegistryObject<Item> SUMMON_BANGLE;
    public static final RegistryObject<Item> ANIMA_BANGLE;

    public static final RegistryObject<Item> CURIO_BAG;
    public static final RegistryObject<Item> CASTER_BAG;

    public static final RegistryObject<Item> DEBUG_ICON;
    public static final RegistryObject<Item> MARK_OF_MASTERY;

    public static final RegistryObject<Item> HOMING_LENS;
    public static final RegistryObject<Item> ARC_LENS;
    public static final RegistryObject<Item> ACC_LENS;
    public static final RegistryObject<Item> DEC_LENS;
    public static final RegistryObject<Item> PIERCE_LENS;
    public static final RegistryObject<Item> RGB_LENS;

    public static final RegistryObject<Item> ANIMA_ESSENCE;

    public static final RegistryObject<Item> SIREN_SHARDS;
    public static final RegistryObject<Item> SIREN_CHARM;
    public static final RegistryObject<Item> FIRENANDO_CHARM;

    public static final RegistryObject<Item> SPELL_HORN;

    static Item.Properties itemProps() {
        return new Item.Properties();
    }

    static Item.Properties FocusProp() {
        return itemProps().stacksTo(1).fireResistant().rarity(Rarity.EPIC);
    }

    static Item.Properties UncommonProp() {
        return itemProps().stacksTo(1).rarity(Rarity.UNCOMMON);
    }

    public static Item.Properties ArmorProp() {
        return itemProps().rarity(Rarity.EPIC);
    }

    public static FoodProperties FLASHPINE_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.6F)
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 30 * 20), .4f)
            .effect(() -> new MobEffectInstance(MobEffects.GLOWING, 30 * 20), .4f)
            .effect(() -> new MobEffectInstance(ModPotions.SHOCKED_EFFECT.get(), 30 * 20, 0), .8f)
            .effect(() -> new MobEffectInstance(LIGHTNING_LURE.get(), 30 * 20, 0), .2f)
            .alwaysEat().build();

    static {

        SIREN_SHARDS = ITEMS.register("siren_shards", () -> new ModItem(itemProps()).withTooltip(Component.translatable("tooltip.siren_shards")));
        SIREN_CHARM = ITEMS.register("siren_charm", () -> new SirenCharm(itemProps()));
        FIRENANDO_CHARM = ITEMS.register("firenando_charm", () -> new FirenandoCharm(itemProps().fireResistant()));

        DEBUG_ICON = ITEMS.register("debug", () -> new Debugger(FocusProp()));
        MARK_OF_MASTERY = ITEMS.register("mark_of_mastery", () -> new Item(itemProps()));
        ANIMA_ESSENCE = ITEMS.register("anima_essence", () -> new NecroEssence(itemProps()));

        SPELL_HORN = ITEMS.register("spell_horn", () -> new SpellHorn(itemProps()));

        HOMING_LENS = ITEMS.register("homing_prism_lens", () -> new HomingPrismLens(itemProps()));
        ARC_LENS = ITEMS.register("arc_prism_lens", () -> new ArcPrismLens(itemProps()));
        RGB_LENS = ITEMS.register("rainbow_prism_lens", () -> new RainbowPrismLens(itemProps()));
        ACC_LENS = ITEMS.register("acceleration_prism_lens", () -> new AccelerationPrismLens(itemProps()));
        DEC_LENS = ITEMS.register("deceleration_prism_lens", () -> new DecelerationPrismLens(itemProps()));
        PIERCE_LENS = ITEMS.register("piercing_prism_lens", () -> new PiercingPrismLens(itemProps()));
        //curio
        CURIO_BAG = ITEMS.register("curio_bag", () -> new CurioHolder(itemProps().fireResistant().stacksTo(1)));
        FIRE_FOCUS = ITEMS.register("fire_focus", () -> new GreaterElementalFocus(FocusProp(), SpellSchools.ELEMENTAL_FIRE));
        WATER_FOCUS = ITEMS.register("water_focus", () -> new GreaterElementalFocus(FocusProp(), SpellSchools.ELEMENTAL_WATER));
        AIR_FOCUS = ITEMS.register("air_focus", () -> new GreaterElementalFocus(FocusProp(), SpellSchools.ELEMENTAL_AIR));
        EARTH_FOCUS = ITEMS.register("earth_focus", () -> new GreaterElementalFocus(FocusProp(), SpellSchools.ELEMENTAL_EARTH));
        NECRO_FOCUS = ITEMS.register("necrotic_focus", () -> new NecroticFocus(FocusProp()));

        LESSER_FIRE_FOCUS = ITEMS.register("lesser_fire_focus", () -> new ElementalFocus(UncommonProp(), SpellSchools.ELEMENTAL_FIRE));
        LESSER_WATER_FOCUS = ITEMS.register("lesser_water_focus", () -> new ElementalFocus(UncommonProp(), SpellSchools.ELEMENTAL_WATER));
        LESSER_AIR_FOCUS = ITEMS.register("lesser_air_focus", () -> new ElementalFocus(UncommonProp(), SpellSchools.ELEMENTAL_AIR));
        LESSER_EARTH_FOCUS = ITEMS.register("lesser_earth_focus", () -> new ElementalFocus(UncommonProp(), SpellSchools.ELEMENTAL_EARTH));

        //bangles
        ENCHANTER_BANGLE = ITEMS.register("base_bangle", () -> new BaseBangle(itemProps().stacksTo(1)));
        FIRE_BANGLE = ITEMS.register("fire_bangle", () -> new FireBangles(UncommonProp()));
        WATER_BANGLE = ITEMS.register("water_bangle", () -> new WaterBangles(UncommonProp()));
        AIR_BANGLE = ITEMS.register("air_bangle", () -> new AirBangles(UncommonProp()));
        EARTH_BANGLE = ITEMS.register("earth_bangle", () -> new EarthBangles(UncommonProp()));
        SUMMON_BANGLE = ITEMS.register("summon_bangle", () -> new SummonBangles(UncommonProp()));
        ANIMA_BANGLE = ITEMS.register("anima_bangle", () -> new AnimaBangles(UncommonProp()));

        //caster tomes
        CASTER_BAG = ITEMS.register("caster_bag", () -> new CasterHolder(itemProps().fireResistant().stacksTo(1)));
        FIRE_CTOME = ITEMS.register("fire_caster_tome", () -> new ElementalCasterTome(itemProps(), SpellSchools.ELEMENTAL_FIRE));
        WATER_CTOME = ITEMS.register("water_caster_tome", () -> new ElementalCasterTome(itemProps(), SpellSchools.ELEMENTAL_WATER));
        AIR_CTOME = ITEMS.register("air_caster_tome", () -> new ElementalCasterTome(itemProps(), SpellSchools.ELEMENTAL_AIR));
        EARTH_CTOME = ITEMS.register("earth_caster_tome", () -> new ElementalCasterTome(itemProps(), SpellSchools.ELEMENTAL_EARTH));
        NECRO_CTOME = ITEMS.register("anima_caster_tome", () -> new ElementalCasterTome(itemProps(), ArsNouveauRegistry.NECROMANCY));
        SHAPERS_CTOME = ITEMS.register("manipulation_caster_tome", () -> new ElementalCasterTome(itemProps(), SpellSchools.MANIPULATION));

        //blocks
        WATER_URN = addBlock("everfull_urn", () -> new EverfullUrnBlock(blockProps(Blocks.CLAY, MapColor.COLOR_BROWN).sound(SoundType.PACKED_MUD).noOcclusion()));
        MERMAID_ROCK = addBlock("mermaid_rock", () -> new MermaidRock(blockProps(Blocks.STONE, MapColor.COLOR_LIGHT_BLUE).sound(SoundType.CORAL_BLOCK).strength(2.0f, 6.0f).noOcclusion().lightLevel(b -> 10)));
        GROUND_BLOSSOM = addBlock("spore_blossom_up", () -> new SporeBlossomGround(blockProps(Blocks.SPORE_BLOSSOM, MapColor.COLOR_PINK).sound(SoundType.SPORE_BLOSSOM).noOcclusion()));
        WATER_UPSTREAM_BLOCK = addBlock("water_upstream", () -> new UpstreamBlock(blockProps(Blocks.STONE, MapColor.COLOR_LIGHT_BLUE).sound(SoundType.STONE).strength(2.0f, 6.0f)));
        LAVA_UPSTREAM_BLOCK = addBlock("magma_upstream", () -> new UpstreamBlock(blockProps(Blocks.STONE, MapColor.COLOR_RED).sound(SoundType.STONE).strength(2.0f, 6.0f)) {
            @Override
            public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
                return new MagmaUpstreamTile(pPos, pState);
            }
        });
        AIR_UPSTREAM_BLOCK = addBlock("air_upstream", () -> new UpstreamBlock(blockProps(Blocks.STONE, MapColor.COLOR_YELLOW).sound(SoundType.STONE).strength(2.0f, 6.0f)) {
            @Override
            public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
                return new AirUpstreamTile(pPos, pState);
            }
        });

        //turrets
        FIRE_TURRET = addGeckoBlock("fire_turret", () -> new ElementalTurret(blockProps(Blocks.GOLD_BLOCK, MapColor.COLOR_RED).sound(SoundType.STONE).strength(2.0f, 6.0f), SpellSchools.ELEMENTAL_FIRE), "fire");
        WATER_TURRET = addGeckoBlock("water_turret", () -> new ElementalTurret(blockProps(Blocks.GOLD_BLOCK, MapColor.COLOR_LIGHT_BLUE).sound(SoundType.STONE).strength(2.0f, 6.0f), SpellSchools.ELEMENTAL_WATER), "water");
        AIR_TURRET = addGeckoBlock("air_turret", () -> new ElementalTurret(blockProps(Blocks.GOLD_BLOCK, MapColor.COLOR_YELLOW).sound(SoundType.STONE).strength(2.0f, 6.0f), SpellSchools.ELEMENTAL_AIR), "air");
        EARTH_TURRET = addGeckoBlock("earth_turret", () -> new ElementalTurret(blockProps(Blocks.GOLD_BLOCK, MapColor.COLOR_GREEN).sound(SoundType.STONE).strength(2.0f, 6.0f), SpellSchools.ELEMENTAL_EARTH), "earth");
        SHAPING_TURRET = addGeckoBlock("manipulation_turret", () -> new ElementalTurret(blockProps(Blocks.GOLD_BLOCK, MapColor.COLOR_ORANGE).sound(SoundType.STONE).strength(2.0f, 6.0f), SpellSchools.MANIPULATION), "manipulation");


        ADVANCED_PRISM = BLOCKS.register("advanced_prism", () -> new AdvancedPrism(blockProps(Blocks.STONE, MapColor.TERRACOTTA_WHITE)));
        ITEMS.register("advanced_prism", () -> new RendererBlockItem(ADVANCED_PRISM.get(), itemProps()) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return PrismRenderer::getISTER;
            }
        });
        SPELL_MIRROR = addBlock("spell_mirror", () -> new SpellMirror(blockProps(Blocks.STONE, MapColor.TERRACOTTA_WHITE)));

        //Trees
        FLASHING_SAPLING = addBlock("yellow_archwood_sapling", () -> new SaplingBlock(new MagicTree(ModWorldgen.FLASHING_TREE_SAPLING), SAP_PROP));
        FLASHING_LEAVES = addBlock("yellow_archwood_leaves", () -> new MagicLeaves(blockProps(Blocks.OAK_LEAVES, MapColor.COLOR_YELLOW).lightLevel(b -> 8).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(
                ModItems::allowsSpawnOnLeaves).isSuffocating(ModItems::isntSolid).isViewBlocking(ModItems::isntSolid)));
        FLASHING_ARCHWOOD_LOG_STRIPPED = addBlock("stripped_yellow_archwood_log", () -> new RotatedPillarBlock(LOG_PROP.mapColor(MapColor.COLOR_YELLOW).lightLevel(b -> 6)));
        FLASHING_ARCHWOOD_STRIPPED = addBlock("stripped_yellow_archwood", () -> new RotatedPillarBlock(LOG_PROP.mapColor(MapColor.COLOR_YELLOW).lightLevel(b -> 6)));
        FLASHING_ARCHWOOD_LOG = addBlock("yellow_archwood_log", () -> new StrippableLog(LOG_PROP.mapColor(MapColor.COLOR_YELLOW).lightLevel(b -> 8), FLASHING_ARCHWOOD_LOG_STRIPPED));
        FLASHING_ARCHWOOD = addBlock("yellow_archwood", () -> new StrippableLog(LOG_PROP.mapColor(MapColor.COLOR_YELLOW).lightLevel(b -> 8), FLASHING_ARCHWOOD_STRIPPED));
        FLASHING_POD = BLOCKS.register("flashpine_pod", () -> new ArchfruitPod(AETagsProvider.AEBlockTagsProvider.FLASHING_LOGS));
        ITEMS.register("flashpine_pod", () -> new ItemNameBlockItem(FLASHING_POD.get(), itemProps().food(FLASHPINE_FOOD)));
        POT_FLASHING_SAPLING = BLOCKS.register("potted_yellow_archwood_sapling", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLASHING_SAPLING, blockProps(Blocks.FLOWER_POT, MapColor.COLOR_YELLOW).instabreak().noOcclusion()));
    }

    static RegistryObject<Block> addBlock(String name, Supplier<Block> blockSupp) {
        RegistryObject<Block> block = BLOCKS.register(name, blockSupp);
        ITEMS.register(name, () -> new BlockItem(block.get(), itemProps()));
        return block;
    }

    static RegistryObject<Block> addGeckoBlock(String name, Supplier<Block> blockSupp, String model) {
        RegistryObject<Block> block = BLOCKS.register(name, blockSupp);
        ITEMS.register(name, () -> new RendererBlockItem(block.get(), itemProps()) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return () -> ElementalTurretRenderer.getISTER(model);
            }
        });
        return block;
    }

    static BlockBehaviour.Properties blockProps(Block copyFrom, MapColor color) {
        return BlockBehaviour.Properties.copy(copyFrom).mapColor(color);
    }

    private static Boolean allowsSpawnOnLeaves(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }

}
