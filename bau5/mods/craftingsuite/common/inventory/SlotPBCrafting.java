package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import bau5.mods.craftingsuite.common.CSLogger;
import bau5.mods.craftingsuite.common.handlers.IModifierHandler;
import bau5.mods.craftingsuite.common.helpers.ItemHelper;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench.PositionedFluidStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class SlotPBCrafting extends SlotCrafting {

	public TileEntityProjectBench tileEntity;
	
	private final IInventory craftingMatrix;
	private EntityPlayer thePlayer;
	public IModifierHandler handler;
	
	public SlotPBCrafting(EntityPlayer player, 
			IInventory craftMatrix, IModifierHandler hndlr, IInventory slotInventory, int slotID,
			int xDisplay, int yDisplay) {
		super(player, craftMatrix, slotInventory, slotID, xDisplay, yDisplay);
		tileEntity = (TileEntityProjectBench)craftMatrix;
		handler = hndlr;
		craftingMatrix = craftMatrix;
		thePlayer = player;
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer player,
			ItemStack stack) {
		LocalInventoryCrafting craftingMatrix = tileEntity.inventoryHandler.getCraftingMatrix();
		tileEntity.containerWorking = true;
		boolean found = false;
		boolean metaSens = false;
		boolean once = true;
		GameRegistry.onItemCrafted(thePlayer, stack, craftingMatrix);	
        this.onCrafting(stack);

        //Looping through crafting matrix finding required items
        for(int invIndex = 0; invIndex < 9; invIndex++)
        {
        	metaSens = false;
        	found = false;
        	//Grabs the item for comparison
//        	ItemStack craftComponentStack = tileEntity.craftingMatrix.getStackInSlot(invIndex);
        	ItemStack craftComponentStack = craftingMatrix.getStackInSlot(invIndex);
        	if(craftComponentStack != null)
        	{
        		if(craftComponentStack.getItemDamage() != OreDictionary.WILDCARD_VALUE
        				&& !craftComponentStack.isItemStackDamageable() && craftComponentStack.getMaxDamage() == 0
        				&& craftComponentStack.itemID != Block.planks.blockID
        				&& craftComponentStack.itemID != Block.cloth.blockID
        				&& craftComponentStack.itemID != Block.leaves.blockID)
				{
					metaSens = true;
				}
        		//Consume extras in crafting matrix
        		for(int craftInv = 0; craftInv < 9; craftInv++){
//        			ItemStack craftStack = tileEntity.craftingMatrix.getStackInSlot(craftInv);
        			ItemStack craftStack = craftingMatrix.getStackInSlot(craftInv);
        			if(craftStack != null && craftStack.stackSize > 1){
        				if(craftStack.getItem().equals(craftComponentStack.getItem()) && craftComponentStack.stackSize <= craftStack.stackSize){
	        				if(metaSens)
	    					{
	    						if(craftComponentStack.getItemDamage() != craftStack.getItemDamage())
	    						{
	    							continue;
	    						} else 
	    						{
//	    							tileEntity.craftingMatrix.decrStackSize(craftInv, 1);
	    							craftingMatrix.decrStackSize(craftInv, 1);
	            					found = true;
	    						}
	    					}
	    					else
	    					{
	    						craftingMatrix.decrStackSize(craftInv, 1);
//	    						tileEntity.craftingMatrix.decrStackSize(craftInv, 1);
	        					found = true;
	    					}
	        				
	        				if (craftComponentStack.getItem().hasContainerItem())
	                        {
	                            ItemStack conStack = craftComponentStack.getItem().getContainerItemStack(craftComponentStack);
	                            
	                            if (conStack.isItemStackDamageable() && conStack.getItemDamage() > conStack.getMaxDamage())
	                            {
	                                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, conStack));
	                                conStack = null;
	                            }

	                            if (conStack != null && (!craftComponentStack.getItem().doesContainerItemLeaveCraftingGrid(craftComponentStack) || !this.thePlayer.inventory.addItemStackToInventory(conStack)))
	                            {
//	                            	if (tileEntity.craftingMatrix.getStackInSlot(invIndex) == null)
	                            	if (craftingMatrix.getStackInSlot(invIndex) == null)
	                                {
	                            		craftingMatrix.setInventorySlotContents(invIndex, conStack);
	                                }
	                                else
	                                {
	                                    this.thePlayer.dropPlayerItem(conStack);
	                                }
	                            }
	                        }
	        				if(found)
	        					break;
        				}
        			}
        		}
	            if(!found && handler != null && handler.handlesCrafting())
	            	found = handler.handleCraftingPiece(craftComponentStack, metaSens);
	            if(!found){
	                if(tileEntity.hasFluidCapabilities() && !tileEntity.fluidForCrafting.isEmpty()){
	                	if(!found){
		                	for(PositionedFluidStack fluid : tileEntity.fluidForCrafting){
		                		if(fluid.isInUse() && craftComponentStack.getItem().equals(fluid.full.getItem())){
		                			FluidStack drained = tileEntity.drain(ForgeDirection.UP, fluid.fluid.copy(), true);
		                			if(drained.amount != fluid.fluid.amount){
		                				tileEntity.fill(ForgeDirection.UP, drained, true);
		                			}else{
		                				found = true;
		                				craftingMatrix.setInventorySlotContents(invIndex, null);
		                				fluid.resetInUse();
		                				break;
		                			}
		                		}
		                	}
	                	}
	                }
	            }
        		//Checking the supply inventory for matching item
        		if(!found){
	    			for(int supplyInv = 9; supplyInv < 27; supplyInv++)
					{
		    			//Grabs the item in the supply Matrix
		    			ItemStack supplyMatrixStack = tileEntity.getStackInSlot(supplyInv);
		    			if(supplyMatrixStack != null)
		    			{
		    				if(supplyMatrixStack.getItem().equals(craftComponentStack.getItem()) || 
		    						(craftComponentStack.getItemDamage() == OreDictionary.WILDCARD_VALUE &&
		    						 ItemHelper.checkOreDictMatch(craftComponentStack, supplyMatrixStack)))
		    				{
		    					if(metaSens)
		    					{
		    						if(craftComponentStack.getItemDamage() != supplyMatrixStack.getItemDamage())
		    						{
		    							continue;
		    						} else 
		    						{
		    							tileEntity.decrStackSize(supplyInv, 1);
		            					found = true;
		    						}
		    					}
		    					else
		    					{
		        					tileEntity.decrStackSize(supplyInv, 1);
		        					found = true;
		    					}
		    					//Found item!
		    					if (supplyMatrixStack.getItem().hasContainerItem())
				                {
				                    ItemStack contStack = supplyMatrixStack.getItem().getContainerItemStack(supplyMatrixStack);
				                    
				                    if (contStack.isItemStackDamageable() && contStack.getItemDamage() > contStack.getMaxDamage())
				                    {
				                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, contStack));
				                        contStack = null;
				                    }
		
				                    if (contStack != null && (!supplyMatrixStack.getItem().doesContainerItemLeaveCraftingGrid(supplyMatrixStack) || !this.thePlayer.inventory.addItemStackToInventory(contStack)))
				                    {
				                        if (this.craftingMatrix.getStackInSlot(supplyInv) == null)
				                        {
				                            this.craftingMatrix.setInventorySlotContents(supplyInv, contStack);
				                        }
				                        else
				                        {
				                            this.thePlayer.dropPlayerItem(contStack);
				                        }
				                    }
				                }
		    					break;
		    				}
		    			}
		    		}
        		}
        		//Didn't find it in the supply inventory, remove from crafting matrix
    			if(!found)
        		{
        			craftingMatrix.decrStackSize(invIndex, 1);
        			if (craftComponentStack.getItem().hasContainerItem())
                    {
                        ItemStack conStack = craftComponentStack.getItem().getContainerItemStack(craftComponentStack);
                        
                        if (conStack.isItemStackDamageable() && conStack.getItemDamage() > conStack.getMaxDamage())
                        {
                            MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, conStack));
                            conStack = null;
                        }

                        if (conStack != null && (!craftComponentStack.getItem().doesContainerItemLeaveCraftingGrid(craftComponentStack) || !this.thePlayer.inventory.addItemStackToInventory(conStack)))
                        {
                            if (craftingMatrix.getStackInSlot(invIndex) == null)
                            {
                            	craftingMatrix.setInventorySlotContents(invIndex, conStack);
                            }
                            else
                            {
                                this.thePlayer.dropPlayerItem(conStack);
                            }
                        }
                    }
        		}
        	}
        }
        if(tileEntity.getSelectedToolIndex() != -1 && tileEntity.getInventoryHandler().toolIndexInCrafting != -1 && tileEntity.getInventoryModifier() == EnumInventoryModifier.TOOLS){
        	ItemStack toolCopy = craftingMatrix.getStackInSlot(tileEntity.inventoryHandler.toolIndexInCrafting);
        	CSLogger.log("Copying " +toolCopy +" from crafting slot " +tileEntity.inventoryHandler.toolIndexInCrafting +" to tool slot " +tileEntity.getSelectedToolIndex());
        	tileEntity.tools[tileEntity.inventoryHandler.selectedToolIndex] = toolCopy;
        	tileEntity.setInventorySlotContents(tileEntity.getSelectedToolIndex() + tileEntity.getToolModifierInvIndex(), toolCopy);
        	craftingMatrix.setInventorySlotContents(tileEntity.inventoryHandler.toolIndexInCrafting, null);
			tileEntity.inventoryHandler.toolIndexInCrafting = -1;
        	if(toolCopy == null)
        		tileEntity.inventoryHandler.selectedToolIndex = -1;
        }
        
        for(int i = 0; i < craftingMatrix.getSizeInventory(); i++){
        	tileEntity.setInventorySlotContents(i, craftingMatrix.getStackInSlot(i));
        }
//        if(tileEntity.getSelectedToolIndex() != -1 && tileEntity.getInventoryModifier() == EnumInventoryModifier.TOOLS){
//        	ItemStack toolCopy = tileEntity.craftingMatrix.getStackInSlot(tileEntity.toolIndexInCrafting);
//        	CSLogger.log("Copying " +toolCopy +" from crafting slot " +tileEntity.toolIndexInCrafting +" to tool slot " +tileEntity.getSelectedToolIndex());
//        	tileEntity.tools[tileEntity.selectedToolIndex] = toolCopy;
//        	tileEntity.setInventorySlotContents(tileEntity.getSelectedToolIndex() + tileEntity.getToolModifierInvIndex(), toolCopy);
//        	craftingMatrix.setInventorySlotContents(tileEntity.toolIndexInCrafting, null);
//			tileEntity.toolIndexInCrafting = -1;
//        	if(toolCopy == null)
//        		tileEntity.selectedToolIndex = -1;
//        }
//        for(int i = 0; i < craftingMatrix.getSizeInventory(); i++){
//        	tileEntity.setInventorySlotContents(i, craftingMatrix.getStackInSlot(i));
//        }
//        if(tileEntity.getSelectedToolIndex() != -1 && tileEntity.getInventoryModifier() == EnumInventoryModifier.TOOLS){
//        	ItemStack toolCopy = tileEntity.craftingMatrix.getStackInSlot(tileEntity.toolIndexInCrafting);
//        	CSLogger.log("Copying " +toolCopy +" from crafting slot " +tileEntity.toolIndexInCrafting +" to tool slot " +tileEntity.getSelectedToolIndex());
//        	tileEntity.tools[tileEntity.selectedToolIndex] = toolCopy;
//        	tileEntity.setInventorySlotContents(tileEntity.getSelectedToolIndex() + tileEntity.getToolModifierInvIndex(), toolCopy);
//        	tileEntity.craftingMatrix.setInventorySlotContents(tileEntity.toolIndexInCrafting, null);
//			tileEntity.toolIndexInCrafting = -1;
//        	if(toolCopy == null)
//        		tileEntity.selectedToolIndex = -1;
//        }
//        for(int i = 0; i < tileEntity.craftingMatrix.getSizeInventory(); i++){
//        	tileEntity.setInventorySlotContents(i, tileEntity.craftingMatrix.getStackInSlot(i));
//        }
		tileEntity.containerWorking = false;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return super.canTakeStack(par1EntityPlayer);
	}
}
