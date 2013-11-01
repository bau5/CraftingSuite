package bau5.mods.craftingsuite.common.recipe.modification;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import bau5.mods.craftingsuite.common.ModificationStackHelper;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;

public class ModificationRecipeBasic implements IModRecipe{
	private ItemStack[] recipeStacks;
	private ItemStack   result;
	public ModificationRecipeBasic(ItemStack result, ItemStack[] stacks){
		recipeStacks = stacks;
		this.result = result;
	}
	
	@Override
	public ItemStack getOutput(){
		return result;
	}
	
	@Override
	public ItemStack getExactOutput(ItemStack[] input){
		ItemStack stack = ModificationStackHelper.makeItemStack(result, input);
		return stack;
	}
	
	@Override
	public ItemStack[] doesRecipeMatch(ItemStack[] provided){
		if(provided.length != recipeStacks.length)
			return null;
		ItemStack[] used = new ItemStack[recipeStacks.length];
		boolean results[] = new boolean[recipeStacks.length];
		int i = 0;
		for(ItemStack component : recipeStacks){
			inner : for(ItemStack input : provided){
				if(input == null)
					continue inner;
				int idi = OreDictionary.getOreID(input);
				int idc = OreDictionary.getOreID(component);
				if(OreDictionary.itemMatches(component, input, false)
						|| (idi != -1 && idi == idc)){
					if(component.stackSize <= input.stackSize){
						ItemStack theStack = input.copy();
						theStack.stackSize = component.stackSize;
						used[i] = theStack;
						results[i++] = true; 
						break;
					}else
						results[i] = false;
				}else{
					results[i] = false;
				}
			}
		}
		boolean flag = false;
		for(boolean result : results){
			flag = result;
			if(!flag)
				return null;
		}
		return used;
	}
	
	@Override
	public boolean consumeItems(TileEntityModificationTable tile){
		boolean flag = true;
		for(ItemStack component : recipeStacks){
			for(int i = 0; i < tile.getSizeInventory(); i++){
				int idi = OreDictionary.getOreID(tile.getStackInSlot(i));
				int idc = OreDictionary.getOreID(component);
				if(OreDictionary.itemMatches(component, tile.getStackInSlot(i), false)
						|| (idi != -1 && idi == idc)){
					tile.decrStackSize(i, component.stackSize);
					break;
				}else
					flag = false;
			}
		}
		return flag;
	}
}
