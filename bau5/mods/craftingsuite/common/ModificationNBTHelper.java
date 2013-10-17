package bau5.mods.craftingsuite.common;

import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ModificationNBTHelper {
	
	public static final String tagListName = "ModificationInfo";
	public static final String upgradeArrayName = "UpgradeArray";
	
	private static final byte[] baseArray = new byte[]{ 2, 0, 0, 0 };
	
	public static NBTTagCompound makeBaseTag(){
		NBTTagCompound baseTag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagByteArray(upgradeArrayName, baseArray));
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
}
