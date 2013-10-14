package bau5.mods.craftingsuite.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.Reference;
import bau5.mods.craftingsuite.common.tileentity.ContainerProjectBench;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiProjectBench extends GuiContainer {
	private ResourceLocation resource;
	private boolean once = true;
	public GuiProjectBench(InventoryPlayer inventory, TileEntity te) {
		super(new ContainerProjectBench(inventory, (TileEntityProjectBench)te));
		ySize += 48;
		resource = new ResourceLocation("craftingsuite", "textures/gui/pbGUI.png");
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		//TODO Localization
		fontRenderer.drawString("Project Bench", 8, 6, 4210752);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int x = (width-xSize)/2;
		int y = (height-ySize)/2;
		if(once){
			once = false;
			buttonList.add(new PBClearInventoryButton(15294, width/2 -76, height/2 -49, 11, 11, ""));
		}
		mc.getTextureManager().bindTexture(resource);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	public class PBClearInventoryButton extends GuiButton{

		public PBClearInventoryButton(int id, int xPos, int yPos, int width,
				int height, String label) {
			super(id, xPos, yPos, width, height, label);
		}
		@Override
		public void drawButton(Minecraft mc, int i, int j) {
			if (this.drawButton)
	        {
	            FontRenderer fontrenderer = mc.fontRenderer;
	            mc.getTextureManager().bindTexture(resource);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            this.field_82253_i = i >= this.xPosition && j >= this.yPosition && i < this.xPosition + this.width && j < this.yPosition + this.height;
	            int k = this.getHoverState(this.field_82253_i);
	            //Top left, top right, bottom left, bottom right, icon
	            this.drawTexturedModalRect(this.xPosition, this.yPosition, 176, 0 + (k-1)*11, width, height); 
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

}
