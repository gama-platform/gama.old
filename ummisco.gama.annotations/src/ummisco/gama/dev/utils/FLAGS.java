package ummisco.gama.dev.utils;

/**
 * A class intended to host several flags used in the code, with the idea of loading them through property files /
 * parameters later. They are distinct from preferences in that they are more low-level, internal to the code. Some
 * might nevertheless be turned into preferences at some point.
 *
 * @author drogoul
 *
 */
public class FLAGS {

	// For debugging purposes, see #3164. False by default.
	public static final boolean USE_OLD_ANIMATOR = false;

	// Used in LayeredDisplayView. False by default.
	public static final boolean USE_OLD_SYNC_STRATEGY = false;

	// Used in GamaPreferences, whether to use the global (JRE) preference store or the per GAMA instance preference
	// store (Eclipse). True by default.
	public static final boolean USE_JRE_PREFERENCE_STORE = true;

}
