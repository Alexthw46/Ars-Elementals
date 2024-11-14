package alexthw.ars_elemental.api.item;

import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import org.jetbrains.annotations.NotNull;

public interface ISchoolProvider {
    @NotNull SpellSchool getSchool();

}
