package alexthw.ars_elemental.client.patchouli;


import alexthw.ars_elemental.recipe.ElementalArmorRecipe;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ElementalArmorProcessor implements IComponentProcessor {

    private RecipeHolder<? extends ElementalArmorRecipe> holder;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        RecipeManager manager = level.getRecipeManager();
        String recipeID = variables.get("recipe", level.registryAccess()).asString();
        try {
            holder = (RecipeHolder<? extends ElementalArmorRecipe>) manager.byKey(ResourceLocation.tryParse(recipeID)).orElse(null);
        } catch (Exception ignored) {
        }
    }

    @Override
    public @NotNull IVariable process(Level level, String key) {
        if (holder == null) return IVariable.empty();
        var recipe = holder.value();
        return switch (key) {
            case "reagent" -> IVariable.wrapList(Arrays.stream(recipe.reagent().getItems())
                    .map(ItemStack::copy)
                    .peek(i -> i.set(DataComponentRegistry.ARMOR_PERKS, i.get(DataComponentRegistry.ARMOR_PERKS).setTier(2)))
                    .map(i -> IVariable.from(i, level.registryAccess())).collect(Collectors.toList()), level.registryAccess());
            case "recipe" -> IVariable.wrap(holder.id().toString(), level.registryAccess());
            case "tier" -> IVariable.wrap(recipe.getOutputComponent().getString(), level.registryAccess());
            case "output" -> IVariable.from(recipe.result(), level.registryAccess());
            case "footer" -> IVariable.wrap(recipe.result().getItem().getDescriptionId(), level.registryAccess());
            default -> IVariable.empty();
        };
    }

}
