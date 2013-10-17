package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;

public class ContainerModdedCraftingTable extends ContainerBase {

	private TileEntityModdedTable tileEntity;
	
	public ContainerModdedCraftingTable(TileEntityModdedTable te, EntityPlayer player) {
		tileEntity = te;
		buildContainerFromTile(player);
		tileEntity.inventoryHandler().findRecipe(true);
	}

	private void buildContainerFromTile(EntityPlayer player) {
		if(tileEntity.getUpgrades()[0] == 1){
			buildBasicCraftingInventory(player.inventory, tileEntity.inventoryHandler(), tileEntity.inventoryHandler().resultMatrix());
		}else{
			for(Slot slot : tileEntity.containerHandler().getSlots()){
				this.addSlotToContainer(slot);
			}
		}
	}

	@Override
	protected int[] getXYZ() {
		return new int[]{
				tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord
		};
	}
}
