package bau5.mods.craftingsuite.common;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.world.World;
import bau5.mods.craftingsuite.common.inventory.BasicInventoryCrafting;

public class EntityCraftingFrame extends EntityItemFrame {

	public IInventory craftingMatrix = new BasicInventoryCrafting();
	public IInventory craftingResult = new InventoryCraftResult(); 
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
		this.setDirection(dir);
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

	public boolean occupiesBlock(int x, int y, int z) {
		double newX = Math.floor(Math.abs(posX));
		double newY = Math.floor(Math.abs(posY));
		double newZ = Math.floor(Math.abs(posZ));
		boolean flag = (newX == Math.abs(x)) && (newY == Math.abs(y)) && (newZ == Math.abs(z));
		System.out.println(flag + " world " +worldObj.isRemote);
		return flag;
	}

}
