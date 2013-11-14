package bau5.mods.craftingsuite.common.tileentity.parthandlers;

import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;

public class ModdedTableInfo {
	private byte[] upgrades = null;
	private int[] craftingStartStop = new int[2];
	private int[] chestStartStop = new int[2];
	private boolean holdsCrafting;
	private boolean hasChest;
	private boolean chestSupplies;
	private int inventorySize;
	
	public ModdedTableInfo(byte[] bytes){
		upgrades = bytes;
		init();
	}
	
	private void init() {
		byte first = upgrades[0];
		if(first == 1){
			craftingStartStop[0] = 0;
			craftingStartStop[1] = 8;
			hasChest = chestSupplies = false;
			holdsCrafting = true;
			computerInventorySize();
		}
	}
	
	private void computerInventorySize(){
		switch(upgrades[1]){
		case 3: inventorySize = 9 + EnumInventoryModifier.TOOLS.getNumSlots();
			break;
		case 4: inventorySize = 9 + EnumInventoryModifier.DEEP.getNumSlots();
			break;
		default: inventorySize = 9;
			break;
		}
	}
	
	public int getSizeInventory() {
		return inventorySize;
	}

	public int[] getCrafingRange() {
		return craftingStartStop;
	}
	
	public boolean getHoldsCrafting(){
		return holdsCrafting;
	}
	
	public boolean getHasChest(){
		return hasChest;
	}
	
	public boolean getChestSupplies(){
		return chestSupplies;
	}
}
