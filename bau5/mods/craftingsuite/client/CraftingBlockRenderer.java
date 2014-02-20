package bau5.mods.craftingsuite.client;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.BlockCrafting;
import bau5.mods.craftingsuite.common.BlockModificationTable;
import bau5.mods.craftingsuite.common.CraftingSuite;
import bau5.mods.craftingsuite.common.helpers.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CraftingBlockRenderer extends TileEntitySpecialRenderer implements IItemRenderer, ISimpleBlockRenderingHandler{
	
	//TODO - Optifine messes with rendering, check for optifine loaded and change render?
	
	private RenderBlocks renderBlocks;
	private RenderItem  renderItems;
	
	private Random random;
	
	private ModelModificationTable modelModificationTable;
	private ModelCraftingTable	   modelCraftingTable;
	
	public static boolean renderInDisplay = false;
	public static boolean cmas = false;
	
	private ResourceLocation sm = new ResourceLocation("textures/entity/snowman.png");
	
	public CraftingBlockRenderer(){
		modelModificationTable = new ModelModificationTable();
		modelCraftingTable = new ModelCraftingTable();
		
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
		cmas = CraftingSuite.cmas;
		random = new Random();
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
        	FMLClientHandler.instance().getClient().renderEngine.bindTexture(modelModificationTable.modelTexture);
    		modelPreRender(type);
    		modelModificationTable.renderAll();
            break;
        case 1:
        	//Bind model texture...
        	FMLClientHandler.instance().getClient().renderEngine.bindTexture(modelCraftingTable.modelTexture);
        	modelPreRender(type); //set up model rendering environment
        	modelCraftingTable.renderDefaultParts(); //render all parts of the model, except the planks area.
        	//Unbind model texture, rebind normal texture environment.
        	FMLClientHandler.instance().getClient().renderEngine.bindTexture(FMLClientHandler.instance().getClient().getTextureManager().getResourceLocation(item.getItemSpriteNumber()));
        	//Pass off to custom renderer to finish the job, planks, overlay, display, etc.
        	renderModdedCraftingTableInInventory(item);
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
        	renderProjectBenchInInventory(item, 2, 1f);
        	break;
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	/**
	 * Applies the appropriate translates/rotates to align the model
	 * correctly in the corresponding environment.
	 * 
	 * @param type ItemRenderType passed on by Forge
	 */
	private void modelPreRender(ItemRenderType type){
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
	        default:
	        	break;
        }
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
		Block blck = Block.blocksList[tile.worldObj.getBlockId(tile.xCoord, tile.yCoord, tile.zCoord)];
		if(blck == null)
			return;
		renderBlocks.renderBlockByRenderType(blck, tile.xCoord, tile.yCoord, tile.zCoord);
        boolean renderResult = tile.getModifications().visual() == 1 ? true : false;
        boolean renderTools  = tile.getModifications().upgrades() == 3;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x0, (float) y0, (float) z0);
        GL11.glEnable(32826 /* rescale */);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(renderResult){
        	ItemStack stack = tile.inventoryHandler.result;
        	if(stack != null){
            	renderTileEntityResult((TileEntity)tile, stack);
        	}
        }
        if(renderTools && tile.getSizeInventory() >= 30 && Minecraft.isFancyGraphicsEnabled()){
        	GL11.glPushMatrix();
        	byte rotation = tile.getDirectionFacing();
    		if(rotation == 5){
    			GL11.glRotatef(-90, 0f, 1.0f, 0f);
    			GL11.glTranslatef(0f, 0f, -1.0f);
    		}else if(rotation == 3){
    			GL11.glRotatef(180, 0f, 1f, 0f);
    			GL11.glTranslatef(-1.0f, 0f, -1.0f);
    		}else if(rotation == 4){
    			GL11.glRotatef(90, 0f, 1.0f, 0f);
    			GL11.glTranslatef(-1.0f, 0f, 0f);
    		}
    		GL11.glTranslatef(0.8F, 1.015F, 0.14F);
    		GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
    		GL11.glScalef(0.6F, 0.6F, 0.6F);
    		for(int i = 0; i < 3; i++){
    			GL11.glPushMatrix();
    			ItemStack copy = tile.tools[i];
    			if(tile.selectedToolIndex == i){
    				GL11.glTranslatef(-0.5F, 0.3F, 0.0F);
    			}else{
	                switch(i){
		                case 0:	GL11.glRotatef(-40, 0.0F, 0.0F, 1.0F);
		                	break;
		                case 1: GL11.glTranslatef(0.0F, 1.0F, 0.0F);
		                		GL11.glRotatef(35, 0.0F, 0.0F, 1.0F);
		                	break;
		                case 2: GL11.glTranslatef(-1.0F, 0.4F, 0.0F);
		                		GL11.glRotatef(30, 0.0F, 0.0F, 1.0F);
		                	break;
	                }
    			}
    			if(copy != null){
	    			copy = copy.copy();
		    		EntityItem ei = new EntityItem(tile.worldObj, tile.xCoord, tile.yCoord +1, tile.zCoord);
		            ei.hoverStart = 0f;
		            ei.setEntityItemStack(copy);
		            Random rand = new Random();
		            GL11.glTranslatef(tile.randomShift, tile.randomShift, 0.0F);
		            renderItems.doRenderItem(ei, 0, 0, 0, 0, 0);
    			}
        		GL11.glPopMatrix();
    		}
    		GL11.glPopMatrix();
        }
        if(cmas){
        	byte rotation = tile.getDirectionFacing();
        	GL11.glPushMatrix();
        	GL11.glRotatef(180, 1.0F, 0.0F, 0.0F);
        	GL11.glTranslatef(0.5F, -1.25F, -0.5F);
        	GL11.glScalef(0.01F, 0.01F, 0.01F);
        	switch(rotation){
        	case 2: GL11.glRotatef(180, 0.0F, 1.0F, 0.0F);
        			GL11.glTranslatef(25.0F, 0.0F, 25.0F);
        			GL11.glRotatef(45, 0.0F, 1.0F, 0.0F);
    			break;
        	case 3: GL11.glTranslatef(25.0F, 0.0F, 25.0F);
    				GL11.glRotatef(45, 0.0F, 1.0F, 0.0F);
				break;
        	case 4: GL11.glRotatef(90, 0.0F, 1.0F, 0.0F);
        			GL11.glTranslatef(25.0F, 0.0F, 25.0F);
        			GL11.glRotatef(45, 0.0F, 1.0F, 0.0F);
				break;
        	case 5: GL11.glRotatef(-90, 0.0F, 1.0F, 0.0F);
					GL11.glTranslatef(25.0F, 0.0F, 25.0F);
					GL11.glRotatef(45, 0.0F, 1.0F, 0.0F);
        	}
        	renderBlocks.minecraftRB.renderEngine.bindTexture(sm);
        	ModelSnowMan sm = new ModelSnowMan();
        	sm.render(new EntitySnowman(tile.worldObj), 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
        	renderBlocks.minecraftRB.renderEngine.bindTexture(renderBlocks.minecraftRB.getTextureManager().getResourceLocation(new ItemStack(Block.stone).getItemSpriteNumber()));
        	GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
	}
	
	private void renderTileEntityResult(TileEntity tile, ItemStack stack) {
		ItemStack copy = stack.copy();
		copy.stackSize = 1;
    	EntityItem ei = new EntityItem(tile.worldObj, tile.xCoord, tile.yCoord +1, tile.zCoord);
        ei.hoverStart = 0f;
        ei.setEntityItemStack(copy);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glEnable(32826 /* rescale */);
        int l = tile.worldObj.getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);
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
        	GL11.glTranslatef(0.5F, 1.12F, 0.5F);
        	GL11.glScalef(0.65F, 0.65F, 0.65F);
        	GL11.glRotatef(rotational / 5, 0F, 1.0F, 0F);
            renderItems.doRenderItem(ei, 0, 0, 0, 0, 0);
            GL11.glPopMatrix();
        }
        GL11.glDisable(32826 /* scale */);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void renderModificationTable(TileEntityModificationTable tile, double x, double y, double z, float f){
		bindTexture(modelModificationTable.modelTexture);
		GL11.glPushMatrix();
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)x +0.5f, (float)y +1.5f, (float)z +0.5f);
        GL11.glRotatef(180, 1f, 0, 0);
        if(tile.model != null){
	        tile.model.renderAll();
	        tile.model.rotateSpinner(tile.rotation);
        }
        renderResultModificationTable(tile);
        GL11.glDisable(32826 /* scale */);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
	}
	
	private void renderResultModificationTable(TileEntityModificationTable tile){
        ItemStack theStack = null;
      	EntityItem ei = new EntityItem(tile.worldObj, tile.xCoord, tile.yCoord +1, tile.zCoord);
      	ei.hoverStart = 0f;
      	GL11.glPushMatrix();
      	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      	GL11.glRotatef(180, 1.0F, 0.0F, 0.0F);
      	GL11.glTranslatef(-0.5F, -2.14F, -0.5F);
      	int l = tile.worldObj.getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);
      	OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 170F, 170F);
      	
      	float rotational = (Minecraft.getSystemTime()) / (3000.0F) * 300.0F;
      	GL11.glTranslatef(0.5F, 1.6F, 0.5F);
      	if(tile.isCrafting() && tile.craftingResult != null){
      		theStack = tile.craftingResult.copy();
      		theStack.stackSize = 1;
      		ei.setEntityItemStack(theStack);
      		GL11.glRotatef(Math.abs(tile.rotation) *10, 0F, 1.0F, 0F);
      		GL11.glTranslatef(0.0F, 0.002F * -((tile.finishTime/10)-(tile.timeCrafting/10)), 0.0F);
      	}else if((tile.getStackInSlot(5) == null && tile.craftingResult != null ) || (tile.craftingResult != null && !ItemStack.areItemStackTagsEqual(tile.craftingResult, tile.getStackInSlot(5)))){
      		GL11.glTranslatef(0.0F, -0.5F, 0.0F);
      		theStack = tile.craftingResult.copy();
      		theStack.stackSize = 1;
			ei.setEntityItemStack(theStack);
      	}else{
      		if(tile.getStackInSlot(5) == null){
      			GL11.glPopMatrix();
      			return;
      		}
      		theStack = tile.getStackInSlot(5).copy();
      		theStack.stackSize = 1;
      		ei.setEntityItemStack(theStack);
      	}
      	renderItems.doRenderItem(ei, 0, 0, 0, 0, 0);
      	GL11.glPopMatrix();
	}
	
	public void renderModdedTable(TileEntityModdedTable tile, double d0, double d1, double d2, float f) {
		if(tile.getModifications().type() == 1)
			renderCraftingTable(tile, d0, d1, d2, f);
	}
	
	private void renderCraftingTable(TileEntityModdedTable tile, double x, double y, double z, float f){
		Block blck = Block.blocksList[tile.worldObj.getBlockId(tile.xCoord, tile.yCoord, tile.zCoord)];
		if(blck == null)
			return;
		renderBlocks.renderBlockByRenderType(blck, tile.xCoord, tile.yCoord, tile.zCoord);
		bindTexture(modelCraftingTable.modelTexture);
		GL11.glPushMatrix();
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)x +0.5F, (float)y +1.5F, (float)z +0.5F);
        GL11.glRotatef(180, 1f, 0, 0);
        modelCraftingTable.renderDefaultParts();
    	if(tile.getModifications().visual() == 1){
    		if(tile.getInventoryHandler().result != null){
    			GL11.glRotatef(180, 1.0F, 0.0F, 0.0F);
    			GL11.glTranslatef(-0.5F, -1.5F, -0.5F);
    			renderTileEntityResult(tile, tile.getInventoryHandler().result);
    		}
    	}
        
        GL11.glDisable(32826 /* scale */);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
	}

	private void renderModdedCraftingTableInInventory(ItemStack theStack) {
		 Tessellator tessellator = Tessellator.instance;
        if(tessellator.isDrawing)
        	tessellator.draw();
        if(theStack.stackTagCompound == null)
        	return;
        byte[] bytes = ModificationNBTHelper.getUpgradeByteArray(theStack.stackTagCompound);
        if(bytes == null || bytes.length != 5)
        	return;

        ItemStack plankUsed = ItemStack.loadItemStackFromNBT(ModificationNBTHelper.getPlanksUsed(theStack.stackTagCompound));
        Block block = Block.blocksList[theStack.itemID];
        if(plankUsed == null)
        	return;
        Icon plankIcon = Block.blocksList[plankUsed.itemID].getIcon(0, plankUsed.getItemDamage());
        GL11.glPushMatrix();
        if (renderBlocks.useInventoryTint)
        {
        	int j = block.getRenderColor(0);
            float f11 = (float)(j >> 16 & 255) / 255.0F;
            float f21 = (float)(j >> 8 & 255) / 255.0F;
            float f31 = (float)(j & 255) / 255.0F;
            GL11.glColor4f(f11 * 1.0F, f21 * 1.0F, f31 * 1.0F, 1.0F);
        }
        
        block.setBlockBoundsForItemRender();
        renderBlocks.clearOverrideBlockTexture();
        renderBlocks.setRenderBoundsFromBlock(block);
        GL11.glTranslatef(-0.5F, 0.6F, -0.5F);
        renderBlocks.setOverrideBlockTexture(plankIcon);
        
        renderBlocks.renderMinY = 0.125D;
        renderBlocks.renderMaxY = 0.75D;
        renderBlocks.renderMaxX = 0.7D;
        renderBlocks.renderMaxZ = 0.78D;
        renderBlocks.renderMinZ = 0.22D;
        if(!tessellator.isDrawing)
        	tessellator.startDrawingQuads();
    	renderBlocks.renderFaceXPos(block, 0.080D, 0.0D, 0.0D, null);
    	tessellator.draw();
    	tessellator.startDrawingQuads();
    	renderBlocks.renderFaceXNeg(block, 0.220D, 0.0D, 0.0D, null);
    	tessellator.draw();
        renderBlocks.renderMinY = 0.0D;
        renderBlocks.renderMaxY = 1.0D;
        renderBlocks.renderMaxX = 1.0D;
        renderBlocks.renderMaxZ = 1.0D;
        renderBlocks.renderMinZ = 0.0D;
        renderBlocks.renderMinY = 0.125D;
        renderBlocks.renderMaxY = 0.75D;
        renderBlocks.renderMaxZ = 0.7D;
        renderBlocks.renderMaxX = 0.78D;
        renderBlocks.renderMinX = 0.22D;
        tessellator.startDrawingQuads();
    	renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.080D, null);
    	tessellator.draw();
    	tessellator.startDrawingQuads();
    	renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.220D, null);
    	tessellator.draw();
        renderBlocks.renderMinY = 0.0D;
        renderBlocks.renderMaxY = 1.0D;
        renderBlocks.renderMaxZ = 1.0D;
        renderBlocks.renderMaxX = 1.0D;
        renderBlocks.renderMinX = 0.0D;
        renderBlocks.renderMinX = 0.2D;
        renderBlocks.renderMaxX = 0.8D;
        renderBlocks.renderMinZ = 0.2D;
        renderBlocks.renderMaxZ = 0.8D;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderBlocks.renderFaceYNeg(block, 0.0D, 0.125D, 0.0D, null);
        tessellator.draw();
        renderBlocks.renderMinX = 0.0D;
        renderBlocks.renderMaxX = 1.0D;
        renderBlocks.renderMinZ = 0.0D;
        renderBlocks.renderMaxZ = 1.0D;
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, -1.0F, -1.0F);
        renderBlocks.renderFaceYNeg(block, 0.0D, -0.090D, 0.0D, null);
        tessellator.draw();	
        renderBlocks.clearOverrideBlockTexture();
        GL11.glPopMatrix();
	}
	
	/**
	 * Renders the item in inventories.
	 * 
	 * @param theStack
	 * @param i
	 * @param f
	 */
	public void renderProjectBenchInInventory(ItemStack theStack, int i, float f){
        Tessellator tessellator = Tessellator.instance;
        if(tessellator.isDrawing)
        	tessellator.draw();
        if(theStack.stackTagCompound == null)
        	return;
        byte[] bytes = ModificationNBTHelper.getUpgradeByteArray(theStack.stackTagCompound);
        if(bytes == null || bytes.length != 5)
        	return;
        ItemStack plankUsed = ItemStack.loadItemStackFromNBT(ModificationNBTHelper.getPlanksUsed(theStack.stackTagCompound)/*ModificationNBTHelper.getPlanksUsed_Base(theStack.stackTagCompound)*/);
        Block theBlock = Block.blocksList[theStack.itemID];
        Icon[] icons = null;
        boolean overlay = (bytes[3] -1 ) != -1;
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
        Icon icOverlay = null;
        Icon icPlanks  = null;
        if(overlay)
        	icOverlay = Block.cloth.getIcon(0, bytes[3] -1);
    	if(plankUsed != null)
    		icPlanks = Block.blocksList[plankUsed.itemID].getIcon(0, plankUsed.getItemDamage());
    	if(icOverlay == null)
    		icOverlay = Block.cloth.getIcon(0, 0);
    	if(icPlanks == null)
    		icPlanks = Block.planks.getIcon(0, 0);
    	renderBlocks.renderFaceYPos(theBlock, 0.0D, 0.0D, 0.0D, overlay ? icOverlay : icPlanks);
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
        renderBlocks.setOverrideBlockTexture(icPlanks);
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
            renderBlocks.renderMaxY = 0.24997111D;
            renderBlocks.renderMaxX = 0.755D;
            renderBlocks.renderFaceZNeg(theBlock, 0.121D, 0.7502999D, 0.001D, icOverlay/*Block.cloth.getIcon(0, bytes[3])*/);
            renderBlocks.renderFaceZPos(theBlock, 0.121D, 0.7502999D, -0.001D, icOverlay/*Block.cloth.getIcon(0, bytes[3])*/);
            renderBlocks.renderMaxX = 1.0D;
            renderBlocks.renderMaxZ = 0.755D;
            renderBlocks.renderFaceXNeg(theBlock, 0.001D, 0.750D, 0.121D, icOverlay/*Block.cloth.getIcon(0, bytes[3])*/);
            renderBlocks.renderFaceXPos(theBlock, -0.001D, 0.750D, 0.121D, icOverlay/*Block.cloth.getIcon(0, bytes[3])*/);
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
        renderBlocks.setOverrideBlockTexture(icPlanks);
        renderBlocks.renderFaceZPos(theBlock, 0.0D, 0.0D, -0.01D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 3, i));
        tessellator.draw();
        renderBlocks.clearOverrideBlockTexture();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXNeg(theBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 4, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderBlocks.setOverrideBlockTexture(icPlanks);
        renderBlocks.renderFaceXNeg(theBlock, 0.01D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 4, i));
        tessellator.draw();
        renderBlocks.clearOverrideBlockTexture();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXPos(theBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 5, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.setOverrideBlockTexture(icPlanks);
        renderBlocks.renderFaceXPos(theBlock, -0.01D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(theBlock, 4, i));
        tessellator.draw();
        renderBlocks.clearOverrideBlockTexture();
        
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
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
		int meta = world.getBlockMetadata(x, y, z);
		switch(world.getBlockMetadata(x, y, z)){
		case 1: return renderModdedCraftingTableInWorld(world, x, y, z, block, renderer);
		case 2: return renderProjectBenchInWorld(world, x, y, z, block, renderer);
		default: return false;
		}
	}
	
	private boolean renderModdedCraftingTableInWorld(IBlockAccess world, int x,
			int y, int z, Block block, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		int l = block.getMixedBrightnessForBlock(world, x, y, z);

		ItemStack planks = ((TileEntityModdedTable)world.getBlockTileEntity(x, y, z)).getModifications().getPlanks();
        if(planks == null)
        	planks = new ItemStack(Block.planks.blockID, 1, 0);
        Block planksBlock = Block.blocksList[planks.itemID];
        if(planksBlock == null)
        	planksBlock = Block.planks;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.1F);
        
        //Wrapping plank texture around middle thing
        renderBlocks.renderMinY = 0.125D;
        renderBlocks.renderMaxY = 0.75D;
        renderBlocks.renderMaxX = 0.7D;
        renderBlocks.renderMaxZ = 0.78D;
        renderBlocks.renderMinZ = 0.22D;
        Icon plankIcon = planksBlock.getIcon(0, planks.getItemDamage());
        tessellator.setBrightness(renderBlocks.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(world, x, y, z));
        tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    	renderBlocks.renderFaceXPos(block, (double)x + 0.080F, (double)y, (double)z, plankIcon);
    	renderBlocks.renderFaceXNeg(block, (double)x + 0.22F, (double)y, (double)z, plankIcon);
        renderBlocks.renderMinY = 0.0D;
        renderBlocks.renderMaxY = 1.0D;
        renderBlocks.renderMaxX = 1.0D;
        renderBlocks.renderMaxZ = 1.0D;
        renderBlocks.renderMinZ = 0.0D;
        renderBlocks.renderMinY = 0.125D;
        renderBlocks.renderMaxY = 0.75D;
        renderBlocks.renderMaxZ = 0.7D;
        renderBlocks.renderMaxX = 0.78D;
        renderBlocks.renderMinX = 0.22D;
    	renderBlocks.renderFaceZPos(block, (double)x, (double)y, (double)z + 0.080F, plankIcon);
    	renderBlocks.renderFaceZNeg(block, (double)x, (double)y, (double)z + 0.22F, plankIcon);
        renderBlocks.renderMinY = 0.0D;
        renderBlocks.renderMaxY = 1.0D;
        renderBlocks.renderMaxZ = 1.0D;
        renderBlocks.renderMaxX = 1.0D;
        renderBlocks.renderMinX = 0.0D;
        renderBlocks.renderMinX = 0.2D;
        renderBlocks.renderMaxX = 0.8D;
        renderBlocks.renderMinZ = 0.2D;
        renderBlocks.renderMaxZ = 0.8D;
        renderBlocks.renderFaceYPos(block, (double)x, (double)y -0.25F, (double)z, plankIcon);
        renderBlocks.renderMinX = 0.0D;
        renderBlocks.renderMaxX = 1.0D;
        renderBlocks.renderMinZ = 0.0D;
        renderBlocks.renderMaxZ = 1.0D;
        renderBlocks.renderFaceYPos(block, (double)x, (double)y -0.0003F, (double)z, plankIcon);
        
//        renderBlocks.renderMinX = 1.0D;
		
		return false;
	}

	private boolean renderProjectBenchInWorld(IBlockAccess world, int x, int y,
			int z, Block block, RenderBlocks renderer) {
		renderer.renderStandardBlock(block, x, y, z)/*(block, x, y, z, 1.0F, 1.0F, 1.0F)*/;
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
      	TileEntityProjectBench tpb = (TileEntityProjectBench)world.getBlockTileEntity(x, y, z);
        boolean overlay = tpb.getModifications().color() > 0;
        
        Icon[] icons = null;
        if(block instanceof BlockCrafting)
        	icons = ((BlockCrafting)block).icons;
        ItemStack planks = ((TileEntityProjectBench)world.getBlockTileEntity(x, y, z)).getModifications().getPlanks();
        if(planks == null)
        	planks = new ItemStack(Block.planks.blockID, 1, 0);
        Block planksBlock = Block.blocksList[planks.itemID];
        if(planksBlock == null)
        	planksBlock = Block.planks;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.1F);
        
        Icon plankIcon = planksBlock.getIcon(0, planks.getItemDamage());
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
        	Icon wool = Block.cloth.getIcon(0, tpb.getModifications().color() -1);
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
            renderBlocks.renderFaceYPos(block, (double)x, (double)y -0.001, (double)z, plankIcon);
        }
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return CraftingSuite.proxy.getRenderID();
	}
}
