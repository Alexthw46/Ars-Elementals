package alexthw.ars_elemental.mixin;

import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCut;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EffectCut.class)
public class CutMixin {

    @WrapOperation(method = "onResolveEntity", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/common/spell/effect/EffectCut;attemptDamage(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lcom/hollingsworth/arsnouveau/api/spell/SpellStats;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean attemptDamage(EffectCut instance, Level level, LivingEntity living, SpellStats spellStats, SpellContext spellContext, SpellResolver spellResolver, Entity entity, DamageSource damageSource, float v, Operation<Boolean> original) {
        if (ISchoolFocus.airCheck(spellResolver))
            return original.call(instance, level, living, spellStats, spellContext, spellResolver, entity, DamageUtil.source(level, ModRegistry.CUT, ANFakePlayer.getOrFakePlayer((ServerLevel) level, living)), v);
        return original.call(instance, level, living, spellStats, spellContext, spellResolver, entity, damageSource, v);
    }

}
