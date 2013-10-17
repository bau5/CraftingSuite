package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import bau5.mods.craftingsuite.common.tileentity.TileEntityCraftingTable;

public class ContainerCraftingTable extends ContainerBase{
	
	public IInventory craftResult = new InventoryCraftResult();
	public TileEntityCraftingTable tileEntity;
	public ContainerCraftingTable(TileEntityCraftingTable tile, InventoryPlayer invPlayer, World world, int x, int y, int z) {
		tileEntity = tile;
		buildBasicCraftingInventory(invPlayer, tile, tile.craftResult);
		tileEntity.findRecipe();
	}
	
	@Override
	public ItemStack slotClick(int slot, int par2, int par3,
			EntityPlayer par4EntityPlayer) {
		if(slot == 0 && par3 == 6)
			par3 = 0;
		ItemStack stack = super.slotClick(slot, par2, par3, par4EntityPlayer);
		return stack;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
	}

	@Override
	protected int[] getXYZ() {
		return new int[]{
			tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord
		};
	}
}
