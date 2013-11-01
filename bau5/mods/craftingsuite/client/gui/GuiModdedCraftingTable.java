package bau5.mods.craftingsuite.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import bau5.mods.craftingsuite.common.inventory.ContainerModdedCraftingTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;

public class GuiModdedCraftingTable extends GuiCraftingSuiteBase{

	public GuiModdedCraftingTable(TileEntityModdedTable te, EntityPlayer player) {
		super(new ContainerModdedCraftingTable(te, player));
	}
}
