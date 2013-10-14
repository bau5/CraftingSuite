package bau5.mods.craftingsuite.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class ContainerCraftingFrame extends Container {
	
	public EntityCraftingFrame craftingFrame;

	public ContainerCraftingFrame(InventoryPlayer inventory, EntityCraftingFrame ent) {
		
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
