package bau5.mods.craftingsuite.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.helpers.ItemHelper;
import bau5.mods.craftingsuite.common.inventory.ContainerBase;
import bau5.mods.craftingsuite.common.inventory.SlotDeep;

public class DeepSlotHandler implements IModifierHandler{
	public ContainerBase container;
	public SlotDeep	deepSlot;
	
	private boolean shiftClickedCrafting = false;
	
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
					theSlot.addStack(copy);
					if(!playerStack.equals(copy) && copy.stackSize == 0){
						playerStack.stackSize -= 1;
					}
					if(playerStack.stackSize == 0)
						player.inventory.setItemStack(null);
					theSlot.onSlotChanged();
					return null;
				}
			}
			if(clickType == 1){
				clickType = 0;
			}
		}
		ItemStack stack = container.slotClick_plain(slot, clickType, clickMeta, player);
		if(container.getTileEntity().getInventoryHandler().affectsCrafting(slot))
			container.modifiedTile.getInventoryHandler().findRecipe(false);
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
            if(deepSlot.isItemValid(itemstack1) && !shiftClickedCrafting){
            	boolean flag = deepSlot.getHasStack();
            	deepSlot.addStack(itemstack1);
            	
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
//		if(!(container instanceof ContainerProjectBench))
			return container.transferStackInSlot_plain(par1EntityPlayer, par2);
//		return null;
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
			boolean flag = false;
			if(metaSens){
				flag = ItemHelper.checkItemMatch(neededStack, slotStack);

			}else{
				flag = ItemHelper.checkOreDictMatch(neededStack, slotStack);
			}
			if(flag){
				slotStack.stackSize -= 1;
				if(slotStack.stackSize <= 0)
					deepSlot.putStack(null);
				return true;
			}
		}
		return false;
	}

	@Override
	public void shiftClickedCraftingSlot() {
		shiftClickedCrafting = !shiftClickedCrafting;
	}

	@Override
	public boolean handlesThisTransfer(int numSlot, ItemStack stack) {
		if(numSlot == deepSlot.slotNumber)
			return false;
		if(deepSlot.isItemValid(stack))
			return true;
		return false;
	}


}
