package alexthw.ars_elemental.common.entity.spells;

import alexthw.ars_elemental.registry.ModEntities;
import alexthw.ars_elemental.util.GlyphEffectUtil;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityLingeringSpell;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class EntityMagnetSpell extends EntityLingeringSpell {

    List<Predicate<Entity>> ignored;
    public static final EntityDataAccessor<Float> DURATION_UP = SynchedEntityData.defineId(EntityMagnetSpell.class, EntityDataSerializers.FLOAT);
    LivingEntity tracked;

    public EntityMagnetSpell(EntityType<? extends EntityProjectileSpell> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityMagnetSpell(Level worldIn) {
        super(ModEntities.LINGER_MAGNET.get(), worldIn);
    }

    static public EntityMagnetSpell createMagnet(Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, Vec3 location) {
        EntityMagnetSpell magnet = new EntityMagnetSpell(world);
        magnet.ignored = makeIgnores(shooter, spellContext.getSpell(), spellContext.getCurrentIndex() + 1);
        magnet.setPos(location);
        magnet.setAoe((float) spellStats.getAoeMultiplier());
        magnet.setOwner(shooter);
        magnet.setExtendedTime(spellStats.getDurationMultiplier());
        magnet.setColor(spellContext.getColors());
        return magnet;
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntities.LINGER_MAGNET.get();
    }

    @Override
    public boolean shouldFall() {
        return false;
    }

    @Override
    public float getAoe() {
        return super.getAoe() / 2;
    }

    @Override
    public void tickNextPosition() {
        if (this.tracked != null) {
            this.setPos(tracked.getX(), tracked.getY(), tracked.getZ());
        }
    }

    public float getExtendedTime() {
        return this.entityData.get(DURATION_UP);
    }

    private void setExtendedTime(double durationMultiplier) {
        this.entityData.set(DURATION_UP, (float) durationMultiplier);
    }

    @Override
    public int getExpirationTime() {
        return 70 + (int) (getExtendedTime() * 200);
    }

    @Override
    public void tick() {

        age++;

        if (this.age > getExpirationTime()) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        if (level().isClientSide() && this.age > getParticleDelay()) {
            playParticles();
        }
        // Magnetize entities
        if (!level().isClientSide() && this.age % 5 == 0) {
            for (Entity entity : level().getEntities(this, new AABB(this.blockPosition()).inflate(getAoe()))) {
                if (testFilters(entity)) continue;
                Vec3 vec3d = new Vec3(this.getX() - entity.getX(), this.getY() - entity.getY(), this.getZ() - entity.getZ());
                if (vec3d.length() < 1) continue;
                entity.setDeltaMovement(entity.getDeltaMovement().add(vec3d.normalize()).scale(0.5F));
                entity.hurtMarked = true;
            }
        }
        tickNextPosition();
    }

    @Override
    public void playParticles() {
        alexthw.ars_elemental.util.ParticleUtil.gravityParticles(getOnPos(), level, random, getParticleColor(), Math.round(getAoe()), 25, 10);
        ParticleUtil.spawnLight(level, getParticleColor(), position().add(0, 0.5, 0), 5);
    }

    public boolean testFilters(Entity entity) {
        return ignored.stream().anyMatch(filter -> entity == this.tracked || filter.test(entity));
    }

    public static List<Predicate<Entity>> makeIgnores(LivingEntity shooter, Spell spell, int index) {
        List<Predicate<Entity>> ignore = new ArrayList<>();
        // prevent magnet from pulling itself and other lingering spells and familiars and entities that are ignored by filters
        ignore.add((entity -> entity instanceof EntityLingeringSpell));
        ignore.add((entity -> entity == shooter));
        ignore.add(entity -> entity instanceof FamiliarEntity);
        ignore.add(shooter::isAlliedTo);
        Set<IFilter> filters = GlyphEffectUtil.getFilters(spell.unsafeList(), index);
        if (!filters.isEmpty()) {
            ignore.add(entity -> GlyphEffectUtil.checkIgnoreFilters(entity, filters));
        }
        return ignore;
    }

    public void setTracked(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            this.tracked = livingEntity;
        }
    }

    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(DURATION_UP, 0.0F);
    }
}
