package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import bau5.mods.craftingsuite.common.EntityCraftingFrame;

public class ContainerCraftingFrame extends ContainerBase {
	
	public EntityCraftingFrame craftingFrame;

	public ContainerCraftingFrame(InventoryPlayer invPlayer, EntityCraftingFrame ent) {
		craftingFrame = ent;
		buildBasicCraftingInventory(invPlayer, craftingFrame.craftingMatrix, craftingFrame.craftingResult);
	}

	@Override
	protected int[] getXYZ() {
		return new int[]{
			(int) craftingFrame.posX, (int) craftingFrame.posY, (int) craftingFrame.posZ
		};
	}
}
