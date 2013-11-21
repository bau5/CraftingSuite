package bau5.mods.craftingsuite.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IModifierHandler {
	public ItemStack handleSlotClick(int slot, int clickType, int clickMeta, EntityPlayer player);
	public ItemStack handleTransferClick(EntityPlayer par1EntityPlayer, int par2);
	
	public boolean handleCraftingPiece(ItemStack neededStack, boolean metaSens);
	
	public boolean handlesSlotClicks();
	public boolean handlesTransfers();
	public boolean handlesCrafting();
	public void shiftClickedCraftingSlot();
	public boolean handlesThisTransfer(int numSlot, ItemStack stack);
}
