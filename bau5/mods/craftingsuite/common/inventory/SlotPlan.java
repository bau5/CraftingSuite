package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.CraftingSuite;

public class SlotPlan extends Slot {

	public SlotPlan(IInventory inv, int id, int x, int y) {
		super(inv, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return par1ItemStack.itemID == CraftingSuite.planItem.itemID;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return super.canTakeStack(par1EntityPlayer);
	}
}
