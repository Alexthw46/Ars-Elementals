package alexthw.ars_elemental.common.items.foci;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static alexthw.ars_elemental.ConfigHandler.COMMON;
import static alexthw.ars_elemental.registry.ModPotions.MAGIC_FIRE;


public class GreaterElementalFocus extends ElementalFocus {
    public GreaterElementalFocus(Properties properties, SpellSchool element) {
        super(properties, element);
    }

    @Override
    public double getDiscount() {
        return COMMON.MajorFocusDiscount.get();
    }

    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        if (element.isPartOfSchool(spellPart)) {
            builder.addAmplification(getBoostMultiplier() * 2);
        }
        return builder;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide() || !(slotContext.entity() instanceof Player player) || !COMMON.EnableRegenBonus.get())
            return;
        // every 20 ticks, check if the player is in the right environment for the school, and apply the appropriate effect
        if (player.tickCount % 20 == 0) {
            var mainSchool = getSchool();
            List<SpellSchool> schools = new ArrayList<>(1 + mainSchool.getSubSchools().size());
            schools.add(mainSchool);
            schools.addAll(mainSchool.getSubSchools());

            for (var school : schools) {
                switch (school.getId()) {
                    case "fire" -> {
                        if (player.isOnFire() || player.isInLava() || player.hasEffect(MAGIC_FIRE))
                            player.addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT, 200, 1));
                    }
                    case "water" -> {
                        if (player.isInWaterRainOrBubble()) {
                            if (player.isSwimming()) {
                                player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 200, 1));
                                player.addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 120, 1));
                            } else {
                                player.addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 120, 0));
                            }
                        }
                    }
                    case "air" -> {
                        if (player.hasEffect(ModPotions.SHOCKED_EFFECT)) {
                            player.addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 60, 1));
                            player.addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT, 60, 1));
                        } else if (player.getY() > 200)
                            player.addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 120, 0));
                    }
                    case "earth" -> {
                        if (player.getY() < 0)
                            player.addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 120, 0));
                    }
                }
            }
        }

    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation uuid, ItemStack stack) {
        if (element.equals(SpellSchools.ELEMENTAL_EARTH)) {
            Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
            map.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, 0.2f, AttributeModifier.Operation.ADD_VALUE));
            return map;
        }
        return super.getAttributeModifiers(slotContext, uuid, stack);
    }

}
