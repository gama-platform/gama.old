/*********************************************************************************************
 * 
 * 
 * 'GamaPreferences.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.common;

import gnu.trove.map.hash.THashMap;
import java.awt.Color;
import java.util.*;
import java.util.prefs.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import org.geotools.referencing.CRS;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Class GamaPreferencesView.
 * 
 * @author drogoul
 * @since 26 août 2013
 * 
 */
public class GamaPreferences {

	public static final String GENERAL = "General";
	public static final String DISPLAY = "Display";
	public static final String CODE = "Code";
	public static final String EDITOR = "Editor";
	public static final String WORKSPACE = "Workspace";
	public static final String LIBRARIES = "External";
	private static Preferences store = Preferences.userRoot().node("gama");
	private static Map<String, Entry> prefs = new LinkedHashMap();
	private static List<String> storeKeys;

	static {

		try {
			storeKeys = new GamaList(store.keys());
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static <T> Entry<T> create(final String key, final T value) {
		return create(key, value, IType.STRING);
	}

	public static <T> Entry<T> create(final String key, final T value, final int type) {
		return create(key, "Value of " + key, value, type);
	}

	public static <T> Entry<T> create(final String key, final String title, final T value, final int type) {
		Entry e = new Entry(key).named(title).in(GENERAL).init(value).typed(type);
		register(e);
		return e;
	}

	public static interface IPreferenceChange<T> {

		/**
		 * A change listener, that receives the valueChange() message before the preference is assigned a new value,
		 * with this value in parameter. Returning true will enable the change, returning false will veto it. Only one
		 * change listener can be registered for a preference.
		 * @param newValue, the new value set to this preference
		 * @return true or false, wheter or not the change is accepted by the listener.
		 */
		public boolean valueChange(T newValue);
	}

	public static class GenericFile extends GamaFile {

		public GenericFile(final String pathName) throws GamaRuntimeException {
			super(null, pathName);
		}

		@Override
		public Envelope computeEnvelope(final IScope scope) {
			return new Envelope(0, 0, 0, 0);
		}

		@Override
		protected void fillBuffer(final IScope scope) throws GamaRuntimeException {}

		@Override
		protected void flushBuffer() throws GamaRuntimeException {}

	}

	public static class Entry<T> implements IParameter {

		String key, title, tab, group;
		T value, initial;
		IType type;
		List<T> values;
		Number min, max;
		String[] activates, deactivates;
		IPreferenceChange<T> onChange;

		private Entry(final String key) {
			tab = GENERAL;
			this.key = key;
		}

		public Entry group(final String group) {
			this.group = group;
			return this;
		}

		public Entry among(final T ... values) {
			return among(new GamaList(values));
		}

		public Entry among(final List<T> values) {
			this.values = values;
			return this;
		}

		public Entry between(final Number min, final Number max) {
			this.min = min;
			this.max = max;
			return this;
		}

		public Entry in(final String category) {
			this.tab = category;
			return this;
		}

		public Entry named(final String title) {
			this.title = title;
			return this;
		}

		public Entry init(final T value) {
			this.initial = value;
			this.value = value;
			return this;
		}

		public Entry set(final T value) {
			this.value = value;
			return this;
		}

		public Entry typed(final int type) {
			this.type = Types.get(type);
			return this;
		}

		public Entry activates(final String ... link) {
			activates = link;
			return this;
		}

		public Entry deactivates(final String ... link) {
			deactivates = link;
			return this;
		}

		public T getValue() {
			return value;
		}

		@Override
		public IType getType() {
			return type;
		}

		@Override
		public String getTitle() {
			return title;
		}

		public String getKey() {
			return key;
		}

		public List<T> getValues() {
			return values;
		}

		@Override
		public String getName() {
			return key;
		}

		@Override
		public String getCategory() {
			return group;
		}

		@Override
		public String getUnitLabel() {
			return null;
		}

		@Override
		public void setUnitLabel(final String label) {}

		@Override
		public Integer getDefinitionOrder() {
			return 0;
		}

		@Override
		public void setValue(final IScope scope, final Object value) {
			this.value = (T) value;
		}

		public Entry onChange(final IPreferenceChange<T> r) {
			onChange = r;
			return this;
		}

		@Override
		public Object value(final IScope scope) throws GamaRuntimeException {
			return value;
		}

		// @Override
		// public IType getContentType() {
		// return Types.NO_TYPE;
		// }

		@Override
		public String toGaml() {
			return Cast.toGaml(value);
		}

		@Override
		public Object getInitialValue(final IScope scope) {
			return initial;
		}

		@Override
		public Number getMinValue() {
			return min;
		}

		@Override
		public Number getMaxValue() {
			return max;
		}

		@Override
		public List getAmongValue() {
			return values;
		}

		@Override
		public boolean isEditable() {
			return true;
		}

		@Override
		public boolean isDefined() {
			return true;
		}

		@Override
		public void setDefined(final boolean b) {}

		@Override
		public Number getStepValue() {
			return null;
		}

		/**
		 * If the value is modified in the view, this method is called. Should return true to accept the change, false
		 * otherwise
		 */
		public boolean acceptChange(final T newValue) {
			if ( onChange != null ) { return onChange.valueChange(newValue); }
			return true;
		}

		public String[] getActivable() {
			return this.activates;
		}

		public String[] getDeactivable() {
			return this.deactivates;
		}

		public void save() {
			Map<String, Object> map = new THashMap();
			map.put(getName(), getValue());
			GamaPreferences.setNewPreferences(map);
		}
	}

	/**
	 * Definition of the preferences contributed by msi.gama.core
	 */

	// GENERAL PAGE
	public static final List<String> GENERATOR_NAMES = Arrays.asList(IKeyword.CELLULAR, IKeyword.XOR, IKeyword.JAVA,
		IKeyword.MERSENNE);
	/**
	 * Random Number Generation
	 */
	public static final Entry<String> CORE_RNG = create("core.rng", "Random number generator", IKeyword.MERSENNE,
		IType.STRING).among(GENERATOR_NAMES).in(GENERAL).group("Random Number Generation");
	public static final Entry<Boolean> CORE_SEED_DEFINED = create("core.seed_defined", "Define a default seed", false,
		IType.BOOL).activates("core.seed").in(GENERAL).group("Random Number Generation");
	public static final Entry<Double> CORE_SEED = create("core.seed", "Default seed value", 0d, IType.FLOAT)
		.in(GENERAL).group("Random Number Generation");
	public static final Entry<Boolean> CORE_RND_EDITABLE = create("core.define_rng",
		"Include in the parameters of models", true, IType.BOOL).in(GENERAL).group("Random Number Generation");
	/**
	 * User Interface
	 */
	public static final Entry<Integer> CORE_MENU_SIZE = create("core.menu_size", "Break down agents in menus every",
		50, IType.INT).between(10, 100).in(GENERAL).group("User interface");
	/**
	 * Simulation Errors
	 */
	public static final Entry<Boolean> CORE_SHOW_ERRORS = create("core.display_errors", "Display errors", true,
		IType.BOOL).in(GENERAL).activates("core.errors_number", "core.recent").group("Simulation errors");
	public static final Entry<Integer> CORE_ERRORS_NUMBER = create("core.errors_number", "Number of errors to display",
		10, IType.INT).in(GENERAL).group("Simulation errors").between(1, null);
	public static final Entry<Boolean> CORE_RECENT = create("core.recent", "Display most recent first", true,
		IType.BOOL).in(GENERAL).group("Simulation errors");
	public static final Entry<Boolean> CORE_REVEAL_AND_STOP = create("core.stop", "Stop simulation at first error",
		true, IType.BOOL).in(GENERAL).group("Simulation errors");
	public static final Entry<Boolean> CORE_WARNINGS = create("core.warnings", "Treat warnings as errors", false,
		IType.BOOL).in(GENERAL).group("Simulation errors");
	/**
	 * Startup
	 */
	public static final Entry<Boolean> CORE_SHOW_PAGE = create("core.show_page", "Display Welcome page at startup",
		true, IType.BOOL).in(GENERAL).group("Startup");
	/**
	 * Runtime
	 */
	public static final Entry<Double> CORE_DELAY_STEP = create("core.delay_step",
		"Default step for delay slider (in sec.)", 0.01, IType.FLOAT).in(GENERAL).group("Runtime");
	public static final Entry<Boolean> CORE_AUTO_RUN = create("core.auto_run",
		"Auto-run experiments when they are launched", false, IType.BOOL).in(GENERAL).group("Runtime");
	public static final Entry<Boolean> CORE_ASK_CLOSING = create("core.ask_closing",
		"Ask to close the previous simulation before launching a new one ?", true, IType.BOOL).in(GENERAL).group(
		"Runtime");

	// DISPLAY PAGE
	/**
	 * Properties
	 */
	public static final Entry<String> CORE_DISPLAY = create("core.display",
		"Default display method when none is specified", "Java2D", IType.STRING).among("Java2D", "OpenGL").in(DISPLAY)
		.group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_SYNC = create("core.sync", "Synchronize displays with simulations", false,
		IType.BOOL).in(DISPLAY).group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_OVERLAY = create("core.overlay", "Show display overlay", false, IType.BOOL)
		.in(DISPLAY).activates("core.scale").group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_SCALE =
		create("core.scale", "Show scale bar in overlay", false, IType.BOOL).in(DISPLAY).group(
			"Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_ANTIALIAS = create("core.antialias", "Apply antialiasing", false,
		IType.BOOL).in(DISPLAY).group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Color> CORE_BACKGROUND = create("core.background", "Default background color",
		Color.white, IType.COLOR).in(DISPLAY).group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Color> CORE_HIGHLIGHT = create("core.highlight", "Default highlight color",
		new Color(0, 200, 200), IType.COLOR).in(DISPLAY).group(
		"Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_DISPLAY_ORDER = create("core.display_order",
		"Stack displays on screen in the order defined by the model", true, IType.BOOL).in(DISPLAY).group(
		"Properties (settings effective after experiment relaunch)");
	/**
	 * Default Aspect
	 */
	public static final Entry<String> CORE_SHAPE = create("core.shape", "Defaut shape to use for agents", "shape",
		IType.STRING).among("shape", "circle", "square", "triangle", "point", "cube", "sphere").in(DISPLAY)
		.group("Default aspect (settings effective after experiment relaunch)");
	public static final Entry<Double> CORE_SIZE = create("core.size", "Default size to use for agents", 1.0,
		IType.FLOAT).between(0.01, null).in(DISPLAY)
		.group("Default aspect (settings effective after experiment relaunch)");
	public static final Entry<Color> CORE_COLOR = create("core.color", "Default color to use for agents", Color.yellow,
		IType.COLOR).in(DISPLAY).group("Default aspect (settings effective after experiment relaunch)");
	/**
	 * OpenGL
	 */
	public static final Entry<Boolean> CORE_Z_FIGHTING = create("core.z_fighting", "Use improved z positioning", true,
		IType.BOOL).in(DISPLAY).group("OpenGL (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_DRAW_ENV = create("core.draw_env", "Draw 3D referential", true, IType.BOOL)
		.in(DISPLAY).group("OpenGL (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_SHOW_FPS = create("core.show_fps", "Show number of frames per second",
		false, IType.BOOL).in(DISPLAY).group("OpenGL (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_IS_LIGHT_ON = create("core.islighton", "Enable lighting", true, IType.BOOL)
		.in(DISPLAY).group("OpenGL (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_DRAW_NORM = create("core.draw_norm", "Draw normals to objects", false,
		IType.BOOL).in(DISPLAY).group("OpenGL (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_CUBEDISPLAY = create("core.cubedisplay", "Display as a cube", false,
		IType.BOOL).in(DISPLAY).group("OpenGL (settings effective after experiment relaunch)");

	// EDITOR PAGE
	public static final Entry<Boolean> CORE_PERSPECTIVE = create("core.perspective",
		"Automatically switch to modeling perspective when editing a model", false, IType.BOOL).in(EDITOR).group(
		"Options");

	// LIBRARIES PAGE
	/**
	 * Spatialite
	 */
	public static final Entry<String> LIB_SPATIALITE = create("core.lib_spatialite",
		"Path to the Spatialite (see http://www.gaia-gis.it/gaia-sins/) library",
		new GenericFile("Please select the path"), IType.FILE).in(LIBRARIES).group("Paths");
	/**
	 * R
	 */
	public static final Entry<String> LIB_R = create("core.lib_r",
		"Path to the RScript (see http://www.r-project.org) library", new GenericFile(getDefaultRPath()), IType.FILE)
		.in(LIBRARIES).group("Paths");
	/**
	 * GeoTools
	 */
	public static final Entry<Boolean> LIB_TARGETED = create("core.lib_targeted",
		"Let GAMA decide which CRS to use to project GIS data", true, IType.BOOL).deactivates("core.lib_target_crs")
		.in(LIBRARIES)
		.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)");
	public static final Entry<Integer> LIB_TARGET_CRS = create("core.lib_target_crs",
		"...or use the following CRS (EPSG code)", 32648, IType.INT).in(LIBRARIES)
		.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)")
		.onChange(new IPreferenceChange<Integer>() {

			@Override
			public boolean valueChange(final Integer newValue) {
				Set<String> codes = CRS.getSupportedCodes(newValue.toString());
				if ( codes.isEmpty() ) { return false; }
				return true;
			}
		});
	public static final Entry<Boolean> LIB_PROJECTED =
		create("core.lib_projected",
			"When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS", true,
			IType.BOOL).deactivates("core.lib_initial_crs").in(LIBRARIES)
			.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)");
	public static final Entry<Integer> LIB_INITIAL_CRS = create("core.lib_initial_crs",
		"...or use the following CRS (EPSG code)", 4326, IType.INT).in(LIBRARIES)
		.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)")
		.onChange(new IPreferenceChange<Integer>() {

			@Override
			public boolean valueChange(final Integer newValue) {
				Set<String> codes = CRS.getSupportedCodes(newValue.toString());
				if ( codes.isEmpty() ) { return false; }
				return true;
			}
		});
	public static final Entry<Boolean> LIB_USE_DEFAULT = create("core.lib_use_default",
		"When no CRS is provided, save the GIS data with the current CRS", true, IType.BOOL)
		.deactivates("core.lib_output_crs").in(LIBRARIES)
		.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)");
	public static final Entry<Integer> LIB_OUTPUT_CRS = create("core.lib_output_crs",
		"... or use this following CRS (EPSG code)", 4326, IType.INT).in(LIBRARIES)
		.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)")
		.onChange(new IPreferenceChange<Integer>() {

			@Override
			public boolean valueChange(final Integer newValue) {
				Set<String> codes = CRS.getSupportedCodes(newValue.toString());
				if ( codes.isEmpty() ) { return false; }
				return true;
			}
		});

	private static String getDefaultRPath() {
		String os = System.getProperty("os.name");
		String osbit = System.getProperty("os.arch");
		if ( os.startsWith("Mac") ) {
			if ( osbit.endsWith("64") ) { return "/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/x86_64/RScript"; }
			return "/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/i386/RScript";
		} else if ( os.startsWith("Linux") ) { return "usr/bin/RScript"; }
		if ( os.startsWith("Windows") ) {
			if ( osbit.endsWith("64") ) { return "C:\\Program Files\\R\\R-2.15.1\\bin\\x64\\Rscript.exe"; }
			return "C:\\Program Files\\R\\R-2.15.1\\bin\\Rscript.exe";
		}
		return "";
	}

	private static void register(final Entry gp) {
		IScope scope = GAMA.obtainNewScope();
		prefs.put(gp.getKey(), gp);
		String key = gp.key;
		Object value = gp.value;
		switch (gp.type.id()) {
			case IType.INT:
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, store.getInt(key, Cast.as(value, Integer.class)));
				} else {
					store.putInt(key, Cast.as(value, Integer.class));
				}
				break;
			case IType.FLOAT:
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, store.getDouble(key, Cast.as(value, Double.class)));
				} else {
					store.putDouble(key, Cast.as(value, Double.class));
				}
				break;
			case IType.BOOL:
				value = Cast.asBool(scope, value);
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, store.getBoolean(key, Cast.as(value, Boolean.class)));
				} else {
					store.putBoolean(key, Cast.as(value, Boolean.class));
				}
				break;
			case IType.STRING:
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, store.get(key, Cast.as(value, String.class)));
				} else {
					store.put(key, (String) value);
				}
				break;
			case IType.FILE:
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, new GenericFile(store.get(key, "")));
				} else {
					store.put(key, ((IGamaFile) value).getPath());
				}
				break;
			case IType.COLOR:
				// Stores the preference as an int but create a color
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, GamaColor.getInt(store.getInt(key, Cast.as(value, Integer.class))));
				} else {
					store.putInt(key, Cast.as(value, Integer.class));
				}
				break;
			default:
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, store.get(key, Cast.as(value, String.class)));
				} else {
					store.put(key, Cast.as(value, String.class));
				}
		}
		if ( scope != null ) {
			GAMA.releaseScope(scope);
		}
		try {
			store.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static void writeToStore(final Entry gp) {
		String key = gp.key;
		Object value = gp.value;
		switch (gp.type.id()) {
			case IType.INT:
				store.putInt(key, (Integer) value);
				break;
			case IType.FLOAT:
				store.putDouble(key, (Double) value);
				break;
			case IType.BOOL:
				store.putBoolean(key, (Boolean) value);
				break;
			case IType.STRING:
				store.put(key, (String) value);
				break;
			case IType.FILE:
				store.put(key, ((GamaFile) value).getPath());
				break;
			case IType.COLOR:
				// Stores the preference as an int but create a color
				int code = ((Color) value).getRGB();
				store.putInt(key, code);
				break;
			default:
				store.put(key, (String) value);
		}
	}

	public static Map<String, Map<String, List<Entry>>> organizePrefs() {
		Map<String, Map<String, List<Entry>>> result = new TOrderedHashMap();
		for ( Entry e : prefs.values() ) {
			String tab = e.tab;
			Map<String, List<Entry>> groups = result.get(tab);
			if ( groups == null ) {
				groups = new TOrderedHashMap();
				result.put(tab, groups);
			}
			String group = e.group;
			List<Entry> in_group = groups.get(group);
			if ( in_group == null ) {
				in_group = new ArrayList();
				groups.put(group, in_group);
			}
			in_group.add(e);
		}
		return result;
	}

	public static void setNewPreferences(final Map<String, Object> modelValues) {
		for ( String name : modelValues.keySet() ) {
			Entry e = prefs.get(name);
			if ( e == null ) {
				continue;
			}
			e.set(modelValues.get(name));
			writeToStore(e);
			try {
				store.flush();
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	public static void revertToDefaultValues(final Map<String, Object> modelValues) {
		for ( String name : modelValues.keySet() ) {
			Entry e = prefs.get(name);
			if ( e == null ) {
				continue;
			}
			modelValues.put(name, e.initial);
		}

	}

}
