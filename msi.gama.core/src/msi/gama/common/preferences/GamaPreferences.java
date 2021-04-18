/*******************************************************************************************************
 *
 * msi.gama.common.preferences.GamaPreferences.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.preferences;

import static msi.gama.common.util.StringUtils.toJavaString;
import static msi.gama.util.GamaDate.fromISOString;
import static msi.gaml.operators.Cast.asBool;
import static msi.gaml.operators.Cast.asFloat;
import static msi.gaml.operators.Cast.asInt;
import static msi.gaml.operators.Cast.asPoint;
import static msi.gaml.operators.Cast.asString;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.geotools.referencing.CRS;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.IPreferenceChangeListener.IPreferenceBeforeChangeListener;
import msi.gama.common.preferences.Pref.ValueProvider;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GenericFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.kernel.GamaMetaModel;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.GamaFontType;
import msi.gaml.types.IType;
import one.util.streamex.StreamEx;

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
				create("pref_show_welcome_page", "Display welcome page", true, IType.BOOL, false).in(NAME, STARTUP);
		public static final Pref<Boolean> CORE_SHOW_MAXIMIZED =
				create("pref_show_maximized", "Maximize GAMA window", true, IType.BOOL, false).in(NAME, STARTUP);
		public static final Pref<Boolean> CORE_ASK_REBUILD =
				create("pref_ask_rebuild", "Ask before rebuilding a corrupted workspace", true, IType.BOOL, false)
						.in(NAME, STARTUP);
		public static final Pref<Boolean> CORE_ASK_OUTDATED = create("pref_ask_outdated",
				"Ask before using a workspace created by another version", true, IType.BOOL, false).in(NAME, STARTUP);
		/**
		 * Menus
		 */
		public static final String MENUS = "Menus";
		public static final Pref<Integer> CORE_MENU_SIZE =
				create("pref_menu_size", "Break down agents in menus every", 50, IType.INT, false).between(10, 1000)
						.in(NAME, MENUS);
		/**
		 * Console
		 */
		public static final String CONSOLE = "Console";
		public static final Pref<Integer> CORE_CONSOLE_SIZE = create("pref_console_size",
				"Max. number of characters to display (-1 = unlimited)", 20000, IType.INT, true).in(NAME, CONSOLE);
		public static final Pref<Integer> CORE_CONSOLE_BUFFER =
				create("pref_console_buffer", "Max. number of characters to keep when paused (-1 = unlimited)", 20000,
						IType.INT, true).in(NAME, CONSOLE);
		public static final Pref<Boolean> CORE_CONSOLE_WRAP =
				create("pref_console_wrap", "Wrap long lines (can slow down output)", false, IType.BOOL, true).in(NAME,
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
				"Append the name of simulations to their outputs", false, IType.BOOL, true).in(NAME, SIMULATIONS);
		public static final Pref<GamaColor>[] SIMULATION_COLORS = new Pref[5];

		public static Pref<Boolean> KEEP_NAVIGATOR_STATE = create("pref_keep_navigator_state",
				"Maintain the state of the navigator across sessions", true, IType.BOOL, false).in(NAME, STARTUP);

		static {
			for (var i = 0; i < 5; i++) {
				SIMULATION_COLORS[i] = create("pref_simulation_color_" + i,
						"Color of Simulation " + i + " in the UI (console, view tabs) ", BASIC_COLORS[i], IType.COLOR,
						true).in(NAME, SIMULATIONS);
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
		// public static final Pref<Boolean> CORE_PERSPECTIVE = create("pref_switch_perspective",
		// "Switch to modeling perspective when editing a model", false, IType.BOOL, false).in(NAME, OPTIONS);
		/**
		 * Validation
		 */
		// public static final String VALIDATION = "Validation of Models";
		public static final Pref<Boolean> WARNINGS_ENABLED =
				create("pref_editor_enable_warnings", "Show warning markers in the editor", true, IType.BOOL, false)
						.in(NAME, OPTIONS);

		public static final Pref<Boolean> INFO_ENABLED =
				create("pref_editor_enable_infos", "Show information markers in the editor", true, IType.BOOL, false)
						.in(NAME, OPTIONS);

		public static final Pref<Boolean> EDITOR_PERSPECTIVE_SAVE =
				create("pref_editor_perspective_save", "Save all editors when switching perspectives", true, IType.BOOL,
						false).in(Modeling.NAME, Modeling.OPTIONS).activates("pref_editor_ask_save");
		public static final Pref<Boolean> EDITOR_PERSPECTIVE_HIDE = create("pref_editor_perspective_hide",
				"Hide editors when switching to simulation perspectives (can be overriden in the 'layout' statement)",
				true, IType.BOOL, false).in(Modeling.NAME, Modeling.OPTIONS);
		public static Pref<String> OPERATORS_MENU_SORT = GamaPreferences
				.create("pref_menu_operators_sort", "Sort operators menu by", "Category", IType.STRING, false)
				.among("Name", "Category").in(Interface.NAME, Interface.MENUS);

		public static final Pref<Boolean> CORE_CLOSE_QUOTE = GamaPreferences
				.create("pref_editor_close_quote", "Automatically close single quotes — '..'", true, IType.BOOL, false)
				.in(NAME, TEXT);
		public static final Pref<Boolean> CORE_CLOSE_DOUBLE = GamaPreferences.create("pref_editor_close_double",
				"Automatically close double quotes — \"..\"", true, IType.BOOL, false).in(NAME, TEXT);
		public static final Pref<Boolean> CORE_CLOSE_CURLY = GamaPreferences
				.create("pref_editor_close_curly", "Automatically close curly brackets — {..}", true, IType.BOOL, false)
				.in(NAME, TEXT);
		public static final Pref<Boolean> CORE_CLOSE_SQUARE = GamaPreferences.create("pref_editor_close_square",
				"Automatically close square brackets — [..]", true, IType.BOOL, false).in(NAME, TEXT);
		public static final Pref<Boolean> CORE_CLOSE_PARENTHESES =
				GamaPreferences.create("pref_editor_close_parentheses", "Automatically close parentheses — (..)", true,
						IType.BOOL, false).in(NAME, TEXT);
		public static final Pref<Boolean> EDITOR_CLEAN_UP =
				GamaPreferences.create("pref_editor_save_format", "Apply formatting on save", false, IType.BOOL, false)
						.in(NAME, GamaPreferences.Modeling.OPTIONS);
		public static final Pref<Boolean> EDITOR_SAVE =
				GamaPreferences
						.create("pref_editor_save_all", "Save all editors before lauching an experiment", true,
								IType.BOOL, false)
						.in(NAME, GamaPreferences.Modeling.OPTIONS).activates("pref_editor_ask_save");
		public static final Pref<Boolean> EDITOR_DRAG_RESOURCES = GamaPreferences.create("pref_editor_drag_resources",
				"Drag files and resources as references in GAML files", true, IType.BOOL, false).in(NAME, OPTIONS);
		public static final Pref<Boolean> EDITOR_SAVE_ASK =
				GamaPreferences.create("pref_editor_ask_save", "Ask before saving each file", false, IType.BOOL, false)
						.in(NAME, OPTIONS);
		public static final Pref<Boolean> EDITBOX_ENABLED = GamaPreferences
				.create("pref_editor_editbox_on", "Turn on colorization of code sections", false, IType.BOOL, false)
				.in(NAME, TEXT);
		public static final Pref<GamaFont> EDITOR_BASE_FONT = GamaPreferences
				.create("pref_editor_font", "Font of editors", (GamaFont) null, IType.FONT, false).in(NAME, TEXT);
		public static final Pref<GamaColor> EDITOR_BACKGROUND_COLOR =
				GamaPreferences.create("pref_editor_background_color", "Background color of editors", (GamaColor) null,
						IType.COLOR, false).in(NAME, TEXT);
		public static final Pref<Boolean> EDITOR_MARK_OCCURRENCES = GamaPreferences
				.create("pref_editor_mark_occurrences", "Mark occurrences of symbols", true, IType.BOOL, false)
				.in(NAME, TEXT);

		// .activates("pref_tests_period");
		// public static final Pref<String> TESTS_PERIOD = create("pref_tests_period", "Every", "Update", IType.STRING)
		// .among(Arrays.asList("Day", "Week", "Month", "Update")).in(NAME, TESTS);

	}
	//
	// public static class Experiments {
	// public static final String NAME = "Experiments";
	//
	// // Unused for the moment
	// }

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
		public static final Pref<Boolean> CORE_AUTO_RUN = create("pref_experiment_auto_run",
				"Auto-run experiments when they are launched", false, IType.BOOL, true).in(NAME, EXECUTION);
		public static final Pref<Boolean> CORE_ASK_CLOSING =
				create("pref_experiment_ask_closing", "Ask to close the previous experiment when launching a new one",
						true, IType.BOOL, true).in(NAME, EXECUTION);
		public static final Pref<Boolean> CORE_ASK_FULLSCREEN =
				create("pref_experiment_ask_fullscreen", "Ask to go to fullscreen mode", false, IType.BOOL, true)
						.in(NAME, EXECUTION);
		// public static final Pref<Double> CORE_DELAY_STEP = create("pref_experiment_default_step",
		// "Default step for the delay slider (in sec.)", 0.001, IType.FLOAT, true).in(NAME, EXECUTION).disabled();
		public static final Pref<Boolean> CORE_SYNC =
				create("pref_display_synchronized", "Synchronize outputs with the simulation", false, IType.BOOL, true)
						.in(NAME, EXECUTION);
		public static final Pref<Boolean> CORE_EXPAND_PARAMS =
				create("pref_experiment_expand_params", "Auto expand Parameters Categories", false, IType.BOOL, true)
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
				create("pref_tests_sorted", "Sorts the results of tests by severity", false, IType.BOOL, false)
						.in(NAME, TESTS).withComment(", if true, aborted and failed tests are displayed first");
		// public static final Pref<Boolean> RUN_TESTS =
		// create("pref_run_tests", "Run tests after each update of the platform", false, IType.BOOL, false)
		// .in(NAME, TESTS).disabled().hidden();
		public static final Pref<Boolean> START_TESTS =
				create("pref_start_tests", "Run tests at each start of the platform", false, IType.BOOL, false).in(NAME,
						TESTS);
		public static final Pref<Boolean> USER_TESTS =
				create("pref_user_tests", "Include user-defined tests in the tests suite", false, IType.BOOL, false)
						.in(NAME, TESTS).withComment(", if true, will run user models with 'test' experiments");
		public static final Pref<Boolean> FAILED_TESTS = create("pref_failed_tests",
				"Only display (in the UI and in headless runs) failed and aborted tests", false, IType.BOOL, true)
						.in(NAME, TESTS).withComment(", if true, only aborted and failed tests are displayed");

		public static final String MEMORY = "Memory";
		public static final Pref<Boolean> CORE_MEMORY_POLLING =
				create("pref_check_memory", "Monitor memory and emit a warning if it is low", true, IType.BOOL, true)
						.in(NAME, MEMORY).activates("pref_memory_threshold", "pref_memory_frequency");
		public static final Pref<Integer> CORE_MEMORY_PERCENTAGE =
				create("pref_memory_threshold", "Trigger warnings when the percentage of available memory is below", 20,
						IType.INT, true).in(NAME, MEMORY);
		public static final Pref<Integer> CORE_MEMORY_FREQUENCY = create("pref_memory_frequency",
				"Interval (in seconds) at which memory should be monitored", 2, IType.INT, true).in(NAME, MEMORY);
		public static final Pref<Boolean> CORE_MEMORY_ACTION = create("pref_memory_action",
				"If true, when running out of memory, GAMA will try to close the experiment, otherwise it exits", true,
				IType.BOOL, true).in(NAME, MEMORY);
		/**
		 * Errors & warnings
		 */
		public static final String ERRORS = "Runtime errors";

		public static final Pref<Boolean> CORE_SHOW_ERRORS =
				create("pref_errors_display", "Show execution errors", true, IType.BOOL, true).in(NAME, ERRORS)
						.activates("pref_errors_number", "pref_errors_recent_first", "pref_display_show_errors");
		public static final Pref<Boolean> ERRORS_IN_DISPLAYS = create("pref_display_show_errors",
				"Show errors thrown in displays and outputs", false, IType.BOOL, true).in(NAME, ERRORS);
		public static final Pref<Integer> CORE_ERRORS_NUMBER =
				create("pref_errors_number", "Number of errors to display", 10, IType.INT, true).in(NAME, ERRORS)
						.between(1, null);
		public static final Pref<Boolean> CORE_RECENT =
				create("pref_errors_recent_first", "Display most recent first", true, IType.BOOL, true).in(NAME,
						ERRORS);
		public static final Pref<Boolean> CORE_REVEAL_AND_STOP =
				create("pref_errors_stop", "Stop simulation at first error", true, IType.BOOL, true).in(NAME, ERRORS);
		public static final Pref<Boolean> CORE_WARNINGS =
				create("pref_errors_warnings_errors", "Treat warnings as errors", false, IType.BOOL, true).in(NAME,
						ERRORS);
		public static final Pref<Boolean> CORE_ERRORS_EDITOR_LINK = create("pref_errors_in_editor",
				"Automatically open an editor and point at the faulty part of the model if an error or a warning is thrown",
				true, IType.BOOL, true).in(NAME, ERRORS);

	}

	public static class Displays {
		public static final String NAME = "Displays";
		public static final String PRESENTATION = "Presentation and Behavior of Graphical Display Views";
		/**
		 * Presentation
		 */
		public static final List<String> LAYOUTS = Arrays.asList("None", "Stacked", "Split", "Horizontal", "Vertical");
		public static final Pref<String> CORE_DISPLAY_LAYOUT =
				create("pref_display_view_layout", "Default layout of display views", "None", IType.STRING, true)
						.among(LAYOUTS.toArray(new String[LAYOUTS.size()])).in(NAME, PRESENTATION);
		// Unused code found by UCDetector
		// public static final Pref<Boolean> CORE_DISPLAY_ORDER = create("pref_display_same_order",
		// "Stack displays in the order defined in the model", true, IType.BOOL, true).in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_BORDER =
				create("pref_display_show_border", "Display a border around display views", false, IType.BOOL, true)
						.in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_PERSPECTIVE =
				create("pref_display_continue_drawing", "Continue to draw displays when in Modeling perspective", false,
						IType.BOOL, true).in(NAME, PRESENTATION);
		public static final Pref<Boolean> DISPLAY_FAST_SNAPSHOT = create("pref_display_fast_snapshot",
				"Enable fast snapshots (uncomplete when the display is obscured by others but much faster)", false,
				IType.BOOL, true).in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_TOOLBAR =
				create("pref_display_show_toolbar", "Show the display top toolbar", true, IType.BOOL, true).in(NAME,
						PRESENTATION);
		public static final Pref<Boolean> CORE_OVERLAY =
				create("pref_display_show_overlay", "Show the display bottom overlay", false, IType.BOOL, true).in(NAME,
						PRESENTATION);

		/**
		 * Charts
		 */
		public static final String CHARTS = "Charts Preferences";
		public static final Pref<Boolean> CHART_FLAT =
				create("pref_display_flat_charts", "Display 'flat' histograms", true, IType.BOOL, true).in(NAME,
						CHARTS);
		public static final Pref<Boolean> CHART_MEMORIZE = create("pref_display_memorize_charts",
				"Keep values in memory (to save them as CSV)", true, IType.BOOL, true).in(NAME, CHARTS);
		public static final Pref<Boolean> CHART_GRIDLINES =
				create("pref_chart_display_gridlines", "Display grid lines", true, IType.BOOL, true).in(NAME, CHARTS);
		/**
		 * Drawing methods and defaults
		 */
		public static final String DRAWING = "Default Rendering Properties";
		public static final Pref<String> CORE_DISPLAY =
				create("pref_display_default", "Default rendering method (Java2D for 2D, OpenGL for 3D)", "Java2D",
						IType.STRING, true).among("Java2D", "OpenGL").in(NAME, DRAWING);
		public static final Pref<Boolean> CORE_ANTIALIAS =
				create("pref_display_antialias", "Apply antialiasing", false, IType.BOOL, true).in(NAME, DRAWING);
		public static final Pref<GamaColor> CORE_BACKGROUND =
				create("pref_display_background_color", "Default background color ('background' facet of 'display')",
						() -> GamaColor.getNamed("white"), IType.COLOR, true).in(NAME, DRAWING);
		public static final Pref<GamaColor> CORE_HIGHLIGHT = create("pref_display_highlight_color",
				"Default highlight color", () -> new GamaColor(0, 200, 200), IType.COLOR, true).in(NAME, DRAWING);
		public static final Pref<String> CORE_SHAPE =
				create("pref_display_default_shape", "Defaut shape of agents", "shape", IType.STRING, true)
						.among("shape", "circle", "square", "triangle", "point", "cube", "sphere").in(NAME, DRAWING);
		public static final Pref<Double> CORE_SIZE =
				create("pref_display_default_size", "Default size of agents", 1.0, IType.FLOAT, true)
						.between(0.01, null).in(NAME, DRAWING);
		public static final Pref<GamaColor> CORE_COLOR = create("pref_display_default_color", "Default color of agents",
				() -> GamaColor.getNamed("yellow"), IType.COLOR, true).in(NAME, DRAWING);
		/**
		 * Options
		 */
		public static final String OPTIONS = "Advanced ";
		public static final Pref<Boolean> DISPLAY_ONLY_VISIBLE = create("pref_display_visible_agents",
				"Only display visible agents (faster, may create visual oddities)", false, IType.BOOL, true).in(NAME,
						OPTIONS);
		public static final Pref<Boolean> DISPLAY_NO_ACCELERATION = create("pref_display_no_java2d_acceleration",
				"Disable acceleration for Java2D (necessary on some configurations)", false, IType.BOOL, true).in(NAME,
						OPTIONS);
		/**
		 * OPENGL
		 */
		public static final String RENDERING = "OpenGL Rendering Properties";
		public static final Pref<Boolean> CORE_DRAW_ENV =
				create("pref_display_show_referential", "Draw 3D axes", true, IType.BOOL, true).in(NAME, RENDERING);
		public static final Pref<Boolean> DRAW_ROTATE_HELPER =
				create("pref_display_show_rotation", "Draw rotation axes", true, IType.BOOL, true).in(NAME, RENDERING);
		public static final Pref<Double> CORE_LINE_WIDTH = create("pref_display_line_width",
				"Default line width (facet 'width' of 'draw')", 1.2d, IType.FLOAT, true).in(NAME, RENDERING);
		public static final Pref<Boolean> ONLY_VISIBLE_FACES = create("pref_display_visible_faces",
				"Draw only the 'external' faces of objects", false, IType.BOOL, true).in(NAME, RENDERING).hidden();
		public static final Pref<Integer> DISPLAY_SLICE_NUMBER =
				create("pref_display_slice_number", "Number of slices of circular geometries", 16, IType.INT, true)
						.in(NAME, RENDERING);
		/**
		 * Options
		 */
		// public static final String OPTIONS = "OpenGL ";
		public static final Pref<Double> OPENGL_ZOOM =
				create("pref_display_zoom_factor", "Set the zoom factor (0 for slow, 1 fast)", 0.5, IType.FLOAT, true)
						.in(NAME, RENDERING).between(0, 1);
		public static final Pref<Integer> OPENGL_FPS =
				create("pref_display_max_fps", "Max. number of frames per second", 20, IType.INT, true).in(NAME,
						RENDERING);
		// public static final Pref<Boolean> DISPLAY_SHARED_CONTEXT = create("pref_display_shared_cache",
		// "Enable OpenGL background loading of textures (faster, but can cause issues on Linux and Windows)",
		// false, IType.BOOL).in(NAME, OPTIONS);
		public static final Pref<Boolean> DISPLAY_POWER_OF_TWO = create("pref_display_power_of_2",
				"Forces textures dimensions to a power of 2 (e.g. 16x16. Necessary on some configurations)", false,
				IType.BOOL, true).in(NAME, RENDERING);
		public static final Pref<Boolean> OPENGL_TRIANGULATOR = create("pref_display_triangulator",
				"Use OpenGL tesselator (false is more precise, but more CPU intensive)", true, IType.BOOL, true)
						.in(NAME, RENDERING);
		public static final Pref<Boolean> OPENGL_NUM_KEYS_CAM = create("pref_display_numkeyscam",
				"Use Numeric Keypad (2,4,6,8) for camera interaction", true, IType.BOOL, true).in(NAME, RENDERING);
		public static final Pref<Boolean> OPENGL_CLIPBOARD_CAM = create("pref_display_clipboard_cam",
				"Copy the camera definition to the clipboard when it is changed on the display", false, IType.BOOL,
				true).in(NAME, RENDERING);
	}

	public static class External {
		public static final String NAME = "Data and Operators";
		/**
		 * Http connections
		 */
		public static final String HTTP = "Http connections";
		public static final Pref<Integer> CORE_HTTP_CONNECT_TIMEOUT =
				create("pref_http_connect_timeout", "Connection timeout (in ms)", 20000, IType.INT, true).in(NAME,
						HTTP);
		public static final Pref<Integer> CORE_HTTP_READ_TIMEOUT =
				create("pref_http_read_timeout", "Read timeout (in ms)", 20000, IType.INT, true).in(NAME, HTTP);
		public static final Pref<Integer> CORE_HTTP_RETRY_NUMBER = create("pref_http_retry_number",
				"Number of times to retry if connection cannot be established", 3, IType.INT, true).in(NAME, HTTP);
		public static final Pref<Boolean> CORE_HTTP_EMPTY_CACHE = create("pref_http_empty_cache",
				"Empty the local cache of files downloaded from the web", true, IType.BOOL, true).in(NAME, HTTP);

		/**
		 * Random numbers
		 */
		public static final String RNG = "Random number generation";
		public static final Pref<String> CORE_RNG =
				create("pref_rng_name", "Default random number generator", IKeyword.MERSENNE, IType.STRING, true)
						.among(GENERATOR_NAMES).in(NAME, RNG);
		public static final Pref<Boolean> CORE_SEED_DEFINED =
				create("pref_rng_define_seed", "Define a default seed", false, IType.BOOL, true)
						.activates("pref_rng_default_seed").in(NAME, RNG);
		public static final Pref<Double> CORE_SEED =
				create("pref_rng_default_seed", "Default seed value (0 is undefined)", 1d, IType.FLOAT, true).in(NAME,
						RNG);
		public static final Pref<Boolean> CORE_RND_EDITABLE =
				create("pref_rng_in_parameters", "Include in the parameters", false, IType.BOOL, true).in(NAME, RNG);
		/**
		 * Dates
		 */
		public static final String DATES = "Management of dates";
		/**
		 * Optimizations
		 */
		public static final String OPTIMIZATIONS = "Optimizations";
		public static final Pref<Boolean> CONSTANT_OPTIMIZATION = create("pref_optimize_constant_expressions",
				"Optimize constant expressions (experimental)", false, IType.BOOL, true).in(NAME, OPTIMIZATIONS);
		public static final Pref<Boolean> AGENT_OPTIMIZATION =
				create("pref_optimize_agent_memory", "Optimize agents memory", true, IType.BOOL, true).in(NAME,
						OPTIMIZATIONS);
		// public static final Pref<Boolean> MATH_OPTIMIZATION = create("pref_optimize_math_functions",
		// "Use faster (but less accurate) arithmetic functions", false, IType.BOOL, true).in(NAME, OPTIMIZATIONS);
		public static final Pref<Boolean> AT_DISTANCE_OPTIMIZATION =
				create("pref_optimize_at_distance", "Optimize the 'at_distance' operator", true, IType.BOOL, true)
						.in(NAME, OPTIMIZATIONS);
		public static final Pref<Boolean> PATH_COMPUTATION_OPTIMIZATION = create("pref_optimize_path_computation",
				"Optimize the path computation operators and goto action (but with possible 'jump' issues)", false,
				IType.BOOL, true).in(NAME, OPTIMIZATIONS);
		public static final Pref<Boolean> QUADTREE_OPTIMIZATION = create("pref_optimize_quadtree",
				"Optimize spatial queries: add agents only when necessary in the quadtree (still experimental)", false,
				IType.BOOL, true).in(NAME, OPTIMIZATIONS);
		public static final Pref<Boolean> QUADTREE_SYNCHRONIZATION = create("pref_synchronize_quadtree",
				"Forces the spatial index to synchronize its operations. Useful for interactive models where the user may interfere.",
				true, IType.BOOL, true).in(NAME, OPTIMIZATIONS);
		public static final Pref<Boolean> USE_POOLING =
				create("pref_use_pooling", "Use object pooling to reduce memory usage (still experimental)", false,
						IType.BOOL, true).in(NAME, OPTIMIZATIONS);
		public static final Pref<Double> TOLERANCE_POINTS =
				create("pref_point_tolerance", "Tolerance for the comparison of points", 0.0, IType.FLOAT, true)
						.in(NAME, OPTIMIZATIONS);

		/**
		 * Paths to libraries
		 */
		public static final String PATHS = "External libraries support";
		public static final Pref<? extends IGamaFile> LIB_SPATIALITE =
				create("pref_lib_spatialite", "Path to Spatialite library (http://www.gaia-gis.it/gaia-sins/)",
						() -> new GenericFile("Enter path", false), IType.FILE, true).in(NAME, PATHS);
		public static final String jriFile = System.getProperty("os.name").startsWith("Mac") ? "libjri.jnilib"
				: System.getProperty("os.name").startsWith("Linux") ? "libjri.so" : "jri.dll";

		public static final Pref<? extends IGamaFile> LIB_R = create("pref_lib_r",
				"Path to JRI library ($R_HOME/library/rJava/jri/" + jriFile + ") (http://www.r-project.org)",
				() -> new GenericFile(getDefaultRPath(), false), IType.FILE, true).in(NAME, PATHS);
		/**
		 * GeoTools
		 */
		public static final String GEOTOOLS =
				"GIS Coordinate Reference Systems (http://spatialreference.org/ref/epsg/ for EPSG codes)";
		public static final Pref<Boolean> LIB_TARGETED = create("pref_gis_auto_crs",
				"Let GAMA find which CRS to use to project GIS data", true, IType.BOOL, true).in(NAME, GEOTOOLS);
		public static final Pref<Boolean> LIB_PROJECTED = create("pref_gis_same_crs",
				"When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS", true,
				IType.BOOL, true).deactivates("pref_gis_initial_crs").in(NAME, GEOTOOLS);
		public static final Pref<Boolean> LIB_USE_DEFAULT =
				create("pref_gis_save_crs", "When no CRS is provided, save the GIS data with the current CRS", true,
						IType.BOOL, true).deactivates("pref_gis_output_crs").in(NAME, GEOTOOLS);
		public static final Pref<Integer> LIB_TARGET_CRS = create("pref_gis_default_crs",
				"...or use the following EPSG code (the one that will also be used if no projection information is found)",
				32648, IType.INT, true).in(NAME, GEOTOOLS)
						.addChangeListener((IPreferenceBeforeChangeListener<Integer>) newValue -> {
							final var codes = CRS.getSupportedCodes(newValue.toString());
							if (codes.isEmpty()) return false;
							return true;
						});

		public static final Pref<Integer> LIB_INITIAL_CRS =
				create("pref_gis_initial_crs", "...or use the following CRS (EPSG code)", 4326, IType.INT, true)
						.in(NAME, GEOTOOLS).addChangeListener((IPreferenceBeforeChangeListener<Integer>) newValue -> {
							final var codes = CRS.getSupportedCodes(newValue.toString());
							if (codes.isEmpty()) return false;
							return true;
						});

		public static final Pref<Integer> LIB_OUTPUT_CRS =
				create("pref_gis_output_crs", "... or use this following CRS (EPSG code)", 4326, IType.INT, true)
						.in(NAME, GEOTOOLS).addChangeListener((IPreferenceBeforeChangeListener<Integer>) newValue -> {
							final var codes = CRS.getSupportedCodes(newValue.toString());
							if (codes.isEmpty()) return false;
							return true;
						});

		// RScript adress:
		// "/Library/Frameworks/R.framework/Versions/3.4/Resources/bin/exec/x86_64/RScript"
		// "usr/bin/RScript"
		// "C:\\Program Files\\R\\R-2.15.1\\bin\\x64\\Rscript.exe"
		// "C:\\Program Files\\R\\R-2.15.1\\bin\\Rscript.exe"
		private static String getDefaultRPath() {
			final var os = System.getProperty("os.name");
			final var osbit = System.getProperty("os.arch");
			if (os.startsWith("Mac"))
				return "/Library/Frameworks/R.framework/Resources/library/rJava/jri/libjri.jnilib";
			else if (os.startsWith("Linux")) return "/usr/local/lib/libjri.so";
			if (os.startsWith("Windows")) {
				if (osbit.endsWith("64")) return "C:\\Program Files\\R\\R-3.4.0\\library\\rJava\\jri\\jri.dll";
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

	public static <T> Pref<T> create(final String key, final String title, final T value, final int type,
			final boolean inGaml) {
		// if (key.contains(".") || key.contains(" ")) {
		// DEBUG.OUT("WARNING. Preference " + key + " cannot be used as a variable");
		// }
		final var e = new Pref<T>(key, type, inGaml).named(title).in(Interface.NAME, "").init(value);
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
			final int type, final boolean inGaml) {
		// if (key.contains(".") || key.contains(" ")) {
		// DEBUG.OUT("WARNING. Preference " + key + " cannot be used as a variable");
		// }
		final var e = new Pref<T>(key, type, inGaml).named(title).in(Interface.NAME, "").init(provider);
		register(e);
		return e;
	}

	static final String DEFAULT_FONT = "Default";

	private static void register(final Pref gp) {
		final IScope scope = null;
		final var key = gp.key;
		if (key == null) return;
		prefs.put(key, gp);
		final var value = gp.value;
		if (storeKeys.contains(key)) {
			switch (gp.type) {
				case IType.POINT:
					gp.init(() -> asPoint(scope, store.get(key, asString(scope, value)), false));
					break;
				case IType.INT:
					gp.init(() -> store.getInt(key, asInt(scope, value)));
					break;
				case IType.FLOAT:
					gp.init(() -> store.getDouble(key, asFloat(scope, value)));
					break;
				case IType.BOOL:
					gp.init(() -> store.getBoolean(key, asBool(scope, value)));
					break;
				case IType.STRING:
					gp.init(() -> store.get(key, toJavaString(asString(scope, value))));
					break;
				case IType.FILE:
					gp.init(() -> new GenericFile(store.get(key, (String) value), false));
					break;
				case IType.COLOR:
					gp.init(() -> GamaColor.getInt(store.getInt(key, asInt(scope, value))));
					break;
				case IType.FONT:
					gp.init(() -> {
						final var font = store.get(key, asString(scope, value));
						if (DEFAULT_FONT.equals(font)) return null;
						return GamaFontType.staticCast(scope, font, false);
					});
					break;
				case IType.DATE:
					gp.init(() -> fromISOString(toJavaString(store.get(key, asString(scope, value)))));
					break;
				default:
					gp.init(() -> store.get(key, asString(scope, value)));
			}
		}
		try {
			store.flush();
		} catch (final BackingStoreException ex) {
			ex.printStackTrace();
		}
		// Adds the preferences to the platform species if it is already created
		final var spec = GamaMetaModel.INSTANCE.getPlatformSpeciesDescription();
		if (spec != null) {
			if (!spec.hasAttribute(key)) {
				spec.addPref(key, gp);
				spec.validate();
			}
		}
		// Registers the preferences in the variable of the scope provider

	}

	public static void writeToStore(final Pref gp) {
		final var key = gp.key;
		final var value = gp.value;
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
				store.put(key, toJavaString((String) value));
				break;
			case IType.FILE:
				store.put(key, ((GamaFile) value).getPath(null));
				break;
			case IType.COLOR:
				final var code = ((GamaColor) value).getRGB();
				store.putInt(key, code);
				break;
			case IType.POINT:
				store.put(key, ((GamaPoint) value).stringValue(null));
				break;
			case IType.FONT:
				store.put(key, value == null ? DEFAULT_FONT : value.toString());
				break;
			case IType.DATE:
				store.put(key, toJavaString(((GamaDate) value).toISOString()));
				break;
			default:
				store.put(key, (String) value);
		}
	}

	public static Map<String, Map<String, List<Pref>>> organizePrefs() {
		final Map<String, Map<String, List<Pref>>> result = GamaMapFactory.create();
		for (final Pref e : prefs.values()) {
			if (e.isHidden()) { continue; }
			final var tab = e.tab;
			var groups = result.get(tab);
			if (groups == null) {
				groups = GamaMapFactory.create();
				result.put(tab, groups);
			}
			final var group = e.group;
			var in_group = groups.get(group);
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
			if (e == null) { continue; }
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
		// First we erase all preferences
		final var store = Preferences.userRoot().node("gama");
		try {
			store.removeNode();
		} catch (BackingStoreException e1) {
			e1.printStackTrace();
		}
		// storeKeys.clear();
		// reloadPreferences(modelValues);
		//
		//
		// for (final String name : modelValues.keySet()) {
		// final Pref e = prefs.get(name);
		// if (e == null) {
		// continue;
		// }
		// modelValues.put(name, e.getInitialValue(null));
		// e.set(e.initial);
		// }

	}

	private static void reloadPreferences(final Map<String, Object> modelValues) {
		final List<Pref> entries = new ArrayList(prefs.values());
		for (final Pref e : entries) {
			register(e);
			modelValues.put(e.key, e.getValue());
		}
	}

	public static void applyPreferencesFrom(final String path, final Map<String, Object> modelValues) {
		// DEBUG.OUT("Apply preferences from " + path);
		try (final var is = new FileInputStream(path);) {
			store.importPreferences(is);
			reloadPreferences(modelValues);
		} catch (final IOException | InvalidPreferencesFormatException e) {
			e.printStackTrace();
		}
	}

	public static void savePreferencesTo(final String path) {
		try (var os = new FileWriter(path)) {
			final var entries = StreamEx.ofValues(prefs).sortedBy(Pref::getName).toList();

			final var read = new StringBuilder(1000);
			final var write = new StringBuilder(1000);
			for (final Pref e : entries) {
				if (e.isHidden() || !e.inGaml()) { continue; }
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
	static Simulations s_ = new Simulations();
	static Displays d_ = new Displays();
	static External ext_ = new External();

}
