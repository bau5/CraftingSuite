package bau5.mods.craftingsuite.client;

import net.minecraft.world.World;
import bau5.mods.craftingsuite.common.CommonProxy;
import cpw.mods.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy{
	@Override
	public void registerRenderingInformation() {
		//TODO rendering info
	}
	
	@Override
	public World getClientSideWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}
}
