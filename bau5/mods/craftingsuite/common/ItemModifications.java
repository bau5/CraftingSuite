package bau5.mods.craftingsuite.common;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemModifications extends Item {
	
	private Icon[] icons;
	private final String[] itemNames = {
		"holding"	
	};

	public ItemModifications(int id) {
		super(id);
		setMaxDamage(0);
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("modification");
	}
	
	@Override
	public void registerIcons(IconRegister registrar) {
		icons = new Icon[1];
		icons[0] = registrar.registerIcon(String.format("%s:%s", Reference.TEX_LOC, itemNames[0]));
	}
	
	@Override
	public Icon getIconFromDamage(int par1) {
		return icons[par1];
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName()+"." + itemNames[stack.getItemDamage()];
	}
}
