package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import bau5.mods.craftingsuite.common.CSLogger;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench.LocalInventoryCrafting;
import cpw.mods.fml.common.registry.GameRegistry;

public class SlotPBCrafting extends SlotCrafting {

	public TileEntityProjectBench tileEntity;
	
	private final IInventory craftingMatrix;
	private EntityPlayer thePlayer;
	
	public SlotPBCrafting(EntityPlayer player, 
			IInventory craftMatrix, IInventory slotInventory, int slotID,
			int xDisplay, int yDisplay) {
		super(player, craftMatrix, slotInventory, slotID, xDisplay, yDisplay);
		tileEntity = (TileEntityProjectBench)craftMatrix;
		craftingMatrix = craftMatrix;
		thePlayer = player;
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer player,
			ItemStack stack) {
		tileEntity.containerWorking = true;
		boolean found = false;
		boolean metaSens = false;
//		LocalInventoryCrafting crafting = tileEntity.getCopyOfMatrix(tileEntity.craftingMatrix);
//        GameRegistry.onItemCrafted(thePlayer, stack, crafting/*tileEntity.craftingMatrix*/);
		GameRegistry.onItemCrafted(thePlayer, stack, tileEntity.craftingMatrix);	
        this.onCrafting(stack);
//        for(int i = 0; i < crafting.getSizeInventory(); i++){
//        	ItemStack stack1 = crafting.getStackInSlot(i);
//        	ItemStack stack2 = tileEntity.craftingMatrix.getStackInSlot(i);
//        	ItemStack stack3 = tileEntity.getStackInSlot(i);
//        	CSLogger.log("  Pre process");
//        	CSLogger.log("   Copy: "  +stack1);
//        	CSLogger.log("   TileM: " +stack2); 
//        	CSLogger.log("   TileI: " +stack3);
//        	if(!ItemStack.areItemStacksEqual(stack1, stack2)){
//        		tileEntity.setInventorySlotContents(i, stack1);
//        		toolStack = stack1;
//        		toolSlot = i;
//        	}
//        	CSLogger.log("  Post process");
//        	CSLogger.log("   Copy: "  +stack1);
//        	CSLogger.log("   TileM: " +stack2); 
//        	CSLogger.log("   TileI: " +stack3);
//        }

        //Looping through crafting matrix finding required items
        for(int invIndex = 0; invIndex < 9; invIndex++)
        {
        	metaSens = false;
        	found = false;
        	//Grabs the item for comparison
//        	ItemStack craftComponentStack = tileEntity.getStackInSlot(invIndex);
        	ItemStack craftComponentStack = tileEntity.craftingMatrix.getStackInSlot(invIndex);
        	if(craftComponentStack != null)
        	{
        		if(!craftComponentStack.isItemStackDamageable() && craftComponentStack.getMaxDamage() == 0
        				&& craftComponentStack.itemID != Block.planks.blockID
        				&& craftComponentStack.itemID != Block.cloth.blockID
        				&& craftComponentStack.itemID != Block.leaves.blockID)
				{
					metaSens = true;
				}
        		//Consume extras in crafting matrix
        		for(int craftInv = 0; craftInv < 9; craftInv++){
//        			ItemStack craftStack = tileEntity.getStackInSlot(craftInv);
        			ItemStack craftStack = tileEntity.craftingMatrix.getStackInSlot(craftInv);
        			if(craftStack != null && craftStack.stackSize > 1){
        				if(craftStack.getItem().equals(craftComponentStack.getItem()) && craftComponentStack.stackSize <= craftStack.stackSize){
	        				if(metaSens)
	    					{
	    						if(craftComponentStack.getItemDamage() != craftStack.getItemDamage())
	    						{
	    							continue;
	    						} else 
	    						{
	    							tileEntity.craftingMatrix.decrStackSize(craftInv, 1);
	            					found = true;
	    						}
	    					}
	    					else
	    					{
//	        					tileEntity.decrStackSize(craftInv, 1);
	    						tileEntity.craftingMatrix.decrStackSize(craftInv, 1);
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
//	                                if (this.craftingMatrix.getStackInSlot(invIndex) == null)
//	                                {
//	                                    this.craftingMatrix.setInventorySlotContents(invIndex, conStack);
//	                                }
	                            	if (tileEntity.craftingMatrix.getStackInSlot(invIndex) == null)
	                                {
	                            		tileEntity.craftingMatrix.setInventorySlotContents(invIndex, conStack);
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
        		//Checking the supply inventory for matching item
        		if(!found){
	    			for(int supplyInv = 9; supplyInv < 27; supplyInv++)
					{
		    			//Grabs the item in the supply Matrix
		    			ItemStack supplyMatrixStack = tileEntity.getStackInSlot(supplyInv);
		    			if(supplyMatrixStack != null)
		    			{
		    				if(supplyMatrixStack.getItem().equals(craftComponentStack.getItem()))
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
        			tileEntity.craftingMatrix.decrStackSize(invIndex, 1);
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
//                            if (this.craftingMatrix.getStackInSlot(invIndex) == null)
//                            {
//                                this.craftingMatrix.setInventorySlotContents(invIndex, conStack);
//                            } 
                            if (tileEntity.craftingMatrix.getStackInSlot(invIndex) == null)
                            {
                            	tileEntity.craftingMatrix.setInventorySlotContents(invIndex, conStack);
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
//        for(int i = 0; i < 9; i++){
//        	CSLogger.log("Copy: " +crafting.getStackInSlot(i));
//        	CSLogger.log("TileM: " +tileEntity.craftingMatrix.getStackInSlot(i));
//        	CSLogger.log("TileI: " +tileEntity.getStackInSlot(i));
//        }
//        if(tileEntity.selectedToolIndex != -1){
//        	ItemStack toolCopy = null;
//        	if(toolStack != null){
//        		toolCopy = toolStack.copy();
//	        	tileEntity.setInventorySlotContents(toolSlot, null);
//	        	tileEntity.setInventorySlotContents(tileEntity.getToolModifierInvIndex() + tileEntity.selectedToolIndex, toolCopy);
//        	}else{
//        		toolCopy = crafting.getStackInSlot(tileEntity.toolIndexInCrafting);
//        		CSLogger.log(toolCopy);
//        		if(toolCopy != null){
//        			CSLogger.log("Got tool from slot" + tileEntity.toolIndexInCrafting);
//        			toolCopy = toolCopy.copy();
//        			tileEntity.setInventorySlotContents(tileEntity.selectedToolIndex, toolCopy);
//        			tileEntity.setInventorySlotContents(tileEntity.toolIndexInCrafting, null);
//        			tileEntity.toolIndexInCrafting = -1;
//        		}
//        	}
//        }
        if(tileEntity.getSelectedToolIndex() != -1 && tileEntity.getInventoryModifier() == EnumInventoryModifier.TOOLS){
        	ItemStack toolCopy = tileEntity.craftingMatrix.getStackInSlot(tileEntity.toolIndexInCrafting);
        	CSLogger.log("Copying " +toolCopy +" from crafting slot " +tileEntity.toolIndexInCrafting +" to tool slot " +tileEntity.getSelectedToolIndex());
        	tileEntity.tools[tileEntity.selectedToolIndex] = toolCopy;
        	tileEntity.setInventorySlotContents(tileEntity.getSelectedToolIndex() + tileEntity.getToolModifierInvIndex(), toolCopy);
        	tileEntity.craftingMatrix.setInventorySlotContents(tileEntity.toolIndexInCrafting, null);
			tileEntity.toolIndexInCrafting = -1;
        	if(toolCopy == null)
        		tileEntity.selectedToolIndex = -1;
        }
        for(int i = 0; i < tileEntity.craftingMatrix.getSizeInventory(); i++){
        	tileEntity.setInventorySlotContents(i, tileEntity.craftingMatrix.getStackInSlot(i));
        }
		tileEntity.containerWorking = false;
	}
}
