package bau5.mods.projectbench.common;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;
import bau5.mods.projectbench.client.ProjectBenchGui;
import cpw.mods.fml.client.FMLClientHandler;

/**
 * 
 * ContainerProjectBench
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class ContainerProjectBench extends Container
{
	public TileEntityProjectBench tileEntity;
	
	public IInventory craftSupplyMatrix;
	public int craftResultSlot = 0;
	public int planSlot = 27;
		
	public ContainerProjectBench(InventoryPlayer invPlayer, TileEntityProjectBench tpb)
	{
		tileEntity = tpb;
		craftSupplyMatrix = tileEntity.craftSupplyMatrix;
		addSlotToContainer(new SlotPBCrafting(this, invPlayer.player, tileEntity.craftMatrix, tileEntity.craftResult, 
										 tileEntity, craftResultSlot, 124, 35));
		addSlotToContainer(new SlotPBPlan(tileEntity, planSlot, 9, 35));
		layoutContainer(invPlayer, tileEntity);
		bindPlayerInventory(invPlayer);
		detectAndSendChanges();
		tileEntity.markShouldUpdate();
	}
	private void layoutContainer(InventoryPlayer invPlayer, TileEntityProjectBench tpb)
	{
		int row;
		int col;
		int index = -1;
		int counter = 0;
		Slot slot = null;

		for(row = 0; row < 3; row++)
		{
			for(col = 0; col < 3; col++)
			{
				slot = new Slot(tileEntity, ++index, 30 + col * 18, 17 + row * 18);
				addSlotToContainer(slot);
				counter++;
			}
		}
		
		for(row = 0; row < 2; row++)
		{
			for(col = 0; col < 9; col++)
			{
				if(row == 1)
				{
					slot = new Slot(tileEntity, 18 + col, 8 + col * 18, 
									(row * 2 - 1) + 77 + row * 18);
					addSlotToContainer(slot);
				} else
				{
					slot = new Slot(tileEntity, 9 + col, 8 + col * 18,
							77 + row * 18);
					addSlotToContainer(slot);
				}
				counter++;
			}
		}
	}
	protected void bindPlayerInventory(InventoryPlayer invPlayer) 
	{
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9,
											8 + j * 18, 84 + i * 18 + 37));
			}
		}
		for(int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142 + 37));
		}
	}
	public void updateCrafting(boolean flag){
		tileEntity.markShouldUpdate();
	}
	
	@Override
	public ItemStack slotClick(int slot, int clickType, int clickMeta, EntityPlayer player)
	{
		if(slot == 1 && clickMeta == 6)
			clickMeta = 0;
		if(slot <= 9 && slot > -1)
			updateCrafting(true);
		if(slot == 0  && validPlanInSlot() && planUseEnabled() && !tileEntity.worldObj.isRemote && !ItemStack.areItemStacksEqual(getPlanResult(), tileEntity.getResult())){
			tileEntity.setResult(getPlanResult());
		}
		if(slot == 0 && validPlanInSlot() && ItemStack.areItemStacksEqual(getPlanResult(), tileEntity.getResult()) && planUseEnabled()){
			ItemStack returnStack = null;
			findAndMoveComponents(slot, clickType, clickMeta, player);
			if(CraftingManager.getInstance().findMatchingRecipe(tileEntity.craftMatrix, tileEntity.worldObj) != null)
				returnStack = super.slotClick(slot, clickType, clickMeta, player);
			else{
				tileEntity.setResult(null);
				tileEntity.updateResultSlot();
			}
			if(!tileEntity.worldObj.isRemote)
				PBPacketHandler.completeEmptyOfMatrix((EntityPlayerMP)player);
			return returnStack;
		}
		ItemStack stack = super.slotClick(slot, clickType, clickMeta, player);
		return stack;
	}
	
	private void findAndMoveComponents(int slot, int clickType, int clickMeta, EntityPlayer player) {
		InventoryCrafting crafting = tileEntity.getPlanCraftingInventory();
		IInventory supply = tileEntity.getSupplyInventory();
		outer : for(int i = 0; i < crafting.getSizeInventory(); i++){
			inner : for(int j = 0; j <supply.getSizeInventory(); j++){
				ItemStack planStack = crafting.getStackInSlot(i);
				ItemStack supplyStack = supply.getStackInSlot(j);
				if(planStack == null)
					continue outer;
				if(supplyStack == null)
					continue inner;
				if(OreDictionary.itemMatches(supplyStack, planStack, false)){
					ItemStack realStack = supply.getStackInSlot(j);
					ItemStack stack = realStack.copy();
					stack.stackSize = 1;
					supply.decrStackSize(j, 1);
					if(supply.getStackInSlot(j) == null)
						tileEntity.shallowSet(j+9, null);
					ItemStack stackInMatrix = tileEntity.craftMatrix.getStackInSlot(i);
					if(stackInMatrix == null)
						tileEntity.craftMatrix.setInventorySlotContents(i, stack);
					break inner;
				}
			}
		}
	}
	
	public void writePlanToNBT() {
		if(!tileEntity.worldObj.isRemote)
			tileEntity.findRecipe(true);
		ItemStack thePlan = tileEntity.getStackInSlot(27);
		IInventory crafting = tileEntity.getLocalInventoryCrafting();
		
		NBTTagCompound stackTag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for(int i = 0; i < crafting.getSizeInventory(); i++){
			NBTTagCompound tag = new NBTTagCompound();
			ItemStack stack = crafting.getStackInSlot(i);
			if(stack != null)
				tag = stack.writeToNBT(tag);
			list.appendTag(tag);
		}
		stackTag.setTag("Components", list);
		stackTag.setTag("Result", tileEntity.getResult().writeToNBT(new NBTTagCompound()));
		getPlanStack().stackTagCompound = stackTag;
		getPlanStack().setItemDamage(1);
	}
	
	public ItemStack[] translateToOreDictionary(InventoryCrafting crafting){
		List recipes = CraftingManager.getInstance().getRecipeList();
		IRecipe rec = null;
		for (int j = 0; j < recipes.size(); ++j)
        {
            IRecipe irecipe = (IRecipe)recipes.get(j);

            if (irecipe.matches(crafting, tileEntity.worldObj))
            {
                rec = irecipe;
            }
        }
		ItemStack[]	stacksForRecipe = new ItemStack[9];
		if(rec != null){
			for(int i = 0; i < crafting.getSizeInventory(); i++){
//				if(OreDictionary.itemMatches(rec., input, strict))
			}
		}else{
			for(int i = 0; i < crafting.getSizeInventory(); i++){
				stacksForRecipe[i] = crafting.getStackInSlot(i);
			}
		}
		return stacksForRecipe;
	}
	
	public ItemStack getPlanStack(){
		return tileEntity.getPlanStack();
	}
	
	public ItemStack getPlanResult(){
		return tileEntity.getPlanResult();
	}

	public boolean validPlanInSlot(){
		return tileEntity.validPlanInSlot();
	}
	
	public boolean planUseEnabled(){
		for(int i = 0; i < 9; i++)
			if(tileEntity.getStackInSlot(i) != null)
				return false;
		return true;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) 
	{
		return tileEntity.isUseableByPlayer(player);
	}
	@Override
	public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack) {
		tileEntity.containerInit = true;
		super.putStacksInSlots(par1ArrayOfItemStack);
		tileEntity.containerInit = false;
		tileEntity.onInventoryChanged();
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int numSlot)
    {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(numSlot);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack2 = slot.getStack();
            stack = stack2.copy();
            
            if (numSlot == 0)
            {
                if (!this.mergeItemStack(stack2, 11, 55, true))
                {
                    return null;
                }
                updateCrafting(true);
            }
            //Merge crafting matrix item with supply matrix inventory
            else if(numSlot > 0 && numSlot <= 10)
            {
            	if(!this.mergeItemStack(stack2, 11, 29, false))
            	{
            		if(!this.mergeItemStack(stack2, 29, 65, false))
            		{
                		return null;
            		}
            	}
            	updateCrafting(true);
            }
            //Merge Supply matrix item with player inventory
            else if (numSlot >= 11 && numSlot <= 64)
            {
            	if(stack2.getItem().equals(ProjectBench.instance.projectBenchPlan)){
            		Slot slotPlan = (Slot)inventorySlots.get(1);
            		ItemStack stack3 = slotPlan.getStack();
            		if(stack3 == null){
            			if(!mergeItemStack(stack2, 1, 2, false))
            				return null;
            		}else{
            			if(mergeItemStack(stack3, 11, 65,false)){
            				slotPlan.putStack(null);
            				mergeItemStack(stack2, 1, 2, false);
            			}else 
            				return null;
            		}
            	}else if(numSlot > 28){
            		if(!mergeItemStack(stack2, 11, 29, false))
            			return null;
            	}else if (!this.mergeItemStack(stack2, 29, 64, false))
                {
                    return null;
                }
            }
            //Merge player inventory item with supply matrix
            else if (numSlot >= 28 && numSlot < 65)
            {
                if (!this.mergeItemStack(stack2, 11, 29, false))
                {
                    return null;
                }
            }

            if (stack2.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (stack2.stackSize == stack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, stack2);
        }

        return stack;
    }
}