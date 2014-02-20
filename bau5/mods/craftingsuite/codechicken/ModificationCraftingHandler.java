package bau5.mods.craftingsuite.codechicken;

import static codechicken.core.gui.GuiDraw.changeTexture;
import static codechicken.core.gui.GuiDraw.drawTexturedModalRect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import bau5.mods.craftingsuite.common.CraftingSuite;
import bau5.mods.craftingsuite.common.helpers.ModificationNBTHelper;
import bau5.mods.craftingsuite.common.helpers.ModificationStackHelper;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class ModificationCraftingHandler extends TemplateRecipeHandler {
	
	private List<BenchCachedRecipe> recipeList = new ArrayList<BenchCachedRecipe>();
	
	private ResourceLocation tableResource = new ResourceLocation("craftingsuite", "textures/gui/neimodgui.png");
	
	@Override
	public void loadCraftingRecipes(ItemStack result) {
		if(result.itemID == CraftingSuite.craftingTableBlock.blockID){
			ItemStack copy = result.copy();
			copy.stackSize = 1;
			ArrayList<ItemStack> inputs = ModificationStackHelper.getPartsForBench(result);
			arecipes.add(new BenchCachedRecipe(copy, inputs));
		}
	}
	
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		Random rand = new Random();
		if(ingredient.itemID == CraftingSuite.craftingTableBlock.blockID){
			if(ingredient.hasTagCompound() && ModificationNBTHelper.getUpgradeByteArray(ingredient.stackTagCompound)[1] == -1){
				ArrayList<ItemStack> ins = new ArrayList<ItemStack>();
				ArrayList<ItemStack> outs= new ArrayList<ItemStack>();
				int i;
				for(i = 3; i < 6; i++){
					ItemStack copy = ingredient.copy();
					copy.stackSize = 1;
					ins.add(copy);
					ins.add(new ItemStack(CraftingSuite.modItems.itemID, 1, i));
					ItemStack ingred = ingredient.copy();
					byte[] upgrades = ModificationNBTHelper.getUpgradeByteArray(ingred.stackTagCompound);
					ingred.stackSize = 1;
					upgrades[1] = (byte)i;
					if(upgrades[3] == -1){
						int col = rand.nextInt(15);
						upgrades[3] = (byte)col;
						ins.add(new ItemStack(Block.carpet.blockID, 1, col));
					}
					if(upgrades[4] == -1){
						upgrades[4] = 1;
						ins.add(new ItemStack(Block.blockClay));
					}
					outs.add(ingred);
					arecipes.add(new BenchCachedRecipe(ingred, ins));
					outs.clear();
					ins.clear();
				} i = 0;
				for(ItemStack out : outs){
					arecipes.add(new BenchCachedRecipe(out, outs.subList(i*2, i*2+1)));
				} i = 0;
			}
		}else if(ingredient.itemID == CraftingSuite.modificationTableBlock.blockID){
			ArrayList<ItemStack> types = new ArrayList<ItemStack>();
			types.add(ModificationStackHelper.makeModdedTableType(2, -1, 12, 0));
			types.add(ModificationStackHelper.makeModdedTableType(2, 5, 14, 1));
			types.add(ModificationStackHelper.makeModdedTableType(2, 4, 13, 0));
			types.add(ModificationStackHelper.makeModdedTableType(2, 3, 0, 2));
			types.add(ModificationStackHelper.makeModdedTableType(1, -1, 0, 2));
			types.add(ModificationStackHelper.makeModdedTableType(1, 4, 0, 0));
			for(int i = 0; i < types.size(); i++){
				ArrayList<ItemStack> parts = ModificationStackHelper.getPartsForBench(types.get(i));
				arecipes.add(new BenchCachedRecipe(types.get(i), parts));
			}
		}else if(ingredient.itemID == CraftingSuite.modItems.itemID){
			if(ingredient.getItemDamage() > 2){
				ArrayList<ItemStack> types = new ArrayList<ItemStack>();
				if(ingredient.getItemDamage() == 4)
					types.add(ModificationStackHelper.makeModdedTableType(1, ingredient.getItemDamage(), 0, 0));
				types.add(ModificationStackHelper.makeModdedTableType(2, ingredient.getItemDamage(), 13, 0));
				for(int i = 0; i < types.size(); i++){
					ArrayList<ItemStack> parts = ModificationStackHelper.getPartsForBench(types.get(i));
					arecipes.add(new BenchCachedRecipe(types.get(i), parts));
				}
			}else if(ingredient.getItemDamage() > 0){
				ArrayList<ItemStack> types = new ArrayList<ItemStack>();
				int dam = ingredient.getItemDamage();
				types.add(ModificationStackHelper.makeModdedTableType(dam, -1, dam == 2 ? rand.nextInt(15) : -1, rand.nextInt(4)));
				for(int i = 0; i < types.size(); i++){
					ArrayList<ItemStack> parts = ModificationStackHelper.getPartsForBench(types.get(i));
					arecipes.add(new BenchCachedRecipe(types.get(i), parts));
				}
			}
		}else if(ingredient.itemID == Block.blockClay.blockID){
			for(int i = 1; i < 3; i++){
				ItemStack out = ModificationStackHelper.makeModdedTableType(i, -1, i == 2 ? rand.nextInt(14) : -1, rand.nextInt(4));
				arecipes.add(new BenchCachedRecipe(out, ModificationStackHelper.getPartsForBench(out)));
			}
		}else if(ingredient.itemID == Block.carpet.blockID){
			ItemStack out = ModificationStackHelper.makeModdedTableType(2, -1, ingredient.getItemDamage(), rand.nextInt(4));
			arecipes.add(new BenchCachedRecipe(out, ModificationStackHelper.getPartsForBench(out)));
		}else{
			int id1 = OreDictionary.getOreID(ingredient);
			int id2 = OreDictionary.getOreID("plankWood");
			if(id1 == id2 && id1 != -1){
				for(int i = 1; i < 3; i++){
					ItemStack out = ModificationStackHelper.makeModdedTableType(i, -1, i == 2 ? rand.nextInt(14) : -1, 0);
					ModificationNBTHelper.setTagPlanksUsed(out.stackTagCompound, ingredient);
					arecipes.add(new BenchCachedRecipe(out, ModificationStackHelper.getPartsForBench(out)));
				}
			}
		}
		super.loadUsageRecipes(ingredient);
	}
	
	@Override
	public List<PositionedStack> getOtherStacks(int recipe) {
		if(recipe < recipeList.size()){
			return recipeList.get(recipe).getOthers();
		}
		else return new ArrayList<PositionedStack>();
	}
	
	@Override
	public PositionedStack getResultStack(int recipe) {
		// TODO Auto-generated method stub
		return super.getResultStack(recipe);
	}
	
	@Override
	public List<PositionedStack> getIngredientStacks(int recipe) {
		// TODO Auto-generated method stub
		return super.getIngredientStacks(recipe);
	}
	
	@Override
	public int recipiesPerPage() {
		return 1;
	}
	
	@Override
	public String getRecipeName() {
		return "Modification Table";
	}

	@Override
	public String getGuiTexture() {
		return "craftingsuite/textures/gui/neimodgui.png";
	}
	
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        changeTexture(tableResource);
        drawTexturedModalRect(-5, 0, 0, 0, 176, 130);
    }
    
    public void drawForeground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        changeTexture(tableResource);
        drawExtras(recipe);
    }

	public class BenchCachedRecipe extends CachedRecipe{
		private PositionedStack presult;
		private List<PositionedStack> others;
		public BenchCachedRecipe(ItemStack res, Object ...objects){
			others = new ArrayList<PositionedStack>();
			setResult(res);
			if(objects[0] instanceof List)
				setOthers((List)objects[0]);
			else{
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				stacks.add((ItemStack)objects[0]);
				setOthers(stacks);
			}
				
		}
		
		public void setResult(ItemStack result){
			presult = new PositionedStack(result, 137, 108, false);
		}
		
		public void setOthers(List<ItemStack> stacks){
			int shift = 0;
			boolean flag = false;
			for(ItemStack stack : stacks){
				if(stack.itemID == CraftingSuite.craftingTableBlock.blockID){
					others.add(new PositionedStack(stack, 6, 20, false));
					flag = true;
				}else if(stack.itemID == CraftingSuite.modItems.itemID){
					others.add(new PositionedStack(stack, 6, 20 + (flag?36:0), false));
				}else if(stack.itemID == Block.planks.blockID || OreDictionary.getOreID(stack) == 1){
					others.add(new PositionedStack(stack, 6, 56, false));
				}else if(stack.itemID == Block.carpet.blockID){
					others.add(new PositionedStack(stack, 6, 92, false));
					shift++;
				}else if(stack.itemID == Block.blockClay.blockID){
					others.add(new PositionedStack(stack, 6 +(shift*22), 92, false));
				}
			}
			recipeList.add(this);
		}
		
		@Override
		public PositionedStack getResult() {
			return presult;
		}
		
		public List<PositionedStack> getOthers(){
			return others;
		}
	}
}
