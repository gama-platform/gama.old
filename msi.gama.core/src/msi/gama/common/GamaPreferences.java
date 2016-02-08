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

import java.awt.Color;
import java.util.*;
import java.util.prefs.*;
import org.geotools.referencing.CRS;
import com.vividsolutions.jts.geom.Envelope;
import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gaml.types.*;

/**
 * Class GamaPreferencesView.
 *
 * @author drogoul
 * @since 26 août 2013
 *
 */
public class GamaPreferences {

	// public static final String GENERAL = "General";
	public static final String UI = "UI";
	public static final String EXPERIMENTS = "Experiments";
	public static final String SIMULATIONS = "Simulations";
	public static final String DISPLAY = "Display";
	// public static final String CODE = "Code";
	public static final String EDITOR = "Editor";
	// public static final String WORKSPACE = "Workspace";
	public static final String LIBRARIES = "External";
	public static final String CHARTS = "Charts";
	static Preferences store;
	private static Map<String, Entry> prefs = new LinkedHashMap();
	private static List<String> storeKeys;

	static {

		try {
			store = Preferences.userRoot().node("gama");
			try {
				store.flush();
			} catch (BackingStoreException e1) {
				e1.printStackTrace();
			}
			try {
				storeKeys = Arrays.asList(store.keys());
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public static <T> Entry<T> get(final String key, final Class<T> clazz) {
		return prefs.get(key);
	}

	//
	// public static <T> Entry<T> create(final String key, final T value) {
	// return create(key, value, IType.STRING);
	// }

	// public static <T> Entry<T> create(final String key, final T value, final int type) {
	// return create(key, "Value of " + key, value, type);
	// }

	public static <T> Entry<T> create(final String key, final String title, final T value, final int type) {
		Entry e = new Entry(key, type).named(title).in(UI).init(value);
		register(e);
		return e;
	}

	public static interface IPreferenceChangeListener<T> {

		/**
		 * A change listener, that receives the beforeValueChange() message before the preference is assigned a new value,
		 * with this value in parameter. Returning true will enable the change, returning false will veto it.
		 * @param newValue, the new value set to this preference
		 * @return true or false, wheter or not the change is accepted by the listener.
		 */
		public boolean beforeValueChange(T newValue);

		/**
		 * A change listener, that receives the afterValueChange() message after the preference is assigned a new value,
		 * with this value in parameter, in order to perform anything needed for this change.
		 * @param newValue, the new value set to this preference
		 * @return true or false, wheter or not the change is accepted by the listener.
		 */
		public void afterValueChange(T newValue);
	}

	public static class GenericFile extends GamaFile {

		public GenericFile(final String pathName) throws GamaRuntimeException {
			super(null, pathName);
		}

		@Override
		public IContainerType getType() {
			return Types.FILE;
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
		final int type;
		List<T> values;
		Number min, max;
		String[] activates, deactivates;
		Set<IPreferenceChangeListener<T>> listeners = new HashSet();

		private Entry(final String key, final int type) {
			tab = UI;
			this.type = type;
			this.key = key;
		}

		public Entry group(final String group) {
			this.group = group;
			return this;
		}

		public Entry among(final T ... values) {
			return among(Arrays.asList(values));
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
			if ( isValueChanged(value) && acceptChange(value) ) {
				this.value = value;
				afterChange(value);
			}
			return this;
		}

		private boolean isValueChanged(final T newValue) {
			return value == null ? newValue != null : !value.equals(newValue);
		}

		// public Entry typed(final int type) {
		// this.type = Types.get(type);
		// return this;
		// }

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
			return Types.get(type);
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
			set((T) value);
		}

		public Entry addChangeListener(final IPreferenceChangeListener<T> r) {
			listeners.add(r);
			return this;
		}

		public void removeChangeListener(final IPreferenceChangeListener<T> r) {
			listeners.remove(r);
		}

		@Override
		public T value(final IScope scope) throws GamaRuntimeException {
			return getValue();
		}

		// @Override
		// public IType getContentType() {
		// return Types.NO_TYPE;
		// }

		@Override
		public String serialize(final boolean includingBuiltIn) {
			return StringUtils.toGaml(value, includingBuiltIn);
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
		 * If the value is modified, this method is called. Should return true to accept the change, false
		 * otherwise
		 */
		public boolean acceptChange(final T newValue) {
			for ( IPreferenceChangeListener<T> listener : listeners ) {
				if ( !listener.beforeValueChange(newValue) ) { return false; }
			}
			return true;
		}

		protected void afterChange(final T newValue) {
			for ( IPreferenceChangeListener<T> listener : listeners ) {
				listener.afterValueChange(newValue);
			}
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

	// SIMULATIONS PAGE
	// Corresponds to IGamaColors.BLUE.toGamaColor(), IGamaColors.OK.toGamaColor(),
	// IGamaColors.NEUTRAL.toGamaColor(), IGamaColors.WARNING.toGamaColor(), IGamaColors.BROWN.toGamaColor()
	public static final Color[] BASIC_COLORS = new Color[] { new Color(74, 97, 144), new Color(66, 119, 42),
		new Color(83, 95, 107), new Color(195, 98, 43), new Color(150, 132, 106) };
	public static final Entry<Color>[] SIMULATION_COLORS = new Entry[5];

	static {
		for ( int i = 0; i < 5; i++ ) {
			SIMULATION_COLORS[i] =
				create("simulation.ui.color" + i, "Color of Simulation " + i, BASIC_COLORS[i], IType.COLOR)
					.in(SIMULATIONS).group("Simulation colors in the interface (console, views)");
		}
	}

	// GENERAL PAGE
	public static final List<String> GENERATOR_NAMES =
		Arrays.asList(IKeyword.CELLULAR, IKeyword.JAVA, IKeyword.MERSENNE);
	/**
	 * Random Number Generation
	 */
	public static final Entry<String> CORE_RNG =
		create("core.rng", "Random number generator", IKeyword.MERSENNE, IType.STRING).among(GENERATOR_NAMES)
			.in(EXPERIMENTS).group("Random Number Generation");
	public static final Entry<Boolean> CORE_SEED_DEFINED =
		create("core.seed_defined", "Define a default seed", false, IType.BOOL).activates("core.seed").in(EXPERIMENTS)
			.group("Random Number Generation");
	public static final Entry<Double> CORE_SEED =
		create("core.seed", "Default seed value (0 means undefined)", 1d, IType.FLOAT).in(EXPERIMENTS)
			.group("Random Number Generation");
	public static final Entry<Boolean> CORE_RND_EDITABLE =
		create("core.define_rng", "Include in the parameters of models", true, IType.BOOL).in(EXPERIMENTS)
			.group("Random Number Generation");
	/**
	 * User Interface
	 */
	public static final Entry<Integer> CORE_MENU_SIZE =
		create("core.menu_size", "Break down agents in menus every", 50, IType.INT).between(10, 100).in(UI)
			.group("Menus");

	public static final Entry<Integer> CORE_CONSOLE_SIZE =
		create("core.console_size", "Max. number of characters to display in the console (-1 means no limit) ", 20000,
			IType.INT).in(UI).group("Console");
	public static final Entry<Integer> CORE_CONSOLE_BUFFER = create("core.console_buffer",
		"Max. number of characters to keep in memory when console is paused (-1 means no limit)", 20000, IType.INT)
			.in(UI).group("Console");
	/**
	 * Simulation Errors
	 */
	public static final Entry<Boolean> CORE_SHOW_ERRORS =
		create("core.display_errors", "Display errors", true, IType.BOOL).in(EXPERIMENTS)
			.activates("core.errors_number", "core.recent").group("Errors");
	public static final Entry<Integer> CORE_ERRORS_NUMBER =
		create("core.errors_number", "Number of errors to display", 10, IType.INT).in(EXPERIMENTS).group("Errors")
			.between(1, null);
	public static final Entry<Boolean> CORE_RECENT =
		create("core.recent", "Display most recent first", true, IType.BOOL).in(EXPERIMENTS).group("Errors");
	public static final Entry<Boolean> CORE_REVEAL_AND_STOP =
		create("core.stop", "Stop simulation at first error", true, IType.BOOL).in(EXPERIMENTS).group("Errors");
	public static final Entry<Boolean> CORE_WARNINGS =
		create("core.warnings", "Treat warnings as errors", false, IType.BOOL).in(EXPERIMENTS).group("Errors");
	/**
	 * Startup
	 */
	public static final Entry<Boolean> CORE_SHOW_PAGE =
		create("core.show_page", "Display Welcome page at startup", true, IType.BOOL).in(UI).group("Startup");
	/**
	 * Runtime
	 */
	public static final Entry<Double> CORE_DELAY_STEP =
		create("core.delay_step", "Default step for delay slider (in sec.)", 0.01, IType.FLOAT).in(EXPERIMENTS)
			.group("Runtime");
	public static final Entry<Boolean> CORE_AUTO_RUN =
		create("core.auto_run", "Auto-run experiments when they are launched", false, IType.BOOL).in(EXPERIMENTS)
			.group("Runtime");
	public static final Entry<Boolean> CORE_ASK_CLOSING =
		create("core.ask_closing", "Ask to close the previous simulation before launching a new one ?", true,
			IType.BOOL).in(EXPERIMENTS).group("Runtime");

	// DISPLAY PAGE
	/**
	 * Properties
	 */
	public static final Entry<Boolean> CORE_DISPLAY_PERSPECTIVE =
		create("core.display_perspective", "Continue to draw displays (in the background) when in Modeling perspective",
			false, IType.BOOL).in(DISPLAY).group("Behavior of displays");
	public static final Entry<String> CORE_DISPLAY =
		create("core.display", "Default display method when none is specified", "Java2D", IType.STRING)
			.among("Java2D", "OpenGL").in(DISPLAY).group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_SYNC =
		create("core.sync", "Synchronize displays with simulations", false, IType.BOOL).in(DISPLAY)
			.group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_OVERLAY = create("core.overlay", "Show display overlay", false, IType.BOOL)
		.in(DISPLAY).activates("core.scale").group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_SCALE = create("core.scale", "Show scale bar in overlay", false, IType.BOOL)
		.in(DISPLAY).group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_ANTIALIAS =
		create("core.antialias", "Apply antialiasing", false, IType.BOOL).in(DISPLAY)
			.group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Color> CORE_BACKGROUND =
		create("core.background", "Default background color", Color.white, IType.COLOR).in(DISPLAY)
			.group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Color> CORE_HIGHLIGHT =
		create("core.highlight", "Default highlight color", new Color(0, 200, 200), IType.COLOR).in(DISPLAY)
			.group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_DISPLAY_ORDER =
		create("core.display_order", "Stack displays on screen in the order defined by the model", true, IType.BOOL)
			.in(DISPLAY).group("Properties (settings effective after experiment relaunch)");
	public static final Entry<Boolean> DISPLAY_ONLY_VISIBLE =
		create("core.display_visible", "Only process for display the agents that are visible", false, IType.BOOL)
			.in(DISPLAY).group("*** Experimental ***");
	/**
	 * Default Aspect
	 */
	public static final Entry<String> CORE_SHAPE =
		create("core.shape", "Defaut shape to use for agents", "shape", IType.STRING)
			.among("shape", "circle", "square", "triangle", "point", "cube", "sphere").in(DISPLAY)
			.group("Default aspect (settings effective after experiment relaunch)");
	public static final Entry<Double> CORE_SIZE =
		create("core.size", "Default size to use for agents", 1.0, IType.FLOAT).between(0.01, null).in(DISPLAY)
			.group("Default aspect (settings effective after experiment relaunch)");
	public static final Entry<Color> CORE_COLOR =
		create("core.color", "Default color to use for agents", Color.yellow, IType.COLOR).in(DISPLAY)
			.group("Default aspect (settings effective after experiment relaunch)");
	/**
	 * OpenGL
	 */
	// public static final Entry<Boolean> CORE_Z_FIGHTING =
	// create("core.z_fighting", "Use improved z positioning", true, IType.BOOL).in(DISPLAY)
	// .group("OpenGL (settings effective after experiment relaunch)");
	public static final Entry<Boolean> CORE_DRAW_ENV = create("core.draw_env", "Draw 3D referential", true, IType.BOOL)
		.in(DISPLAY).group("OpenGL (settings effective after experiment relaunch)");
	// public static final Entry<Boolean> CORE_SHOW_FPS =
	// create("core.show_fps", "Show number of frames per second", false, IType.BOOL).in(DISPLAY)
	// .group("OpenGL (settings effective after experiment relaunch)");
	// public static final Entry<Boolean> CORE_IS_LIGHT_ON = create("core.islighton", "Enable lighting", true, IType.BOOL)
	// .in(DISPLAY).group("OpenGL (settings effective after experiment relaunch)");
	public static final Entry<Double> OPENGL_ZOOM =
		create("opengl.zoom", "Set the zoom factor to use (from 0 for a slow zoom to 1 for a fast one)", 0.5,
			IType.FLOAT).in(DISPLAY).group("OpenGL (settings effective immediately)").between(0, 1);
	public static final Entry<Integer> OPENGL_FPS =
		create("opengl.fps", "Set the maximum number of frames per second to display", 20, IType.INT).in(DISPLAY)
			.group("OpenGL (settings effective after experiment relaunch)");
	public static final Entry<Double> CORE_LINE_WIDTH =
		create("opengl.line.width", "Set the width of lines drawn on displays", 1.2d, IType.FLOAT).in(DISPLAY)
			.group("OpenGL (settings effective immediately)");
	// public static final Entry<Boolean> CORE_DRAW_NORM =
	// create("core.draw_norm", "Draw normals to objects", false, IType.BOOL).in(DISPLAY)
	// .group("OpenGL (settings effective after experiment relaunch)");
	// public static final Entry<Boolean> CORE_CUBEDISPLAY =
	// create("core.cubedisplay", "Display as a cube", false, IType.BOOL).in(DISPLAY)
	// .group("OpenGL (settings effective after experiment relaunch)");

	// EDITOR PAGE
	public static final Entry<Boolean> CORE_PERSPECTIVE =
		create("core.perspective", "Automatically switch to modeling perspective when editing a model", false,
			IType.BOOL).in(EDITOR).group("Options");

	/**
	 * Validation
	 */
	public static final GamaPreferences.Entry<Boolean> WARNINGS_ENABLED =
		GamaPreferences.create("editor.warnings.enabled", "Show warning markers when editing a model", true, IType.BOOL)
			.in(GamaPreferences.EDITOR).group("Validation");

	public static final GamaPreferences.Entry<Boolean> INFO_ENABLED =
		GamaPreferences.create("editor.info.enabled", "Show information markers when editing a model", true, IType.BOOL)
			.in(GamaPreferences.EDITOR).group("Validation");

	// LIBRARIES PAGE
	/**
	 * Spatialite
	 */
	public static final Entry<IGamaFile> LIB_SPATIALITE =
		create("core.lib_spatialite", "Path to the Spatialite (see http://www.gaia-gis.it/gaia-sins/) library",
			new GenericFile("Please select the path"), IType.FILE).in(LIBRARIES).group("Paths");
	/**
	 * R
	 */
	public static final Entry<IGamaFile> LIB_R =
		create("core.lib_r", "Path to the RScript (see http://www.r-project.org) library",
			new GenericFile(getDefaultRPath()), IType.FILE).in(LIBRARIES).group("Paths");
	/**
	 * GeoTools
	 */
	public static final Entry<Boolean> LIB_TARGETED =
		create("core.lib_targeted", "Let GAMA decide which CRS to use to project GIS data", true, IType.BOOL)
			.deactivates("core.lib_target_crs").in(LIBRARIES)
			.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)");
	public static final Entry<Integer> LIB_TARGET_CRS =
		create("core.lib_target_crs", "...or use the following CRS (EPSG code)", 32648, IType.INT).in(LIBRARIES)
			.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)")
			.addChangeListener(new IPreferenceChangeListener<Integer>() {

				@Override
				public boolean beforeValueChange(final Integer newValue) {
					Set<String> codes = CRS.getSupportedCodes(newValue.toString());
					if ( codes.isEmpty() ) { return false; }
					return true;
				}

				@Override
				public void afterValueChange(final Integer newValue) {}
			});
	public static final Entry<Boolean> LIB_PROJECTED = create("core.lib_projected",
		"When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS", true, IType.BOOL)
			.deactivates("core.lib_initial_crs").in(LIBRARIES)
			.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)");
	public static final Entry<Integer> LIB_INITIAL_CRS =
		create("core.lib_initial_crs", "...or use the following CRS (EPSG code)", 4326, IType.INT).in(LIBRARIES)
			.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)")
			.addChangeListener(new IPreferenceChangeListener<Integer>() {

				@Override
				public boolean beforeValueChange(final Integer newValue) {
					Set<String> codes = CRS.getSupportedCodes(newValue.toString());
					if ( codes.isEmpty() ) { return false; }
					return true;
				}

				@Override
				public void afterValueChange(final Integer newValue) {}
			});
	public static final Entry<Boolean> LIB_USE_DEFAULT =
		create("core.lib_use_default", "When no CRS is provided, save the GIS data with the current CRS", true,
			IType.BOOL).deactivates("core.lib_output_crs").in(LIBRARIES)
				.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)");
	public static final Entry<Integer> LIB_OUTPUT_CRS =
		create("core.lib_output_crs", "... or use this following CRS (EPSG code)", 4326, IType.INT).in(LIBRARIES)
			.group("GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)")
			.addChangeListener(new IPreferenceChangeListener<Integer>() {

				@Override
				public boolean beforeValueChange(final Integer newValue) {
					Set<String> codes = CRS.getSupportedCodes(newValue.toString());
					if ( codes.isEmpty() ) { return false; }
					return true;
				}

				@Override
				public void afterValueChange(final Integer newValue) {}
			});

	private static String getDefaultRPath() {
		String os = System.getProperty("os.name");
		String osbit = System.getProperty("os.arch");
		if ( os.startsWith("Mac") ) {
			if ( osbit.endsWith(
				"64") ) { return "/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/x86_64/RScript"; }
			return "/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/i386/RScript";
		} else if ( os.startsWith("Linux") ) { return "usr/bin/RScript"; }
		if ( os.startsWith("Windows") ) {
			if ( osbit.endsWith("64") ) { return "C:\\Program Files\\R\\R-2.15.1\\bin\\x64\\Rscript.exe"; }
			return "C:\\Program Files\\R\\R-2.15.1\\bin\\Rscript.exe";
		}
		return "";
	}

	private static void register(final Entry gp) {
		System.out.println("+++ Registering preference " + gp.key + " in store");
		IScope scope = null;
		String key = gp.key;
		if ( key == null ) { return; }
		prefs.put(key, gp);
		Object value = gp.value;
		switch (gp.type) {
			case IType.INT:
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, store.getInt(key, GamaIntegerType.staticCast(scope, value, null, false)));
				} else {
					store.putInt(key, GamaIntegerType.staticCast(scope, value, null, false));
				}
				break;
			case IType.FLOAT:
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, store.getDouble(key, GamaFloatType.staticCast(scope, value, null, false)));
				} else {
					store.putDouble(key, GamaFloatType.staticCast(scope, value, null, false));
				}
				break;
			case IType.BOOL:
				value = GamaBoolType.staticCast(scope, value, null, false);
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, store.getBoolean(key, (Boolean) value));
				} else {
					store.putBoolean(key, (Boolean) value);
				}
				break;
			case IType.STRING:
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, store.get(key, GamaStringType.staticCast(scope, value, false)));
				} else {
					store.put(key, GamaStringType.staticCast(scope, value, false));
				}
				break;
			case IType.FILE:
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, new GenericFile(store.get(key, "")));
				} else {
					store.put(key, value == null ? "" : ((IGamaFile) value).getPath());
				}
				break;
			case IType.COLOR:
				// Stores the preference as an int but create a color
				if ( storeKeys.contains(key) ) {
					int val = store.getInt(key, GamaIntegerType.staticCast(scope, value, null, false));
					gp.setValue(scope, GamaColor.getInt(val));
				} else {
					store.putInt(key, GamaIntegerType.staticCast(scope, value, null, false));
				}
				break;
			case IType.FONT:
				if ( storeKeys.contains(key) ) {
					String val = store.get(key, GamaStringType.staticCast(scope, value, false));
					gp.setValue(scope, GamaFontType.staticCast(scope, val, false));
				} else {
					store.put(key, GamaStringType.staticCast(scope, value, false));
				}
				break;
			default:
				if ( storeKeys.contains(key) ) {
					gp.setValue(scope, store.get(key, GamaStringType.staticCast(scope, value, false)));
				} else {
					store.put(key, GamaStringType.staticCast(scope, value, false));
				}
		}
		// if ( scope != null ) {
		// GAMA.releaseScope(scope);
		// }
		try {
			store.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static void writeToStore(final Entry gp) {
		String key = gp.key;
		Object value = gp.value;
		switch (gp.type) {
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
			case IType.FONT:
				store.put(key, value.toString());
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
