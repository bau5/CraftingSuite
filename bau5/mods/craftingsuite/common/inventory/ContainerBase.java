package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public abstract class ContainerBase extends Container {

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return rangeCheck(entityplayer);
	}

	protected boolean rangeCheck(EntityPlayer entityplayer) {
		int[] loc = this.getXYZ();
		return entityplayer.getDistanceSq(loc[0] +0.5, loc[1] +0.5, loc[2] +0.5) < 64;
	}

	protected abstract int[] getXYZ();

	@Override
	public ItemStack slotClick(int par1, int par2, int par3,
			EntityPlayer par4EntityPlayer) {
 		return super.slotClick(par1, par2, par3, par4EntityPlayer);
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
		this.addSlotToContainer(new SlotCrafting(invPlayer.player, craftingMatrix, resultMatrix, 0, 124, 35));
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
	
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 == 0)
            {
                if (!this.mergeItemStack(itemstack1, 10, 46, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (par2 >= 10 && par2 < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return null;
                }
            }
            else if (par2 >= 37 && par2 < 46)
            {
                if (!this.mergeItemStack(itemstack1, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
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
}
