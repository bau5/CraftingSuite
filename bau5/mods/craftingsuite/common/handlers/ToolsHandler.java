package bau5.mods.craftingsuite.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.inventory.ContainerBase;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.inventory.SlotTool;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;

public class ToolsHandler implements IModifierHandler{
	
	public ContainerBase container;
	public SlotTool[] toolSlots;
	public ToolsHandler(ContainerBase cont, SlotTool[] slots) {
		container = cont;
		toolSlots = slots;
	}

	@Override
	public ItemStack handleSlotClick(int slot, int clickType, int clickMeta,
			EntityPlayer player) {
		if(container.getInventoryModifier() == EnumInventoryModifier.TOOLS && slot >= toolSlots[0].slotNumber && slot <= toolSlots[2].slotNumber){
			IModifiedTileEntityProvider te = container.modifiedTile;
 			int index = slot - 64;
			
			if(clickType == 0 && clickMeta == 0 && te.getInventoryHandler().tools[index] != null){
				if(te.getSelectedToolIndex() == index)
					te.setSelectedToolIndex(-1);
				else
					te.setSelectedToolIndex(index);
				te.getInventoryHandler().findRecipe(true);
				return null;
			}
			te.setSelectedToolIndex(-1);
			te.getInventoryHandler().findRecipe(true);
		}
		return container.slotClick_plain(slot, clickType, clickMeta, player);
	}

	@Override
	public ItemStack handleTransferClick(EntityPlayer par1EntityPlayer, int par2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean handlesSlotClicks() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean handlesTransfers() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handlesCrafting() {
		return true;
	}

	@Override
	public boolean handleCraftingPiece(ItemStack neededStack, boolean metaSens) {
		// TODO Auto-generated method stub
		return false;
	}

}
