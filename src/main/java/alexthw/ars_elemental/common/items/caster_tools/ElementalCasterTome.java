package alexthw.ars_elemental.common.items.caster_tools;

import alexthw.ars_elemental.api.item.ISchoolFocus;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.CasterTome;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElementalCasterTome extends CasterTome implements ISchoolFocus {
    private final SpellSchool school;

    public ElementalCasterTome(Properties properties, SpellSchool school) {
        super(properties);
        this.school = school;
    }

    @Override
    public SpellSchool getSchool() {
        return school;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        tooltip2.add(Component.translatable("tooltip.ars_elemental.caster_tome"));
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public double getDiscount() {
        return 0;
    }

    @Override
    public @NotNull ISpellCaster getSpellCaster(ItemStack stack) {
        return new TomeSpellCaster(stack) {
            @Override
            public SpellResolver getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn) {
                return new ETomeResolver(context, getSchool());
            }
        };
    }

    static class ETomeResolver extends SpellResolver {

        public SpellSchool getSchool() {
            return school;
        }

        private final SpellSchool school;

        public ETomeResolver(SpellContext context, SpellSchool school) {
            super(context);
            this.school = school;
        }

        @Override
        public boolean hasFocus(ItemStack stack) {
            return hasFocus(stack.getItem());
        }

        @Override
        public boolean hasFocus(Item item) {
            if (item instanceof ISchoolFocus focus) {
                return school == focus.getSchool();
            } else if (item == ItemsRegistry.SHAPERS_FOCUS.get()) {
                return school == SpellSchools.MANIPULATION;
            }
            return super.hasFocus(item);
        }

        @Override
        public SpellResolver getNewResolver(SpellContext context) {
            return new ETomeResolver(context, getSchool());
        }
    }
}
