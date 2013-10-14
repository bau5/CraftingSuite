package bau5.mods.craftingsuite.common;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityCraftingFrame extends EntityItemFrame {

	/**
	 * Server constructor (?)
	 * @param world
	 */
	public EntityCraftingFrame(World world) {
		super(world);
	}

	public EntityCraftingFrame(World world, int x, int y, int z,
			int dir) {
		super(world, x, y, z, dir);
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if(player == null)
			return true;
		if(player.isSneaking()){
			player.openGui(CraftingSuite.instance, 2, player.worldObj, (int)posX, (int)posY, (int)posZ);
		}
		return super.interactFirst(player);
	}

}
