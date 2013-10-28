package bau5.mods.craftingsuite.common;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import cpw.mods.fml.common.FMLLog;

public class ModificationNBTHelper {
	
	public static final String tagListName = "ModificationInfo";
	public static final String upgradeArrayName = "UpgradeArray";
	public static final String planksName = "PlanksUsed";
	public static final String modifierTag = "Modifiers";
	
	public static final int ARRAY_LENGTH = 5;
	
	/**
	 *  The byte array stores the relevant upgrades as bytes for low cost
	 *  saving. 
	 *  byte[0] is the meta of the table
	 *  byte[1] is unused atm, intended for functional upgrades
	 *  byte[2] unused
	 *  byte[3] stores the meta of the carpet used
	 *  byte[4] stores if the block has clay upgrade (render)
	 */
	
	public static NBTTagCompound makeBaseTag(){
		NBTTagCompound baseTag = new NBTTagCompound("tag");
		NBTTagCompound modifiers = new NBTTagCompound();
		modifiers.setByteArray(upgradeArrayName, newBytes());
		baseTag.setTag(modifierTag, modifiers);
		return baseTag;
	}
	
	public static void setTagUpgradeBytes(NBTTagCompound baseTag, byte[] bytes) {
		NBTTagCompound modifier = getModifierTag(baseTag);
		if(bytes == null){
			CSLogger.logError("Trying to set upgrades tag with null bytes. Expect broken things.");
			bytes = newBytes();
		}
		modifier.setByteArray(upgradeArrayName, bytes);
	}
	
	public static void setTagPlanksUsed(NBTTagCompound newTag, ItemStack planks) {
		NBTTagCompound tag = getModifierTag(newTag);
		if(planks == null){
			CSLogger.logError("Trying to set planks used with null plank ItemStack.");
			planks = new ItemStack(Block.planks.blockID, 1, 1);
		}
		tag.setCompoundTag(planksName, planks.writeToNBT(new NBTTagCompound()));
	}
	
	public static NBTTagCompound getModifierTag(NBTTagCompound baseTag){
		if(baseTag == null)
			baseTag = makeBaseTag();
		if(baseTag.getName() == modifierTag)
			return baseTag;
		if(baseTag.hasKey(modifierTag))
			return baseTag.getCompoundTag(modifierTag);
		else{
			NBTTagCompound tag = new NBTTagCompound();
			baseTag.setTag(modifierTag, tag);
			return tag.getCompoundTag(modifierTag);
		}
	}

	public static byte[] getUpgradeByteArray(NBTTagCompound tag){
		byte[] array = null;
		try{
			if(tag.hasKey(modifierTag))
				array = tag.getCompoundTag(modifierTag).getByteArray(upgradeArrayName);
			else if(tag.hasKey(upgradeArrayName))
				array = tag.getByteArray(upgradeArrayName);
		}catch(Exception ex){
			FMLLog.log(Level.SEVERE, ex, "%s", "Crafting Suite encountered an error. A broken stack was encountered, resetting stack.");
			array = newBytes();
			getModifierTag(tag).setByteArray(upgradeArrayName, array);
		}
		return array;
	}
	
	public static byte[] ensureSize(byte[] array){
		if(array.length != ARRAY_LENGTH){
			byte[] bytes = new byte[ARRAY_LENGTH];
			for(int i = 0; i < bytes.length; i++){
				if(i < array.length)
					bytes[i] = array[i];
				else
					bytes[i] = -1;
			}
			return bytes;
		}
		return array;
	}
	public static byte[] newBytes(){
		byte[] bytes = new byte[ARRAY_LENGTH];
		int i = 0;
		for(byte b : bytes)
			bytes[i++] = -1;
		return bytes;
	}
	
	public static NBTTagCompound convertOldNBT(NBTTagCompound oldTag){
		NBTTagCompound tag = oldTag.getCompoundTag(modifierTag);
		
		return oldTag;
	}
	
	public static NBTTagCompound getPlanksUsed_Base(NBTTagCompound stackTagCompound){
		if(stackTagCompound == null){
			return new ItemStack(Block.planks.blockID, 1, 0).writeToNBT(new NBTTagCompound());
		}
		return getPlanksUsed(stackTagCompound.getCompoundTag(modifierTag));
	}
	
	public static NBTTagCompound getPlanksUsed(NBTTagCompound stackTagCompound) {
		if(stackTagCompound == null)
			return new ItemStack(Block.planks.blockID, 1, 0).writeToNBT(new NBTTagCompound());
		return stackTagCompound.getCompoundTag(planksName);
	}
}
