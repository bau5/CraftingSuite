package bau5.mods.craftingsuite.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;

public class CraftingItemBlock extends ItemBlock {
	private final static String blockNames[] = {
//		"ctmkii", "pb", "frame"
		"pbmodder"
	};
	public CraftingItemBlock(int id){
		super(id);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ, int metadata) {
		ItemStack stack2 = stack.copy();
		boolean bool = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityModdedTable){
			TileEntityModdedTable tile = (TileEntityModdedTable)te;
			tile.initializeFromNBT(ModificationNBTHelper.getModInfoList(stack2.stackTagCompound));
		}
		return bool;
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}
}
