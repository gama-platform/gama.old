/*********************************************************************************************
 *
 *
 * 'IGamaIcons.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.resources;

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

	public static final GamaIcon PERSPECTIVE_MODELING = GamaIcons.create("perspective.modeling2");
	public static final GamaIcon PERSPECTIVE_SIMULATION = GamaIcons.create("perspective.simulation2");

	// Views

	public static final GamaIcon VIEW_DISPLAY = GamaIcons.create("view.display2");
	public static final GamaIcon VIEW_BROWSER = GamaIcons.create("view.browser2");
	public static final GamaIcon VIEW_INSPECTOR = GamaIcons.create("view.inspector2");
	public static final GamaIcon VIEW_NAVIGATOR = GamaIcons.create("view.navigator2");
	public static final GamaIcon VIEW_PARAMETERS = GamaIcons.create("view.parameters2");
	public static final GamaIcon VIEW_CONSOLE = GamaIcons.create("view.console2");
	public static final GamaIcon VIEW_MONITORS = GamaIcons.create("view.monitors2");
	public static final GamaIcon VIEW_PANEL = GamaIcons.create("view.panel2");
	public static final GamaIcon VIEW_ERRORS = GamaIcons.create("view.errors2");
	public static final GamaIcon VIEW_PREFERENCES = GamaIcons.create("view.preferences2");
	public static final GamaIcon VIEW_REPOSITORIES = GamaIcons.create("view.repositories2");

	// Display toolbar

	public static final GamaIcon DISPLAY_TOOLBAR_PAUSE = GamaIcons.create("display.pause2");
	public static final GamaIcon DISPLAY_TOOLBAR_KNOB = GamaIcons.create("display.knob2");
	public static final GamaIcon DISPLAY_TOOLBAR_SLIDER = GamaIcons.create("display.slider2");
	public static final GamaIcon DISPLAY_TOOLBAR_SYNC = GamaIcons.create("display.sync2");
	public static final GamaIcon DISPLAY_TOOLBAR_SNAPSHOT = GamaIcons.create("display.snapshot2");
	public static final GamaIcon DISPLAY_TOOLBAR_ZOOMIN = GamaIcons.create("display.zoomin2");
	public static final GamaIcon DISPLAY_TOOLBAR_CSVEXPORT = GamaIcons.create("menu.saveas2");
	public static final GamaIcon DISPLAY_TOOLBAR_ZOOMOUT = GamaIcons.create("display.zoomout2");
	public static final GamaIcon DISPLAY_TOOLBAR_ZOOMFIT = GamaIcons.create("display.zoomfit2");
	public static final GamaIcon DISPLAY_TOOLBAR_AGENTS = GamaIcons.create("display.agents2");
	public static final GamaIcon DISPLAY_TOOLBAR_OPENGL = GamaIcons.create("display.opengl2");

	public static final GamaIcon DISPLAY_TOOLBAR_PAUSE_HOVER = GamaIcons.create("display.pause3");
	public static final GamaIcon DISPLAY_TOOLBAR_KNOB_HOVER = GamaIcons.create("display.knob3");
	public static final GamaIcon DISPLAY_TOOLBAR_SYNC_HOVER = GamaIcons.create("display.sync3");
	public static final GamaIcon DISPLAY_TOOLBAR_SNAPSHOT_HOVER = GamaIcons.create("display.snapshot3");
	public static final GamaIcon DISPLAY_TOOLBAR_ZOOMIN_HOVER = GamaIcons.create("display.zoomin3");
	public static final GamaIcon DISPLAY_TOOLBAR_ZOOMOUT_HOVER = GamaIcons.create("display.zoomout3");
	public static final GamaIcon DISPLAY_TOOLBAR_ZOOMFIT_HOVER = GamaIcons.create("display.zoomfit3");
	public static final GamaIcon DISPLAY_TOOLBAR_AGENTS_HOVER = GamaIcons.create("display.agents3");
	public static final GamaIcon DISPLAY_TOOLBAR_OPENGL_HOVER = GamaIcons.create("display.opengl3");

	public static final GamaIcon DISPLAY_TOOLBAR_CAMERA = GamaIcons.create("display.camera2");
	public static final GamaIcon DISPLAY_TOOLBAR_ROTATE = GamaIcons.create("display.rotate2");
	public static final GamaIcon DISPLAY_TOOLBAR_SPLIT = GamaIcons.create("display.split2");
	public static final GamaIcon DISPLAY_TOOLBAR_TRIANGULATE = GamaIcons.create("display.triangulate2");
	public static final GamaIcon DISPLAY_TOOLBAR_DRAG = GamaIcons.create("display.drag2");
	public static final GamaIcon DISPLAY_TOOLBAR_OVERLAY = GamaIcons.create("display.overlay2");
	public static final GamaIcon DISPLAY_TOOLBAR_SIDEBAR = GamaIcons.create("display.sidebar2");

	// Menus

	public static final GamaIcon MENU_BROWSE = GamaIcons.create("menu.browse2");;
	public static final GamaIcon MENU_POPULATION = DISPLAY_TOOLBAR_AGENTS;
	public static final GamaIcon MENU_AGENT = GamaIcons.create("menu.agent2");
	public static final GamaIcon MENU_INSPECT = GamaIcons.create("menu.inspect2");;
	public static final GamaIcon MENU_HIGHLIGHT = GamaIcons.create("menu.highlight2");
	public static final GamaIcon MENU_KILL = GamaIcons.create("menu.kill2");
	public static final GamaIcon MENU_FOCUS = GamaIcons.create("menu.focus2");
	public static final GamaIcon MENU_FOLLOW = GamaIcons.create("menu.follow2");
	public static final GamaIcon MENU_PREFERENCES = GamaIcons.create("menu.preferences2");
	public static final GamaIcon MENU_PARAMETERS = VIEW_PARAMETERS;
	public static final GamaIcon MENU_CONSOLE = VIEW_CONSOLE;
	public static final GamaIcon MENU_ADD_MONITOR = GamaIcons.create("menu.monitor2");
	public static final GamaIcon MENU_MODELING_PERSPECTIVE = GamaIcons.create("menu.modeling2");
	public static final GamaIcon MENU_SIMULATION_PERSPECTIVE = GamaIcons.create("menu.simulation2");
	public static final GamaIcon MENU_RUN_ACTION = GamaIcons.create("menu.action2");
	public static final GamaIcon MENU_RUN = GamaIcons.create("menu.run4");
	// public static final GamaIcon MENU_PAUSE = GamaIcons.create("menu.pause2");
	// public static final GamaIcon MENU_STEP = GamaIcons.create("menu.step2");
	// public static final GamaIcon MENU_RELOAD = GamaIcons.create("menu.reload2");
	// public static final GamaIcon MENU_ERROR_STOP = GamaIcons.create("toolbar.stop2");
	// public static final GamaIcon MENU_ERROR_WARNING = GamaIcons.create("menu.warning2");
	// public static final GamaIcon MENU_ERROR_DISPLAY = GamaIcons.create("menu.show2");

	// Layers

	public static final GamaIcon LAYER_GRID = GamaIcons.create("layer.grid2");
	public static final GamaIcon LAYER_SPECIES = GamaIcons.create("layer.species2");
	public static final GamaIcon LAYER_AGENTS = GamaIcons.create("layer.agents2");
	public static final GamaIcon LAYER_GRAPHICS = GamaIcons.create("layer.graphics2");
	public static final GamaIcon LAYER_TEXT = GamaIcons.create("layer.text2");
	public static final GamaIcon LAYER_IMAGE = GamaIcons.create("layer.image2");
	public static final GamaIcon LAYER_SHAPEFILE = GamaIcons.create("layer.shapefile2");
	public static final GamaIcon LAYER_CHART = GamaIcons.create("layer.chart2");

	// Actions

	public static final GamaIcon ACTION_PAUSE = DISPLAY_TOOLBAR_PAUSE;
	public static final GamaIcon ACTION_REVERT = GamaIcons.create("action.revert2");
	public static final GamaIcon ACTION_SAVE = GamaIcons.create("action.save2");
	public static final GamaIcon ACTION_CLEAR = GamaIcons.create("action.clear2");
	public static final GamaIcon ACTION_ADD_MONITOR = MENU_ADD_MONITOR;

	// General toolbar

	public static final GamaIcon TOOLBAR_PREFERENCES = MENU_PREFERENCES;
	public static final GamaIcon TOOLBAR_MODELING_PERSPECTIVE = MENU_MODELING_PERSPECTIVE;
	public static final GamaIcon TOOLBAR_SIMULATION_PERSPECTIVE = MENU_SIMULATION_PERSPECTIVE;
	public static final GamaIcon TOOLBAR_RUN = MENU_RUN;
	// public static final GamaIcon TOOLBAR_PAUSE = MENU_PAUSE;
	// public static final GamaIcon TOOLBAR_STEP = MENU_STEP;
	// public static final GamaIcon TOOLBAR_RELOAD = MENU_RELOAD;
	// public static final GamaIcon TOOLBAR_STOP = MENU_ERROR_STOP;
	public static final GamaIcon TOOLBAR_KNOB = GamaIcons.create("toolbar.knob4");
	public static final GamaIcon TOOLBAR_KNOB_HOVER = GamaIcons.create("toolbar.knob5");
	public static final GamaIcon TOOLBAR_SLIDER = GamaIcons.create("toolbar.slider2");

	// User Panels

	public static final GamaIcon PANEL_CONTINUE = GamaIcons.create("panel.continue2");
	public static final GamaIcon PANEL_INSPECT = MENU_INSPECT;
	public static final GamaIcon PANEL_LOCATE = GamaIcons.create("panel.locate2");
	public static final GamaIcon PANEL_ACTION = MENU_RUN_ACTION;
	public static final GamaIcon PANEL_GOTO = GamaIcons.create("panel.goto2");

	// Preferences tabs. 24x24

	public static final GamaIcon PREFS_GENERAL = GamaIcons.create("prefs.general2");
	public static final GamaIcon PREFS_DISPLAY = GamaIcons.create("prefs.display2");
	public static final GamaIcon PREFS_EDITOR = GamaIcons.create("prefs.editor2");
	public static final GamaIcon PREFS_CODE = GamaIcons.create("prefs.code2");
	public static final GamaIcon PREFS_WORKSPACE = GamaIcons.create("prefs.workspace2");
	public static final GamaIcon PREFS_OTHER = GamaIcons.create("prefs.other2");
	public static final GamaIcon PREFS_LIBS = GamaIcons.create("prefs.libraries2");

	// Navigator

	public static final GamaIcon FOLDER_SHARED = GamaIcons.create(/* "folder.shared2" */"navigator/folder.shared");
	public static final GamaIcon FOLDER_BUILTIN = GamaIcons.create(/* "folder.builtin2" */"navigator/folder.library2");
	public static final GamaIcon FOLDER_BUILTIN_16 = GamaIcons.create("navigator/folder.library2.16");
	public static final GamaIcon FOLDER_PLUGIN = GamaIcons.create("navigator/folder.plugin2");
	public static final GamaIcon FOLDER_PLUGIN_16 = GamaIcons.create("navigator/folder.plugin2.16");
	public static final GamaIcon FOLDER_PROJECT = GamaIcons.create("folder.user2");
	public static final GamaIcon FOLDER_CLOSED = GamaIcons.create("folder.closed2");
	public static final GamaIcon FOLDER_MODEL = GamaIcons.create("folder.model3");
	public static final GamaIcon FOLDER_RESOURCES = GamaIcons.create("folder.resources2");
	public static final GamaIcon FILE_ICON = GamaIcons.create("file.icon2");
	public static final GamaIcon FOLDER_USER = GamaIcons.create(/* "folder.icon2" */"navigator/folder.user");
	public static final GamaIcon FOLDER_USER_16 = GamaIcons.create(/* "folder.icon2" */"navigator/folder.user.16");
	public static final GamaIcon NAVIGATOR_RUN = GamaIcons.create("navigator/navigator.run2");

	// Buttons 16x16

	public static final GamaIcon BUTTON_EDIT = GamaIcons.create("button.edit2");
	public static final GamaIcon BUTTON_INSPECT = MENU_INSPECT;
	public static final GamaIcon BUTTON_BROWSE = MENU_BROWSE;

	// Editor specific

	public static final GamaIcon BUTTON_EDITBOX = GamaIcons.create("editor.editbox2");
	public static final GamaIcon BUTTON_GUI = GamaIcons.create("small.run");
	public static final GamaIcon BUTTON_BATCH = GamaIcons.create("small.batch");

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

	public static final GamaIcon OVERLAY_OK = GamaIcons.create("overlay.ok2");

	// Wizard

	public static final GamaIcon GAMA_ICON = GamaIcons.create("launcher_icons/icon205");

	// Browser
	public static final GamaIcon BROWSER_BACK = GamaIcons.create("browser.back2");
	public static final GamaIcon BROWSER_FORWARD = GamaIcons.create("browser.forward2");
	public static final GamaIcon BROWSER_REFRESH = GamaIcons.create("browser.refresh2");
	public static final GamaIcon BROWSER_HOME = GamaIcons.create("browser.home2");
	public static final GamaIcon BROWSER_STOP = GamaIcons.create("browser.stop2");
	public static final GamaIcon BROWSER_ICON = GamaIcons.create("browser.icon2");

}
