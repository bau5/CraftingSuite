package bau5.mods.craftingsuite.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import bau5.mods.craftingsuite.common.inventory.ContainerProjectBench;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench;
import bau5.mods.craftingsuite.common.tileentity.TileEntityProjectBench.PositionedFluidStack;

public class FluidHandler extends ExtraHandler {
	
	private TileEntityProjectBench projectBench;
	private ContainerProjectBench container;
	
	private FluidStack lastFluidStack	 = null;
	private ItemStack lastFluidContainer = null;
	private ItemStack lastFluidEmptyContainer = null;
	
	public FluidHandler(ContainerProjectBench containerProjectBench) {
		projectBench = containerProjectBench.tileEntity;
		container = containerProjectBench;
	}

	@Override
	public boolean handlesSlotClicks() {
		return true;
	}

	@Override
	public ItemStack handleSlotClick(int slot, int clickType, int clickMeta,
			EntityPlayer player) {
		FluidStack fstack = projectBench.getFluidInTank(ForgeDirection.UP);
		if(slot < 1 || slot > 9)
			return null;
		FluidStack perfectfstack = null;
		if(!container.getSlot(slot).getHasStack()){
			int remove = -1;
			for(int i = 0; i < projectBench.fluidForCrafting.size(); i++){
				PositionedFluidStack stack = projectBench.fluidForCrafting.get(i);
				if(stack.slotNumber == slot -1){
					remove = i;
				}
			}
			if(remove != -1){
				projectBench.fluidForCrafting.remove(remove);
				return null;
			}
			if(lastFluidStack == null && fstack != null){
				lastFluidStack = fstack.copy();
				for(FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()){
					if(data.fluid.getFluid() == fstack.getFluid()){
						lastFluidContainer = data.filledContainer;
						lastFluidEmptyContainer = data.emptyContainer;
						perfectfstack = data.fluid.copy();
						break;
					}
				}
				if(lastFluidContainer == null || lastFluidEmptyContainer == null || perfectfstack == null)
					return null;
				lastFluidStack = perfectfstack.copy();
				projectBench.fluidForCrafting.add(projectBench.new PositionedFluidStack(slot -1, perfectfstack, lastFluidContainer, lastFluidEmptyContainer));
			}else if(fstack != null && lastFluidStack.getFluid() == fstack.getFluid()){
				if(lastFluidContainer == null || lastFluidEmptyContainer == null){
					lastFluidStack = null;
					return null;
				}
				projectBench.fluidForCrafting.add(projectBench.new PositionedFluidStack(slot -1, lastFluidStack, lastFluidContainer, lastFluidEmptyContainer));
			}
		}
		return null;
	}
}
