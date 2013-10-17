package bau5.mods.craftingsuite.common;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;

public class ModificationStackHelper {
	
	public static ItemStack makeBasicMkICrafter(){
		NBTTagCompound baseTag = ModificationNBTHelper.makeBaseTag();
		NBTTagByteArray bytesTag = ModificationNBTHelper.getUpgradeByteArray(baseTag);
		byte[] bytes = bytesTag.byteArray;
		bytes[0] = 1;
		ItemStack theStack = new ItemStack(CraftingSuite.craftingBlock.blockID, 1, 1);
		theStack.setTagCompound(baseTag);
		return theStack;
	}

	public static ItemStack makeItemStack(ItemStack result, ItemStack[] input) {

		ItemStack stack = new ItemStack(result.itemID, result.stackSize, result.getItemDamage());
		NBTTagCompound baseTag = ModificationNBTHelper.makeBaseTag();
		NBTTagByteArray bytesTag = ModificationNBTHelper.getUpgradeByteArray(baseTag);
		byte[] bytes = bytesTag.byteArray;
		if(input[1].itemID == CraftingSuite.modItems.itemID){
			bytes[0] = 1;
		}
		if(input.length >= 3 && input[2].itemID == Block.carpet.blockID){
			bytes[3] = (byte)input[2].getItemDamage();
		}
		stack.stackTagCompound = baseTag;
		return stack;
	}
}
