package bau5.mods.craftingsuite.common.tileentity;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import bau5.mods.craftingsuite.common.CSLogger;
import bau5.mods.craftingsuite.common.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.helpers.ItemHelper;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ContainerHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.InventoryHandler;
import cpw.mods.fml.client.FMLClientHandler;

public class TileEntityProjectBench extends TileEntityBase implements IModifiedTileEntityProvider, IInventory, ISidedInventory{
	
	private NBTTagCompound modifiers;
	private byte[]		   upgrades;
	
	public TileNetHandler netHandler;
	public InventoryHandler inventoryHandler;
	public ContainerHandler containerHandler;
	
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
	
	public ItemStack[] inv = null;
	public ItemStack result;
	public ItemStack[] tools = new ItemStack[3];
	public int selectedToolIndex  = -1;
	public int toolIndexInCrafting= -1;
	private ItemStack lastResult = null;
	public IInventory craftingResult = new InventoryCraftResult();
	public LocalInventoryCrafting craftingMatrix = new LocalInventoryCrafting(this);
	public boolean containerInit = false;
	public boolean containerWorking = false;
	public boolean initialized = false;
	private boolean shouldUpdateOutput;
	private int update = 0;
	private byte directionFacing = 0;
	public float randomShift = 0.0F;
	
	private HashMap<EnumInventoryModifier, int[]> inventoryMap = new HashMap();
	
	/**
	 * Unlinked crafting matrix, used to test the difference between inventory changes,
	 * avoid spam of searching the recipe list.
	 */
	private LocalInventoryCrafting lastCraftMatrix = new LocalInventoryCrafting();

	public TileEntityProjectBench() {
		modifiers = ModificationNBTHelper.getModifierTag(null);
		upgrades = ModificationNBTHelper.newBytes();
		netHandler = new TileNetHandler(this);
		containerHandler = new ContainerHandler();
		inventoryHandler = new InventoryHandler(this);
	}

	@Override
	public void initializeFromNBT(NBTTagCompound nbtTagCompound) {
		modifiers = ModificationNBTHelper.getModifierTag(nbtTagCompound);
		upgrades = modifiers.getByteArray(ModificationNBTHelper.upgradeArrayName);
		initialized = true;
	}

	private void setResult(ItemStack recipe) {
		result = recipe;
		craftingResult.setInventorySlotContents(0, result);
	}

	@Override
	public void handleModifiers() {
		inv = new ItemStack[getModifiedInventorySize()];
		inventoryHandler.initInventory();
		inventoryHandler.inv = inv;
		if(getInventoryModifier() == EnumInventoryModifier.PLAN)
			inventoryHandler.planIndex = 27;
		buildInventoryMap();
	}
	
	@Override
	public EnumInventoryModifier getInventoryModifier() {
		if(upgrades == null || upgrades.length == 0)
			return EnumInventoryModifier.NONE;
		switch(upgrades[1]){
		case 3: return EnumInventoryModifier.TOOLS;
		case 4: return EnumInventoryModifier.DEEP;
		case 5: return EnumInventoryModifier.PLAN;
		}
		return EnumInventoryModifier.NONE;
	}
	
	private void buildInventoryMap(){
		inventoryMap = new HashMap<EnumInventoryModifier, int[]>();
		int[] indicies = new int[2];
		indicies[0] = 0; indicies[1] = 27;
		inventoryMap.put(EnumInventoryModifier.NONE, indicies);
		switch(getInventoryModifier()){
		case TOOLS:
			indicies = new int[2];
			indicies[0] = 27; indicies[1] = 30;
			inventoryMap.put(EnumInventoryModifier.TOOLS, indicies);
			break;
		case DEEP: 
			indicies[0] = indicies[1] = 27;
			inventoryMap.put(EnumInventoryModifier.DEEP, indicies);
		case PLAN:
			indicies[0] = 27; indicies[1] = 27;
			inventoryMap.put(EnumInventoryModifier.PLAN, indicies);
			break;
		default: break;
		}
	}

	@Override
	public int getToolModifierInvIndex() {
		if(getInventoryModifier() == EnumInventoryModifier.TOOLS){
			if(inventoryMap == null || !inventoryMap.containsKey(EnumInventoryModifier.TOOLS)){
				buildInventoryMap();
				return 27;
			}
			return inventoryMap.get(EnumInventoryModifier.TOOLS)[0];
		}
		else
			return -1;
	}

	@Override
	public ItemStack getSelectedTool(){
		if(getInventoryModifier() != EnumInventoryModifier.TOOLS)
			return null;
//		return inv[selectedToolIndex + getToolModifierInvIndex()];
		return inv[inventoryHandler.selectedToolIndex + getToolModifierInvIndex()];
	}
	
	@Override
	public int getSelectedToolIndex(){
//		return selectedToolIndex;
		return inventoryHandler.selectedToolIndex;
	}
	@Override
	public int getModifiedInventorySize() {
		return getBaseInventorySize() +getInventoryModifier().getNumSlots();
	}
	
	@Override
	public int getBaseInventorySize() {
		return 27;
	}

	public NBTTagCompound getModifiers() {
		return modifiers;
	}
	
	public byte[] getUpgrades(){
		return upgrades;
	}

	public ItemStack getPlanksUsed() {
		NBTTagCompound tag = ModificationNBTHelper.getPlanksUsed(modifiers);
		ItemStack stack = ItemStack.loadItemStackFromNBT(ModificationNBTHelper.getPlanksUsed(modifiers));
		return stack;
	}
	
	public LocalInventoryCrafting getCopyOfMatrix(LocalInventoryCrafting matrix){
		LocalInventoryCrafting temp = new LocalInventoryCrafting(this);
		for(int i = 0; i < temp.getSizeInventory(); i++){
			temp.setInventorySlotContents(i, matrix.getStackInSlot(i) != null ? matrix.getStackInSlot(i).copy() : null);
		}
		return temp;
	}
	
	@Override
	public void onInventoryChanged() {
//		if(!containerInit && !containerWorking){
//			if(checkDifferences()){
//				markForUpdate();
//				makeNewMatrix();
//			}
//		}
		inventoryHandler.onInventoryChanged();
		super.onInventoryChanged();
	}
	
	@Override
	public void updateEntity() {
		if(modifiers.hasNoTags() && worldObj != null && !worldObj.isRemote){
			destroyBench();
			return;
		}
		if((shouldUpdateOutput || inventoryHandler.shouldUpdate) && !containerInit && !containerWorking){
			inventoryHandler.findRecipe(false);
			shouldUpdateOutput = false;
			inventoryHandler.shouldUpdate = false;
		}
		if(sendRenderPacket && !worldObj.isRemote){
			netHandler.postRenderPacket(0);
			sendRenderPacket = false;
		}
		netHandler.tick();
		if(update <= 5 && update != -1)
			update++;
		if(update >= 5 && worldObj.isRemote){
			FMLClientHandler.instance().getClient().renderGlobal.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
			update = -1;
		}
		if(initialized && upgrades != null && (upgrades.length == 0 || getModifiers() == null || getInventoryModifier() == null)){
			if(getModifiers() == null)
				CSLogger.logError("TIle entity has null upgrades.");
			if(getInventoryModifier() == null)
				CSLogger.logError("Tile entity has null inventory modifier.");
		}
		super.updateEntity();
	}
	
	private void destroyBench() {
		if(inv == null || inv.length == 0)
			return;
		ItemStack[] inventory = new ItemStack[inv.length];
		for(int i = 0; i < inventory.length; i++){
			inventory[i] = inv[i] != null ? inv[i].copy() : null;
		}
		TileEntityChest chest = new TileEntityChest();
		for(int i = 0; i < inventory.length; i++){
			chest.setInventorySlotContents(i, inventory[i]);
		}
		worldObj.setBlock(xCoord, yCoord, zCoord, 0);
		worldObj.setBlock(xCoord, yCoord, zCoord, Block.chest.blockID);
		worldObj.setBlockTileEntity(xCoord, yCoord, zCoord, chest);
	}

	private void makeNewMatrix() {
		for(int i = 0; i < 9; i++){
			lastCraftMatrix.setInventorySlotContents(i, inv[i]);
		}
	}

	private boolean checkDifferences() {
		for(int i = 0; i < 9; i++){
			if(!ItemStack.areItemStacksEqual(lastCraftMatrix.getStackInSlot(i), inv[i]))
				return true;
		}
		return false;
	}

	public void markForUpdate() {
		shouldUpdateOutput = true;
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
		netHandler.onDataPacket(pkt);
		inventoryHandler.inv = inv;
		if(pkt.data.hasKey("id")){
			if(worldObj != null && inv != null)
				inventoryHandler.findRecipe(true);
		}/*else if(getInventoryModifier() == EnumInventoryModifier.TOOLS)
			inventoryHandler.findRecipe(true);*/
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
		if((stack != null && stack.stackSize >= getInventoryStackLimit() && getInventoryModifier() == EnumInventoryModifier.DEEP)){
			int[] i = inventoryHandler.inventoryMap.get("Deep");
			if(i[0] != slot && stack != null && stack.stackSize > getInventoryStackLimit())
				stack.stackSize = getInventoryStackLimit();
		}
		if(slot >= 27 && getInventoryModifier() == EnumInventoryModifier.TOOLS && !worldObj.isRemote){
			sendRenderPacket = true;
			if(slot == 27)
				tools[0] = inv[slot];
			if(slot == 28)
				tools[1] = inv[slot];
			if(slot == 29)
				tools[2] = inv[slot];
			inventoryHandler.tools = tools;
		}
	}

	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		
		try{
			initializeFromNBT(ModificationNBTHelper.getModifierTag(tagCompound));
			handleModifiers();
			
			if(!modifiers.getName().equals(""))
				modifiers.setName("");
			
			if(inv != null){
				NBTTagList tagList = tagCompound.getTagList("Inventory");
				for(int i = 0; i < tagList.tagCount(); i++)
				{
					NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
					byte slot = tag.getByte("Slot");
					if(slot >= 0 && slot < inv.length)
					{
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
						if(slot >= 27){
							if(slot == 27)
								tools[0] = inv[slot];
							if(slot == 28)
								tools[1] = inv[slot];
							if(slot == 29)
								tools[2] = inv[slot];
							if(inventoryHandler != null)
								inventoryHandler.tools = tools;
						}
					}
				}
			}
			if(getInventoryModifier() == EnumInventoryModifier.TOOLS){
				setSelectedToolIndex(tagCompound.getByte("selectedToolIndex"));
			}
			directionFacing = tagCompound.getByte("direction");
		}catch(Exception ex){
			String coords = "" +xCoord +" " +yCoord +" " +zCoord;
			CSLogger.logError("Failed loading a crafting table at " +coords, ex);
		}
	}
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		
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
		if(modifiers.hasKey(ModificationNBTHelper.modifierTag))
			modifiers = (NBTTagCompound) modifiers.getTag(ModificationNBTHelper.modifierTag);
		if(!modifiers.getName().equals(""))
			modifiers.setName(null);
		tagCompound.setTag(ModificationNBTHelper.modifierTag, modifiers);
		tagCompound.setByte("direction", directionFacing);
		if(getInventoryModifier() == EnumInventoryModifier.TOOLS)
			tagCompound.setByte("selectedToolIndex", (byte)selectedToolIndex);
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
				player.getDistanceSq(xCoord +0.5, yCoord +0.5, zCoord +0.5) < 64;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {
		if(!worldObj.isRemote)
			netHandler.postRenderPacket(1);
	}

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

	@Override
	public byte getDirectionFacing() {
		return directionFacing;
	}

	@Override
	public void setDirectionFacing(byte byt) {
		directionFacing = byt;
	}

	@Override
	public byte[] getModifierBytes() {
		return upgrades;
	}

	@Override
	public ItemStack getRenderedResult() {
		return inventoryHandler.result;
	}

	@Override
	public ItemStack[] getInventory() {
		return inv;
	}

	@Override
	public void setRenderedResult(ItemStack stack) {
		result = stack;
	}

	@Override
	public void setTools(ItemStack[] stacks) {
		int i = 0;
		for(ItemStack stack : stacks)
			tools[i++] = stack == null ? stack : stack.copy();
		if(inventoryHandler != null)
			inventoryHandler.tools = tools;
	}

	@Override
	public void setSelectedToolIndex(int i) {
		selectedToolIndex = i;
		inventoryHandler.selectedToolIndex = selectedToolIndex;
		sendRenderPacket = true;
	}

	@Override
	public void setRandomShift(float f) {
		randomShift = f;
	}

	@Override
	public ContainerHandler getContainerHandler() {
		return containerHandler;
	}

	@Override
	public InventoryHandler getInventoryHandler() {
		return inventoryHandler;
	}

	@Override
	public int getPlanIndexInInventory() {
		// TODO Auto-generated method stub
		return inventoryHandler.planIndex;
	}
}
