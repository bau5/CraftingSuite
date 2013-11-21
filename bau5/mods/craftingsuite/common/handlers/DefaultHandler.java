package bau5.mods.craftingsuite.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class DefaultHandler implements IModifierHandler {
	@Override
	public ItemStack handleSlotClick(int slot, int clickType, int clickMeta,
			EntityPlayer player) {
		return null;
	}

	@Override
	public ItemStack handleTransferClick(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}

	@Override
	public boolean handleCraftingPiece(ItemStack neededStack, boolean metaSens) {
		return false;
	}

	@Override
	public boolean handlesSlotClicks() {
		return false;
	}

	@Override
	public boolean handlesTransfers() {
		return false;
	}

	@Override
	public boolean handlesCrafting() {
		return true;
	}

	@Override
	public void shiftClickedCraftingSlot() {
		
	}

	@Override
	public boolean handlesThisTransfer(int numSlot, ItemStack stack) {
		return false;
	}
}
