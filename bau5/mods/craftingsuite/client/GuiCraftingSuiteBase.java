package bau5.mods.craftingsuite.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.inventory.ContainerBase;

public class GuiCraftingSuiteBase extends GuiContainer{
	protected ResourceLocation guiTexture;
	protected GuiHandler guiHandler;
	public GuiCraftingSuiteBase(ContainerBase container) {
		super(container);
		setGuiTexture();
		guiHandler = new GuiHandler(this, container.getTileEntity());
	}

	protected void setGuiTexture(){
		guiTexture = new ResourceLocation("textures/gui/container/crafting_table.png");
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString(I18n.getString("container.crafting"), 28, 6, 4210752);
        this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(guiTexture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        guiHandler.drawAdditionalParts(k, l, this.xSize, this.ySize);
	}
}
