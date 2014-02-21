package bau5.mods.craftingsuite.common.helpers;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;
import bau5.mods.craftingsuite.common.CraftingSuite;
import bau5.mods.craftingsuite.common.tileentity.IModifiedTileEntityProvider;
import bau5.mods.craftingsuite.common.tileentity.Modifications;

public class ModificationStackHelper {
	
	public static ItemStack makeBasicMkICrafter(){
		ItemStack theStack = new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 1);
		NBTTagCompound baseTag = ModificationNBTHelper.makeBaseTag();
		byte[] bytes = ModificationNBTHelper.getUpgradeByteArray(baseTag);
		bytes[0] = 1;
		bytes[4] = 1;
		theStack = makeStackFromInfo(theStack, bytes, new ItemStack(Block.planks.blockID, 1, 0));
		return theStack;
	}

	public static ItemStack makeBasicProjectBench() {
		ItemStack stack = new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, 2);
		byte[] bytes = ModificationNBTHelper.newBytes();
		bytes[0] = 2;
		bytes[1] = 5;
		bytes[2] = 0;
		bytes[3] = 14;
		bytes[4] = 1;
		stack = makeStackFromInfo(stack, bytes, new ItemStack(Block.planks.blockID, 1, 1));
		return stack;
	}
	
	/**
	 * Helper method for making Benches based on passed types.
	 * 
	 * @param type 2 for Project Bench, 1 for Modded Table
	 * @param upgrade 5 for Plan, 4 for Deep Slot, 3 for Tools
	 * @param color
	 * @return
	 */
	public static ItemStack makeModdedTableType(int type, int upgrade, int extra, int color, int woodDamage){
		ItemStack stack = new ItemStack(CraftingSuite.craftingTableBlock.blockID, 1, type);
		byte[] bytes = ModificationNBTHelper.newBytes();
		bytes[0] = (byte)type;
		bytes[1] = (byte)upgrade;
		if(type == 2){ 
			bytes[2] = (byte)extra;
			bytes[3] = (byte)(color +1);
		}
		bytes[4] = 1;
		stack = makeStackFromInfo(stack, bytes, new ItemStack(Block.planks.blockID, 1, woodDamage));
		return stack;
	}
	
	public static ItemStack makeStackFromInfo(ItemStack vanillaStack, TileEntity tile) {
		byte[] bytes = null;
		ItemStack stack = null;
		if(tile instanceof IModifiedTileEntityProvider){
			bytes = ModificationNBTHelper.newBytes();
			Modifications mod = ((IModifiedTileEntityProvider) tile).getModifications();
			return makeStackFromInfo(vanillaStack, mod);
		}else{
			bytes = ModificationNBTHelper.newBytes();
		}
		vanillaStack = makeStackFromInfo(vanillaStack, bytes, stack);
		return vanillaStack;
	}

	public static ItemStack makeStackFromInfo(ItemStack stack, Object ... data) {
		NBTTagCompound newTag = null;
		byte[] upgrades = null;
		ItemStack planks = null;
		if(data[0] instanceof Modifications){
			upgrades = ModificationNBTHelper.newBytes();
			Modifications mod = (Modifications)data[0];
			upgrades[0] = (byte) mod.type();
			upgrades[1] = (byte) mod.upgrades();
			upgrades[3] = (byte) mod.color();
			upgrades[4] = (byte) mod.visual();
			newTag = ModificationNBTHelper.makeBaseTag();
			planks = mod.getPlanks();
			ModificationNBTHelper.setTagUpgradeBytes(newTag, upgrades);
			ModificationNBTHelper.setTagPlanksUsed(newTag, planks);
			
		}else if(data[0] instanceof byte[]){
				upgrades = (byte[]) data[0];
			if(data[1] instanceof ItemStack)
				planks = (ItemStack) data[1];
			if(planks == null)
				planks = new ItemStack(Block.planks.blockID, 1, 0);
			newTag = ModificationNBTHelper.makeBaseTag();
			ModificationNBTHelper.setTagUpgradeBytes(newTag, upgrades);
			ModificationNBTHelper.setTagPlanksUsed(newTag, planks);
		}else{
			if(data[0] instanceof NBTTagCompound){
				newTag = (NBTTagCompound)data[0];
				NBTTagCompound baseTag = new NBTTagCompound("tag");
				baseTag.setTag(ModificationNBTHelper.modifierTag, newTag);
				newTag = (NBTTagCompound) baseTag.copy();
			}
		}
		stack.stackTagCompound = newTag;		
		return stack;
	}
	
	public static ItemStack makeItemStack(ItemStack result, ItemStack[] input) {
		ItemStack stack = new ItemStack(result.itemID, result.stackSize, result.getItemDamage());
		NBTTagCompound baseTag = ModificationNBTHelper.makeBaseTag();
		byte[] bytes = ModificationNBTHelper.getUpgradeByteArray(baseTag);
		for(ItemStack stackUsed : input){
			if(stackUsed.itemID == CraftingSuite.modItems.itemID){
				if(stackUsed.getItemDamage() == 1)
					bytes[0] = 1;
				if(stackUsed.getItemDamage() == 2)
					bytes[0] = 2;
				continue;
			} 
			if(OreDictionary.getOreID(stackUsed) == 1){
				ItemStack temp = stackUsed.copy();
				temp.stackSize = 1;
				ModificationNBTHelper.setTagPlanksUsed(baseTag, temp);
				continue;
			}
			if(stackUsed.itemID == Block.carpet.blockID){
				bytes[3] = (byte)stackUsed.getItemDamage();
				continue;
			}
			if(stackUsed.itemID == Block.blockClay.blockID){
				bytes[4] = 1;
				continue;
			}
		}
		stack.stackTagCompound = baseTag;
		return stack;
	}

	public static ArrayList<ItemStack> getPartsForBench(ItemStack result) {
		ArrayList<ItemStack> parts = new ArrayList<ItemStack>();
		if(result.itemID != CraftingSuite.craftingTableBlock.blockID || result.stackTagCompound == null)
			return parts;
		byte[] bytes = ModificationNBTHelper.getUpgradeByteArray(result.stackTagCompound);
		if(bytes[0] != -1){
			ItemStack planks = ItemStack.loadItemStackFromNBT(ModificationNBTHelper.getPlanksUsed(result.stackTagCompound));
			if(bytes[1] != -1){
				parts.add(makeModdedTableType(bytes[0], -1, bytes[2], bytes[3], planks.getItemDamage()));
				parts.add(new ItemStack(CraftingSuite.modItems.itemID, 1, bytes[1]));
				return parts;
			}
			if(bytes[0] == 1){
				parts.add(new ItemStack(CraftingSuite.modItems.itemID, 1, 1));
				planks.stackSize = 2;
				parts.add(planks);
			}else if(bytes[0] == 2){
				parts.add(new ItemStack(CraftingSuite.modItems.itemID, 1, 2));
				planks.stackSize = 4;
				parts.add(planks);
			}
			if(bytes[3] != -1){
				parts.add(new ItemStack(Block.carpet.blockID, 1, bytes[3]));
			}
			if(bytes[4] != -1){
				parts.add(new ItemStack(Block.blockClay));
			}
		}
		
		return parts;
	}

	public static Modifications convertToModInfo(ItemStack stack2) {
		Modifications mods = new Modifications();
		return mods.convert(stack2);
	}
}
