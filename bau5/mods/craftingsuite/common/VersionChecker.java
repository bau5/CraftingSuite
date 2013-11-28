package bau5.mods.craftingsuite.common;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.minecraft.client.resources.I18n;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class VersionChecker implements Runnable {
	private static VersionChecker instance = new VersionChecker();
	
	private static final String versionURL = "https://raw.github.com/bau5/CraftingSuite/master/version.xml";
	private static final String changesURL = "https://raw.github.com/bau5/CraftingSuite/master/changes.xml";
	public static Properties versionProperties = new Properties();
	public static Properties changesProperties = new Properties();
	
	public static final byte NOT_DONE = 0;
	public static final byte UP_TO_DATE = 1;
	public static final byte OUT_OF_DATE = 2;
	public static final byte FAILED = 3;
	
	private static byte result = NOT_DONE;

	public static String remoteVersion = null;
	public static String remoteVersionImportance = null;
	
	public static void checkVersion(){
		InputStream remoteVersionStream = null;
		result = NOT_DONE;
		try{
			URL versionurl = new URL(versionURL);
			remoteVersionStream = versionurl.openStream();
			versionProperties.loadFromXML(remoteVersionStream);
			String versionFromRemote = versionProperties.getProperty(Loader.instance().getMCVersionString());
			if(versionFromRemote != null){
				String[] versionSplit = versionFromRemote.split("\\|");
				if(versionSplit[0] != null)
					remoteVersion = versionSplit[0];
				if(versionSplit[1] != null)
					remoteVersionImportance = versionSplit[1];
				if(remoteVersion != null){
					Reference.LATEST_VERSION = remoteVersion;
					if(remoteVersion.equalsIgnoreCase(Reference.VERSION))
						result = UP_TO_DATE;
					else
						result = OUT_OF_DATE;
				}
			}else
				result = FAILED;
		}catch(Exception ex){
			CSLogger.log(I18n.getString("cs.versioncheck.fail"), ex);
		}finally{
			if(result == NOT_DONE)
				result = FAILED;
			try{
				if(remoteVersionStream != null)
					remoteVersionStream.close();
			}catch(Exception ex){/*frick*/}
		}
	}
	public static void checkLatestChanges(){
		InputStream remoteChangesStream = null;
		
		try{
			URL remoteChangesURL = new URL(changesURL);
			remoteChangesStream = remoteChangesURL.openStream();
			changesProperties.loadFromXML(remoteChangesStream);
			String changesFromRemote = changesProperties.getProperty(Reference.LATEST_VERSION);
			if(changesFromRemote != null){
				CSLogger.log("Latest Changes: " +changesFromRemote);
				Reference.LATEST_CHANGES = changesFromRemote;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(remoteChangesStream != null)
					remoteChangesStream.close();
			}catch(Exception ex){/*double frick*/}
		}
	}
	
	@Override
	public void run() {
		int tries = 0;
		CSLogger.log(I18n.getString("cs.versioncheck.start"));
		
		try{
			while(tries < 3 && (result != OUT_OF_DATE && result != UP_TO_DATE)){
				checkVersion();
				tries++;
				if(result == OUT_OF_DATE){
					Reference.UP_TO_DATE = false;
					checkLatestChanges();
					TickRegistry.registerTickHandler(new VersionCheckTicker(), Side.CLIENT);
				}
				if(result == UP_TO_DATE)
					CSLogger.log(I18n.getString("cs.versioncheck.up"));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void go(){
		new Thread(instance).start();
	}
}
