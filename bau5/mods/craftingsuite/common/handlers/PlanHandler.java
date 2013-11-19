package bau5.mods.craftingsuite.common.handlers;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import bau5.mods.craftingsuite.common.helpers.ItemHelper;
import bau5.mods.craftingsuite.common.inventory.ContainerBase;
import bau5.mods.craftingsuite.common.inventory.LocalInventoryCrafting;
import bau5.mods.craftingsuite.common.inventory.SlotPlan;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;

public class PlanHandler implements IModifierHandler {
	private SlotPlan planSlot;
	public ContainerBase container;
	
	public PlanHandler(ContainerBase cont, SlotPlan slot) {
		container = cont;
		planSlot  = slot;
	}

	@Override
	public ItemStack handleSlotClick(int slot, int clickType, int clickMeta,
			EntityPlayer player) {
		if(slot == 0 && planSlot.getHasStack() && planSlot.getStack().stackTagCompound != null){
			LocalInventoryCrafting fromPlan = container.getTileEntity().getInventoryHandler().getCraftingMatrix();
			LocalInventoryCrafting copy     = fromPlan.copyInventory();
			NBTTagList list = planSlot.getStack().stackTagCompound.getTagList("Components");
			if(list != null){
				for(int i = 0; i < fromPlan.getSizeInventory(); i++){
					ItemStack stack = ItemStack.loadItemStackFromNBT((NBTTagCompound)list.tagAt(i));
					fromPlan.setInventorySlotContents(i, stack);
				}
			}
			InventoryBasic basic = new InventoryBasic("asdf", true, 18);
			for(int i = 0; i < basic.getSizeInventory(); i++){
				ItemStack stack = container.getTileEntity().getInventoryHandler().getStackInSlot(i + 9);
				if(stack != null)
					stack = stack.copy();
				basic.setInventorySlotContents(i, stack);
			}
			boolean flag = false;
			for(int matrixIndex = 0; matrixIndex < 9; matrixIndex++){
				flag = false;
				ItemStack component = fromPlan.getStackInSlot(matrixIndex);
				if(component == null)
					continue;
				for(int supplyIndex = 0; supplyIndex < 18; supplyIndex++){
					ItemStack supplyStack = basic.getStackInSlot(supplyIndex);
					if(supplyStack == null)
						continue;
					if(OreDictionary.itemMatches(component, supplyStack, false) || ItemHelper.checkOreDictMatch(component, supplyStack)/*ItemHelper.checkItemMatch(supplyStack, component)*/){
						supplyStack.stackSize -= 1;
						if(supplyStack.stackSize == 0)
							basic.setInventorySlotContents(supplyIndex, null);
						flag = true;
						break;
					}
				}
				if(!flag){
					System.out.println("failed");
					return null;
				}
			}
			ItemStack handledStack = container.slotClick_plain(slot, clickType, clickMeta, player);
			container.getTileEntity().getInventoryHandler().setCraftingMatrix(copy);
			for(int i = 0; i < copy.getSizeInventory(); i++){
	        	container.getTileEntity().getInventoryHandler().setInventorySlotContents(i, copy.getStackInSlot(i));
	        }
			return handledStack;
		}		
		ItemStack handledStack = container.slotClick_plain(slot, clickType, clickMeta, player);
		if(slot == planSlot.slotNumber){
			container.modifiedTile.getInventoryHandler().findRecipe(true);
		}
		return handledStack;
	}

	@Override
	public ItemStack handleTransferClick(EntityPlayer par1EntityPlayer, int par2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean handleCraftingPiece(ItemStack neededStack, boolean metaSens) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void writePlanToStack(){
		IModifiedTileEntityProvider tile = container.modifiedTile;
		if(tile.getInventoryHandler().getResult() != null){
			NBTTagCompound stackTag = new NBTTagCompound("tag");
			NBTTagList list = new NBTTagList();
			LocalInventoryCrafting matrix = tile.getInventoryHandler().getCraftingMatrix();

			boolean oreRecFlag = false;
			ItemStack result = CraftingManager.getInstance().findMatchingRecipe(matrix, ((TileEntity)tile).worldObj);
			IRecipe recipe = findMatchingRecipe(matrix, ((TileEntity)tile).worldObj);
			if(recipe != null && result != null && recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessOreRecipe){
				oreRecFlag = true;
			}
			
			for(int i = 0; i < matrix.getSizeInventory(); i++){
				NBTTagCompound tag = new NBTTagCompound();
				ItemStack stack = matrix.getStackInSlot(i);
				if(stack != null){
					stack = stack.copy();
					if(oreRecFlag){
						int id = OreDictionary.getOreID(stack);
						if(id != -1){
							stack = OreDictionary.getOres(id).get(0).copy();
							stack.setItemDamage(OreDictionary.WILDCARD_VALUE);
						}
					}
					stack.stackSize = 1;
					tag = stack.writeToNBT(tag);
				}
				list.appendTag(tag);
			}
			
			stackTag.setTag("Components", list);
			stackTag.setTag("Result", tile.getInventoryHandler().getResult().writeToNBT(new NBTTagCompound()));
			planSlot.getStack().setTagCompound(stackTag);
			planSlot.getStack().setItemDamage(1);
		}
	}

	@Override
	public boolean handlesSlotClicks() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean handlesTransfers() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean handlesCrafting() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean validPlanInSlot() {
		ItemStack plan = planSlot.getStack();
		return plan != null && plan.stackTagCompound != null && plan.stackTagCompound.hasKey("Components");
	}
	
	public boolean blankPlanInSlot() {
		ItemStack plan = planSlot.getStack();
		return plan != null && plan.stackTagCompound == null && plan.stackSize == 1;
	}
	
	public ItemStack[] getPlanStacks(){
		if(!planSlot.getHasStack())
			return null;
		ItemStack[] stacks = new ItemStack[9];
		NBTTagList list = planSlot.getStack().stackTagCompound.getTagList("Components");
		for(int i = 0; i < list.tagCount(); i++){
			ItemStack stack = ItemStack.loadItemStackFromNBT((NBTTagCompound)list.tagAt(i));
			stacks[i] = stack;
		}
		return stacks;
	}
	
    public IRecipe findMatchingRecipe(InventoryCrafting par1InventoryCrafting, World par2World)
    {
    	List recipes = CraftingManager.getInstance().getRecipeList();
        int i = 0;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;
        int j;

        for (j = 0; j < par1InventoryCrafting.getSizeInventory(); ++j)
        {
            ItemStack itemstack2 = par1InventoryCrafting.getStackInSlot(j);

            if (itemstack2 != null)
            {
                if (i == 0)
                {
                    itemstack = itemstack2;
                }

                if (i == 1)
                {
                    itemstack1 = itemstack2;
                }

                ++i;
            }
        }

        if (i == 2 && itemstack.itemID == itemstack1.itemID && itemstack.stackSize == 1 && itemstack1.stackSize == 1 && Item.itemsList[itemstack.itemID].isRepairable())
        {
            Item item = Item.itemsList[itemstack.itemID];
            int k = item.getMaxDamage() - itemstack.getItemDamageForDisplay();
            int l = item.getMaxDamage() - itemstack1.getItemDamageForDisplay();
            int i1 = k + l + item.getMaxDamage() * 5 / 100;
            int j1 = item.getMaxDamage() - i1;

            if (j1 < 0)
            {
                j1 = 0;
            }
            return null;
        }
        else
        {
            for (j = 0; j < recipes.size(); ++j)
            {
                IRecipe irecipe = (IRecipe)recipes.get(j);

                if (irecipe.matches(par1InventoryCrafting, par2World))
                {
                    return irecipe;
                }
            }

            return null;
        }
    }
}
