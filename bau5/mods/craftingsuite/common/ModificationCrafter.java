package bau5.mods.craftingsuite.common;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;

public class ModificationCrafter {
	
	private static final ModificationCrafter instance = new ModificationCrafter();
	private ArrayList<ModificationRecipe> recipeList = new ArrayList<ModificationRecipe>();
	
	private ModificationCrafter(){
		initRecipes();
	}
	
	public ModificationRecipe findRecipe(TileEntityModificationTable table, ItemStack[] input){
		input = filterNulls(input);
		ModificationRecipe recipe = null;
		for(ModificationRecipe rec : recipeList){
			ItemStack[] usedInput = rec.doesRecipeMatch(input);
			table.inputForResult = usedInput;
			if(usedInput != null){
				recipe = rec;
				break;
			}
		}
		return recipe;
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
		//Crafting Table - holding, no overlay
		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 1), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems, 1, 1), new ItemStack(Block.planks.blockID, 4, OreDictionary.WILDCARD_VALUE)
		}));
		//Crafting Table - holding, overlay
		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 1), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems, 1, 1), 
			new ItemStack(Block.planks.blockID, 4, OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE)
		}));
		//Project Bench - no render, no overlay
		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems, 1, 2), 
			new ItemStack(Block.planks.blockID, 4,  OreDictionary.WILDCARD_VALUE)
		}));
		//ProjectBench - render, no overlay
		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems, 1, 2), 
			new ItemStack(Block.planks.blockID, 4,  OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE)
		}));
		//ProjectBench - no render, overlay
		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems, 1, 2), 
			new ItemStack(Block.planks.blockID, 4,  OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE)
		}));
		//ProjectBench -  render, overlay
		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems, 1, 2), 
			new ItemStack(Block.planks.blockID, 4,  OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.blockClay.blockID, 1, 0)
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
					if(OreDictionary.itemMatches(component, input, false)){
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
		
		public boolean consumeItems(TileEntityModificationTable tile){
			boolean flag = true;
			for(ItemStack component : recipeStacks){
				for(int i = 0; i < tile.getSizeInventory(); i++){
					if(OreDictionary.itemMatches(component, tile.getStackInSlot(i), false)){
						tile.decrStackSize(i, component.stackSize);
						break;
					}else
						flag = false;
				}
			}
			return flag;
		}
	}
}
