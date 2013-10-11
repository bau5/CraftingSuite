package bau5.mods.projectbench.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import bau5.mods.projectbench.common.ContainerProjectBench;
import bau5.mods.projectbench.common.ContainerProjectBenchII;
import bau5.mods.projectbench.common.TEProjectBenchII;
import bau5.mods.projectbench.common.TileEntityProjectBench;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ProjectBenchGui extends GuiContainer {
	private int ID;
	private boolean once = true;
	private ResourceLocation resource;
	
	public ProjectBenchGui(InventoryPlayer inventoryPlayer,
			TileEntity tileEntity, int guiID) {
		super((guiID == 0) ? new ContainerProjectBench(inventoryPlayer, (TileEntityProjectBench)tileEntity) 
							  : new ContainerProjectBenchII(inventoryPlayer, (TEProjectBenchII)tileEntity));
		ySize += 48;
		ID = guiID;
		if(ID == 0){
			resource = new ResourceLocation("projectbench","textures/gui/pbGUI.png");
		}
		else if (ID == 1){
			resource = new ResourceLocation("projectbench", "textures/gui/pbGUI2.png");
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		if(ID == 0)
			fontRenderer.drawString("Project Bench", 8, 6, 4210752);
		else if(ID == 1)
			fontRenderer.drawString("Project Bench Mk. II", 8, 4, 4210752);
	}

	@Override
	protected void drawSlotInventory(Slot par1Slot) {
		super.drawSlotInventory(par1Slot);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		if(once && ID == 0){
			buttonList.add(new PBWritePlanButton(15295, width/2 - 76, height /2 -88, 11, 11, ""));
			once = false;
		}
		mc.getTextureManager().bindTexture(resource);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	public class PBWritePlanButton extends GuiButton{

		public PBWritePlanButton(int id, int xPos, int yPos, int width,
				int height, String label) {
			super(id, xPos, yPos, width, height, label);
		}
		
		@Override
		public void drawButton(Minecraft mc, int i, int j) {
			if(drawButton && isEnabled()){
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
		
		public boolean isEnabled(){
			ItemStack plan = ((ContainerProjectBench)inventorySlots).getPlanStack();
			return (((ContainerProjectBench)inventorySlots).tileEntity.getResult() != null && plan != null && plan.stackTagCompound == null);
		}
		
		@Override
		public boolean mousePressed(Minecraft mc, int par2, int par3) {
			if(!isEnabled())
				return false;
			boolean fireButton = super.mousePressed(mc, par2, par3);
			if(fireButton){
				PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("bau5_PB", new byte[]{2}));
				((ContainerProjectBench)inventorySlots).writePlanToNBT();
			}
			return fireButton;
		}
	}

}