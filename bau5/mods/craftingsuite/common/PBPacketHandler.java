package bau5.mods.craftingsuite.common;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import bau5.mods.craftingsuite.common.handlers.PlanHandler;
import bau5.mods.craftingsuite.common.inventory.ContainerBase;
import bau5.mods.craftingsuite.common.inventory.ContainerModificationTable;
import bau5.mods.craftingsuite.common.inventory.ContainerProjectBench;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PBPacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if(packet.data.length == 1){
			handleTinyPacket(manager, packet, player);
			return;
		}
	}

	private void handleTinyPacket(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		switch(packet.data[0]){
		case 0: if(player instanceof EntityPlayerMP)completeEmptyOfMatrix((EntityPlayerMP)player);
				else System.out.println("Failed emptying matrix, wrong player type.");
			break;
		case 1: ((ContainerModificationTable)((EntityPlayerMP)player).openContainer).tileEntity.craftRecipe();
			break;
		case 2: ((PlanHandler)((ContainerBase)((EntityPlayerMP)player).openContainer).handler).writePlanToStack();
			break;
		}
	}
	
	public static void completeEmptyOfMatrix(EntityPlayerMP thePlayer){
		if(!(thePlayer.openContainer instanceof ContainerProjectBench))
			return;
		ArrayList itemListToSend = new ArrayList();
        ((ContainerProjectBench)thePlayer.openContainer).tileEntity.containerInit = true;
        for(int i = 0; i < 9; i++){
        	thePlayer.openContainer.transferStackInSlot(thePlayer, i + 1);
        }
        ((ContainerProjectBench)thePlayer.openContainer).tileEntity.containerInit = false;
        for (int i = 0; i < thePlayer.openContainer.inventorySlots.size(); ++i) {
            itemListToSend.add(((Slot) thePlayer.openContainer.inventorySlots.get(i)).getStack());
        }

        thePlayer.sendContainerAndContentsToPlayer(thePlayer.openContainer, itemListToSend);
        ((ContainerBase)thePlayer.openContainer).getTileEntity().getInventoryHandler().findRecipe(false);
	}
}
