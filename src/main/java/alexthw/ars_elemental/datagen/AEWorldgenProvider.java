package alexthw.ars_elemental.datagen;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.registry.ModEntities;
import alexthw.ars_elemental.world.ModWorldgen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static alexthw.ars_elemental.ArsElemental.prefix;
import static alexthw.ars_elemental.world.ModWorldgen.COMMON_FLASHING_CONFIGURED;

public class AEWorldgenProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModWorldgen::bootstrapConfiguredFeatures)
            .add(Registries.PLACED_FEATURE, ModWorldgen::bootstrapPlacedFeatures)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AEWorldgenProvider::generateBiomeModifiers)
            .add(Registries.BIOME, ModWorldgen.Biomes::registerBiomes);

    public AEWorldgenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ArsElemental.MODID));
    }

    public static final ResourceKey<BiomeModifier> SIREN_SPAWN = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, prefix("siren_spawns"));
    public static final ResourceKey<BiomeModifier> COMMON_FLASHING_MODIFIER = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModWorldgen.COMMON_FLASHING_CONFIGURED.location());

    public static void generateBiomeModifiers(BootstrapContext<BiomeModifier> context) {

        HolderSet<Biome> OVERWORLD_TAG = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD);
        HolderSet<Biome> COMMON_FLASH_ARCHWOOD_TAG = context.lookup(Registries.BIOME).getOrThrow(AETagsProvider.AEBiomeTagsProvider.FLASHING_TREE_COMMON_BIOME);
        HolderSet<Biome> SIREN_SPAWN_TAG = context.lookup(Registries.BIOME).getOrThrow(AETagsProvider.AEBiomeTagsProvider.SIREN_SPAWN_TAG);

        context.register(SIREN_SPAWN, BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(SIREN_SPAWN_TAG, new MobSpawnSettings.SpawnerData(ModEntities.SIREN_ENTITY.get(),
                3, 1, 3)));

        try {
            Holder.Reference<PlacedFeature> TREESET_CMN = context.lookup(Registries.PLACED_FEATURE).get(COMMON_FLASHING_CONFIGURED).orElseThrow();
            context.register(COMMON_FLASHING_MODIFIER, new BiomeModifiers.AddFeaturesBiomeModifier(COMMON_FLASH_ARCHWOOD_TAG, HolderSet.direct(TREESET_CMN), GenerationStep.Decoration.VEGETAL_DECORATION));
        } catch (Exception ignored) {
        }
    }


}
