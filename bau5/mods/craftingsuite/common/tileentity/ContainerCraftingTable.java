package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

public class ContainerCraftingTable extends Container{
	
	public IInventory craftResult = new InventoryCraftResult();
	public TileEntityCraftingTable tileEntity;
	public ContainerCraftingTable(TileEntityCraftingTable tile, InventoryPlayer invPlayer, World world, int x, int y, int z) {
		tileEntity = tile;
		buildContainer(tile, invPlayer);
		tileEntity.findRecipe();
	}
	
	private void buildContainer(TileEntityCraftingTable tile, InventoryPlayer invPlayer) {
		addSlotToContainer(new SlotCrafting(invPlayer.player, tile.craftingMatrix, tile.craftResult, 0, 124, 35));
        int i;
        int j;

        for (i = 0; i < 3; ++i)
        {
            for (j = 0; j < 3; ++j)
            {
                this.addSlotToContainer(new Slot(tileEntity, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

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

        this.onCraftMatrixChanged(tile.craftingMatrix);
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return true;
	}
	
	@Override
	public ItemStack slotClick(int slot, int par2, int par3,
			EntityPlayer par4EntityPlayer) {
		if(slot == 0 && par3 == 6)
			par3 = 0;
		ItemStack stack = super.slotClick(slot, par2, par3, par4EntityPlayer);
		return stack;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
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
