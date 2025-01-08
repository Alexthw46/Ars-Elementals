package alexthw.ars_elemental.api.item;

import com.hollingsworth.arsnouveau.api.spell.SpellSchool;

import java.util.HashSet;
import java.util.Set;

public interface ISchoolProvider {
    SpellSchool getSchool();

    default Set<SpellSchool> getSchools() {
        Set<SpellSchool> schools = new HashSet<>();
        schools.add(getSchool());
        schools.addAll(getSchool().getSubSchools());
        return schools;
    }

}
