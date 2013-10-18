package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import bau5.mods.craftingsuite.common.ModificationCrafter;
import bau5.mods.craftingsuite.common.ModificationCrafter.ModificationRecipe;

public class TileEntityModificationTable extends TileEntity implements IInventory{
	
	public ItemStack[] inv;
	public IInventory craftResult = new InventoryCraftResult();
	public ItemStack result = null;
	public ItemStack[] inputForResult = null;
	
	public int finishTime;
	public int timeCrafting;
	public boolean crafting;
	public float rotation = 0.0F;
	private int increment;
	
	public TileEntityModificationTable(){
		inv = new ItemStack[5];
	}
	
	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		updateResult();
	}

	public void craftRecipe() {
		if(!worldObj.isRemote)
			updateResult();
		if(result != null){
			ItemStack[] stacks = new ItemStack[inv.length];
			for(int i = 0; i < stacks.length; i++)
				stacks[i] = inv[i];
			ModificationRecipe rec = ModificationCrafter.instance().findRecipe(this, stacks);
			ItemStack output = rec.getExactOutput(ModificationCrafter.instance().filterNulls(stacks));
			if(ItemStack.areItemStacksEqual(result, output) && ItemStack.areItemStackTagsEqual(result, output)){
				crafting = true;
				rec.consumeItems(this);
				initiateCrafting();
			}else{
				result = null;
				updateResult();
			}
		}
	}
	
	private void initiateCrafting() {
		if(isCrafting())
			return;
		finishTime = 2000;
		timeCrafting = 0;
		increment = 10;
		craftResult.setInventorySlotContents(0, null);
	}
	
	private void finishCrafting(){
		craftResult.setInventorySlotContents(0, result);
		crafting = false;
		updateResult();
	}

	public ItemStack updateResult(){
		if(isCrafting() || crafting)
			return null;
		ItemStack[] stacks = new ItemStack[inv.length];
		for(int i = 0; i < stacks.length; i++)
			stacks[i] = inv[i];
		ModificationRecipe recipe = ModificationCrafter.instance().findRecipe(this, stacks);
		result = recipe != null ? recipe.getExactOutput(ModificationCrafter.instance().filterNulls(stacks)) : null;
		return result;
	}
	
	public ItemStack getResult(){
		return result;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		timeCrafting += increment;
		if(!isCrafting() && timeCrafting > 0){
			finishTime = 0;
			timeCrafting = 0;
			increment = 0;
			rotation = 0.0F;
			finishCrafting();
		}
		if(isCrafting()){
			if(timeCrafting <= finishTime/2) 
				rotation += 0.1F;
			else
				rotation -= 0.1F;
		}
	}
	
	public boolean isCrafting(){
		return finishTime > timeCrafting;
	}
	
	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if(stack != null){
			if(stack.stackSize <= amount){
				setInventorySlotContents(slot, null);
			}else{
				stack = stack.splitStack(amount);
				if(stack.stackSize == 0){
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
		if(stack != null){
			setInventorySlotContents(slot, null);
		}
		onInventoryChanged();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv[slot] = stack;
		if(stack != null && stack.stackSize > getInventoryStackLimit()){
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName() {
		return "Modification Table";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this &&
				entityplayer.getDistanceSq(xCoord +0.5, yCoord +0.5, zCoord +0.5) < 64;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		
		NBTTagList tagList = tagCompound.getTagList("Inventory");
		for(int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if(slot >= 0 && slot < inv.length)
			{
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		
		NBTTagList itemList = new NBTTagList();	
		
		for(int i = 0; i < inv.length; i++)
		{
			ItemStack stack = inv[i];
			if(stack != null)
			{
				NBTTagCompound tag = new NBTTagCompound();	
				tag.setByte("Slot", (byte)i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Inventory", itemList);
	}
}
