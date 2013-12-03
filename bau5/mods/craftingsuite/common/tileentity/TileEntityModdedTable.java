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
import bau5.mods.craftingsuite.common.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ContainerHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.InventoryHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ModdedTableInfo;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileEntityModdedTable extends TileEntityBase implements IModifiedTileEntityProvider{
	
	private byte[] upgrades = null;
	private final InventoryHandler inventoryHandler;
	private final ContainerHandler containerHandler;
	private final TileNetHandler 	   netHandler;
	
	private ModdedTableInfo modifications;
	
	private NBTTagCompound modifiers;
	
	private boolean initialized = false;
	public boolean sendRenderPacket = false;
	private byte direcitonFacing = 0;
	private int update = 0;
	public float randomShift = 0.0F;
	
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
		if(inventoryHandler.inv == null && modifiers != null){
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
		initializeFromNBT(ModificationNBTHelper.getModifierTag(pkt.data));
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
	
	public ModdedTableInfo modifications(){
		return modifications;
	}
	
	public void init(){
		if(!initialized){
			modifications = new ModdedTableInfo(upgrades);
			inventoryHandler.initInventory();
			initialized = true;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		
		try{
			initializeFromNBT(ModificationNBTHelper.getModifierTag(tagCompound));
			handleModifiers();
			init();
			inventoryHandler.readInventoryFromNBT(tagCompound);
			if(modifiers != null && !modifiers.getName().equals(""))
				modifiers.setName("");
		}catch(Exception ex){
			CSLogger.logError("Failed loading a crafting table.");
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		inventoryHandler.writeInventoryToNBT(tagCompound);
		
		if(modifiers.hasKey(ModificationNBTHelper.modifierTag))
			modifiers = (NBTTagCompound) modifiers.getTag(ModificationNBTHelper.modifierTag);
		if(!modifiers.getName().equals(""))
			modifiers.setName(null);
		tagCompound.setTag(ModificationNBTHelper.modifierTag, modifiers);
	}

	public byte[] getUpgrades() {
		return upgrades;
	}
	
	@Override
	public int getModifiedInventorySize() {
		return getBaseInventorySize() + getInventoryModifier().getNumSlots();
	}
	@Override
	public EnumInventoryModifier getInventoryModifier() {
		if(upgrades == null || upgrades.length == 0)
			return EnumInventoryModifier.NONE;
		switch(upgrades[1]){
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
	public void initializeFromNBT(NBTTagCompound theModifierTag) {
		modifiers = theModifierTag;
		upgrades  = ModificationNBTHelper.getUpgradeByteArray(modifiers);
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
		return direcitonFacing;
	}

	@Override
	public void setDirectionFacing(byte byt) {
		direcitonFacing = byt;
	}
	
	public ItemStack getPlanksUsed() {
		NBTTagCompound tag = ModificationNBTHelper.getPlanksUsed(modifiers);
		ItemStack stack = ItemStack.loadItemStackFromNBT(ModificationNBTHelper.getPlanksUsed(modifiers));
		return stack;
	}

	@Override
	public NBTTagCompound getModifiers() {
		return modifiers;
	}

	@Override
	public byte[] getModifierBytes() {
		return upgrades;
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
	
}
