package bau5.mods.craftingsuite.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.Reference;
import bau5.mods.craftingsuite.common.inventory.ContainerModificationTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiModificationTable extends GuiContainer{
	
	private ResourceLocation guiTexture;
	private ResourceLocation partsTexture = new ResourceLocation("craftingsuite", "textures/gui/modtable.png");
	private TileEntityModificationTable tileEntity;
	private boolean once;

	public GuiModificationTable(TileEntityModificationTable tile, EntityPlayer player) {
		super(new ContainerModificationTable(tile, player));
		tileEntity = tile;
		guiTexture = new ResourceLocation("craftingsuite", "textures/gui/modtable.png");
		ySize = 256;
		xSize = 256;
		once = true;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString(I18n.getString("gui.modificationtable.name"), 8, 6, 30000800);
		this.fontRenderer.drawString(I18n.getString("gui.modificationtable.modifiers"), 8, 45, 0);
		this.fontRenderer.drawString(I18n.getString("gui.modificationtable.aesthetics"), 8, 81, 0);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int x = (width-xSize)/2;
		int y = (height-ySize)/2;
		if(once || buttonList.size() == 0){
			buttonList.add(new CraftButton(x +181, y +112));
			once = false;
		}
		mc.getTextureManager().bindTexture(guiTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		if(tileEntity.isCrafting())
			displayResult(tileEntity.craftingResult, x, y);
		else if(tileEntity.getResult() != null)
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
        CraftingBlockRenderer.renderInDisplay = true;
        itemRenderer.renderItemAndEffectIntoGUI(font, this.mc.renderEngine, result, x +195, y +46);
        CraftingBlockRenderer.renderInDisplay = false;
        GL11.glPopMatrix();
        this.zLevel = 0.0F;
        itemRenderer.zLevel = prevZ;
	}
	
	public class CraftButton extends GuiButton{
		public CraftButton(int x, int y){
			super(0, x, y, 40, 20, "Craft");
		}
		
		@Override
		public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
			boolean fire = super.mousePressed(par1Minecraft, par2, par3);
			if(fire){
				PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Reference.CHANNEL, new byte[]{ 1 }));
				tileEntity.craftRecipe();
				((ContainerModificationTable)inventorySlots).clearDummySlots();
			}
			return fire;
		}
		
		@Override
		public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
			enabled = false;
			if(tileEntity.isCrafting())
				enabled = false;
			else{
				ItemStack inSlot = tileEntity.getStackInSlot(5);
				if(inSlot == null){
					if(tileEntity.getResult() != null)
						enabled = true;
				}else{
					ItemStack stack = inSlot.copy();
					stack.stackSize = 1;
					if(tileEntity.getResult() != null && 
							ItemStack.areItemStacksEqual(stack, tileEntity.getResult()) &&
							ItemStack.areItemStackTagsEqual(stack, tileEntity.getResult())){
						enabled = true;
					}
				}
			}
			super.drawButton(par1Minecraft, par2, par3);
			if(enabled){
				if(getHoverState(field_82253_i) == 2){
//					if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
						int x = par2 -guiLeft +4;
						int y = par3 -guiTop +6;
//						drawTexturedModalRect(par2 +5, par3 +6, 25, 0, 4, 20);
//						drawTexturedModalRect(par2 +9, par3 +6, 25, 0, par2 + (16 * (tileEntity.inputForResult.length + 1)) - 8, 3+par3 + 23);
						drawGradientRect(par2 +5, par3 +6, par2 + (16 * (tileEntity.inputForResult.length + 1)) - 8, 3+par3 + 23, -99999999, -99999999);
						((ContainerModificationTable)inventorySlots).addItemsForRender(x+2, y);
//					}else
//						((ContainerModificationTable)inventorySlots).clearDummySlots();
				}else{
					((ContainerModificationTable)inventorySlots).clearDummySlots();
				}
			}
		}
	}
}
