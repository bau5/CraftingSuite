package bau5.mods.craftingsuite.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.helpers.ItemHelper;
import bau5.mods.craftingsuite.common.inventory.ContainerBase;
import bau5.mods.craftingsuite.common.inventory.ContainerProjectBench;
import bau5.mods.craftingsuite.common.inventory.SlotDeep;

public class DeepSlotHandler implements IModifierHandler{
	public ContainerBase container;
	public SlotDeep	deepSlot;
	public DeepSlotHandler(ContainerBase cont, SlotDeep slot){
		container = cont;
		deepSlot = slot;
	}
	
	@Override
	public ItemStack handleSlotClick(int slot, int clickType, int clickMeta,
			EntityPlayer player) {
		if(slot >= 0 && container.inventorySlots.get(slot) instanceof SlotDeep){
			SlotDeep theSlot = (SlotDeep)container.inventorySlots.get(slot);
			if(theSlot.getStack() != null){
				ItemStack playerStack = player.inventory.getItemStack();
				if(playerStack != null && theSlot.isItemValid(playerStack)){
					ItemStack copy = playerStack;
					if(clickType == 1){
						copy = playerStack.copy();
						copy.stackSize = 1;
					}
					theSlot.putStack(copy);
					if(!playerStack.equals(copy) && copy.stackSize == 0){
						playerStack.stackSize -= 1;
					}
					if(playerStack.stackSize == 0)
						player.inventory.setItemStack(null);
					return null;
				}
			}
		}
		ItemStack stack = container.slotClick_plain(slot, clickType, clickMeta, player);
		container.modifiedTile.getInventoryHandler().findRecipe(true);
		return stack;
	}

	@Override
	public ItemStack handleTransferClick(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = null;
        Slot slot = (Slot)container.inventorySlots.get(par2);
        if(slot == deepSlot && deepSlot.getHasStack()){
        	return null;
        }else if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(deepSlot.isItemValid(itemstack1)){
            	boolean flag = deepSlot.getHasStack();
            	deepSlot.putStack(itemstack1);
            	
                if (itemstack1.stackSize == 0 || !flag)
                {
                    slot.putStack((ItemStack)null);
                }
                else
                {
                    slot.onSlotChanged();
                }

                if (itemstack1.stackSize == itemstack.stackSize)
                {
                    return null;
                }

                slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
                
                return itemstack;
            }
        }
		if(!(container instanceof ContainerProjectBench))
			return container.transferStackInSlot_plain(par1EntityPlayer, par2);
		return null;
	}

	@Override
	public boolean handlesSlotClicks() {
		return true;
	}

	@Override
	public boolean handlesTransfers() {
		return true;
	}

	@Override
	public boolean handlesCrafting() {
		return true;
	}

	@Override
	public boolean handleCraftingPiece(ItemStack neededStack, boolean metaSens) {
		if(deepSlot.getHasStack()){
			ItemStack slotStack = deepSlot.getStack();
			if(ItemHelper.checkItemMatch(neededStack, slotStack)){
				slotStack.stackSize -= 1;
				if(slotStack.stackSize <= 0)
					deepSlot.putStack(null);
				return true;
			}
		}
		return false;
	}


}
