package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import bau5.mods.craftingsuite.common.CSLogger;
import bau5.mods.craftingsuite.common.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ContainerHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.InventoryHandler;
import bau5.mods.craftingsuite.common.tileentity.parthandlers.ModdedTableInfo;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

public class TileEntityModdedTable extends TileEntity implements IModifiedTileEntityProvider{
	
	private byte[] upgrades = null;
	private final InventoryHandler inventoryHandler;
	private final ContainerHandler containerHandler;
	
	private ModdedTableInfo modifications;
	
	private NBTTagCompound modifiers;
	
	private boolean initialized = false;
	public boolean sendRenderPacket = false;
	private byte direcitonFacing = 0;
	private int update = 0;
	
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
 			if(sendRenderPacket){
 				PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 64D, worldObj.provider.dimensionId, getLiteDescription(0));
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
	
	private Packet getLiteDescription(int type) {
		NBTTagCompound tag = new NBTTagCompound();
		if(type == 0){
			tag.setTag("displayedResult", inventoryHandler.result != null ? inventoryHandler.result.writeToNBT(new NBTTagCompound()) : new NBTTagCompound());
			if(getInventoryModifier() == EnumInventoryModifier.TOOLS){
				NBTTagCompound tag2 = new NBTTagCompound();
				for(int i = 0; i < 3; i++){
					tag2 = new NBTTagCompound();
					tag.setTag("tool" +i, inventoryHandler.inv[i +getToolModifierInvIndex()] != null ? inventoryHandler.inv[i +getToolModifierInvIndex()].writeToNBT(tag2) : tag2);
				}
//				tag.setByte("selectedToolIndex", (byte)selectedToolIndex);
			}
		}
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
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
		if(pkt.data.hasKey("displayedResult")){
			inventoryHandler.result = ItemStack.loadItemStackFromNBT(pkt.data.getCompoundTag("displayedResult"));
			return;
		}
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
		
		try{
			initializeFromNBT(ModificationNBTHelper.getModifierTag(tagCompound));
			handleModifiers();
			init();
			inventoryHandler.readInventoryFromNBT(tagCompound);
			if(!modifiers.getName().equals(""))
				modifiers.setName("");
		}catch(Exception ex){
			CSLogger.logError("Failed loading a crafting table. Dropping inventory at " +xCoord +","+yCoord+","+zCoord, ex);
			NBTTagList tagList = tagCompound.getTagList("Inventory");
			if(tagList != null && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
				for(int i = 0; i < tagList.tagCount(); i++)
				{
					ItemStack item = ItemStack.loadItemStackFromNBT((NBTTagCompound)tagList.tagAt(i));
					if(item != null && item.stackSize > 0)
					{
						EntityItem ei = new EntityItem(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld(), xCoord, yCoord, zCoord,
								new ItemStack(item.itemID, item.stackSize, item.getItemDamage()));
						if(item.hasTagCompound())
							ei.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
						float factor = 0.05f;
						FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().spawnEntityInWorld(ei);
						item.stackSize = 0;
					}
				}
			}
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
	
	public ItemStack getPlanksUsed() {
		NBTTagCompound tag = ModificationNBTHelper.getPlanksUsed(modifiers);
		ItemStack stack = ItemStack.loadItemStackFromNBT(ModificationNBTHelper.getPlanksUsed(modifiers));
		return stack;
	}

	@Override
	public NBTTagCompound getModifiers() {
		return modifiers;
	}
}
