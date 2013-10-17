package bau5.mods.craftingsuite.common.tileentity.parthandlers;

import net.minecraft.inventory.Slot;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;

public class ContainerHandler {
	private TileEntityModdedTable tileEntity;
	
	public boolean containerWorking = false;
	public boolean containerInit = false;
	
	public ContainerHandler(TileEntityModdedTable tile) {
		tileEntity = tile;
	}

	public Slot[] getSlots() {
		return null;
	}
	
	public boolean isContainerWorking(){
		return containerWorking;
	}
	
	public boolean isContainerInit(){
		return containerInit;
	}
	
}
