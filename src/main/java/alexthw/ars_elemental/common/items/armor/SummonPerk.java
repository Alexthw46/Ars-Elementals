package alexthw.ars_elemental.common.items.armor;

import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

import static alexthw.ars_elemental.ArsElemental.prefix;

public class SummonPerk extends Perk {

    public static SummonPerk INSTANCE = new SummonPerk();

    public SummonPerk() {
        super(prefix("thread_summon"));
    }

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers.withModifierAdded(ModRegistry.SUMMON_POWER, new AttributeModifier(prefix("summon_power"), slotValue - 1, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
    }

    @Override
    public String getLangName() {
        return "Summoning";
    }

    @Override
    public String getLangDescription() {
        return "Reduce Summon Sickness by 10%% for each tier. Starting from tier 2, it also slightly increase damage dealt by summoned creatures.";
    }
}
