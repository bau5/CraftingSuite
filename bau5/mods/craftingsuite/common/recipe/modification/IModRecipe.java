package bau5.mods.craftingsuite.common.recipe.modification;

import net.minecraft.item.ItemStack;
import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;

public interface IModRecipe {
	public ItemStack getOutput();
	public ItemStack getExactOutput(ItemStack[] input);
	public ItemStack[] doesRecipeMatch(ItemStack[] provided);
	public boolean consumeItems(TileEntityModificationTable tile);
}
