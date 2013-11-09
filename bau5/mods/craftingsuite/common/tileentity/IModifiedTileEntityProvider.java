package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;

public interface IModifiedTileEntityProvider {
	public EnumInventoryModifier getInventoryModifier();
	public int getModifiedInventorySize();
	public int getBaseInventorySize();
	public void initializeFromNBT(NBTTagCompound modifierTag);
	public void handleModifiers();
	
	public int getToolModifierInvIndex();
	public byte getDirectionFacing();
	public void setDirectionFacing(byte byt);
	public NBTTagCompound getModifiers();
	
	/**
	 * Get the bytes that contain the information 
	 * for the modifiers, typically will be the same 
	 * as the NBTTagCompound.getByteArray("modifiers")
	 * 
	 * @return the byte array with the modifiers.
	 */
	public byte[] getModifierBytes();
}
