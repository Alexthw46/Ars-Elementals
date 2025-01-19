package alexthw.ars_elemental.documentation;

import alexthw.ars_elemental.recipe.ElementalArmorRecipe;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.entry.PedestalRecipeEntry;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AEArmorEntry extends PedestalRecipeEntry {
    RecipeHolder<ElementalArmorRecipe> apparatusRecipe;

    public AEArmorEntry(RecipeHolder<ElementalArmorRecipe> recipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.apparatusRecipe = recipe;
        this.title = Component.translatable("block.ars_nouveau.enchanting_apparatus");
        if(recipe != null && recipe.value() != null) {
            this.outputStack = recipe.value().result();
            this.ingredients = recipe.value().pedestalItems();
            this.reagentStack = recipe.value().reagent();
        }
    }

    public static SinglePageCtor create(RecipeHolder<ElementalArmorRecipe> recipe){
        return (parent, x, y, width, height) -> new AEArmorEntry(recipe, parent, x, y, width, height);
    }
}