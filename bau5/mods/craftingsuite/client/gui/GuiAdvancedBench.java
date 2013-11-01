package bau5.mods.craftingsuite.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import bau5.mods.craftingsuite.common.inventory.ContainerAdvancedBench;
import bau5.mods.craftingsuite.common.tileentity.TileEntityAdvancedBench;

public class GuiAdvancedBench extends GuiCraftingSuiteBase{
	private TileEntityAdvancedBench tileEntity;
	
	public GuiAdvancedBench(InventoryPlayer inventory,
			TileEntityAdvancedBench te) {
		super(new ContainerAdvancedBench(inventory, te));
		tileEntity = te;
		guiTexture = new ResourceLocation("craftingsuite", "textures/gui/pbGUIab.png");
		ySize += 52;
		xSize += 17;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString(I18n.getString("gui.advancedbench.name"), 7, 6, 4210752);
	}
}
