package alexthw.ars_elemental.common.blocks;

import alexthw.ars_elemental.api.item.ISchoolFocus;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;

public class ElementalTurret extends BasicSpellTurret {

    public final SpellSchool school;

    public ElementalTurret(Properties properties, SpellSchool school) {
        super(properties.noOcclusion().forceSolidOn());
        this.school = school;
    }

    public void shootSpell(ServerLevel world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof ElementalSpellTurretTile tile)) return;
        ISpellCaster caster = tile.getSpellCaster();
        if (caster.getSpell().isEmpty())
            return;
        int manaCost = tile.getManaCost();
        if (manaCost > 0 && SourceUtil.takeSourceWithParticles(pos, world, 10, manaCost) == null)
            return;
        Networking.sendToNearby(world, pos, new PacketOneShotAnimation(pos));
        Position iposition = getDispensePosition(new BlockSourceImpl(world, pos));
        Direction direction = world.getBlockState(pos).getValue(FACING);
        FakePlayer fakePlayer = ANFakePlayer.getPlayer(world);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        var resolver = new TurretSpellResolver(new SpellContext(world, caster.getSpell(), fakePlayer, new TileCaster(tile, SpellContext.CasterType.TURRET)), tile);
        if (resolver.castType != null && TURRET_BEHAVIOR_MAP.containsKey(resolver.castType)) {
            TURRET_BEHAVIOR_MAP.get(resolver.castType).onCast(resolver, world, pos, fakePlayer, iposition, direction);
            caster.playSound(pos, world, null, caster.getCurrentSound(), SoundSource.BLOCKS);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElementalSpellTurretTile(pos, state).setSchool(school);
    }

    static class TurretSpellResolver extends EntitySpellResolver {

        ElementalSpellTurretTile tile;

        public TurretSpellResolver(SpellContext context, ElementalSpellTurretTile tile) {
            super(context);
            this.tile = tile;
        }

        @Override
        public boolean hasFocus(ItemStack stack) {
            return hasFocus(stack.getItem());
        }

        @Override
        public boolean hasFocus(Item item) {
            if (item instanceof ISchoolFocus focus) {
                return tile.getSchool() == focus.getSchool();
            } else if (item == ItemsRegistry.SHAPERS_FOCUS.get()) {
                return tile.getSchool() == SpellSchools.MANIPULATION;
            }
            return super.hasFocus(item);
        }


        @Override
        public SpellResolver getNewResolver(SpellContext context) {
            return new TurretSpellResolver(context, tile);
        }
    }

}
