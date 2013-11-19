package bau5.mods.craftingsuite.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.inventory.ContainerProjectBench;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiProjectBench extends GuiContainer implements IGuiBridge{
	private ResourceLocation resource;
	private ResourceLocation parts;
	private GuiHandler		 guiHandler;
	private boolean once = true;
	public GuiProjectBench(InventoryPlayer inventory, TileEntity te) {
		super(new ContainerProjectBench(inventory, (TileEntityProjectBench)te));
		ySize += 48;
		resource = new ResourceLocation("craftingsuite", "textures/gui/pbGUI.png");
		parts    = new ResourceLocation("craftingsuite", "textures/gui/parts.png");
		guiHandler = new GuiHandler(this, (IModifiedTileEntityProvider)te);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString(StatCollector.translateToLocal("gui.projectbench.name"), 8, 6, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int x = (width-xSize)/2;
		int y = (height-ySize)/2;
		if(once){
			guiHandler.makeButtons();
			once = false;
		}
		if(buttonList.size() != guiHandler.buttons.size()){
			if(!buttonList.containsAll(guiHandler.buttons)){
				buttonList.addAll(guiHandler.buttons);
			}
		}
		
		mc.getTextureManager().bindTexture(resource);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		guiHandler.drawAdditionalParts(i, j, xSize, ySize);
	}
	
	@Override
	public void setZLevel(float f) {
		this.zLevel = f;
	}

	@Override
	public FontRenderer getFontRenderer() {
		return fontRenderer;
	}

	@Override
	public RenderItem getItemRenderer() {
		return itemRenderer;
	}

	@Override
	public Minecraft getMinecraft() {
		return mc;
	}
}
