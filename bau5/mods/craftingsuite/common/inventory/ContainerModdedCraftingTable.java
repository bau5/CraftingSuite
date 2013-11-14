package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.handlers.DeepSlotHandler;
import bau5.mods.craftingsuite.common.handlers.ToolsHandler;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;

public class ContainerModdedCraftingTable extends ContainerBase {

	private TileEntityModdedTable tileEntity;
	
	public ContainerModdedCraftingTable(TileEntityModdedTable te, EntityPlayer player) {
		super(te);
		tileEntity = te;
		handleInventoryModifiers(); 
		buildContainerFromTile(player);
		tileEntity.getInventoryHandler().findRecipe(true);
	}

	private void buildContainerFromTile(EntityPlayer player) {
		if(tileEntity.getUpgrades()[0] == 1){
			buildBasicCraftingInventory(player.inventory, tileEntity.getInventoryHandler(), tileEntity.getInventoryHandler().resultMatrix());
		}else{
//			for(Slot slot : tileEntity.containerHandler().getSlots()){
//				this.addSlotToContainer(slot);
//			}
		}
	}
	
	@Override
	public ItemStack slotClick(int slot, int clickType, int clickMeta,
			EntityPlayer player) {
		if(slot == 0 && clickMeta == 6)
			clickMeta = 0;
		ItemStack stack = super.slotClick(slot, clickType, clickMeta, player);
		if(slot == 0){
			tileEntity.getInventoryHandler().findRecipe(false);
		}
		return stack;
	}

	@Override
	protected int[] getXYZ() {
		return new int[]{
				tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord
		};
	}

	@Override
	public EnumInventoryModifier getInventoryModifier() {
		return tileEntity.getInventoryModifier();
	}

	@Override
	public int getSizeInventoryOfTile() {
		return tileEntity.getModifiedInventorySize();
	}

	@Override
	protected void handleInventoryModifiers() {
		switch(getInventoryModifier()){
		case NONE: break;
		case TOOLS: 
			for(int i = 0; i < 3; i++){
				this.addSlotToContainer(new SlotTool(tileEntity.getInventoryHandler(), tileEntity.getToolModifierInvIndex() +i, -17, 17 + (16*i +(i*2))));
			}
			handler = new ToolsHandler(this);
			break;
		case DEEP:
			SlotDeep slot = new SlotDeep(tileEntity.getInventoryHandler(), 9, -17, 34);
			this.addSlotToContainer(slot);
			handler = new DeepSlotHandler(this, slot);
			break;
		}		
	}

	@Override
	public IModifiedTileEntityProvider getTileEntity() {
		return (IModifiedTileEntityProvider)tileEntity;
	}
}
