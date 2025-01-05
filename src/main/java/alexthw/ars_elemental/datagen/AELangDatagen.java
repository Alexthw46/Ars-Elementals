package alexthw.ars_elemental.datagen;


import alexthw.ars_elemental.ArsElemental;
import com.hollingsworth.arsnouveau.api.registry.FamiliarRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AELangDatagen extends LanguageProvider {

    public AELangDatagen(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.ars_elemental", "Ars Elemental");
        add("ars_nouveau.school.elemental", "Elemental");
        add("item.ars_elemental.spell_horn", "Enchanter's Horn");
        add("item.ars_elemental.fire_focus", "Focus of Fire");
        add("item.ars_elemental.water_focus", "Focus of Water");
        add("item.ars_elemental.air_focus", "Focus of Air");
        add("item.ars_elemental.earth_focus", "Focus of Earth");
        add("item.ars_elemental.lesser_fire_focus", "Lesser Focus of Fire");
        add("item.ars_elemental.lesser_water_focus", "Lesser Focus of Water");
        add("item.ars_elemental.lesser_air_focus", "Lesser Focus of Air");
        add("item.ars_elemental.lesser_earth_focus", "Lesser Focus of Earth");
        add("item.ars_elemental.necrotic_focus", "Focus of Necromancy");
        add("item.ars_elemental.base_bangle", "Enchanter's Bangle");
        add("item.ars_elemental.fire_bangle", "Fire Bangle");
        add("item.ars_elemental.water_bangle", "Water Bangle");
        add("item.ars_elemental.air_bangle", "Air Bangle");
        add("item.ars_elemental.earth_bangle", "Earth Bangle");
        add("item.ars_elemental.summon_bangle", "Summoner's Bangle");
        add("item.ars_elemental.anima_bangle", "Anima Bangle");
        add("item.ars_elemental.fire_caster_tome", "Caster Tome of Fire");
        add("item.ars_elemental.water_caster_tome", "Caster Tome of Water");
        add("item.ars_elemental.air_caster_tome", "Caster Tome of Air");
        add("item.ars_elemental.earth_caster_tome", "Caster Tome of Earth");
        add("item.ars_elemental.anima_caster_tome", "Caster Tome of Anima");
        add("item.ars_elemental.manipulation_caster_tome", "Caster Tome of Manipulation");
        add("tooltip.ars_elemental.caster_tome", "Mimics the abilities of a school focus.");
        add("item.ars_elemental.siren_shards", "Siren Token");
        add("item.ars_elemental.siren_charm", "Siren Charm");
        add("item.ars_elemental.firenando_charm", "Flarecannon Charm");
        add("item.ars_elemental.curio_bag", "Trinkets Pouch");
        add("item.ars_elemental.caster_bag", "Spellcaster Bag");
        add("item.ars_elemental.debug", "Focus of Debug");
        add("item.ars_elemental.mark_of_mastery", "Mark of Mastery");
        add("item.ars_elemental.anima_essence", "Anima Essence");
        add("item.ars_elemental.flashpine_pod", "Flashpine");
        add("item.ars_elemental.arc_prism_lens", "Arc Prism Lens");
        add("item.ars_elemental.homing_prism_lens", "Homing Prism Lens");
        add("item.ars_elemental.rainbow_prism_lens", "Rainbow Prism Lens");
        add("item.ars_elemental.acceleration_prism_lens", "Acceleration Prism Lens");
        add("item.ars_elemental.deceleration_prism_lens", "Deceleration Prism Lens");
        add("item.ars_elemental.piercing_prism_lens", "Piercing Prism Lens");
        add("item.ars_elemental.sparkflower", "Sparkflower");
        add("block.ars_elemental.water_upstream", "Bubble Current Elevator");
        add("block.ars_elemental.magma_upstream", "Magmatic Current Elevator");
        add("block.ars_elemental.air_upstream", "Slipstream Current Elevator");
        add("block.ars_elemental.everfull_urn", "Urn of Endless Waters");
        add("block.ars_elemental.mermaid_rock", "Siren Shrine");
        add("block.ars_elemental.spell_mirror", "Spell Mirror");
        add("block.ars_elemental.advanced_prism", "Advanced Spell Prism");
        add("block.ars_elemental.yellow_archwood", "Flashing Archwood Wood");
        add("block.ars_elemental.yellow_archwood_log", "Flashing Archwood Log");
        add("block.ars_elemental.stripped_yellow_archwood", "Stripped Flashing Archwood Wood");
        add("block.ars_elemental.stripped_yellow_archwood_log", "Stripped Flashing Archwood Log");
        add("block.ars_elemental.yellow_archwood_sapling", "Flashing Archwood Sapling");
        add("block.ars_elemental.yellow_archwood_leaves", "Flashing Archwood Leaves");
        add("block.ars_elemental.sparkflower", "Sparkflower");
        add("block.ars_elemental.spore_blossom_up", "Spore Blossom (ground)");
        add("block.ars_elemental.fire_turret", "Fire Infused Turret");
        add("block.ars_elemental.water_turret", "Water Infused Turret");
        add("block.ars_elemental.air_turret", "Air Infused Turret");
        add("block.ars_elemental.earth_turret", "Earth Infused Turret");
        add("block.ars_elemental.manipulation_turret", "Manipulation Infused Turret");
        add("entity.ars_elemental.homing_projectile", "Homing Projectile");
        add("entity.ars_elemental.lerp", "Interpolated Source Effect");
        add("entity.ars_elemental.summon_skelehorse", "Summoned Skeletal Steed");
        add("entity.ars_elemental.summon_camel", "Summoned Camel");
        add("entity.ars_elemental.summon_direwolf", "Summoned Direwolf");
        add("entity.ars_elemental.summon_vhex", "Summoned Vhex");
        add("entity.ars_elemental.summon_wskeleton", "Summoned Revenant");
        add("entity.ars_elemental.summon_dolphin", "Summoned Dolphin");
        add("entity.ars_elemental.summon_strider", "Summoned Strider");
        add("entity.ars_elemental.linger_magnet", "Gravity Well");
        add("entity.ars_elemental.siren_entity", "Siren");
        add("entity.ars_elemental.siren_familiar", "Siren Familiar");
        add("entity.ars_elemental.firenando_entity", "Flarecannon");
        add("entity.ars_elemental.firenando_familiar", "Flarecannon Familiar");
        add("entity.ars_elemental.fire_mage", "Rogue Fire Mage");
        add("entity.ars_elemental.water_mage", "Rogue Water Mage");
        add("entity.ars_elemental.air_mage", "Rogue Air Mage");
        add("entity.ars_elemental.earth_mage", "Rogue Earth Mage");
        add("entity.ars_elemental.flashing_weald_walker", "Flashing Weald Walker");
        add("entity.ars_elemental.dripstone_spike", "Earth Spike");
        add("entity.ars_elemental.ice_spike", "Ice Spike");
        add("key.ars_elemental.open_pouch", "[Elemental] Open trinkets pouch");
        add("curios.modifiers.an_focus", "While in spell focus slot:");
        add("curios.modifiers.bangle", "While in bangle slot:");
        add("curios.identifier.bundle", "Bundle");
        add("curios.identifier.bangle", "Bangle");
        add("ars_elemental.perk.summon_power", "Summoning Power");
        add("effect.ars_elemental.enderference", "Enderference");
        add("effect.ars_elemental.enderference.description", "Makes the target unable to teleport or be teleported.");
        add("effect.ars_elemental.watery_grave", "Watery Grave");
        add("effect.ars_elemental.watery_grave.description", "Makes the target sink and run out of air quicker.");
        add("effect.ars_elemental.mana_shield", "Mana Shield");
        add("effect.ars_elemental.mana_shield.description", "Use mana to mitigate damage taken and add a chance to not suffer from negative effects.");
        add("effect.ars_elemental.static_charged", "Static Charged");
        add("effect.ars_elemental.static_charged.description", "Calls a lightning to hit the afflicted when the duration ends.");
        add("effect.ars_elemental.hellfire", "Magic Burn");
        add("effect.ars_elemental.hellfire.description", "The magic flames burns even nether creatures making them vulnerable to fire. It slighly decrease damage taken from earth, but let magic damage pierce through some of the armor.");
        add("effect.ars_elemental.life_link", "Life Linked");
        add("effect.ars_elemental.life_link.description", "Caster will split incoming damage with the target and the target will split incoming healing with the caster. Using Cut will dispel the effect on both sides.");
        add("effect.ars_elemental.enthralled", "Enthralled");
        add("effect.ars_elemental.enthralled.description", "Enthralled mobs won't attack the caster but defend them.");
        add("effect.ars_elemental.hymn_of_order", "Hymn of Order");
        add("effect.ars_elemental.hymn_of_order.description", "Unobtainable potion, for Admin use. Sets the punished player's mana to 1 and disables spellcasting, greatly reducing their chances to create chaos in the server.");
        add("effect.ars_elemental.venom", "Envenomed");
        add("effect.ars_elemental.venom.description", "The target is poisoned with a deadly venom and takes more damage from poison spores.");
        add("effect.ars_elemental.frozen", "Frozen");
        add("effect.ars_elemental.frozen.description", "The target is frozen and can't heal, next fire damage taken is increased but dispels the effect.");
        add("enchantment.ars_elemental.mirror_shield", "Spell Reflection");
        add("enchantment.ars_elemental.mirror_shield.desc", "Shield have a chance to reflect projectile spells");
        add("enchantment.ars_elemental.soulbound", "Soulbound");
        add("enchantment.ars_elemental.soulbound.description", "Preserve items on death.");
        add("ars_nouveau.page.elemental_tweaks", "Elemental Tweaks");
        add("ars_elemental.page.elemental_tweaks", "As long as Ars Elemental is installed, these tweaks will be active:$(br)Enchanter's Shield can trigger reactive on block.$(br)Summoned Lightnings won't destroy items.$(br)Crush can process items if augmented with Sensitive.$(br)Cold Snap deals more damage to mobs that are freezing.$(br)Ignite melts ice blocks into water.");
        add("ars_nouveau.page.flashing_archwood", "Flashing Archwood Trees");
        add("ars_elemental.page1.flashing_archwood", "This golden tree has an affinity with the sky. Just like the other archwood trees it can be found anywhere and in their biome. It can be used to make ritual tablets or as a dim light source. The corresponding Weald Walker launches enemies in the air and wind shear them.");
        add("ars_elemental.page2.flashing_archwood", "A fruit with a shocking flavour used to brew Static Charge Potions, causing a lightning to fall on the entity when the duration ends or allowing Discharge to hit. The energy stored in the fruit may cause whoever eats it to glow, get night vision, be shocked or charged with static energy.");
        add("ars_elemental.page1.spell_horn", "The Enchanter's Horn can be used to cast spells on you and nearby entities. Hold the Horn to increase the range, at max charge it will also give a Spell Damage effect to the player. It MUST be inscribed with a spell that does NOT have another method, using a Scribing Table.");
        add("ars_elemental.page.curio_bag", "All those magical trinkets can easily clutter your inventory, but fear not! Using some magebloom fiber you can make a magic pouch to store items. You can also open it with $(k:ars_elemental.open_pouch) while in the hotbar or in a curio slot. You can further upgrade it into the Spellcaster bag, which is larger and can be dyed.");
        add("ars_elemental.page1.fire_focus", "This spell focus is attuned to the school of Fire. While equipped, the glyphs of this school will be amplified and discounted. The lesser focus, as a drawback, will weaken glyphs of the other elemental schools. The major focus will also grant Spell Damage II while the wearer is on fire or in lava.");
        add("ars_elemental.page2.fire_focus", "This focus empowers Ignite to inflict Magic Burn. This effect allows flare to inflict damage and spread even on fire resistant mobs and let magic pierce through part of the enemy armor, but will also make earth damage less effective. Summon Steed will be changed to summon a rideable Strider. Ignite + Evaporate combo will sublimate Ice.");
        add("ars_elemental.page1.water_focus", "This spell focus is attuned to the school of Water. While equipped, the glyphs of this school will be amplified and discounted. The lesser focus, as a drawback, will weaken glyphs of the other elemental schools. The major focus will also grant Mana Regen I while the wearer is wet or Mana Regen II and Dolphin Grace while swimming.");
        add("ars_elemental.page2.water_focus", "This focus empowers Freeze, adding Freezing buildups to the target and eventually Frozen status for a short time, stopping the target from healing. If used after Conjure Water, the conjured water will become ice. Summon Steed will be changed to summon a rideable Dolphin, time your jumps out of the water to build up speed. All Drowning damage against water creatures will be converted to magic.");
        add("ars_elemental.page1.air_focus", "This spell focus is attuned to the school of Air. While equipped, the glyphs of this school will be amplified and discounted. The lesser focus, as a drawback, will weaken glyphs of the other elemental schools. The major focus will also grant Mana Regen I while the wearer stands over Y 200 or is under the shocked effect.");
        add("ars_elemental.page2.air_focus", "This focus empowers Launch, changing it to apply Levitate when augmented with ExtendTime. It also empower Cut, giving chances to drop an head or skull if it deals the killing blow.");
        add("ars_elemental.page1.earth_focus", "This spell focus is attuned to the school of Earth. While equipped, the glyphs of this school will be amplified and discounted. The lesser focus, as a drawback, will weaken glyphs of the other elemental schools. The major focus also grants Mana Regen I while the wearer stand under Y 0.");
        add("ars_elemental.page2.earth_focus", "This focus empowers Poison Spores and Grow, dealing damage to undead with a chance of spawning a spore blossom, and Gravity, when augmented with Sensitive, changing it to create a gravity field that pull entities towards its center (filter-compatible), also offers knockback resistance and boosts natural and instant healing by 1.5.");
        add("ars_elemental.page1.necrotic_focus", "This school of magic branched out from Conjuration and Abjuration mages that tried to understand life, death and what's between. The essence tied to this school cycles between life and death, experiments suggest that it will make horses cycle between flesh, skeleton and zombie forms, but will it be the same horse as before?");
        add("ars_elemental.page2.necrotic_focus", "By imbuing the Focus of Summoning with evil energies, you can corrupt its powers towards Necromancy. Glyphs of the Anima school will get two free ExtendTime, Heal gets two amplify and Charm has way more chance to affect undead mobs. Summon Steed will change in Summon Skeletal Steed, which is able to walk and breathe underwater.");
        add("ars_elemental.page3.necrotic_focus", "Summoned Wolves, Undead and Vexes seems unchanged at first, but if they die while the summoner wear this focus they will rise from death once, filled with blood lust. These undead summons will cast Homing spells when you do and heal you every time they kill an enemy.");
        add("ars_elemental.page.water_upstream", "This block generates an upstream current that will make surrounding entities in water float upwards as if inside a bubble column, even if they are not in source blocks. Sneaking will allow to descend.");
        add("ars_elemental.page.magma_upstream", "This block generates an upstream current that will make surrounding entities in lava float upwards and gain a short Fire Resistance effect. Sneaking will allow to descend");
        add("ars_elemental.page.air_upstream", "This block generates an upstream current that will make surrounding entities levitate. Sneaking will give Slowfall and allow to descend. Consumes Source when at least an entity is affected.");
        add("ars_elemental.page.everfull_urn", "This magic urn converts Source into water. Link a cauldron or an Apothecary to the urn using a dominion wand and it will be refilled for a cheap amount of source.");
        add("ars_elemental.page1.fire_golem", "The Flarecannon can be used as a wandering sentry, like the weald walkers. It will shoot flare homing projectiles at enemies and patrol around the area assigned using the dominion wand. Soul Sand and Magma blocks can be used to change appearance. If defeated, it can be reactivated with blaze powder or magma cream. Friends call it Firenando.");
        add("ars_elemental.page1.mermaid", "Sirens are found in warmer seas, jumping out of the water or following boats. You can befriend a wild mermaid by giving them a sea pickle, they will jump and leave behind few tokens. The siren charm can be used to summon a Siren and if used on Prismarine it will transform in a Siren Shrine. Tamed sirens can change colors if you give them kelp or corals.");
        add("ars_elemental.page2.mermaid", "The Shrine will slowly generate fishing loot items over time, asking for source after each cycle. The number of items and chance of getting a treasure will increase if many different water animals and plants are near the Shrine. You should build an aquarium or pond around the Shrine, to let your sirens have fun. [Note: it will take some time to update the score].");
        add("ars_elemental.page1.advanced_prism", "Upgrade of the Spell Prism that can be adjusted to aim to a specific block. Use the dominion wand to link the prism to a block. This prism also allows lenses, but can't be pushed by pistons. Some lens will require a bit of source whenever a spell is redirected.");
        add("ars_elemental.page2.advanced_prism", "A Prism Lens can be applied to this advanced prisms to customize how the prism redirects projectiles. Arc and Homing lenses change the projectile to be an arc or homing projectile. Rainbow lens randomize the color of the projectile, while Acceleration and Deceleration lenses allow to adjust the speed.");
        add("ars_elemental.page.spell_mirror", "Mirror similar to a Spell Prism that can be placed on walls, floor and ceiling. If a spell projectile hits the mirror, it will be reflected by with a mirrored angle.");
        add("ars_nouveau.page.elemental_turrets", "Elemental Infused Turrets");
        add("ars_elemental.page1.elemental_turrets", "Enchanted Turrets can be imbued with the power of an elemental focus to gift them a fractions of its abilities. Spells shot by these turrets will trigger the combos of the corresponding focus and will be discounted by 65 %% if the spell contains a glyph of the matching elemental school.");
        add("ars_elemental.page.base_bangle", "This magic accessory has a chance to boost the damage of your spells. Its magic is unstable but perhaps attuning it to a school can stabilize its abilities.");
        add("ars_elemental.page1.air_bangle", "This bangle will boost the damage of your Air spells. Your arms sparks with the element, giving a passive boost to speed and attack knockback.");
        add("ars_elemental.page1.fire_bangle", "This bangle will boost the damage of your Fire spells. Your arms are engulfed in the element, setting on fire enemies hit and granting a passive boost to speed while in hot biomes.");
        add("ars_elemental.page1.earth_bangle", "This bangle will boost the damage of your Earth spells. Plants blossom on your arms, inflicting snare to enemies hit and granting the wearer immunity to cactus and berry bushes and knockback resistance.");
        add("ars_elemental.page1.water_bangle", "This bangle will boost the damage of your Water spells. Your arms chills the air around, freezing enemies on every hit. It will also grant the wearer a passive boost to speed in water and rain.");
        add("ars_elemental.page1.summon_bangle", "This bangle will boost the damage of your Summoning spells. Your summons follows your arms movement, targeting whatever your hit with increased damage.");
        add("ars_elemental.page1.anima_bangle", "This bangle will boost the damage of your Anima spells. You can feel a cycle of life and death in your arms, randomly healing or withering the enemies hit and giving you a small health boost.");
        add("ars_elemental.page.book_protection", "This book upgrade will protect your spellbook from all kinds of damage, may it be a cactus or a pool of lava. Can't guarantee on the void. The gold inlays will become netherite-black but you can toggle it off in the client configs.");
        add("ars_elemental.enchantment_desc.mirror_shield", "Can only be applied on the Enchanter's Shield. Gives a chance of reflecting spell projectile while blocking with the shield. 25% per level.");
        add("ars_elemental.enchantment_desc.soulbound", "Prevent the item enchanted from being lost on death.");
        add("ars_nouveau.school.necromancy", "Anima");
        add("death.attack.hellflare", "%1$s was burned by magic flames");
        add("death.attack.hellflare.item", "%1$s was burned by %2$s with the magic flames of %3$s");
        add("death.attack.poison", "%2$s's poison spores blossomed inside %1$s");
        add("death.attack.poison.item", "%1$s was poisoned by %2$s using %3$s");
        add("death.attack.spark", "%1$s experienced a short circuit");
        add("death.attack.spark.item", "%1$s was shocked to death    by %2$s using %3$s");
        add("item.ars_elemental.fire_hat", "Pyromancer's Hat");
        add("item.ars_elemental.fire_robes", "Pyromancer's Robes");
        add("item.ars_elemental.fire_leggings", "Pyromancer's Leggings");
        add("item.ars_elemental.fire_boots", "Pyromancer's Boots");
        add("item.ars_elemental.aqua_hat", "Aquamancer's Hat");
        add("item.ars_elemental.aqua_robes", "Aquamancer's Robes");
        add("item.ars_elemental.aqua_leggings", "Aquamancer's Leggings");
        add("item.ars_elemental.aqua_boots", "Aquamancer's Boots");
        add("item.ars_elemental.earth_hat", "Geomancer's Hat");
        add("item.ars_elemental.earth_robes", "Geomancer's Robes");
        add("item.ars_elemental.earth_leggings", "Geomancer's Leggings");
        add("item.ars_elemental.earth_boots", "Geomancer's Boots");
        add("item.ars_elemental.air_hat", "Aethermancer's Hat");
        add("item.ars_elemental.air_robes", "Aethermancer's Robes");
        add("item.ars_elemental.air_leggings", "Aethermancer's Leggings");
        add("item.ars_elemental.air_boots", "Aethermancer's Boots");
        add("ars_elemental.armor_set.shift_info", "Hold %s for set info");
        add("ars_elemental.lens.shift_info", "Hold %s for lens info");
        add("ars_elemental.lens.arc", "Convert the redirected projectile in an Arc Projectile.");
        add("ars_elemental.lens.homing", "Convert the redirected projectile in an Homing Projectile. The targeting filters of the original projectile won't be inherited.");
        add("ars_elemental.lens.rgb", "Makes the redirected projectile cycle colors.");
        add("ars_elemental.lens.acceleration", "Increase the speed of the redirected projectile.");
        add("ars_elemental.lens.deceleration", "Decrease the speed of the redirected projectile.");
        add("ars_elemental.lens.pierce", "Makes the redirected projectile pierce through more blocks/entities if source is supplied to the prism.");
        add("ars_elemental.page.armor_set.wip", "Note: Since the Armor Rework requires a lot of assets work, the light and heavy elemental armors variants won't be ready for 1.19, so the 'medium' elemental armors can be crafted will all three types. Upgrading will keep enchants and threads, but requires the base armor to be at tier 3.");
        add("ars_elemental.armor_set.fire", "Pyromancer's Set");
        add("ars_elemental.armor_set.fire.desc", "Absorb some fire-related damage and convert into mana, instantly clears fire.");
        add("ars_elemental.armor_set.aqua", "Aquamancer's Set");
        add("ars_elemental.armor_set.aqua.desc", "Absorb some water-related damage and convert into mana, refill air if about to drown.");
        add("ars_elemental.armor_set.earth", "Geomancer's Set");
        add("ars_elemental.armor_set.earth.desc", "Absorb some earth-related damage and convert into mana, gives food deep underground if about to starve.");
        add("ars_elemental.armor_set.air", "Aethermancer's Set");
        add("ars_elemental.armor_set.air.desc", "Absorb some air-related damage and convert into mana, highly reduce fall damage.");
        add("ars_elemental.page.armor_set.fire", "Attuned to the School of Fire, each piece of this armor will amplify fire glyphs, make them cheaper, and reduce damage related to fire, like lava, dragon breath and magma.$(br)When all the pieces are equipped, the damage reduced is converted into mana and fire is immediately put out.");
        add("ars_elemental.page.armor_set.aqua", "Attuned to the School of Water, each piece of this armor will amplify water glyphs, make them cheaper and reduce damage related to water, like drowning, freezing and lightning.$(br)When all the pieces are equipped, the damage reduced is converted into mana and refill air if about to drown.");
        add("ars_elemental.page.armor_set.air", "Attuned to the School of Air, each piece of this armor will amplify air glyphs, make them cheaper and reduce damage related to air, like falling, hitting walls while flying and lightning.$(br)When all the pieces are equipped, the damage reduced is converted into mana, falling won't be a problem anymore!.");
        add("ars_elemental.page.armor_set.earth", "Attuned to the School of Earth, each piece of this armor will amplify earth glyphs, make them cheaper and reduce damage related to earth, like starving, berry bushes, cactus and crushing.$(br)When all the pieces are equipped, the damage reduced is converted into mana and gives food deep underground if about to starve.");
        add("ars_elemental.thread_of", "Thread of %s");
        add("ars_elemental.tablet_of", "Tablet of %s");
        add("tooltip.siren_shards", "Obtained by giving a sea pickle to a siren.");
        add("tooltip.ars_elemental.lens", "Can be applied on Advanced Spell Prisms.");
        add("tooltip.ars_nouveau.blessed", "Protection of the 4 elements");
        add("ars_elemental.focus.shift_info", "Hold %s for more focus info");
        add("tooltip.ars_elemental.focus_element", "Focus attuned to a specific element, empowers some glyphs with new effects.");
        add("tooltip.ars_elemental.focus_boost", "Amplifies and discount glyphs of the %s school.");
        add("tooltip.ars_elemental.focus_malus", "Dampens glyphs of the other elemental schools.");
        add("tooltip.ars_elemental.focus_element_mana.air", "Grants Mana Regen I while the wearer is high in the sky.");
        add("tooltip.ars_elemental.focus_element_mana.fire", "Grants Spell Damage II while the wearer is on fire or in lava.");
        add("tooltip.ars_elemental.focus_element_mana.water", "Grants Mana Regen I while the wearer is wet, Mana Regen II and Dolphin Grace while swimming.");
        add("tooltip.ars_elemental.focus_element_mana.earth", "Grants Mana Regen I while the wearer is deep underground.");
        add("tooltip.ars_elemental.focus_anima", "Entities summoned will raise from death once as a more powerful version. These revenant will mirror your homing spells and heal you when they kill an enemy.");
        add("tooltip.ars_elemental.bags", "Can be opened with %s while in the hotbar or in a curio slot.");
        add("item.minecraft.splash_potion.effect.enderference_potion_strong", "Splash Potion of Enderference");
        add("item.minecraft.splash_potion.effect.enderference_potion", "Splash Potion of Enderference");
        add("item.minecraft.splash_potion.effect.enderference_potion_long", "Splash Potion of Enderference");
        add("item.minecraft.potion.effect.enderference_potion_strong", "Potion of Enderference");
        add("item.minecraft.potion.effect.enderference_potion", "Potion of Enderference");
        add("item.minecraft.potion.effect.enderference_potion_long", "Potion of Enderference");
        add("item.minecraft.lingering_potion.effect.enderference_potion_strong", "Lingering Potion of Enderference");
        add("item.minecraft.lingering_potion.effect.enderference_potion", "Lingering Potion of Enderference");
        add("item.minecraft.lingering_potion.effect.enderference_potion_long", "Lingering Potion of Enderference");
        add("item.minecraft.tipped_arrow.effect.enderference_potion_strong", "Arrow of Enderference");
        add("item.minecraft.tipped_arrow.effect.enderference_potion", "Arrow of Enderference");
        add("item.minecraft.tipped_arrow.effect.enderference_potion_long", "Arrow of Enderference");
        add("item.minecraft.splash_potion.effect.shock_potion_potion_strong", "Splash Potion of Static Charge");
        add("item.minecraft.splash_potion.effect.shock_potion", "Splash Potion of Static Charge");
        add("item.minecraft.splash_potion.effect.shock_potion_long", "Splash Potion of Static Charge");
        add("item.minecraft.potion.effect.shock_potion_strong", "Potion of Static Charge");
        add("item.minecraft.potion.effect.shock_potion", "Potion of Static Charge");
        add("item.minecraft.potion.effect.shock_potion_long", "Potion of Static Charge");
        add("item.minecraft.lingering_potion.effect.shock_potion_strong", "Lingering Potion of Static Charge");
        add("item.minecraft.lingering_potion.effect.shock_potion", "Lingering Potion of Static Charge");
        add("item.minecraft.lingering_potion.effect.shock_potion_long", "Lingering Potion of Static Charge");
        add("item.minecraft.tipped_arrow.effect.shock_potion_strong", "Arrow of Static Charge");
        add("item.minecraft.tipped_arrow.effect.shock_potion", "Arrow of Static Charge");
        add("item.minecraft.tipped_arrow.effect.shock_potion_long", "Arrow of Static Charge");
        add("ars_nouveau.connections.fail.urn", "This block is not compatible with the urn.");
        add("ars_elemental.adv.title.siren_charm", "Not a Manatee");
        add("ars_elemental.adv.desc.siren_charm", "Acquire a Siren Charm");
        add("ars_elemental.adv.title.firenando_charm", "They call me Firenando Mc Cannon");
        add("ars_elemental.adv.desc.firenando_charm", "Acquire a Flarecannon Charm");
        add("ars_elemental.adv.title.lesser_air_focus", "The way of Air");
        add("ars_elemental.adv.desc.lesser_air_focus", "Acquire a lesser focus of Air");
        add("ars_elemental.adv.title.air_focus", "Master of Air");
        add("ars_elemental.adv.desc.air_focus", "Acquire a major focus of Air");
        add("ars_elemental.adv.title.lesser_fire_focus", "The way of Fire");
        add("ars_elemental.adv.desc.lesser_fire_focus", "Acquire a lesser focus of Fire");
        add("ars_elemental.adv.title.fire_focus", "Master of Fire");
        add("ars_elemental.adv.desc.fire_focus", "Acquire a major focus of Fire");
        add("ars_elemental.adv.title.lesser_earth_focus", "The way of Earth");
        add("ars_elemental.adv.desc.lesser_earth_focus", "Acquire a lesser focus of Earth");
        add("ars_elemental.adv.title.earth_focus", "Master of Earth");
        add("ars_elemental.adv.desc.earth_focus", "Acquire a major focus of Earth");
        add("ars_elemental.adv.title.lesser_water_focus", "The way of Water");
        add("ars_elemental.adv.desc.lesser_water_focus", "Acquire a lesser focus of Water");
        add("ars_elemental.adv.title.water_focus", "Master of Water");
        add("ars_elemental.adv.desc.water_focus", "Acquire a major focus of Water");
        add("ars_elemental.adv.title.curio_bag", "Never ask a mage what's in their bag");
        add("ars_elemental.adv.title.necrotic_focus", "A corrupted summoning");
        add("ars_elemental.adv.desc.necrotic_focus", "Acquire a Necromancy focus");
        add("ars_elemental.adv.desc.curio_bag", "Acquire a Trinkets Pouch");
        add("ars_elemental.adv.title.caster_bag", "Your portable arsenal");
        add("ars_elemental.adv.desc.caster_bag", "Acquire a Spellcaster Bag");
        add("ars_elemental.adv.title.spore_blossom", "A Blooming Death");
        add("ars_elemental.adv.desc.spore_blossom", "Obtain a Spore Blossom by killing an undead with Grow or Poison Spores");
        add("ars_elemental.adv.title.mark_of_mastery", "Elemental Maestro");
        add("ars_elemental.adv.desc.mark_of_mastery", "Combine the essences with the tribute and obtain the Mark of Mastery");
        add("ars_elemental.adv.title.summon_dolphin", "Your personal submarine");
        add("ars_elemental.adv.desc.summon_dolphin", "Summon a dolphin using Summon Steed wearing a water focus");
        add("ars_elemental.adv.title.summon_strider", "It's Striding Time");
        add("ars_elemental.adv.desc.summon_strider", "Summon a strider using Summon Steed wearing a fire focus");
        add("ars_elemental.adv.title.summon_skeleton_horse", "The Harbinger");
        add("ars_elemental.adv.desc.summon_skeleton_horse", "Summon a skeleton horse using Summon Steed wearing a necromancy focus");
        add("ars_elemental.adv.title.everfull_urn", "Filled with tears of Nostalgia");
        add("ars_elemental.adv.desc.everfull_urn", "Acquire an Urn of Endless Waters");
        add("ars_elemental.adv.title.levitation", "It's ShulkÃ¨r, not ShulkeÃ©r");
        add("ars_elemental.adv.desc.levitation", "Use Launch + Extend Time while wearing an air focus to make a mob levitate");
        add("ars_elemental.adv.title.mirror_shield", "Feeling like a Prism");
        add("ars_elemental.adv.desc.mirror_shield", "Reflect a spell projectile using a spell reflection shield");
        add("ars_elemental.adv.title.x", "");
        add("ars_elemental.adv.desc.x", "");

        for (FamiliarScript i : FamiliarRegistry.getFamiliarScriptMap().values()) {
            if (i.familiar.getRegistryName().getNamespace().equals(ArsElemental.MODID)) {
                add("ars_elemental.familiar_desc." + i.familiar.getRegistryName().getPath(), i.familiar.getBookDescription());
                add("ars_elemental.familiar_name." + i.familiar.getRegistryName().getPath(), i.familiar.getBookName());
                add("item.ars_elemental." + i.familiar.getRegistryName().getPath(), i.familiar.getBookName());
            }
        }

        for (RitualTablet i : RitualRegistry.getRitualItemMap().values()) {
            if (i.ritual.getRegistryName().getNamespace().equals(ArsElemental.MODID)) {
                add("ars_elemental.ritual_desc." + i.ritual.getRegistryName().getPath(), i.ritual.getLangDescription());
                add("item.ars_elemental." + i.ritual.getRegistryName().getPath(), i.ritual.getLangName());
            }
        }

        for (PerkItem i : PerkRegistry.getPerkItemMap().values()) {
            if (i.perk.getRegistryName().getNamespace().equals(ArsElemental.MODID) && !i.perk.getRegistryName().getPath().equals("blank_thread")) {
                add("ars_elemental.perk_desc." + i.perk.getRegistryName().getPath(), i.perk.getLangDescription());
                add("item.ars_elemental." + i.perk.getRegistryName().getPath(), i.perk.getLangName());
            }
        }

        for (Supplier<Glyph> supplier : GlyphRegistry.getGlyphItemMap().values()) {
            Glyph glyph = supplier.get();
            AbstractSpellPart spellPart = glyph.spellPart;
            ResourceLocation registryName = glyph.spellPart.getRegistryName();
            if (registryName.getNamespace().equals(ArsElemental.MODID)) {
                add("ars_elemental.glyph_desc." + registryName.getPath(), spellPart.getBookDescription());
                add("ars_elemental.glyph_name." + registryName.getPath(), spellPart.getName());

                Map<AbstractAugment, String> augmentDescriptions = new HashMap<>();
                spellPart.addAugmentDescriptions(augmentDescriptions);

                for (AbstractAugment augment : augmentDescriptions.keySet()) {
                    add("ars_nouveau.augment_desc." + registryName.getPath() + "_" + augment.getRegistryName().getPath(), augmentDescriptions.get(augment));
                }
            }
        }
    }

}