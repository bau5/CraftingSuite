package bau5.mods.craftingsuite.common.inventory;

import bau5.mods.craftingsuite.common.handlers.ExtraHandler;
import bau5.mods.craftingsuite.common.handlers.IModifierHandler;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;

public interface IModifiedContainerProvider {
	public EnumInventoryModifier getInventoryModifier();
	public int getSizeInventoryOfTile();
	public IModifiedTileEntityProvider getTileEntity();
	public IModifierHandler getModifierHandler();
	public ExtraHandler getExtraModifierHandler();
}
