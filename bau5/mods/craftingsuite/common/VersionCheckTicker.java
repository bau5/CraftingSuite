package bau5.mods.craftingsuite.common;

import java.util.EnumSet;

import net.minecraft.client.resources.I18n;
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
							FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().printChatMessage(String.format("%s\n    %s", I18n.getString("cs.versioncheck.new"), I18n.getString("cs.versioncheck.version") + ": " +Reference.LATEST_VERSION +": " +Reference.LATEST_CHANGES));
							FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().printChatMessage(I18n.getString("cs.versioncheck.importance") +" " +VersionChecker.remoteVersionImportance +" - " +Reference.UPDATE_URL);
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
		return "Crafting Suite: " +this.getClass().getSimpleName();
	}

}
