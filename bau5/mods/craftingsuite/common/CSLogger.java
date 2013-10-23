package bau5.mods.craftingsuite.common;


public class CSLogger {
	public static void log(Object ...data){
		if(CraftingSuite.VERBOSE){
			if(data.length == 1)
				System.out.println("[CraftingSuite]: " +(String)data[0]);
			else if(data.length == 2)
				System.out.println("[CraftingSuite]: " +String.format((String)data[0], data[1]));
		}
	}
}
