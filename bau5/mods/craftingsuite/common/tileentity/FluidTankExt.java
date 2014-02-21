package bau5.mods.craftingsuite.common.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;

public class FluidTankExt extends FluidTank {
	public FluidTankExt(int amount, TileEntity theTile) {
		super(amount);
		tile = theTile;
		capacity = 16000;
	}
}
