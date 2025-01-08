package alexthw.ars_elemental.world;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.ConfigHandler;
import com.hollingsworth.arsnouveau.common.world.biome.ArchwoodRegion;
import com.hollingsworth.arsnouveau.setup.registry.BiomeRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import terrablender.api.ParameterUtils;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

public class TerrablenderAE {
    public static void registerBiomes() {
        Regions.register(new ArchwoodRegion(ResourceLocation.fromNamespaceAndPath(ArsElemental.MODID, "overworld"), ConfigHandler.Common.EXTRA_BIOMES.get()) {
            @Override
            public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
                VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

                // Classic Archwood Forest is a biome where the elements are balanced, it's the most common biome in the region
                new ParameterUtils.ParameterPointListBuilder()
                        .temperature(ParameterUtils.Temperature.COOL, ParameterUtils.Temperature.NEUTRAL, ParameterUtils.Temperature.WARM)
                        .humidity(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET)
                        .continentalness(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND)
                        .erosion(ParameterUtils.Erosion.FULL_RANGE)
                        .depth(ParameterUtils.Depth.SURFACE)
                        .weirdness(Climate.Parameter.span(-0.4F, 0.4F))
                        .build().forEach(point -> builder.add(point, BiomeRegistry.ARCHWOOD_FOREST));


                // Cascading Forest is a biome where water and cold elements are dominant, higher weirdness causes waterfalls to spawn more frequently
                new ParameterUtils.ParameterPointListBuilder()
                        .temperature(ParameterUtils.Temperature.ICY, ParameterUtils.Temperature.COOL, ParameterUtils.Temperature.NEUTRAL, ParameterUtils.Temperature.WARM)
                        .humidity(ParameterUtils.Humidity.HUMID, ParameterUtils.Humidity.WET, ParameterUtils.Humidity.NEUTRAL)
                        .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.OCEAN, ParameterUtils.Continentalness.FAR_INLAND))
                        .erosion(ParameterUtils.Erosion.FULL_RANGE)
                        .depth(ParameterUtils.Depth.SURFACE)
                        .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                        .build().forEach(point -> builder.add(point, ModWorldgen.Biomes.CASCADING_FOREST_KEY));

                // Flashing Forest is a biome on high peaks thanks to higher weirdness, more frequent lightning strikes during storms
                new ParameterUtils.ParameterPointListBuilder()
                        .temperature(ParameterUtils.Temperature.FULL_RANGE)
                        .humidity(ParameterUtils.Humidity.FULL_RANGE)
                        .continentalness(ParameterUtils.Continentalness.MID_INLAND, ParameterUtils.Continentalness.FAR_INLAND)
                        .erosion(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1, ParameterUtils.Erosion.EROSION_2)
                        .depth(ParameterUtils.Depth.SURFACE)
                        .weirdness(Climate.Parameter.span(-1.0F, -0.4F), Climate.Parameter.span(0.4F, 1.0F))
                        .build().forEach(point -> builder.add(point, ModWorldgen.Biomes.FLASHING_FOREST_KEY));

                // Blazing Forest is a biome where heat and dryness are dominant, more frequent fires and lava pools
                new ParameterUtils.ParameterPointListBuilder()
                        .temperature(ParameterUtils.Temperature.NEUTRAL, ParameterUtils.Temperature.WARM, ParameterUtils.Temperature.HOT)
                        .humidity(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.NEUTRAL)
                        .continentalness(ParameterUtils.Continentalness.MID_INLAND, ParameterUtils.Continentalness.FAR_INLAND)
                        .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_5))
                        .depth(ParameterUtils.Depth.SURFACE)
                        .weirdness(Climate.Parameter.span(-0.56666666F, 0.56666666F))
                        .build().forEach(point -> builder.add(point, ModWorldgen.Biomes.BLAZING_FOREST_KEY));

                // Flourishing Forest is a biome where warmth and humidity are dominant, more frequent flowers and lush vegetation
                new ParameterUtils.ParameterPointListBuilder()
                        .temperature(ParameterUtils.Temperature.COOL, ParameterUtils.Temperature.NEUTRAL, ParameterUtils.Temperature.WARM)
                        .humidity(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID)
                        .continentalness(ParameterUtils.Continentalness.INLAND)
                        .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_4))
                        .depth(Climate.Parameter.span(0.0F, 0.4F))
                        .weirdness(Climate.Parameter.span(-0.4F, 0.4F))
                        .build().forEach(point -> builder.add(point, ModWorldgen.Biomes.FLOURISHING_FOREST_KEY));

                // Add our points to the mapper
                builder.build().forEach(mapper);
            }
        });
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, ArsElemental.MODID, makeRules());
    }

    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.ConditionSource aboveOrHighUnderground = SurfaceRules.yBlockCheck(VerticalAnchor.aboveBottom(64 + 16), 0);
        SurfaceRules.RuleSource grassSurface = SurfaceRules.sequence(
                SurfaceRules.ifTrue(aboveOrHighUnderground, SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, GRASS_BLOCK)),
                SurfaceRules.ifTrue(aboveOrHighUnderground, SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)));
        return SurfaceRules.ifTrue(SurfaceRules.isBiome(ModWorldgen.Biomes.FLOURISHING_FOREST_KEY), grassSurface);
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}