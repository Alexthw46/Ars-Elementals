package alexthw.ars_elemental.common.mob_effects;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LightningLureEffect extends MobEffect {
    public LightningLureEffect() {
        super(MobEffectCategory.HARMFUL, ParticleColor.YELLOW.getColor());
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        for (int i = 0; i < pAmplifier; i++)
            fallLightning(pLivingEntity);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        // Trigger the effect only once, when the time left reaches 1.
        return pDuration == 1;
    }

    public static void fallLightning(LivingEntity pLivingEntity) {
        // Spawn a lightning bolt at the entity's position when the effect triggers.
        LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(pLivingEntity.level());
        if (lightningbolt == null) return;
        lightningbolt.moveTo(Vec3.atBottomCenterOf(pLivingEntity.blockPosition()));
        lightningbolt.setCause(pLivingEntity instanceof ServerPlayer sp ? sp : null);
        pLivingEntity.level().addFreshEntity(lightningbolt);
    }

}
