package alexthw.ars_elemental.mixin;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpellSchool.class)
public interface SpellSchoolAccessor {

    @Accessor("docIcon")
    void setDocIcon(DocAssets.BlitInfo icon);

}
