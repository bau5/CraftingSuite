package bau5.mods.craftingsuite.common;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

public class ItemPlan extends Item {
	
	private Icon[] icons = new Icon[2];
	private String[] itemNames ={
		"blank", "used"	
	};
	public ItemPlan(int id) {
		super(id);
		setMaxDamage(0);
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("plan");
	}
	
	@Override
	public void registerIcons(IconRegister registrar) {
		int i = 0;
		for(Icon ic : icons)
			icons[i] = registrar.registerIcon(String.format("%s:%s%s", Reference.TEX_LOC, "plan", itemNames[i++]));
	}
	
	@Override
	public Icon getIcon(ItemStack stack, int pass) {
		if(stack.hasTagCompound())
			return icons[1];
		else return icons[0];
	}
	
	@Override
	public Icon getIconFromDamage(int damage) {
		return icons[damage];
	}
	
	@Override
	public String getItemDisplayName(ItemStack par1ItemStack) {
		if(par1ItemStack.stackTagCompound != null){
			NBTTagCompound tag = par1ItemStack.stackTagCompound.getCompoundTag("Result");
			if(tag != null){
				ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
				return EnumChatFormatting.ITALIC +"" +EnumChatFormatting.BLUE + stack.getDisplayName() + EnumChatFormatting.RESET +" " +StatCollector.translateToLocal(getUnlocalizedName()+".used");
			}
			return StatCollector.translateToLocal(getUnlocalizedName()+".broken");
		}else
			return StatCollector.translateToLocal(getUnlocalizedName()+".blank");
	}
	
	@Override
	public void addInformation(ItemStack stack,
			EntityPlayer palyer, List par3List, boolean par4) {
//		if((Keyboard.isKeyDown(42) || ItemStack.areItemStackTagsEqual(stack, ((ContainerProjectBench)player.openContainer).getPlanStack())) && stack.stackTagCompound != null){
//			NBTTagList tag = stack.stackTagCompound.getTagList("Components");
//			if(tag != null){
//				ItemStack[] stacks = new ItemStack[tag.tagCount()];
//				for(int i = 0; i < tag.tagCount(); i++){
//					ItemStack stack2 = ItemStack.loadItemStackFromNBT((NBTTagCompound)tag.tagAt(i));
//					stacks[i] = stack2;
//				}
//				RecipeCrafter crafter = new RecipeCrafter();
//				ItemStack[] stacks2 = crafter.consolidateItemStacks(stacks);
//				ItemStack[] missingStacks = crafter.getMissingStacks((ContainerProjectBench)player.openContainer, stack);
//				
//				for(ItemStack stack3 : stacks2){
//					boolean flag = false;
//					if(player.openContainer instanceof ContainerProjectBench){
//						if(missingStacks != null && missingStacks.length > 0){
//							for(ItemStack missingStack : missingStacks){
//								if(crafter.checkItemMatch(stack3, missingStack, false)){
//									flag = true;
//									break;
//								}
//							}
//						}
//					}
//					list.add((flag ? EnumChatFormatting.DARK_RED : EnumChatFormatting.DARK_GREEN) + stack3.getDisplayName() + " x " +stack3.stackSize);
//					flag = false;
//				}
//			}
//		}
	}
}
