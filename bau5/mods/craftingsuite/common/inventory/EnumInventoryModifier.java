package bau5.mods.craftingsuite.common.inventory;

public enum EnumInventoryModifier {
	NONE(0),
	TOOLS(3);
	
	int extraSize;
	
	EnumInventoryModifier(int size){
		extraSize = size;
	}
	
	public int getNumSlots() {
		return extraSize;
	}
}
