package alexthw.ars_elemental;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {

    public static class Common {

        public final ModConfigSpec.ConfigValue<Double> MajorFocusDiscount;
        public final ModConfigSpec.ConfigValue<Double> LesserFocusDiscount;

        public final ModConfigSpec.ConfigValue<Double> FireMasteryBuff;
        public final ModConfigSpec.ConfigValue<Double> WaterMasteryBuff;
        public final ModConfigSpec.ConfigValue<Double> AirMasteryBuff;
        public final ModConfigSpec.ConfigValue<Double> EarthMasteryBuff;

        public final ModConfigSpec.ConfigValue<Double> FireMasteryDebuff;
        public final ModConfigSpec.ConfigValue<Double> WaterMasteryDebuff;
        public final ModConfigSpec.ConfigValue<Double> AirMasteryDebuff;
        public final ModConfigSpec.ConfigValue<Double> EarthMasteryDebuff;

        public final ModConfigSpec.ConfigValue<Boolean> EnableGlyphEmpowering;
        public final ModConfigSpec.ConfigValue<Boolean> EnableRegenBonus;
        public final ModConfigSpec.ConfigValue<Boolean> IFRAME_SKIP;

        public static ModConfigSpec.IntValue ARMOR_MAX_MANA;
        public static ModConfigSpec.IntValue ARMOR_MANA_REGEN;
        public static ModConfigSpec.BooleanValue HOMING_GLOWING;
        public static ModConfigSpec.BooleanValue FIRENANDO_KILL;

        public static ModConfigSpec.IntValue SIREN_MANA_COST;
        public static ModConfigSpec.IntValue SIREN_MAX_PROGRESS;
        public static ModConfigSpec.IntValue SIREN_BASE_ITEM;
        public static ModConfigSpec.IntValue SIREN_UNIQUE_BONUS;
        public static ModConfigSpec.DoubleValue SIREN_TREASURE_BONUS;
        public static ModConfigSpec.IntValue SIREN_QUANTITY_CAP;

        public static ModConfigSpec.IntValue WATER_URN_COST;
        public static ModConfigSpec.IntValue AIR_ELEVATOR_COST;
        public static ModConfigSpec.IntValue LAVA_ELEVATOR_COST;
        public static ModConfigSpec.IntValue WATER_ELEVATOR_COST;

        public static ModConfigSpec.BooleanValue LIGHTNINGS_BIOME;
        public static ModConfigSpec.BooleanValue SOULBOUND_LOOT;
        public static ModConfigSpec.IntValue PIERCE_LENS_LIMIT;

        public static ModConfigSpec.IntValue SQUIRREL_REFRESH_RATE;
        public static ModConfigSpec.BooleanValue MAGES_AGGRO;
        public static final Integer TREE_SPAWN_RATE = 200;
        public static ModConfigSpec.IntValue EXTRA_BIOMES;


        public Common(ModConfigSpec.Builder builder) {

            IFRAME_SKIP = builder.comment("Enable iframe skip glyph recipe").define("frame_skip_recipe", false);

            builder.comment("Adjust these values to balance how much the matching spell cost gets discounted by foci.")
                    .push("Elemental Spell Foci - Discount");

            LesserFocusDiscount = builder.comment("Adjust this value to define how much the matching spell cost gets discounted by the lesser focus")
                    .defineInRange("elemental_less_focus_discount", 0.15D, 0.0D, 0.99D);
            MajorFocusDiscount = builder.comment("Adjust this value to define how much the matching spell cost gets discounted by the greater focus")
                    .defineInRange("elemental_maj_focus_discount", 0.25D, 0.0D, 0.99D);
            builder.pop();

            builder.comment("Adjust these values to balance how much a spell gets amplified by the matching spell focus, doubled for major foci.")
                    .push("Elemental Spell Foci - Amplify");

            FireMasteryBuff = builder.define("fire_focus_buff", 1.0D);
            WaterMasteryBuff = builder.define("water_focus_buff", 1.0D);
            AirMasteryBuff = builder.define("air_focus_buff", 1.0D);
            EarthMasteryBuff = builder.define("earth_focus_buff", 1.0D);

            builder.pop();

            builder.comment("Adjust these values to balance how much an elemental spell gets dampened by a not-matching lesser spell focus")
                    .push("Elemental Spell Foci - Dampening");
            FireMasteryDebuff = builder.define("fire_focus_debuff", -1.0D);
            WaterMasteryDebuff = builder.define("water_focus_debuff", -1.0D);
            AirMasteryDebuff = builder.define("air_focus_debuff", -1.0D);
            EarthMasteryDebuff = builder.define("earth_focus_debuff", -1.0D);

            builder.pop();

            builder.comment("Enable or disable the passive bonus of the foci").push("Elemental Spell Foci - Abilities");

            EnableGlyphEmpowering = builder.comment("Enable glyph empowering").define("glyph_empower", true);
            EnableRegenBonus = builder.comment("Enable regen bonus under special conditions").define("regen_bonus", true);

            builder.pop();

            builder.comment("Adjust Elemental Armor Mana Buffs").push("Elemental Armors");

            ARMOR_MAX_MANA = builder.comment("Max mana bonus for each elemental armor piece").defineInRange("armorMaxMana", 100, 0, 10000);
            ARMOR_MANA_REGEN = builder.comment("Mana regen bonus for each elemental armor piece").defineInRange("armorManaRegen", 4, 0, 100);

            builder.pop();

            builder.push("Mermaid Fishing");
            SIREN_MANA_COST = builder.comment("How much source mermaids consume per generation").defineInRange("mermaidManaCost", 1000, 0, 10000);
            SIREN_MAX_PROGRESS = builder.comment("How many channels must occur before a siren produces loot.").defineInRange("mermaidMaxProgress", 30, 0, 300);
            SIREN_UNIQUE_BONUS = builder.comment("Max number of extra item rolls a shrine produces if the mood is high.").defineInRange("mermaidScoreBonus", 2, 0, 10);
            SIREN_TREASURE_BONUS = builder.comment("Chance multiplier to produce a treasure relative to the siren shrine score.").defineInRange("mermaidTreasureBonus", 0.002D, 0D, 1D);
            SIREN_BASE_ITEM = builder.comment("Base number of items rolls a shrine produces per cycle.").defineInRange("mermaidBaseItems", 1, 0, 300);
            SIREN_QUANTITY_CAP = builder.comment("Max number of items a siren shrine can produce per cycle.").defineInRange("mermaidQuantityCap", 5, 0, 300);
            builder.pop();

            builder.push("Source cost");

            WATER_URN_COST = builder.comment("How much source does the water urn consume.").defineInRange("waterUrnCost", 100, 0, 10000);
            AIR_ELEVATOR_COST = builder.comment("How much source does the slipstream elevator consume.").defineInRange("airElevatorCost", 10, 0, 1000);
            WATER_ELEVATOR_COST = builder.comment("How much source does the bubble elevator consume.").defineInRange("waterElevatorCost", 0, 0, 1000);
            LAVA_ELEVATOR_COST = builder.comment("How much source does the magmatic elevator consume.").defineInRange("lavaElevatorCost", 0, 0, 1000);

            builder.pop();


            builder.push("Misc");
            FIRENANDO_KILL = builder.comment("If enabled, flarecannons will simply die and drop the charm, instead of deactivating, if killed by their owner").define("flarecannon_owner_kill", true);
            EXTRA_BIOMES = builder.comment("Set over 0 to enable archwood forests with specific trees").defineInRange("extra_biomes", 0, 0, 100);
            LIGHTNINGS_BIOME = builder.comment("Set to false to disable the lightning crashing often on flashing archwood biome(s).").define("always_thunder", true);
            HOMING_GLOWING = builder.comment("If enabled, homing will be able to target mobs only if they're glowing").define("homing_nerf", false);
            SQUIRREL_REFRESH_RATE = builder.comment("Define the refresh rate of the Squirrel Ritual buff, in ticks.").defineInRange("squirrelRefreshRate", 600, 1, Integer.MAX_VALUE);
            SOULBOUND_LOOT = builder.comment("If enabled, soulbound enchantment can appear in randomly enchanted loot chests.").define("soulbound_loot", true);
            PIERCE_LENS_LIMIT = builder.comment("Define the maximum number of pierce that a lens can apply to a spell.").defineInRange("pierceLensLimit", 10, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("Mobs-Disabled");

            MAGES_AGGRO = builder.comment("If true, the wandering mages will target players too, unless they wear the focus of the same school.").define("magesAggro", true);

            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    public static class Client {
        public static ModConfigSpec.ConfigValue<Boolean> EnableSFRendering;
        public static ModConfigSpec.ConfigValue<Boolean> NetheriteTexture;

        public Client(ModConfigSpec.Builder builder) {
            builder.push("Visual Configs");

            EnableSFRendering = builder.comment("Enables the rendering of the spell focus while equipped").define("Enable SpellFocusRender", true);
            NetheriteTexture = builder.comment("Enables the black texture of the spell book while upgraded").define("Enable BlackBookTexture", true);

            builder.pop();
        }
    }

    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {

        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

        final Pair<Client, ModConfigSpec> specClientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specClientPair.getRight();
        CLIENT = specClientPair.getLeft();

    }

}
