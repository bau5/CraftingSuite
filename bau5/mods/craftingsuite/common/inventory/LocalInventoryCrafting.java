package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;


public class LocalInventoryCrafting extends InventoryCrafting{
	private TileEntity theTile;
	public LocalInventoryCrafting() {
		super(new Container(){
			@Override
			public boolean canInteractWith(EntityPlayer var1) {
				return false;
			}
		}, 3, 3);
	}
	public LocalInventoryCrafting(TileEntity tileEntity){
		this();
		theTile = tileEntity;
	}
	
	public LocalInventoryCrafting copyInventory(){
		LocalInventoryCrafting crafting2 = new LocalInventoryCrafting();
		for(int i = 0; i < this.getSizeInventory(); i++){
			ItemStack copy = this.getStackInSlot(i);
			if(copy != null)
				copy = copy.copy();
			crafting2.setInventorySlotContents(i, copy);
		}
		return crafting2;
	}
}
