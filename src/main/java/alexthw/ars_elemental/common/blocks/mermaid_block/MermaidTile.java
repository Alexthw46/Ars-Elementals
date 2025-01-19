package alexthw.ars_elemental.common.blocks.mermaid_block;

import alexthw.ars_elemental.ConfigHandler.Common;
import alexthw.ars_elemental.common.entity.MermaidEntity;
import alexthw.ars_elemental.common.entity.spells.EntityLerpedProjectile;
import alexthw.ars_elemental.registry.ModTiles;
import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.KelpPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static alexthw.ars_elemental.datagen.AETagsProvider.AEBlockTagsProvider.AQUARIUM_BONUS;


public class MermaidTile extends SummoningTile implements ITooltipProvider {

    public int progress;
    public int bonus;
    public boolean needsMana;

    public MermaidTile(BlockPos pos, BlockState state) {
        super(ModTiles.MERMAID_TILE.get(), pos, state);
    }

    public void convertedEffect() {
        super.convertedEffect();
        if (level instanceof ServerLevel world) {
            // when the block is converted, spawn a mermaid
            if (tickCounter >= 120) {
                converted = true;
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(SummoningTile.CONVERTED, true));
                MermaidEntity mermaid = new MermaidEntity(level, true);
                mermaid.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5);
                mermaid.setHome(getBlockPos());
                level.addFreshEntity(mermaid);
                ParticleUtil.spawnPoof(world, worldPosition.above());
                tickCounter = 0;
                return;
            }

            if (tickCounter % 10 == 0) {
                RandomSource r = level.random;
                int min = -2;
                int max = 2;
                EntityFollowProjectile proj1 = new EntityFollowProjectile(level, worldPosition.offset(r.nextInt(max - min) + min, 3, r.nextInt(max - min) + min), worldPosition, r.nextInt(255), r.nextInt(255), r.nextInt(255));
                level.addFreshEntity(proj1);
            }
        }

    }

    public int getMaxProgress() {
        return Common.SIREN_MAX_PROGRESS.get();
    }

    public void giveProgress() {
        if (progress < getMaxProgress() && level != null) {
            progress += 1;
            updateBlock();
        }

    }

    public void evaluateAquarium() {
        if (!(getLevel() instanceof ServerLevel world)) return;
        // create a map to track the blocks of the aquarium to get a score
        Set<BlockState> blocks = new HashSet<>();
        Set<EntityType<?>> entities = new HashSet<>();
        int score = 0;
        int water = 0;
        for (BlockPos b : BlockPos.betweenClosed(getBlockPos().north(16).west(16).below(10), getBlockPos().south(16).east(16).above(10))) {
            if (world.isOutsideBuildHeight(b))
                continue;
            BlockState block = world.getBlockState(b);
            // get the score of the block, and add it to the score if it is not already in the map and is not 0
            int points = getScore(block);
            if (points == 0 && world.getBlockEntity(b) instanceof MobJarTile tile) {
                if (tile.getEntity() instanceof LivingEntity jarred && jarred.getType().is(EntityTypeTags.AQUATIC)) {
                    entities.add(jarred.getType());
                }
            }
            switch (points) {
                case 0: //continue
                case 1: {
                    water++;
                }
                default: {
                    score += blocks.add(block) ? points : 0;
                }
            }
        }

        // if the aquarium has more than 50 water blocks, add 5 points for each unique entity type
        if (water > 50) {
            // first add jarred entities, with a smaller bonus
            score += entities.size() * 3;
            // use entities set to avoid duplicates while adding entities from the aquarium
            score += getNearbyEntities().stream().filter(b -> entities.add(b.getType())).mapToInt(b -> 5).sum();
        }

        bonus = score;
    }

    public int getScore(BlockState state) {

        // if the block is air, return 0
        if (state.isAir())
            return 0;

        // if the block is water, return 1
        if (state == Blocks.WATER.defaultBlockState())
            return 1;

        // if the block is a plant or coral, return 2
        if (state.is(AQUARIUM_BONUS) || state.getBlock() instanceof KelpPlantBlock || state.getBlock() instanceof CoralBlock)
            return 2;


        // otherwise, return 0
        return 0;
    }

    ItemStack getRod() {
        ItemStack rod = Items.FISHING_ROD.getDefaultInstance();
        if (this.level != null && this.bonus >= 25) {
            level.holder(Enchantments.LUCK_OF_THE_SEA).
                    ifPresent(enchantmentReference -> rod.enchant(enchantmentReference, bonus / 25));
        }
        return rod;
    }

    public void generateItems() {
        if (!(this.level instanceof ServerLevel server)) return;

        // get the loot tables for fishing and create a fake player to get the loot context
        ANFakePlayer fakePlayer = ANFakePlayer.getPlayer(server);
        ReloadableServerRegistries.Holder lootData = server.getServer().reloadableRegistries();
        LootTable lootTable = lootData.getLootTable(BuiltInLootTables.FISHING);
        LootTable lootTableTreasure = lootData.getLootTable(BuiltInLootTables.FISHING_TREASURE);
        LootTable lootTableJunk = lootData.getLootTable(BuiltInLootTables.FISHING_JUNK);

        LootParams lootContext = (new LootParams.Builder(server))
                .withParameter(LootContextParams.ORIGIN, fakePlayer.position())
                .withParameter(LootContextParams.TOOL, getRod())
                .create(LootContextParamSets.FISHING);

        boolean flag = true;
        List<ItemStack> list;

        // get the number of bonus rolls and the number of items to drop
        int bonus_rolls = Math.min(bonus / 25, Common.SIREN_UNIQUE_BONUS.get());
        int counter = 0;
        // roll the loot table for each bonus roll and drop the items, if the counter is greater than the cap, stop
        // if the bonus is greater than 30, there is at least 10% chance to get a treasure item, otherwise there is a 20% chance to get a junk item
        for (int i = 0; i < Common.SIREN_BASE_ITEM.get() + bonus_rolls; i++) {
            if (flag && bonus > 50 && this.level.random.nextDouble() < Math.max(0.1, bonus * Common.SIREN_TREASURE_BONUS.get())) {
                list = lootTableTreasure.getRandomItems(lootContext);
                flag = false;
            } else if (flag && bonus <= 25 && this.level.random.nextDouble() < 0.5F) {
                list = lootTableJunk.getRandomItems(lootContext);
            } else list = lootTable.getRandomItems(lootContext);

            // deposit the items into the inventory
            for (ItemStack item : list) {
                BlockUtil.insertItemAdjacent(level, worldPosition, item);
                counter++;
            }
            if (counter >= Common.SIREN_QUANTITY_CAP.get()) break;
        }


        //reset the progress and mana
        this.progress = 0;
        this.needsMana = true;
        updateBlock();
    }

    public static final ParticleColor shrineParticle = new ParticleColor(20, 100, 200);

    @Override
    public void tick() {
        super.tick();

        if (level == null || isOff) return;
        if (level.isClientSide() && !needsMana) {
            for (int i = 0; i < progress / 2; i++) {
                level.addParticle(
                        GlowParticleData.createData(shrineParticle, i / 2F * 0.2f, i / 2F * 0.75f, 20),
                        getBlockPos().getX() + 0.5 + ParticleUtil.inRange(-0.1, 0.1),
                        getBlockPos().getY() + 1.0 + ParticleUtil.inRange(-0.1, 0.1),
                        getBlockPos().getZ() + 0.5 + ParticleUtil.inRange(-0.1, 0.1),
                        0, 0, 0);
            }
        } else {

            // every 100 seconds, evaluate the aquarium
            long gameTime = level.getGameTime();
            if (gameTime % 2000 == 0) {
                evaluateAquarium();
            }

            // every 4 seconds, if the shrine needs mana, take mana from the source
            if (gameTime % 80 == 0 && needsMana && SourceUtil.takeSourceMultiple(worldPosition, level, 7, Common.SIREN_MANA_COST.get()) != null) {
                this.needsMana = false;
                updateBlock();
            }

            // every 50 seconds, if the shrine has enough progress, generate items
            if (gameTime % 1000 == 0 && !needsMana) {
                if (progress >= getMaxProgress()) {
                    generateItems();
                } else {
                    // if the shrine doesn't have enough progress, generate a particle projectile and give progress
                    LivingEntity rnd = getRandomEntity();
                    if (rnd != null) {
                        EntityLerpedProjectile orb = new EntityLerpedProjectile(level,
                                rnd.blockPosition(), this.getBlockPos(),
                                20, 50, 255);
                        level.addFreshEntity(orb);
                        giveProgress();
                    }
                }
            }
        }
    }

    /**
     * A list of tool tips to render on the screen when looking at this target.
     */
    @Override
    public void getTooltip(List<Component> tooltip) {
        if (this.needsMana) {
            tooltip.add(Component.translatable("ars_nouveau.wixie.need_mana"));
        } else {
            //tooltip.add(Component.translatable("Progress: " + progress + "/" + Common.SIREN_MAX_PROGRESS));
            tooltip.add(Component.translatable("Aquarium Bonus: " + bonus));
        }
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        this.progress = compound.getInt("progress");
        this.bonus = compound.getInt("bonus");
        this.needsMana = compound.getBoolean("needsMana");
        super.loadAdditional(compound, pRegistries);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putInt("progress", progress);
        tag.putInt("bonus", bonus);
        tag.putBoolean("needsMana", needsMana);
    }

    public LivingEntity getRandomEntity() {
        // if there are no entities nearby, return null, otherwise return a random entity from the list
        if (getNearbyEntities().isEmpty() || level == null)
            return null;
        return getNearbyEntities().get(level.random.nextInt(getNearbyEntities().size()));
    }

    private List<LivingEntity> getNearbyEntities() {
        // get a list of entities within 10 blocks of the shrine
        if (level == null) return ImmutableList.of();
        return level.getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos()).inflate(20), e -> e.getType().is(EntityTypeTags.AQUATIC) && !(e instanceof MermaidEntity));
    }
}
