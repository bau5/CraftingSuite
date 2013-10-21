package bau5.mods.craftingsuite.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.BlockCrafting;
import bau5.mods.craftingsuite.common.BlockModificationTable;
import bau5.mods.craftingsuite.common.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.Reference;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class CraftingBlockRenderer extends TileEntitySpecialRenderer implements IItemRenderer, ISimpleBlockRenderingHandler{
	
	private RenderBlocks renderBlocks;
	private RenderItem  renderItems;
	
	private ModelModificationTable model;
	private ResourceLocation modelTexture = new ResourceLocation(Reference.TEX_LOC +":" +"textures/models/ModificationTable.png");
	
	public static boolean renderInDisplay = false;
	
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

        switch(item.getItemDamage()){
        case 0: 
    		FMLClientHandler.instance().getClient().renderEngine.bindTexture(modelTexture);
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
            model.renderAll(null);
            break;
        case 2: 
        	switch(type){
        	case EQUIPPED_FIRST_PERSON:
        		GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        		break;
        	case EQUIPPED:
        		GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        		break;
        	case INVENTORY:
        		if(renderInDisplay){
        			GL11.glScalef(3.0F, 3.0F, 3.0F);
        			float rotational = (Minecraft.getSystemTime()) / (3000.0F) * 300.0F;
        			GL11.glRotatef(rotational / 5, 0.0F, 1.0F, 0.0F);
        		}
        		break;
        	default: break;
        	}
        	renderModdedCraftingTable(item, 2, 1f);
        	break;
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double d0, double d1,
			double d2, float f) {
		if(renderBlocks.blockAccess == null)
			renderBlocks.blockAccess = tile.worldObj;
		if(tile instanceof TileEntityModificationTable)
			renderModificationTable((TileEntityModificationTable)tile, d0, d1, d2, f);
		if(tile instanceof TileEntityModdedTable)
			renderModdedTable((TileEntityModdedTable)tile, d0, d1, d2, f);
		if(tile instanceof TileEntityProjectBench)
			renderProjectBench((TileEntityProjectBench)tile, d0, d1, d2, f);
	}
	
	public void renderProjectBench(TileEntityProjectBench tile, double x0, double y0, double z0, float f) {
		renderBlocks.renderBlockByRenderType(Block.blocksList[tile.worldObj.getBlockId(tile.xCoord, tile.yCoord, tile.zCoord)], tile.xCoord, tile.yCoord, tile.zCoord);
        boolean renderResult = (tile.upgrades.length == 5) ? (tile.upgrades[4] == 1 ? true : false) : false;;
        if(renderResult){
        	ItemStack stack = tile.result;
        	if(stack != null){
        		ItemStack copy = stack.copy();
        		copy.stackSize = 1;
	        	EntityItem ei = new EntityItem(tile.worldObj, tile.xCoord, tile.yCoord +1, tile.zCoord);
	            ei.hoverStart = 0f;
	            ei.setEntityItemStack(copy);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            GL11.glPushMatrix();
	            GL11.glEnable(32826 /* rescale */);
	            GL11.glTranslatef((float) x0, (float) y0, (float) z0);
	            int l = tile.worldObj.getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);
	            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 170F, 170F);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            
	            float rotational = (Minecraft.getSystemTime()) / (3000.0F) * 300.0F;
	
	            if(stack.itemID < Block.blocksList.length && Block.blocksList[stack.itemID] != null
	                              && Block.blocksList[stack.itemID].blockID != 0)
	            {
	            	GL11.glPushMatrix();
	            	GL11.glTranslatef(0.5F, 1.2F, 0.5F);
	            	GL11.glRotatef(rotational / 5, 0F, 1.0F, 0F);
	                renderItems.doRenderItem(ei, 0, 0, 0, 0, 0);
	                GL11.glPopMatrix();
	            }else
	            {
	            	GL11.glPushMatrix();
	            	GL11.glTranslatef(0.5F, 1.1F, 0.5F);
	            	GL11.glRotatef(rotational / 5, 0F, 1.0F, 0F);
	                renderItems.doRenderItem(ei, 0, 0, 0, 0, 0);
	                GL11.glPopMatrix();
	            }
	            GL11.glDisable(32826 /* scale */);
	            GL11.glPopMatrix();
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        	}
        }
	}

	public void renderModificationTable(TileEntityModificationTable tile, double x, double y, double z, float f){
		bindTexture(modelTexture);
		GL11.glPushMatrix();
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)x +0.5f, (float)y +1.5f, (float)z +0.5f);
        GL11.glRotatef(180, 1f, 0, 0);
        model.renderAll(tile);
        model.rotatePiece(tile.rotation);
        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public void renderModdedTable(TileEntityModdedTable tile, double d0, double d1, double d2, float f){}
	
	/**
	 * Renders the item in inventories.
	 * 
	 * @param theStack
	 * @param i
	 * @param f
	 */
	public void renderModdedCraftingTable(ItemStack theStack, int i, float f){
        Tessellator tessellator = Tessellator.instance;
        if(tessellator.isDrawing)
        	tessellator.draw();
        byte[] bytes = ((NBTTagByteArray)ModificationNBTHelper.getUpgradeByteArray(theStack.stackTagCompound)).byteArray;
        byte plankMeta = bytes[2];
        Block theBlock = Block.blocksList[theStack.itemID];
        Icon[] icons = null;
        boolean overlay = bytes[3] != -1;
        if(theBlock instanceof BlockCrafting)
        	icons = ((BlockCrafting)theBlock).icons;
		theBlock.setBlockBoundsForItemRender();
        renderBlocks.setRenderBoundsFromBlock(theBlock);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        if (renderBlocks.useInventoryTint)
        {
        	int j = theBlock.getRenderColor(0);
            float f11 = (float)(j >> 16 & 255) / 255.0F;
            float f21 = (float)(j >> 8 & 255) / 255.0F;
            float f31 = (float)(j & 255) / 255.0F;
            GL11.glColor4f(f11 * 1.0F, f21 * 1.0F, f31 * 1.0F, 1.0F);
        }
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderBlocks.renderFaceYNeg(theBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 0, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        Icon ic = null;
        if(overlay)
        	ic = Block.cloth.getIcon(0, bytes[3]);
        else
        	ic = Block.planks.getIcon(0, plankMeta);
    	renderBlocks.renderFaceYPos(theBlock, 0.0D, 0.0D, 0.0D, ic);
    	renderBlocks.setOverrideBlockTexture(icons[4]);
        renderBlocks.renderFaceYPos(theBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 1, i));
        renderBlocks.clearOverrideBlockTexture();
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderBlocks.renderFaceZNeg(theBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 2, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderBlocks.setOverrideBlockTexture(Block.planks.getIcon(0, plankMeta));
        renderBlocks.renderFaceZNeg(theBlock, 0.0D, 0.0D, 0.01D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 2, i));
        tessellator.draw();
        renderBlocks.clearOverrideBlockTexture();
        if(overlay && icons != null){
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            //Draw in outer overlay 
            GL11.glPushMatrix();
            renderBlocks.renderFaceZNeg(theBlock, -0.000001D, 0.0D, 0.0D, icons[3]);
            renderBlocks.renderFaceZPos(theBlock, -0.000001D, 0.0D, 0.0D, icons[3]);
            renderBlocks.renderFaceXNeg(theBlock, 0.000001D, 0.0D, 0.0D, icons[3]);
            renderBlocks.renderFaceXPos(theBlock, 0.000001D, 0.0D, 0.0D, icons[3]);
            renderBlocks.clearOverrideBlockTexture();
            tessellator.draw();
            GL11.glPopMatrix();
            //Draw in colored overlay
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            GL11.glPushMatrix();
//            renderBlocks.setOverrideBlockTexture(ic);
            renderBlocks.renderMaxY = 0.24997111D;
            renderBlocks.renderMaxX = 0.755D;
            renderBlocks.renderFaceZNeg(theBlock, 0.121D, 0.7502999D, 0.001D, ic/*Block.cloth.getIcon(0, bytes[3])*/);
            renderBlocks.renderFaceZPos(theBlock, 0.121D, 0.7502999D, -0.001D, ic/*Block.cloth.getIcon(0, bytes[3])*/);
            renderBlocks.renderMaxX = 1.0D;
            renderBlocks.renderMaxZ = 0.755D;
            renderBlocks.renderFaceXNeg(theBlock, 0.001D, 0.750D, 0.121D, ic/*Block.cloth.getIcon(0, bytes[3])*/);
            renderBlocks.renderFaceXPos(theBlock, -0.001D, 0.750D, 0.121D, ic/*Block.cloth.getIcon(0, bytes[3])*/);
            renderBlocks.renderMaxY = 1.0D;
            renderBlocks.renderMaxZ = 1.0D;
            renderBlocks.clearOverrideBlockTexture();
            tessellator.draw();
            GL11.glPopMatrix();
        }
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderBlocks.renderFaceZPos(theBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 3, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderBlocks.setOverrideBlockTexture(Block.planks.getIcon(0, plankMeta));
        renderBlocks.renderFaceZPos(theBlock, 0.0D, 0.0D, -0.01D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 3, i));
        tessellator.draw();
        renderBlocks.clearOverrideBlockTexture();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXNeg(theBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 4, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderBlocks.setOverrideBlockTexture(Block.planks.getIcon(0, plankMeta));
        renderBlocks.renderFaceXNeg(theBlock, 0.01D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 4, i));
        tessellator.draw();
        renderBlocks.clearOverrideBlockTexture();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXPos(theBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 5, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.setOverrideBlockTexture(Block.planks.getIcon(0, plankMeta));
        renderBlocks.renderFaceXPos(theBlock, -0.01D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 4, i));
        tessellator.draw();
        renderBlocks.clearOverrideBlockTexture();
        
	}
	
	public void renderStandardBlockInInventory(Block block, int i, float f){
        Tessellator tessellator = Tessellator.instance;
        if(tessellator.isDrawing)
        	tessellator.draw();
		block.setBlockBoundsForItemRender();
        renderBlocks.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 0, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 1, i));
        tessellator.draw();
        if (renderBlocks.useInventoryTint)
        {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
        }
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 2, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 3, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 4, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 5, i));
        tessellator.draw();
        tessellator.drawMode = 7;
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        ReflectionHelper.setPrivateValue(Tessellator.class, tessellator, true, 6);
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXPos(block, 0.0D, 1.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 5, i));
        ReflectionHelper.setPrivateValue(Tessellator.class, tessellator, false, 6);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		// TODO Auto-generated method stub
		
	}
	

	/**
	 *  Render block in the world.
	 */
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		if(world == null || renderer.blockAccess == null)
			return false;
		if(block instanceof BlockModificationTable)
			return false;
		if(world.getBlockTileEntity(x, y, z) == null)
			return false;
		renderer.renderStandardBlockWithAmbientOcclusion(block, x, y, z, 1.0F, 1.0F, 1.0F);
		Tessellator tessellator = Tessellator.instance;
		int l = block.getMixedBrightnessForBlock(world, x, y, z);
		float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4;
        float f8 = f4;
        float f9 = f4;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;
        byte[] bytes = ((TileEntityProjectBench)world.getBlockTileEntity(x, y, z)).upgrades;
        byte plankMeta = bytes[2];
        boolean overlay = bytes[3] != -1;
        
        Icon[] icons = null;
        if(block instanceof BlockCrafting)
        	icons = ((BlockCrafting)block).icons;
        Icon plankIcon = Block.planks.getIcon(0, plankMeta);
        tessellator.setBrightness(renderBlocks.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(world, x + 1, y, z));
        tessellator.setColorOpaque_F(f12, f15, f18);
    	renderBlocks.renderFaceXPos(block, (double)x -0.0009, (double)y, (double)z, plankIcon);
    	if(overlay) renderBlocks.renderFaceXPos(block, (double)x -0.0001, (double)y, (double)z, icons[3]);
    	
        tessellator.setBrightness(renderBlocks.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(world, x - 1, y, z));
        tessellator.setColorOpaque_F(f12, f15, f18);
    	renderBlocks.renderFaceXNeg(block, (double)x +0.0009, (double)y, (double)z, plankIcon);
    	if(overlay) renderBlocks.renderFaceXNeg(block, (double)x +0.0001, (double)y, (double)z, icons[3]);
    	
        tessellator.setBrightness(renderBlocks.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(world, x, y, z + 1));
        tessellator.setColorOpaque_F(f11, f14, f17);
    	renderBlocks.renderFaceZPos(block, (double)x, (double)y, (double)z -0.0009, plankIcon);
    	if(overlay) renderBlocks.renderFaceZPos(block, (double)x, (double)y, (double)z -0.0001, icons[3]);
    	
        tessellator.setBrightness(renderBlocks.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(world, x, y, z - 1));
        tessellator.setColorOpaque_F(f11, f14, f17);
    	renderBlocks.renderFaceZNeg(block, (double)x, (double)y, (double)z +0.0009, plankIcon);
    	if(overlay) renderBlocks.renderFaceZNeg(block, (double)x, (double)y, (double)z +0.0001, icons[3]);
    	
        if(icons != null && overlay){
        	
        	Icon wool = Block.cloth.getIcon(0, bytes[3]);
        	tessellator.setBrightness(renderBlocks.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(world, x, y + 1, z));
            tessellator.setColorOpaque_F(f7, f8, f9);
            renderBlocks.renderFaceYPos(block, (double)x, (double)y -0.001D, (double)z, wool);
        	
            renderBlocks.renderMaxY = 0.2491D;
            renderBlocks.renderMaxX = 0.7509D;
            tessellator.setBrightness(renderBlocks.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(world, x, y, z + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            renderBlocks.renderFaceZPos(block, (double)x +0.1247D, (double)y +0.7505D, (double)z -0.0005D, wool);
            tessellator.setBrightness(renderBlocks.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(world, x, y, z - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            renderBlocks.renderFaceZNeg(block, (double)x +0.1247D, (double)y +0.7505D,(double)z +0.0005D, wool);
            
            renderBlocks.renderMaxX = 1.0D;
            renderBlocks.renderMaxZ = 0.7509D;
            tessellator.setBrightness(renderBlocks.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(world, x + 1, y, z));
            tessellator.setColorOpaque_F(f12, f15, f18);
            renderBlocks.renderFaceXPos(block, (double)x -0.0005D, (double)y +0.7505D, (double)z +0.1247D, wool);
            tessellator.setBrightness(renderBlocks.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(world, x - 1, y, z));
            tessellator.setColorOpaque_F(f12, f15, f18);
            renderBlocks.renderFaceXNeg(block, (double)x +0.0005D, (double)y +0.7505D, (double)z +0.1247D, wool);
            renderBlocks.renderMaxY = 1.0D;
            renderBlocks.renderMaxZ = 1.0D;
        }else{
        	tessellator.setBrightness(renderBlocks.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(world, x, y + 1, z));
            tessellator.setColorOpaque_F(f7, f8, f9);
            renderBlocks.renderFaceYPos(block, (double)x, (double)y -0.001, (double)z, Block.planks.getIcon(0, plankMeta));
        }
        
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.getRenderID();
	}
}
