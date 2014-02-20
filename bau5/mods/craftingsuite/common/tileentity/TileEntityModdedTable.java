package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import bau5.mods.craftingsuite.common.CSLogger;
import bau5.mods.craftingsuite.common.helpers.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ContainerHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.InventoryHandler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileEntityModdedTable extends TileEntityBase implements IModifiedTileEntityProvider{
	
	private final InventoryHandler inventoryHandler;
	private final ContainerHandler containerHandler;
	private final TileNetHandler 	   netHandler;
	
	private boolean initialized = false;
	public boolean sendRenderPacket = false;
	private byte directionFacing = 0;
	private int update = 0;
	public float randomShift = 0.0F;
	
	private ItemStack planksUsed = null;
	
	public TileEntityModdedTable(){
		inventoryHandler = new InventoryHandler(this);
		containerHandler = new ContainerHandler(this);
		netHandler		 = new TileNetHandler(this);
	}
	
	@Override
	public void updateEntity() {
		if(!(inventoryHandler == null || containerHandler == null)){
 			if(inventoryHandler.shouldUpdate && !containerHandler().isContainerInit() && !containerHandler().isContainerWorking()){
				inventoryHandler.findRecipe(false);
				inventoryHandler.shouldUpdate = false;
			}
 			if(sendRenderPacket && !worldObj.isRemote){
 				netHandler.postRenderPacket(0);
 				sendRenderPacket = false;
 			}
		}
		if(update  <= 5 && update != -1)
			update++;
		if(update >= 5 && worldObj.isRemote){
			FMLClientHandler.instance().getClient().renderGlobal.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
			update = -1;
		}
		super.updateEntity();
	}

	@Override
	public Packet getDescriptionPacket() {
		if(inventoryHandler.inv == null && !modifications.isInitialized()){
			init();
		}
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		Packet132TileEntityData packet = new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
		return packet;
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		super.onDataPacket(net, pkt);
		initializeFromNBT(pkt.data);
		handleModifiers();
		netHandler.onDataPacket(pkt);
		if(!inventoryHandler.checkValidity())
			return;
		if(worldObj != null)
			inventoryHandler.findRecipe(true);
	}

	@Override
	public void onInventoryChanged() {
		inventoryHandler.onTileInventoryChanged();
		super.onInventoryChanged();
	}
	public ContainerHandler containerHandler(){
		return containerHandler;
	}
	
	
	public void init(){
		if(!initialized){
			inventoryHandler.initInventory();
			initialized = true;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		
		try{
			directionFacing = tagCompound.getByte("direction");
			blockMetadata = tagCompound.getInteger("blockMetadata");
			initializeFromNBT(tagCompound);
			handleModifiers();
			init();
			inventoryHandler.readInventoryFromNBT(tagCompound);
		}catch(Exception ex){
			CSLogger.logError("Failed loading a crafting table.");
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		inventoryHandler.writeInventoryToNBT(tagCompound);
		tagCompound.setByte("direction", getDirectionFacing());
		tagCompound.setByteArray("UpgradeArray", modifications.buildUpgradeArray());
		if(modifications.getPlanks() != null)
			tagCompound.setTag("Planks", planksUsed.writeToNBT(new NBTTagCompound()));
		if(getInventoryModifier() == EnumInventoryModifier.TOOLS)
			tagCompound.setByte("selectedToolIndex", (byte)inventoryHandler.selectedToolIndex);
	}

	@Override
	public int getModifiedInventorySize() {
		return getBaseInventorySize() + getInventoryModifier().getNumSlots();
	}
	@Override
	public EnumInventoryModifier getInventoryModifier() {
		switch(modifications.upgrades()){
		case 3: return EnumInventoryModifier.TOOLS;
		case 4: return EnumInventoryModifier.DEEP;
		default:return EnumInventoryModifier.NONE;
		}
	}

	@Override
	public int getBaseInventorySize() {
		return 9;
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
		initialized = true;
	}

	@Override
	public void initializeFromMods(Modifications mods) {
		this.planksUsed = mods.getPlanks().copy();
		this.modifications = mods;
	}

	@Override
	public void handleModifiers() {
		inventoryHandler.inv = new ItemStack[getModifiedInventorySize()];
	}

	@Override
	public int getToolModifierInvIndex() {
		return (getInventoryModifier() == EnumInventoryModifier.TOOLS) ? 9 : -1;
	}

	@Override
	public byte getDirectionFacing() {
		return directionFacing;
	}

	@Override
	public void setDirectionFacing(byte byt) {
		directionFacing = byt;
	}
	
	public ItemStack getPlanksUsed() {
		return modifications.getPlanks();
	}

	@Override
	public ItemStack[] getInventory() {
		return inventoryHandler.inv;
	}

	@Override
	public ItemStack getRenderedResult() {
		return inventoryHandler.result;
	}

	@Override
	public int getSelectedToolIndex() {
		return inventoryHandler.selectedToolIndex;
	}

	@Override
	public void setRenderedResult(ItemStack stack) {
		inventoryHandler.result = stack;
	}

	@Override
	public void setTools(ItemStack[] stacks) {
		int i = 0;
		for(ItemStack stack : stacks)
			inventoryHandler.tools[i++] = stack == null ? stack : stack.copy();
	}

	@Override
	public void setSelectedToolIndex(int i) {
		inventoryHandler.selectedToolIndex = i;
	}

	@Override
	public void setRandomShift(float f) {
		randomShift = f;
	}
	
	@Override
	public ItemStack getSelectedTool(){
		if(getInventoryModifier() != EnumInventoryModifier.TOOLS)
			return null;
		if(inventoryHandler.selectedToolIndex + getToolModifierInvIndex() < inventoryHandler.inv.length)
			return inventoryHandler.inv[inventoryHandler.selectedToolIndex + getToolModifierInvIndex()];
		else
			return new ItemStack(Block.stone);
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

	@Override
	public Modifications getModifications() {
		return modifications;
	}
}
