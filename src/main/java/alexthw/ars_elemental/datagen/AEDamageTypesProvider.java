package alexthw.ars_elemental.datagen;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.registry.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AEDamageTypesProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, AEDamageTypesProvider::bootstrap);


    public static void bootstrap(BootstapContext<DamageType> ctx) {
        ctx.register(ModRegistry.POISON, new DamageType("poison", 0.1F));
        ctx.register(ModRegistry.HELLFIRE, new DamageType("hellfire", 0.1F));
        ctx.register(ModRegistry.SPARK, new DamageType("spark", 0.1F));
    }

    public AEDamageTypesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ArsElemental.MODID));
    }

    @Override
    @NotNull
    public String getName() {
        return "Ars Elemental's Damage Type Data";
    }
}
