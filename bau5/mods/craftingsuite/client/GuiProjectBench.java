package bau5.mods.craftingsuite.client;

//import invtweaks.api.container.ChestContainer;
//import invtweaks.api.container.ContainerSection;
//import invtweaks.api.container.ContainerSectionCallback;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.Reference;
import bau5.mods.craftingsuite.common.inventory.ContainerProjectBench;
import bau5.mods.craftingsuite.common.inventory.EnumInventoryModifier;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//@ChestContainer
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
		fontRenderer.drawString(StatCollector.translateToLocal("gui.projectbench.name"), 8, 6, 4210752);
	}
	
	/*@ContainerSectionCallback
	public Map<ContainerSection, List<Slot>> getContainerSections(){
		Map<ContainerSection, List<Slot>> sectionMap = new HashMap<ContainerSection, List<Slot>>();
		sectionMap.put(ContainerSection.CRAFTING_OUT, inventorySlots.inventorySlots.subList(0, 1));
		sectionMap.put(ContainerSection.CRAFTING_IN_PERSISTENT, inventorySlots.inventorySlots.subList(1, 10));
		sectionMap.put(ContainerSection.CHEST, inventorySlots.inventorySlots.subList(10, 28));
		return sectionMap;
	}*/
	
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
		if(((ContainerProjectBench)inventorySlots).tileEntity.getInventoryModifier() == EnumInventoryModifier.TOOLS){
			TileEntityProjectBench tpb = (TileEntityProjectBench)((ContainerProjectBench)inventorySlots).tileEntity;
			this.drawTexturedModalRect(x-21, y+10, 176, 44, 24, 66);
			int index = tpb.selectedToolIndex;
			if(index != -1){
				this.zLevel = 700F;
				this.drawTexturedModalRect(x-21, y+13 + (index * 18), 176, 110, 24, 24);
				this.zLevel = 0F;
				if(tpb.getSelectedTool() == null)
					return;
				ItemStack stack = tpb.getSelectedTool();
				if(stack == null)
					return;
				
				GL11.glTranslatef(0.0F, 0.0F, 32.0F);        
				GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(768, 1);
		        GL11.glColor4d(1.0F, 1.0F, 1.0F, 0.9F);
		        
		        this.zLevel = 200.0F;
		        itemRenderer.zLevel = 200.0F;
		        FontRenderer font = null;
		        if (stack != null) font = stack.getItem().getFontRenderer(stack);
		        if (font == null) font = fontRenderer;
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		        itemRenderer.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), stack, x+48, y+35);
		        itemRenderer.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), stack, x+48, y+35, null);
		        this.zLevel = 0.0F;
		        itemRenderer.zLevel = 0.0F;
		        
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		        GL11.glDisable(GL11.GL_BLEND);
		        GL11.glEnable(GL11.GL_LIGHTING);
			}
		}
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
