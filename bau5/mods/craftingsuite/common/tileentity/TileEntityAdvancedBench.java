package bau5.mods.craftingsuite.common.tileentity;

import bau5.mods.craftingsuite.common.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAdvancedBench extends TileEntity implements IModifiedTileEntityProvider, IInventory {
	public ItemStack[] inv;
	private byte directionFacing = 0;
	private byte[] upgrades;
	private NBTTagCompound modifierTag;
	
	public TileEntityAdvancedBench(){
		modifierTag = ModificationNBTHelper.getModifierTag(null);
		upgrades = ModificationNBTHelper.newBytes();
	}
	
	@Override
	public EnumInventoryModifier getInventoryModifier() {
		return EnumInventoryModifier.NONE;
	}

	@Override
	public int getModifiedInventorySize() {
		return getBaseInventorySize() + getInventoryModifier().getNumSlots();
	}

	@Override
	public int getBaseInventorySize() {
		return 36;
	}

	@Override
	public void initializeFromNBT(NBTTagCompound modifierTag) {
		this.modifierTag = modifierTag;
		upgrades = modifierTag.getByteArray(ModificationNBTHelper.upgradeArrayName);
	}

	@Override
	public void handleModifiers() {
		inv = new ItemStack[getModifiedInventorySize()];
	}

	@Override
	public int getToolModifierInvIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getDirectionFacing() {
		return directionFacing;
	}

	@Override
	public void setDirectionFacing(byte byt) {
		directionFacing = byt;
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if(stack != null)
		{
			if(stack.stackSize <= amount)
			{
				setInventorySlotContents(slot, null);
			} else
			{
				stack = stack.splitStack(amount);
				if(stack.stackSize == 0) 
				{
					setInventorySlotContents(slot, null);
				}else
					onInventoryChanged();
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if(stack != null)
		{
			setInventorySlotContents(slot, null);
		}
		onInventoryChanged();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv[slot] = stack;
		if(stack != null && stack.stackSize > getInventoryStackLimit())
		{
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName() {
		// TODO Auto-generated method stub
		return "Advanced Bench";
	}

	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this &&
				player.getDistanceSq(xCoord +0.5, yCoord +0.5, zCoord +0.5) < 64;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public NBTTagCompound getModifierTag() {
		return modifierTag;
	}
}
