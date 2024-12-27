package alexthw.ars_elemental.common.entity.familiars;

import alexthw.ars_elemental.common.entity.FirenandoEntity;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import static alexthw.ars_elemental.ArsElemental.prefix;

public class FirenandoHolder extends AbstractFamiliarHolder {

    public FirenandoHolder() {
        super(prefix("firenando_familiar"), (e) -> e instanceof FirenandoEntity);
    }

    @Override
    public IFamiliar getSummonEntity(Level world, CompoundTag tag) {
        FirenandoFamiliar Firenando = new FirenandoFamiliar(world);
        Firenando.setTagData(tag);
        return Firenando;
    }

    @Override
    public String getBookName() {
        return "Flarecannon";
    }

    @Override
    public String getBookDescription() {
        return "A Flarecannon Familiar increases the damage of Fire spells by 2 and reduce projectile-based spells cost by 20%%. You can feed it a Magma Cream to get a short Fire Resistance buff. Obtained by performing the Ritual of Binding near a Flarecannon.";
    }
}
