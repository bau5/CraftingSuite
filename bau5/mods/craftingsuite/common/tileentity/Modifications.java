package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import bau5.mods.craftingsuite.common.helpers.ModificationNBTHelper;

public class Modifications {
	private int _type;
	private int _upgrade;
	private int _color;
	private int _visual;
	private ItemStack _planks;
	
	private boolean init = false;
	
	public Modifications(ItemStack planks, byte[] bytes){
		init(planks, bytes);
	}
	
	public Modifications(){}
	
	public ItemStack getPlanks(){
		if(_planks == null)
			return null;
		return _planks.copy();
	}
	
	public int type(){
		return _type;
	}
	
	public int upgrades(){
		return _upgrade;
	}
	
	/**
	 * This is shifted +1, 0 for no color, 1 for white.
	 */
	public int color(){
		return _color;
	}
	
	public int visual(){
		return _visual;
	}
	
	private void init(ItemStack planks, byte[] bytes){
		_type = bytes[0];
		_upgrade = bytes[1];
		_color = bytes[3];
		_visual = bytes[4];
		_planks = planks.copy();
		init = true;
	}
	
	public boolean isInitialized() {
		return init;
	}
	
	public byte[] buildUpgradeArray(){
		byte[] bytes = new byte[5];
		bytes[0] = (byte) _type;
		bytes[1] = (byte) _upgrade;
		bytes[3] = (byte) _color;
		bytes[4] = (byte) _visual;
		return bytes;
	}

	public Modifications convert(ItemStack stack2) {
		NBTTagCompound modifiers = stack2.stackTagCompound;
		if(stack2.stackTagCompound != null && stack2.stackTagCompound.hasKey(ModificationNBTHelper.modifierTag)){
			modifiers = stack2.stackTagCompound.getCompoundTag(ModificationNBTHelper.modifierTag);
		}
		byte[] bytes = modifiers.getByteArray(ModificationNBTHelper.upgradeArrayName);
		for(int i = 0; i < bytes.length; i++){
			if(bytes[i] == -1 )
				bytes[i] = 0;
		}
		_type = bytes[0];
		_upgrade = bytes[1];
		_color = bytes[3];
		_visual = bytes[4];
		_planks = ItemStack.loadItemStackFromNBT(modifiers.getCompoundTag(ModificationNBTHelper.planksName));
		init = true;
		return this;
	}
}