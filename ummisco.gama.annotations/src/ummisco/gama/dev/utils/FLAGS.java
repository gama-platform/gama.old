/*******************************************************************************************************
 *
 * FLAGS.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
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
 * -Duse_legacy_drawers=false -Duse_delayed_resize=false -Duse_native_opengl_window=true
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
		if (b) { System.out.println(STRINGS.PAD("> FLAG: " + name, 55, ' ') + STRINGS.PAD(" set to", 15, '_') + " " + b); }
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
	 * For debugging purposes, see #3164. True by default until bugs on Linux regarding the use of multiple threads in
	 * UI processes are solved. Update 12/03/22: now false by default, tested on Ubuntu
	 */
	// public static final boolean USE_OLD_ANIMATOR = get("use_old_animator", false);

	/**
	 * Used in LayeredDisplayView. True to use a combination of wait(), notify() and Thread.sleep() for synchronizing
	 * displays with the simulation or false to use semaphores (reduces the time spent between frames). False by default
	 */
	// public static final boolean USE_OLD_SYNC_STRATEGY = get("use_old_sync_strategy", false);

	/**
	 * Used in GamaPreferences, true to save the preferences in the global (managed by the JRE) preference store or
	 * false to save them in each GAMA instance preference store (like Eclipse, see #3115). True by default.
	 */
	public static final boolean USE_GLOBAL_PREFERENCE_STORE = get("use_global_preference_store", true);

	/**
	 * Used in msi.gama.application.Application to transmit the value of "quarter" (if true) to property
	 * org.eclipse.swt.internal.DPIUtil.SWT_AUTOSCALE, enabling more precise scaling methods for HiDPI screens.
	 * Otherwise the default of "integer200" is used by DPIUtil (see #3180). False by default.
	 *
	 * 05 Feb. 2023: Reinstantiates this flag to support #3596 and #3308, set to true by default now 16 Feb. 2023: Again
	 * commented for issue #3604 -- -Dswt.autoScale=exact is used directly instead
	 */
	// public static final boolean USE_PRECISE_SCALING = get("use_precise_scaling", true);

	/**
	 * Used in GamlEditor, see #2950. Set to true to disable editing gaml files. False by default.
	 */
	public static final boolean IS_READ_ONLY = get("read_only", false);

	/**
	 * Used in msi.gama.application.workbench.ApplicationWorkbenchWindowAdvisor to impose the use of the "classic" view
	 * tabs (with a visible border) and inject a specific CSS stylesheet. See #3187. True by default.
	 */
	// public static final boolean USE_OLD_TABS = get("use_old_tabs", true);

	/**
	 * Used in ummisco.gama.opengl.OpenGL to impose the use of the "legacy" text and mesh drawers (ie without VBO/VBA).
	 * False by default.
	 */
	// public static final boolean USE_LEGACY_DRAWERS = get("use_legacy_drawers", false);

	/**
	 * Originally used in msi.gama.application.workbench.ApplicationWorkbenchWindowAdvisor to work around issue #3195.
	 * If true, makes the workbench window resize its views asynchronously. Could prove useful in all environments, for
	 * instance in the presence of slow graphic cards/computers. False by default
	 */
	// public static final boolean USE_DELAYED_RESIZE = get("use_delayed_resize", true);

	/**
	 * Used in JOGL displays, esp. ummisco.gama.opengl.view.SWTOpenGLDisplaySurface to create a NEWT window instead of a
	 * GLCanvas. Advantages are multiple (smaller memory footprint, immediate opening and resizing, etc.), and only a
	 * few glitches remain (esp. on macOS). True by defautl
	 */
	public static final boolean USE_NATIVE_OPENGL_WINDOW = true; // get("use_native_opengl_window", true);

	/**
	 * The Constant PRODUCE_ICONS. Used to tell GAMA to produce the PNG icons from the SVG ones in
	 * ummisco.gama.ui.shared
	 */
	// public static final boolean PRODUCE_ICONS = get("produce_icons", false);

}
