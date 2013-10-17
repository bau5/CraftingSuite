package bau5.mods.craftingsuite.client;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.CraftingSuite;
import bau5.mods.craftingsuite.common.Reference;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import cpw.mods.fml.client.FMLClientHandler;

public class CraftingBlockRenderer extends TileEntitySpecialRenderer implements IItemRenderer{
	
	private RenderBlocks renderBlocks;
	private RenderItem  renderItems;
	
	private ModelModificationTable model;
	private ResourceLocation modelTexture = new ResourceLocation(Reference.TEX_LOC +":" +"textures/models/ModificationTable.png");
	
	public CraftingBlockRenderer(){
		model = new ModelModificationTable();
		renderBlocks = new RenderBlocks();
		renderItems  = new RenderItem() {
			public byte getMiniItemCountForItemStack(ItemStack stack) { return 1; }
			public byte getMiniBlockCountForItemStack(ItemStack stack){ return 1; }
			@Override
			public boolean shouldBob() { return false; }
			@Override
			public boolean shouldSpreadItems() { return false; }
		};
		renderItems.setRenderManager(RenderManager.instance);	
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        switch(type){
        case INVENTORY:
        	GL11.glTranslatef(0, 1f, 0);
            GL11.glRotatef(180, 1f, 0, 0);
            break;
        case EQUIPPED:
        	GL11.glRotatef(180, 1f, 0, 0);
        	GL11.glTranslatef(0.4f, -2f, -0.4f);
        	break;
        case EQUIPPED_FIRST_PERSON:
        	GL11.glRotatef(180, 1f, 0, 0);
        	GL11.glTranslatef(0.5f, -2f, -0.5f);
        	break;
        case ENTITY:
        	GL11.glRotatef(180, 1f, 0, 0);
        	GL11.glTranslatef(0f, -1.2f, 0);
        	break;
        default: break;
        }
        switch(item.itemID){
        case 0: 
    		FMLClientHandler.instance().getClient().renderEngine.bindTexture(modelTexture);
            model.renderAll(null);
            break;
        case 1: 
        	break;
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double d0, double d1,
			double d2, float f) {
		if(tile instanceof TileEntityModificationTable)
			renderModificationTable((TileEntityModificationTable)tile, d0, d1, d2, f);
		if(tile instanceof TileEntityModdedTable)
			renderModdedTable((TileEntityModdedTable)tile, d0, d1, d2, f);
	}
	
	public void renderModificationTable(TileEntityModificationTable tile, double x, double y, double z, float f){
		if(renderBlocks.blockAccess == null)
			renderBlocks.blockAccess = tile.worldObj;
		renderBlocks.renderStandardBlock(CraftingSuite.craftingBlock, (int) x, (int) y, (int) z);
		bindTexture(modelTexture);
		GL11.glPushMatrix();
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)x +0.5f, (float)y +1.5f, (float)z +0.5f);
        GL11.glRotatef(180, 1f, 0, 0);
        model.renderAll(tile);
        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public void renderModdedTable(TileEntityModdedTable tile, double d0, double d1, double d2, float f){
		
	}
}
