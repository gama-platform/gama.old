/*********************************************************************************************
 *
 * 'GamaPreferences.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.preferences;

import static msi.gama.common.util.StringUtils.toJavaString;
import static msi.gama.util.GamaDate.fromISOString;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.geotools.referencing.CRS;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.IPreferenceChangeListener.IPreferenceBeforeChangeListener;
import msi.gama.common.preferences.Pref.ValueProvider;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaDate;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GenericFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.kernel.GamaMetaModel;
import msi.gaml.descriptions.PlatformSpeciesDescription;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.GamaBoolType;
import msi.gaml.types.GamaFloatType;
import msi.gaml.types.GamaFontType;
import msi.gaml.types.GamaIntegerType;
import msi.gaml.types.GamaPointType;
import msi.gaml.types.GamaStringType;
import msi.gaml.types.IType;

/**
 * Class GamaPreferencesView.
 *
 * @author drogoul
 * @since 26 août 2013
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaPreferences {

	public static final List<String> GENERATOR_NAMES =
			Arrays.asList(IKeyword.CELLULAR, IKeyword.JAVA, IKeyword.MERSENNE);

	public static final ValueProvider<GamaColor>[] BASIC_COLORS = new ValueProvider[] {
			() -> new GamaColor(74, 97, 144), () -> new GamaColor(66, 119, 42), () -> new GamaColor(83, 95, 107),
			() -> new GamaColor(195, 98, 43), () -> new GamaColor(150, 132, 106) };

	/**
	 * 
	 * Interface tab
	 *
	 */
	public static class Interface {
		public static final String NAME = "Interface";
		/**
		 * Startup
		 */
		public static final String STARTUP = "Startup";
		public static final Pref<Boolean> CORE_SHOW_PAGE =
				create("pref_show_welcome_page", "Display welcome page", true, IType.BOOL).in(NAME, STARTUP);
		public static final Pref<Boolean> CORE_SHOW_MAXIMIZED =
				create("pref_show_maximized", "Maximize GAMA window", true, IType.BOOL).in(NAME, STARTUP);
		/**
		 * Menus
		 */
		public static final String MENUS = "Menus";
		public static final Pref<Integer> CORE_MENU_SIZE =
				create("pref_menu_size", "Break down agents in menus every", 50, IType.INT).between(10, 100).in(NAME,
						MENUS);
		/**
		 * Console
		 */
		public static final String CONSOLE = "Console";
		public static final Pref<Integer> CORE_CONSOLE_SIZE =
				create("pref_console_size", "Max. number of characters to display (-1 = unlimited)", 20000, IType.INT)
						.in(NAME, CONSOLE);
		public static final Pref<Integer> CORE_CONSOLE_BUFFER = create("pref_console_buffer",
				"Max. number of characters to keep when paused (-1 = unlimited)", 20000, IType.INT).in(NAME, CONSOLE);
		public static final Pref<Boolean> CORE_CONSOLE_WRAP =
				create("pref_console_wrap", "Wrap long lines (can slow down output)", false, IType.BOOL).in(NAME,
						CONSOLE);
		/**
		 * Appearance
		 */
		public static final String APPEARANCE = "Appearance";
		/**
		 * Simulation Interface
		 */
		public static final String SIMULATIONS = "Simulations";
		public static final Pref<Boolean> CORE_SIMULATION_NAME = create("pref_append_simulation_name",
				"Append the name of simulations to their outputs", false, IType.BOOL).in(NAME, SIMULATIONS);
		public static final Pref<GamaColor>[] SIMULATION_COLORS = new Pref[5];

		static {
			for (int i = 0; i < 5; i++) {
				SIMULATION_COLORS[i] = create("pref_simulation_color_" + i,
						"Color of Simulation " + i + " in the UI (console, view tabs) ", BASIC_COLORS[i], IType.COLOR)
								.in(NAME, SIMULATIONS);
			}
		}

	}

	/**
	 * 
	 * Modeling tab
	 *
	 */
	public static class Modeling {
		public static final String NAME = "Editors";
		public static final String TEXT = "Edition";
		/**
		 * Options
		 */
		public static final String OPTIONS = "Options";
		public static final Pref<Boolean> CORE_PERSPECTIVE = create("pref_switch_perspective",
				"Switch to modeling perspective when editing a model", false, IType.BOOL).in(NAME, OPTIONS);
		/**
		 * Validation
		 */
		// public static final String VALIDATION = "Validation of Models";
		public static final Pref<Boolean> WARNINGS_ENABLED =
				create("pref_editor_enable_warnings", "Show warning markers in the editor", true, IType.BOOL).in(NAME,
						OPTIONS);

		public static final Pref<Boolean> INFO_ENABLED =
				create("pref_editor_enable_infos", "Show information markers in the editor", true, IType.BOOL).in(NAME,
						OPTIONS);

		// .activates("pref_tests_period");
		// public static final Pref<String> TESTS_PERIOD = create("pref_tests_period", "Every", "Update", IType.STRING)
		// .among(Arrays.asList("Day", "Week", "Month", "Update")).in(NAME, TESTS);

	}

	public static class Experiments {
		public static final String NAME = "Experiments";

		// Unused for the moment
	}

	/**
	 * 
	 * Simulations tab
	 *
	 */
	public static class Simulations {
		public static final String NAME = "Simulations";

	}

	/**
	 * 
	 * Runtime tab
	 *
	 */
	public static class Runtime {
		public static final String NAME = "Execution";

		/**
		 * General
		 */
		/**
		 * Running experiments
		 */
		public static final String EXECUTION = "Experiments";
		public static final Pref<Boolean> CORE_AUTO_RUN =
				create("pref_experiment_auto_run", "Auto-run experiments when they are launched", false, IType.BOOL)
						.in(NAME, EXECUTION);
		public static final Pref<Boolean> CORE_ASK_CLOSING = create("pref_experiment_ask_closing",
				"Ask to close the previous experiment when launching a new one", true, IType.BOOL).in(NAME, EXECUTION);
		public static final Pref<Double> CORE_DELAY_STEP =
				create("pref_experiment_default_step", "Default step for the delay slider (in sec.)", 0.01, IType.FLOAT)
						.in(NAME, EXECUTION);
		public static final Pref<Boolean> CORE_SYNC =
				create("pref_display_synchronized", "Synchronize outputs with the simulation", false, IType.BOOL)
						.in(NAME, EXECUTION);
		/**
		 * Concurrency
		 */
		public static final String CONCURRENCY = "Parallelism";
		/**
		 * Tests
		 */
		public static final String TESTS = "Tests";
		public static final Pref<Boolean> TESTS_SORTED =
				create("pref_tests_sorted", "Sorts the results of tests by severity", false, IType.BOOL).in(NAME, TESTS)
						.withComment(", if true, aborted and failed tests are displayed first");
		public static final Pref<Boolean> RUN_TESTS =
				create("pref_run_tests", "Run tests after each update of the platform", false, IType.BOOL)
						.in(NAME, TESTS).disabled().hidden();
		public static final Pref<Boolean> START_TESTS =
				create("pref_start_tests", "Run tests at each start of the platform", false, IType.BOOL).in(NAME,
						TESTS);
		public static final Pref<Boolean> USER_TESTS =
				create("pref_user_tests", "Include user-defined tests in the tests suite", false, IType.BOOL)
						.in(NAME, TESTS).withComment(", if true, will run user models with 'test' experiments");
		public static final Pref<Boolean> FAILED_TESTS = create("pref_failed_tests",
				"Only display (in the UI and in headless runs) failed and aborted tests", false, IType.BOOL)
						.in(NAME, TESTS).withComment(", if true, only aborted and failed tests are displayed");

		/**
		 * Errors & warnings
		 */
		public static final String ERRORS = "Runtime errors";
		public static final Pref<Boolean> CORE_SHOW_ERRORS =
				create("pref_errors_display", "Show execution errors", true, IType.BOOL).in(NAME, ERRORS)
						.activates("pref_errors_number", "pref_errors_recent_first", "pref_display_show_errors");
		public static final Pref<Boolean> ERRORS_IN_DISPLAYS =
				create("pref_display_show_errors", "Show errors thrown in displays and outputs", false, IType.BOOL)
						.in(NAME, ERRORS);
		public static final Pref<Integer> CORE_ERRORS_NUMBER =
				create("pref_errors_number", "Number of errors to display", 10, IType.INT).in(NAME, ERRORS).between(1,
						null);
		public static final Pref<Boolean> CORE_RECENT =
				create("pref_errors_recent_first", "Display most recent first", true, IType.BOOL).in(NAME, ERRORS);
		public static final Pref<Boolean> CORE_REVEAL_AND_STOP =
				create("pref_errors_stop", "Stop simulation at first error", true, IType.BOOL).in(NAME, ERRORS);
		public static final Pref<Boolean> CORE_WARNINGS =
				create("pref_errors_warnings_errors", "Treat warnings as errors", false, IType.BOOL).in(NAME, ERRORS);

	}

	public static class Displays {
		public static final String NAME = "Displays";
		public static final String PRESENTATION = "Presentation and Behavior of Graphical Display Views";
		/**
		 * Presentation
		 */
		public static final List<String> LAYOUTS = Arrays.asList("None", "Stacked", "Split", "Horizontal", "Vertical");
		public static final Pref<String> CORE_DISPLAY_LAYOUT =
				create("pref_display_view_layout", "Default layout of display views", "None", IType.STRING)
						.among(LAYOUTS.toArray(new String[0])).in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_ORDER =
				create("pref_display_same_order", "Stack displays in the order defined in the model", true, IType.BOOL)
						.in(NAME, PRESENTATION);
		// public static final Pref<Integer> CORE_OUTPUT_DELAY = create("pref_display_delay_views",
		// "Delay in ms between the opening of views (increase to avoid freezes of Java2D displays)", 200,
		// IType.INT).between(0, 1000).in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_BORDER =
				create("pref_display_show_border", "Display a border around display views", false, IType.BOOL).in(NAME,
						PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_PERSPECTIVE = create("pref_display_continue_drawing",
				"Continue to draw displays when in Modeling perspective", false, IType.BOOL).in(NAME, PRESENTATION);
		// public static final Pref<Boolean> DISPLAY_NATIVE_FULLSCREEN = create("pref_display_fullscreen_native",
		// "Use the native mode for full-screen (experimental)", false, IType.BOOL).in(NAME, PRESENTATION);
		// public static final Pref<Boolean> DISPLAY_MODAL_FULLSCREEN = create("pref_display_fullscreen_menu",
		// "Disable the OS menu bar when a display is full-screen", true, IType.BOOL).in(NAME, PRESENTATION);
		// public static final Pref<Boolean> DISPLAY_TOOLBAR_FULLSCREEN = create("pref_display_fullscreen_toolbar",
		// "Show the toolbar when a display is full-screen", false, IType.BOOL).in(NAME, PRESENTATION);
		public static final Pref<Boolean> DISPLAY_FAST_SNAPSHOT = create("pref_display_fast_snapshot",
				"Enable fast snapshots (uncomplete when the display is obscured by others but much faster)", false,
				IType.BOOL).in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_TOOLBAR =
				create("pref_display_show_toolbar", "Show the display top toolbar", true, IType.BOOL).in(NAME,
						PRESENTATION);
		public static final Pref<Boolean> CORE_OVERLAY =
				create("pref_display_show_overlay", "Show the display bottom overlay", false, IType.BOOL)
						.in(NAME, PRESENTATION).activates("pref_display_show_scale", "pref_display_show_fps");
		public static final Pref<Boolean> CORE_SCALE =
				create("pref_display_show_scale", "Show scale bar", false, IType.BOOL).in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_SHOW_FPS =
				create("pref_display_show_fps", "Show number of frames per second", false, IType.BOOL).in(NAME,
						PRESENTATION);

		/**
		 * Charts
		 */
		public static final String CHARTS = "Charts Preferences";
		public static final Pref<Boolean> CHART_FLAT =
				create("pref_display_flat_charts", "Display 'flat' histograms", false, IType.BOOL).in(NAME, CHARTS);
		public static final Pref<Boolean> CHART_MEMORIZE =
				create("pref_display_memorize_charts", "Keep values in memory (to save them as CSV)", true, IType.BOOL)
						.in(NAME, CHARTS);
		public static final Pref<Boolean> CHART_GRIDLINES =
				create("pref_chart_display_gridlines", "Display grid lines", true, IType.BOOL).in(NAME, CHARTS);
		/**
		 * Drawing methods and defaults
		 */
		public static final String DRAWING = "Default Rendering Properties";
		public static final Pref<String> CORE_DISPLAY =
				create("pref_display_default", "Default rendering method (Java2D for 2D, OpenGL for 3D)", "Java2D",
						IType.STRING).among("Java2D", "OpenGL").in(NAME, DRAWING);
		public static final Pref<Boolean> CORE_ANTIALIAS =
				create("pref_display_antialias", "Apply antialiasing", false, IType.BOOL).in(NAME, DRAWING);
		public static final Pref<GamaColor> CORE_BACKGROUND =
				create("pref_display_background_color", "Default background color ('background' facet of 'display')",
						() -> GamaColor.getNamed("white"), IType.COLOR).in(NAME, DRAWING);
		public static final Pref<GamaColor> CORE_HIGHLIGHT = create("pref_display_highlight_color",
				"Default highlight color", () -> new GamaColor(0, 200, 200), IType.COLOR).in(NAME, DRAWING);
		public static final Pref<String> CORE_SHAPE =
				create("pref_display_default_shape", "Defaut shape of agents", "shape", IType.STRING)
						.among("shape", "circle", "square", "triangle", "point", "cube", "sphere").in(NAME, DRAWING);
		public static final Pref<Double> CORE_SIZE =
				create("pref_display_default_size", "Default size of agents", 1.0, IType.FLOAT).between(0.01, null)
						.in(NAME, DRAWING);
		public static final Pref<GamaColor> CORE_COLOR = create("pref_display_default_color", "Default color of agents",
				() -> GamaColor.getNamed("yellow"), IType.COLOR).in(NAME, DRAWING);
		/**
		 * Options
		 */
		public static final String OPTIONS = "Advanced ";
		public static final Pref<Boolean> DISPLAY_ONLY_VISIBLE = create("pref_display_visible_agents",
				"Only display visible agents (faster, may create visual oddities)", false, IType.BOOL).in(NAME,
						OPTIONS);
		public static final Pref<Boolean> DISPLAY_NO_ACCELERATION = create("pref_display_no_java2d_acceleration",
				"Disable acceleration for Java2D (necessary on some configurations)", false, IType.BOOL).in(NAME,
						OPTIONS);
		/**
		 * OPENGL
		 */
		public static final String RENDERING = "OpenGL Rendering Properties";
		public static final Pref<Boolean> CORE_DRAW_ENV =
				create("pref_display_show_referential", "Draw 3D axes", true, IType.BOOL).in(NAME, RENDERING);
		public static final Pref<Boolean> DRAW_ROTATE_HELPER =
				create("pref_display_show_rotation", "Draw rotation axes", true, IType.BOOL).in(NAME, RENDERING);
		public static final Pref<Double> CORE_LINE_WIDTH =
				create("pref_display_line_width", "Default line width (facet 'width' of 'draw')", 1.2d, IType.FLOAT)
						.in(NAME, RENDERING);
		public static final Pref<Boolean> ONLY_VISIBLE_FACES =
				create("pref_display_visible_faces", "Draw only the 'external' faces of objects", false, IType.BOOL)
						.in(NAME, RENDERING).hidden();
		public static final Pref<Integer> DISPLAY_SLICE_NUMBER =
				create("pref_display_slice_number", "Number of slices of circular geometries", 16, IType.INT).in(NAME,
						RENDERING);
		/**
		 * Options
		 */
		// public static final String OPTIONS = "OpenGL ";
		public static final Pref<Double> OPENGL_ZOOM =
				create("pref_display_zoom_factor", "Set the zoom factor (0 for slow, 1 fast)", 0.5, IType.FLOAT)
						.in(NAME, RENDERING).between(0, 1);
		public static final Pref<Integer> OPENGL_FPS =
				create("pref_display_max_fps", "Max. number of frames per second", 20, IType.INT).in(NAME, RENDERING);
		// public static final Pref<Boolean> DISPLAY_SHARED_CONTEXT = create("pref_display_shared_cache",
		// "Enable OpenGL background loading of textures (faster, but can cause issues on Linux and Windows)",
		// false, IType.BOOL).in(NAME, OPTIONS);
		public static final Pref<Boolean> DISPLAY_POWER_OF_TWO = create("pref_display_power_of_2",
				"Forces textures dimensions to a power of 2 (e.g. 16x16. Necessary on some configurations)", false,
				IType.BOOL).in(NAME, RENDERING);
		public static final Pref<Boolean> OPENGL_TRIANGULATOR = create("pref_display_triangulator",
				"Use OpenGL tesselator (false is more precise, but more CPU intensive)", true, IType.BOOL).in(NAME,
						RENDERING);
	}

	public static class OpenGL {
		public static final String NAME = "OpenGL";
		/**
		 * Rendering
		 */

	}

	public static class External {
		public static final String NAME = "Data and Operators";
		/**
		 * Random numbers
		 */
		public static final String RNG = "Random number generation";
		public static final Pref<String> CORE_RNG =
				create("pref_rng_name", "Default random number generator", IKeyword.MERSENNE, IType.STRING)
						.among(GENERATOR_NAMES).in(NAME, RNG);
		public static final Pref<Boolean> CORE_SEED_DEFINED =
				create("pref_rng_define_seed", "Define a default seed", false, IType.BOOL)
						.activates("pref_rng_default_seed").in(NAME, RNG);
		public static final Pref<Double> CORE_SEED =
				create("pref_rng_default_seed", "Default seed value (0 is undefined)", 1d, IType.FLOAT).in(NAME, RNG);
		public static final Pref<Boolean> CORE_RND_EDITABLE =
				create("pref_rng_in_parameters", "Include in the parameters", false, IType.BOOL).in(NAME, RNG);
		/**
		 * Dates
		 */
		public static final String DATES = "Management of dates";
		/**
		 * Optimizations
		 */
		public static final String OPTIMIZATIONS = "Operators options";
		public static final Pref<Boolean> CONSTANT_OPTIMIZATION = create("pref_optimize_constant_expressions",
				"Optimize constant expressions (experimental)", false, IType.BOOL).in(NAME, OPTIMIZATIONS);
		public static final Pref<Boolean> AGENT_OPTIMIZATION =
				create("pref_optimize_agent_memory", "Optimize agents memory", true, IType.BOOL).in(NAME,
						OPTIMIZATIONS);
		public static final Pref<Boolean> MATH_OPTIMIZATION = create("pref_optimize_math_functions",
				"Use faster (but less accurate) arithmetic functions", false, IType.BOOL).in(NAME, OPTIMIZATIONS);
		public static final Pref<Boolean> AT_DISTANCE_OPTIMIZATION =
				create("pref_optimize_at_distance", "Optimize the 'at_distance' operator", true, IType.BOOL).in(NAME,
						OPTIMIZATIONS);
		public static final Pref<Boolean> PATH_COMPUTATION_OPTIMIZATION = create("pref_optimize_path_computation",
				"Optimize the path computation operators and goto action (but with possible 'jump' issues)", false,
				IType.BOOL).in(NAME, OPTIMIZATIONS);
		public static final Pref<Boolean> QUADTREE_OPTIMIZATION = create("pref_optimize_quadtree",
				"Optimize spatial queries: add agents only when necessary in the quadtree (still experimental)", false,
				IType.BOOL).in(NAME, OPTIMIZATIONS);

		public static final Pref<Double> TOLERANCE_POINTS =
				create("pref_point_tolerance", "Tolerance for the comparison of points", 0.0, IType.FLOAT).in(NAME,
						OPTIMIZATIONS);

		/**
		 * Paths to libraries
		 */
		public static final String PATHS = "External libraries support";
		public static final Pref<? extends IGamaFile> LIB_SPATIALITE =
				create("pref_lib_spatialite", "Path to Spatialite library (http://www.gaia-gis.it/gaia-sins/)",
						() -> new GenericFile("Enter path", false), IType.FILE).in(NAME, PATHS);
		public static final String jriFile = System.getProperty("os.name").startsWith("Mac") ? "libjri.jnilib"
				: System.getProperty("os.name").startsWith("Linux") ? "libjri.so" : "jri.dll";

		public static final Pref<? extends IGamaFile> LIB_R = create("pref_lib_r",
				"Path to JRI library ($R_HOME/library/rJava/jri/" + jriFile + ") (http://www.r-project.org)",
				() -> new GenericFile(getDefaultRPath(), false), IType.FILE).in(NAME, PATHS);
		/**
		 * GeoTools
		 */
		public static final String GEOTOOLS =
				"GIS Coordinate Reference Systems (http://spatialreference.org/ref/epsg/ for EPSG codes)";
		public static final Pref<Boolean> LIB_TARGETED =
				create("pref_gis_auto_crs", "Let GAMA decide which CRS to use to project GIS data", true, IType.BOOL)
						.deactivates("pref_gis_default_crs").in(NAME, GEOTOOLS);
		public static final Pref<Boolean> LIB_PROJECTED = create("pref_gis_same_crs",
				"When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS", true,
				IType.BOOL).deactivates("pref_gis_initial_crs").in(NAME, GEOTOOLS);
		public static final Pref<Boolean> LIB_USE_DEFAULT =
				create("pref_gis_save_crs", "When no CRS is provided, save the GIS data with the current CRS", true,
						IType.BOOL).deactivates("pref_gis_output_crs").in(NAME, GEOTOOLS);
		public static final Pref<Integer> LIB_TARGET_CRS =
				create("pref_gis_default_crs", "...or use the following CRS (EPSG code)", 32648, IType.INT)
						.in(NAME, GEOTOOLS).addChangeListener((IPreferenceBeforeChangeListener<Integer>) newValue -> {
							final Set<String> codes = CRS.getSupportedCodes(newValue.toString());
							if (codes.isEmpty()) { return false; }
							return true;
						});

		public static final Pref<Integer> LIB_INITIAL_CRS =
				create("pref_gis_initial_crs", "...or use the following CRS (EPSG code)", 4326, IType.INT)
						.in(NAME, GEOTOOLS).addChangeListener((IPreferenceBeforeChangeListener<Integer>) newValue -> {
							final Set<String> codes = CRS.getSupportedCodes(newValue.toString());
							if (codes.isEmpty()) { return false; }
							return true;
						});

		public static final Pref<Integer> LIB_OUTPUT_CRS =
				create("pref_gis_output_crs", "... or use this following CRS (EPSG code)", 4326, IType.INT)
						.in(NAME, GEOTOOLS).addChangeListener((IPreferenceBeforeChangeListener<Integer>) newValue -> {
							final Set<String> codes = CRS.getSupportedCodes(newValue.toString());
							if (codes.isEmpty()) { return false; }
							return true;
						});

		// RScript adress:
		// "/Library/Frameworks/R.framework/Versions/3.4/Resources/bin/exec/x86_64/RScript"
		// "usr/bin/RScript"
		// "C:\\Program Files\\R\\R-2.15.1\\bin\\x64\\Rscript.exe"
		// "C:\\Program Files\\R\\R-2.15.1\\bin\\Rscript.exe"
		private static String getDefaultRPath() {
			final String os = System.getProperty("os.name");
			final String osbit = System.getProperty("os.arch");
			if (os.startsWith("Mac")) {
				return "/Library/Frameworks/R.framework/Resources/library/rJava/jri/libjri.jnilib";
			} else if (os.startsWith("Linux")) { return "/usr/local/lib/libjri.so"; }
			if (os.startsWith("Windows")) {
				if (osbit.endsWith("64")) { return "C:\\Program Files\\R\\R-3.4.0\\library\\rJava\\jri\\jri.dll"; }
				return "C:\\Program Files\\R\\R-3.4.0\\library\\rJava\\jri\\jri.dll";
			}
			return "";
		}
	}

	// TAB NAMES

	static Preferences store;
	private static Map<String, Pref<? extends Object>> prefs = new LinkedHashMap<>();
	private static List<String> storeKeys;

	static {

		try {
			store = Preferences.userRoot().node("gama");
			try {
				store.flush();
			} catch (final BackingStoreException e1) {
				e1.printStackTrace();
			}
			try {
				storeKeys = Arrays.asList(store.keys());
			} catch (final BackingStoreException e) {
				e.printStackTrace();
			}
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	public static <T> Pref<T> get(final String key, final Class<T> clazz) {
		return (Pref<T>) prefs.get(key);
	}

	public static Pref<?> get(final String key) {
		return prefs.get(key);
	}

	public static Map<String, Pref<?>> getAll() {
		return prefs;
	}

	public static <T> Pref<T> create(final String key, final String title, final T value, final int type) {
		if (key.contains(".") || key.contains(" ")) {
			System.out.println("WARNING. Preference " + key + " cannot be used as a variable");
		}
		final Pref<T> e = new Pref<T>(key, type).named(title).in(Interface.NAME, "").init(value);
		register(e);
		return e;
	}

	/**
	 * Lazy create (tries not to compute immediately the value)
	 * 
	 * @param key
	 * @param title
	 * @param value
	 * @param type
	 * @return
	 */
	public static <T> Pref<T> create(final String key, final String title, final ValueProvider<T> provider,
			final int type) {
		if (key.contains(".") || key.contains(" ")) {
			System.out.println("WARNING. Preference " + key + " cannot be used as a variable");
		}
		final Pref<T> e = new Pref<T>(key, type).named(title).in(Interface.NAME, "").init(provider);
		register(e);
		return e;
	}

	private static void register(final Pref gp) {
		// System.out.println("+++ Registering preference " + gp.key + " in
		// store");
		final IScope scope = null;
		final String key = gp.key;
		if (key == null) { return; }
		prefs.put(key, gp);
		Object value = gp.value;
		switch (gp.type) {
			case IType.POINT:
				if (storeKeys.contains(key)) {
					final String val = store.get(key, GamaStringType.staticCast(scope, value, false));
					gp.setValue(scope, GamaPointType.staticCast(scope, val, false));
				}
				// else {
				// store.put(key, GamaStringType.staticCast(scope, value, false));
				// }
				break;
			case IType.INT:
				if (storeKeys.contains(key)) {
					gp.setValue(scope, store.getInt(key, GamaIntegerType.staticCast(scope, value, null, false)));
				}
				// else {
				// store.putInt(key, GamaIntegerType.staticCast(scope, value, null, false));
				// }
				break;
			case IType.FLOAT:
				if (storeKeys.contains(key)) {
					gp.setValue(scope, store.getDouble(key, GamaFloatType.staticCast(scope, value, null, false)));
				}
				// else {
				// store.putDouble(key, GamaFloatType.staticCast(scope, value, null, false));
				// }
				break;
			case IType.BOOL:
				value = GamaBoolType.staticCast(scope, value, null, false);
				if (storeKeys.contains(key)) {
					gp.setValue(scope, store.getBoolean(key, (Boolean) value));
				}
				// else {
				// store.putBoolean(key, (Boolean) value);
				// }
				break;
			case IType.STRING:
				if (storeKeys.contains(key)) {
					gp.setValue(scope,
							store.get(key, StringUtils.toJavaString(GamaStringType.staticCast(scope, value, false))));
				}
				// else {
				// store.put(key, StringUtils.toJavaString(GamaStringType.staticCast(scope, value, false)));
				// }
				break;
			case IType.FILE:
				if (storeKeys.contains(key)) {
					gp.setValue(scope, new GenericFile(store.get(key, ""), false));
				}
				// else {
				// store.put(key, value == null ? "" : ((IGamaFile) value).getPath(scope));
				// }
				break;
			case IType.COLOR:
				// Stores the preference as an int but create a color
				if (storeKeys.contains(key)) {
					final int val = store.getInt(key, GamaIntegerType.staticCast(scope, value, null, false));
					gp.setValue(scope, GamaColor.getInt(val));
				}
				// else {
				// store.putInt(key, GamaIntegerType.staticCast(scope, value, null, false));
				// }
				break;
			case IType.FONT:
				if (storeKeys.contains(key)) {
					final String val = store.get(key, GamaStringType.staticCast(scope, value, false));
					gp.setValue(scope, GamaFontType.staticCast(scope, val, false));
				}
				// else {
				// store.put(key, GamaStringType.staticCast(scope, value, false));
				// }
				break;
			case IType.DATE:
				if (storeKeys.contains(key)) {
					final String val = toJavaString(store.get(key, GamaStringType.staticCast(scope, value, false)));
					gp.setValue(scope, fromISOString(val));
				}
				// else {
				// store.put(key, GamaStringType.staticCast(scope, value, false));
				// }
				break;
			default:
				if (storeKeys.contains(key)) {
					gp.setValue(scope, store.get(key, GamaStringType.staticCast(scope, value, false)));
				}
				// else {
				// store.put(key, GamaStringType.staticCast(scope, value, false));
				// }
		}
		try {
			store.flush();
		} catch (final BackingStoreException ex) {
			ex.printStackTrace();
		}
		// Adds the preferences to the platform species if it is already created
		final PlatformSpeciesDescription spec = GamaMetaModel.INSTANCE.getPlatformSpeciesDescription();
		if (spec != null) {
			if (!spec.hasAttribute(key)) {
				spec.addPref(key, gp);
				spec.validate();
			}
		}
		// Registers the preferences in the variable of the scope provider

	}

	public static void writeToStore(final Pref gp) {
		final String key = gp.key;
		final Object value = gp.value;
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
				store.put(key, StringUtils.toJavaString((String) value));
				break;
			case IType.FILE:
				store.put(key, ((GamaFile) value).getPath(null));
				break;
			case IType.COLOR:
				// Stores the preference as an int but create a color
				final int code = ((GamaColor) value).getRGB();
				store.putInt(key, code);
				break;
			case IType.POINT:
				store.put(key, ((GamaPoint) value).stringValue(null));
				break;
			case IType.FONT:
				store.put(key, value.toString());
				break;
			case IType.DATE:
				final GamaDate d = (GamaDate) value;
				store.put(key, StringUtils.toJavaString(d.toISOString()));
				break;
			default:
				store.put(key, (String) value);
		}
	}

	public static Map<String, Map<String, List<Pref>>> organizePrefs() {
		final Map<String, Map<String, List<Pref>>> result = new TOrderedHashMap();
		for (final Pref e : prefs.values()) {
			if (e.isHidden()) {
				continue;
			}
			final String tab = e.tab;
			Map<String, List<Pref>> groups = result.get(tab);
			if (groups == null) {
				groups = new TOrderedHashMap();
				result.put(tab, groups);
			}
			final String group = e.group;
			List<Pref> in_group = groups.get(group);
			if (in_group == null) {
				in_group = new ArrayList<>();
				groups.put(group, in_group);
			}
			in_group.add(e);
		}
		return result;
	}

	public static void setNewPreferences(final Map<String, Object> modelValues) {
		for (final String name : modelValues.keySet()) {
			final Pref e = prefs.get(name);
			if (e == null) {
				continue;
			}
			e.set(modelValues.get(name));
			writeToStore(e);
			try {
				store.flush();
			} catch (final BackingStoreException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	public static void revertToDefaultValues(final Map<String, Object> modelValues) {
		for (final String name : modelValues.keySet()) {
			final Pref e = prefs.get(name);
			if (e == null) {
				continue;
			}
			modelValues.put(name, e.getInitialValue(null));
			e.set(e.initial);
		}

	}

	private static void reloadPreferences(final Map<String, Object> modelValues) {
		final List<Pref> entries = new ArrayList(prefs.values());
		for (final Pref e : entries) {
			register(e);
			modelValues.put(e.key, e.getValue());
		}
	}

	public static void applyPreferencesFrom(final String path, final Map<String, Object> modelValues) {
		// System.out.println("Apply preferences from " + path);
		try (final FileInputStream is = new FileInputStream(path);) {
			store.importPreferences(is);
			reloadPreferences(modelValues);
		} catch (final IOException | InvalidPreferencesFormatException e) {
			e.printStackTrace();
		}
	}

	public static void savePreferencesTo(final String path) {
		try (FileWriter os = new FileWriter(path)) {
			final List<Pref> entries = new ArrayList(prefs.values());
			final StringBuilder read = new StringBuilder(1000);
			final StringBuilder write = new StringBuilder(1000);
			for (final Pref e : entries) {
				read.append(Strings.TAB).append("//").append(e.getTitle()).append(Strings.LN);
				read.append(Strings.TAB).append("write sample(gama.").append(e.getName()).append(");")
						.append(Strings.LN).append(Strings.LN);
				write.append(Strings.TAB).append("//").append(e.getTitle()).append(Strings.LN);
				write.append(Strings.TAB).append("gama.").append(e.getName()).append(" <- ")
						.append(Cast.toGaml(e.getValue())).append(";").append(Strings.LN).append(Strings.LN);
			}
			os.append("// ").append(GAMA.VERSION).append(" Preferences saved on ")
					.append(LocalDateTime.now().toString()).append(Strings.LN).append(Strings.LN);
			os.append("model preferences").append(Strings.LN).append(Strings.LN);
			os.append("experiment 'Display Preferences' type: gui {").append(Strings.LN);
			os.append("init {").append(Strings.LN);
			os.append(read);
			os.append("}").append(Strings.LN);
			os.append("}").append(Strings.LN).append(Strings.LN).append(Strings.LN);
			os.append("experiment 'Set Preferences' type: gui {").append(Strings.LN);
			os.append("init {").append(Strings.LN);
			os.append(write);
			os.append("}").append(Strings.LN);
			os.append("}").append(Strings.LN);
			os.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	// To force preferences to load

	static Interface i_ = new Interface();
	static Modeling m_ = new Modeling();
	static Runtime r_ = new Runtime();
	static Experiments e_ = new Experiments();
	static Simulations s_ = new Simulations();
	static Displays d_ = new Displays();
	// static OpenGL o = new OpenGL();
	static External ext_ = new External();

}
