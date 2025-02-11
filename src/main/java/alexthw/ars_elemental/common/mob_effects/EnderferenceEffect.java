package alexthw.ars_elemental.common.mob_effects;

import alexthw.ars_elemental.registry.ModPotions;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class EnderferenceEffect extends MobEffect {

    public EnderferenceEffect() {
        super(MobEffectCategory.HARMFUL, 8080895);
    }

    @SubscribeEvent
    public static void enderference(EntityTeleportEvent event){
        // Prevents the player from teleporting if they have the Enderference effect.
        // The player can still teleport with the /tp command.
        if (event instanceof EntityTeleportEvent.TeleportCommand) return;
        if (event.getEntity() instanceof LivingEntity living) {
            if (living.hasEffect(ModPotions.ENDERFERENCE)) {
                event.setCanceled(true);
            }
        }
    }

}
