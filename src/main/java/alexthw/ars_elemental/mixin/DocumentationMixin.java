package alexthw.ars_elemental.mixin;

import alexthw.ars_elemental.documentation.AEArmorEntry;
import alexthw.ars_elemental.recipe.ElementalArmorRecipe;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.setup.registry.Documentation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Documentation.class)
public class DocumentationMixin {

    @Inject(method = "getRecipePages(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/List;", at = @At("TAIL"))
    private static void getRecipePages(ResourceLocation recipeId, CallbackInfoReturnable<List<SinglePageCtor>> cir) {
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();
        RecipeHolder<ElementalArmorRecipe> recipe = manager.byKeyTyped(ModRegistry.ELEMENTAL_ARMOR_UP.get(), recipeId);

        if (recipe != null) {
            cir.getReturnValue().add(AEArmorEntry.create(recipe));
        }
    }


}
