package bau5.mods.craftingsuite.common;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemModifications extends Item {
	
	private Icon[] icons;
	private final String[] itemNames = {
		"crafter", "holding", "storing"	
	};

	public ItemModifications(int id) {
		super(id);
		setMaxDamage(0);
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("modification");
	}
	
	@Override
	public void registerIcons(IconRegister registrar) {
		icons = new Icon[3];
		int i = 0;
		for(Icon ic : icons)
			icons[i] = registrar.registerIcon(String.format("%s:%s%s", Reference.TEX_LOC, "p_", itemNames[i++]));
	}
	
	@Override
	public Icon getIconFromDamage(int par1) {
		return icons[par1];
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName()+"." + itemNames[stack.getItemDamage()];
	}
	
	@Override
	public boolean getHasSubtypes() {
		return true;
	}
	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
			List list) {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
		list.add(new ItemStack(this, 1, 2));
	}
}
