/*******************************************************************************************************
 *
 * FLAGS.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.2.0.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama2 for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.dev.utils;

/**
 * A class intended to host several flags used in the code, allowing to load them through VM properties/parameters. They
 * are distinct from preferences in that they are more low-level, internal to the code. Some might nevertheless be
 * turned into preferences at some point.
 *
 * Use -Dflag=true/false in your VM arguments in run configurations (or in the arguments passed to your VM in gama.ini)
 * to change this default value (e.g. -Denable_logging=false). Note that preferences can also be set as system
 * properties using the same syntax
 *
 *
 *
 * DEFAULTS: -Denable_debug=true -Denable_logging=true -Duse_old_animator=true -Duse_old_sync_strategy=false
 * -Duse_global_preference_store=true -Duse_precise_autoscale=false -Dread_only=false -Duse_old_tabs=true
 * -Duse_legacy_drawers=false -Duse_delayed_resize=false
 *
 *
 * @author A. Drogoul Aug. 2021
 *
 */
public class FLAGS {

	/**
	 * Load.
	 */
	public static void load() {
		DEBUG.OFF();
	}
	//
	// static {
	//
	// }

	/**
	 * Returns the value of a named system property if it is set in the system/VM properties/arguments, and otherwise
	 * the default value passed in parameter
	 *
	 * @param name
	 *            the name of the property
	 * @param def
	 *            the default value to use if the property is not defined
	 *
	 * @return either the value of the property passed as an argument to GAMA or the default value
	 */
	private static boolean get(final String name, final boolean def) {
		String v = System.getProperty(name);
		if (v == null) return def;
		boolean b = Boolean.parseBoolean(v);
		if (b) {
			System.out.println(STRINGS.PAD("> FLAG  : " + name, 55, ' ') + STRINGS.PAD(" set to", 15, '_') + " " + b);
		}
		return b;
	}

	/**
	 * Used in DEBUG, set to true to enable logging the debug messages (DEBUG.OUT(...), DEBUG.ERR(...) which will follow
	 * the declaration of DEBUG.ON() on the classes). Set to false to suppress all debug logging (but regular logging
	 * using DEBUG.LOG(...) or DEBUG.TIMER(...) will still operate). True by default.
	 *
	 * Important to KEEP IT THE FIRST PROPERTY
	 */
	public static final boolean ENABLE_DEBUG = get("enable_debug", true);

	/**
	 * Used in DEBUG, set to true to enable simple logging activities using DEBUG.LOG(...), DEBUG.TIMER(...). Set to
	 * false to prevent all logging activities (incl. debug ones) True by default.
	 *
	 */
	public static final boolean ENABLE_LOGGING = get("enable_logging", true);

	/**
	 * Used in GamaPreferences, true to save the preferences in the global (managed by the JRE) preference store or
	 * false to save them in each GAMA instance preference store (like Eclipse, see #3115). True by default.
	 */
	public static final boolean USE_GLOBAL_PREFERENCE_STORE = get("use_global_preference_store", true);

	/**
	 * Used in GamlEditor, see #2950. Set to true to disable editing gaml files. False by default.
	 */
	public static final boolean IS_READ_ONLY = get("read_only", false);

}
