/**
 * Created by drogoul, 13 sept. 2013
 * 
 */
package msi.gama.gui.swt;

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

	public static final String PERSPECTIVE_MODELING = "perspective.modeling";
	public static final String PERSPECTIVE_SIMULATION = "perspective.simulation";

	// Views

	public static final String VIEW_DISPLAY = "view.display";
	public static final String VIEW_BROWSER = "view.browser";
	public static final String VIEW_INSPECTOR = "view.inspector";
	public static final String VIEW_NAVIGATOR = "view.navigator";
	public static final String VIEW_PARAMETERS = "view.parameters";
	public static final String VIEW_CONSOLE = "view.console";
	public static final String VIEW_MONITORS = "view.monitors";
	public static final String VIEW_PANEL = "view.panel";
	public static final String VIEW_ERRORS = "view.errors";
	public static final String VIEW_PREFERENCES = "view.preferences";
	public static final String VIEW_REPOSITORIES = "view.repositories";

	// Display toolbar

	public static final String DISPLAY_TOOLBAR_PAUSE = "display.pause";
	public static final String DISPLAY_TOOLBAR_KNOB = "display.knob";
	public static final String DISPLAY_TOOLBAR_SLIDER = "display.slider";
	public static final String DISPLAY_TOOLBAR_SYNC = "display.sync";
	public static final String DISPLAY_TOOLBAR_SNAPSHOT = "display.snapshot";
	public static final String DISPLAY_TOOLBAR_ZOOMIN = "display.zoomin";
	public static final String DISPLAY_TOOLBAR_ZOOMOUT = "display.zoomout";
	public static final String DISPLAY_TOOLBAR_ZOOMFIT = "display.zoomfit";
	public static final String DISPLAY_TOOLBAR_AGENTS = "display.agents";
	public static final String DISPLAY_TOOLBAR_OPENGL = "display.opengl";
	public static final String DISPLAY_TOOLBAR_CAMERA = "display.camera";
	public static final String DISPLAY_TOOLBAR_ROTATE = "display.rotate";
	public static final String DISPLAY_TOOLBAR_SPLIT = "display.split";
	public static final String DISPLAY_TOOLBAR_TRIANGULATE = "display.triangulate";
	public static final String DISPLAY_TOOLBAR_DRAG = "display.drag";
	public static final String DISPLAY_TOOLBAR_INERTIA = "display.inertia";
	public static final String DISPLAY_TOOLBAR_OVERLAY = "display.overlay";
	public static final String DISPLAY_TOOLBAR_SIDEBAR = "display.sidebar";

	// Menus

	public static final String MENU_BROWSE = VIEW_BROWSER;
	public static final String MENU_POPULATION = DISPLAY_TOOLBAR_AGENTS;
	public static final String MENU_AGENT = "menu.agent";
	public static final String MENU_INSPECT = VIEW_INSPECTOR;
	public static final String MENU_HIGHLIGHT = "menu.highlight";
	public static final String MENU_FOCUS = "menu.focus";
	public static final String MENU_FOLLOW = "menu.follow";
	public static final String MENU_PREFERENCES = "menu.preferences";
	public static final String MENU_PARAMETERS = VIEW_PARAMETERS;
	public static final String MENU_CONSOLE = VIEW_CONSOLE;
	public static final String MENU_ADD_MONITOR = "menu.monitor";
	public static final String MENU_MODELING_PERSPECTIVE = "menu.modeling";
	public static final String MENU_SIMULATION_PERSPECTIVE = "menu.simulation";
	public static final String MENU_RUN_ACTION = "menu.action";
	public static final String MENU_RUN = "menu.run";
	public static final String MENU_PAUSE = "menu.pause";
	public static final String MENU_STEP = "menu.step";
	public static final String MENU_RELOAD = "menu.reload";
	public static final String MENU_ERROR_STOP = "toolbar.stop";
	public static final String MENU_ERROR_WARNING = "menu.warning";
	public static final String MENU_ERROR_DISPLAY = "menu.show";

	// Layers

	public static final String LAYER_GRID = "layer.grid";
	public static final String LAYER_SPECIES = "layer.species";
	public static final String LAYER_AGENTS = "layer.agents";
	public static final String LAYER_GRAPHICS = "layer.graphics";
	public static final String LAYER_TEXT = "layer.text";
	public static final String LAYER_IMAGE = "layer.image";
	public static final String LAYER_SHAPEFILE = "layer.shapefile";
	public static final String LAYER_CHART = "layer.chart";

	// Actions

	public static final String ACTION_PAUSE = DISPLAY_TOOLBAR_PAUSE;
	public static final String ACTION_REVERT = "action.revert";
	public static final String ACTION_SAVE = "action.save";
	public static final String ACTION_CLEAR = "action.clear";
	public static final String ACTION_ADD_MONITOR = MENU_ADD_MONITOR;

	// General toolbar

	public static final String TOOLBAR_PREFERENCES = MENU_PREFERENCES;
	public static final String TOOLBAR_MODELING_PERSPECTIVE = MENU_MODELING_PERSPECTIVE;
	public static final String TOOLBAR_SIMULATION_PERSPECTIVE = MENU_SIMULATION_PERSPECTIVE;
	public static final String TOOLBAR_RUN = MENU_RUN;
	public static final String TOOLBAR_PAUSE = MENU_PAUSE;
	public static final String TOOLBAR_STEP = MENU_STEP;
	public static final String TOOLBAR_RELOAD = MENU_RELOAD;
	public static final String TOOLBAR_STOP = MENU_ERROR_STOP;
	public static final String TOOLBAR_KNOB = "toolbar.knob";
	public static final String TOOLBAR_SLIDER = "toolbar.slider";

	// User Panels

	public static final String PANEL_CONTINUE = "panel.continue";
	public static final String PANEL_INSPECT = MENU_INSPECT;
	public static final String PANEL_LOCATE = "panel.locate";
	public static final String PANEL_ACTION = MENU_RUN_ACTION;
	public static final String PANEL_GOTO = "panel.goto";

	// Preferences tabs. 24x24

	public static final String PREFS_GENERAL = "prefs.general";
	public static final String PREFS_DISPLAY = "prefs.display";
	public static final String PREFS_EDITOR = "prefs.editor";
	public static final String PREFS_CODE = "prefs.code";
	public static final String PREFS_WORKSPACE = "prefs.workspace";
	public static final String PREFS_OTHER = "prefs.other";
	public static final String PREFS_LIBS = "prefs.libraries";

	// Folders 16x16

	public static final String FOLDER_SHARED = "folder.shared";
	public static final String FOLDER_BUILTIN = "folder.builtin";
	public static final String FOLDER_USER = "folder.user";

	// Buttons 16x16

	public static final String BUTTON_EDIT = "button.edit";
	public static final String BUTTON_INSPECT = MENU_INSPECT;
	public static final String BUTTON_BROWSE = MENU_BROWSE;

	// Small Icons

	public static final String SMALL_LOCK = "small.lock";
	public static final String SMALL_UNLOCK = "small.unlock";
	public static final String SMALL_PLUS = "small.plus";
	public static final String SMALL_MINUS = "small.minus";
	public static final String SMALL_EXPAND = "small.expand";
	public static final String SMALL_COLLAPSE = "small.collapse";
	public static final String SMALL_PAUSE = "small.pause";
	public static final String SMALL_RESUME = "small.resume";
	public static final String SMALL_CLOSE = "small.close";

	// Overlays

	public static final String OVERLAY_OK = "overlay.ok";

	// Wizard

}
