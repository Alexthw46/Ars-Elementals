package alexthw.ars_elemental.common.items.bangles;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.item.ISchoolBangle;
import alexthw.ars_elemental.common.items.ElementalCurio;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class AirBangles extends ElementalCurio implements ISchoolBangle {

    public AirBangles(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull SpellSchool getSchool() {
        return SpellSchools.ELEMENTAL_AIR;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, ArsElemental.MODID + ":air_bangle_speed", 0.06f, AttributeModifier.Operation.ADDITION));
        map.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(uuid, ArsElemental.MODID + ":air_bangle_kback", 1.2f, AttributeModifier.Operation.ADDITION));
        return map;
    }

}
