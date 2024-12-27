package alexthw.ars_elemental.common.items.armor;

import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.spell.IDamageEffect;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

import static alexthw.ars_elemental.ArsElemental.prefix;
import static alexthw.ars_elemental.registry.ModPotions.LIGHTNING_LURE;

public class ShockPerk extends Perk implements IEffectResolvePerk {

    public static ShockPerk INSTANCE = new ShockPerk();

    public ShockPerk() {
        super(prefix("thread_shock"));
    }

    @Override
    public String getLangDescription() {
        return "Damaging effects cause the target to be shocked for a short duration before the effect resolves. Inflicts Static Charged at tier 3.";

    }

    @Override
    public String getLangName() {
        return "Shocking";
    }

    @Override
    public void onEffectPreResolve(EffectResolveEvent.Pre event, PerkInstance perkInstance) {
        // If the effect is a damage effect, the raytrace result is a living entity, and the entity hit is not the shooter, then apply shocked or lightning lure.
        if (event.resolveEffect instanceof IDamageEffect damageEffect && event.rayTraceResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity && livingEntity != event.shooter) {
            if (damageEffect.canDamage(event.shooter, event.spellStats, event.context, event.resolver, entityHitResult.getEntity())) {
                int value = perkInstance.getSlot().value();
                livingEntity.addEffect(new MobEffectInstance(value < 3 ? ModPotions.SHOCKED_EFFECT : LIGHTNING_LURE, 100, value < 3 ? value : 1));
            }
        }
    }

}
