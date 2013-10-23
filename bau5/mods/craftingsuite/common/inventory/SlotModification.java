package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
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
		case 0: return (stack.itemID == CraftingSuite.modItems.itemID) ||
					   (stack.itemID == CraftingSuite.craftingTableBlock.blockID && stack.getItemDamage() == 1);
		case 1: return (OreDictionary.getOreID(stack) == 1);
		case 5: return false;
		default: return super.isItemValid(stack);
		}
	}
	
	@Override
	public int getSlotStackLimit() {
		return super.getSlotStackLimit();
	}
}
