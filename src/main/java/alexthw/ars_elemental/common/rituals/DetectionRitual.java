package alexthw.ars_elemental.common.rituals;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;

import java.util.List;

import static alexthw.ars_elemental.ArsElemental.prefix;

public class DetectionRitual extends AbstractRitual {

    @Override
    public String getName() {
        return "Detection";
    }

    @Override
    public String getLangDescription() {
        return "Makes all monsters in a 128 blocks radius glow for 10 minutes.";
    }

    @Override
    protected void tick() {

        if (getWorld() == null) return;

        ParticleUtil.spawnFallingSkyEffect(tile.ritual, tile, rand, getCenterColor().toWrapper());
        if (getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide())
            incrementProgress();

        // If the ritual is finished, apply the effect to all monsters in a 128 block radius.
        if (getWorld() instanceof ServerLevel level && getProgress() >= 15) {
            List<Monster> entities = level.getEntitiesOfClass(Monster.class, new AABB(tile.getBlockPos()).inflate(128));
            for (LivingEntity entity : entities) {
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 12000, 0, false, false));
            }
            setFinished();
        }
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(
                100 + rand.nextInt(155),
                50 + rand.nextInt(200),
                rand.nextInt(250));
    }

    @Override
    public int getSourceCost() {
        return 250;
    }

    @Override
    public String getLangName() {
        return "Detection";
    }

    @Override
    public ResourceLocation getRegistryName() {
        return prefix(ID);
    }

    public static String ID = "ritual_detection";

}
