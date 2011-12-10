package com.supermercerbros.gameengine.util;

/**
 * @author Daniel Mercer
 *
 * A clone of the android.util.Log class, to be used for Java-only testing.
 */
public class Log {
	private static boolean print;

	public static void d(String tag, String message){
		if (print) System.out.println("d/" + tag + ": " + message);
	}
	
	public static void i(String tag, String message){
		if (print) System.out.println("i/" + tag + ": " + message);
	}
	
	public static void w(String tag, String message){
		if (print) System.out.println("w/" + tag + ": " + message);
	}
	
	public static void e(String tag, String message){
		if (print) System.out.println("e/" + tag + ": " + message);
	}
	
	public static void v(String tag, String message){
		if (print) System.out.println("v/" + tag + ": " + message);
	}

	public static void setPrint(boolean debug) {
		print = debug;
	}

}

