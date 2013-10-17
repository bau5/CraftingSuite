package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;

public class ContainerModificationTable extends ContainerBase {
	
	public TileEntityModificationTable tileEntity;
	
	public ContainerModificationTable(TileEntityModificationTable table, EntityPlayer player) {
		tileEntity = table;
		buildContainer(table, player);
	}
	
	private void buildContainer(TileEntityModificationTable table, EntityPlayer player){
		this.addSlotToContainer(new SlotModification(0, table, 0, 11, 20));
		this.addSlotToContainer(new SlotModification(1, table, 1, 11, 56));
		this.addSlotToContainer(new SlotModification(2, table, 2, 11, 92));

		super.bindPlayerInventory(player.inventory, 40, 89);
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
}
