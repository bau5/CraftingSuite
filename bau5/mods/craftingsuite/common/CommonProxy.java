package bau5.mods.craftingsuite.common;

import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import bau5.mods.craftingsuite.client.GuiCraftingTableII;
import bau5.mods.craftingsuite.client.GuiProjectBench;
import bau5.mods.craftingsuite.common.tileentity.ContainerCraftingTable;
import bau5.mods.craftingsuite.common.tileentity.ContainerProjectBench;
import bau5.mods.craftingsuite.common.tileentity.TileEntityCraftingTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {

	public void registerRenderingInformation() { }
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te == null)
			return null;
		switch(ID){
		case 0:
			return new ContainerCraftingTable((TileEntityCraftingTable)te, player.inventory, world, x, y, z);
		case 1:
			return new ContainerProjectBench(player.inventory, (TileEntityProjectBench)te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te == null)
			return null;
		switch(ID){
		case 0: 
			return new GuiCraftingTableII((TileEntityCraftingTable)te, player.inventory, world, x, y, z);
		case 1:
			return new GuiProjectBench(player.inventory, te);
		
		}
		return null;
	}
	
	public World getClientSideWorld(){
		return null;
	}
}
