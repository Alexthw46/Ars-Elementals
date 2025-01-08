package alexthw.ars_elemental.registry;

import alexthw.ars_elemental.common.entity.DripstoneSpikeEntity;
import alexthw.ars_elemental.common.entity.FirenandoEntity;
import alexthw.ars_elemental.common.entity.IceSpikeEntity;
import alexthw.ars_elemental.common.entity.MermaidEntity;
import alexthw.ars_elemental.common.entity.familiars.FirenandoFamiliar;
import alexthw.ars_elemental.common.entity.familiars.MermaidFamiliar;
import alexthw.ars_elemental.common.entity.mages.*;
import alexthw.ars_elemental.common.entity.spells.EntityLerpedProjectile;
import alexthw.ars_elemental.common.entity.spells.EntityMagnetSpell;
import alexthw.ars_elemental.common.entity.spells.FlashLightning;
import alexthw.ars_elemental.common.entity.summon.*;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectDelay;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLaunch;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectWindshear;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static alexthw.ars_elemental.ArsElemental.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<MermaidEntity>> SIREN_ENTITY;
    public static final DeferredHolder<EntityType<?>, EntityType<MermaidFamiliar>> SIREN_FAMILIAR;
    public static final DeferredHolder<EntityType<?>, EntityType<FirenandoEntity>> FIRENANDO_ENTITY;
    public static final DeferredHolder<EntityType<?>, EntityType<FirenandoFamiliar>> FIRENANDO_FAMILIAR;

    public static final DeferredHolder<EntityType<?>, EntityType<WealdWalker>> FLASHING_WEALD_WALKER;

    public static final DeferredHolder<EntityType<?>, EntityType<EntityMageBase>> FIRE_MAGE;
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMageBase>> WATER_MAGE;
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMageBase>> AIR_MAGE;
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMageBase>> EARTH_MAGE;

    public static final DeferredHolder<EntityType<?>, EntityType<SummonSkeleHorse>> SKELEHORSE_SUMMON;
    public static final DeferredHolder<EntityType<?>, EntityType<SummonDirewolf>> DIREWOLF_SUMMON;
    public static final DeferredHolder<EntityType<?>, EntityType<SummonUndead>> WSKELETON_SUMMON;
    public static final DeferredHolder<EntityType<?>, EntityType<SummonDolphin>> DOLPHIN_SUMMON;
    public static final DeferredHolder<EntityType<?>, EntityType<SummonStrider>> STRIDER_SUMMON;
    public static final DeferredHolder<EntityType<?>, EntityType<SummonCamel>> CAMEL_SUMMON;


    public static final DeferredHolder<EntityType<?>, EntityType<AllyVhexEntity>> VHEX_SUMMON;
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMagnetSpell>> LINGER_MAGNET;
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLerpedProjectile>> LERP_PROJECTILE;
    public static final DeferredHolder<EntityType<?>, EntityType<FlashLightning>> FLASH_LIGHTNING;
    public static final DeferredHolder<EntityType<?>, EntityType<DripstoneSpikeEntity>> DRIPSTONE_SPIKE;
    public static final DeferredHolder<EntityType<?>, EntityType<IceSpikeEntity>> ICE_SPIKE;

    static {
        SIREN_ENTITY = registerEntity("siren_entity", 0.4F, 1.0F, MermaidEntity::new, MobCategory.WATER_CREATURE);
        SIREN_FAMILIAR = registerEntity("siren_familiar", 0.4F, 1.0F, MermaidFamiliar::new, MobCategory.WATER_CREATURE);

        FIRENANDO_ENTITY = addEntity("firenando_entity", 1.0F, 2.2F, true, false, FirenandoEntity::new, MobCategory.CREATURE);
        FIRENANDO_FAMILIAR = addEntity("firenando_familiar", 1.0F, 1.8F, true, false, FirenandoFamiliar::new, MobCategory.CREATURE);

        FIRE_MAGE = registerEntity("fire_mage", 0.5F, 1.8F, FireMage::new, MobCategory.MONSTER);
        WATER_MAGE = registerEntity("water_mage", 0.5F, 1.8F, WaterMage::new, MobCategory.MONSTER);
        AIR_MAGE = registerEntity("air_mage", 0.5F, 1.8F, AirMage::new, MobCategory.MONSTER);
        EARTH_MAGE = registerEntity("earth_mage", 0.5F, 1.8F, EarthMage::new, MobCategory.MONSTER);

        FLASHING_WEALD_WALKER = registerEntity("flashing_weald_walker", 1.4F, 3F,
                (EntityType<WealdWalker> type, Level world) -> {
                    WealdWalker walker = new WealdWalker(type, world);
                    walker.spell = new Spell(MethodProjectile.INSTANCE, EffectLaunch.INSTANCE, EffectLaunch.INSTANCE, EffectDelay.INSTANCE, EffectWindshear.INSTANCE);
                    walker.color = new ParticleColor(200, 150, 15);
                    return walker;
                }, MobCategory.CREATURE);

        SKELEHORSE_SUMMON = addEntity("summon_skelehorse", 1.4F, 1.6F, true, true, SummonSkeleHorse::new, MobCategory.CREATURE);
        CAMEL_SUMMON = addEntity("summon_camel", 1.7F, 2.375F, true, true, SummonCamel::new, MobCategory.CREATURE);

        DIREWOLF_SUMMON = registerEntity("summon_direwolf", 0.9F, 1.0F, SummonDirewolf::new, MobCategory.CREATURE);
        WSKELETON_SUMMON = registerEntity("summon_wskeleton", 1.0F, 1.8F, SummonUndead::new, MobCategory.CREATURE);
        DOLPHIN_SUMMON = addEntity("summon_dolphin", 0.9F, 0.6F, false, true, SummonDolphin::new, MobCategory.WATER_CREATURE);
        STRIDER_SUMMON = addEntity("summon_strider", 0.9F, 1.7F, true, true, SummonStrider::new, MobCategory.CREATURE);

        VHEX_SUMMON = registerEntity("summon_vhex", 0.4F, 0.8F, AllyVhexEntity::new, MobCategory.MONSTER);
        LINGER_MAGNET = addEntity("linger_magnet", 0.5F, 0.5F, true, true, EntityMagnetSpell::new, MobCategory.MISC);
        LERP_PROJECTILE = addEntity("lerp", 0.5F, 0.5F, true, true, EntityLerpedProjectile::new, MobCategory.MISC);
        FLASH_LIGHTNING = addEntity("flash_lightning", 0.5F, 0.5F, true, true, FlashLightning::new, MobCategory.MISC);
        DRIPSTONE_SPIKE = addEntity("dripstone_spike", 1.0F, 1.0F, true, true, DripstoneSpikeEntity::new, MobCategory.MISC);
        ICE_SPIKE = addEntity("ice_spike", 1.0F, 1.0F, true, true, IceSpikeEntity::new, MobCategory.MISC);

    }

    static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, float width, float height, EntityType.EntityFactory<T> factory, MobCategory kind) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, kind).setTrackingRange(16).sized(width, height).build(MODID + ":" + name));
    }

    static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> addEntity(String name, float width, float height, boolean fire, boolean noSave, EntityType.EntityFactory<T> factory, MobCategory kind) {
        return ENTITIES.register(name, () -> {
            EntityType.Builder<T> builder = EntityType.Builder.of(factory, kind)
                    .setTrackingRange(32)
                    .sized(width, height);
            if (noSave) {
                builder.noSave();
            }
            if (fire) {
                builder.fireImmune();
            }
            return builder.build(MODID + ":" + name);
        });
    }


    @SubscribeEvent
    public static void registerSP(RegisterSpawnPlacementsEvent event) {

        event.register(FLASHING_WEALD_WALKER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, com.hollingsworth.arsnouveau.setup.registry.ModEntities::genericGroundSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SIREN_ENTITY.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (p_186238_, p_186239_, p_186240_, p_186241_, p_186242_) -> MermaidEntity.checkSurfaceWaterAnimalSpawnRules(p_186239_, p_186241_), RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(FIRE_MAGE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::surfaceSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(AIR_MAGE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::surfaceSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(WATER_MAGE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::surfaceSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EARTH_MAGE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::surfaceSpawn, RegisterSpawnPlacementsEvent.Operation.AND);

    }

    public static boolean surfaceSpawn(EntityType<? extends Entity> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return worldIn.getBlockState(pos.below()).is(BlockTags.DIRT) && pos.getY() > 45 && worldIn.getRawBrightness(pos, 0) <= 4;
    }

}
