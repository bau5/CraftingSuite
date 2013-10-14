package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.CraftingSuite;

public class ContainerProjectBench extends Container{
	
	public TileEntityProjectBench tileEntity;
	
	public ContainerProjectBench(InventoryPlayer invPlayer, TileEntityProjectBench tpb){
		tileEntity = tpb;
		layoutContainer(invPlayer, tileEntity);
		tileEntity.findRecipe(false);
	}

	private void layoutContainer(InventoryPlayer invPlayer, TileEntityProjectBench tile) {
		addSlotToContainer(new SlotPBCrafting(invPlayer.player, tileEntity, tileEntity.craftingResult, 0, 124, 35));
//		addSlotToContainer(new SlotPBPlan(tileEntity, 27, 9, 35));
		int row;
		int col;
		int index = -1;
		int counter = 0;
		Slot slot = null;

		for(row = 0; row < 3; row++)
		{
			for(col = 0; col < 3; col++)
			{
				slot = new Slot(tileEntity, ++index, 30 + col * 18, 17 + row * 18);
				addSlotToContainer(slot);
				counter++;
			}
		}
		
		for(row = 0; row < 2; row++)
		{	
			for(col = 0; col < 9; col++)
			{
				if(row == 1)
				{
					slot = new Slot(tileEntity, 18 + col, 8 + col * 18, 
									(row * 2 - 1) + 77 + row * 18);
					addSlotToContainer(slot);
				} else
				{
					slot = new Slot(tileEntity, 9 + col, 8 + col * 18,
							77 + row * 18);
					addSlotToContainer(slot);
				}
				counter++;
			}
		}
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9,
											8 + j * 18, 82 + i * 18 + 39));
			}
		}
		for(int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142 + 37));
		}
	}

	@Override
	public ItemStack slotClick(int slot, int clickType, int clickMeta,
			EntityPlayer player) {
		if(slot == 0 && clickMeta == 6)
			clickMeta = 0;
		if(slot == 0)
			tileEntity.markForUpdate();
		return super.slotClick(slot, clickType, clickMeta, player);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}
	
	@Override
	public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack) {
		tileEntity.containerInit = true;
		super.putStacksInSlots(par1ArrayOfItemStack);
		tileEntity.containerInit = false;
		tileEntity.onInventoryChanged();
	}
	
	@Override
	public void putStackInSlot(int slot, ItemStack itemStack) {
		tileEntity.containerInit = true;
		super.putStackInSlot(slot, itemStack);
		tileEntity.containerInit = false;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int numSlot)
    {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(numSlot);

        if (slot != null && slot.getHasStack())
        {
        	boolean flag = false;
            ItemStack stack2 = slot.getStack();
            stack = stack2.copy();
            //TODO PLANS
//            if(stack2.getItem().equals(CraftingSuite.instance.pbPlan))
//            	flag = this.mergeItemStack(stack2, 28, 29, false);
            if(!flag){
	            if (numSlot == 0)
	            {
	                if (!this.mergeItemStack(stack2, 10, 55, true))
	                {
	                    return null;
	                }
//	                updateCrafting(true);
	            }
	            //Merge crafting matrix item with supply matrix inventory
	            else if(numSlot > 0 && numSlot <= 9)
	            {
	            	if(!this.mergeItemStack(stack2, 10, 28, false))
	            	{
	            		if(!this.mergeItemStack(stack2, 28, 64, false))
	            		{
	                		return null;
	            		}
	            	}
//	            	updateCrafting(true);
	            }
	            //Merge Supply matrix item with player inventory
	            else if (numSlot >= 10 && numSlot <= 27)
	            {
	                if (!this.mergeItemStack(stack2, 29, 65, false))
	                {
	                    return null;
	                }
	            }
	            //Merge player inventory item with supply matrix
	            else if (numSlot >= 28 && numSlot < 64)
	            {
	                if (!this.mergeItemStack(stack2, 10, 28, false))
	                {
	                    return null;
	                }
	            }
            }

            if (stack2.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (stack2.stackSize == stack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, stack2);
        }

        return stack;
    }
}
