package bau5.mods.craftingsuite.common.tileentity;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileEntityProjectBench extends TileEntity implements IInventory, ISidedInventory{
	
	public class LocalInventoryCrafting extends InventoryCrafting{
		private TileEntity theTile;
		public LocalInventoryCrafting(TileEntity tileEntity) {
			super(new Container(){
				@Override
				public boolean canInteractWith(EntityPlayer var1) {
					return false;
				}
			}, 3, 3);
			theTile = tileEntity;
		}
	}
	
	public ItemStack[] inv = new ItemStack[28];
	public ItemStack result;
	private ItemStack lastResult = null;
	public IInventory craftingResult = new InventoryCraftResult();
	public LocalInventoryCrafting craftingMatrix = new LocalInventoryCrafting(this);
	public boolean containerInit = false;

	public TileEntityProjectBench() {
		
	}
	
	public ItemStack findRecipe(boolean fromPacket) {
		if(worldObj == null)
			return null;
		System.out.println("Finding recipe. " +worldObj.isRemote);
		lastResult = result;
		
		ItemStack stack = null;
		for(int i = 0; i < craftingMatrix.getSizeInventory(); ++i) 
		{
			stack = getStackInSlot(i);
			craftingMatrix.setInventorySlotContents(i, stack);
		}
	
		ItemStack recipe = CraftingManager.getInstance().findMatchingRecipe(craftingMatrix, worldObj);
//		if(recipe == null && validPlanInSlot() && haveSuppliesForPlan())
//			recipe = getPlanResult();
		setResult(recipe);
		
		if(!ItemStack.areItemStacksEqual(lastResult, result) && !fromPacket && !worldObj.isRemote){
			PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 30D, worldObj.provider.dimensionId, this.getDescriptionPacket());
		}
	
		return recipe;
		
	}

	private void setResult(ItemStack recipe) {
		result = recipe;
		craftingResult.setInventorySlotContents(0, result);
	}

	@Override
	public void onInventoryChanged() {
		if(!containerInit)
			findRecipe(false);
		super.onInventoryChanged();
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.data);
		if(this.worldObj != null)
			findRecipe(true);
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
		onInventoryChanged();
	}

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
	
	@Override
	public String getInvName() {
		return "Project Bench";
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
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this &&
				player.getDistanceSq(xCoord +0.5, yCoord +0.5, zCoord +0.5) < 10;
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
	public int[] getAccessibleSlotsFromSide(int side) {
		int[] slots = null;
		switch(side){
		case 0: 
			slots = new int[9];
			for(int i = 0; i < slots.length; i++)
				slots[i] = i;
			return slots;
		default:
			slots = new int[18];
			for(int i = 0; i < slots.length; i++)
				slots[i] = i +9;
			return slots;
		}
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return true;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;
	}

	
}