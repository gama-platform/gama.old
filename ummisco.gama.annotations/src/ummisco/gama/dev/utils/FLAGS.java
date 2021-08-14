package ummisco.gama.dev.utils;

/**
 * A class intended to host several flags used in the code, allowing to load them through VM properties/parameters. They
 * are distinct from preferences in that they are more low-level, internal to the code. Some might nevertheless be
 * turned into preferences at some point.
 *
 * Use -Dflag=true/false in your VM arguments in the run configuration (or the arguments passed to your VM in gama.ini)
 * to change this default value.
 *
 *
 * @author A. Drogoul Aug. 2021
 *
 */
public class FLAGS {

	static {
		DEBUG.OFF();
	}

	private static boolean get(final String name, final boolean def) {
		String v = System.getProperty(name);
		if (v == null) return def;
		DEBUG.LOG("Reading flag " + name + " with value " + v);
		return Boolean.parseBoolean(v);
	}

	// For debugging purposes, see #3164. False by default.
	public static final boolean USE_OLD_ANIMATOR = get("use_old_animator", false);

	// Used in LayeredDisplayView. True to use a combination of wait(), notify() and Thread.sleep() for synchronizing
	// displays with the simulation or false to use semaphores (reduces the time spent between frames). False by
	// default.
	public static final boolean USE_OLD_SYNC_STRATEGY = get("use_old_sync_strategy", false);

	// Used in GamaPreferences, true to save the preferences in the global (managed by the JRE) preference store or
	// false to save them in each GAMA instance preference store (like Eclipse, see #3115). True by default.
	public static final boolean USE_JRE_PREFERENCE_STORE = get("use_jre_preference_store", true);

}
