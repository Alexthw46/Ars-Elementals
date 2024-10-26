package alexthw.ars_elemental.common;

import alexthw.ars_elemental.common.items.CurioHolder;
import alexthw.ars_elemental.registry.ModRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerCopySlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CurioHolderContainer extends AbstractContainerMenu {
    private final IItemHandler inventory;

    public CurioHolderContainer(int windowId, Inventory playerInv, ItemStack backpack) {
        this(ModRegistry.CURIO_HOLDER.get(), windowId, playerInv, backpack.getCapability(Capabilities.ItemHandler.ITEM), backpack);
    }

    public CurioHolderContainer(MenuType<? extends CurioHolderContainer> containerType, int windowId, Inventory playerInv, @Nullable IItemHandler inventory, ItemStack backpack) {
        super(containerType, windowId);
        this.inventory = inventory;
        if (inventory == null) {
            return;
        }
        for (int i = 0; i < inventory.getSlots() / 9f; ++i) {
            for (int j = 0; j < 9; ++j) {
                int index = i * 9 + j;
                addSlot(this.makeSlot(inventory, i, j, index));
            }
        }
        int offset = offset();
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInv, j1 + (l + 1) * 9, 8 + j1 * 18, offset + 84 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInv, i1, 8 + i1 * 18, offset + 142));
        }
    }

    @NotNull
    protected Slot makeSlot(IItemHandler inventory, int i, int j, int index) {
        return new ItemHandlerCopySlot(inventory, index, 8 + j * 18, 18 + i * 18) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return CurioHolder.canStore(stack);
            }

        };
    }

    @Override
    public void removed(Player playerIn) {
        playerIn.level().playSound(null, playerIn.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1, 1);
        super.removed(playerIn);
    }

    public int offset() {
        return 0;
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack = slot.getItem();
            copy = itemstack.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.moveItemStackTo(itemstack, this.inventory.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack, 0, this.inventory.getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return copy;
    }

}
