package alexthw.ars_elemental.datagen;

import alexthw.ars_elemental.common.glyphs.*;
import alexthw.ars_elemental.common.glyphs.filters.*;
import alexthw.ars_elemental.registry.ModItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.datagen.GlyphRecipeProvider;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;


public class AEGlyphProvider extends GlyphRecipeProvider {

    public AEGlyphProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }


    @Override
    public void collectJsons(CachedOutput cache) {

        addRecipe(EffectConjureTerrain.INSTANCE, ItemsRegistry.EARTH_ESSENCE, Items.DIRT);
        addRecipe(EffectWaterGrave.INSTANCE, Items.KELP, Items.PRISMARINE_SHARD, ItemsRegistry.WATER_ESSENCE);
        addRecipe(EffectBubbleShield.INSTANCE, Items.HEART_OF_THE_SEA, Items.PRISMARINE_SHARD, BlockRegistry.BASTION_POD.asItem(), ItemsRegistry.WATER_ESSENCE);
        addRecipe(EffectSpores.INSTANCE, Items.SPORE_BLOSSOM, Items.RED_MUSHROOM, ItemsRegistry.EARTH_ESSENCE);
        addRecipe(EffectDischarge.INSTANCE, Items.LIGHTNING_ROD, ModItems.FLASHING_POD.get().asItem(), ItemsRegistry.AIR_ESSENCE);
        recipes.add(get(EffectSpark.INSTANCE).withItem(ItemsRegistry.AIR_ESSENCE).withIngredient(Ingredient.of(ItemTags.WOOL)).withItem(Items.IRON_BARS));
        addRecipe(EffectCharm.INSTANCE, ModItems.ANIMA_ESSENCE.get(), Items.GOLDEN_CARROT, ItemsRegistry.SOURCE_BERRY_PIE, Blocks.CAKE);
        addRecipe(EffectLifeLink.INSTANCE, Items.LEAD, ModItems.ANIMA_ESSENCE.get(), Items.SCULK_SENSOR);
        addRecipe(EffectPhantom.INSTANCE, Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, ModItems.ANIMA_ESSENCE.get());
        addRecipe(EffectConflagrate.INSTANCE, Items.GUNPOWDER, ItemsRegistry.FIRE_ESSENCE, BlockRegistry.BOMBEGRANTE_POD.asItem(), Items.NETHERITE_SCRAP);

        addRecipe(EffectSpike.INSTANCE, Items.POINTED_DRIPSTONE, Items.NETHERITE_INGOT, ItemsRegistry.EARTH_ESSENCE);
        addRecipe(EffectEnvenom.INSTANCE, Items.POISONOUS_POTATO, Items.FERMENTED_SPIDER_EYE, Items.SUSPICIOUS_STEW);

        addRecipe(MethodArcProjectile.INSTANCE, Items.ARROW, Items.SNOWBALL, Items.SLIME_BALL, Items.ENDER_PEARL);
        addRecipe(MethodHomingProjectile.INSTANCE, Items.NETHER_STAR, ItemsRegistry.MANIPULATION_ESSENCE, ItemsRegistry.DOWSING_ROD, Items.ENDER_EYE);

        addRecipe(PropagatorArc.INSTANCE, ItemsRegistry.MANIPULATION_ESSENCE, MethodArcProjectile.INSTANCE.getGlyph());
        addRecipe(PropagatorHoming.INSTANCE, ItemsRegistry.MANIPULATION_ESSENCE, MethodHomingProjectile.INSTANCE.getGlyph());

        recipes.add(get(AquaticFilter.INSTANCE).withItem(ItemsRegistry.ALLOW_ITEM_SCROLL).withIngredient(Ingredient.of(ItemTags.FISHES)));
        recipes.add(get(AquaticFilter.NOT_INSTANCE).withItem(ItemsRegistry.DENY_ITEM_SCROLL).withIngredient(Ingredient.of(ItemTags.FISHES)));

        addRecipe(AerialFilter.INSTANCE, ItemsRegistry.ALLOW_ITEM_SCROLL, Items.PHANTOM_MEMBRANE);
        addRecipe(AerialFilter.NOT_INSTANCE, ItemsRegistry.DENY_ITEM_SCROLL, Items.PHANTOM_MEMBRANE);

        addRecipe(FieryFilter.INSTANCE, ItemsRegistry.ALLOW_ITEM_SCROLL, Items.BLAZE_POWDER);
        addRecipe(FieryFilter.NOT_INSTANCE, ItemsRegistry.DENY_ITEM_SCROLL, Items.BLAZE_POWDER);

        addRecipe(UndeadFilter.INSTANCE, ItemsRegistry.ALLOW_ITEM_SCROLL, Items.ROTTEN_FLESH);
        addRecipe(UndeadFilter.NOT_INSTANCE, ItemsRegistry.DENY_ITEM_SCROLL, Items.ROTTEN_FLESH);

        addRecipe(SummonFilter.INSTANCE, ItemsRegistry.ALLOW_ITEM_SCROLL, Items.BONE);
        addRecipe(SummonFilter.NOT_INSTANCE, ItemsRegistry.DENY_ITEM_SCROLL, Items.BONE);

        addRecipe(InsectFilter.INSTANCE, ItemsRegistry.ALLOW_ITEM_SCROLL, Items.SPIDER_EYE);
        addRecipe(InsectFilter.NOT_INSTANCE, ItemsRegistry.DENY_ITEM_SCROLL, Items.SPIDER_EYE);


        for (GlyphRecipe recipe : recipes) {
            Path path = getScribeGlyphPath(output, recipe.output.getItem());
            saveStable(cache, GlyphRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).getOrThrow(), path);
        }

        var conditionalRecipe = GlyphRecipe.CODEC.encodeStart(JsonOps.INSTANCE, addRecipe(EffectNullify.INSTANCE, Items.NETHER_STAR, ModItems.MARK_OF_MASTERY, Items.NETHERITE_BLOCK, ModItems.MARK_OF_MASTERY)).getOrThrow();
        // wrap conditionalRecipe in a ConfigCondition
        var condition = new JsonObject();
        condition.addProperty("config", "frame_skip_recipe");
        condition.addProperty("type", "ars_elemental:config");
        var array = new JsonArray();
        array.add(condition);
        conditionalRecipe.getAsJsonObject().add("neoforge:conditions", array);

        saveStable(cache, conditionalRecipe, getScribeGlyphPath(output, EffectNullify.INSTANCE.getGlyph()));

    }

    public GlyphRecipe addRecipe(AbstractSpellPart part, ItemLike... items) {
        var builder = get(part);
        for (ItemLike item : items) {
            builder.withItem(item);
        }
        recipes.add(builder);
        return builder;
    }

    protected static Path getScribeGlyphPath(Path pathIn, Item glyph) {
        return pathIn.resolve("data/ars_elemental/recipe/" + getRegistryName(glyph).getPath() + ".json");
    }

    @Override
    public @NotNull String getName() {
        return "Ars Elemental Glyph Recipes";
    }
}


