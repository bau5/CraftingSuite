package bau5.mods.craftingsuite.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;

public class ParticleRenderer {

	public static void doParticle(int i, World worldObj, double d, int j,
			double e) {
		if(i == 0 && Minecraft.getMinecraft().gameSettings.particleSetting == 0){
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new SFFX(worldObj, d, j, e));
		}
	}

}
