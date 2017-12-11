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
import msi.gama.common.util.StringUtils;
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

	public static final GamaColor[] BASIC_COLORS =
			new GamaColor[] { new GamaColor(74, 97, 144), new GamaColor(66, 119, 42), new GamaColor(83, 95, 107),
					new GamaColor(195, 98, 43), new GamaColor(150, 132, 106) };

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
		public static final String STARTUP = "Startup options";
		public static final Pref<Boolean> CORE_SHOW_PAGE =
				create("pref_show_welcome_page", "Display Welcome page at startup", true, IType.BOOL).in(NAME, STARTUP);
		public static final Pref<Boolean> CORE_SHOW_MAXIMIZED =
				create("pref_show_maximized", "Maximize GAMA windows at startup", true, IType.BOOL).in(NAME, STARTUP);
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
				create("pref_console_size", "Max. number of characters to display in the console (-1 means no limit) ",
						20000, IType.INT).in(NAME, CONSOLE);
		public static final Pref<Integer> CORE_CONSOLE_BUFFER = create("pref_console_buffer",
				"Max. number of characters to keep in memory when console is paused (-1 means no limit)", 20000,
				IType.INT).in(NAME, CONSOLE);
		public static final Pref<Boolean> CORE_CONSOLE_WRAP =
				create("pref_console_wrap", "Automatically wrap long lines in the console (can slow down output)",
						false, IType.BOOL).in(NAME, CONSOLE);
		/**
		 * Appearance
		 */
		public static final String APPEARANCE = "Appearance";

	}

	/**
	 * 
	 * Modeling tab
	 *
	 */
	public static class Modeling {
		public static final String NAME = "Modeling";
		public static final String TEXT = "Text Options";
		/**
		 * Options
		 */
		public static final String OPTIONS = "Editor Options";
		public static final Pref<Boolean> CORE_PERSPECTIVE =
				create("pref_switch_perspective", "Automatically switch to modeling perspective when editing a model",
						false, IType.BOOL).in(NAME, OPTIONS);
		/**
		 * Validation
		 */
		public static final String VALIDATION = "Validation of Models";
		public static final Pref<Boolean> WARNINGS_ENABLED =
				create("pref_editor_enable_warnings", "Show warning markers when editing a model", true, IType.BOOL)
						.in(NAME, VALIDATION);

		public static final Pref<Boolean> INFO_ENABLED =
				create("pref_editor_enable_infos", "Show information markers when editing a model", true, IType.BOOL)
						.in(NAME, VALIDATION);

		public static final String TESTS = "Tests";
		public static final Pref<Boolean> TESTS_SORTED =
				create("pref_tests_sorted", "Sorts the results of tests by severity", false, IType.BOOL).in(NAME, TESTS)
						.withComment(", if true, the display of tests will put first the aborted and failed ones");
		public static final Pref<Boolean> RUN_TESTS =
				create("pref_run_tests", "Run tests after each update of the platform", false, IType.BOOL)
						.in(NAME, TESTS).disabled();
		public static final Pref<Boolean> START_TESTS =
				create("pref_start_tests", "Run tests at each start of the platform", false, IType.BOOL).in(NAME,
						TESTS);
		public static final Pref<Boolean> USER_TESTS = create("pref_user_tests",
				"Include user-defined tests in the tests suite", false, IType.BOOL).in(NAME, TESTS).withComment(
						", if true, will look into user projects named 'tests' for models with 'test' experiments and run them automatically");
		public static final Pref<Boolean> FAILED_TESTS =
				create("pref_failed_tests", "Only display (in the UI and in headless runs) failed and aborted tests",
						false, IType.BOOL).in(NAME, TESTS).withComment(
								", if true, only the results of tests that fail or exit abnormally are displayed");
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
		/**
		 * Interface
		 */
		public static final String INTERFACE = "Interface";
		public static final Pref<Boolean> CORE_SIMULATION_NAME = create("pref_append_simulation_name",
				"Append the name of the simulation to the title of its outputs", false, IType.BOOL).in(NAME, INTERFACE);
		public static final Pref<GamaColor>[] SIMULATION_COLORS = new Pref[5];

		static {
			for (int i = 0; i < 5; i++) {
				SIMULATION_COLORS[i] = create("pref_simulation_color_" + i,
						"Color of Simulation " + i + " in the interface (console text, view tabs) ", BASIC_COLORS[i],
						IType.COLOR).in(NAME, INTERFACE);
			}
		}
		/**
		 * Random numbers
		 */
		public static final String RNG = "Random number generation";
		public static final Pref<String> CORE_RNG =
				create("pref_rng_name", "Random number generator to use by default", IKeyword.MERSENNE, IType.STRING)
						.among(GENERATOR_NAMES).in(NAME, RNG);
		public static final Pref<Boolean> CORE_SEED_DEFINED =
				create("pref_rng_define_seed", "Define a default seed", false, IType.BOOL)
						.activates("pref_rng_default_seed").in(NAME, RNG);
		public static final Pref<Double> CORE_SEED =
				create("pref_rng_default_seed", "Default seed value (0 for undefined)", 1d, IType.FLOAT).in(NAME, RNG);
		public static final Pref<Boolean> CORE_RND_EDITABLE = create("pref_rng_in_parameters",
				"Include the choice of generator in the parameters view of experiments", false, IType.BOOL).in(NAME,
						RNG);
		/**
		 * Dates
		 */
		public static final String DATES = "Management of dates";
	}

	/**
	 * 
	 * Runtime tab
	 *
	 */
	public static class Runtime {
		public static final String NAME = "Runtime";
		/**
		 * General
		 */
		/**
		 * Running experiments
		 */
		public static final String EXECUTION = "Execution of Experiments";
		public static final Pref<Boolean> CORE_AUTO_RUN =
				create("pref_experiment_auto_run", "Auto-run experiments when they are launched", false, IType.BOOL)
						.in(NAME, EXECUTION);
		public static final Pref<Boolean> CORE_ASK_CLOSING = create("pref_experiment_ask_closing",
				"Ask to close previous experiment before launching a new one", true, IType.BOOL).in(NAME, EXECUTION);
		public static final Pref<Double> CORE_DELAY_STEP =
				create("pref_experiment_default_step", "Default step for the delay slider (in sec.)", 0.01, IType.FLOAT)
						.in(NAME, EXECUTION);
		public static final Pref<Boolean> CORE_SYNC = create("pref_display_synchronized",
				"Synchronize outputs with the simulations steps (slows down experiments)", false, IType.BOOL).in(NAME,
						EXECUTION);
		/**
		 * Concurrency
		 */
		public static final String CONCURRENCY = "Concurrency";
		/**
		 * Errors & warnings
		 */
		public static final String ERRORS = "Warnings and errors";
		public static final Pref<Boolean> CORE_SHOW_ERRORS =
				create("pref_errors_display", "Show execution errors", true, IType.BOOL).in(NAME, ERRORS)
						.activates("pref_errors_number", "pref_errors_recent_first", "pref_display_show_errors");
		public static final Pref<Boolean> ERRORS_IN_DISPLAYS =
				create("pref_display_show_errors", "Show errors happening in displays and outputs", false, IType.BOOL)
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
		/**
		 * Optimizations
		 */
		public static final String OPTIMIZATIONS = "Various Optimizations";
		public static final Pref<Boolean> CONSTANT_OPTIMIZATION = create("pref_optimize_constant_expressions",
				"Automatically optimize constant expressions (still experimental)", false, IType.BOOL).in(NAME,
						OPTIMIZATIONS);
		public static final Pref<Boolean> AGENT_OPTIMIZATION = create("pref_optimize_agent_memory",
				"Automatically optimize the memory used by agents", true, IType.BOOL).in(NAME, OPTIMIZATIONS);
		public static final Pref<Boolean> MATH_OPTIMIZATION = create("pref_optimize_math_functions",
				"Use optimized (but less accurate) arithmetic and trigonometric functions", false, IType.BOOL).in(NAME,
						OPTIMIZATIONS);
		public static final Pref<Boolean> AT_DISTANCE_OPTIMIZATION =
				create("pref_optimize_at_distance", "Automatically optimize the at_distance operator", true, IType.BOOL)
						.in(NAME, OPTIMIZATIONS);
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
		public static final Pref<Boolean> CORE_DISPLAY_ORDER = create("pref_display_same_order",
				"Stack displays on screen in the order defined by the model", true, IType.BOOL).in(NAME, PRESENTATION);
		public static final Pref<Integer> CORE_OUTPUT_DELAY = create("pref_display_delay_views",
				"Delay (in ms) between the opening of display views (increase if you experience freezes when opening displays, esp. Java2D displays)",
				200, IType.INT).between(0, 1000).in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_BORDER =
				create("pref_display_show_border", "Display a border around display views", false, IType.BOOL).in(NAME,
						PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_PERSPECTIVE = create("pref_display_continue_drawing",
				"Continue to draw displays (in the background) when in Modeling perspective", false, IType.BOOL)
						.in(NAME, PRESENTATION);
		public static final Pref<Boolean> DISPLAY_NATIVE_FULLSCREEN = create("pref_display_fullscreen_native",
				"Use the OS native mode for full-screen (experimental)", false, IType.BOOL).in(NAME, PRESENTATION);
		public static final Pref<Boolean> DISPLAY_MODAL_FULLSCREEN =
				create("pref_display_fullscreen_menu", "Disable the OS menu bar when a display is turned full-screen",
						true, IType.BOOL).in(NAME, PRESENTATION);
		public static final Pref<Boolean> DISPLAY_TOOLBAR_FULLSCREEN = create("pref_display_fullscreen_toolbar",
				"Show the toolbar when a display is turned full-screen", false, IType.BOOL).in(NAME, PRESENTATION);
		public static final Pref<Boolean> DISPLAY_FAST_SNAPSHOT = create("pref_display_fast_snapshot",
				"Enable fast snapshots of displays (uncomplete when the display is obscured by other views but much faster than normal snapshots)",
				false, IType.BOOL).in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_TOOLBAR = create("pref_display_show_toolbar",
				"Whether the display top toolbar is initially visible or not", true, IType.BOOL).in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_OVERLAY = create("pref_display_show_overlay",
				"Whether the display bottom overlay is initially visible or not", false, IType.BOOL)
						.in(NAME, PRESENTATION).activates("pref_display_show_scale", "pref_display_show_fps");
		public static final Pref<Boolean> CORE_SCALE =
				create("pref_display_show_scale", "Show scale bar in overlay", false, IType.BOOL).in(NAME,
						PRESENTATION);
		public static final Pref<Boolean> CORE_SHOW_FPS =
				create("pref_display_show_fps", "Show number of frames per second in overlay", false, IType.BOOL)
						.in(NAME, PRESENTATION);

		/**
		 * Charts
		 */
		public static final String CHARTS = "Charts Preferences";
		public static final Pref<Boolean> CHART_FLAT =
				create("pref_display_flat_charts", "Display histograms with a 'flat' look", false, IType.BOOL).in(NAME,
						CHARTS);
		public static final Pref<Boolean> CHART_MEMORIZE = create("pref_display_memorize_charts",
				"Keep the data displayed in charts in memory (to save them later as CSV)", true, IType.BOOL).in(NAME,
						CHARTS);
		public static final Pref<Boolean> CHART_GRIDLINES = create("pref_chart_display_gridlines",
				"Display grid lines on charts by default (true if x_tick_unit or y_tick_unit is defined)", true,
				IType.BOOL).in(NAME, CHARTS);
		/**
		 * Drawing methods and defaults
		 */
		public static final String DRAWING = "Default Rendering Properties";
		public static final Pref<String> CORE_DISPLAY = create("pref_display_default",
				"Default rendering method when none is specified (Java2D for 2D displays, OpenGL for 3D displays)",
				"Java2D", IType.STRING).among("Java2D", "OpenGL").in(NAME, DRAWING);
		public static final Pref<Boolean> CORE_ANTIALIAS = create("pref_display_antialias",
				"Apply antialiasing by default (turning it to false speeds up displays, but the rendering is less smooth)",
				false, IType.BOOL).in(NAME, DRAWING);
		public static final Pref<GamaColor> CORE_BACKGROUND = create("pref_display_background_color",
				"Default background color (can be individually specified by the 'background' facet in 'display')",
				GamaColor.getNamed("white"), IType.COLOR).in(NAME, DRAWING);
		public static final Pref<GamaColor> CORE_HIGHLIGHT = create("pref_display_highlight_color",
				"Default highlight color", new GamaColor(0, 200, 200), IType.COLOR).in(NAME, DRAWING);
		public static final Pref<String> CORE_SHAPE = create("pref_display_default_shape",
				"Defaut shape to use for agents (when no aspects are defined in their species)", "shape", IType.STRING)
						.among("shape", "circle", "square", "triangle", "point", "cube", "sphere").in(NAME, DRAWING);
		public static final Pref<Double> CORE_SIZE = create("pref_display_default_size",
				"Default size to use for agents (when no aspects are defined in their species)", 1.0, IType.FLOAT)
						.between(0.01, null).in(NAME, DRAWING);
		public static final Pref<GamaColor> CORE_COLOR = create("pref_display_default_color",
				"Default color to use for agents (when no aspects are defined in their species)",
				GamaColor.getNamed("yellow"), IType.COLOR).in(NAME, DRAWING);
		/**
		 * Options
		 */
		public static final String OPTIONS = "Advanced Options ";
		public static final Pref<Boolean> DISPLAY_ONLY_VISIBLE = create("pref_display_visible_agents",
				"Only process for display the agents that are visible (speeds up display, but may create visual artifacts)",
				false, IType.BOOL).in(NAME, OPTIONS);
		public static final Pref<Boolean> DISPLAY_NO_ACCELERATION = create("pref_display_no_java2d_acceleration",
				"Disable graphics hardware acceleration for Java2D displays (can be necessary on some hardware configurations)",
				false, IType.BOOL).in(NAME, OPTIONS);

	}

	public static class OpenGL {
		public static final String NAME = "OpenGL";
		/**
		 * Rendering
		 */
		public static final String RENDERING = "Rendering Properties";
		public static final Pref<Boolean> CORE_DRAW_ENV =
				create("pref_display_show_referential", "Draw 3D reference axes", true, IType.BOOL).in(NAME, RENDERING);
		public static final Pref<Boolean> DRAW_ROTATE_HELPER =
				create("pref_display_show_rotation", "Draw rotation reference axes", true, IType.BOOL).in(NAME,
						RENDERING);
		public static final Pref<Double> CORE_LINE_WIDTH = create("pref_display_line_width",
				"Default width of lines (can be individually specified using the facet 'width' of 'draw')", 1.2d,
				IType.FLOAT).in(NAME, RENDERING);
		public static final Pref<Boolean> ONLY_VISIBLE_FACES = create("pref_display_visible_faces",
				"Draw only the faces of objects considered as 'external' by OpenGL", false, IType.BOOL).in(NAME,
						RENDERING);
		public static final Pref<Integer> DISPLAY_SLICE_NUMBER = create("pref_display_slice_number",
				"Number of slices to use for circular geometries in OpenGL (Smaller is faster but less smooth)", 16,
				IType.INT).in(NAME, RENDERING);
		/**
		 * Options
		 */
		public static final String OPTIONS = "Options";
		public static final Pref<Double> OPENGL_ZOOM = create("pref_display_zoom_factor",
				"Set the zoom factor to use (from 0 for a slow zoom to 1 for a fast one)", 0.5, IType.FLOAT)
						.in(NAME, OPTIONS).between(0, 1);
		public static final Pref<Integer> OPENGL_FPS =
				create("pref_display_max_fps", "Set the maximum number of frames per second to display", 20, IType.INT)
						.in(NAME, OPTIONS);
		// public static final Pref<Boolean> DISPLAY_SHARED_CONTEXT = create("pref_display_shared_cache",
		// "Enable OpenGL background loading of textures (faster, but can cause issues on Linux and Windows)",
		// false, IType.BOOL).in(NAME, OPTIONS);
		public static final Pref<Boolean> DISPLAY_POWER_OF_TWO = create("pref_display_power_of_2",
				"Forces the dimensions of OpenGL textures to be power of 2 (e.g. 8x8, 16x16, etc. Necessary on some hardware configurations)",
				false, IType.BOOL).in(NAME, OPTIONS);
		public static final Pref<Boolean> OPENGL_TRIANGULATOR = create("pref_display_triangulator",
				"Use OpenGL native tesselator to triangulate shapes (false will make GAMA use a more precise, but more CPU intensive, JTS alternative)",
				true, IType.BOOL).in(NAME, OPTIONS);
	}

	public static class External {
		public static final String NAME = "External";
		/**
		 * Paths to libraries
		 */
		public static final String PATHS = "Paths to external libraries";
		public static final Pref<? extends IGamaFile> LIB_SPATIALITE =
				create("pref_lib_spatialite", "Path to the Spatialite (see http://www.gaia-gis.it/gaia-sins/) library",
						new GenericFile("Enter path", false), IType.FILE).in(NAME, PATHS);
		public static final Pref<? extends IGamaFile> LIB_R =
				create("pref_lib_r", "Path to the RScript (see http://www.r-project.org) library",
						new GenericFile(getDefaultRPath(), false), IType.FILE).in(NAME, PATHS);
		/**
		 * GeoTools
		 */
		public static final String GEOTOOLS =
				"GIS Coordinate Reference Systems (see http://spatialreference.org/ref/epsg/ for EPSG codes)";
		public static final Pref<Boolean> LIB_TARGETED =
				create("pref_gis_auto_crs", "Let GAMA decide which CRS to use to project GIS data", true, IType.BOOL)
						.deactivates("pref_gis_default_crs").in(NAME, GEOTOOLS);
		public static final Pref<Integer> LIB_TARGET_CRS =
				create("pref_gis_default_crs", "...or use the following CRS (EPSG code)", 32648, IType.INT)
						.in(NAME, GEOTOOLS).addChangeListener(new IPreferenceChangeListener<Integer>() {

							@Override
							public boolean beforeValueChange(final Integer newValue) {
								final Set<String> codes = CRS.getSupportedCodes(newValue.toString());
								if (codes.isEmpty()) { return false; }
								return true;
							}

							@Override
							public void afterValueChange(final Integer newValue) {}
						});
		public static final Pref<Boolean> LIB_PROJECTED = create("pref_gis_same_crs",
				"When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS", true,
				IType.BOOL).deactivates("pref_gis_initial_crs").in(NAME, GEOTOOLS);
		public static final Pref<Integer> LIB_INITIAL_CRS =
				create("pref_gis_initial_crs", "...or use the following CRS (EPSG code)", 4326, IType.INT)
						.in(NAME, GEOTOOLS).addChangeListener(new IPreferenceChangeListener<Integer>() {

							@Override
							public boolean beforeValueChange(final Integer newValue) {
								final Set<String> codes = CRS.getSupportedCodes(newValue.toString());
								if (codes.isEmpty()) { return false; }
								return true;
							}

							@Override
							public void afterValueChange(final Integer newValue) {}
						});
		public static final Pref<Boolean> LIB_USE_DEFAULT =
				create("pref_gis_save_crs", "When no CRS is provided, save the GIS data with the current CRS", true,
						IType.BOOL).deactivates("pref_gis_output_crs").in(NAME, GEOTOOLS);
		public static final Pref<Integer> LIB_OUTPUT_CRS =
				create("pref_gis_output_crs", "... or use this following CRS (EPSG code)", 4326, IType.INT)
						.in(NAME, GEOTOOLS).addChangeListener(new IPreferenceChangeListener<Integer>() {

							@Override
							public boolean beforeValueChange(final Integer newValue) {
								final Set<String> codes = CRS.getSupportedCodes(newValue.toString());
								if (codes.isEmpty()) { return false; }
								return true;
							}

							@Override
							public void afterValueChange(final Integer newValue) {}
						});
		
		/**
		 * GeoTools
		 */
		public static final String GEOMETRY =
				"Geometry";
		public static final Pref<Double> TOLERANCE_POINTS =
				create("pref_geometry_tolerance", "Tolerance for the comparison of points", 0.0, IType.FLOAT).in(NAME, GEOMETRY);
		

		private static String getDefaultRPath() {
			final String os = System.getProperty("os.name");
			final String osbit = System.getProperty("os.arch");
			if (os.startsWith("Mac")) {
				if (osbit.endsWith(
						"64")) { return "/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/x86_64/RScript"; }
				return "/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/i386/RScript";
			} else if (os.startsWith("Linux")) { return "usr/bin/RScript"; }
			if (os.startsWith("Windows")) {
				if (osbit.endsWith("64")) { return "C:\\Program Files\\R\\R-2.15.1\\bin\\x64\\Rscript.exe"; }
				return "C:\\Program Files\\R\\R-2.15.1\\bin\\Rscript.exe";
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
		if (key.contains(".") || key.contains(" "))
			System.out.println("WARNING. Preference " + key + " cannot be used as a variable");
		final Pref<T> e = new Pref<T>(key, type).named(title).in(Interface.NAME, "").init(value);
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
			case IType.INT:
				if (storeKeys.contains(key)) {
					gp.setValue(scope, store.getInt(key, GamaIntegerType.staticCast(scope, value, null, false)));
				} else {
					store.putInt(key, GamaIntegerType.staticCast(scope, value, null, false));
				}
				break;
			case IType.FLOAT:
				if (storeKeys.contains(key)) {
					gp.setValue(scope, store.getDouble(key, GamaFloatType.staticCast(scope, value, null, false)));
				} else {
					store.putDouble(key, GamaFloatType.staticCast(scope, value, null, false));
				}
				break;
			case IType.BOOL:
				value = GamaBoolType.staticCast(scope, value, null, false);
				if (storeKeys.contains(key)) {
					gp.setValue(scope, store.getBoolean(key, (Boolean) value));
				} else {
					store.putBoolean(key, (Boolean) value);
				}
				break;
			case IType.STRING:
				if (storeKeys.contains(key)) {
					gp.setValue(scope,
							store.get(key, StringUtils.toJavaString(GamaStringType.staticCast(scope, value, false))));
				} else {
					store.put(key, StringUtils.toJavaString(GamaStringType.staticCast(scope, value, false)));
				}
				break;
			case IType.FILE:
				if (storeKeys.contains(key)) {
					gp.setValue(scope, new GenericFile(store.get(key, ""), false));
				} else {
					store.put(key, value == null ? "" : ((IGamaFile) value).getPath(scope));
				}
				break;
			case IType.COLOR:
				// Stores the preference as an int but create a color
				if (storeKeys.contains(key)) {
					final int val = store.getInt(key, GamaIntegerType.staticCast(scope, value, null, false));
					gp.setValue(scope, GamaColor.getInt(val));
				} else {
					store.putInt(key, GamaIntegerType.staticCast(scope, value, null, false));
				}
				break;
			case IType.FONT:
				if (storeKeys.contains(key)) {
					final String val = store.get(key, GamaStringType.staticCast(scope, value, false));
					gp.setValue(scope, GamaFontType.staticCast(scope, val, false));
				} else {
					store.put(key, GamaStringType.staticCast(scope, value, false));
				}
				break;
			case IType.DATE:
				if (storeKeys.contains(key)) {
					final String val =
							StringUtils.toJavaString(store.get(key, GamaStringType.staticCast(scope, value, false)));
					gp.setValue(scope, GamaDate.fromISOString(val));
				} else {
					store.put(key, GamaStringType.staticCast(scope, value, false));
				}
				break;
			default:
				if (storeKeys.contains(key)) {
					gp.setValue(scope, store.get(key, GamaStringType.staticCast(scope, value, false)));
				} else {
					store.put(key, GamaStringType.staticCast(scope, value, false));
				}
		}
		try {
			store.flush();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
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
			modelValues.put(name, e.initial);
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
		try {
			final FileInputStream is = new FileInputStream(path);
			store.importPreferences(is);
			is.close();
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

	static Interface i = new Interface();
	static Modeling m = new Modeling();
	static Runtime r = new Runtime();
	static Experiments e = new Experiments();
	static Simulations s = new Simulations();
	static Displays d = new Displays();
	static OpenGL o = new OpenGL();
	static External ext = new External();

}
