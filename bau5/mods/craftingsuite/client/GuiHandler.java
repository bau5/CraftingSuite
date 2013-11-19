package bau5.mods.craftingsuite.client;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.Reference;
import bau5.mods.craftingsuite.common.handlers.PlanHandler;
import bau5.mods.craftingsuite.common.inventory.ContainerBase;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiHandler {
	private IModifiedTileEntityProvider tile;
	private GuiContainer gui;
	private IGuiBridge 	 guiBridge;
	private ResourceLocation partsResource;
	private EnumInventoryModifier modifier;
	public ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();
	
	private int buttonShiftX = 0;
	private int buttonShiftY = 0;
	
	public GuiHandler(GuiContainer parentGui, IModifiedTileEntityProvider te){
		tile = te;
		gui = parentGui;
		guiBridge = (IGuiBridge)gui;
		partsResource = new ResourceLocation("craftingsuite", "textures/gui/parts.png");
		modifier = tile.getInventoryModifier();
		if(gui instanceof GuiModdedCraftingTable){
			buttonShiftX = 2;
			buttonShiftY = 20;
		}
	}
	
	public void drawAdditionalParts(int i, int j, int xSize, int ySize){
		if(tile == null)
			return;
		guiBridge.getMinecraft().getTextureManager().bindTexture(partsResource);
		RenderItem itemRenderer = guiBridge.getItemRenderer();
		int x = (gui.width-xSize)/2;
		int y = (gui.height-ySize)/2;
		if(modifier == EnumInventoryModifier.TOOLS){
			gui.drawTexturedModalRect(x-22, y+10, 0, 0, 24, 66);
			int index = tile.getSelectedToolIndex();
			if(index != -1){
				guiBridge.setZLevel(700F);
				gui.drawTexturedModalRect(x-22, y+13 + (index * 18), 0, 66, 24, 24);
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
		}else if(modifier == EnumInventoryModifier.PLAN){
			PlanHandler hndlr = (PlanHandler)((ContainerBase)gui.inventorySlots).handler;
			gui.drawTexturedModalRect(x+7, y+33, 0, 90, 18, 18);
			if(hndlr.validPlanInSlot()){
				ItemStack[] stacks = hndlr.getPlanStacks();
				GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(768, 1);
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		        int index = 0;
		        for(int a = 0; a < 3; a++){
		        	for(int b = 0; b < 3; b++){
		        		GL11.glPushMatrix();
						GL11.glDisable(GL11.GL_LIGHTING);
		        		int xLoc = (x + 30)+(b*18);
		        		int yLoc = (y + 17)+(a*18);
		        		ItemStack stack = stacks[index++];
				        FontRenderer font = null;
				        if (stack != null) font = stack.getItem().getFontRenderer(stack);
				        if (font == null) font = guiBridge.getFontRenderer();
				        itemRenderer.renderItemAndEffectIntoGUI(font, guiBridge.getMinecraft().getTextureManager(), stack, xLoc, yLoc);
				        itemRenderer.renderItemOverlayIntoGUI(font, guiBridge.getMinecraft().getTextureManager(), stack, xLoc, yLoc, "");
				        GL11.glPopMatrix();
		        	}
		        }
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		        GL11.glDisable(GL11.GL_BLEND);
		        GL11.glEnable(GL11.GL_LIGHTING);
			}
		}else if(modifier == EnumInventoryModifier.DEEP){
			gui.drawTexturedModalRect(x-21, y+27, 0, 108, 22, 30);
		}
	}

	public void makeButtons() {
		int shift = 6;
		if(tile.getInventoryModifier() == EnumInventoryModifier.PLAN){
			shift = 0;
			buttons.add(new WritePlan(15294, gui.width/2 -86 +buttonShiftX, gui.height/2 -49 +buttonShiftY, 13, 11, ""));
		}
		if(tile instanceof TileEntityProjectBench)
			buttons.add(new ClearInventory(15294, gui.width/2 -73 -shift+buttonShiftX, gui.height/2 -49 +buttonShiftY, 13, 11, ""));
	}
	
	public class ClearInventory extends GuiButton{
		public ClearInventory(int id, int xPos, int yPos, int width,
				int height, String label) {
			super(id, xPos, yPos, width, height, label);
		}
		
		@Override
		public void drawButton(Minecraft mc, int i, int j) {
			if (this.drawButton)
	        {
	            FontRenderer fontrenderer = mc.fontRenderer;
	            mc.getTextureManager().bindTexture(partsResource);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            this.field_82253_i = i >= this.xPosition && j >= this.yPosition && i < this.xPosition + this.width && j < this.yPosition + this.height;
	            int k = this.getHoverState(this.field_82253_i);
	            //Top left, top right, bottom left, bottom right, icon
	            this.drawTexturedModalRect(this.xPosition, this.yPosition, 243, 0 + (k-1)*11, width, height); 
	            this.mouseDragged(mc, i, j);
	            int l = 14737632;

	            if (!this.enabled)
	            {
	                l = -6250336;
	            }
	            else if (this.field_82253_i)
	            {
	                l = 16777120;
	            }

	            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
	        }
		}
		@Override
		public boolean mousePressed(Minecraft mc, int par2, int par3) {
			boolean fireButton = super.mousePressed(mc, par2, par3);
			if(fireButton){
	            mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Reference.CHANNEL, new byte[]{0}));
			}
			return fireButton;
		}
	}
	
	public class WritePlan extends GuiButton{
		public WritePlan(int id, int xPos, int yPos, int width,
				int height, String label) {
			super(id, xPos, yPos, width, height, label);
		}
		
		@Override
		public void drawButton(Minecraft mc, int i, int j) {
			if(drawButton){
				FontRenderer fontrenderer = mc.fontRenderer;
	            mc.getTextureManager().bindTexture(partsResource);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            this.field_82253_i = i >= this.xPosition && j >= this.yPosition && i < this.xPosition + this.width && j < this.yPosition + this.height;
	            int k = this.getHoverState(this.field_82253_i);
	            if(!isEnabled())
	            	k = 1;
	            //Top left, top right, bottom left, bottom right, icon
	            this.drawTexturedModalRect(this.xPosition, this.yPosition, 243, 22 + (k-1)*11, width, height); 
	            this.mouseDragged(mc, i, j);
	            int l = 14737632;

	            if (!this.enabled)
	            {
	                l = -6250336;
	            }
	            else if (this.field_82253_i)
	            {
	                l = 16777120;
	            }

	            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
	       }
		}
		
		public boolean isEnabled(){
			return ((PlanHandler)((ContainerBase)gui.inventorySlots).handler).blankPlanInSlot();
		}
		
		@Override
		public boolean mousePressed(Minecraft mc, int par2, int par3) {
			if(!isEnabled())
				return false;
			boolean fireButton = super.mousePressed(mc, par2, par3);
			if(fireButton){
				PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Reference.CHANNEL, new byte[]{2}));
				((PlanHandler)((ContainerBase)gui.inventorySlots).getModifierHandler()).writePlanToStack();
//				((ContainerProjectBench)inventorySlots).writePlanToNBT();
			}
			return fireButton;
		}
	}
}
