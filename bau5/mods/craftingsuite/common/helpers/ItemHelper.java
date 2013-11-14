package bau5.mods.craftingsuite.common.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemHelper {
	/**
	 * Check if the stacks are identical, omitting stack size.
	 * 
	 * @param stack 
	 * @param stack1
	 * @return boolean result
	 */
	public static boolean checkItemMatch(ItemStack stack, ItemStack stack1){
		if(stack == null || stack1 == null)
			return false;
		return stack.itemID == stack1.itemID && stack.getItemDamage() == stack1.getItemDamage()
				&& ItemStack.areItemStackTagsEqual(stack, stack1);
	}
	
	public static void writeLargeStackToTag(ItemStack stack, NBTTagCompound tag){
        tag.setShort("id", (short)stack.itemID);
        tag.setInteger("Count", (int)stack.stackSize);
        tag.setShort("Damage", (short)stack.getItemDamage());

        if (stack.stackTagCompound != null){
            tag.setTag("tag", stack.stackTagCompound);
        }
	}

	public static ItemStack loadLargeItemStack(NBTTagCompound tag) {
        ItemStack stack = new ItemStack(0, 0, 0);
        stack.itemID = tag.getShort("id");
        stack.stackSize = tag.getInteger("Count");
        stack.setItemDamage(tag.getShort("Damage"));

        if (stack.getItemDamage() < 0){
            stack.setItemDamage(0);
        }

        if (tag.hasKey("tag")){
            stack.stackTagCompound = tag.getCompoundTag("tag");
        }
        return stack;
	}
}
