package bau5.mods.craftingsuite.common;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;

public class ModificationStackHelper {
	
	public static ItemStack makeBasicMkICrafter(){
		NBTTagCompound baseTag = ModificationNBTHelper.makeBaseTag();
		byte[] bytes = ModificationNBTHelper.getUpgradeByteArray(baseTag);
		bytes[0] = 1;
		ItemStack theStack = new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 1);
		theStack.setTagCompound(baseTag);
		return theStack;
	}

	public static ItemStack makeBasicProjectBench() {
		ItemStack stack = new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2);
		byte[] bytes = ModificationNBTHelper.newBytes();
		bytes[0] = 2;
		bytes[1] = 3;
		bytes[3] = 14;
		bytes[4] = 1;
		stack = makeStackFromInfo(stack, bytes, new ItemStack(Block.planks.blockID, 1, 1));
		return stack;
	}
	
	public static ItemStack makeBasicAdvancedBench(){
		ItemStack stack = new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 3);
		byte[] bytes = ModificationNBTHelper.newBytes();
		bytes[0] = 3;
		bytes[3] = 13;
		stack = makeStackFromInfo(stack, bytes, new ItemStack(Block.planks.blockID, 1, 1));
		return stack;
	}
	
	public static ItemStack makeStackFromInfo(ItemStack vanillaStack, TileEntity tile) {
		byte[] bytes = null;
		ItemStack stack = null;
		if(tile instanceof IModifiedTileEntityProvider){
			if(((IModifiedTileEntityProvider) tile).getModifierTag() == null){
				CSLogger.logError("Tile entity has null modifiers. Invalidating.");
				tile.invalidate();
				return new ItemStack(Block.stone);
			}
			bytes = ModificationNBTHelper.getUpgradeByteArray(((IModifiedTileEntityProvider) tile).getModifierTag());
			stack = ItemStack.loadItemStackFromNBT(ModificationNBTHelper.getPlanksUsed(((TileEntityProjectBench) tile).getModifierTag()));
		}else{
			bytes = ModificationNBTHelper.newBytes();
		}
		vanillaStack = makeStackFromInfo(vanillaStack, bytes, stack);
		return vanillaStack;
	}

	public static ItemStack makeStackFromInfo(ItemStack stack, Object ... data) {
		NBTTagCompound newTag = null;
		byte[] upgrades = null;
		ItemStack planks = null;
		if(data[0] instanceof byte[]){
				upgrades = (byte[]) data[0];
			if(data[1] instanceof ItemStack)
				planks = (ItemStack) data[1];
			if(planks == null)
				planks = new ItemStack(Block.planks.blockID, 1, 0);
			newTag = ModificationNBTHelper.makeBaseTag();
			ModificationNBTHelper.setTagUpgradeBytes(newTag, upgrades);
			ModificationNBTHelper.setTagPlanksUsed(newTag, planks);
		}else{
			if(data[0] instanceof NBTTagCompound){
				newTag = (NBTTagCompound)data[0];
				NBTTagCompound baseTag = new NBTTagCompound("tag");
				baseTag.setTag(ModificationNBTHelper.modifierTag, newTag);
				newTag = (NBTTagCompound) baseTag.copy();
			}
		}
		stack.stackTagCompound = newTag;		
		return stack;
	}
	
	public static ItemStack makeItemStack(ItemStack result, ItemStack[] input) {
		ItemStack stack = new ItemStack(result.itemID, result.stackSize, result.getItemDamage());
		NBTTagCompound baseTag = ModificationNBTHelper.makeBaseTag();
		byte[] bytes = ModificationNBTHelper.getUpgradeByteArray(baseTag);
		for(ItemStack stackUsed : input){
			if(stackUsed.itemID == CraftingSuite.modItems.itemID){
				if(stackUsed.getItemDamage() == 1)
					bytes[0] = 1;
				if(stackUsed.getItemDamage() == 2)
					bytes[0] = 2;
				continue;
			} 
			if(OreDictionary.getOreID(stackUsed) == 1){
				ItemStack temp = stackUsed.copy();
				temp.stackSize = 1;
				ModificationNBTHelper.setTagPlanksUsed(baseTag, temp);
				continue;
			}
			if(stackUsed.itemID == Block.carpet.blockID){
				bytes[3] = (byte)stackUsed.getItemDamage();
				continue;
			}
			if(stackUsed.itemID == Block.blockClay.blockID){
				bytes[4] = 1;
				continue;
			}
		}
		stack.stackTagCompound = baseTag;
		return stack;
	}
}
