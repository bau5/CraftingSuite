package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.tileentity.TileEntityAdvancedBench;

public class ContainerAdvancedBench extends ContainerBase {
	public TileEntityAdvancedBench tileEntity;
	
	private int slotIndex = 0;

	public ContainerAdvancedBench(InventoryPlayer inventory,
			TileEntityAdvancedBench te) {
		tileEntity = te;
		layoutInventory(inventory, te);
		bindPlayerInventory(inventory, 0, 53);
	}
	
	private void layoutInventory(InventoryPlayer inventory, TileEntityAdvancedBench te) {
		int row;
		int col;
		Slot slot = null;
		
		for(row = 0; row < 4; row++){
			for(col = 0; col < 9; col++){
				slot = new SlotAdvancedCrafting(inventory.player, te, slotIndex++, 8 + (col * 18), 
						(row * 2 - 1) + 21 + row * 18);
				addSlotToContainer(slot);
			}
		}
	}

	@Override
	public ItemStack slotClick(int par1, int par2, int par3,
			EntityPlayer par4EntityPlayer) {
		return null;
	}
	
	@Override
	public EnumInventoryModifier getInventoryModifier() {
		// TODO Auto-generated method stub
		return tileEntity.getInventoryModifier();
	}

	@Override
	public int getSizeInventoryOfTile() {
		return tileEntity.getSizeInventory();
	}

	@Override
	protected int[] getXYZ() {
		return new int[] {
				tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord
		};
	}

	@Override
	protected void handleInventoryModifiers() {
		// TODO Auto-generated method stub

	}

}
