/*******************************************************************************************************
 *
 * FLAGS.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
 * @author A. Drogoul Aug. 2021
 *
 */
public class FLAGS {

	static {
		DEBUG.OFF();
	}

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
		DEBUG.LOG("Reading flag " + name + " with value " + v);
		return Boolean.parseBoolean(v);
	}

	/**
	 * For debugging purposes, see #3164. True by default until bugs on Linux regarding the use of multiple threads in
	 * UI processes are solved.
	 */
	public static final boolean USE_OLD_ANIMATOR = get("use_old_animator", true);

	/**
	 * Used in LayeredDisplayView. True to use a combination of wait(), notify() and Thread.sleep() for synchronizing
	 * displays with the simulation or false to use semaphores (reduces the time spent between frames). False by default
	 */
	public static final boolean USE_OLD_SYNC_STRATEGY = get("use_old_sync_strategy", false);

	/**
	 * Used in GamaPreferences, true to save the preferences in the global (managed by the JRE) preference store or
	 * false to save them in each GAMA instance preference store (like Eclipse, see #3115). True by default.
	 */
	public static final boolean USE_GLOBAL_PREFERENCE_STORE = get("use_global_preference_store", true);

	/**
	 * Used in DEBUG, set to true to enable logging activities (which will follow the declaration of DEBUG.ON() on the
	 * classes). Set to false to suppress all logging. True by default
	 */
	public static final boolean ENABLE_LOGGING = get("enable_logging", true);

	/**
	 * Used in msi.gama.application.Application to transmit the value of "quarter" (if true) to property
	 * org.eclipse.swt.internal.DPIUtil.SWT_AUTOSCALE, enabling more precise scaling methods for HiDPI screens.
	 * Otherwise the default is used by DPIUtil (see #3180). False by default.
	 */
	public static final boolean USE_PRECISE_AUTOSCALE = get("use_precise_autoscale", false);

	/**
	 * Used in GamlEditor, see #2950. Set to true to disable editing gaml files. False by default.
	 */
	public static final boolean IS_READ_ONLY = get("read_only", false);

	/**
	 * Used in msi.gama.application.workbench.ApplicationWorkbenchWindowAdvisor to impose the use of the "classic" view
	 * tabs (with a visible border) and inject a specific CSS stylesheet. See #3187. True by default.
	 */
	public static final boolean USE_OLD_TABS = get("use_old_tabs", true);

	/**
	 * Used in msi.gama.application.workbench.ApplicationWorkbenchWindowAdvisor to work around issue #3195. If true,
	 * makes the workbench window resize its views asynchronously. True by default on macOS. Could prove useful also in
	 * other environments, for instance in the presence of slow graphic cards/computers.
	 */
	public static final boolean USE_DELAYED_RESIZE =
			get("use_delayed_resize", System.getProperty("os.name").contains("Mac"));

}
