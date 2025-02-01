package alexthw.ars_elemental.common.mob_effects;

import alexthw.ars_elemental.registry.ModPotions;
import alexthw.ars_elemental.util.EntityCarryMEI;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LifeLinkEffect extends MobEffect {

    public LifeLinkEffect() {
        super(MobEffectCategory.NEUTRAL, 1);
        NeoForge.EVENT_BUS.addListener(this::healForHeal);
        NeoForge.EVENT_BUS.addListener(this::hurtForHurt);
    }

    @Override
    public void fillEffectCures(@NotNull Set<EffectCure> cures, @NotNull MobEffectInstance effectInstance) {
    }

    public void healForHeal(LivingHealEvent event) {
        if (event.getEntity().hasEffect(ModPotions.LIFE_LINK)) {
            MobEffectInstance instance = event.getEntity().getEffect(ModPotions.LIFE_LINK);
            if (instance instanceof EntityCarryMEI mei && mei.getTarget() == event.getEntity()) {
                if (mei.getOwner().isAlive()) {
                    int shared = (int) (event.getAmount() / 2F);
                    mei.getOwner().heal(shared);
                    event.setAmount(shared);
                } else {
                    event.getEntity().removeEffect(ModPotions.LIFE_LINK);
                }
            }
        }
    }

    public void hurtForHurt(LivingDamageEvent.Pre event) {
        if (event.getEntity().hasEffect(ModPotions.LIFE_LINK)) {
            MobEffectInstance instance = event.getEntity().getEffect(ModPotions.LIFE_LINK);
            if (instance instanceof EntityCarryMEI mei && mei.getOwner() == event.getEntity()) {
                if (mei.getTarget().isAlive()) {
                    int shared = (int) (event.getNewDamage() / 2F);
                    mei.getTarget().hurt(event.getSource(), shared);
                    event.setNewDamage(shared);
                } else {
                    event.getEntity().removeEffect(ModPotions.LIFE_LINK);
                }
            }
        }
    }

}
