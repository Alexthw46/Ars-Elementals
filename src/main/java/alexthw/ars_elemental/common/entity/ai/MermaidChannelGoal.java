package alexthw.ars_elemental.common.entity.ai;

import alexthw.ars_elemental.common.blocks.mermaid_block.MermaidTile;
import alexthw.ars_elemental.common.entity.MermaidEntity;
import alexthw.ars_elemental.common.entity.spells.EntityLerpedProjectile;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class MermaidChannelGoal extends Goal {
    MermaidEntity mermaid;
    LivingEntity target;
    boolean complete;
    boolean approached;
    int timeChanneling;

    public MermaidChannelGoal(MermaidEntity drygmy) {
        this.mermaid = drygmy;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    public boolean canUse() {
        return this.mermaid.isTamed() && this.mermaid.channelCooldown <= 0 && this.mermaid.getShrine() != null && !this.mermaid.getShrine().isOff;
    }

    public boolean isInterruptable() {
        return false;
    }

    public void start() {
        super.start();
        MermaidTile tile = this.mermaid.getShrine();
        if (tile != null) {
            this.target = tile.getRandomEntity();
            if (target == null) target = mermaid;
            this.complete = false;
            this.approached = false;
            this.timeChanneling = 0;
        }
    }

    public boolean canContinueToUse() {
        return !this.complete && this.canUse() && this.mermaid.getShrine() != null && this.target != null && !this.target.isRemoved() && this.target.isAlive();
    }

    public void tick() {
        super.tick();
        if (!this.complete && this.target != null) {
            if (!this.approached && BlockUtil.distanceFrom(this.mermaid.position(), this.target.position()) >= 2.0) {
                Path path = this.mermaid.getNavigation().createPath(this.target.getX(), this.target.getY(), this.target.getZ(), 1);
                if (path == null || !path.canReach()) {
                    this.approached = true;
                    this.mermaid.getNavigation().stop();
                    return;
                }

                this.mermaid.getNavigation().moveTo(path, 1.0);
            } else {
                this.mermaid.setChannelingEntity(this.target.getId());
                this.mermaid.getLookControl().setLookAt(this.target, 10.0F, (float) this.mermaid.getMaxHeadXRot());
                this.mermaid.getNavigation().stop();
                this.approached = true;
                this.mermaid.setChanneling(true);
                ++this.timeChanneling;
                if (this.timeChanneling >= 100) {
                    this.mermaid.setChanneling(false);
                    this.mermaid.setChannelingEntity(-1);
                    this.complete = true;
                    MermaidTile shrine = this.mermaid.getShrine();
                    BlockPos targetPos = this.target.blockPosition();
                    if (shrine != null && shrine.getY() >= targetPos.getY() - 2) {
                        targetPos = targetPos.above(shrine.getBlockPos().getY() - targetPos.getY());
                        EntityLerpedProjectile item = new EntityLerpedProjectile(this.mermaid.level, targetPos, shrine.getBlockPos(), 20, 100, 200);
                        this.mermaid.level.addFreshEntity(item);
                        shrine.giveProgress();
                    }
                    this.mermaid.channelCooldown = 100;
                }
            }

        }
    }

    public void stop() {
        super.stop();
        this.complete = false;
        this.approached = false;
        this.mermaid.setChannelingEntity(-1);
        this.mermaid.setChanneling(false);
        this.timeChanneling = 0;
    }
}
