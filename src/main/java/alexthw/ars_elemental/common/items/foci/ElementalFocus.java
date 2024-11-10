package alexthw.ars_elemental.common.items.foci;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.client.TooltipUtils;
import alexthw.ars_elemental.common.items.ElementalCurio;
import alexthw.ars_elemental.registry.ModItems;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;

import static alexthw.ars_elemental.ConfigHandler.COMMON;
import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.ELEMENTAL_AIR;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ElementalFocus extends ElementalCurio implements ISchoolFocus {

    protected SpellSchool element;

    @Override
    public double getDiscount() {
        return COMMON.LesserFocusDiscount.get();
    }

    public ElementalFocus(Properties properties, SpellSchool element) {
        super(properties);
        this.element = element;
    }

    public void appendHoverText(@NotNull ItemStack pStack, Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("tooltip.ars_elemental.focus_element"));
        TooltipUtils.addOnShift(pTooltipComponents, () -> {
            pTooltipComponents.add(Component.translatable("tooltip.ars_elemental.focus_boost", element.getTextComponent()));
            pTooltipComponents.add(Component.translatable(this instanceof GreaterElementalFocus ? "tooltip.ars_elemental.focus_element_mana." + element.getId() : "tooltip.ars_elemental.focus_malus"));
        }, "focus");
    }


    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer() instanceof ServerPlayer player && player.getUUID().equals(ArsElemental.Dev) && player.isCrouching()) {
            pContext.getLevel().addFreshEntity(new ItemEntity(pContext.getLevel(), player.getX(), player.getY(), player.getZ(), ModItems.DEBUG_ICON.get().getDefaultInstance()));
        }
        return super.useOn(pContext);
    }

    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        // If the spell is an elemental spell, and the element of the focus is the same as the spell, then amplify the spell.
        if (SpellSchools.ELEMENTAL.isPartOfSchool(spellPart)) {
            if (element.isPartOfSchool(spellPart)) {
                builder.addAmplification(getBoostMultiplier());
            } else {
                // If the spell is an elemental spell, and the element of the focus is not the same as the spell, then dampen the spell.
                builder.addAmplification(getMalusMultiplier());
            }
        }
        return builder;
    }

    public SpellSchool getSchool() {
        return element;
    }

    @Override
    public boolean canWalkOnPowderedSnow(SlotContext slotContext, ItemStack stack) {
        return stack.getItem() instanceof ElementalFocus && ((ElementalFocus) stack.getItem()).element == ELEMENTAL_AIR;
    }

    double getBoostMultiplier() {
        return switch (element.getId()) {
            case "fire" -> COMMON.FireMasteryBuff.get();
            case "water" -> COMMON.WaterMasteryBuff.get();
            case "air" -> COMMON.AirMasteryBuff.get();
            case "earth" -> COMMON.EarthMasteryBuff.get();
            default -> 0;
        };
    }

    double getMalusMultiplier() {
        return switch (element.getId()) {
            case "fire" -> COMMON.FireMasteryDebuff.get();
            case "water" -> COMMON.WaterMasteryDebuff.get();
            case "air" -> COMMON.AirMasteryDebuff.get();
            case "earth" -> COMMON.EarthMasteryDebuff.get();
            default -> 0;
        };
    }

    @NotNull
    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return switch (element.getId()) {
            case "fire" -> new ICurio.SoundInfo(SoundEvents.FIRE_AMBIENT, 1, 1);
            case "water" -> new ICurio.SoundInfo(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 1, 1);
            case "earth" -> new ICurio.SoundInfo(SoundEvents.ROOTED_DIRT_BREAK, 1, 1);
            case "air" -> new ICurio.SoundInfo(SoundEvents.LIGHTNING_BOLT_THUNDER, 1, 1);
            default -> super.getEquipSound(slotContext, stack);
        };
    }

}
