package alexthw.ars_elemental.recipe.jei;

import alexthw.ars_elemental.recipe.ElementalArmorRecipe;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.jei.EnchantingApparatusRecipeCategory;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ElementalUpgradeRecipeCategory extends EnchantingApparatusRecipeCategory<ElementalArmorRecipe> {
    public ElementalUpgradeRecipeCategory(IGuiHelper helper) {
        super(helper);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ElementalArmorRecipe recipe, IFocusGroup focuses) {
        MultiProvider provider = multiProvider.apply(recipe);
        List<Ingredient> inputs = provider.input();
        double angleBetweenEach = 360.0 / inputs.size();
        if (provider.optionalCenter() != null) {
            var stacks = provider.optionalCenter().getItems();
            var list = Arrays.stream(stacks).map(stack -> {
                var newStack = stack;
                ArmorPerkHolder armorPerkHolder = PerkUtil.getPerkHolder(stack);
                if (armorPerkHolder != null) {
                    newStack = stack.copy();
                    newStack.set(DataComponentRegistry.ARMOR_PERKS, armorPerkHolder.setTier(2));
                }
                return newStack;
            }).toList();
            builder.addSlot(RecipeIngredientRole.INPUT, 48, 45).addItemStacks(list);
        }
        for (Ingredient input : inputs) {
            builder.addSlot(RecipeIngredientRole.INPUT, (int) point.x, (int) point.y).addIngredients(input);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 10).addItemStacks(Collections.singletonList(provider.output()));
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.armor_upgrade");
    }

    @Override
    public RecipeType<ElementalArmorRecipe> getRecipeType() {
        return JeiArsExtraPlugin.ELEMENTAL_ARMOR_TYPE;
    }

    @Override
    public void draw(ElementalArmorRecipe recipe, @NotNull IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font renderer = Minecraft.getInstance().font;
        guiGraphics.drawString(renderer, Component.translatable("ars_nouveau.tier", 3), 0, 0, 10, false);

        if (recipe.consumesSource())
            guiGraphics.drawString(renderer, Component.translatable("ars_nouveau.source", recipe.sourceCost()), 0, 100, 10, false);
    }

}
