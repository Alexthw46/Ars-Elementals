package alexthw.ars_elemental.common.entity.ai;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.function.Supplier;

public class ProjCastingGoal<T extends Mob & RangedAttackMob> extends CastGoal<T> {

    protected int strafingTime;
    protected boolean strafingBackwards;
    protected boolean strafingClockwise;

    public ProjCastingGoal(T entity, double speed, int attackInterval, float attackRange, Supplier<Boolean> canUse, int animId, int delayTicks) {
        super(entity, speed, attackInterval, attackRange, canUse, animId, delayTicks);
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) return;

        double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
        boolean canSeeEnemy = this.mob.getSensing().hasLineOfSight(livingentity);

        if (canSeeEnemy != this.seeTime > 0) {
            this.seeTime = 0;
        }

        if (canSeeEnemy) {
            ++this.seeTime;
        } else {
            --this.seeTime;
        }

        if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 20) {
            this.mob.getNavigation().stop();
            ++this.strafingTime;
        } else {
            this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= 20) {
            if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                this.strafingClockwise = !this.strafingClockwise;
            }

            if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                this.strafingBackwards = !this.strafingBackwards;
            }

            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {
            if (d0 > (double) (this.attackRadiusSqr * 0.75F)) {
                this.strafingBackwards = false;
            } else if (d0 < (double) (this.attackRadiusSqr * 0.25F)) {
                this.strafingBackwards = true;
            }

            this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            this.mob.lookAt(livingentity, 30.0F, 30.0F);
        } else {
            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
        }

        animationChecks(livingentity);
    }

    void animationChecks(LivingEntity target) {

        if (this.seeTime >= 40 && !this.hasAnimated) {
            this.hasAnimated = true;
            Networking.sendToNearbyClient(mob.level, mob, new PacketAnimEntity(mob.getId(), animId));
        }

        if (this.hasAnimated) {
            animatedTicks++;
            if (animatedTicks >= delayTicks) {
                mob.performRangedAttack(target, 1);
                this.done = true;
            }
        }
    }

}
