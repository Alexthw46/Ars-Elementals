package alexthw.ars_elemental.util;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectFreeze;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.Nullable;

public class GlyphEffectUtil {

    public static Player getPlayer(LivingEntity entity, ServerLevel world){
        return entity instanceof Player ? (Player) entity : FakePlayerFactory.getMinecraft(world);
    }
    public static void placeBlocks(BlockHitResult rayTraceResult, Level world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, BlockState toPlace) {
        ANFakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel) world);
        if (shooter==null) return;
        for (BlockPos pos : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats)) {
            pos =  rayTraceResult.isInside() ? pos : pos.relative(rayTraceResult.getDirection());
            if(!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos))
                continue;
            BlockState state = world.getBlockState(pos);
            if (state.getMaterial().isReplaceable() && world.isUnobstructed(toPlace, pos, CollisionContext.of(fakePlayer))){
                world.setBlockAndUpdate(pos, toPlace);
            }
        }
    }

    public static boolean hasFollowingEffect(SpellContext spellContext, AbstractEffect toFind) {
        for (AbstractSpellPart part : spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().getSpellSize())) {
            if (part instanceof AbstractEffect) {
                if (part == toFind) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

}
