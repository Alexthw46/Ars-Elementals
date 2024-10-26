package alexthw.ars_elemental.datagen;

import alexthw.ars_elemental.common.glyphs.*;
import alexthw.ars_elemental.util.ParticleUtil;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CasterTomeData;
import com.hollingsworth.arsnouveau.common.datagen.CasterTomeProvider;
import com.hollingsworth.arsnouveau.common.items.CasterTome;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.spell.method.MethodUnderfoot;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.nio.file.Path;

import static alexthw.ars_elemental.ArsElemental.prefix;
import static alexthw.ars_elemental.registry.ModItems.*;

public class AECasterTomeProvider extends CasterTomeProvider {
    public AECasterTomeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput cache) {

        tomes.add(buildTome(SHAPERS_CTOME, "glassmaker", "Safety Jar", new Spell().add(MethodUnderfoot.INSTANCE)
                        .add(EffectConjureTerrain.INSTANCE)
                        .add(AugmentAOE.INSTANCE, 3)
                        .add(AugmentPierce.INSTANCE, 3)
                        .add(EffectCrush.INSTANCE)
                        .add(EffectSmelt.INSTANCE)
                , "Encase yourself in glass.",
                ParticleUtil.airColor));

        tomes.add(buildTome(WATER_CTOME, "dolphin", "Poseidon's Steed", new Spell()
                        .add(MethodSelf.INSTANCE)
                        .add(EffectSummonSteed.INSTANCE)
                        .add(AugmentAOE.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                , "Summon two rideable Dolphins for a short time.",
                ParticleUtil.waterColor));

        tomes.add(buildTome(FIRE_CTOME, "strider", "It's Striding Time", new Spell()
                        .add(MethodSelf.INSTANCE)
                        .add(EffectSummonSteed.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                , "Summon a rideable Strider for a short time.",
                ParticleUtil.fireColor));

        tomes.add(buildTome(EARTH_CTOME, "camel", "Humpday", new Spell()
                        .add(MethodSelf.INSTANCE)
                        .add(EffectSummonSteed.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                , "Summon a Camel for a short time.",
                ParticleUtil.earthColor));

        tomes.add(buildTome(EARTH_CTOME, "magnet", "Gravity Well", new Spell()
                        .add(MethodArcProjectile.INSTANCE)
                        .add(EffectGravity.INSTANCE)
                        .add(AugmentSensitive.INSTANCE)
                        .add(AugmentAOE.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(EffectLinger.INSTANCE)
                        .add(AugmentSensitive.INSTANCE)
                        .add(EffectLaunch.INSTANCE)
                        .add(AugmentDampen.INSTANCE),
                "Creates a gravity center that also attract nearby blocks.",
                ParticleUtil.earthColor));

        tomes.add(buildTome(FIRE_CTOME, "hellflare", "Magiflare", new Spell()
                        .add(MethodHomingProjectile.INSTANCE)
                        .add(EffectIgnite.INSTANCE)
                        .add(EffectHex.INSTANCE)
                        .add(EffectFlare.INSTANCE)
                , "These wicked flames cook so well that even armor becomes weaker to magic.",
                ParticleUtil.fireColor));

        tomes.add(buildTome(WATER_CTOME, "frostbite", "Frostbite", new Spell()
                        .add(MethodHomingProjectile.INSTANCE)
                        .add(EffectFreeze.INSTANCE)
                        .add(EffectColdSnap.INSTANCE)
                , "It's not easy to heal while you're a meat popsicle.",
                ParticleUtil.waterColor));

        tomes.add(buildTome(AIR_CTOME, "zap", "Static Discharge", new Spell()
                        .add(MethodTouch.INSTANCE)
                        .add(EffectSpark.INSTANCE)
                        .add(EffectDischarge.INSTANCE)
                , "Starbchu, I Choose You!.",
                ParticleUtil.airColor));

        tomes.add(buildTome(AIR_CTOME, "leviosa", "Shulkium Leviosa", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectLaunch.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(EffectDelay.INSTANCE)
                        .add(EffectWindshear.INSTANCE)
                        .add(EffectDelay.INSTANCE)
                        .add(EffectWindshear.INSTANCE)
                , "Way worse than being shulked, wind feels sharp today.",
                ParticleUtil.airColor));

        tomes.add(buildTome(EARTH_CTOME, "flower", "Poison Blossom", new Spell()
                        .add(MethodTouch.INSTANCE)
                        .add(EffectEnvenom.INSTANCE)
                        .add(EffectSpores.INSTANCE)
                , "May the venom become the seed of a flower that will blossom in undeath.",
                ParticleUtil.earthColor));

        tomes.add(buildTome(NECRO_CTOME, "skelehorse", "Undead Steed", new Spell()
                        .add(MethodSelf.INSTANCE)
                        .add(EffectSummonSteed.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                , "Summon a Skeletal Horse for a short time.",
                ParticleUtil.soulColor));

        Path output = this.generator.getPackOutput().getOutputFolder();
        for (CasterRecipeWrapper g : tomes) {
            Path path = getRecipePath(output, g.id().getPath());
            saveStable(cache, CasterTomeData.CODEC.encodeStart(JsonOps.INSTANCE, g.toData()).getOrThrow(), path);
        }
    }

    public CasterRecipeWrapper buildTome(DeferredHolder<Item, ? extends CasterTome> item, String id, String name, Spell spell, String flavorText, ParticleColor particleColor) {
        return new CasterRecipeWrapper(prefix(id + "_tome"),
                name,
                spell.serializeRecipe(),
                item.getId(),
                flavorText,
                particleColor.serialize(), ConfiguredSpellSound.DEFAULT);
    }

    protected Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_elemental/recipe/tomes/" + str + ".json");
    }

    @Override
    public String getName() {
        return "Ars Elemental Caster Tomes Datagen";
    }

}
