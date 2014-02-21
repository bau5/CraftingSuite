package bau5.mods.craftingsuite.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class ExtraHandler {

	public abstract boolean handlesSlotClicks();

	public abstract ItemStack handleSlotClick(int slot, int clickType, int clickMeta, EntityPlayer player);

}
