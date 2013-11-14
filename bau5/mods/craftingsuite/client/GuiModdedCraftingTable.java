package bau5.mods.craftingsuite.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import bau5.mods.craftingsuite.common.inventory.ContainerModdedCraftingTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;

public class GuiModdedCraftingTable extends GuiCraftingSuiteBase implements IGuiBridge{

	public GuiModdedCraftingTable(TileEntityModdedTable te, EntityPlayer player) {
		super(new ContainerModdedCraftingTable(te, player));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		super.drawGuiContainerBackgroundLayer(f, i, j);
		guiHandler.drawAdditionalParts(i, j, xSize, ySize);
	}

	@Override
	public void setZLevel(float f) {
		this.zLevel = f;
	}

	@Override
	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}

	@Override
	public RenderItem getItemRenderer() {
		return this.itemRenderer;
	}

	@Override
	public Minecraft getMinecraft() {
		return this.mc;
	}
}
