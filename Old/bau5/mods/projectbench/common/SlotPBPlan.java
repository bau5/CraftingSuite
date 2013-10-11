package bau5.mods.projectbench.common;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.projectbench.common.ProjectBench;

public class SlotPBPlan extends Slot{

	public SlotPBPlan(IInventory inv, int index, int xPos, int yPos) {
		super(inv, index, xPos, yPos);
	}
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return par1ItemStack.getItem().equals(ProjectBench.instance.projectBenchPlan);
	}
	
	@Override
	public int getSlotStackLimit() {
		return 1;
	}
	@Override
	public void putStack(ItemStack par1ItemStack) {
		super.putStack(par1ItemStack);
	}
}
