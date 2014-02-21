package bau5.mods.craftingsuite.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import bau5.mods.craftingsuite.client.ParticleRenderer;
import bau5.mods.craftingsuite.common.CSLogger;
import bau5.mods.craftingsuite.common.CraftingSuite;
import bau5.mods.craftingsuite.common.helpers.ItemHelper;
import bau5.mods.craftingsuite.common.helpers.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ContainerHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.InventoryHandler;
import cpw.mods.fml.client.FMLClientHandler;

public class TileEntityProjectBench extends TileEntityBase implements IFluidHandler, IModifiedTileEntityProvider, IInventory, ISidedInventory{
	
	public TileNetHandler netHandler;
	public InventoryHandler inventoryHandler;
	public ContainerHandler containerHandler;
	
    private FluidTank tank = new FluidTankExt(FluidContainerRegistry.BUCKET_VOLUME, this);
	
	public Random random = new Random();
	
	public boolean cmas = CraftingSuite.cmas;
	
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
	public class PositionedFluidStack{
		public final FluidStack fluid;
		public final ItemStack full;
		public final ItemStack empty;
		public final int slotNumber;
		private boolean inUse;
		public PositionedFluidStack(int slotNum, FluidStack fl, ItemStack f, ItemStack e){
			fluid = fl;
			slotNumber = slotNum;
			full = f.copy();
			empty = e.copy();
			inUse = false;
		}
		public boolean isInUse(){
			return inUse;
		}
		public void setInUse(){
			inUse = true;
		}
		public void resetInUse(){
			inUse = false;
		}
	}
	public ArrayList<PositionedFluidStack> fluidForCrafting = new ArrayList();
	
	public ItemStack[] inv = null;
	public ItemStack result;
	public ItemStack[] tools = new ItemStack[3];
	protected ItemStack planksUsed = null;
	public int selectedToolIndex  = -1;
	public int toolIndexInCrafting= -1;
	private ItemStack lastResult = null;
	public IInventory craftingResult = new InventoryCraftResult();
	public LocalInventoryCrafting craftingMatrix = new LocalInventoryCrafting(this);
	public boolean containerInit = false;
	public boolean containerWorking = false;
	private boolean shouldUpdateOutput;
	private int update = 0;
	private long ticker = 0;
	private byte directionFacing = 0;
	public float randomShift = 0.0F;
	
	private HashMap<EnumInventoryModifier, int[]> inventoryMap = new HashMap();
	
	/**
	 * Unlinked crafting matrix, used to test the difference between inventory changes,
	 * avoid spam of searching the recipe list.
	 */
	private LocalInventoryCrafting lastCraftMatrix = new LocalInventoryCrafting();
	
	public boolean updateMeta = false;

	public TileEntityProjectBench() {
		netHandler = new TileNetHandler(this);
		containerHandler = new ContainerHandler();
		inventoryHandler = new InventoryHandler(this);
	}

	@Override
	public void initializeFromNBT(NBTTagCompound nbtTagCompound) {
		Modifications mods;
		if(nbtTagCompound.hasKey(ModificationNBTHelper.modifierTag) || nbtTagCompound.getName().equals(ModificationNBTHelper.modifierTag)){
			NBTTagCompound tag = (NBTTagCompound)nbtTagCompound.getTag(ModificationNBTHelper.modifierTag);
			byte[] bytes = tag.getByteArray(ModificationNBTHelper.upgradeArrayName);
			mods = new Modifications(ItemStack.loadItemStackFromNBT(tag.getCompoundTag(ModificationNBTHelper.planksName)), bytes);
		}else{
			byte[] bytes = nbtTagCompound.getByteArray("UpgradeArray");
			mods = new Modifications(ItemStack.loadItemStackFromNBT(nbtTagCompound.getCompoundTag("Planks")), bytes);
		}
		initializeFromMods(mods);
	}

	@Override
	public void initializeFromMods(Modifications mods) {
		this.planksUsed = mods.getPlanks().copy();
		this.modifications = mods;
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
		switch(modifications.upgrades()){
		case 3: return EnumInventoryModifier.TOOLS;
		case 4: return EnumInventoryModifier.DEEP;
		case 5: return EnumInventoryModifier.PLAN;
		}
		return EnumInventoryModifier.NONE;
	}

	@Override
	public EnumExtraModifier getExtraModifier() {
		switch(modifications.extraModifier()){
		case 1: return EnumExtraModifier.FLUID;
		}
		return EnumExtraModifier.NONE;
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
			break;
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

	public ItemStack getPlanksUsed() {
		return modifications.getPlanks();
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
		inventoryHandler.onInventoryChanged();
		super.onInventoryChanged();
	}
	
	@Override
	public void updateEntity() {
		ticker++;
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
		if(cmas){
			if(worldObj.isRemote && ticker % 3 > 0){
				ParticleRenderer.doParticle(0, worldObj, xCoord +random.nextDouble(), yCoord+2, zCoord +random.nextDouble());
			}
		}
		if(ticker > 20000)
			ticker = 0;
		super.updateEntity();
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
			initializeFromNBT(tagCompound);
			
			handleModifiers();
			
	        tank.readFromNBT(tagCompound);
			
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

        tank.writeToNBT(tagCompound);
		
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
		tagCompound.setByte("direction", directionFacing);
		tagCompound.setTag("Planks", planksUsed.writeToNBT(new NBTTagCompound()));
		tagCompound.setByteArray("UpgradeArray", modifications.buildUpgradeArray());
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
		return inventoryHandler.planIndex;
	}

	public void setPlanksUsed(ItemStack planks) {
		planksUsed = planks.copy();
	}

	@Override
	public Modifications getModifications() {
		return modifications;
	}
	
	/*
	 * 	Fluid Handler
	 */
	
	public boolean hasFluidCapabilities(){
		return modifications.hasFluidCapabilities();
	}

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (resource == null || !resource.isFluidEqual(tank.getFluid()))
        {
            return null;
        }
    	FluidStack fstack = tank.drain(resource.amount, doDrain);
    	onInventoryChanged();
        return fstack;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
    	FluidStack fstack = tank.drain(maxDrain, doDrain);
    	onInventoryChanged();
        return fstack;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[] { tank.getInfo() };
    }
    
    public FluidStack getFluidInTank(ForgeDirection from){
    	return tank.getFluid() != null ? tank.getFluid().copy() : null;
    }
}
