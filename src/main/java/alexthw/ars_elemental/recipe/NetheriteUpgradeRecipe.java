package alexthw.ars_elemental.recipe;

import alexthw.ars_elemental.common.components.ElementProtectionFlag;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ApparatusRecipeInput;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.Serializers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NetheriteUpgradeRecipe extends EnchantingApparatusRecipe {

    public NetheriteUpgradeRecipe(Ingredient reagent, List<Ingredient> stacks, int cost) {
        super(reagent, ItemStack.EMPTY, stacks, cost, true);
    }

    @Override
    public boolean excludeJei() {
        return true;
    }


    @Override
    public boolean matches(ApparatusRecipeInput input, Level level) {
        ElementProtectionFlag flag = input.catalyst().get(ModRegistry.P4E);
        return super.matches(input, level) && (flag == null || !flag.flag());
    }

    @Override
    public @NotNull ItemStack assemble(ApparatusRecipeInput input, HolderLookup.@NotNull Provider p_346030_) {
        ItemStack temp = input.catalyst().copy();
        temp.set(ModRegistry.P4E, new ElementProtectionFlag(true));
        return temp;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRegistry.NETHERITE_UP.get();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRegistry.NETHERITE_UP_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<NetheriteUpgradeRecipe> {

        public static MapCodec<NetheriteUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("reagent").forGetter(NetheriteUpgradeRecipe::reagent),
                Ingredient.CODEC.listOf().fieldOf("pedestalItems").forGetter(NetheriteUpgradeRecipe::pedestalItems),
                Codec.INT.fieldOf("sourceCost").forGetter(NetheriteUpgradeRecipe::sourceCost)
        ).apply(instance, NetheriteUpgradeRecipe::new));

        @Override
        public @NotNull MapCodec<NetheriteUpgradeRecipe> codec() {
            return CODEC;
        }

        public static StreamCodec<RegistryFriendlyByteBuf, NetheriteUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                NetheriteUpgradeRecipe::reagent,
                Serializers.INGREDIENT_LIST_STREAM,
                NetheriteUpgradeRecipe::pedestalItems,
                ByteBufCodecs.VAR_INT,
                NetheriteUpgradeRecipe::sourceCost,
                NetheriteUpgradeRecipe::new
        );

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, NetheriteUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
