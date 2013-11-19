	package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.handlers.IModifierHandler;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;

public abstract class ContainerBase extends Container implements IModifiedContainerProvider{
	public boolean containerIsWorking = false;
	public IModifiedTileEntityProvider modifiedTile;
	public IModifierHandler 		   handler;
	
	public int craftingSlotIndex = -1;
	
	public ContainerBase(IModifiedTileEntityProvider til){
		modifiedTile = til;
	}
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return rangeCheck(entityplayer);
	}

	protected boolean rangeCheck(EntityPlayer entityplayer) {
		int[] loc = this.getXYZ();
		return entityplayer.getDistanceSq(loc[0] +0.5, loc[1] +0.5, loc[2] +0.5) < 64;
	}

	protected abstract int[] getXYZ();
	protected abstract void handleInventoryModifiers();

	@Override
	public ItemStack slotClick(int slot, int clickType, int clickMeta,
			EntityPlayer player) {
		if(clickMeta == 6 && slot == 0)
			clickMeta = 0;
		if(clickMeta == 1 && slot == craftingSlotIndex){
			Slot theSlot = (Slot) inventorySlots.get(slot);
			if(theSlot.getHasStack()){
				int tries = theSlot.getStack().getMaxStackSize() / theSlot.getStack().stackSize;
				for(int i = 0; i < tries -1; i++){
					if(handler != null && handler.handlesSlotClicks()){
						handler.handleSlotClick(slot, clickType, clickMeta, player);
					}else{
						slotClick_plain(slot, clickType, clickMeta, player);
					}
					modifiedTile.getInventoryHandler().findRecipe(true);
				}
				if(handler != null && handler.handlesSlotClicks())
					return handler.handleSlotClick(slot, clickType, clickMeta, player);
				return slotClick_plain(slot, clickType, clickMeta, player);
			}
		}
		if(handler != null && handler.handlesSlotClicks())
			return handler.handleSlotClick(slot, clickType, clickMeta, player);
 		return slotClick_plain(slot, clickType, clickMeta, player);
	}
	
	public ItemStack slotClick_plain(int slot, int clickType, int clickMeta, EntityPlayer player){
		return super.slotClick(slot, clickType, clickMeta, player);
	}
	
	/**
	 * Used to bind the player's inventory to the container. Only to be used for simple
	 * containers, such as ones that look identical to the crafting table's container.
	 * 
	 * @param ent
	 * @param invPlayer
	 */
	protected void basicBindPlayerInventory(InventoryPlayer invPlayer) {
		int i;
		int j;
		for (i = 0; i < 3; ++i)
        {
            for (j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
        }
	}
	
	protected void bindPlayerInventory(InventoryPlayer invPlayer, int x, int y){
		int i;
		int j;
		for (i = 0; i < 3; ++i)
        {
            for (j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, (8 +x) + j * 18, (84 +y) + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(invPlayer, i, (8 +x) + i * 18, 142 +y));
        }
	}
	
	/**
	 * Builds a basic crafting inventory. Also calls basicBindPlayerInventory
	 * 
	 * @param invPlayer The player
	 * @param craftingMatrix The crafting matrix
	 * @param resultMatrix The crafting result matrix
	 */
	protected void buildBasicCraftingInventory(InventoryPlayer invPlayer, 
					IInventory craftingMatrix, IInventory resultMatrix){
		this.addSlotToContainer(new SlotMTCrafting(invPlayer.player, (TileEntityModdedTable)modifiedTile, craftingMatrix, resultMatrix, 0, 124, 35, handler));
		craftingSlotIndex = inventorySlots.size() -1;
		int i;
		int j;
		
		for (i = 0; i < 3; ++i)
        {
            for (j = 0; j < 3; ++j)
            {
                this.addSlotToContainer(new Slot(craftingMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }
		basicBindPlayerInventory(invPlayer);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
		if(handler != null && handler.handlesTransfers())
			return handler.handleTransferClick(par1EntityPlayer, par2);
		return transferStackInSlot_plain(par1EntityPlayer, par2);
    }
	
	public ItemStack transferStackInSlot_plain(EntityPlayer par1EntityPlayer, int par2){
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 == 0)
            {
                if (!this.mergeItemStack(itemstack1, 11, 46, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (par2 > 10 && par2 < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return null;
                }
            }
            else if (par2 >= 37 && par2 < 46)
            {
                if (!this.mergeItemStack(itemstack1, 11, 37, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 11, 46, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
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
        }

        return itemstack;
	}
	
	@Override
	public IModifierHandler getModifierHandler() {
		return handler;
	}
	
	public boolean invokeMerge(ItemStack par1ItemStack, int par2,
			int par3, boolean par4) {
		return super.mergeItemStack(par1ItemStack, par2, par3, par4);
	}
	@Override
	public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack) {
		if(modifiedTile != null && modifiedTile.getContainerHandler() != null)
			modifiedTile.getContainerHandler().containerWorking = true;
		containerIsWorking = true;
		super.putStacksInSlots(par1ArrayOfItemStack);
		containerIsWorking = false;
		if(modifiedTile != null && modifiedTile.getContainerHandler() != null)
			modifiedTile.getContainerHandler().containerWorking = false;
	}
	
	@Override
	public void putStackInSlot(int par1, ItemStack par2ItemStack) {
		if(modifiedTile != null && modifiedTile.getContainerHandler() != null)
			modifiedTile.getContainerHandler().containerWorking = true;
		containerIsWorking = true;
		super.putStackInSlot(par1, par2ItemStack);
		containerIsWorking = false;
		if(modifiedTile != null && modifiedTile.getContainerHandler() != null)
			modifiedTile.getContainerHandler().containerWorking = false;
	}
}
