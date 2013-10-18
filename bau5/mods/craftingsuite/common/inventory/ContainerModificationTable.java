package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerModificationTable extends ContainerBase {
	
	public TileEntityModificationTable tileEntity;
	
	private IInventory forRender = new InventoryBasic("dummy", false, 10);
	
	@SideOnly(Side.CLIENT)
	private SlotDummy[] dummySlots = new SlotDummy[forRender.getSizeInventory()];
	
	public ContainerModificationTable(TileEntityModificationTable table, EntityPlayer player) {
		tileEntity = table;
		buildContainer(table, player);
	}
	
	private void buildContainer(TileEntityModificationTable table, EntityPlayer player){
		int i = 0;
		this.addSlotToContainer(new SlotModification(0, table, i++, 11, 20));
		this.addSlotToContainer(new SlotModification(1, table, i++, 11, 56));
		this.addSlotToContainer(new SlotModification(2, table, i++, 11, 92));
		this.addSlotToContainer(new SlotModification(3, table, i++, 33, 92));
		this.addSlotToContainer(new SlotModification(4, table, i++, 55, 92));
		this.addSlotToContainer(new Slot(table.craftResult,    i++, 193, 140));
		
		super.bindPlayerInventory(player.inventory, 40, 89);
		i = 0;
		if(tileEntity.worldObj.isRemote){
			for(i = 0; i < dummySlots.length; i++){
				dummySlots[i] = new SlotDummy(forRender, i);
				this.addSlotToContainer(dummySlots[i]);
			}
		}
	}
	
	@Override
	public ItemStack slotClick(int slot, int clickType, int clickMeta, EntityPlayer player) {
		return super.slotClick(slot, clickType, clickMeta, player);
	}

	@Override
	protected int[] getXYZ() {
		return new int[]{
				tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord
		};
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
                if (!this.mergeItemStack(itemstack1, 10, 40, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (par2 >= 1 && par2 < 7)
            {
                if (!this.mergeItemStack(itemstack1, 7, 40, false))
                {
                    return null;
                }
            }
            else if (par2 >= 31 && par2 < 40)
            {
                if (!this.mergeItemStack(itemstack1, 7, 31, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 7, 31, false))
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

	@SideOnly(Side.CLIENT)
	public void addItemsForRender(int mouseX, int mouseY) {
		ItemStack[] stacks = tileEntity.inputForResult;
		if(stacks == null)
			return; 
		int i = 0;
		for(ItemStack stack : stacks){
			forRender.setInventorySlotContents(i, stack);
			dummySlots[i].xDisplayPosition = mouseX + (i * 16);
			dummySlots[i].yDisplayPosition = mouseY+2;
			i++;
		}
	}

	public void clearDummySlots() {
		if(dummySlots[0] == null)
			return;
		if(dummySlots[0].xDisplayPosition != 0){
			for(int i = 0; i < forRender.getSizeInventory(); i++){
				forRender.setInventorySlotContents(i, null);
				if(dummySlots[i] != null){
					dummySlots[i].xDisplayPosition = 0;
					dummySlots[i].yDisplayPosition = 0;
				}
			}
		}
	}
}
