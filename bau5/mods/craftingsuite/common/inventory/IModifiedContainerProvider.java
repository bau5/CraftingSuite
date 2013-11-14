package bau5.mods.craftingsuite.common.inventory;

import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;

public interface IModifiedContainerProvider {
	public EnumInventoryModifier getInventoryModifier();
	public int getSizeInventoryOfTile();
	public IModifiedTileEntityProvider getTileEntity();
}
