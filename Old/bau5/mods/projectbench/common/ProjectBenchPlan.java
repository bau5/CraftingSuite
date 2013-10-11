package bau5.mods.projectbench.common;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;


public class ProjectBenchPlan extends Item {

	private Icon itemIconUsed;
	
	public ProjectBenchPlan(int id) {
		super(id);
		setMaxDamage(0);
	}

	@Override
	public String getItemDisplayName(ItemStack par1ItemStack) {
		if(par1ItemStack.stackTagCompound != null){
			NBTTagCompound tag = par1ItemStack.stackTagCompound.getCompoundTag("Result");
			if(tag != null){
				ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
				return EnumChatFormatting.ITALIC + stack.getDisplayName() + EnumChatFormatting.RESET +" " +StatCollector.translateToLocal(getUnlocalizedName()+".name.used");
			}
			return StatCollector.translateToLocal(getUnlocalizedName()+".name.broken");
		}else
			return StatCollector.translateToLocal(getUnlocalizedName()+".name.blank");
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
		
	}
	
	@Override
	public void registerIcons(IconRegister register) {
		itemIcon = register.registerIcon("projectbench:planblank");
		itemIconUsed = register.registerIcon("projectbench:planused");
	}
	@Override
	public Icon getIconFromDamage(int meta) {
		if(meta == 1){
			return itemIconUsed;
		}
		return meta == 0 ? itemIcon : itemIconUsed;
	}

}
