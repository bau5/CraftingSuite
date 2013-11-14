package bau5.mods.craftingsuite.common.tileentity.parthandlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import bau5.mods.craftingsuite.common.CSLogger;
import bau5.mods.craftingsuite.common.helpers.ItemHelper;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;

public class InventoryHandler implements IInventory{
	public ItemStack[] inv = null;
	public ItemStack[] tools = new ItemStack[3];
	public int selectedToolIndex  = -1;
	public int toolIndexInCrafting= -1;
	private TileEntityModdedTable tileEntity;
	private IInventory craftResult = new InventoryCraftResult();
	private LocalInventoryCrafting craftingMatrix = new LocalInventoryCrafting();
	private LocalInventoryCrafting lastCraftMatrix= new LocalInventoryCrafting();
	private int[] craftingInventoryRange = new int[2];
	
	public ItemStack result = null;
	public ItemStack lastResult = null;
	
	public boolean shouldUpdate = false;
	
	public InventoryHandler(TileEntityModdedTable tile){
		tileEntity = tile;
	}
	
	public void initInventory() {
		inv = new ItemStack[tileEntity.modifications().getSizeInventory()];
		craftingInventoryRange = tileEntity.modifications().getCrafingRange();
	}

	public ItemStack findRecipe(boolean fromPacket){
		if(tileEntity.worldObj == null)
			return null;
		long start = System.currentTimeMillis();
		lastResult = result;
		boolean toolIn = false;
		ItemStack stack = null;
		if(getStackInSlot(4) == null && selectedToolIndex != -1 && !toolIn && stack == null){
			craftingMatrix.setInventorySlotContents(4, tileEntity.getSelectedTool());
			toolIn = true;
			toolIndexInCrafting = 4;
		}
		for(int i = 0; i < craftingMatrix.getSizeInventory(); ++i) 
		{
			stack = getStackInSlot(i);
			if(i == 4 && (toolIn && toolIndexInCrafting == 4))
				continue;
			if(!toolIn && selectedToolIndex != -1 && stack == null){
				craftingMatrix.setInventorySlotContents(i, tileEntity.getSelectedTool());
				toolIn = true;
				toolIndexInCrafting = i;
			}
			else
				craftingMatrix.setInventorySlotContents(i, stack);
		}
	
		ItemStack recipe = CraftingManager.getInstance().findMatchingRecipe(craftingMatrix, tileEntity.worldObj);
//		if(recipe == null && validPlanInSlot() && haveSuppliesForPlan())
//			recipe = getPlanResult();
		setResult(recipe);
		
		if(!ItemStack.areItemStacksEqual(lastResult, result) && !fromPacket && !tileEntity.worldObj.isRemote)
			tileEntity.sendRenderPacket = true;
		long end = System.currentTimeMillis();
		CSLogger.log("Recipe found on " + ((tileEntity.worldObj.isRemote) ? "client " : "server ") +"in " +(end - start) +" milliseconds.");
		return recipe;
	}

	private void setResult(ItemStack recipe) {
		result = recipe;
		craftResult.setInventorySlotContents(0, recipe);
	}
	
	public void onTileInventoryChanged() {
		if(!tileEntity.containerHandler().isContainerInit() && !tileEntity.containerHandler().isContainerWorking()){
			if(checkDifferences()){
				markForUpdate();
				makeNewMatrix();
			}
		}
	}
	
	private void makeNewMatrix() {
		for(int i = 0; i < 9; i++){
			lastCraftMatrix.setInventorySlotContents(i, inv[i +craftingInventoryRange[0]]);
		}
	}

	public void markForUpdate(){
		shouldUpdate = true;
	}
	
	private boolean checkDifferences() {
		for(int i = 0; i < 9; i++){
			if(!ItemStack.areItemStacksEqual(lastCraftMatrix.getStackInSlot(i), inv[i]))
				return true;
		}
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
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
		return "Modded Crafting Table";
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
		return tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) == tileEntity &&
				entityplayer.getDistanceSq(tileEntity.xCoord +0.5, tileEntity.yCoord +0.5, tileEntity.zCoord +0.5) < 64;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}


	public void readInventoryFromNBT(NBTTagCompound tagCompound) {
		NBTTagList tagList = tagCompound.getTagList("Inventory");
		if(inv != null){
			for(int i = 0; i < tagList.tagCount(); i++)
			{
				NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
				byte slot = tag.getByte("Slot");
				if(!tag.hasKey("Large")){
					if(slot >= 0 && slot < inv.length)
					{
						inv[slot] = ItemStack.loadItemStackFromNBT(tag);
					}
				}else{
					if(slot >= 0 && slot < inv.length){
						inv[slot] = ItemHelper.loadLargeItemStack(tag);
					}
				}
			}
		}
	}


	public void writeInventoryToNBT(NBTTagCompound tagCompound) {	
		NBTTagList itemList = new NBTTagList();	
		if(inv != null){
			for(int i = 0; i < inv.length; i++)
			{
				ItemStack stack = inv[i];
				if(stack != null)
				{
					NBTTagCompound tag = new NBTTagCompound();	
					tag.setByte("Slot", (byte)i);
					if(stack.stackSize > 64){
						tag.setByte("Large", (byte)1);
						ItemHelper.writeLargeStackToTag(stack, tag);
					}
					else{
						stack.writeToNBT(tag);
					}
					itemList.appendTag(tag);
					
				}
			}
		}
		tagCompound.setTag("Inventory", itemList);
	}

	public IInventory resultMatrix() {
		return craftResult;
	}
	
	public class LocalInventoryCrafting extends InventoryCrafting{
		private TileEntity theTile;
		public LocalInventoryCrafting() {
			super(new Container(){
				@Override
				public boolean canInteractWith(EntityPlayer var1) {
					return false;
				}
			}, 3, 3);
		}
		public LocalInventoryCrafting(TileEntity tileEntity){
			this();
			theTile = tileEntity;
		}
	}

	@Override
	public void onInventoryChanged() {
		onTileInventoryChanged();
	}
	
	public IModifiedTileEntityProvider getTileProvider(){
		return tileEntity;
	}

	public boolean checkValidity() {
		return inv != null && inv.length > 0;
	}
}
