package alexthw.ars_elemental.world;

import alexthw.ars_elemental.common.entity.mages.EntityMageBase;
import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.util.SupplierBlockStateProviderAE;
import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
import com.hollingsworth.arsnouveau.setup.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static alexthw.ars_elemental.ArsElemental.MODID;
import static alexthw.ars_elemental.ArsElemental.prefix;
import static alexthw.ars_elemental.registry.ModEntities.*;
import static com.hollingsworth.arsnouveau.setup.registry.BiomeRegistry.globalOverworldGeneration;
import static com.hollingsworth.arsnouveau.setup.registry.BiomeRegistry.softDisks;


@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ModWorldgen {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, MODID);

    public static final String SIMPLE_FLASHING_ID = "simple_flashing";
    public static final String COMMON_FLASHING_ID = "common_flashing";

    public static final String RARE_RANDOM_FLASHING_ID = "random_simple_flashing";
    public static final String COMMON_RANDOM_FLASHING_ID = "random_common_flashing";

    public static final String FINAL_RARE_FLASHING = "flashing_archwood";
    public static final String FINAL_COMMON_FLASHING = "common_flashing_archwood";
    public static final String FINAL_CLUSTER_FLASHING = "cluster_flashing_archwood";
    public static final String FINAL_CLUSTER_CASCADING = "cluster_cascading_archwood";
    public static final String FINAL_CLUSTER_BLAZING = "cluster_blazing_archwood";
    public static final String FINAL_CLUSTER_FLOURISHING = "cluster_flourishing_archwood";
    public static final String FINAL_CLUSTER_VEXING = "cluster_vexing_archwood";


    public static class Biomes {

        public static final String FLASHING_FOREST_ID = "flashing_forest";
        public static final String BLAZING_FOREST_ID = "blazing_forest";
        public static final String CASCADING_FOREST_ID = "cascading_forest";
        public static final String FLOURISHING_FOREST_ID = "flourishing_forest";
        public static final ResourceKey<Biome> FLASHING_FOREST_KEY = register(FLASHING_FOREST_ID);
        public static final ResourceKey<Biome> BLAZING_FOREST_KEY = register(BLAZING_FOREST_ID);
        public static final ResourceKey<Biome> CASCADING_FOREST_KEY = register(CASCADING_FOREST_ID);
        public static final ResourceKey<Biome> FLOURISHING_FOREST_KEY = register(FLOURISHING_FOREST_ID);

        public static final ResourceLocation[] ArchwoodBiomes = new ResourceLocation[]{
                FLASHING_FOREST_KEY.location(),
                BLAZING_FOREST_KEY.location(),
                CASCADING_FOREST_KEY.location(),
                FLOURISHING_FOREST_KEY.location()
        };

        public static ResourceKey<Biome> register(String name) {
            return ResourceKey.create(Registries.BIOME, prefix(name));
        }

        public static void registerBiomes(BootstrapContext<Biome> context) {

            BiomeRegistry.bootstrap(context);

            context.register(FLASHING_FOREST_KEY, flashingArchwoodForest(context));
            context.register(BLAZING_FOREST_KEY, blazingArchwoodForest(context));
            context.register(CASCADING_FOREST_KEY, cascadingArchwoodForest(context));
            context.register(FLOURISHING_FOREST_KEY, flourishArchwoodForest(context));

        }

        public static Biome flashingArchwoodForest(BootstrapContext<Biome> context) {
            MobSpawnSettings.Builder spawnBuilder = archwoodSpawns(AIR_MAGE.get(), FLASHING_WEALD_WALKER.get(), null);
            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.BREEZE, 5, 1, 1));
            spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 6, 1, 2));
            BiomeGenerationSettings.Builder biomeBuilder = getArchwoodBiomeBuilder(CLUSTER_FLASHING_CONFIGURED, context, QUARTZ_ROCK_PLACED, VegetationPlacements.TREES_WINDSWEPT_HILLS);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, SCATTERED_SPARKFLOWERS);

            return new Biome.BiomeBuilder()
                    .hasPrecipitation(true)
                    .downfall(0.8f)
                    .temperature(0.4f)
                    .generationSettings(biomeBuilder.build())
                    .mobSpawnSettings(spawnBuilder.build())
                    .specialEffects((new BiomeSpecialEffects.Builder())
                            .waterColor(7978751)
                            .waterFogColor(329011)
                            .skyColor(7978751)
                            .grassColorOverride(13414701)
                            .foliageColorOverride(13084948)
                            .fogColor(12638463)
                            .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                            .backgroundMusic(Musics.createGameMusic(SoundRegistry.ARIA_BIBLIO)).build())
                    .build();
        }

        private static Biome blazingArchwoodForest(BootstrapContext<Biome> context) {
            MobSpawnSettings.Builder spawnBuilder = archwoodSpawns(FIRE_MAGE.get(), ModEntities.ENTITY_BLAZING_WEALD.get(), EntityType.HUSK);
            spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.ARMADILLO, 6, 1, 2));

            BiomeGenerationSettings.Builder biomeBuilder = getArchwoodBiomeBuilder(CLUSTER_BLAZING_CONFIGURED, context, BLACKSTONE_ROCK_PLACED, VegetationPlacements.TREES_WINDSWEPT_SAVANNA);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, SCATTERED_TORCHFLOWERS);
            biomeBuilder.addFeature(GenerationStep.Decoration.LAKES, LAVA_POOLS);

            return new Biome.BiomeBuilder().hasPrecipitation(false)
                    .downfall(0.1f)
                    .temperature(0.9f)
                    .generationSettings(biomeBuilder.build())
                    .mobSpawnSettings(spawnBuilder.build())
                    .specialEffects((new BiomeSpecialEffects.Builder())
                            .waterColor(7978751)
                            .waterFogColor(329011)
                            .skyColor(7978751)
                            .grassColorOverride(13269556)
                            .foliageColorOverride(12679744)
                            .fogColor(12638463)
                            .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                            .backgroundMusic(Musics.createGameMusic(SoundRegistry.ARIA_BIBLIO)).build())
                    .build();

        }

        private static Biome cascadingArchwoodForest(BootstrapContext<Biome> context) {
            MobSpawnSettings.Builder spawnBuilder = archwoodSpawns(WATER_MAGE.get(), ModEntities.ENTITY_CASCADING_WEALD.get(), EntityType.DROWNED);
            // add fish
            spawnBuilder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.COD, 15, 3, 6));
            spawnBuilder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5));
            spawnBuilder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
            spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 6, 1, 3));

            BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
            //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
            globalOverworldGeneration(biomeBuilder);
            BiomeDefaultFeatures.addForestFlowers(biomeBuilder);
            BiomeDefaultFeatures.addFerns(biomeBuilder);
            BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
            BiomeDefaultFeatures.addExtraGold(biomeBuilder);
            softDisks(biomeBuilder);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, LESS_MANGROVE_PLACED);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, POOLS_WITH_DRIP_PLACED);
            BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.WARM_OCEAN_VEGETATION)
                    .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_WARM)
                    .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEA_PICKLE);

            biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, WorldgenRegistry.PLACED_LIGHTS);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CLUSTER_CASCADING_CONFIGURED);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldgenRegistry.PLACED_MOJANK_GRASS);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldgenRegistry.PLACED_MOJANK_FLOWERS);

            return new Biome.BiomeBuilder().hasPrecipitation(true)
                    .downfall(0.8f)
                    .temperature(0.7f)
                    .generationSettings(biomeBuilder.build())
                    .mobSpawnSettings(spawnBuilder.build())
                    .specialEffects((new BiomeSpecialEffects.Builder())
                            .waterColor(7978751)
                            .waterFogColor(329011)
                            .skyColor(7978751)
                            .grassColorOverride(1149867)
                            .foliageColorOverride(2210437)
                            .fogColor(12638463)
                            .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                            .backgroundMusic(Musics.createGameMusic(SoundRegistry.ARIA_BIBLIO)).build())
                    .build();
        }

        private static Biome flourishArchwoodForest(BootstrapContext<Biome> context) {
            MobSpawnSettings.Builder spawnBuilder = archwoodSpawns(EARTH_MAGE.get(), ModEntities.ENTITY_FLOURISHING_WEALD.get(), EntityType.BOGGED);
            spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 6, 1, 3));

            BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
            //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
            globalOverworldGeneration(biomeBuilder);
            BiomeDefaultFeatures.addMossyStoneBlock(biomeBuilder);
            BiomeDefaultFeatures.addForestFlowers(biomeBuilder);
            BiomeDefaultFeatures.addFerns(biomeBuilder);
            BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
            BiomeDefaultFeatures.addExtraGold(biomeBuilder);
            BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
            BiomeDefaultFeatures.addLightBambooVegetation(biomeBuilder);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CLUSTER_FLOURISHING_CONFIGURED);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, SPARSE_JUNGLE);
            BiomeDefaultFeatures.addWarmFlowers(biomeBuilder);
            BiomeDefaultFeatures.addJungleGrass(biomeBuilder);
            BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
            BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.LUSH_CAVES_CEILING_VEGETATION);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.CAVE_VINES);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.ROOTED_AZALEA_TREE);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.SPORE_BLOSSOM);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.CLASSIC_VINES);
            biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, WorldgenRegistry.PLACED_LIGHTS);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldgenRegistry.PLACED_MOJANK_GRASS);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldgenRegistry.PLACED_MOJANK_FLOWERS);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, SCATTERED_BLOSSOMS);
            return new Biome.BiomeBuilder()
                    .hasPrecipitation(true)
                    .downfall(0.6f)
                    .temperature(0.7f)
                    .generationSettings(biomeBuilder.build())
                    .mobSpawnSettings(spawnBuilder.build())
                    .specialEffects((new BiomeSpecialEffects.Builder())
                            .waterColor(7978751)
                            .waterFogColor(329011)
                            .skyColor(7978751)
                            .grassColorOverride(1346066)
                            .foliageColorOverride(30464)
                            .fogColor(12638463)
                            .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                            .backgroundMusic(Musics.createGameMusic(SoundRegistry.ARIA_BIBLIO)).build())
                    .build();
        }

        private static MobSpawnSettings.Builder archwoodSpawns(EntityType<EntityMageBase> mage, EntityType<WealdWalker> ww, EntityType<?> biomeMob) {
            MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
            spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE.get(), 2, 3, 5));
            spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_DRYGMY.get(), 2, 1, 3));
            spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.WHIRLISPRIG_TYPE.get(), 2, 1, 3));
            BiomeDefaultFeatures.farmAnimals(spawnBuilder);
            spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 6, 2, 3));

            BiomeDefaultFeatures.caveSpawns(spawnBuilder);
            // unwrap the monster method so we can override the biome-specific dominant mob
            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 60, 4, 4));
            if (biomeMob != null)
                spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(biomeMob, 60, 2, 4));
            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 20, 4, 4));
            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 60, 4, 4));
            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 60, 4, 4));
            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 40, 4, 4));
            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.WITCH, 5, 1, 1));

            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(mage, 4, 1, 3));
            spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ww, 10, 1, 3));
            return spawnBuilder;
        }

        private static BiomeGenerationSettings.Builder getArchwoodBiomeBuilder(ResourceKey<PlacedFeature> archwoodCluster, BootstrapContext<Biome> context, ResourceKey<PlacedFeature> rock, ResourceKey<PlacedFeature> vanillatree) {
            BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
            //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
            globalOverworldGeneration(biomeBuilder);
            biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, rock);
            BiomeDefaultFeatures.addForestFlowers(biomeBuilder);
            BiomeDefaultFeatures.addFerns(biomeBuilder);
            BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
            BiomeDefaultFeatures.addExtraGold(biomeBuilder);
            softDisks(biomeBuilder);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, vanillatree);

            BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
            BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder);
            biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, WorldgenRegistry.PLACED_LIGHTS);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, archwoodCluster);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldgenRegistry.PLACED_MOJANK_GRASS);
            biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldgenRegistry.PLACED_MOJANK_FLOWERS);
            return biomeBuilder;
        }

    }

    public static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);

        WorldgenRegistry.bootstrapConfiguredFeatures(context);

        context.register(FLASHING_TREE_SAPLING, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                new SupplierBlockStateProviderAE("yellow_archwood_log"),
                new MagicTrunkPlacer(10, 1, 0, false, "ars_elemental:flashpine_pod"),
                new SupplierBlockStateProviderAE("yellow_archwood_leaves"),
                new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0, 0), 0),
                new TwoLayersFeatureSize(2, 0, 2)).ignoreVines().build()));

        context.register(NATURAL_FLASHING_TREE, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                new SupplierBlockStateProviderAE("yellow_archwood_log"),
                new MagicTrunkPlacer(10, 2, 0, true, "ars_elemental:flashpine_pod"),
                new SupplierBlockStateProviderAE("yellow_archwood_leaves"),
                new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0, 0), 0),
                new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(RARE_FLASHING_TREES, new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(placed.getOrThrow(SIMPLE_FLASHING_PLACED)))));
        context.register(COMMON_FLASHING_TREES, new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(placed.getOrThrow(COMMON_FLASHING_PLACED)))));

        context.register(POOLS_WITH_DRIP, new ConfiguredFeature<>(Feature.WATERLOGGED_VEGETATION_PATCH, new VegetationPatchConfiguration(BlockTags.LUSH_GROUND_REPLACEABLE, new WeightedStateProvider(
                SimpleWeightedRandomList.<BlockState>builder()
                        .add(Blocks.GRASS_BLOCK.defaultBlockState(), 75)
                        .add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5)
                        .add(Blocks.WATER.defaultBlockState(), 20)
                        .build()
        ), PlacementUtils.inlinePlaced(holdergetter.getOrThrow(CaveFeatures.DRIPLEAF)), CaveSurface.FLOOR, ConstantInt.of(3), 0.8F, 3, 0.1F, UniformInt.of(4, 7), 0.4F)));

        context.register(BLACKSTONE_ROCK, new ConfiguredFeature<>(BLACKSTONE_SPIKE.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(QUARTZ_ROCK, new ConfiguredFeature<>(QUARTZ_SPIKE.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(SINGLE_TORCHFLOWER, new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.TORCHFLOWER.defaultBlockState()))
        ));
        context.register(SINGLE_SPARKFLOWER, new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(ModItems.SPARKFLOWER.get().defaultBlockState()))
        ));
        context.register(SINGLE_BLOSSOM, new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(ModItems.GROUND_BLOSSOM.get().defaultBlockState()))
        ));

    }

    public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        WorldgenRegistry.bootstrapPlacedFeatures(context);

        context.register(SIMPLE_FLASHING_PLACED, new PlacedFeature(configured.get(NATURAL_FLASHING_TREE).get(), List.of(PlacementUtils.filteredByBlockSurvival(ModItems.FLASHING_SAPLING.get()))));
        context.register(COMMON_FLASHING_PLACED, new PlacedFeature(configured.get(NATURAL_FLASHING_TREE).get(), List.of(PlacementUtils.countExtra(5, 0.01F, 1), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(ModItems.FLASHING_SAPLING.get()))));

        context.register(RARE_FLASHING_CONFIGURED, new PlacedFeature(configured.get(RARE_FLASHING_TREES).get(), VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(200))));
        context.register(COMMON_FLASHING_CONFIGURED, new PlacedFeature(configured.get(COMMON_FLASHING_TREES).get(), VegetationPlacements.treePlacement(CountPlacement.of(3), ModItems.FLASHING_SAPLING.get())));

        context.register(CLUSTER_FLASHING_CONFIGURED, new PlacedFeature(configured.get(COMMON_FLASHING_TREES).get(), VegetationPlacements.treePlacement(CountPlacement.of(4), ModItems.FLASHING_SAPLING.get())));

        context.register(CLUSTER_CASCADING_CONFIGURED, new PlacedFeature(configured.get(WorldgenRegistry.NATURAL_CONFIGURED_CASCADING_TREE).get(), VegetationPlacements.treePlacement(CountPlacement.of(4), BlockRegistry.CASCADING_SAPLING.get())));
        context.register(CLUSTER_BLAZING_CONFIGURED, new PlacedFeature(configured.get(WorldgenRegistry.NATURAL_CONFIGURED_BLAZING_TREE).get(), VegetationPlacements.treePlacement(CountPlacement.of(4), BlockRegistry.BLAZING_SAPLING.get())));
        context.register(CLUSTER_FLOURISHING_CONFIGURED, new PlacedFeature(configured.get(WorldgenRegistry.NATURAL_CONFIGURED_FLOURISHING_TREE).get(), VegetationPlacements.treePlacement(CountPlacement.of(4), BlockRegistry.FLOURISHING_SAPLING.get())));
        context.register(CLUSTER_VEXING_CONFIGURED, new PlacedFeature(configured.get(WorldgenRegistry.NATURAL_CONFIGURED_VEXING_TREE).get(), VegetationPlacements.treePlacement(CountPlacement.of(4), BlockRegistry.VEXING_SAPLING.get())));

        context.register(LESS_MANGROVE_PLACED, new PlacedFeature(configured.get(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.withDefaultNamespace("mangrove_vegetation"))).get(),
                        List.of(new PlacementModifier[]{
                                        CountPlacement.of(5),
                                        InSquarePlacement.spread(),
                                        SurfaceWaterDepthFilter.forMaxDepth(5),
                                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                                        BiomeFilter.biome(),
                                        BlockPredicateFilter.forPredicate(
                                                BlockPredicate.allOf(
                                                        BlockPredicate.wouldSurvive(Blocks.MANGROVE_PROPAGULE.defaultBlockState(), BlockPos.ZERO),
                                                        BlockPredicate.matchesFluids(Fluids.WATER)
                                                )
                                        )
                                }
                        )
                )
        );

        context.register(POOLS_WITH_DRIP_PLACED, new PlacedFeature(configured.get(POOLS_WITH_DRIP).get(), List.of(new PlacementModifier[]{
                CountPlacement.of(15),
                InSquarePlacement.spread(),
                PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12),
                RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                BiomeFilter.biome()
        })));

        context.register(BLACKSTONE_ROCK_PLACED, new PlacedFeature(configured.get(BLACKSTONE_ROCK).get(), List.of(new PlacementModifier[]{
                RarityFilter.onAverageOnceEvery(10),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome()
        })));

        context.register(QUARTZ_ROCK_PLACED, new PlacedFeature(configured.get(QUARTZ_ROCK).get(), List.of(new PlacementModifier[]{
                RarityFilter.onAverageOnceEvery(5),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome()
        })));

        context.register(SCATTERED_TORCHFLOWERS, new PlacedFeature(
                configured.get(SINGLE_TORCHFLOWER).get(),
                List.of(
                        CountPlacement.of(2),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()
                )
        ));

        context.register(SCATTERED_SPARKFLOWERS, new PlacedFeature(
                configured.get(SINGLE_SPARKFLOWER).get(),
                List.of(
                        CountPlacement.of(4),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()
                )
        ));

        context.register(SCATTERED_BLOSSOMS, new PlacedFeature(
                configured.get(SINGLE_BLOSSOM).get(),
                List.of(
                        CountPlacement.of(3),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()
                )
        ));

        context.register(LAVA_POOLS, new PlacedFeature(
                configured.get(MiscOverworldFeatures.LAKE_LAVA).get(),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()
                )
        ));

        context.register(SPARSE_JUNGLE, new PlacedFeature(configured.get(VegetationFeatures.TREES_JUNGLE).get(), VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1F, 1))));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerConfKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    public static ResourceKey<PlacedFeature> registerPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    public static final ResourceKey<ConfiguredFeature<?, ?>> FLASHING_TREE_SAPLING = registerConfKey("flashing_tree_sapling");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NATURAL_FLASHING_TREE = registerConfKey("natural_flashing_tree");

    public static final ResourceKey<PlacedFeature> SIMPLE_FLASHING_PLACED = registerPlacedKey(SIMPLE_FLASHING_ID);
    public static final ResourceKey<PlacedFeature> COMMON_FLASHING_PLACED = registerPlacedKey(COMMON_FLASHING_ID);
    public static final ResourceKey<PlacedFeature> LESS_MANGROVE_PLACED = registerPlacedKey("less_trees_mangrove");

    public static final ResourceKey<ConfiguredFeature<?, ?>> RARE_FLASHING_TREES = registerConfKey(RARE_RANDOM_FLASHING_ID);
    public static final ResourceKey<ConfiguredFeature<?, ?>> COMMON_FLASHING_TREES = registerConfKey(COMMON_RANDOM_FLASHING_ID);

    public static final ResourceKey<PlacedFeature> RARE_FLASHING_CONFIGURED = registerPlacedKey(FINAL_RARE_FLASHING);
    public static final ResourceKey<PlacedFeature> COMMON_FLASHING_CONFIGURED = registerPlacedKey(FINAL_COMMON_FLASHING);
    public static final ResourceKey<PlacedFeature> CLUSTER_FLASHING_CONFIGURED = registerPlacedKey(FINAL_CLUSTER_FLASHING);

    public static final ResourceKey<PlacedFeature> CLUSTER_BLAZING_CONFIGURED = registerPlacedKey(FINAL_CLUSTER_BLAZING);
    public static final ResourceKey<PlacedFeature> CLUSTER_CASCADING_CONFIGURED = registerPlacedKey(FINAL_CLUSTER_CASCADING);
    public static final ResourceKey<PlacedFeature> CLUSTER_FLOURISHING_CONFIGURED = registerPlacedKey(FINAL_CLUSTER_FLOURISHING);
    public static final ResourceKey<PlacedFeature> CLUSTER_VEXING_CONFIGURED = registerPlacedKey(FINAL_CLUSTER_VEXING);

    public static final ResourceKey<ConfiguredFeature<?, ?>> POOLS_WITH_DRIP = registerConfKey("pools_with_drip");
    public static final ResourceKey<PlacedFeature> POOLS_WITH_DRIP_PLACED = registerPlacedKey("pools_with_drip_placed");

    public static final ResourceKey<ConfiguredFeature<?, ?>> BLACKSTONE_ROCK = registerConfKey("blackstone_rock");
    public static final ResourceKey<PlacedFeature> BLACKSTONE_ROCK_PLACED = registerPlacedKey("blackstone_rock_placed");
    public static final DeferredHolder<Feature<?>, BlackstoneFormation> BLACKSTONE_SPIKE = FEATURES.register("blackstone_formation", () -> new BlackstoneFormation(NoneFeatureConfiguration.CODEC));

    public static final ResourceKey<ConfiguredFeature<?, ?>> QUARTZ_ROCK = registerConfKey("quartz_rock");
    public static final ResourceKey<PlacedFeature> QUARTZ_ROCK_PLACED = registerPlacedKey("quartz_rock_placed");
    public static final DeferredHolder<Feature<?>, QuartzSpikeFeature> QUARTZ_SPIKE = FEATURES.register("quartz_spike", () -> new QuartzSpikeFeature(NoneFeatureConfiguration.CODEC));

    public static final ResourceKey<ConfiguredFeature<?, ?>> SINGLE_TORCHFLOWER = registerConfKey("single_torchflower");
    public static final ResourceKey<PlacedFeature> SCATTERED_TORCHFLOWERS = registerPlacedKey("scattered_torchflowers");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SINGLE_SPARKFLOWER = registerConfKey("single_sparkflower");
    public static final ResourceKey<PlacedFeature> SCATTERED_SPARKFLOWERS = registerPlacedKey("scattered_sparkflowers");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SINGLE_BLOSSOM = registerConfKey("single_blossom");
    public static final ResourceKey<PlacedFeature> SCATTERED_BLOSSOMS = registerPlacedKey("scattered_blossoms");

    public static final ResourceKey<PlacedFeature> LAVA_POOLS = registerPlacedKey("lava_pools");
    public static final ResourceKey<PlacedFeature> SPARSE_JUNGLE = registerPlacedKey("sparse_jungle_trees");

}
