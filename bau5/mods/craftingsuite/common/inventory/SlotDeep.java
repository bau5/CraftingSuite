package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.helpers.ItemHelper;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.InventoryHandler;

public class SlotDeep extends Slot {
	private ItemStack type;
	public SlotDeep(IInventory inv, int id, int x, int y) {
		super(inv, id, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if(type == null)
			return super.isItemValid(stack);
		else{
			return ItemHelper.checkItemMatch(type, stack);
		}
	}
	
	@Override
	public int getSlotStackLimit() {
		if(type != null)
			return type.stackSize * 9;
		else
			return super.getSlotStackLimit();
	}
	
	@Override
	public void putStack(ItemStack par1ItemStack) {
		if(((IModifiedTileEntityProvider)((InventoryHandler)inventory).getTileProvider()).getContainerHandler().isContainerWorking())
			return;
		if(type != null && par1ItemStack != null && getStack() != null){
			ItemStack stackInside = getStack();
			stackInside.stackSize += par1ItemStack.stackSize;
			par1ItemStack.stackSize = 0;
		}
		else{
			if(par1ItemStack != null){
				type = par1ItemStack.copy();
				type.stackSize = 1;
			}
			if(par1ItemStack != null && par1ItemStack.stackSize > 64)
				putLargeStack(par1ItemStack);
			else 
				super.putStack(par1ItemStack);
			
		}
	}
	
	public void putLargeStack(ItemStack par1ItemStack){
		ItemStack stackInside = getStack();
		if(stackInside != null){
			stackInside.stackSize += par1ItemStack.stackSize;
			par1ItemStack.stackSize = 0;
		}else{
			int times = par1ItemStack.stackSize / 64;
			for(int i = 0; i < times; i++){
				ItemStack copy = par1ItemStack.copy();
				copy.stackSize = 64;
				putStack(copy);
			}
			ItemStack copy = par1ItemStack.copy();
			copy.stackSize = copy.stackSize % 64;
			if(copy.stackSize > 0)
				putStack(copy);
		}
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer par1EntityPlayer,
			ItemStack par2ItemStack) {
		if(par2ItemStack != null && par2ItemStack.stackSize > 64){
			ItemStack copy = par2ItemStack.copy();
			copy.stackSize -= 64;
			putStack(copy);
			par2ItemStack.stackSize = 64;
		}
		if(this.getStack() == null)
			type = null;
		super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
	}
}
