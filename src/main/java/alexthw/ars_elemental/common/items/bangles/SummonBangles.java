package alexthw.ars_elemental.common.items.bangles;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.item.ISchoolBangle;
import alexthw.ars_elemental.common.items.ElementalCurio;
import alexthw.ars_elemental.registry.ModRegistry;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class SummonBangles extends ElementalCurio implements ISchoolBangle {

    public SummonBangles(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        map.put(ModRegistry.SUMMON_POWER.get(), new AttributeModifier(uuid, ArsElemental.MODID + ":summon_bangle", 2.d, AttributeModifier.Operation.ADDITION));
        return map;
    }

    @Override
    public @NotNull SpellSchool getSchool() {
        return SpellSchools.CONJURATION;
    }
}
