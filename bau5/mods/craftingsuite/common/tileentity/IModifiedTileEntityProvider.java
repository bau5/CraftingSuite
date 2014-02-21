package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ContainerHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.InventoryHandler;

public interface IModifiedTileEntityProvider {
	public EnumInventoryModifier getInventoryModifier();
	public EnumExtraModifier	 getExtraModifier();
	public int getModifiedInventorySize();
	public int getBaseInventorySize();
	public int getToolModifierInvIndex();
	public int getSelectedToolIndex();
	public ItemStack getSelectedTool();
	public int getPlanIndexInInventory();
	public void initializeFromNBT(NBTTagCompound modifierTag);
	public void handleModifiers();
	
	public ContainerHandler getContainerHandler();
	public InventoryHandler getInventoryHandler();
	
	public byte getDirectionFacing();
	public void setDirectionFacing(byte byt);
	
	public ItemStack[] getInventory();
	
	public ItemStack getRenderedResult();
	public void setRenderedResult(ItemStack stack);
	public void setTools(ItemStack[] stacks);
	public void setSelectedToolIndex(int i);
	public void setRandomShift(float f);
	public Modifications getModifications();
	public void initializeFromMods(Modifications mods);
}
