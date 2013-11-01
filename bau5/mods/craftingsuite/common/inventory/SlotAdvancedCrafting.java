package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class SlotAdvancedCrafting extends SlotCrafting {

	public SlotAdvancedCrafting(EntityPlayer player, IInventory inv, int index, int x,
			int y) {
		super(player, new BasicInventoryCrafting(), new InventoryCraftResult(), index, x, y);
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer par1EntityPlayer,
			ItemStack par2ItemStack) {
		super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
	}

}
