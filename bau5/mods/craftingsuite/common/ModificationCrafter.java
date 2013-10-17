package bau5.mods.craftingsuite.common;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModificationCrafter {
	
	private static final ModificationCrafter instance = new ModificationCrafter();
	private ArrayList<ModificationRecipe> recipeList = new ArrayList<ModificationRecipe>();
	
	private ModificationCrafter(){
		initRecipes();
	}
	
	public ItemStack findRecipe(ItemStack[] input){
		input = filterNulls(input);
		ModificationRecipe recipe = null;
		for(ModificationRecipe rec : recipeList){
			if(rec.doesRecipeMatch(input)){
				recipe = rec;
				break;
			}
		}
		return recipe != null ? recipe.getExactOutput(input) : null;
	}
	
	public ItemStack[] filterNulls(ItemStack[] input){
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for(ItemStack stack : input)
			if(stack != null)
				stacks.add(stack);
		ItemStack[] array = new ItemStack[stacks.size()];
		int i = 0;
		for(ItemStack stack : stacks)
			array[i++] = stack;
		return array;
	}
	
	public void initRecipes(){
		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingBlock.blockID, 1, 1), new ItemStack[]{
			new ItemStack(Block.workbench), new ItemStack(CraftingSuite.modItems, 1, 0)
		}));
		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingBlock.blockID, 1, 1), new ItemStack[]{
			new ItemStack(Block.workbench), new ItemStack(CraftingSuite.modItems, 1, 0)
		}));
		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingBlock.blockID, 1, 1), new ItemStack[]{
			new ItemStack(Block.workbench), new ItemStack(CraftingSuite.modItems, 1, 0), new ItemStack(Block.carpet, 1, OreDictionary.WILDCARD_VALUE)
		}));
		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingBlock.blockID, 1, 2), new ItemStack[]{
			new ItemStack(Block.workbench), new ItemStack(CraftingSuite.modItems, 1, 0), new ItemStack(Block.chest)
		}));
	}
	
	
	public void addRecipe(ModificationRecipe rec){
		recipeList.add(rec);
	}
	
	public ArrayList getRecipeList(){
		return recipeList;
	}
	
	public static ModificationCrafter instance(){
		return instance;
	}
	
	public class ModificationRecipe{
		private ItemStack[] recipeStacks;
		private ItemStack   result;
		public ModificationRecipe(ItemStack result, ItemStack[] stacks){
			recipeStacks = stacks;
			this.result = result;
		}
		
		public ItemStack getOutput(){
			return result;
		}
		
		public ItemStack getExactOutput(ItemStack[] input){
			ItemStack stack = ModificationStackHelper.makeItemStack(result, input);
			return stack;
		}
		
		public boolean doesRecipeMatch(ItemStack[] provided){
			if(provided.length != recipeStacks.length)
				return false;
			boolean results[] = new boolean[recipeStacks.length];
			int i = 0;
			for(ItemStack component : recipeStacks){
				inner : for(ItemStack input : provided){
					if(input == null)
						continue inner;
					if(OreDictionary.itemMatches(component, input, false)){
						results[i++] = true; 
						break;
					}else{
						results[i] = false;
					}
				}
			}
			boolean flag = false;
			for(boolean result : results){
				flag = result;
				if(!flag)
					break;
			}
			return flag;
		}
	}
}
