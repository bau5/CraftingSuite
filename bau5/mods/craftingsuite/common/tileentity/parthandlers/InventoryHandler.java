package bau5.mods.craftingsuite.common.tileentity.parthandlers;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import bau5.mods.craftingsuite.common.CSLogger;
import bau5.mods.craftingsuite.common.helpers.ItemHelper;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.inventory.LocalInventoryCrafting;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.TileEntityBase;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench.PositionedFluidStack;

public class InventoryHandler implements IInventory{
	public ItemStack[] inv = null;
	public ItemStack[] tools = new ItemStack[3];
	public int selectedToolIndex  = -1;
	public int toolIndexInCrafting= -1;

	public int planIndex;
	
	private TileEntityBase tileEntity;
	
	private IInventory craftResult = new InventoryCraftResult();
	private LocalInventoryCrafting craftingMatrix = new LocalInventoryCrafting();
	private LocalInventoryCrafting lastCraftMatrix= new LocalInventoryCrafting();
	private int[] craftingInventoryRange = new int[2];
	
	public HashMap<String, int[]> inventoryMap = new HashMap<String, int[]>();
	
	public ItemStack result = null;
	public ItemStack lastResult = null;
	
	public boolean shouldUpdate = false;
	
	public InventoryHandler(TileEntityBase tile){
		tileEntity = tile;
	}
	
	public void initInventory() {
		inv = new ItemStack[tileEntity.getModifiedInventorySize()];
		int typ = tileEntity.getModifications().type();
		switch(typ){
		case 1: 
			inventoryMap.put("Crafting", new int[]{0,8});
			break;
		case 2: 
			inventoryMap.put("Crafting", new int[]{0,8});
			inventoryMap.put("Supply", new int[]{9,26});
			break;
		}
		int[] indicies = new int[2];
		switch(tileEntity.getInventoryModifier()){
		case DEEP: 
			if(typ == 1)
				indicies = new int[]{9,9};
			else
				indicies = new int[]{27,27};
			inventoryMap.put("Deep", indicies);
			break;
		case TOOLS:
			inventoryMap.put("Tools", new int[]{27,29});
			break;
		case PLAN: 
			inventoryMap.put("Plan", new int[]{27,27});
			break;
		case NONE: break;
		}
	}

	public ItemStack findRecipe(boolean fromPacket){
		if(tileEntity.worldObj == null)
			return null;
		long start = System.currentTimeMillis();
		lastResult = result != null ? result.copy() : null;
		boolean toolIn = false;
		toolIndexInCrafting = -1;
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
		
		int neededAmount = 0;
		boolean fail = false;
		if(tileEntity instanceof TileEntityProjectBench){
			for(PositionedFluidStack container : ((TileEntityProjectBench)tileEntity).fluidForCrafting){
				if(craftingMatrix.getStackInSlot(container.slotNumber) == null){
					craftingMatrix.setInventorySlotContents(container.slotNumber, container.full);
					container.setInUse();
					neededAmount += container.fluid.amount;
				}
			}
			if(((TileEntityProjectBench)tileEntity).getFluidInTank(ForgeDirection.UP) == null || neededAmount > ((TileEntityProjectBench)tileEntity).getFluidInTank(ForgeDirection.UP).amount){
				fail = true;
			}
		}
		ItemStack recipe = CraftingManager.getInstance().findMatchingRecipe(craftingMatrix, tileEntity.worldObj);
		if(fail)
			recipe = null;
		if(recipe == null && tileEntity.getInventoryModifier() == EnumInventoryModifier.PLAN){
			ItemStack planStack = inv[planIndex];
			if(planStack != null && planStack.hasTagCompound()){
				setResult(ItemStack.loadItemStackFromNBT((NBTTagCompound)planStack.stackTagCompound.getTag("Result")));
			}else
				setResult(recipe);
		}else
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
	
	public ItemStack getResult(){
		return result;
	}
	
	public void onTileInventoryChanged() {
		if(!tileEntity.getContainerHandler().isContainerInit() && !tileEntity.getContainerHandler().isContainerWorking()){
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
		if((stack != null && stack.stackSize >= getInventoryStackLimit() && tileEntity.getInventoryModifier() == EnumInventoryModifier.DEEP)){
			int[] i = inventoryMap.get("Deep");
			if(i == null)
				return;
			if(i[0] != slot && stack != null && stack.stackSize > getInventoryStackLimit())
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

	public LocalInventoryCrafting getCraftingMatrix(){
		return craftingMatrix;
	}
	
	public void setCraftingMatrix(LocalInventoryCrafting crafting){
		craftingMatrix = crafting;
	}
	
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

	public boolean affectsCrafting(int slot) {
		if(slot == -1)
			return false;
		for(String str : inventoryMap.keySet()){
			if(str.equals("Crafting")){
				int[] indicies = inventoryMap.get(str);
				if(slot <= indicies[1] && slot >= indicies[0]){
					return true;
				}
			}else if(str.equals("Plan")){
				int[] indicies = inventoryMap.get(str);
				if(slot <= indicies[1] && slot >= indicies[0]){
					return true;
				}
			}else if(str.equals("Tools")){
				int[] indicies = inventoryMap.get(str);
				if(slot <= indicies[1] && slot >= indicies[0]){
					return true;
				}
			}
		}
		return false;
	}
}
