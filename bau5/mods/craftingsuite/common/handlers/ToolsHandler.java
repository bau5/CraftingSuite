package bau5.mods.craftingsuite.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.inventory.ContainerBase;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;

public class ToolsHandler implements IModifierHandler{
	
	public ContainerBase container;
	public ToolsHandler(ContainerBase cont) {
		container = cont;
	}

	@Override
	public ItemStack handleSlotClick(int slot, int clickType, int clickMeta,
			EntityPlayer player) {
		if(container.getInventoryModifier() == EnumInventoryModifier.TOOLS && slot >= 46 && slot <= 48){
			IModifiedTileEntityProvider te = container.modifiedTile;
 			int index = slot - 46;
			
			if(clickType == 0 && clickMeta == 0 && te.getInventoryHandler().tools[index] != null){
				te.setSelectedToolIndex(index);
				te.getInventoryHandler().findRecipe(false);
				return null;
			}
			te.setSelectedToolIndex(-1);
			te.getInventoryHandler().findRecipe(false);
		}
		return null;
	}

	@Override
	public ItemStack handleTransferClick(EntityPlayer par1EntityPlayer, int par2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean handlesSlotClicks() {
		// TODO Auto-generated method stub
		return false;
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
