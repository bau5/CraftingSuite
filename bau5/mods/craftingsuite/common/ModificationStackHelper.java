package bau5.mods.craftingsuite.common;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class ModificationStackHelper {
	
	public static ItemStack makeBasicMkICrafter(){
		NBTTagCompound baseTag = ModificationNBTHelper.makeBaseTag();
		NBTTagByteArray bytesTag = ModificationNBTHelper.getUpgradeByteArray(baseTag);
		byte[] bytes = bytesTag.byteArray;
		bytes[0] = 1;
		ItemStack theStack = new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 1);
		theStack.setTagCompound(baseTag);
		return theStack;
	}

	public static ItemStack makeBasicProjectBench() {
		ItemStack stack = new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2);
		NBTTagCompound baseTag = ModificationNBTHelper.makeBaseTag();
		byte[] bytes = ModificationNBTHelper.getUpgradeByteArray(baseTag).byteArray;
		bytes[0] = 2;
		bytes[2] = 1;
		bytes[3] = 14;
		stack.stackTagCompound = baseTag;
		return stack;
	}

	public static ItemStack makeItemStack(ItemStack result, ItemStack[] input) {

		ItemStack stack = new ItemStack(result.itemID, result.stackSize, result.getItemDamage());
		NBTTagCompound baseTag = ModificationNBTHelper.makeBaseTag();
		NBTTagByteArray bytesTag = ModificationNBTHelper.getUpgradeByteArray(baseTag);
		byte[] bytes = bytesTag.byteArray;
		for(ItemStack stackUsed : input){
			if(stackUsed.itemID == CraftingSuite.modItems.itemID){
				if(stackUsed.getItemDamage() == 1)
					bytes[0] = 1;
				if(stackUsed.getItemDamage() == 2)
					bytes[0] = 2;
				continue;
			} 
			if(OreDictionary.getOreID(stackUsed) == 1){
				bytes[2] = (byte)stackUsed.getItemDamage();
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
