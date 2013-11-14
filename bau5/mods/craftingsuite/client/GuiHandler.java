package bau5.mods.craftingsuite.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;

public class GuiHandler {
	private IModifiedTileEntityProvider tile;
	private GuiContainer gui;
	private IGuiBridge 	 guiBridge;
	private ResourceLocation partsResource;
	
	public GuiHandler(GuiContainer parentGui, IModifiedTileEntityProvider te){
		tile = te;
		gui = parentGui;
		guiBridge = (IGuiBridge)gui;
		partsResource = new ResourceLocation("craftingsuite", "textures/gui/parts.png");
	}
	
	public void drawAdditionalParts(int i, int j, int xSize, int ySize){
		if(tile == null)
			return;
		guiBridge.getMinecraft().getTextureManager().bindTexture(partsResource);
		RenderItem itemRenderer = guiBridge.getItemRenderer();
		int x = (gui.width-xSize)/2;
		int y = (gui.height-ySize)/2;
		if(tile.getInventoryModifier() == EnumInventoryModifier.TOOLS){
			gui.drawTexturedModalRect(x-21, y+10, 0, 0, 24, 66);
			int index = tile.getSelectedToolIndex();
			if(index != -1){
				guiBridge.setZLevel(700F);
				gui.drawTexturedModalRect(x-21, y+13 + (index * 18), 0, 66, 24, 24);
				guiBridge.setZLevel(0.0F);
				if(tile.getSelectedTool() == null)
					return;
				ItemStack stack = tile.getSelectedTool();
				if(stack == null)
					return;
				
				GL11.glTranslatef(0.0F, 0.0F, 32.0F);        
				GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(768, 1);
		        GL11.glColor4d(1.0F, 1.0F, 1.0F, 0.9F);
		        
		        guiBridge.setZLevel(200.0F);
		        itemRenderer.zLevel = 200.0F;
		        FontRenderer font = null;
		        if (stack != null) font = stack.getItem().getFontRenderer(stack);
		        if (font == null) font = guiBridge.getFontRenderer();
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		        itemRenderer.renderItemAndEffectIntoGUI(font, guiBridge.getMinecraft().getTextureManager(), stack, x+48, y+35);
//		        itemRenderer.renderItemOverlayIntoGUI(font, guiBridge.getMinecraft().getTextureManager(), stack, x+48, y+35, null);
		        guiBridge.setZLevel(0.0F);
		        itemRenderer.zLevel = 0.0F;
		        
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		        GL11.glDisable(GL11.GL_BLEND);
		        GL11.glEnable(GL11.GL_LIGHTING);
			}
		}
	}
}
