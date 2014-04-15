/*********************************************************************************************
 * 
 *
 * 'Logger.java', in plugin 'msi.gama.display.web', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.display.web.utils;

public class Logger {
	private static final boolean enabled = true;

	public static void mlog(String... args) {
		if (enabled) {
			System.out.println(getLogMessage(args));
		}
	}

	public static void elog(String... args) {
		if (enabled) {
			System.err.println(getLogMessage(args));
		}
	}

	private static String getLogMessage(String... args) {
		StringBuilder sb = new StringBuilder();
		for (String s : args) {
			sb.append(s).append("\n\t");
		}
		return sb.toString();
	}
}
