package alexthw.ars_elemental.mixin;

import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectEvaporate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EffectEvaporate.class, remap = false)
public abstract class EvaporateMixin extends AbstractEffect {


    public EvaporateMixin(String tag, String description) {
        super(tag, description);
    }

    @Inject(method = "evaporate", at = @At("HEAD"), cancellable = true)
    public void evaporate(Level world, BlockPos p, BlockHitResult rayTraceResult, LivingEntity shooter, SpellContext context, SpellResolver resolver, CallbackInfo ci) {

        BlockState state = world.getBlockState(p);
        if (state.getBlock() == Blocks.MUD) {
            if (BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, p)) {

                world.setBlockAndUpdate(p, Blocks.CLAY.defaultBlockState());
                ci.cancel();
            }
        }
    }

}
