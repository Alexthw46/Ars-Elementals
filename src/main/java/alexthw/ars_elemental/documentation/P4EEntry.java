package alexthw.ars_elemental.documentation;

import alexthw.ars_elemental.common.components.ElementProtectionFlag;
import alexthw.ars_elemental.recipe.NetheriteUpgradeRecipe;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.entry.PedestalRecipeEntry;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

public class P4EEntry extends PedestalRecipeEntry {
    RecipeHolder<NetheriteUpgradeRecipe> apparatusRecipe;

    public P4EEntry(RecipeHolder<NetheriteUpgradeRecipe> recipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.apparatusRecipe = recipe;
        this.title = Component.translatable("block.ars_nouveau.enchanting_apparatus");
        if (recipe != null && recipe.value() != null) {
            this.outputStack = recipe.value().result().copy();
            this.outputStack.set(ModRegistry.P4E.get(), new ElementProtectionFlag(true));
            this.ingredients = recipe.value().pedestalItems();
            this.reagentStack = recipe.value().reagent();
        }
    }

    public static SinglePageCtor create(RecipeHolder<NetheriteUpgradeRecipe> recipe) {
        return (parent, x, y, width, height) -> new P4EEntry(recipe, parent, x, y, width, height);
    }
}
