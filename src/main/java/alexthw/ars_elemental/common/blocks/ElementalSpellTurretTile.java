package alexthw.ars_elemental.common.blocks;

import alexthw.ars_elemental.mixin.TurretAccessor;
import alexthw.ars_elemental.registry.ModTiles;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

import java.util.UUID;

import static com.hollingsworth.arsnouveau.common.block.BasicSpellTurret.TURRET_BEHAVIOR_MAP;

public class ElementalSpellTurretTile extends BasicSpellTurretTile {

    private SpellSchool school;

    public ElementalSpellTurretTile(BlockPos pos, BlockState state) {
        super(ModTiles.ELEMENTAL_TURRET.get(), pos, state);
    }

    public SpellSchool getSchool() {
        return this.school;
    }

    @Override
    public int getManaCost() {
        // 65% discount for spells in the same school, 40% for spells in other schools
        Spell spell = this.spellCaster.getSpell();
        if (spell.unsafeList().stream().anyMatch(p -> this.school.isPartOfSchool(p))) {
            return (int) (spell.getCost() * 0.35);
        } else {
            return (int) (spell.getCost() * 0.6);
        }
    }

    @Override
    public void shootSpell() {
        BlockPos pos = this.getBlockPos();

        if (spellCaster.getSpell().isEmpty() || !(this.level instanceof ServerLevel world))
            return;
        int manaCost = getManaCost();
        if (manaCost > 0 && SourceUtil.takeSourceMultiple(pos, world, 10, manaCost) == null)
            return;
        Networking.sendToNearbyClient(world, pos, new PacketOneShotAnimation(pos));
        Position iposition = BasicSpellTurret.getDispensePosition(pos, world.getBlockState(pos).getValue(BasicSpellTurret.FACING));
        Direction direction = world.getBlockState(pos).getValue(BasicSpellTurret.FACING);
        UUID uuid = ((TurretAccessor) this).getUuid();
        FakePlayer fakePlayer = uuid != null
                ? FakePlayerFactory.get(world, new GameProfile(uuid, ""))
                : ANFakePlayer.getPlayer(world);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        var resolver = new ElementalTurret.TurretSpellResolver(new SpellContext(world, spellCaster.getSpell(), fakePlayer, new TileCaster(this, SpellContext.CasterType.TURRET)), getSchool());
        if (resolver.castType != null && TURRET_BEHAVIOR_MAP.containsKey(resolver.castType)) {
            TURRET_BEHAVIOR_MAP.get(resolver.castType).onCast(resolver, world, pos, fakePlayer, iposition, direction);
            spellCaster.playSound(pos, world, null, spellCaster.getCurrentSound(), SoundSource.BLOCKS);
        }
    }

    public ElementalSpellTurretTile setSchool(SpellSchool school) {
        this.school = school;
        return this;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag,pRegistries);
        tag.putString("school", school.getId());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag,pRegistries);
        this.school = switch (tag.getString("school")) {
            case "fire" -> SpellSchools.ELEMENTAL_FIRE;
            case "water" -> SpellSchools.ELEMENTAL_WATER;
            case "air" -> SpellSchools.ELEMENTAL_AIR;
            case "earth" -> SpellSchools.ELEMENTAL_EARTH;
            default -> SpellSchools.MANIPULATION;
        };
    }
}
