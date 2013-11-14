package bau5.mods.craftingsuite.common.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import bau5.mods.craftingsuite.common.handlers.IModifierHandler;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModdedTable;
import cpw.mods.fml.common.registry.GameRegistry;

public class SlotMTCrafting extends SlotCrafting {
	
	private IModifierHandler handler;
	private TileEntityModdedTable tileEntity;
	
	
	public SlotMTCrafting(EntityPlayer par1EntityPlayer, TileEntityModdedTable tile,
			IInventory craftMatrix, IInventory par3iInventory, int par4,
			int par5, int par6, IModifierHandler hndlr) {
		super(par1EntityPlayer, craftMatrix, par3iInventory, par4, par5, par6);
		tileEntity = tile;
		handler = hndlr;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player,
			ItemStack itemstack) {
		 //Looping through crafting matrix finding required items
		boolean found = false;
        GameRegistry.onItemCrafted(player, itemstack, tileEntity.getInventoryHandler());
        this.onCrafting(itemstack);
		
        if(handler.handlesCrafting()){
	        for(int invIndex = 0; invIndex < 9; invIndex++)
	        {
	        	boolean metaSens = false;
	        	found = false;
	        	//Grabs the item for comparison
	        	ItemStack craftComponentStack = tileEntity.getInventoryHandler().getStackInSlot(invIndex);
	        	if(craftComponentStack != null)
	        	{
	        		if(!craftComponentStack.isItemStackDamageable() && craftComponentStack.getMaxDamage() == 0
	        				&& (itemstack.itemID == Block.stairsWoodBirch.blockID
	        				|| itemstack.itemID == Block.stairsWoodOak.blockID
	        				|| itemstack.itemID == Block.stairsWoodJungle.blockID
	        				|| itemstack.itemID == Block.stairsWoodSpruce.blockID
	        				|| itemstack.itemID == Block.stairsStoneBrick.blockID)
	        				|| (craftComponentStack.itemID != Block.planks.blockID
	        				&& craftComponentStack.itemID != Block.cloth.blockID
	        				&& craftComponentStack.itemID != Block.leaves.blockID)
	        				)
					{
						metaSens = true;
					}
	        		//Consume extras in crafting matrix
	        		for(int craftInv = 0; craftInv < 9; craftInv++){
	        			ItemStack craftStack = tileEntity.getInventoryHandler().getStackInSlot(craftInv);
	        			if(craftStack != null && craftStack.stackSize > 1){
	        				if(craftStack.getItem().equals(craftComponentStack.getItem()) && craftComponentStack.stackSize <= craftStack.stackSize){
		        				if(metaSens)
		    					{
		    						if(craftComponentStack.getItemDamage() != craftStack.getItemDamage())
		    						{
		    							continue;
		    						} else 
		    						{
		    							tileEntity.getInventoryHandler().decrStackSize(craftInv, 1);
		            					found = true;
		    						}
		    					}
		    					else
		    					{
		    						tileEntity.getInventoryHandler().decrStackSize(craftInv, 1);
		        					found = true;
		    					}
		        				
		        				if (craftComponentStack.getItem().hasContainerItem())
		                        {
		                            ItemStack conStack = craftComponentStack.getItem().getContainerItemStack(craftComponentStack);
		                            
		                            if (conStack.isItemStackDamageable() && conStack.getItemDamage() > conStack.getMaxDamage())
		                            {
		                                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, conStack));
		                                conStack = null;
		                            }
	
		                            if (conStack != null && (!craftComponentStack.getItem().doesContainerItemLeaveCraftingGrid(craftComponentStack) || !player.inventory.addItemStackToInventory(conStack)))
		                            {
		                            	if (tileEntity.getInventoryHandler().getStackInSlot(invIndex) == null)
		                                {
		                            		tileEntity.getInventoryHandler().setInventorySlotContents(invIndex, conStack);
		                                }
		                                else
		                                {
		                                    player.dropPlayerItem(conStack);
		                                }
		                            }
		                        }
		        				if(found)
		        					break;
	        				}
	        			}
	        		}
		            if(!found && handler.handlesCrafting())
		            	found = handler.handleCraftingPiece(craftComponentStack, metaSens);
		            if(!found){
		            	defaultCrafting(player, itemstack, invIndex);
		            }
	        	}
	        }
        }else{
        	for(int i = 0; i < 9; i++){
        		ItemStack stack = this.tileEntity.getInventoryHandler().getStackInSlot(i);
        		defaultCrafting(player, stack, i);
        	}
        }
	}
        
    public void defaultCrafting(EntityPlayer player, ItemStack stack, int index)
    {
        if (stack != null)
        {
            this.tileEntity.getInventoryHandler().decrStackSize(index, 1);

            if (stack.getItem().hasContainerItem())
            {
                ItemStack itemstack2 = stack.getItem().getContainerItemStack(stack);

                if (itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage())
                {
                    MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, itemstack2));
                    itemstack2 = null;
                }

                if (itemstack2 != null && (!stack.getItem().doesContainerItemLeaveCraftingGrid(stack) || !player.inventory.addItemStackToInventory(itemstack2)))
                {
                    if (this.tileEntity.getInventoryHandler().getStackInSlot(index) == null)
                    {
                        this.tileEntity.getInventoryHandler().setInventorySlotContents(index, itemstack2);
                    }
                    else
                    {
                        player.dropPlayerItem(itemstack2);
                    }
                }
            }
        }
    }
}
