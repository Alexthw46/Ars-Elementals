package alexthw.ars_elemental.event;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.item.IElementalArmor;
import alexthw.ars_elemental.api.item.ISchoolBangle;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.common.blocks.ElementalSpellTurretTile;
import alexthw.ars_elemental.common.entity.FirenandoEntity;
import alexthw.ars_elemental.common.glyphs.EffectBubbleShield;
import alexthw.ars_elemental.common.mob_effects.EnthrallEffect;
import alexthw.ars_elemental.datagen.AETagsProvider;
import alexthw.ars_elemental.recipe.HeadCutRecipe;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCut;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.hollingsworth.arsnouveau.setup.registry.RegistryHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Optional;

import static alexthw.ars_elemental.ConfigHandler.COMMON;
import static alexthw.ars_elemental.registry.ModPotions.*;
import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.ELEMENTAL_AIR;
import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.ELEMENTAL_EARTH;

@Mod.EventBusSubscriber(modid = ArsElemental.MODID)
public class DamageEvents {


    @SubscribeEvent
    public static void betterFilters(SpellDamageEvent.Pre event) {
        //if the spell has a filter, and the target of the attack is not valid, cancel the event
        if (event.context != null && event.context.getCurrentIndex() > 0 && event.context.getSpell().recipe.get(event.context.getCurrentIndex() - 1) instanceof IFilter filter) {
            if (!filter.shouldResolveOnEntity(event.target, event.target.level)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void bypassRes(LivingAttackEvent event) {
        LivingEntity living = event.getEntity();
        if (event.getSource().getEntity() instanceof Player player && living != null) {
            SpellSchool focus = ISchoolFocus.hasFocus(player);
            if (focus != null) {
                switch (focus.getId()) {
                    case "fire" -> {
                        //if the target is fire immune, cancel the event and deal damage
                        if (event.getSource().is(DamageTypeTags.IS_FIRE) && (living.fireImmune() || living.hasEffect(MobEffects.FIRE_RESISTANCE))) {
                            event.setCanceled(true);
                            DamageSource newDamage = DamageUtil.source(player.level, ModRegistry.MAGIC_FIRE, player);
                            living.hurt(newDamage, event.getAmount());
                        }
                    }
                    case "water" -> {
                        //if the target is immune to drowning, cancel the event and deal damage
                        if (event.getSource().is(DamageTypeTags.IS_DROWNING) && living.getMobType() == MobType.WATER) {
                            event.setCanceled(true);
                            DamageSource newDamage = DamageUtil.source(player.level, DamageTypes.MAGIC, player);
                            living.hurt(newDamage, event.getAmount());
                        }
                    }
                }
            }

        } else if (event.getSource().getEntity() instanceof FirenandoEntity FE) {
            //if the firenando is attacking a non-monster, cancel the event
            if (!(living instanceof Monster mob)) {
                event.setCanceled(true);
                living.clearFire();
            } else {
                //if the firenando is attacking a monster, and the monster is fire immune, cancel the event and deal damage
                if ((mob.fireImmune() || living.hasEffect(MobEffects.FIRE_RESISTANCE)) && event.getSource().is(DamageTypeTags.IS_FIRE)) {
                    event.setCanceled(true);
                    mob.hurt(DamageUtil.source(FE.level, ModRegistry.MAGIC_FIRE, FE), event.getAmount());
                }
            }
        }
    }

    @SubscribeEvent
    public static void banglesSpecials(LivingAttackEvent event) {

        LivingEntity living = event.getEntity();

        //if the player is wearing a bangle, apply special effects on hit
        if (event.getSource().getEntity() instanceof Player player && living != null && living != player) {
            for (SpellSchool bangle : ISchoolBangle.getBangles(event.getEntity().level(), player)) {
                switch (bangle.getId()) {
                    case "fire" -> living.setSecondsOnFire(5);
                    case "water" -> living.setTicksFrozen(living.getTicksFrozen() + 100);
                    case "earth" -> living.addEffect(new MobEffectInstance(ModPotions.SNARE_EFFECT.get(), 60));
                    case "necromancy" -> {
                        if (player.getRandom().nextBoolean())
                            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 60));
                        else {
                            living.heal(1.0F);
                            player.heal(1.0F);
                        }
                    }
                    case "conjuration" -> {
                        BlockPos pos = player.blockPosition();
                        // fetch all summons around the player and aggro them to the target
                        player.level().getEntitiesOfClass(LivingEntity.class, new AABB(pos.north(30).west(30).below(10).getCenter(), pos.south(30).east(30).above(10).getCenter()), e -> e instanceof ISummon s && player.equals(s.getOwnerAlt())).forEach(e -> {
                            if (e instanceof Monster mob) {
                                mob.setTarget(living);
                            } else if (e instanceof NeutralMob neutralMob) {
                                neutralMob.setTarget(living);
                            }
                            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 1));
                        });

                    }
                }
            }
        }
        if (living instanceof Player player && (event.getSource().is(DamageTypes.CACTUS) || event.getSource().is(DamageTypes.SWEET_BERRY_BUSH))) {
            if (ISchoolBangle.hasBangle(event.getEntity().level(), player, ELEMENTAL_EARTH)) {
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void handleHealing(LivingHealEvent event) {
        //boost healing if you have earth focus
        if (COMMON.EnableGlyphEmpowering.get() || event.getEntity() instanceof Player player && ISchoolFocus.hasFocus(player) == ELEMENTAL_EARTH) {
            event.setAmount(event.getAmount() * 1.5F);
        }
        //cancel healing if under frozen effect
        if (event.getEntity().hasEffect(FROZEN.get())) {
            event.setCanceled(true);
        }
        //increase healing if you have hellfire and reset the invulnerability time
        if (event.getEntity().hasEffect(MAGIC_FIRE.get()) && COMMON.IFRAME_SKIP.get()) {
            event.setAmount((float) (event.getAmount() * 1.25));
            event.getEntity().invulnerableTime = 0;
        }
    }

    @SubscribeEvent
    public static void damageTweaking(LivingHurtEvent event) {

        var dealer = event.getSource().getEntity();
        var target = event.getEntity();

        SpellSchool focus = ISchoolFocus.hasFocus(dealer);
        if (dealer instanceof Player && focus != null) {
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

        //fetch the damage reduction from the armor according to the damage source
        HashMap<SpellSchool, Integer> bonusMap = new HashMap<>();
        int bonusReduction = 0;

        for (ItemStack stack : event.getEntity().getArmorSlots()) {
            Item item = stack.getItem();
            if (item instanceof IElementalArmor armor && armor.doAbsorb(event.getSource())) {
                bonusReduction++;
                if (bonusMap.containsKey(armor.getSchool())) {
                    bonusMap.put(armor.getSchool(), bonusMap.get(armor.getSchool()) + 1);
                } else {
                    bonusMap.put(armor.getSchool(), 1);
                }
            }
        }

        boolean not_bypassEnchants = !event.getSource().is(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        if (target instanceof Player player) {
            if (event.getSource().getEntity() instanceof LivingEntity living && EnthrallEffect.isEnthralledBy(living, player))
                event.setAmount(event.getAmount() * 0.5f);
            if (not_bypassEnchants) {
                //reduce damage from elytra if you have air focus
                if (event.getSource().is(DamageTypes.FLY_INTO_WALL) && ISchoolFocus.hasFocus(player) == ELEMENTAL_AIR) {
                    event.setAmount(event.getAmount() * 0.1f);
                }

                //if you have 4 pieces of the fire school, fire is removed. Apply the fire focus buff if you have it, since it wouldn't detect the fire otherwise
                if (bonusMap.getOrDefault(SpellSchools.ELEMENTAL_FIRE, 0) == 4 && event.getSource().is(DamageTypeTags.IS_FIRE)) {
                    player.clearFire();
                    if (ISchoolFocus.hasFocus(player) == SpellSchools.ELEMENTAL_FIRE) {
                        player.addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT.get(), 200, 2));
                    }
                }
                //if you have 4 pieces of the water school, you get extra air when drowning
                if (bonusMap.getOrDefault(SpellSchools.ELEMENTAL_WATER, 0) == 4 && event.getSource().is(DamageTypes.DROWN)) {
                    player.setAirSupply(player.getMaxAirSupply());
                    bonusReduction += 5;
                }
                //if you have 4 pieces of the earth school, you get extra food when you are low
                if (bonusMap.getOrDefault(ELEMENTAL_EARTH, 0) == 4 && player.getEyePosition().y() < 20 && player.getFoodData().getFoodLevel() < 2) {
                    player.getFoodData().setFoodLevel(20);
                }
                //if you have 4 pieces of the air school, you get extra fall damage reduction
                if (bonusMap.getOrDefault(ELEMENTAL_AIR, 0) == 4 && event.getSource().is(DamageTypeTags.IS_FALL)) {
                    bonusReduction += 5;
                }

                if (bonusReduction > 0) {
                    int finalBonusReduction = bonusReduction;
                    //convert the damage reduction into mana and add the mana regen effect
                    CapabilityRegistry.getMana(player).ifPresent(mana -> {
                        if (finalBonusReduction > 3) mana.addMana(event.getAmount() * 5);
                        event.getEntity().addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 200, finalBonusReduction / 2));
                    });
                }

            }
        }

        if (bonusReduction > 0 && not_bypassEnchants)
            event.setAmount(event.getAmount() * (1 - bonusReduction / 10F));

        int ManaBubbleCost = EffectBubbleShield.INSTANCE.GENERIC_INT.get();


        //check if the entity has the mana bubble effect and if so, reduce the damage
        if (not_bypassEnchants && event.getEntity() != null && event.getEntity().hasEffect(MANA_BUBBLE.get())) {
            LivingEntity living = event.getEntity();
            CapabilityRegistry.getMana(event.getEntity()).ifPresent(mana -> {
                double maxReduction = mana.getCurrentMana() / ManaBubbleCost;
                double amp = Math.min(1 + living.getEffect(MANA_BUBBLE.get()).getAmplifier() / 2D, maxReduction);
                float newDamage = (float) Math.max(0.1, event.getAmount() - amp);
                float actualReduction = event.getAmount() - newDamage;
                if (actualReduction > 0 && mana.getCurrentMana() >= ManaBubbleCost) {
                    event.setAmount(newDamage);
                    mana.removeMana(actualReduction * ManaBubbleCost);
                }
                if (mana.getCurrentMana() < ManaBubbleCost) {
                    living.removeEffect(MANA_BUBBLE.get());
                }
            });
        }
    }


    //When the entity have the mana bubble and is hit by a harmful effect, it will consume mana to try to protect against it
    @SubscribeEvent
    public static void statusProtect(MobEffectEvent.Applicable event) {
        if (event.getEntity().hasEffect(MANA_BUBBLE.get()))
            if (event.getEffectInstance().getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                Optional<HolderSet.Named<MobEffect>> effects = event.getEntity().level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTag(AETagsProvider.AEMobEffectTagProvider.BUBBLE_BLACKLIST);
                if (effects.isPresent() && effects.get().stream().anyMatch(effect -> effect.get() == event.getEffectInstance().getEffect()))
                    return;

                int ManaBubbleCost = EffectBubbleShield.INSTANCE.GENERIC_INT.get() * 2;
                if (event.getEntity().getRandom().nextInt(10) == 0) {
                    CapabilityRegistry.getMana(event.getEntity()).ifPresent(mana -> {
                        if (mana.getCurrentMana() >= ManaBubbleCost) {
                            mana.removeMana((double) ManaBubbleCost / 2);
                            event.setResult(Event.Result.DENY);
                        }
                    });
                }
            } else if (event.getEffectInstance().getEffect() == MAGIC_FIRE.get()) {
                event.setResult(Event.Result.DENY);
            }
    }

    @SubscribeEvent
    public static void vorpalCut(SpellDamageEvent.Post event) {
        if (!(event.target instanceof LivingEntity living) || living.getHealth() > 0) return;
        SpellSchool school = event.context.getCaster() instanceof TileCaster tc && tc.getTile() instanceof ElementalSpellTurretTile turret ? turret.getSchool() : ISchoolFocus.hasFocus(event.caster);
        Spell subspell = new Spell(event.context.getSpell().recipe.subList(event.context.getCurrentIndex() - 1, event.context.getSpell().recipe.size()));
        if (subspell.recipe.get(0) == EffectCut.INSTANCE && school == ELEMENTAL_AIR) {
            ItemStack skull = null;
            int chance = 0;
            ResourceLocation mob = RegistryHelper.getRegistryName(living.getType());
            if (living instanceof Player player) {
                GameProfile gameprofile = player.getGameProfile();
                skull = new ItemStack(Items.PLAYER_HEAD);
                chance = 20;
                skull.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameprofile));
            } else {
                for (HeadCutRecipe recipe : living.level().getRecipeManager().getAllRecipesFor(ModRegistry.HEAD_CUT.get())) {
                    if (recipe.mob.equals(mob)) {
                        skull = recipe.result.copy();
                        chance = recipe.chance;
                        break;
                    }
                }
            }
            if (skull == null) return;

            int looting = Math.min(3, subspell.getBuffsAtIndex(0, event.caster, AugmentFortune.INSTANCE));
            for (int i = -1; i < looting; i++)
                if (living.getRandom().nextInt(100) <= chance) {
                    living.spawnAtLocation(skull);
                    break;
                }
        }
    }

}
