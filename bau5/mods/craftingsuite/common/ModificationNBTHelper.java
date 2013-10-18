package bau5.mods.craftingsuite.common;

import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ModificationNBTHelper {
	
	public static final String tagListName = "ModificationInfo";
	public static final String upgradeArrayName = "UpgradeArray";
	
	public static final int ARRAY_LENGTH = 5;
	
	/**
	 *  The byte array stores the relevant upgrades as bytes for low cost
	 *  saving. 
	 *  byte[0] is the meta of the table
	 *  byte[1] is unused atm, intended for functional upgrades
	 *  byte[2] stores the meta of the plank used
	 *  byte[3] stores the meta of the carpet used
	 *  byte[4] stores if the block has clay upgrade (render)
	 */
	
	public static NBTTagCompound makeBaseTag(){
		NBTTagCompound baseTag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		byte[] array = newBytes();
		list.appendTag(new NBTTagByteArray(upgradeArrayName, array));
		baseTag.setTag(tagListName, list);
		return baseTag;
	}
	
	public static NBTTagList getModInfoList(NBTTagCompound tag){
		return tag.getTagList(tagListName);
	}
	
	public static NBTTagByteArray getUpgradeByteArray(NBTTagCompound tag){
		return (NBTTagByteArray)getModInfoList(tag).tagAt(0);
	}
	public static NBTTagByteArray getUpgradeByteArray(NBTTagList tagList){
		return (NBTTagByteArray)tagList.tagAt(0);
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
}
