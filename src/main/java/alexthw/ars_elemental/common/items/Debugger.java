package alexthw.ars_elemental.common.items;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.common.entity.mages.*;
import alexthw.ars_elemental.common.items.foci.ElementalFocus;
import alexthw.ars_elemental.registry.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.*;

public class Debugger extends ElementalFocus {

    private final List<SpellSchool> elements = List.of(ELEMENTAL_AIR, ELEMENTAL_FIRE, ELEMENTAL_EARTH, ELEMENTAL_WATER);
    private final RandomSource random;

    public Debugger(Properties properties) {
        super(properties, NECROMANCY);
        random = RandomSource.createNewThreadLocalInstance();
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, Player player, @NotNull Entity entity) {
        if (player.getUUID().equals(ArsElemental.Dev) && entity instanceof Player target)
            target.addEffect(new MobEffectInstance(ModPotions.HYMN_OF_ORDER, 6400));
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        //get a random school from elements
        String element = elements.get(random.nextInt(elements.size())).getId();
        if (pContext.getPlayer() instanceof Player player && player.getOffhandItem().getItem() instanceof ISchoolFocus focus) {
            element = focus.getSchool().getId();
        }
        if (pContext.getLevel() instanceof ServerLevel level) {
            EntityMageBase mage =
                    switch (element) {
                        case "fire" -> new FireMage(level);
                        case "air" -> new AirMage(level);
                        case "earth" -> new EarthMage(level);
                        default -> new WaterMage(level);
                    };
            mage.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
            mage.setPos(pContext.getClickLocation());
            level.addFreshEntity(mage);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext context, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Dev-only item, spawns the wip mages on right click, shift-use to cycle elements."));
    }
}
