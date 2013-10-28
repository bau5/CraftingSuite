package bau5.mods.craftingsuite.common.recipe;

import bau5.mods.craftingsuite.common.tileentity.TileEntityModificationTable;
import net.minecraft.item.ItemStack;

public interface IModRecipe {
	public ItemStack getOutput();
	public ItemStack getExactOutput(ItemStack[] input);
	public ItemStack[] doesRecipeMatch(ItemStack[] provided);
	public boolean consumeItems(TileEntityModificationTable tile);
}
