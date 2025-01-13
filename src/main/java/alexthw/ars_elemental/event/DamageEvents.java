package alexthw.ars_elemental.event;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.item.IElementalArmor;
import alexthw.ars_elemental.api.item.ISchoolBangle;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.common.entity.mages.EntityMageBase;
import alexthw.ars_elemental.common.entity.mages.WaterMage;
import alexthw.ars_elemental.common.glyphs.EffectBubbleShield;
import alexthw.ars_elemental.common.mob_effects.EnthrallEffect;
import alexthw.ars_elemental.datagen.AETagsProvider;
import alexthw.ars_elemental.recipe.HeadCutRecipe;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.hollingsworth.arsnouveau.setup.registry.RegistryHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import static alexthw.ars_elemental.ConfigHandler.COMMON;
import static alexthw.ars_elemental.registry.ModPotions.*;
import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.ELEMENTAL_AIR;
import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.ELEMENTAL_EARTH;

@EventBusSubscriber(modid = ArsElemental.MODID)
public class DamageEvents {


    @SubscribeEvent
    public static void betterFilters(SpellDamageEvent.Pre event) {
        //if the spell has a filter, and the target of the attack is not valid, cancel the event
        // event.context.getCurrentIndex() - 1 is the current, we check the one before it
        if (event.context != null && event.context.getCurrentIndex() > 1 && event.context.getSpell().unsafeList().get(event.context.getCurrentIndex() - 2) instanceof IFilter filter) {
            if (!filter.shouldResolveOnEntity(event.target, event.target.level())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void bypassRes(LivingIncomingDamageEvent event) {
        LivingEntity living = event.getEntity();
        if (event.getSource().getEntity() instanceof Player player) {
            Set<SpellSchool> focus = ISchoolFocus.getFociSchools(player);
            if (!focus.isEmpty()) {
                focusTriggered:
                for (SpellSchool school : focus) {
                    switch (school.getId()) {
                        case "fire" -> {
                            if (event.getSource().is(DamageTypeTags.IS_FIRE) && (living.fireImmune() || living.hasEffect(MobEffects.FIRE_RESISTANCE))) {
                                event.setCanceled(true);
                                DamageSource newDamage = DamageUtil.source(player.level(), ModRegistry.MAGIC_FIRE, player);
                                living.hurt(newDamage, event.getAmount());
                                break focusTriggered;
                            }
                        }
                        case "water" -> {
                            if (event.getSource().is(DamageTypeTags.IS_DROWNING) && living.getType().is(EntityTypeTags.AQUATIC)) {
                                event.setCanceled(true);
                                DamageSource newDamage = DamageUtil.source(player.level(), DamageTypes.MAGIC, player);
                                living.hurt(newDamage, event.getAmount());
                                break focusTriggered;
                            }
                        }
                    }
                }
            }

        }
    }

    @SubscribeEvent
    public static void banglesSpecials(LivingIncomingDamageEvent event) {

        LivingEntity eventTarget = event.getEntity();

        //if the player is wearing a bangle, apply special effects on hit
        if (event.getSource().getEntity() instanceof Player player && eventTarget != player) {
            if (eventTarget instanceof ISummon summon && summon.getOwnerAlt() == player) return;
            for (SpellSchool bangle : ISchoolBangle.getBangles(event.getEntity().level(), player)) {
                if (bangle != null) {
                    switch (bangle.getId()) {
                        case "fire" -> eventTarget.setRemainingFireTicks(20 * 5);
                        case "water" -> eventTarget.setTicksFrozen(eventTarget.getTicksFrozen() + 100);
                        case "earth" -> eventTarget.addEffect(new MobEffectInstance(ModPotions.SNARE_EFFECT, 60));
                        case "necromancy" -> {
                            if (player.getRandom().nextBoolean())
                                eventTarget.addEffect(new MobEffectInstance(MobEffects.WITHER, 60));
                            else {
                                eventTarget.heal(1.0F);
                                player.heal(1.0F);
                            }
                        }
                        case "conjuration" -> {
                            BlockPos pos = player.blockPosition();
                            // fetch all summons around the player and aggro them to the target
                            player.level().getEntitiesOfClass(LivingEntity.class, new AABB(pos.north(30).west(30).below(10).getCenter(), pos.south(30).east(30).above(10).getCenter()), e -> e instanceof ISummon s && player.equals(s.getOwnerAlt())).forEach(e -> {
                                if (e instanceof Monster mob) {
                                    mob.setTarget(eventTarget);
                                } else if (e instanceof NeutralMob neutralMob) {
                                    neutralMob.setTarget(eventTarget);
                                }
                                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 1));
                            });

                        }
                    }
                }
            }
        }

        if (eventTarget instanceof Player player && (event.getSource().is(DamageTypes.CACTUS) || event.getSource().is(DamageTypes.SWEET_BERRY_BUSH))) {
            if (ISchoolBangle.hasBangle(event.getEntity().level(), player, ELEMENTAL_EARTH)) {
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void handleHealing(LivingHealEvent event) {
        //boost healing if you have earth focus
        if (COMMON.EnableGlyphEmpowering.get() || event.getEntity() instanceof Player player && ISchoolFocus.getFociSchools(player).contains(ELEMENTAL_EARTH)) {
            event.setAmount(event.getAmount() * 1.5F);
        }
        //cancel healing if under frozen effect
        if (event.getEntity().hasEffect(FROZEN)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void damageTweaking(LivingIncomingDamageEvent event) {

        var dealer = event.getSource().getEntity();
        var target = event.getEntity();

        // if frozen, boost next fire damage
        if (target.hasEffect(FROZEN) && event.getSource().is(ModRegistry.FIRE_DAMAGE)) {
            event.setAmount(event.getAmount() * 1.5F);
            target.removeEffect(FROZEN);
        }
        // if the target has magic fire, reduce earth damage
        if (target.hasEffect(MAGIC_FIRE) && event.getSource().is(ModRegistry.EARTH_DAMAGE)) {
            event.setAmount(event.getAmount() * 0.85F);
        }


        if (dealer instanceof LivingEntity caster && (target instanceof Player || target instanceof EntityMageBase)) {
            Set<SpellSchool> foci = ISchoolFocus.getFociSchools(caster);
            for (SpellSchool focus : foci) {
                //if the player has a focus, apply the special effects
                switch (focus.getId()) {
                    case "water" -> {
                        //change the freezing buff from useless to the whole damage
                        if (target.getPercentFrozen() > 0.75F && event.getSource().is(DamageTypeTags.IS_FREEZING)) {
                            event.setAmount(event.getAmount() * 1.25F);
                        }
                    }
                    case "air" -> {
                        //let's try to compensate the loss of iframe skip with a buff to WS
                        if (target.hasEffect(MobEffects.LEVITATION) && event.getSource().is(DamageTypeTags.IS_FALL)) {
                            event.setAmount(event.getAmount() * 1.25F);
                        }
                    }
                }
            }
        }

        //fetch the damage reduction from the armor according to the damage source
        HashMap<SpellSchool, Integer> bonusMap = new HashMap<>();
        int bonusReduction = 0;

        for (ItemStack stack : event.getEntity().getArmorSlots()) {
            Item item = stack.getItem();
            if (item instanceof IElementalArmor armor && armor.fillAbsorptions(event.getSource(), bonusMap)) {
                bonusReduction++;
            }
        }

        boolean not_bypassEnchants = !event.getSource().is(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        if (event.getSource().getEntity() instanceof LivingEntity living && target instanceof Player player && EnthrallEffect.isEnthralledBy(living, player))
            event.setAmount(event.getAmount() * .5F);

        if (target instanceof Player || target instanceof EntityMageBase) {

            Set<SpellSchool> schools = ISchoolFocus.getFociSchools(target);

            if (not_bypassEnchants) {
                //reduce damage from elytra if you have air focus
                if (event.getSource().is(DamageTypes.FLY_INTO_WALL) && schools.contains(ELEMENTAL_AIR)) {
                    event.setAmount(event.getAmount() * .1F);
                }

                //if you have 4 pieces of the fire school, fire is removed. Apply the fire focus buff if you have it, since it wouldn't detect the fire otherwise
                if (bonusMap.getOrDefault(SpellSchools.ELEMENTAL_FIRE, 0) == 4 && event.getSource().is(DamageTypeTags.IS_FIRE)) {
                    target.clearFire();
                    if (schools.contains(SpellSchools.ELEMENTAL_FIRE)) {
                        target.addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT, 200, 2));
                    }
                }
                //if you have 4 pieces of the water school, you get extra air when drowning
                if (bonusMap.getOrDefault(SpellSchools.ELEMENTAL_WATER, 0) == 4 && event.getSource().is(DamageTypes.DROWN)) {
                    target.setAirSupply(target.getMaxAirSupply());
                    bonusReduction += 5;
                }
                //if you have 4 pieces of the earth school, you get extra food when you are low
                if (target instanceof Player player && bonusMap.getOrDefault(ELEMENTAL_EARTH, 0) == 4 && target.getEyePosition().y() < 20 && player.getFoodData().getFoodLevel() < 4) {
                    player.getFoodData().setFoodLevel(20);
                }
                //if you have 4 pieces of the air school, you get extra fall damage reduction
                if (bonusMap.getOrDefault(ELEMENTAL_AIR, 0) == 4 && event.getSource().is(DamageTypeTags.IS_FALL)) {
                    bonusReduction += 5;
                }

                if (bonusReduction > 0) {
                    //convert the damage reduction into mana and add the mana regen effect
                    var mana = CapabilityRegistry.getMana(target);
                    if (mana != null) {
                        if (bonusReduction > 3) mana.addMana(event.getOriginalAmount() * 5);
                        event.getEntity().addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 200, bonusReduction / 2));
                    }
                }

            }
        }

        if (bonusReduction > 0 && not_bypassEnchants)
            event.setAmount(event.getAmount() * (1 - bonusReduction / 10F));

        // if damage is magic and target has magic fire, add back the half the damage that was reduced from the armor points
        if (event.getSource().is(Tags.DamageTypes.IS_MAGIC) && target.hasEffect(MAGIC_FIRE)) {
            var armorReduction = event.getContainer().getReduction(DamageContainer.Reduction.ARMOR);
            event.setAmount(event.getAmount() + armorReduction * 0.5F);
        }

        int ManaBubbleCost = EffectBubbleShield.INSTANCE.GENERIC_INT.get();
        //check if the entity has the mana bubble effect and if so, reduce the damage
        LivingEntity living = event.getEntity();
        MobEffectInstance bubbleEffect = living.getEffect(MANA_BUBBLE);
        if (not_bypassEnchants && bubbleEffect != null) {
            var mana = CapabilityRegistry.getMana(living);
            if (mana != null) {
                double maxReduction = mana.getCurrentMana() / ManaBubbleCost;
                double amp = Math.min(1 + bubbleEffect.getAmplifier() / 2D, maxReduction);
                float newDamage = (float) Math.max(0.1, event.getAmount() - amp);
                float actualReduction = event.getAmount() - newDamage;
                // don't deplete mana if the entity is invulnerable due to a previous attack
                if (actualReduction > 0 && mana.getCurrentMana() >= ManaBubbleCost && event.getContainer().getPostAttackInvulnerabilityTicks() != event.getEntity().invulnerableTime) {
                    event.setAmount(newDamage);
                    mana.removeMana(actualReduction * ManaBubbleCost);
                }
                if (mana.getCurrentMana() < ManaBubbleCost) {
                    living.removeEffect(MANA_BUBBLE);
                }
            } else if (living instanceof WaterMage) {
                event.setAmount(event.getAmount() * 0.5F);
            }
        }
    }


    //When the entity have the mana bubble and is hit by a harmful effect, it will consume mana to try to protect against it
    @SubscribeEvent
    public static void statusProtect(MobEffectEvent.Applicable event) {
        if (event.getEntity().hasEffect(MANA_BUBBLE)) {
            event.getEffectInstance();
            if (event.getEffectInstance().getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                Optional<HolderSet.Named<MobEffect>> effects = event.getEntity().level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTag(AETagsProvider.AEMobEffectTagProvider.BUBBLE_BLACKLIST);
                if (effects.isPresent() && effects.get().stream().anyMatch(effect -> effect.value() == event.getEffectInstance().getEffect()))
                    return;

                int ManaBubbleCost = EffectBubbleShield.INSTANCE.GENERIC_INT.get() * 2;
                if (event.getEntity().getRandom().nextInt(10) == 0) {
                    var mana = CapabilityRegistry.getMana(event.getEntity());
                    if (mana != null) {
                        if (mana.getCurrentMana() >= ManaBubbleCost) {
                            mana.removeMana((double) ManaBubbleCost / 2);
                            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                        }
                    }
                }
            } else if (event.getEffectInstance().getEffect() == MAGIC_FIRE.get()) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
    }

    @SubscribeEvent
    public static void vorpalCut(SpellDamageEvent.Post event) {
        if (!(event.target instanceof LivingEntity living) || living.getHealth() > 0) return;

        if (event.damageSource.is(ModRegistry.CUT)) {
            ItemStack skull = null;
            int chance = 0;
            ResourceLocation mob = RegistryHelper.getRegistryName(living.getType());
            if (living instanceof Player player) {
                GameProfile gameprofile = player.getGameProfile();
                skull = new ItemStack(Items.PLAYER_HEAD);
                chance = 20;
                skull.set(DataComponents.PROFILE, new ResolvableProfile(gameprofile));
            } else {
                for (RecipeHolder<HeadCutRecipe> recipeh : living.level().getRecipeManager().getAllRecipesFor(ModRegistry.HEAD_CUT.get())) {
                    HeadCutRecipe recipe = recipeh.value();
                    if (recipe.mob.equals(mob)) {
                        skull = recipe.result.copy();
                        chance = recipe.chance;
                        break;
                    }
                }
            }
            if (skull == null) return;

            int looting = Math.min(3, ((DamageUtil.SpellDamageSource) event.damageSource).getLuckLevel());
            for (int i = -1; i < looting; i++)
                if (living.getRandom().nextInt(100) <= chance) {
                    living.spawnAtLocation(skull);
                    break;
                }
        }
    }

}
