package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotDummy extends Slot {

	public SlotDummy(IInventory inv, int index) {
		super(inv, index, 0, 0);
		
	}
}
