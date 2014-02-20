package bau5.mods.craftingsuite.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import bau5.mods.craftingsuite.common.helpers.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.helpers.ModificationStackHelper;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.Modifications;

public class ItemBlockCrafting extends ItemBlock {

	public ItemBlockCrafting(int id) {
		super(id);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabDecorations);
		setUnlocalizedName("ctblock");
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ, int metadata) {
		ItemStack stack2 = stack.copy();
		Modifications mods = ModificationStackHelper.convertToModInfo(stack2);
		boolean bool = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof IModifiedTileEntityProvider){
			IModifiedTileEntityProvider tile = (IModifiedTileEntityProvider)te;
			tile.initializeFromMods(mods);
			tile.handleModifiers();
		}
		return bool;
	}
	
	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public void addInformation(ItemStack itemstack,
			EntityPlayer player, List list, boolean par4) {
		super.addInformation(itemstack, player, list, par4);
//		String str = player.openContainer.getClass().toString();
		if(itemstack.stackTagCompound != null/*	 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || str.contains("codechicken")*/){
			byte[] bytes = ModificationNBTHelper.getUpgradeByteArray(itemstack.stackTagCompound);
			if(bytes[0] == 2)
				list.add("-" +EnumChatFormatting.DARK_GRAY +StatCollector.translateToLocal(new ItemStack(CraftingSuite.modItems, 1, 2).getUnlocalizedName() +".name"));
			if(bytes[0] == 1)
				list.add("-" +EnumChatFormatting.DARK_GRAY +StatCollector.translateToLocal(new ItemStack(CraftingSuite.modItems, 1, 1).getUnlocalizedName() +".name"));
			if(bytes[1] != -1){
				if(bytes[1] >= 3)
					list.add("-" +EnumChatFormatting.DARK_GRAY +StatCollector.translateToLocal(new ItemStack(CraftingSuite.modItems, 1, bytes[1]).getUnlocalizedName() +".name"));
			}
			if(bytes[3] != 0)
				list.add("-" +EnumChatFormatting.DARK_GRAY +StatCollector.translateToLocal(Item.itemsList[Block.cloth.blockID].getUnlocalizedName(new ItemStack(Block.cloth.blockID, 1, bytes[3] -1)) +".name"));
			if(bytes[4] != -1)
				list.add("-" +EnumChatFormatting.DARK_GRAY +StatCollector.translateToLocal(Block.blockClay.getUnlocalizedName() +".name"));
		}
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if(stack.stackTagCompound != null){
			byte[] bytes = ModificationNBTHelper.getUpgradeByteArray(stack.stackTagCompound);
			String part = "";
			switch(bytes[0]){
			case 1: part = "ct";
				break;
			case 2: part = "pb";
				break;
			}
			String str = String.format("%s.%s", super.getUnlocalizedName(stack), part);
			return str;
		}
		return String.format("%s.%s", super.getUnlocalizedName(stack), "pb");
	}
}
