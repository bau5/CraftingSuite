package bau5.mods.craftingsuite.common;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import bau5.mods.craftingsuite.client.GuiCraftingFrame;
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
		List entities = null;
		if(ID == 2)
			entities = world.getEntitiesWithinAABB(EntityCraftingFrame.class, AxisAlignedBB.getBoundingBox(x, y, z, x+0.9, y+0.9, z+0.9));
		switch(ID){
		case 0:
			return new ContainerCraftingTable((TileEntityCraftingTable)te, player.inventory, world, x, y, z);
		case 1:
			return new ContainerProjectBench(player.inventory, (TileEntityProjectBench)te);
		case 2:
			if(entities == null || entities.isEmpty())
				return null;
			for(Entity ent : (List<Entity>)entities){
				if(ent instanceof EntityCraftingFrame){
					return new ContainerCraftingFrame(player.inventory, (EntityCraftingFrame)ent);
				}
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		List entities = null;
		if(ID == 2)
			entities = world.getEntitiesWithinAABB(EntityCraftingFrame.class, AxisAlignedBB.getBoundingBox(x, y, z, x+0.9, y+0.9, z+0.9));
		switch(ID){
		case 0: 
			return new GuiCraftingTableII((TileEntityCraftingTable)te, player.inventory, world, x, y, z);
		case 1:
			return new GuiProjectBench(player.inventory, te);
		case 2: 
			if(entities == null || entities.isEmpty())
				return null;
			for(Entity ent : (List<Entity>)entities){
				if(ent instanceof EntityCraftingFrame){
					return new GuiCraftingFrame(player.inventory, (EntityCraftingFrame)ent);
				}
			}
			
		
		}
		return null;
	}
	
	public World getClientSideWorld(){
		return null;
	}
}
