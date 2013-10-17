package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.CraftingSuite;

public class SlotModification extends Slot {
	private int type;
	public SlotModification(int typ, IInventory inventory, int index, int x,
			int y) {
		super(inventory, index, x, y);
		type = typ;
		
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		switch(type){
		case 0: return (stack.itemID == Block.workbench.blockID);
		case 1: return (stack.itemID == CraftingSuite.instance.modItems.itemID)
					&& (stack.getItemDamage() == 0);
		default: return super.isItemValid(stack);
		}
	}
	
	@Override
	public int getSlotStackLimit() {
		return super.getSlotStackLimit();
	}
}
