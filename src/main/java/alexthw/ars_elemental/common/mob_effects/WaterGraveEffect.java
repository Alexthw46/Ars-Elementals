package alexthw.ars_elemental.common.mob_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class WaterGraveEffect extends MobEffect {

    public WaterGraveEffect() {
        super(MobEffectCategory.HARMFUL, 47359);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amp) {
        // bring the entity down if it is in water or rain
        if (!livingEntity.onGround() && livingEntity.isInWaterRainOrBubble()) {
            Vec3 vec3 = livingEntity.getDeltaMovement();
            double d0;
            d0 = Math.min(-0.5D, vec3.y - 0.03D);
            livingEntity.setDeltaMovement(vec3.x, d0, vec3.z);
            int air = Math.max(-19, livingEntity.getAirSupply() - 5 * (1 + amp));
            livingEntity.setAirSupply(air);
        } else {
            livingEntity.setAirSupply(livingEntity.getAirSupply() - 5);
        }
        return true;
    }

}
