package alexthw.ars_elemental.common.items.armor;

import alexthw.ars_elemental.ArsElemental;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

import static alexthw.ars_elemental.ArsElemental.MODID;

public class AAMaterials {

    public static final DeferredRegister<ArmorMaterial> A_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, MODID);

    public static final EnumMap<ArmorItem.Type, Integer> ARMOR_SLOT_PROTECTION = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 2);
        map.put(ArmorItem.Type.LEGGINGS, 5);
        map.put(ArmorItem.Type.CHESTPLATE, 6);
        map.put(ArmorItem.Type.HELMET, 2);
        map.put(ArmorItem.Type.BODY, 4);
    });

    public final static Holder<ArmorMaterial> fire = A_MATERIALS.register("medium_fire", () -> new ArmorMaterial(ARMOR_SLOT_PROTECTION
            , 40, new Holder.Direct<>(SoundEvents.GENERIC_BURN), () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ArsElemental.prefix("medium_fire"))), 2.0f, 0));
    public final static Holder<ArmorMaterial> water = A_MATERIALS.register("medium_water", () -> new ArmorMaterial(ARMOR_SLOT_PROTECTION
            , 40, new Holder.Direct<>(SoundEvents.BREWING_STAND_BREW), () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ArsElemental.prefix("medium_water"))), 2.0f, 0));
    public final static Holder<ArmorMaterial> earth = A_MATERIALS.register("medium_earth", () -> new ArmorMaterial(ARMOR_SLOT_PROTECTION, 40, new Holder.Direct<>(SoundEvents.GLASS_PLACE), () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ArsElemental.prefix("medium_earth"))), 2.0f, 0.02F));
    public final static Holder<ArmorMaterial> air = A_MATERIALS.register("medium_air", () -> new ArmorMaterial(ARMOR_SLOT_PROTECTION, 40, SoundEvents.ARMOR_EQUIP_ELYTRA, () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ArsElemental.prefix("medium_air"))), 2.0f, 0));

}