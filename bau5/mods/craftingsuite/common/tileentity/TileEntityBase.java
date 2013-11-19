package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityBase extends TileEntity implements IModifiedTileEntityProvider{
	public boolean sendRenderPacket = false; 
}
