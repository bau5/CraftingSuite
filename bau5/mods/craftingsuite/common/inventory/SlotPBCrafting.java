package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
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
        GameRegistry.onItemCrafted(thePlayer, stack, craftingMatrix);
        this.onCrafting(stack);
        
        //Looping through crafting matrix finding required items
        for(int invIndex = 0; invIndex < 9; invIndex++)
        {
        	metaSens = false;
        	found = false;
        	//Grabs the item for comparison
        	ItemStack craftComponentStack = tileEntity.getStackInSlot(invIndex);
        	if(craftComponentStack != null)
        	{
        		if(!craftComponentStack.isItemStackDamageable() && craftComponentStack.getMaxDamage() == 0
        				&& craftComponentStack.itemID != Block.planks.blockID
        				&& craftComponentStack.itemID != Block.cloth.blockID
        				&& craftComponentStack.itemID != Block.leaves.blockID)
				{
					metaSens = true;
				}
        		//Checking the supply inventory for matching item
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
        		//Didn't find it in the supply inventory, remove from crafting matrix
    			if(!found)
        		{
        			tileEntity.decrStackSize(invIndex, 1);
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
                            if (this.craftingMatrix.getStackInSlot(invIndex) == null)
                            {
                                this.craftingMatrix.setInventorySlotContents(invIndex, conStack);
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
		tileEntity.containerWorking = false;
	}
}
