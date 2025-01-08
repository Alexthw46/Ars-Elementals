package alexthw.ars_elemental.common.entity.ai;

import alexthw.ars_elemental.common.entity.mages.EntityMageBase;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class MageProjCastingGoal<T extends EntityMageBase> extends ProjCastingGoal<T> {
    public MageProjCastingGoal(T entity, double speed, int attackInterval, float attackRange, Supplier<Boolean> canUse, int animId, int delayTicks) {
        super(entity, speed, attackInterval, attackRange, canUse, animId, delayTicks);
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) return;

        double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
        boolean canSeeEnemy = this.mob.getSensing().hasLineOfSight(livingentity);

        double minDistanceSqr = this.attackRadiusSqr * 0.5 * 0.5; // Minimum safe distance
        double maxDistanceSqr = this.attackRadiusSqr * 1.5 * 1.5; // Maximum engagement distance
        double maxAggroSqr = this.attackRadiusSqr * 2.0 * 2.0; // Prevent excessive retreat

        // Update visibility timer
        if (canSeeEnemy != this.seeTime > 0) {
            this.seeTime = 0;
        }
        if (canSeeEnemy) {
            ++this.seeTime;
        } else {
            --this.seeTime;
        }

        // Adjust movement based on distance
        if (d0 > maxDistanceSqr) {
            // If too far, close the gap
            this.strafingBackwards = false;
            this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
            this.strafingTime = -1;
        } else if (d0 < minDistanceSqr) {
            // If too close, back away but avoid excessive retreat
            this.strafingBackwards = !(d0 > maxAggroSqr); // Stop retreating
            this.mob.getNavigation().stop();
        } else {
            // Within range, enable strafing
            this.mob.getNavigation().stop();
            ++this.strafingTime;
        }

        // Dynamic strafing adjustments
        if (this.strafingTime >= 20) {
            if (this.mob.getRandom().nextFloat() < 0.3D) {
                this.strafingClockwise = !this.strafingClockwise;
            }
            if (this.mob.getRandom().nextFloat() < 0.3D) {
                this.strafingBackwards = false; // Favor sideways strafing over backpedaling
            }
            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {
            float strafeSpeed = (float) Math.min(0.5, Math.sqrt(d0) / Math.sqrt(this.attackRadiusSqr));
            this.mob.getMoveControl().strafe(
                    this.strafingBackwards ? -strafeSpeed : strafeSpeed,
                    this.strafingClockwise ? strafeSpeed : -strafeSpeed
            );
            this.mob.lookAt(livingentity, 30.0F, 30.0F);
        } else {
            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
        }

        animationChecks(livingentity);
    }

}
