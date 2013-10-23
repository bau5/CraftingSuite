package bau5.mods.craftingsuite.client;

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import bau5.mods.craftingsuite.common.CommonProxy;
import bau5.mods.craftingsuite.common.CraftingSuite;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy{
	private static int renderid = RenderingRegistry.getNextAvailableRenderId();
	@Override
	public void registerRenderingInformation() {
		CraftingBlockRenderer generalRenderer = new CraftingBlockRenderer();
		MinecraftForgeClient.registerItemRenderer(CraftingSuite.modificationTableBlock.blockID, generalRenderer);
		MinecraftForgeClient.registerItemRenderer(CraftingSuite.craftingTableBlock.blockID, generalRenderer);
		RenderingRegistry.registerBlockHandler(renderid, generalRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityModificationTable.class, generalRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjectBench.class, generalRenderer);
	}
	
	@Override
	public World getClientSideWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}
	
	@Override
	public int getRenderID(){
		return renderid;
	}
}
