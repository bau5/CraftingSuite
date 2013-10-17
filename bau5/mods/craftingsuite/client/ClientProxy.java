package bau5.mods.craftingsuite.client;

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import bau5.mods.craftingsuite.common.CommonProxy;
import bau5.mods.craftingsuite.common.CraftingSuite;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy{
	@Override
	public void registerRenderingInformation() {
		MinecraftForgeClient.registerItemRenderer(CraftingSuite.craftingBlock.blockID, new CraftingBlockRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityModificationTable.class, new CraftingBlockRenderer());
	}
	
	@Override
	public World getClientSideWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}
}
