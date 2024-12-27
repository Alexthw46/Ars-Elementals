package alexthw.ars_elemental.common.rituals;

import alexthw.ars_elemental.ConfigHandler.Common;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.List;

import static alexthw.ars_elemental.ArsElemental.prefix;

public class SquirrelRitual extends AbstractRitual {

    @Override
    public String getLangName() {
        return "Fast Squirrels";
    }

    @Override
    public String getLangDescription() {
        return "Gives a long speed boost to Starbuncles in the area. Refresh buff every 30 seconds in a 15 cubes radius. Radius can be augmented to 30 with a gold block";

    }

    @Override
    protected void tick() {
        int modifier = didConsumeItem(Items.GOLD_BLOCK) ? 2 : 1;
        // Check if the ritual is on a server and if the ritual is ready to refresh
        if (getWorld() instanceof ServerLevel level && level.getGameTime() % Common.SQUIRREL_REFRESH_RATE.get() == 0 && this.tile != null) {
            // Get all entities of type Starbuncle within a 15 or 30 block radius of the ritual
            List<Starbuncle> entities = level.getEntitiesOfClass(Starbuncle.class, new AABB(tile.getBlockPos()).inflate(15 * modifier));
            for (Starbuncle entity : entities) {
                if (entity != null) {
                    // Give the Starbuncle a speed and jump boost for 120 seconds
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400, 1, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 2400, 0, false, false));
                }
                setNeedsSource(true);
            }
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return getConsumedItems().isEmpty() && stack.getItem() == Items.GOLD_BLOCK;
    }

    @Override
    public int getSourceCost() {
        return 150;
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(
                150 + rand.nextInt(105),
                50 + rand.nextInt(100),
                rand.nextInt(25));
    }

    @Override
    public ResourceLocation getRegistryName() {
        return prefix(ID);
    }

    public static String ID = "ritual_squirrels";

}
