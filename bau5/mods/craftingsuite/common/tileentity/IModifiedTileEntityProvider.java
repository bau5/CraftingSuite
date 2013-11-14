package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ContainerHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.InventoryHandler;

public interface IModifiedTileEntityProvider {
	public EnumInventoryModifier getInventoryModifier();
	public NBTTagCompound getModifiers();
	public int getModifiedInventorySize();
	public int getBaseInventorySize();
	public int getToolModifierInvIndex();
	public int getSelectedToolIndex();
	public ItemStack getSelectedTool();
	public void initializeFromNBT(NBTTagCompound modifierTag);
	public void handleModifiers();
	
	public ContainerHandler getContainerHandler();
	public InventoryHandler getInventoryHandler();
	
	public byte getDirectionFacing();
	public void setDirectionFacing(byte byt);
	
	/**
	 * Get the bytes that contain the information 
	 * for the modifiers, typically will be the same 
	 * as the NBTTagCompound.getByteArray("modifiers")
	 * 
	 * @return the byte array with the modifiers.
	 */
	public byte[] getModifierBytes();
	
	public ItemStack[] getInventory();
	
	public ItemStack getRenderedResult();
	public void setRenderedResult(ItemStack stack);
	public void setTools(ItemStack[] stacks);
	public void setSelectedToolIndex(int i);
	public void setRandomShift(float f);
}
