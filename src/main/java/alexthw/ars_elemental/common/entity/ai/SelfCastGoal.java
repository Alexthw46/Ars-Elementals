package alexthw.ars_elemental.common.entity.ai;

import alexthw.ars_elemental.common.entity.mages.EntityMageBase;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import static alexthw.ars_elemental.util.ParticleUtil.schoolToColor;

public class SelfCastGoal<T extends EntityMageBase> extends CastGoal<T> {

    int index;
    Spell spell;

    public SelfCastGoal(T entity, int cooldown, int index, Supplier<Boolean> canUse, int animId, int delayTicks) {
        super(entity, 1.0, cooldown, 100, canUse, animId, delayTicks);
        this.index = index;
    }

    @Override
    public void tick() {
        super.tick();
        if (spell == null) spell = mob.sSpells.get(index);
        if (mob.castCooldown <= 0) {
            ParticleColor color = schoolToColor(mob.school.getId());
            EntitySpellResolver resolver = new EntityMageBase.MageResolver(new SpellContext(mob.level, this.spell, this.mob, new LivingCaster(this.mob)).withColors(color), mob.getSchool());
            resolver.onCast(ItemStack.EMPTY, mob.level);
            mob.castCooldown = 20;
            stop();
        }
    }

    @Override
    public void start() {
        super.start();
        Networking.sendToNearbyClient(mob.level, mob, new PacketAnimEntity(mob.getId(), animId));
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse.get();
    }

}
