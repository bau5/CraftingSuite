package bau5.mods.craftingsuite.common;

import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

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
		list.appendTag(makeByteArrayTag());
		baseTag.setTag(tagListName, list);
		return baseTag;
	}
	
	public static NBTTagByteArray makeByteArrayTag(){
		return new NBTTagByteArray(upgradeArrayName, newBytes());
	}
	
	public static byte[] convertTileEntityToTag(TileEntity tile) {
		if(tile instanceof TileEntityProjectBench){
			return ((TileEntityProjectBench) tile).upgrades;
		}
		return newBytes();
	}
	
	public static void setTagUpgradeBytes(NBTTagCompound baseTag, byte[] bytes) {
		NBTTagByteArray byts = getUpgradeByteArray(baseTag);
		byts.byteArray = bytes;
	}
	
	public static NBTTagList getModInfoList(NBTTagCompound tag){
		if(tag == null){
			tag = makeBaseTag();
		}
		if(tag.hasKey(tagListName)){
			return tag.getTagList(tagListName);
		}else{
			NBTTagList list = new NBTTagList();
			list.appendTag(makeByteArrayTag());
			tag.setTag(tagListName, list);
			return tag.getTagList(tagListName);
		}
	}
	
	public static NBTTagByteArray getUpgradeByteArray(NBTTagCompound tag){
		NBTTagByteArray array = null;
		try{
			array = (NBTTagByteArray)getModInfoList(tag).tagAt(0);
		}catch(Exception ex){
			FMLLog.log(Level.SEVERE, ex, "%s", "Crafting Suite encountered an error. A broken stack was encountered, resetting stack.");
			array = makeByteArrayTag();
			getModInfoList(tag).appendTag(array);
		}
		return array;
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
