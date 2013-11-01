package bau5.mods.craftingsuite.common;


public class CSLogger {
	public static void log(Object ...data){
		if(CraftingSuite.VERBOSE){
			if(data.length == 1)
				System.out.println("[CraftingSuite]: " +data[0].toString());
			else if(data.length == 2)
				System.out.println("[CraftingSuite]: " +String.format((String)data[0], data[1]));
		}
	}
	
	public static void logError(Object ...data){
		if(data.length == 2 && data[0] instanceof String
							&& data[1] instanceof Exception){
			System.err.println("[CraftingSuite]: Crash error");
			System.err.println(data[0]);
			((Exception)data[1]).printStackTrace();
		}else{
			System.err.println("[CraftingSuite]: Crash error");
			System.err.println(data[0]);
		}
	}
}
