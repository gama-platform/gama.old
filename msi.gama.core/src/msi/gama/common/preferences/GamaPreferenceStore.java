/*******************************************************************************************************
 *
 * GamaPreferenceStore.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.preferences;

import static java.util.function.Function.identity;
import static java.util.prefs.Preferences.userRoot;
import static msi.gama.common.util.StringUtils.toJavaString;
import static msi.gaml.operators.Cast.asInt;
import static msi.gaml.operators.Cast.asString;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.eclipse.core.internal.preferences.PreferencesService;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import msi.gama.common.preferences.Pref.ValueProvider;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaDate;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GenericFile;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.GamaFontType;
import msi.gaml.types.IType;
import ummisco.gama.dev.utils.FLAGS;

/**
 * A store that acts as a gateway with either the JRE preference store (global) or configuration-specific preference
 * stores (Eclipse). In addition, allows preferences to be overriden if they are passed as VM arguments to GAMA (e.g.
 * "-Dpref_use_pooling=true"), enabling different instances to set different values even if the store used is global
 *
 * @author drogoul
 *
 * @param <T>
 */
@SuppressWarnings ({ "restriction", "unchecked", "rawtypes" })
public abstract class GamaPreferenceStore<T> {

	static {
		// DEBUG.ON();
	}

	/** The store. */
	private static volatile GamaPreferenceStore STORE;

	/** The Constant NODE_NAME. */
	private static final String NODE_NAME = "gama";

	/** The Constant DEFAULT_FONT. */
	private static final String DEFAULT_FONT = "Default";

	/**
	 * Gets the store.
	 *
	 * @return the store
	 */
	public static GamaPreferenceStore getStore() {
		if (STORE == null) {
			STORE = FLAGS.USE_GLOBAL_PREFERENCE_STORE ? new JRE(userRoot().node(NODE_NAME))
					: new Configuration(ConfigurationScope.INSTANCE.getNode(NODE_NAME));
		}
		return STORE;
	}

	/**
	 * A store for all the instances of GAMA (shared across versions and applications)
	 *
	 */
	static class JRE extends GamaPreferenceStore<Preferences> {

		/**
		 * Instantiates a new jre.
		 *
		 * @param store
		 *            the store
		 */
		JRE(final Preferences store) {
			super(store);
		}

		@Override
		protected List<String> computeKeys() {
			try {
				return Arrays.asList(store.keys());
			} catch (BackingStoreException e) {
				return Collections.EMPTY_LIST;
			}
		}

		@Override
		public void put(final String key, final String value) {
			store.put(key, value);
			flush();
		}

		@Override
		public void putInt(final String key, final int value) {
			store.putInt(key, value);
			flush();
		}

		@Override
		public void putDouble(final String key, final Double value) {
			store.putDouble(key, value);
			flush();
		}

		@Override
		public void putBoolean(final String key, final Boolean value) {
			store.putBoolean(key, value);
			flush();
		}

		@Override
		public String getStringPreference(final String key, final String def) {
			return store.get(key, def);
		}

		@Override
		public Integer getIntPreference(final String key, final Integer def) {
			return store.getInt(key, def);
		}

		@Override
		public Double getDoublePreference(final String key, final Double def) {
			return store.getDouble(key, def);
		}

		@Override
		public Boolean getBooleanPreference(final String key, final Boolean def) {
			return store.getBoolean(key, def);
		}

		@Override
		public void flush() {
			try {
				store.flush();
			} catch (BackingStoreException e) {}
		}

		@Override
		public void clear() {
			try {
				store.removeNode();
			} catch (BackingStoreException e) {}
		}

		@Override
		public void loadFromProperties(final String path) {
			try (final var is = new FileInputStream(path);) {
				Preferences.importPreferences(is);
			} catch (final IOException | InvalidPreferencesFormatException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void saveToProperties(final String path) {
			try (final var os = new FileOutputStream(path);) {
				Properties prop = new Properties();
				for (String key : store.keys()) { prop.setProperty(key, store.get(key, null)); }
				prop.store(os, "GAMA Preferences " + LocalDateTime.now());
			} catch (final IOException | BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * A store for each instance of GAMA (shared across workspaces of this instance)
	 *
	 */

	static class Configuration extends GamaPreferenceStore<IEclipsePreferences> {

		/**
		 * Instantiates a new configuration.
		 *
		 * @param store
		 *            the store
		 */
		Configuration(final IEclipsePreferences store) {
			super(store);
		}

		@Override
		protected List<String> computeKeys() {
			try {
				return Arrays.asList(store.keys());
			} catch (org.osgi.service.prefs.BackingStoreException e) {
				return Collections.EMPTY_LIST;
			}
		}

		@Override
		public void put(final String key, final String value) {
			store.put(key, value);
			flush();
		}

		@Override
		public void putInt(final String key, final int value) {
			store.putInt(key, value);
			flush();
		}

		@Override
		public void putDouble(final String key, final Double value) {
			store.putDouble(key, value);
			flush();
		}

		@Override
		public void putBoolean(final String key, final Boolean value) {
			store.putBoolean(key, value);
			flush();
		}

		@Override
		public String getStringPreference(final String key, final String def) {
			return store.get(key, def);
		}

		@Override
		public Integer getIntPreference(final String key, final Integer def) {
			return store.getInt(key, def);
		}

		@Override
		public Double getDoublePreference(final String key, final Double def) {
			return store.getDouble(key, def);
		}

		@Override
		public Boolean getBooleanPreference(final String key, final Boolean def) {
			return store.getBoolean(key, def);
		}

		@Override
		public void flush() {
			try {
				store.flush();
			} catch (org.osgi.service.prefs.BackingStoreException e) {}
		}

		@Override
		public void clear() {
			try {
				store.removeNode();
			} catch (org.osgi.service.prefs.BackingStoreException e) {}
		}

		@Override
		public void loadFromProperties(final String path) {
			try (final var is = new FileInputStream(path);) {
				PreferencesService.getDefault().importPreferences(is);
			} catch (final IOException | CoreException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void saveToProperties(final String path) {
			try (final var os = new FileOutputStream(path);) {
				Properties prop = new Properties();
				for (String key : store.keys()) { prop.setProperty(key, store.get(key, null)); }
				prop.store(os, "GAMA Preferences " + LocalDateTime.now());
			} catch (final IOException | org.osgi.service.prefs.BackingStoreException e) {
				e.printStackTrace();
			}
		}

	}

	/** The store. */
	T store;

	/** The keys. */
	private final List<String> keys;

	/**
	 * Instantiates a new gama preference store.
	 *
	 * @param store
	 *            the store
	 */
	GamaPreferenceStore(final T store) {
		this.store = store;
		keys = computeKeys();
		flush();
	}

	/**
	 * Gets the keys.
	 *
	 * @return the keys
	 */
	public List<String> getKeys() { return keys; }

	/**
	 * Compute keys.
	 *
	 * @return the list
	 */
	protected abstract List<String> computeKeys();

	/**
	 * Put.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public abstract void put(final String key, final String value);

	/**
	 * Put int.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public abstract void putInt(final String key, final int value);

	/**
	 * Put double.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public abstract void putDouble(final String key, final Double value);

	/**
	 * Put boolean.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public abstract void putBoolean(final String key, final Boolean value);

	/**
	 * First searches if the preference is overriden in the system/VM properties/arguments, then looks into the store if
	 * not
	 *
	 * @param key
	 * @param def
	 * @return
	 */

	public final <T> T get(final String key, final Function<String, T> function, final T def) {
		String result = System.getProperty(key);
		return result == null ? def : function.apply(result);
	}

	/**
	 * Gets the.
	 *
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the string
	 */
	public final String get(final String key, final String def) {
		return get(key, identity(), getStringPreference(key, def));
	}

	/**
	 * Gets the string preference.
	 *
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the string preference
	 */
	protected abstract String getStringPreference(String key, String def);

	/**
	 * First searches if the preference is overriden in the system/VM properties/arguments, then looks into the store if
	 * not
	 *
	 * @param key
	 * @param def
	 * @return
	 */
	public final Integer getInt(final String key, final Integer def) {
		return get(key, Integer::valueOf, getIntPreference(key, def));
	}

	/**
	 * Gets the int preference.
	 *
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the int preference
	 */
	protected abstract Integer getIntPreference(String key, Integer def);

	/**
	 * First searches if the preference is overriden in the system/VM properties/arguments, then looks into the store if
	 * not
	 *
	 * @param key
	 * @param def
	 * @return
	 */
	public final Double getDouble(final String key, final Double def) {
		return get(key, Double::valueOf, getDoublePreference(key, def));
	}

	/**
	 * Gets the double preference.
	 *
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the double preference
	 */
	protected abstract Double getDoublePreference(String key, Double def);

	/**
	 * First searches if the preference is overriden in the system/VM properties/arguments, then looks into the store if
	 * not
	 *
	 * @param key
	 * @param def
	 * @return
	 */
	public final Boolean getBoolean(final String key, final Boolean def) {
		return get(key, Boolean::valueOf, getBooleanPreference(key, def));
	}

	/**
	 * Gets the boolean preference.
	 *
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the boolean preference
	 */
	protected abstract Boolean getBooleanPreference(String key, Boolean def);

	/**
	 * Makes sure preferences are kept in sync between GAMA runtime and the backend file
	 */

	public abstract void flush();

	/**
	 * Destroys the preferences node (all preferences are removed and replaced by defaults
	 */
	public abstract void clear();

	/**
	 * Exports the contents of the preferences as a properties (key = value) file, which can then be reloaded in another
	 * instance of GAMA
	 *
	 * @param path
	 */
	public abstract void saveToProperties(final String path);

	/**
	 * Reads a properties file and sets the contents of the preferences to the values registered in the file
	 *
	 * @param path
	 */
	public abstract void loadFromProperties(final String path);

	/**
	 * Write.
	 *
	 * @param gp
	 *            the gp
	 */
	public void write(final Pref gp) {
		final var key = gp.key;
		final var value = gp.value;
		switch (gp.type) {
			case IType.INT:
				putInt(key, (Integer) value);
				break;
			case IType.FLOAT:
				putDouble(key, (Double) value);
				break;
			case IType.BOOL:
				putBoolean(key, (Boolean) value);
				break;
			case IType.STRING:
				put(key, toJavaString((String) value));
				break;
			case IType.FILE:
				put(key, value == null ? "" : ((GamaFile) value).getPath(null));
				break;
			case IType.COLOR:
				putInt(key, value == null ? 0 : ((GamaColor) value).getRGB());
				break;
			case IType.POINT:
				put(key, value==null ? "{0,0}" : ((GamaPoint) value).stringValue(null));
				break;
			case IType.FONT:
				put(key, value == null ? DEFAULT_FONT : value.toString());
				break;
			case IType.DATE:
				put(key, value== null ? toJavaString(GamaDateType.EPOCH.toISOString()): toJavaString(((GamaDate) value).toISOString()));
				break;
			default:
				put(key, (String) value);
		}
		flush();
	}

	/**
	 * Register.
	 *
	 * @param gp
	 *            the gp
	 */
	public void register(final Pref<?> gp) {
		final IScope scope = null;
		final var key = gp.key;
		if (key == null) return;
		final var value = gp.value;
		if (getKeys().contains(key)) {
			switch (gp.type) {
				case IType.POINT:
					gp.init((ValueProvider) () -> Cast.asPoint(scope, get(key, asString(scope, value)), false));
					break;
				case IType.INT:
					gp.init((ValueProvider) () -> getInt(key, asInt(scope, value)));
					break;
				case IType.FLOAT:
					gp.init((ValueProvider) () -> getDouble(key, Cast.asFloat(scope, value)));
					break;
				case IType.BOOL:
					gp.init((ValueProvider) () -> getBoolean(key, Cast.asBool(scope, value)));
					break;
				case IType.STRING:
					gp.init((ValueProvider) () -> get(key, toJavaString(asString(scope, value))));
					break;
				case IType.FILE:
					gp.init((ValueProvider) () -> new GenericFile(get(key, (String) value), false));
					break;
				case IType.COLOR:
					gp.init((ValueProvider) () -> GamaColor.getInt(getInt(key, asInt(scope, value))));
					break;
				case IType.FONT:
					gp.init((ValueProvider) () -> {
						final var font = get(key, asString(scope, value));
						if (DEFAULT_FONT.equals(font)) return null;
						return GamaFontType.staticCast(scope, font, false);
					});
					break;
				case IType.DATE:
					gp.init((ValueProvider) () -> GamaDate
							.fromISOString(toJavaString(get(key, asString(scope, value)))));
					break;
				default:
					gp.init((ValueProvider) () -> get(key, asString(scope, value)));
			}
		}
		flush();
	}

}