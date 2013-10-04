/**
 * Created by drogoul, 13 sept. 2013
 * 
 */
package msi.gama.gui.swt;

import msi.gama.gui.swt.GamaIcons.GamaIcon;

/**
 * Class IGamaIcons.
 * 
 * @author drogoul
 * @since 13 sept. 2013
 * 
 */
public interface IGamaIcons {

	// EXTERNALIZE : FINISH THIS WORK !

	// Perspectives

	public static final GamaIcon PERSPECTIVE_MODELING = GamaIcons.create("perspective.modeling");
	public static final GamaIcon PERSPECTIVE_SIMULATION = GamaIcons.create("perspective.simulation");

	// Views

	public static final GamaIcon VIEW_DISPLAY = GamaIcons.create("view.display");
	public static final GamaIcon VIEW_BROWSER = GamaIcons.create("view.browser");
	public static final GamaIcon VIEW_INSPECTOR = GamaIcons.create("view.inspector");
	public static final GamaIcon VIEW_NAVIGATOR = GamaIcons.create("view.navigator");
	public static final GamaIcon VIEW_PARAMETERS = GamaIcons.create("view.parameters");
	public static final GamaIcon VIEW_CONSOLE = GamaIcons.create("view.console");
	public static final GamaIcon VIEW_MONITORS = GamaIcons.create("view.monitors");
	public static final GamaIcon VIEW_PANEL = GamaIcons.create("view.panel");
	public static final GamaIcon VIEW_ERRORS = GamaIcons.create("view.errors");
	public static final GamaIcon VIEW_PREFERENCES = GamaIcons.create("view.preferences");
	public static final GamaIcon VIEW_REPOSITORIES = GamaIcons.create("view.repositories");

	// Display toolbar

	public static final GamaIcon DISPLAY_TOOLBAR_PAUSE = GamaIcons.create("display.pause");
	public static final GamaIcon DISPLAY_TOOLBAR_KNOB = GamaIcons.create("display.knob");
	public static final GamaIcon DISPLAY_TOOLBAR_SLIDER = GamaIcons.create("display.slider");
	public static final GamaIcon DISPLAY_TOOLBAR_SYNC = GamaIcons.create("display.sync");
	public static final GamaIcon DISPLAY_TOOLBAR_SNAPSHOT = GamaIcons.create("display.snapshot");
	public static final GamaIcon DISPLAY_TOOLBAR_ZOOMIN = GamaIcons.create("display.zoomin");
	public static final GamaIcon DISPLAY_TOOLBAR_ZOOMOUT = GamaIcons.create("display.zoomout");
	public static final GamaIcon DISPLAY_TOOLBAR_ZOOMFIT = GamaIcons.create("display.zoomfit");
	public static final GamaIcon DISPLAY_TOOLBAR_AGENTS = GamaIcons.create("display.agents");
	public static final GamaIcon DISPLAY_TOOLBAR_OPENGL = GamaIcons.create("display.opengl");
	public static final GamaIcon DISPLAY_TOOLBAR_CAMERA = GamaIcons.create("display.camera");
	public static final GamaIcon DISPLAY_TOOLBAR_ROTATE = GamaIcons.create("display.rotate");
	public static final GamaIcon DISPLAY_TOOLBAR_SPLIT = GamaIcons.create("display.split");
	public static final GamaIcon DISPLAY_TOOLBAR_TRIANGULATE = GamaIcons.create("display.triangulate");
	public static final GamaIcon DISPLAY_TOOLBAR_DRAG = GamaIcons.create("display.drag");
	public static final GamaIcon DISPLAY_TOOLBAR_INERTIA = GamaIcons.create("display.inertia");
	public static final GamaIcon DISPLAY_TOOLBAR_OVERLAY = GamaIcons.create("display.overlay");
	public static final GamaIcon DISPLAY_TOOLBAR_SIDEBAR = GamaIcons.create("display.sidebar");

	// Menus

	public static final GamaIcon MENU_BROWSE = VIEW_BROWSER;
	public static final GamaIcon MENU_POPULATION = DISPLAY_TOOLBAR_AGENTS;
	public static final GamaIcon MENU_AGENT = GamaIcons.create("menu.agent");
	public static final GamaIcon MENU_INSPECT = VIEW_INSPECTOR;
	public static final GamaIcon MENU_HIGHLIGHT = GamaIcons.create("menu.highlight");
	public static final GamaIcon MENU_FOCUS = GamaIcons.create("menu.focus");
	public static final GamaIcon MENU_FOLLOW = GamaIcons.create("menu.follow");
	public static final GamaIcon MENU_PREFERENCES = GamaIcons.create("menu.preferences");
	public static final GamaIcon MENU_PARAMETERS = VIEW_PARAMETERS;
	public static final GamaIcon MENU_CONSOLE = VIEW_CONSOLE;
	public static final GamaIcon MENU_ADD_MONITOR = GamaIcons.create("menu.monitor");
	public static final GamaIcon MENU_MODELING_PERSPECTIVE = GamaIcons.create("menu.modeling");
	public static final GamaIcon MENU_SIMULATION_PERSPECTIVE = GamaIcons.create("menu.simulation");
	public static final GamaIcon MENU_RUN_ACTION = GamaIcons.create("menu.action");
	public static final GamaIcon MENU_RUN = GamaIcons.create("menu.run");
	public static final GamaIcon MENU_PAUSE = GamaIcons.create("menu.pause");
	public static final GamaIcon MENU_STEP = GamaIcons.create("menu.step");
	public static final GamaIcon MENU_RELOAD = GamaIcons.create("menu.reload");
	public static final GamaIcon MENU_ERROR_STOP = GamaIcons.create("toolbar.stop");
	public static final GamaIcon MENU_ERROR_WARNING = GamaIcons.create("menu.warning");
	public static final GamaIcon MENU_ERROR_DISPLAY = GamaIcons.create("menu.show");

	// Layers

	public static final GamaIcon LAYER_GRID = GamaIcons.create("layer.grid");
	public static final GamaIcon LAYER_SPECIES = GamaIcons.create("layer.species");
	public static final GamaIcon LAYER_AGENTS = GamaIcons.create("layer.agents");
	public static final GamaIcon LAYER_GRAPHICS = GamaIcons.create("layer.graphics");
	public static final GamaIcon LAYER_TEXT = GamaIcons.create("layer.text");
	public static final GamaIcon LAYER_IMAGE = GamaIcons.create("layer.image");
	public static final GamaIcon LAYER_SHAPEFILE = GamaIcons.create("layer.shapefile");
	public static final GamaIcon LAYER_CHART = GamaIcons.create("layer.chart");

	// Actions

	public static final GamaIcon ACTION_PAUSE = DISPLAY_TOOLBAR_PAUSE;
	public static final GamaIcon ACTION_REVERT = GamaIcons.create("action.revert");
	public static final GamaIcon ACTION_SAVE = GamaIcons.create("action.save");
	public static final GamaIcon ACTION_CLEAR = GamaIcons.create("action.clear");
	public static final GamaIcon ACTION_ADD_MONITOR = MENU_ADD_MONITOR;

	// General toolbar

	public static final GamaIcon TOOLBAR_PREFERENCES = MENU_PREFERENCES;
	public static final GamaIcon TOOLBAR_MODELING_PERSPECTIVE = MENU_MODELING_PERSPECTIVE;
	public static final GamaIcon TOOLBAR_SIMULATION_PERSPECTIVE = MENU_SIMULATION_PERSPECTIVE;
	public static final GamaIcon TOOLBAR_RUN = MENU_RUN;
	public static final GamaIcon TOOLBAR_PAUSE = MENU_PAUSE;
	public static final GamaIcon TOOLBAR_STEP = MENU_STEP;
	public static final GamaIcon TOOLBAR_RELOAD = MENU_RELOAD;
	public static final GamaIcon TOOLBAR_STOP = MENU_ERROR_STOP;
	public static final GamaIcon TOOLBAR_KNOB = GamaIcons.create("toolbar.knob");
	public static final GamaIcon TOOLBAR_SLIDER = GamaIcons.create("toolbar.slider");

	// User Panels

	public static final GamaIcon PANEL_CONTINUE = GamaIcons.create("panel.continue");
	public static final GamaIcon PANEL_INSPECT = MENU_INSPECT;
	public static final GamaIcon PANEL_LOCATE = GamaIcons.create("panel.locate");
	public static final GamaIcon PANEL_ACTION = MENU_RUN_ACTION;
	public static final GamaIcon PANEL_GOTO = GamaIcons.create("panel.goto");

	// Preferences tabs. 24x24

	public static final GamaIcon PREFS_GENERAL = GamaIcons.create("prefs.general");
	public static final GamaIcon PREFS_DISPLAY = GamaIcons.create("prefs.display");
	public static final GamaIcon PREFS_EDITOR = GamaIcons.create("prefs.editor");
	public static final GamaIcon PREFS_CODE = GamaIcons.create("prefs.code");
	public static final GamaIcon PREFS_WORKSPACE = GamaIcons.create("prefs.workspace");
	public static final GamaIcon PREFS_OTHER = GamaIcons.create("prefs.other");
	public static final GamaIcon PREFS_LIBS = GamaIcons.create("prefs.libraries");

	// Folders 16x16

	public static final GamaIcon FOLDER_SHARED = GamaIcons.create("folder.shared");
	public static final GamaIcon FOLDER_BUILTIN = GamaIcons.create("folder.builtin");
	public static final GamaIcon FOLDER_USER = GamaIcons.create("folder.user");

	// Buttons 16x16

	public static final GamaIcon BUTTON_EDIT = GamaIcons.create("button.edit");
	public static final GamaIcon BUTTON_INSPECT = MENU_INSPECT;
	public static final GamaIcon BUTTON_BROWSE = MENU_BROWSE;

	// Small Icons

	public static final GamaIcon SMALL_LOCK = GamaIcons.create("small.lock");
	public static final GamaIcon SMALL_UNLOCK = GamaIcons.create("small.unlock");
	public static final GamaIcon SMALL_PLUS = GamaIcons.create("small.plus");
	public static final GamaIcon SMALL_MINUS = GamaIcons.create("small.minus");
	public static final GamaIcon SMALL_EXPAND = GamaIcons.create("small.expand");
	public static final GamaIcon SMALL_COLLAPSE = GamaIcons.create("small.collapse");
	public static final GamaIcon SMALL_PAUSE = GamaIcons.create("small.pause");
	public static final GamaIcon SMALL_RESUME = GamaIcons.create("small.resume");
	public static final GamaIcon SMALL_CLOSE = GamaIcons.create("small.close");
	public static final GamaIcon SMALL_CHAIN = GamaIcons.create("small.chain");
	public static final GamaIcon SMALL_PIN = GamaIcons.create("small.pin");
	public static final GamaIcon SMALL_RUN = GamaIcons.create("small.run");

	// Overlays

	public static final GamaIcon OVERLAY_OK = GamaIcons.create("overlay.ok");

	// Wizard

	public static final GamaIcon GAMA_ICON = GamaIcons.create("launcher_icons/icon205");

}
