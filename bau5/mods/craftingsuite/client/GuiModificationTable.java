package bau5.mods.craftingsuite.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.inventory.ContainerModificationTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;

public class GuiModificationTable extends GuiContainer{
	
	private ResourceLocation guiTexture;
	private TileEntityModificationTable tileEntity;

	public GuiModificationTable(TileEntityModificationTable tile, EntityPlayer player) {
		super(new ContainerModificationTable(tile, player));
		tileEntity = tile;
		guiTexture = new ResourceLocation("craftingsuite", "textures/gui/modtable.png");
		ySize = 256;
		xSize = 256;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		//TODO Localization
		fontRenderer.drawString("Modification Table", 8, 6, 30000800);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int x = (width-xSize)/2;
		int y = (height-ySize)/2;
		mc.getTextureManager().bindTexture(guiTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		if(tileEntity.getResult() != null)
			displayResult(tileEntity.getResult(), x, y);
	}

	private void displayResult(ItemStack result, int x, int y) {
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        float prevZ = itemRenderer.zLevel;
        itemRenderer.zLevel = 300.0F;
    	GL11.glPushMatrix();
        FontRenderer font = result.getItem().getFontRenderer(result);
        if (font == null) font = fontRenderer;
        itemRenderer.renderItemAndEffectIntoGUI(font, this.mc.renderEngine, result, x +195, y +46);
        itemRenderer.renderItemOverlayIntoGUI( font, this.mc.renderEngine, result,  x +195, y +46, null);
        GL11.glPopMatrix();
        this.zLevel = 0.0F;
        itemRenderer.zLevel = prevZ;
	}
}
