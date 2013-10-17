package bau5.mods.craftingsuite.nei;

import bau5.mods.craftingsuite.client.GuiCraftingSuiteBase;
import bau5.mods.craftingsuite.client.GuiProjectBench;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.DefaultOverlayHandler;

public class NEIProjectBenchConfig implements IConfigureNEI {
	private boolean active = false;
	@Override
	public void loadConfig() {
		active = true;
		API.registerGuiOverlay(GuiCraftingSuiteBase.class, "crafting",5,11);
        API.registerGuiOverlayHandler(GuiCraftingSuiteBase.class, new DefaultOverlayHandler(5,11), "crafting");
        API.registerGuiOverlay(GuiProjectBench.class, "crafting",5,11);
        API.registerGuiOverlayHandler(GuiProjectBench.class, new DefaultOverlayHandler(5,11), "crafting");
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
		return "1.0";
	}
}