package bau5.mods.craftingsuite.common.recipe;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import bau5.mods.craftingsuite.common.CraftingSuite;
import bau5.mods.craftingsuite.common.ModificationStackHelper;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;

public class ModificationCrafter {
	
	private static final ModificationCrafter instance = new ModificationCrafter();
	private ArrayList<IModRecipe> recipeList = new ArrayList<IModRecipe>();
	
	private ModificationCrafter(){
		initRecipes();
	}
	
	public IModRecipe findRecipe(TileEntityModificationTable table, ItemStack[] input){
		input = filterNulls(input);
		if(input == null || input.length == 0)
			return null;
		IModRecipe recipe = null;
		for(IModRecipe rec : recipeList){
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
//		//Crafting Table - holding, no overlay
//		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 1), new ItemStack[]{
//			new ItemStack(CraftingSuite.modItems, 1, 1), new ItemStack(Block.planks.blockID, 4, OreDictionary.WILDCARD_VALUE)
//		}));
//		//Crafting Table - holding, overlay
//		this.addRecipe(new ModificationRecipe(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 1), new ItemStack[]{
//			new ItemStack(CraftingSuite.modItems, 1, 1), 
//			new ItemStack(Block.planks.blockID, 4, OreDictionary.WILDCARD_VALUE),
//			new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE)
//		}));
		//Project Bench - no render, no overlay
		this.addRecipe(new ModificationRecipeBasic(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems, 1, 2), 
			new ItemStack(Block.planks.blockID, 4,  OreDictionary.WILDCARD_VALUE)
		}));
		//ProjectBench - render, no overlay
		this.addRecipe(new ModificationRecipeBasic(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems, 1, 2), 
			new ItemStack(Block.planks.blockID, 4,  OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.blockClay.blockID, 1, 0)
		}));
		//ProjectBench - no render, overlay
		this.addRecipe(new ModificationRecipeBasic(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems, 1, 2), 
			new ItemStack(Block.planks.blockID, 4,  OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE)
		}));
		//ProjectBench -  render, overlay
		this.addRecipe(new ModificationRecipeBasic(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems, 1, 2), 
			new ItemStack(Block.planks.blockID, 4,  OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.blockClay.blockID, 1, 0)
		}));
		//ProjectBench Modify recipe
		this.addRecipe(new ModificationRecipeUpgrade(new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2), new ItemStack[]{
			new ItemStack(CraftingSuite.modItems.itemID, 1, OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE),
			new ItemStack(Block.blockClay.blockID, 1, 0)
		}));
	}
	
	public void addRecipe(IModRecipe rec){
		recipeList.add(rec);
	}
	
	public ArrayList getRecipeList(){
		return recipeList;
	}
	
	public static ModificationCrafter instance(){
		return instance;
	}
}
