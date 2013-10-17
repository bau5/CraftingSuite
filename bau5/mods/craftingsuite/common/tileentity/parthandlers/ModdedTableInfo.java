package bau5.mods.craftingsuite.common.tileentity.parthandlers;

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
			inventorySize = 9;
			holdsCrafting = true;
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
