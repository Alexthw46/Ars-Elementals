package alexthw.ars_elemental.common.items;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.ArsNouveauRegistry;
import alexthw.ars_elemental.common.entity.mages.*;
import alexthw.ars_elemental.common.items.foci.ElementalFocus;
import alexthw.ars_elemental.registry.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.*;

public class Debugger extends ElementalFocus {

    private final List<SpellSchool> elements = List.of(ELEMENTAL_AIR, ELEMENTAL_FIRE, ELEMENTAL_EARTH, ELEMENTAL_WATER);
    private int index;

    public Debugger(Properties properties) {
        super(properties, ArsNouveauRegistry.NECROMANCY);
        index = 0;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        var tag = pPlayer.getItemInHand(pUsedHand).getOrCreateTag();
        if (pPlayer.isCrouching() && !pLevel.isClientSide()) {
            index = ++index % 4;
            tag.putInt("element", index);
        }
        this.element = elements.get(tag.getInt("element"));
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player.getUUID().equals(ArsElemental.Dev) && entity instanceof Player target)
            target.addEffect(new MobEffectInstance(ModPotions.HYMN_OF_ORDER.get(), 6400));
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        if (pContext.getLevel() instanceof ServerLevel level) {
            EntityMageBase mage =
                    switch (element.getId()) {
                        case "fire" -> new FireMage(level);
                        case "air" -> new AirMage(level);
                        case "earth" -> new EarthMage(level);
                        default -> new WaterMage(level);
                    };
            mage.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
            mage.setPos(pContext.getClickLocation());
            level.addFreshEntity(mage);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
