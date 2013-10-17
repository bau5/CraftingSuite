package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;

public class BasicInventoryCrafting extends InventoryCrafting {
	public BasicInventoryCrafting() {
		super(new Container(){
			@Override
			public boolean canInteractWith(EntityPlayer entityplayer) {
				return true;
			}
		}, 3, 3);
	}
	public BasicInventoryCrafting(Container theContainer){
		super(theContainer, 3, 3);
	}
}
