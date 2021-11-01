/*******************************************************************************************************
 *
 * GamaPreferences.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.preferences;

import static msi.gama.common.preferences.GamaPreferenceStore.getStore;
import static msi.gama.runtime.PlatformHelper.isLinux;
import static msi.gama.runtime.PlatformHelper.isMac;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.geotools.referencing.CRS;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.IPreferenceChangeListener.IPreferenceBeforeChangeListener;
import msi.gama.common.preferences.Pref.ValueProvider;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.file.GenericFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.kernel.GamaMetaModel;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.IType;
import one.util.streamex.StreamEx;

/**
 * Class GamaPreferences.
 *
 * @author drogoul
 * @since 26 août 2013
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaPreferences {

	/** The Constant GENERATOR_NAMES. */
	public static final List<String> GENERATOR_NAMES =
			Arrays.asList(IKeyword.CELLULAR, IKeyword.JAVA, IKeyword.MERSENNE);

	/** The Constant BASIC_COLORS. */
	public static final ValueProvider<GamaColor>[] BASIC_COLORS = new ValueProvider[] {
			() -> new GamaColor(74, 97, 144), () -> new GamaColor(66, 119, 42), () -> new GamaColor(83, 95, 107),
			() -> new GamaColor(195, 98, 43), () -> new GamaColor(150, 132, 106) };

	/**
	 *
	 * Interface tab
	 *
	 */
	public static class Interface {

		/** The Constant NAME. */
		public static final String NAME = "Interface";
		/**
		 * Startup
		 */
		public static final String STARTUP = "Startup";

		/** The Constant CORE_SHOW_PAGE. */
		public static final Pref<Boolean> CORE_SHOW_PAGE =
				create("pref_show_welcome_page", "Display welcome page", true, IType.BOOL, false).in(NAME, STARTUP);

		/** The Constant CORE_SHOW_MAXIMIZED. */
		public static final Pref<Boolean> CORE_SHOW_MAXIMIZED =
				create("pref_show_maximized", "Maximize GAMA window", true, IType.BOOL, false).in(NAME, STARTUP);

		/** The Constant CORE_ASK_REBUILD. */
		public static final Pref<Boolean> CORE_ASK_REBUILD =
				create("pref_ask_rebuild", "Ask before rebuilding a corrupted workspace", true, IType.BOOL, false)
						.in(NAME, STARTUP);

		/** The Constant CORE_ASK_OUTDATED. */
		public static final Pref<Boolean> CORE_ASK_OUTDATED = create("pref_ask_outdated",
				"Ask before using a workspace created by another version", true, IType.BOOL, false).in(NAME, STARTUP);
		/**
		 * Menus
		 */
		public static final String MENUS = "Menus";

		/** The Constant CORE_MENU_SIZE. */
		public static final Pref<Integer> CORE_MENU_SIZE =
				create("pref_menu_size", "Break down agents in menus every", 50, IType.INT, false).between(10, 1000)
						.in(NAME, MENUS);
		/**
		 * Console
		 */
		public static final String CONSOLE = "Console";

		/** The Constant CORE_CONSOLE_SIZE. */
		public static final Pref<Integer> CORE_CONSOLE_SIZE = create("pref_console_size",
				"Max. number of characters to display (-1 = unlimited)", 20000, IType.INT, true).in(NAME, CONSOLE);

		/** The Constant CORE_CONSOLE_BUFFER. */
		public static final Pref<Integer> CORE_CONSOLE_BUFFER =
				create("pref_console_buffer", "Max. number of characters to keep when paused (-1 = unlimited)", 20000,
						IType.INT, true).in(NAME, CONSOLE);

		/** The Constant CORE_CONSOLE_WRAP. */
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

		/** The Constant CORE_SIMULATION_NAME. */
		public static final Pref<Boolean> CORE_SIMULATION_NAME = create("pref_append_simulation_name",
				"Append the name of simulations to their outputs", false, IType.BOOL, true).in(NAME, SIMULATIONS);

		/** The Constant SIMULATION_COLORS. */
		public static final Pref<GamaColor>[] SIMULATION_COLORS = new Pref[5];

		/** The keep navigator state. */
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

		/** The Constant NAME. */
		public static final String NAME = "Editors";

		/** The Constant TEXT. */
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

		/** The Constant INFO_ENABLED. */
		public static final Pref<Boolean> INFO_ENABLED =
				create("pref_editor_enable_infos", "Show information markers in the editor", true, IType.BOOL, false)
						.in(NAME, OPTIONS);

		/** The Constant EDITOR_PERSPECTIVE_SAVE. */
		public static final Pref<Boolean> EDITOR_PERSPECTIVE_SAVE =
				create("pref_editor_perspective_save", "Save all editors when switching perspectives", true, IType.BOOL,
						false).in(Modeling.NAME, Modeling.OPTIONS).activates("pref_editor_ask_save");

		/** The Constant EDITOR_PERSPECTIVE_HIDE. */
		public static final Pref<Boolean> EDITOR_PERSPECTIVE_HIDE = create("pref_editor_perspective_hide",
				"Hide editors when switching to simulation perspectives (can be overriden in the 'layout' statement)",
				true, IType.BOOL, false).in(Modeling.NAME, Modeling.OPTIONS);

		/** The operators menu sort. */
		public static Pref<String> OPERATORS_MENU_SORT = GamaPreferences
				.create("pref_menu_operators_sort", "Sort operators menu by", "Category", IType.STRING, false)
				.among("Name", "Category").in(Interface.NAME, Interface.MENUS);

		/** The Constant CORE_CLOSE_QUOTE. */
		public static final Pref<Boolean> CORE_CLOSE_QUOTE = GamaPreferences
				.create("pref_editor_close_quote", "Automatically close single quotes — '..'", true, IType.BOOL, false)
				.in(NAME, TEXT);

		/** The Constant CORE_CLOSE_DOUBLE. */
		public static final Pref<Boolean> CORE_CLOSE_DOUBLE = GamaPreferences.create("pref_editor_close_double",
				"Automatically close double quotes — \"..\"", true, IType.BOOL, false).in(NAME, TEXT);

		/** The Constant CORE_CLOSE_CURLY. */
		public static final Pref<Boolean> CORE_CLOSE_CURLY = GamaPreferences
				.create("pref_editor_close_curly", "Automatically close curly brackets — {..}", true, IType.BOOL, false)
				.in(NAME, TEXT);

		/** The Constant CORE_CLOSE_SQUARE. */
		public static final Pref<Boolean> CORE_CLOSE_SQUARE = GamaPreferences.create("pref_editor_close_square",
				"Automatically close square brackets — [..]", true, IType.BOOL, false).in(NAME, TEXT);

		/** The Constant CORE_CLOSE_PARENTHESES. */
		public static final Pref<Boolean> CORE_CLOSE_PARENTHESES =
				GamaPreferences.create("pref_editor_close_parentheses", "Automatically close parentheses — (..)", true,
						IType.BOOL, false).in(NAME, TEXT);

		/** The Constant EDITOR_CLEAN_UP. */
		public static final Pref<Boolean> EDITOR_CLEAN_UP =
				GamaPreferences.create("pref_editor_save_format", "Apply formatting on save", false, IType.BOOL, false)
						.in(NAME, GamaPreferences.Modeling.OPTIONS);

		/** The Constant EDITOR_SAVE. */
		public static final Pref<Boolean> EDITOR_SAVE =
				GamaPreferences
						.create("pref_editor_save_all", "Save all editors before lauching an experiment", true,
								IType.BOOL, false)
						.in(NAME, GamaPreferences.Modeling.OPTIONS).activates("pref_editor_ask_save");

		/** The Constant EDITOR_DRAG_RESOURCES. */
		public static final Pref<Boolean> EDITOR_DRAG_RESOURCES = GamaPreferences.create("pref_editor_drag_resources",
				"Drag files and resources as references in GAML files", true, IType.BOOL, false).in(NAME, OPTIONS);

		/** The Constant EDITOR_SAVE_ASK. */
		public static final Pref<Boolean> EDITOR_SAVE_ASK =
				GamaPreferences.create("pref_editor_ask_save", "Ask before saving each file", false, IType.BOOL, false)
						.in(NAME, OPTIONS);

		/** The Constant EDITBOX_ENABLED. */
		public static final Pref<Boolean> EDITBOX_ENABLED = GamaPreferences
				.create("pref_editor_editbox_on", "Turn on colorization of code sections", false, IType.BOOL, false)
				.in(NAME, TEXT);

		/** The Constant EDITOR_BASE_FONT. */
		public static final Pref<GamaFont> EDITOR_BASE_FONT = GamaPreferences
				.create("pref_editor_font", "Font of editors", (GamaFont) null, IType.FONT, false).in(NAME, TEXT);

		/** The Constant EDITOR_BACKGROUND_COLOR. */
		public static final Pref<GamaColor> EDITOR_BACKGROUND_COLOR =
				GamaPreferences.create("pref_editor_background_color", "Background color of editors", (GamaColor) null,
						IType.COLOR, false).in(NAME, TEXT);

		/** The Constant EDITOR_MARK_OCCURRENCES. */
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

		/** The Constant NAME. */
		public static final String NAME = "Simulations";

	}

	/**
	 *
	 * Runtime tab
	 *
	 */
	public static class Runtime {

		/** The Constant NAME. */
		public static final String NAME = "Execution";

		/**
		 * General
		 */
		/**
		 * Running experiments
		 */
		public static final String EXECUTION = "Experiments";

		/** The Constant CORE_AUTO_RUN. */
		public static final Pref<Boolean> CORE_AUTO_RUN = create("pref_experiment_auto_run",
				"Auto-run experiments when they are launched", false, IType.BOOL, true).in(NAME, EXECUTION);

		/** The Constant CORE_ASK_CLOSING. */
		public static final Pref<Boolean> CORE_ASK_CLOSING =
				create("pref_experiment_ask_closing", "Ask to close the previous experiment when launching a new one",
						true, IType.BOOL, true).in(NAME, EXECUTION);

		/** The Constant CORE_ASK_FULLSCREEN. */
		public static final Pref<Boolean> CORE_ASK_FULLSCREEN =
				create("pref_experiment_ask_fullscreen", "Ask before entering fullscreen mode", false, IType.BOOL, true)
						.in(NAME, EXECUTION);
		// public static final Pref<Double> CORE_DELAY_STEP = create("pref_experiment_default_step",
		/** The Constant CORE_SYNC. */
		// "Default step for the delay slider (in sec.)", 0.001, IType.FLOAT, true).in(NAME, EXECUTION).disabled();
		public static final Pref<Boolean> CORE_SYNC =
				create("pref_display_synchronized", "Synchronize outputs with the simulation", false, IType.BOOL, true)
						.in(NAME, EXECUTION);

		/** The Constant CORE_EXPAND_PARAMS. */
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

		/** The Constant TESTS_SORTED. */
		public static final Pref<Boolean> TESTS_SORTED =
				create("pref_tests_sorted", "Sorts the results of tests by severity", false, IType.BOOL, false)
						.in(NAME, TESTS).withComment(", if true, aborted and failed tests are displayed first");
		// public static final Pref<Boolean> RUN_TESTS =
		// create("pref_run_tests", "Run tests after each update of the platform", false, IType.BOOL, false)
		/** The Constant START_TESTS. */
		// .in(NAME, TESTS).disabled().hidden();
		public static final Pref<Boolean> START_TESTS =
				create("pref_start_tests", "Run tests at each start of the platform", false, IType.BOOL, false).in(NAME,
						TESTS);

		/** The Constant USER_TESTS. */
		public static final Pref<Boolean> USER_TESTS =
				create("pref_user_tests", "Include user-defined tests in the tests suite", false, IType.BOOL, false)
						.in(NAME, TESTS).withComment(", if true, will run user models with 'test' experiments");

		/** The Constant FAILED_TESTS. */
		public static final Pref<Boolean> FAILED_TESTS = create("pref_failed_tests",
				"Only display (in the UI and in headless runs) failed and aborted tests", false, IType.BOOL, true)
						.in(NAME, TESTS).withComment(", if true, only aborted and failed tests are displayed");

		/** The Constant MEMORY. */
		public static final String MEMORY = "Memory";

		/** The Constant CORE_MEMORY_POLLING. */
		public static final Pref<Boolean> CORE_MEMORY_POLLING =
				create("pref_check_memory", "Monitor memory and emit a warning if it is low", true, IType.BOOL, true)
						.in(NAME, MEMORY).activates("pref_memory_threshold", "pref_memory_frequency");

		/** The Constant CORE_MEMORY_PERCENTAGE. */
		public static final Pref<Integer> CORE_MEMORY_PERCENTAGE =
				create("pref_memory_threshold", "Trigger warnings when the percentage of available memory is below", 20,
						IType.INT, true).in(NAME, MEMORY);

		/** The Constant CORE_MEMORY_FREQUENCY. */
		public static final Pref<Integer> CORE_MEMORY_FREQUENCY = create("pref_memory_frequency",
				"Interval (in seconds) at which memory should be monitored", 2, IType.INT, true).in(NAME, MEMORY);

		/** The Constant CORE_MEMORY_ACTION. */
		public static final Pref<Boolean> CORE_MEMORY_ACTION = create("pref_memory_action",
				"If true, when running out of memory, GAMA will try to close the experiment, otherwise it exits", true,
				IType.BOOL, true).in(NAME, MEMORY);
		/**
		 * Errors & warnings
		 */
		public static final String ERRORS = "Runtime errors";

		/** The Constant CORE_SHOW_ERRORS. */
		public static final Pref<Boolean> CORE_SHOW_ERRORS =
				create("pref_errors_display", "Show execution errors", true, IType.BOOL, true).in(NAME, ERRORS)
						.activates("pref_errors_number", "pref_errors_recent_first", "pref_display_show_errors");

		/** The Constant ERRORS_IN_DISPLAYS. */
		public static final Pref<Boolean> ERRORS_IN_DISPLAYS = create("pref_display_show_errors",
				"Show errors thrown in displays and outputs", false, IType.BOOL, true).in(NAME, ERRORS);

		/** The Constant CORE_ERRORS_NUMBER. */
		public static final Pref<Integer> CORE_ERRORS_NUMBER =
				create("pref_errors_number", "Number of errors to display", 10, IType.INT, true).in(NAME, ERRORS)
						.between(1, null);

		/** The Constant CORE_RECENT. */
		public static final Pref<Boolean> CORE_RECENT =
				create("pref_errors_recent_first", "Display most recent first", true, IType.BOOL, true).in(NAME,
						ERRORS);

		/** The Constant CORE_REVEAL_AND_STOP. */
		public static final Pref<Boolean> CORE_REVEAL_AND_STOP =
				create("pref_errors_stop", "Stop simulation at first error", true, IType.BOOL, true).in(NAME, ERRORS);

		/** The Constant CORE_WARNINGS. */
		public static final Pref<Boolean> CORE_WARNINGS =
				create("pref_errors_warnings_errors", "Treat warnings as errors", false, IType.BOOL, true).in(NAME,
						ERRORS);

		/** The Constant CORE_ERRORS_EDITOR_LINK. */
		public static final Pref<Boolean> CORE_ERRORS_EDITOR_LINK = create("pref_errors_in_editor",
				"Automatically open an editor and point at the faulty part of the model if an error or a warning is thrown",
				true, IType.BOOL, true).in(NAME, ERRORS);

	}

	/**
	 * The Class Displays.
	 */
	public static class Displays {

		/** The Constant NAME. */
		public static final String NAME = "Displays";

		/** The Constant PRESENTATION. */
		public static final String PRESENTATION = "Presentation and Behavior of Graphical Display Views";
		/**
		 * Presentation
		 */
		public static final List<String> LAYOUTS = Arrays.asList("None", "Stacked", "Split", "Horizontal", "Vertical");

		/** The Constant CORE_DISPLAY_LAYOUT. */
		public static final Pref<String> CORE_DISPLAY_LAYOUT =
				create("pref_display_view_layout", "Default layout of display views", "None", IType.STRING, true)
						.among(LAYOUTS.toArray(new String[LAYOUTS.size()])).in(NAME, PRESENTATION);
		// public static final Pref<Boolean> CORE_DISPLAY_ORDER = create("pref_display_same_order",
		/** The Constant CORE_DISPLAY_BORDER. */
		// "Stack displays in the order defined in the model", true, IType.BOOL, true).in(NAME, PRESENTATION);
		public static final Pref<Boolean> CORE_DISPLAY_BORDER =
				create("pref_display_show_border", "Display a border around display views", false, IType.BOOL, true)
						.in(NAME, PRESENTATION);

		/** The Constant CORE_DISPLAY_PERSPECTIVE. */
		public static final Pref<Boolean> CORE_DISPLAY_PERSPECTIVE =
				create("pref_display_continue_drawing", "Continue to draw displays when in Modeling perspective", false,
						IType.BOOL, true).in(NAME, PRESENTATION);

		/** The Constant DISPLAY_FAST_SNAPSHOT. */
		public static final Pref<Boolean> DISPLAY_FAST_SNAPSHOT = create("pref_display_fast_snapshot",
				"Enable fast snapshots (uncomplete when the display is obscured by others but much faster)", false,
				IType.BOOL, true).in(NAME, PRESENTATION);

		/** The Constant CORE_DISPLAY_TOOLBAR. */
		public static final Pref<Boolean> CORE_DISPLAY_TOOLBAR =
				create("pref_display_show_toolbar", "Show the display top toolbar", true, IType.BOOL, true).in(NAME,
						PRESENTATION);

		/** The Constant CORE_OVERLAY. */
		public static final Pref<Boolean> CORE_OVERLAY =
				create("pref_display_show_overlay", "Show the display bottom overlay", false, IType.BOOL, true).in(NAME,
						PRESENTATION);

		/**
		 * Charts
		 */
		public static final String CHARTS = "Charts Preferences";

		/** The Constant CHART_FLAT. */
		public static final Pref<Boolean> CHART_FLAT =
				create("pref_display_flat_charts", "Display 'flat' histograms", true, IType.BOOL, true).in(NAME,
						CHARTS);

		/** The Constant CHART_MEMORIZE. */
		public static final Pref<Boolean> CHART_MEMORIZE = create("pref_display_memorize_charts",
				"Keep values in memory (to save them as CSV)", true, IType.BOOL, true).in(NAME, CHARTS);

		/** The Constant CHART_GRIDLINES. */
		public static final Pref<Boolean> CHART_GRIDLINES =
				create("pref_chart_display_gridlines", "Display grid lines", true, IType.BOOL, true).in(NAME, CHARTS);
		/**
		 * Drawing methods and defaults
		 */
		public static final String DRAWING = "Default Rendering Properties";

		/** The Constant CORE_DISPLAY. */
		public static final Pref<String> CORE_DISPLAY =
				create("pref_display_default", "Default rendering method (Java2D for 2D, OpenGL for 3D)", "Java2D",
						IType.STRING, true).among("Java2D", "OpenGL").in(NAME, DRAWING);

		/** The Constant CORE_ANTIALIAS. */
		public static final Pref<Boolean> CORE_ANTIALIAS =
				create("pref_display_antialias", "Apply antialiasing", false, IType.BOOL, true).in(NAME, DRAWING);

		/** The Constant CORE_BACKGROUND. */
		public static final Pref<GamaColor> CORE_BACKGROUND =
				create("pref_display_background_color", "Default background color ('background' facet of 'display')",
						() -> GamaColor.getNamed("white"), IType.COLOR, true).in(NAME, DRAWING);

		/** The Constant CORE_HIGHLIGHT. */
		public static final Pref<GamaColor> CORE_HIGHLIGHT = create("pref_display_highlight_color",
				"Default highlight color", () -> new GamaColor(0, 200, 200), IType.COLOR, true).in(NAME, DRAWING);

		/** The Constant CORE_SHAPE. */
		public static final Pref<String> CORE_SHAPE =
				create("pref_display_default_shape", "Defaut shape of agents", "shape", IType.STRING, true)
						.among("shape", "circle", "square", "triangle", "point", "cube", "sphere").in(NAME, DRAWING);

		/** The Constant CORE_SIZE. */
		public static final Pref<Double> CORE_SIZE =
				create("pref_display_default_size", "Default size of agents", 1.0, IType.FLOAT, true)
						.between(0.01, null).in(NAME, DRAWING);

		/** The Constant CORE_COLOR. */
		public static final Pref<GamaColor> CORE_COLOR = create("pref_display_default_color", "Default color of agents",
				() -> GamaColor.getNamed("yellow"), IType.COLOR, true).in(NAME, DRAWING);
		/**
		 * Options
		 */
		public static final String OPTIONS = "Advanced ";

		/** The Constant DISPLAY_ONLY_VISIBLE. */
		public static final Pref<Boolean> DISPLAY_ONLY_VISIBLE = create("pref_display_visible_agents",
				"Only display visible agents (faster, may create visual oddities)", false, IType.BOOL, true).in(NAME,
						OPTIONS);

		/** The Constant DISPLAY_NO_ACCELERATION. */
		public static final Pref<Boolean> DISPLAY_NO_ACCELERATION = create("pref_display_no_java2d_acceleration",
				"Disable acceleration for Java2D (necessary on some configurations)", false, IType.BOOL, true).in(NAME,
						OPTIONS);
		/**
		 * OPENGL
		 */
		public static final String RENDERING = "OpenGL Rendering Properties";

		/** The Constant CORE_DRAW_ENV. */
		public static final Pref<Boolean> CORE_DRAW_ENV =
				create("pref_display_show_referential", "Draw 3D axes", true, IType.BOOL, true).in(NAME, RENDERING);

		/** The Constant DRAW_ROTATE_HELPER. */
		public static final Pref<Boolean> DRAW_ROTATE_HELPER =
				create("pref_display_show_rotation", "Draw rotation axes", true, IType.BOOL, true).in(NAME, RENDERING);

		/** The Constant CORE_LINE_WIDTH. */
		public static final Pref<Double> CORE_LINE_WIDTH = create("pref_display_line_width",
				"Default line width (facet 'width' of 'draw')", 1.2d, IType.FLOAT, true).in(NAME, RENDERING);

		/** The Constant ONLY_VISIBLE_FACES. */
		public static final Pref<Boolean> ONLY_VISIBLE_FACES = create("pref_display_visible_faces",
				"Draw only the 'external' faces of objects", false, IType.BOOL, true).in(NAME, RENDERING).hidden();

		/** The Constant DISPLAY_SLICE_NUMBER. */
		public static final Pref<Integer> DISPLAY_SLICE_NUMBER =
				create("pref_display_slice_number", "Number of slices of circular geometries", 16, IType.INT, true)
						.in(NAME, RENDERING);
		/**
		 * Options
		 */
		public static final Pref<Double> OPENGL_ZOOM =
				create("pref_display_zoom_factor", "Set the zoom factor (0 for slow, 1 fast)", 0.5, IType.FLOAT, true)
						.in(NAME, RENDERING).between(0, 1);

		/** The Constant OPENGL_CAP_FPS. */
		public static final Pref<Boolean> OPENGL_CAP_FPS =
				create("pref_display_cap_fps", "Limit the number of frames per second", false, IType.BOOL, true)
						.in(NAME, RENDERING).activates("pref_display_max_fps");

		/** The Constant OPENGL_FPS. */
		public static final Pref<Integer> OPENGL_FPS =
				create("pref_display_max_fps", "Max. number of frames per second", 60, IType.INT, true).in(NAME,
						RENDERING);

		/** The Constant DISPLAY_POWER_OF_TWO. */
		public static final Pref<Boolean> DISPLAY_POWER_OF_TWO = create("pref_display_power_of_2",
				"Forces textures dimensions to a power of 2 (e.g. 16x16. Necessary on some configurations)", false,
				IType.BOOL, true).in(NAME, RENDERING);

		/** The Constant OPENGL_NUM_KEYS_CAM. */
		public static final Pref<Boolean> OPENGL_NUM_KEYS_CAM = create("pref_display_numkeyscam",
				"Use Numeric Keypad (2,4,6,8) for camera interaction", true, IType.BOOL, true).in(NAME, RENDERING);

		/** The Constant OPENGL_CLIPBOARD_CAM. */
		public static final Pref<Boolean> OPENGL_CLIPBOARD_CAM = create("pref_display_clipboard_cam",
				"Copy the camera definition to the clipboard when it is changed on the display", false, IType.BOOL,
				true).in(NAME, RENDERING);

		/** The Constant OPENGL_USE_IMAGE_CACHE. */
		public static final Pref<Boolean> OPENGL_USE_IMAGE_CACHE = create("pref_display_use_cache",
				"Use GAMA image cache when building textures in OpenGL (potentially faster when running several simulations, but uses more memory)",
				true, IType.BOOL, true).in(NAME, RENDERING);

	}

	/**
	 * The Class External.
	 */
	public static class External {

		/** The Constant NAME. */
		public static final String NAME = "Data and Operators";
		/**
		 * Http connections
		 */
		public static final String HTTP = "Http connections";

		/** The Constant CORE_HTTP_CONNECT_TIMEOUT. */
		public static final Pref<Integer> CORE_HTTP_CONNECT_TIMEOUT =
				create("pref_http_connect_timeout", "Connection timeout (in ms)", 20000, IType.INT, true).in(NAME,
						HTTP);

		/** The Constant CORE_HTTP_READ_TIMEOUT. */
		public static final Pref<Integer> CORE_HTTP_READ_TIMEOUT =
				create("pref_http_read_timeout", "Read timeout (in ms)", 20000, IType.INT, true).in(NAME, HTTP);

		/** The Constant CORE_HTTP_RETRY_NUMBER. */
		public static final Pref<Integer> CORE_HTTP_RETRY_NUMBER = create("pref_http_retry_number",
				"Number of times to retry if connection cannot be established", 3, IType.INT, true).in(NAME, HTTP);

		/** The Constant CORE_HTTP_EMPTY_CACHE. */
		public static final Pref<Boolean> CORE_HTTP_EMPTY_CACHE = create("pref_http_empty_cache",
				"Empty the local cache of files downloaded from the web", true, IType.BOOL, true).in(NAME, HTTP);

		/**
		 * Random numbers
		 */
		public static final String RNG = "Random number generation";

		/** The Constant CORE_RNG. */
		public static final Pref<String> CORE_RNG =
				create("pref_rng_name", "Default random number generator", IKeyword.MERSENNE, IType.STRING, true)
						.among(GENERATOR_NAMES).in(NAME, RNG);

		/** The Constant CORE_SEED_DEFINED. */
		public static final Pref<Boolean> CORE_SEED_DEFINED =
				create("pref_rng_define_seed", "Define a default seed", false, IType.BOOL, true)
						.activates("pref_rng_default_seed").in(NAME, RNG);

		/** The Constant CORE_SEED. */
		public static final Pref<Double> CORE_SEED =
				create("pref_rng_default_seed", "Default seed value (0 is undefined)", 1d, IType.FLOAT, true).in(NAME,
						RNG);

		/** The Constant CORE_RND_EDITABLE. */
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

		/** The Constant CONSTANT_OPTIMIZATION. */
		public static final Pref<Boolean> CONSTANT_OPTIMIZATION = create("pref_optimize_constant_expressions",
				"Optimize constant expressions (experimental, performs a rebuild of models)", false, IType.BOOL, true)
						.in(NAME, OPTIMIZATIONS).onChange(v -> {
							try {
								ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
							} catch (CoreException e) {}
						});

		/** The Constant AGENT_OPTIMIZATION. */
		public static final Pref<Boolean> AGENT_OPTIMIZATION =
				create("pref_optimize_agent_memory", "Optimize agents memory", true, IType.BOOL, true).in(NAME,
						OPTIMIZATIONS);

		/** The Constant AT_DISTANCE_OPTIMIZATION. */
		public static final Pref<Boolean> AT_DISTANCE_OPTIMIZATION =
				create("pref_optimize_at_distance", "Optimize the 'at_distance' operator", true, IType.BOOL, true)
						.in(NAME, OPTIMIZATIONS);

		/** The Constant PATH_COMPUTATION_OPTIMIZATION. */
		public static final Pref<Boolean> PATH_COMPUTATION_OPTIMIZATION = create("pref_optimize_path_computation",
				"Optimize the path computation operators and goto action (but with possible 'jump' issues)", false,
				IType.BOOL, true).in(NAME, OPTIMIZATIONS);

		/** The Constant QUADTREE_OPTIMIZATION. */
		public static final Pref<Boolean> QUADTREE_OPTIMIZATION = create("pref_optimize_quadtree",
				"Optimize spatial queries: add agents only when necessary in the quadtree (still experimental)", false,
				IType.BOOL, true).in(NAME, OPTIMIZATIONS);

		/** The Constant QUADTREE_SYNCHRONIZATION. */
		public static final Pref<Boolean> QUADTREE_SYNCHRONIZATION = create("pref_synchronize_quadtree",
				"Forces the spatial index to synchronize its operations. Useful for interactive models where the user may interfere.",
				true, IType.BOOL, true).in(NAME, OPTIMIZATIONS);

		/** The Constant USE_POOLING. */
		public static final Pref<Boolean> USE_POOLING =
				create("pref_use_pooling", "Use object pooling to reduce memory usage (still experimental)", false,
						IType.BOOL, true).in(NAME, OPTIMIZATIONS);

		/** The Constant TOLERANCE_POINTS. */
		public static final Pref<Double> TOLERANCE_POINTS =
				create("pref_point_tolerance", "Tolerance for the comparison of points", 0.0, IType.FLOAT, true)
						.in(NAME, OPTIMIZATIONS);

		/**
		 * Paths to libraries
		 */
		public static final String PATHS = "External libraries support";

		/** The Constant LIB_SPATIALITE. */
		public static final Pref<? extends IGamaFile> LIB_SPATIALITE =
				create("pref_lib_spatialite", "Path to Spatialite library (http://www.gaia-gis.it/gaia-sins/)",
						() -> new GenericFile("Enter path", false), IType.FILE, true).in(NAME, PATHS);

		/** The Constant jriFile. */
		public static final String jriFile = isMac() ? "libjri.jnilib" : isLinux() ? "libjri.so" : "jri.dll";

		/** The Constant LIB_R. */
		public static final Pref<? extends IGamaFile> LIB_R = create("pref_lib_r",
				"Path to JRI library ($R_HOME/library/rJava/jri/" + jriFile + ") (http://www.r-project.org)",
				() -> new GenericFile(getDefaultRPath(), false), IType.FILE, true).in(NAME, PATHS);
		/**
		 * GeoTools
		 */
		public static final String GEOTOOLS =
				"GIS Coordinate Reference Systems (http://spatialreference.org/ref/epsg/ for EPSG codes)";

		/** The Constant LIB_TARGETED. */
		public static final Pref<Boolean> LIB_TARGETED = create("pref_gis_auto_crs",
				"Let GAMA find which CRS to use to project GIS data", true, IType.BOOL, true).in(NAME, GEOTOOLS);

		/** The Constant LIB_PROJECTED. */
		public static final Pref<Boolean> LIB_PROJECTED = create("pref_gis_same_crs",
				"When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS", true,
				IType.BOOL, true).deactivates("pref_gis_initial_crs").in(NAME, GEOTOOLS);

		/** The Constant LIB_USE_DEFAULT. */
		public static final Pref<Boolean> LIB_USE_DEFAULT =
				create("pref_gis_save_crs", "When no CRS is provided, save the GIS data with the current CRS", true,
						IType.BOOL, true).deactivates("pref_gis_output_crs").in(NAME, GEOTOOLS);

		/** The Constant LIB_TARGET_CRS. */
		public static final Pref<Integer> LIB_TARGET_CRS = create("pref_gis_default_crs",
				"...or use the following EPSG code (the one that will also be used if no projection information is found)",
				32648, IType.INT, true).in(NAME, GEOTOOLS)
						.addChangeListener((IPreferenceBeforeChangeListener<Integer>) newValue -> {
							final var codes = CRS.getSupportedCodes(newValue.toString());
							if (codes.isEmpty()) return false;
							return true;
						});

		/** The Constant LIB_INITIAL_CRS. */
		public static final Pref<Integer> LIB_INITIAL_CRS =
				create("pref_gis_initial_crs", "...or use the following CRS (EPSG code)", 4326, IType.INT, true)
						.in(NAME, GEOTOOLS).addChangeListener((IPreferenceBeforeChangeListener<Integer>) newValue -> {
							final var codes = CRS.getSupportedCodes(newValue.toString());
							if (codes.isEmpty()) return false;
							return true;
						});

		/** The Constant LIB_OUTPUT_CRS. */
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
		/**
		 * Gets the default R path.
		 *
		 * @return the default R path
		 */
		// "C:\\Program Files\\R\\R-2.15.1\\bin\\Rscript.exe"
		private static String getDefaultRPath() {
			if (isMac()) return "/Library/Frameworks/R.framework/Resources/library/rJava/jri/libjri.jnilib";
			if (isLinux()) return "/usr/local/lib/libjri.so";
			return "C:\\Program Files\\R\\R-3.4.0\\library\\rJava\\jri\\jri.dll";
		}
	}

	/** The prefs. */
	private static Map<String, Pref<? extends Object>> prefs = new LinkedHashMap<>();

	/**
	 * Gets the.
	 *
	 * @param <T>
	 *            the generic type
	 * @param key
	 *            the key
	 * @param clazz
	 *            the clazz
	 * @return the pref
	 */
	public static <T> Pref<T> get(final String key, final Class<T> clazz) {
		return (Pref<T>) prefs.get(key);
	}

	/**
	 * Gets the.
	 *
	 * @param key
	 *            the key
	 * @return the pref
	 */
	public static Pref<?> get(final String key) {
		return prefs.get(key);
	}

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	public static Map<String, Pref<?>> getAll() { return prefs; }

	/**
	 * Creates the.
	 *
	 * @param <T>
	 *            the generic type
	 * @param key
	 *            the key
	 * @param title
	 *            the title
	 * @param value
	 *            the value
	 * @param type
	 *            the type
	 * @param inGaml
	 *            the in gaml
	 * @return the pref
	 */
	public static <T> Pref<T> create(final String key, final String title, final T value, final int type,
			final boolean inGaml) {
		final var e = new Pref<T>(key, type, inGaml).named(title).in(Interface.NAME, "").init(value);
		register(e);
		return e;
	}

	/**
	 * Lazy create (tries not to compute immediately the value)
	 *
	 */
	public static <T> Pref<T> create(final String key, final String title, final ValueProvider<T> provider,
			final int type, final boolean inGaml) {
		final var e = new Pref<T>(key, type, inGaml).named(title).in(Interface.NAME, "").init(provider);
		register(e);
		return e;
	}

	/**
	 * Register.
	 *
	 * @param gp
	 *            the gp
	 */
	private static void register(final Pref gp) {
		final var key = gp.key;
		if (key == null) return;
		prefs.put(key, gp);
		getStore().register(gp);
		// Adds the preferences to the platform species if it is already created
		final var spec = GamaMetaModel.INSTANCE.getPlatformSpeciesDescription();
		if (spec != null && !spec.hasAttribute(key)) {
			spec.addPref(key, gp);
			spec.validate();
		}
	}

	/**
	 * Organize prefs.
	 *
	 * @return the map
	 */
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

	/**
	 * Sets the new preferences.
	 *
	 * @param modelValues
	 *            the model values
	 */
	public static void setNewPreferences(final Map<String, Object> modelValues) {
		for (final String name : modelValues.keySet()) {
			final Pref e = prefs.get(name);
			if (e == null) { continue; }
			e.set(modelValues.get(name));
			getStore().write(e);
		}
	}

	/**
	 * Revert to default values.
	 *
	 * @param modelValues
	 *            the model values
	 */
	public static void revertToDefaultValues(final Map<String, Object> modelValues) {
		getStore().clear();
	}

	/**
	 * Apply preferences from.
	 *
	 * @param path
	 *            the path
	 * @param modelValues
	 *            the model values
	 */
	public static void applyPreferencesFrom(final String path, final Map<String, Object> modelValues) {
		// DEBUG.OUT("Apply preferences from " + path);
		getStore().loadFromProperties(path);
		final List<Pref> entries = new ArrayList(prefs.values());
		for (final Pref e : entries) {
			register(e);
			modelValues.put(e.key, e.getValue());
		}
	}

	/**
	 * Save preferences to GAML.
	 *
	 * @param path
	 *            the path
	 */
	public static void savePreferencesToGAML(final String path) {
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

	/**
	 * Save preferences to properties.
	 *
	 * @param path
	 *            the path
	 */
	public static void savePreferencesToProperties(final String path) {
		getStore().saveToProperties(path);
	}

	// To force preferences to load

	/** The i. */
	static Interface i_ = new Interface();

	/** The m. */
	static Modeling m_ = new Modeling();

	/** The r. */
	static Runtime r_ = new Runtime();

	/** The s. */
	static Simulations s_ = new Simulations();

	/** The d. */
	static Displays d_ = new Displays();

	/** The ext. */
	static External ext_ = new External();

}
