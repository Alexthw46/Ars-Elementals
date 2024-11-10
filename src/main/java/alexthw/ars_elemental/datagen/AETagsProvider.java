package alexthw.ars_elemental.datagen;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.registry.ModPotions;
import alexthw.ars_elemental.registry.ModRegistry;
import alexthw.ars_elemental.world.ModWorldgen;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.datagen.BiomeTagProvider;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.lib.PotionEffectTags;
import com.hollingsworth.arsnouveau.setup.registry.BiomeRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static alexthw.ars_elemental.ArsElemental.MODID;
import static alexthw.ars_elemental.ArsElemental.prefix;
import static alexthw.ars_elemental.registry.ModEntities.FIRENANDO_FAMILIAR;
import static alexthw.ars_elemental.registry.ModEntities.SIREN_FAMILIAR;
import static com.hollingsworth.arsnouveau.setup.registry.ModPotions.SUMMONING_SICKNESS_EFFECT;

public class AETagsProvider {

    public static class AEItemTagsProvider extends ItemTagsProvider {

        String[] curioSlots = {"curio", "back", "belt", "body", "bracelet", "charm", "feet", "head", "hands", "necklace", "ring", "spellbook"};

        static TagKey<Item> curiosTag(String key) {
            return ItemTags.create(new ResourceLocation(CuriosApi.MODID, key));
        }

        public static final TagKey<Item> CURIO_SPELL_FOCUS = curiosTag("an_focus");
        public static final TagKey<Item> CURIO_BANGLE = curiosTag("bracelet");
        public static final TagKey<Item> SUMMON_SHARDS = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "magic_shards"));
        public static final TagKey<Item> SPELLBOOK = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "spellbook"));
        public static final TagKey<Item> PRISM_LENS = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "spell_prism_lens"));


        public static final TagKey<Item> MAGIC_HOOD = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "hood"));
        public static final TagKey<Item> MAGIC_ROBE = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "robe"));
        public static final TagKey<Item> MAGIC_LEG = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "legs"));
        public static final TagKey<Item> MAGIC_BOOT = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "boot"));

        // create log compat

        public static final TagKey<Item> STRIPPED_LOGS = ItemTags.create(new ResourceLocation("forge", "stripped_logs"));
        public static final TagKey<Item> STRIPPED_WOODS = ItemTags.create(new ResourceLocation("forge", "stripped_wood"));


        public AEItemTagsProvider(DataGenerator gen, CompletableFuture<HolderLookup.Provider> provider, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(gen.getPackOutput(), provider, blockTagsProvider.contentsGetter(), ArsElemental.MODID, existingFileHelper);
        }


        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(SPELLBOOK).add(ItemsRegistry.NOVICE_SPELLBOOK.get(), ItemsRegistry.APPRENTICE_SPELLBOOK.get(), ItemsRegistry.ARCHMAGE_SPELLBOOK.get(), ItemsRegistry.CREATIVE_SPELLBOOK.get()).addOptional(new ResourceLocation("ars_omega", "arcane_book"));
            tag(ModRegistry.SOULBOUND_ABLE).add(Items.WRITABLE_BOOK, Items.WRITTEN_BOOK);
            tag(CURIO_SPELL_FOCUS).add(ModItems.AIR_FOCUS.get(), ModItems.FIRE_FOCUS.get(), ModItems.EARTH_FOCUS.get(), ModItems.NECRO_FOCUS.get(), ModItems.WATER_FOCUS.get(), ModItems.LESSER_AIR_FOCUS.get(), ModItems.LESSER_FIRE_FOCUS.get(), ModItems.LESSER_EARTH_FOCUS.get(), ModItems.LESSER_WATER_FOCUS.get());
            tag(CURIO_BANGLE).add(ModItems.AIR_BANGLE.get(), ModItems.FIRE_BANGLE.get(), ModItems.EARTH_BANGLE.get(), ModItems.WATER_BANGLE.get(), ModItems.ENCHANTER_BANGLE.get(), ModItems.SUMMON_BANGLE.get(), ModItems.ANIMA_BANGLE.get());
            tag(SUMMON_SHARDS).add(ModItems.SIREN_SHARDS.get(), ItemsRegistry.DRYGMY_SHARD.get(), ItemsRegistry.STARBUNCLE_SHARD.get(), ItemsRegistry.WIXIE_SHARD.get(), ItemsRegistry.WHIRLISPRIG_SHARDS.get());
            tag(PRISM_LENS).add(ModItems.ARC_LENS.get(), ModItems.HOMING_LENS.get(), ModItems.RGB_LENS.get(), ModItems.PIERCE_LENS.get(), ModItems.ACC_LENS.get(), ModItems.DEC_LENS.get());
            tag(ModRegistry.CURIO_BAGGABLE).add(ItemsRegistry.ALCHEMISTS_CROWN.get(), ItemsRegistry.WORN_NOTEBOOK.get(), ItemsRegistry.DOMINION_ROD.get(), ItemsRegistry.DOWSING_ROD.get(), ItemsRegistry.JAR_OF_LIGHT.get(), ItemsRegistry.VOID_JAR.get(), ItemsRegistry.RUNIC_CHALK.get(), ItemsRegistry.WARP_SCROLL.get(), ItemsRegistry.STABLE_WARP_SCROLL.get(), ItemsRegistry.SPELL_PARCHMENT.get()).addTag(SUMMON_SHARDS).addTag(PRISM_LENS);
            tag(ModRegistry.CASTER_BAGGABLE).add(ItemsRegistry.ENCHANTERS_SHIELD.get());
            this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
            this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
            this.copy(BlockTags.LOGS, ItemTags.LOGS);
            this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
            tag(ItemTagProvider.ARCHWOOD_LOG_TAG).add(
                    ModItems.FLASHING_ARCHWOOD_LOG.get().asItem(),
                    ModItems.FLASHING_ARCHWOOD.get().asItem(),
                    ModItems.FLASHING_ARCHWOOD_LOG_STRIPPED.get().asItem(),
                    ModItems.FLASHING_ARCHWOOD_STRIPPED.get().asItem()
            );
            tag(MAGIC_HOOD).add(ItemsRegistry.BATTLEMAGE_HOOD.get(), ItemsRegistry.ARCANIST_HOOD.get(), ItemsRegistry.SORCERER_HOOD.get());
            tag(MAGIC_ROBE).add(ItemsRegistry.BATTLEMAGE_ROBES.get(), ItemsRegistry.ARCANIST_ROBES.get(), ItemsRegistry.SORCERER_ROBES.get());
            tag(MAGIC_LEG).add(ItemsRegistry.BATTLEMAGE_LEGGINGS.get(), ItemsRegistry.ARCANIST_LEGGINGS.get(), ItemsRegistry.SORCERER_LEGGINGS.get());
            tag(MAGIC_BOOT).add(ItemsRegistry.BATTLEMAGE_BOOTS.get(), ItemsRegistry.ARCANIST_BOOTS.get(), ItemsRegistry.SORCERER_BOOTS.get());

            Arrays.stream(curioSlots).map(AEItemTagsProvider::curiosTag).forEach(t ->
                    tag(ModRegistry.CURIO_BAGGABLE).addOptionalTag(t.location())
            );

            tag(ModRegistry.BLACKLIST_BAGGABLE).add(ModItems.CURIO_BAG.get(), ModItems.CASTER_BAG.get());

            tag(STRIPPED_LOGS).add(ModItems.FLASHING_ARCHWOOD_LOG_STRIPPED.get().asItem());
            tag(STRIPPED_WOODS).add(ModItems.FLASHING_ARCHWOOD_STRIPPED.get().asItem());
        }

        @Override
        public @NotNull String getName() {
            return "Ars Elemental Item Tags";
        }
    }

    public static class AEBlockTagsProvider extends BlockTagsProvider {
        final TagKey<Block> ARCHWOOD_LEAVES = BlockTags.create(new ResourceLocation("minecraft", "leaves/archwood_leaves"));
        public static final TagKey<Block> FLASHING_LOGS = BlockTags.create(new ResourceLocation(ArsNouveau.MODID, "flashing_logs"));

        public AEBlockTagsProvider(DataGenerator gen, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(gen.getPackOutput(), provider, ArsElemental.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            addPickMineable(1, ModItems.WATER_UPSTREAM_BLOCK.get(), ModItems.AIR_UPSTREAM_BLOCK.get(), ModItems.LAVA_UPSTREAM_BLOCK.get());
            addPickMineable(0, ModItems.SPELL_MIRROR.get(), ModItems.AIR_TURRET.get(), ModItems.FIRE_TURRET.get(), ModItems.EARTH_TURRET.get(), ModItems.WATER_TURRET.get(), ModItems.SHAPING_TURRET.get());
            logsTag(ModItems.FLASHING_ARCHWOOD_LOG.get(),
                    ModItems.FLASHING_ARCHWOOD.get(),
                    ModItems.FLASHING_ARCHWOOD_STRIPPED.get(),
                    ModItems.FLASHING_ARCHWOOD_LOG_STRIPPED.get()
            );
            tag(FLASHING_LOGS).add(ModItems.FLASHING_ARCHWOOD_LOG.get(),
                    ModItems.FLASHING_ARCHWOOD.get(),
                    ModItems.FLASHING_ARCHWOOD_STRIPPED.get(),
                    ModItems.FLASHING_ARCHWOOD_LOG_STRIPPED.get()
            );
            tag(BlockTags.LEAVES).add(ModItems.FLASHING_LEAVES.get());
            tag(ARCHWOOD_LEAVES).add(ModItems.FLASHING_LEAVES.get());
            tag(BlockTags.MINEABLE_WITH_HOE).add(ModItems.FLASHING_LEAVES.get());
            tag(BlockTags.SAPLINGS).add(ModItems.FLASHING_SAPLING.get());
            tag(BlockTagProvider.MAGIC_SAPLINGS).add(ModItems.FLASHING_SAPLING.get());

        }

        void logsTag(Block... blocks) {
            tag(BlockTags.LOGS).add(blocks);
            tag(BlockTags.LOGS_THAT_BURN).add(blocks);
            tag(BlockTags.MINEABLE_WITH_AXE).add(blocks);
        }

        void addPickMineable(int level, Block... blocks) {
            for (Block block : blocks) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
                switch (level) {
                    case 1 -> tag(BlockTags.NEEDS_STONE_TOOL).add(block);
                    case 2 -> tag(BlockTags.NEEDS_IRON_TOOL).add(block);
                    case 3 -> tag(BlockTags.NEEDS_DIAMOND_TOOL).add(block);
                    case 4 -> tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(block);
                }
            }

        }

        @Override
        public @NotNull String getName() {
            return "Ars Elemental Block Tags";
        }
    }

    public static class AEBiomeTagsProvider extends BiomeTagsProvider {
        public AEBiomeTagsProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator.getPackOutput(), provider, ArsElemental.MODID, existingFileHelper);
        }

        public static final TagKey<Biome> SIREN_SPAWN_TAG = TagKey.create(Registries.BIOME, prefix("siren_spawn"));
        public static final TagKey<Biome> FLASHING_BIOME = TagKey.create(Registries.BIOME, prefix("flashing_biome"));
        public static final TagKey<Biome> FLASHING_TREE_COMMON_BIOME = TagKey.create(Registries.BIOME, prefix("flashing_tree_biome"));

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            this.tag(SIREN_SPAWN_TAG).addTag(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL).addOptionalTag(BiomeTagProvider.ARCHWOOD_BIOME_TAG.location());
            this.tag(FLASHING_BIOME).addOptional(ModWorldgen.Biomes.FLASHING_FOREST_KEY.location());
            this.tag(FLASHING_TREE_COMMON_BIOME).addOptional(BiomeRegistry.ARCHWOOD_FOREST.location());

            for (var forest : ModWorldgen.Biomes.ArchwoodBiomes) {
                this.tag(BiomeTagProvider.ARCHWOOD_BIOME_TAG).addOptional(forest);
                this.tag(BiomeTagProvider.BERRY_SPAWN).addOptional(forest);
            }
        }

        @Override
        public @NotNull String getName() {
            return "Ars Elemental Biome Tags";
        }
    }

    public static class AEFeatureTagsProvider extends TagsProvider<PlacedFeature> {
        public AEFeatureTagsProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator.getPackOutput(), Registries.PLACED_FEATURE, provider, ArsElemental.MODID, existingFileHelper);
        }

        public static TagKey<PlacedFeature> RARE_ARCHWOOD_TREES = TagKey.create(Registries.PLACED_FEATURE, prefix(ModWorldgen.FINAL_RARE_FLASHING));
        public static TagKey<PlacedFeature> COMMON_ARCHWOOD_TREES = TagKey.create(Registries.PLACED_FEATURE, prefix(ModWorldgen.FINAL_COMMON_FLASHING));

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(RARE_ARCHWOOD_TREES).addOptional(ModWorldgen.RARE_FLASHING_CONFIGURED.location());
            tag(COMMON_ARCHWOOD_TREES).addOptional(ModWorldgen.COMMON_FLASHING_CONFIGURED.location());
        }

        @Override
        public @NotNull String getName() {
            return "Ars Elemental Feature Tags";
        }
    }

    public static class AEEntityTagProvider extends EntityTypeTagsProvider {

        public AEEntityTagProvider(DataGenerator pGenerator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator.getPackOutput(), provider, MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            this.tag(EntityTags.FAMILIAR).add(FIRENANDO_FAMILIAR.get(), SIREN_FAMILIAR.get());
            this.tag(ModRegistry.AERIAL).add(EntityType.PHANTOM, EntityType.WITHER, EntityType.BAT, EntityType.ALLAY, EntityType.ENDER_DRAGON, EntityType.PARROT, EntityType.GHAST, EntityType.VEX, EntityType.BEE, ModEntities.WILDEN_STALKER.get(), ModEntities.WILDEN_BOSS.get());
            this.tag(ModRegistry.FIERY).add(EntityType.ENDER_DRAGON);
            this.tag(ModRegistry.UNDEAD).add(EntityType.GHAST);
            this.tag(ModRegistry.AQUATIC).add(EntityType.AXOLOTL, EntityType.FROG, EntityType.DROWNED);
            this.tag(ModRegistry.INSECT).add(EntityType.SILVERFISH);
            this.tag(ModRegistry.ATTRACT_BLACKLIST).add(EntityType.PLAYER).addTag(Tags.EntityTypes.BOSSES).add(ModEntities.ENTITY_FOLLOW_PROJ.get()).addTag(EntityTags.FAMILIAR);
        }

        @Override
        public @NotNull String getName() {
            return "Ars Elemental Entity Tags";
        }
    }

    public static class AEMobEffectTagProvider extends IntrinsicHolderTagsProvider<MobEffect> {

        public AEMobEffectTagProvider(DataGenerator pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pOutput.getPackOutput(), Registries.MOB_EFFECT, pProvider, ef -> ForgeRegistries.MOB_EFFECTS.getResourceKey(ef).orElseThrow(), ArsElemental.MODID, existingFileHelper);
        }

        public static TagKey<MobEffect> BUBBLE_BLACKLIST = TagKey.create(Registries.MOB_EFFECT, prefix("manabubble_blacklist"));

        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider) {
            tag(BUBBLE_BLACKLIST).add(MobEffects.LEVITATION, ModPotions.HYMN_OF_ORDER.get(), SUMMONING_SICKNESS_EFFECT.get());
            tag(PotionEffectTags.TO_SYNC).add(ModPotions.MAGIC_FIRE.get(), ModPotions.VENOM.get());
        }

    }

    public static class AEDamageTypeProvider extends DamageTypeTagsProvider {

        public AEDamageTypeProvider(DataGenerator pGenerator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator.getPackOutput(), provider, MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {

            tag(ModRegistry.FIRE_DAMAGE).addTag(DamageTypeTags.IS_FIRE).add(
                            DamageTypes.DRAGON_BREATH,
                            DamageTypes.EXPLOSION,
                            DamageTypes.PLAYER_EXPLOSION,
                            DamageTypes.FIREWORKS)
                    .addOptional(DamageTypesRegistry.FLARE.location())
                    .addOptional(ModRegistry.MAGIC_FIRE.location());

            tag(ModRegistry.WATER_DAMAGE).addTag(DamageTypeTags.IS_FREEZING).addTag(DamageTypeTags.IS_DROWNING).add(
                            DamageTypes.TRIDENT,
                            DamageTypes.MAGIC)
                    .addOptional(DamageTypesRegistry.COLD_SNAP.location());

            tag(ModRegistry.EARTH_DAMAGE).add(DamageTypes.FALLING_BLOCK,
                            DamageTypes.FALLING_STALACTITE,
                            DamageTypes.CACTUS,
                            DamageTypes.FALLING_ANVIL,
                            DamageTypes.STING,
                            DamageTypes.SWEET_BERRY_BUSH)
                    .addOptional(DamageTypesRegistry.CRUSH.location())
                    .addOptional(ModRegistry.POISON.location());

            tag(ModRegistry.AIR_DAMAGE).addTag(DamageTypeTags.IS_LIGHTNING).add(DamageTypes.FALL,
                            DamageTypes.FLY_INTO_WALL,
                            DamageTypes.SONIC_BOOM)
                    .addOptional(ModRegistry.SPARK.location())
                    .addOptional(DamageTypesRegistry.WINDSHEAR.location());

        }
    }
}
