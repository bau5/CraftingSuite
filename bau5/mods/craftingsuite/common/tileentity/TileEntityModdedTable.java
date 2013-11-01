package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import bau5.mods.craftingsuite.common.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ContainerHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.InventoryHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ModdedTableInfo;

public class TileEntityModdedTable extends TileEntity implements IModifiedTileEntityProvider{
	
	private byte[] upgrades = null;
	private final InventoryHandler inventoryHandler;
	private final ContainerHandler containerHandler;
	
	private ModdedTableInfo modifications;
	
	private NBTTagCompound modifiers;
	
	private boolean initialized = false;
	private byte direcitonFacing = 0;
	
	public TileEntityModdedTable(){
		inventoryHandler = new InventoryHandler(this);
		containerHandler = new ContainerHandler(this);
	}
	
	@Override
	public void updateEntity() {
		if(!(inventoryHandler == null || containerHandler == null)){
 			if(inventoryHandler().shouldUpdate && !containerHandler().isContainerInit() && !containerHandler().isContainerWorking()){
				inventoryHandler().findRecipe(false);
				inventoryHandler().shouldUpdate = false;
			}
		}
		super.updateEntity();
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		Packet132TileEntityData packet = new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
		return packet;
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
		initializeFromNBT(ModificationNBTHelper.getModifierTag(pkt.data));
		handleModifiers();
		super.onDataPacket(net, pkt);
	}

	@Override
	public void onInventoryChanged() {
		inventoryHandler.onTileInventoryChanged();
		super.onInventoryChanged();
	}
	
	public InventoryHandler inventoryHandler(){
		return inventoryHandler;
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
			inventoryHandler().initInventory();
			initialized = true;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		inventoryHandler.readInventoryFromNBT(tagCompound);
		initializeFromNBT(ModificationNBTHelper.getModifierTag(tagCompound));
		handleModifiers();
		init();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		inventoryHandler.writeInventoryToNBT(tagCompound);
		
		NBTTagList modInfoList = new NBTTagList();
		for(int i = 0; i < 1; i++){
			if(i == 0){
				NBTTagByteArray bytes =  new NBTTagByteArray(ModificationNBTHelper.upgradeArrayName);
				if(upgrades != null){
					bytes.byteArray = upgrades;
				}else
					bytes.byteArray = new byte[0];
				modInfoList.appendTag(bytes);
			}
		}
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
		switch(upgrades[1]){
		case 1: return EnumInventoryModifier.TOOLS;
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
}
