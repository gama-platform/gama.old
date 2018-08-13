package msi.gama.precompiler.doc.utils;

public class OSUtils {

	private static String OS = null;

	public static String getOsName() {
		if (OS == null) {
			OS = System.getProperty("os.name");
		}
		return OS;
	}

	public static boolean isWindows() {
		return getOsName().startsWith("Windows");
	}

	public static boolean isMacOS() {
		return getOsName().startsWith("Mac");
	}

	public static boolean isOther() {
		return !isWindows() && !isMacOS();
	}

}
