package bau5.mods.craftingsuite.common;

import java.util.EnumSet;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class VersionCheckTicker implements ITickHandler {

	private boolean init = true;
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(init){
			for(TickType tickType: type){
				if(tickType == TickType.CLIENT){
					if(FMLClientHandler.instance().getClient().currentScreen == null){
						init = false;
						if(!Reference.UP_TO_DATE){
							FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().printChatMessage("A new version of Crafting Suite is available.\n    Version: " +Reference.LATEST_VERSION +": " +Reference.LATEST_CHANGES);
							FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().printChatMessage("This update is " +VersionChecker.remoteVersionImportance +" - " +Reference.UPDATE_URL);
						}
						
					}
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "Craftin Suite: " +this.getClass().getSimpleName();
	}

}
