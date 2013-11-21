package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.helpers.ItemHelper;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.TileEntityBase;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.InventoryHandler;

public class SlotDeep extends Slot {
	private ItemStack type;
	public SlotDeep(IInventory inv, int id, int x, int y) {
		super(inv, id, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if(getStack() == null)
			return super.isItemValid(stack);
		else{
			return ItemHelper.checkItemMatch(stack, getStack());
		}
	}
	
	@Override
	public int getSlotStackLimit() {
		if(getStack() != null)
			return getStack().getMaxStackSize() * 6;
		else
			return super.getSlotStackLimit();
	}
	
	@Override
	public void putStack(ItemStack par1ItemStack) {
		super.putStack(par1ItemStack);
	}
	
	public void addStack(ItemStack stack){
		if(inventory instanceof InventoryHandler){
			if(((IModifiedTileEntityProvider)((InventoryHandler)inventory).getTileProvider()).getContainerHandler().isContainerWorking())
				return;
		}else{
			if(((TileEntityBase)inventory).getContainerHandler().isContainerInit()){
				return;
			}
		}
		if(stack != null && getStack() != null){
			ItemStack stackInside = getStack();
			if(stackInside.stackSize + stack.stackSize > getSlotStackLimit()){
				int canPut = (getSlotStackLimit() - stackInside.stackSize);
				if(canPut <= stack.stackSize){
					stackInside.stackSize += canPut;
					stack.stackSize -= canPut;
				}
			}else{
				stackInside.stackSize += stack.stackSize;
				stack.stackSize = 0;
			}
		}
		else{
			if(stack != null && stack.stackSize > 64)
				putLargeStack(stack);
			else 
				super.putStack(stack);
			
		}
		onSlotChanged();
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
