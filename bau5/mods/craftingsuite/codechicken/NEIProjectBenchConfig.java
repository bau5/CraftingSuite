package bau5.mods.craftingsuite.codechicken;

import bau5.mods.craftingsuite.client.GuiModdedCraftingTable;
import bau5.mods.craftingsuite.client.GuiProjectBench;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIProjectBenchConfig implements IConfigureNEI {
	private boolean active = false;
	@Override
	public void loadConfig() {
		active = true;
		API.registerGuiOverlay(GuiModdedCraftingTable.class, "crafting",5,11);
        API.registerGuiOverlayHandler(GuiModdedCraftingTable.class, new codechicken.nei.recipe.DefaultOverlayHandler(5,11), "crafting");
        API.registerGuiOverlay(GuiProjectBench.class, "crafting",5,11);
        API.registerGuiOverlayHandler(GuiProjectBench.class, new DefaultOverlayHandler(5,11), "crafting");
        API.registerRecipeHandler(new ModificationCraftingHandler());
        API.registerUsageHandler(new ModificationCraftingHandler());
	}

	public boolean isNEIActive(){
		return active;
	}

	@Override
	public String getName() {
		return "Crafting Suite Plugin";
	}

	@Override
	public String getVersion() {
		return "1.5";
	}
}