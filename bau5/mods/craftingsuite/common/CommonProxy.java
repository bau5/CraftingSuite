package bau5.mods.craftingsuite.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import bau5.mods.craftingsuite.client.GuiModdedCraftingTable;
import bau5.mods.craftingsuite.client.GuiModificationTable;
import bau5.mods.craftingsuite.client.GuiProjectBench;
import bau5.mods.craftingsuite.common.inventory.ContainerModdedCraftingTable;
import bau5.mods.craftingsuite.common.inventory.ContainerModificationTable;
import bau5.mods.craftingsuite.common.inventory.ContainerProjectBench;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {

	public void registerRenderingInformation() { }
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		switch(ID){
		case 0: return new ContainerModificationTable((TileEntityModificationTable)te, player);
		case 1: return new ContainerModdedCraftingTable((TileEntityModdedTable)te, player);
		case 2: return new ContainerProjectBench(player.inventory, (TileEntityProjectBench)te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		switch(ID){
		case 0: return new GuiModificationTable((TileEntityModificationTable)te, player);
		case 1: return new GuiModdedCraftingTable((TileEntityModdedTable)te, player);
		case 2: return new GuiProjectBench(player.inventory, (TileEntityProjectBench)te);
		}
		return null;
	}
	
	public World getClientSideWorld(){
		return null;
	}
	public int getRenderID() {
		return 0;
	}
}
