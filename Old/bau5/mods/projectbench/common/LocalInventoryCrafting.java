package bau5.mods.projectbench.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class LocalInventoryCrafting extends InventoryCrafting
{
	public Container eventHandler;
	private TileEntityProjectBench te;
	
	public LocalInventoryCrafting(TileEntity entity) {
		super(new Container(){
			public boolean canInteractWith(EntityPlayer var1) {
				return false;
			}
		}, 3, 3);
		te =  (TileEntityProjectBench) entity;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		te.shallowSet(slot, stack);
	}

	public void setUnlinkedInventory(int i, ItemStack itemStack) {
		super.setInventorySlotContents(i, itemStack);
	}
};
