package bau5.mods.craftingsuite.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class CraftingItemBlock extends ItemBlock {
	private final static String blockNames[] = {
		"ctmkii", "pb"
	};
	public CraftingItemBlock(int id){
		super(id);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}
}
