package bau5.mods.craftingsuite.common.tileentity;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileNetHandler {
	private IModifiedTileEntityProvider tile;
	
	private Packet nextRenderPacket;
	private Packet nextDescriptionPacket;
	
	public TileNetHandler(IModifiedTileEntityProvider te){
		tile = te;
	}
	
	public void onDataPacket(Packet132TileEntityData packet){
		if(packet.data.hasKey("displayedResult")){
			NBTTagCompound tag = packet.data.getCompoundTag("displayedResult");
			if(tag.hasKey("displayedResult"))
				tag = packet.data.getCompoundTag("displayedResult");
//			tile.setRenderedResult(ItemStack.loadItemStackFromNBT(tag));
			tile.getInventoryHandler().result = ItemStack.loadItemStackFromNBT(tag);
			if(tile.getInventoryModifier() == EnumInventoryModifier.TOOLS){
				ItemStack[] tools = new ItemStack[3];
				for(int i = 0; i < 3; i++){
					tools[i] = ItemStack.loadItemStackFromNBT(packet.data.getCompoundTag("tool" +i));
				}
				tile.setTools(tools);
				tile.setSelectedToolIndex(packet.data.getByte("selectedToolIndex"));
			}
			return;
		}
		if(packet.data.hasKey("randomShift")){
			tile.setRandomShift(packet.data.getFloat("randomShift"));
			return;
		}
		TileEntity te = (TileEntity)tile;
		te.readFromNBT(packet.data);
	}
	
	public Packet getDescriptionPacket(){
	
		return null;
	}
	
	public Packet getInformationPacket(int type){
		
		return null;
	}
	
	public void tick(){
		if(nextRenderPacket != null){
			TileEntity te = (TileEntity)tile;
			PacketDispatcher.sendPacketToAllAround(te.xCoord, te.yCoord, te.zCoord, 64D, te.worldObj.provider.dimensionId, nextRenderPacket);
			nextRenderPacket = null;
		}
	}
	
/*	public void markForRenderPacket(){
		postRenderPacket = true;
	}*/
	
	public Packet getRenderPacket(int type){
		NBTTagCompound tag = new NBTTagCompound();
		TileEntity te = (TileEntity)tile;
		if(type == 0){
			ItemStack result = tile.getRenderedResult();
			tag.setTag("displayedResult", result != null ? result.writeToNBT(new NBTTagCompound()) : new NBTTagCompound());
			if(tile.getInventoryModifier() == EnumInventoryModifier.TOOLS){
				NBTTagCompound tag2 = new NBTTagCompound();
				for(int i = 0; i < 3; i++){
					tag2 = new NBTTagCompound();
					tag.setTag("tool" +i, tile.getInventory()[i +tile.getToolModifierInvIndex()] != null ? tile.getInventory()[i +tile.getToolModifierInvIndex()].writeToNBT(tag2) : tag2);
				}
				tag.setByte("selectedToolIndex", (byte)tile.getSelectedToolIndex());
			}
		}
		if(type == 1){
			tag.setFloat("randomShift", new Random().nextFloat()/100);
		}
		return new Packet132TileEntityData(te.xCoord, te.yCoord, te.zCoord, 1, tag);
	}
	
	public void postRenderPacket(int type){
		nextRenderPacket = getRenderPacket(type);
	}
}
