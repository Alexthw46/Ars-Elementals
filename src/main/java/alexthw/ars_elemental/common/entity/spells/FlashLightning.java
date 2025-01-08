package alexthw.ars_elemental.common.entity.spells;

import alexthw.ars_elemental.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlashLightning extends LightningBolt {
    public FlashLightning(EntityType<? extends LightningBolt> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public FlashLightning(Level pLevel) {
        super(ModEntities.FLASH_LIGHTNING.get(), pLevel);
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntities.FLASH_LIGHTNING.get();
    }

    public void tick() {
        this.baseTick();
        if (this.life == 2) {
            if (this.level.isClientSide()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 60.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 1.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
            } else {
                this.powerLightningRod();
                clearCopperOnLightningStrike(this.level, this.getStrikePosition());
                this.gameEvent(GameEvent.LIGHTNING_STRIKE);
            }
        }

        --this.life;
        if (this.life < 0) {
            if (this.flashes == 0) {
                this.discard();
            } else if (this.life < -this.random.nextInt(10)) {
                --this.flashes;
                this.life = 1;
                this.seed = this.random.nextLong();
            }
        }

        if (this.life >= 0) {
            if (!(this.level instanceof ServerLevel serverLevel)) {
                this.level.setSkyFlashTime(2);
            } else {
                List<Entity> list1 = this.level.getEntities(this, new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D), Entity::isAlive);

                for(Entity entity : list1) {
                    if (!EventHooks.onEntityStruckByLightning(entity, this))
                        entity.thunderHit(serverLevel, this);
                }

                this.hitEntities.addAll(list1);

            }
        }

    }

}
