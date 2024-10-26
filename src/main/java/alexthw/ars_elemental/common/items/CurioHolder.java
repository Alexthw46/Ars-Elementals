package alexthw.ars_elemental.common.items;

import alexthw.ars_elemental.common.CurioHolderContainer;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

import static alexthw.ars_elemental.client.ClientEvents.CURIO_BAG_KEYBINDING;
import static alexthw.ars_elemental.registry.ModRegistry.BLACKLIST_BAGGABLE;

public class CurioHolder extends Item implements ICurioItem {
    public CurioHolder(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.ars_elemental.bags", CURIO_BAG_KEYBINDING.getTranslatedKeyMessage()));
    }

    public static SlotReference isEquipped(Player playerEntity) {
        if (playerEntity != null) {
            IItemHandlerModifiable items = CuriosUtil.getAllWornItems(playerEntity);
            if (items != null) {
                for (int i = 0; i < items.getSlots(); ++i) {
                    ItemStack item = items.getStackInSlot(i);
                    if (item.getItem() instanceof CurioHolder) {
                        return new SlotReference(items, i);
                    }
                }
            }
            Inventory inv = playerEntity.getInventory();
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = inv.items.get((inv.selected + i) % 9);
                if (stack.getItem() instanceof CurioHolder) {
                    return new SlotReference(new PlayerMainInvWrapper(inv), (inv.selected + i) % 9);
                }
            }
        }
        return SlotReference.empty();
    }

    public static boolean canStore(ItemStack stack) {
        return !stack.is(BLACKLIST_BAGGABLE) && stack.getItem().canFitInsideContainerItems();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        if (!level.isClientSide) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            openContainer(level, playerIn, stack, handIn == InteractionHand.MAIN_HAND ? playerIn.getInventory().selected : 1);
        }
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }

    public void openContainer(Level level, Player player, ItemStack bag, int index) {
        if (!level.isClientSide) {
            MenuProvider container = new SimpleMenuProvider((w, p, pl) -> new CurioHolderContainer(w, p, bag), bag.getHoverName());
            player.openMenu(container, b -> b.writeInt(index));
            player.level.playSound(null, player.blockPosition(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 1, 1);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

}
