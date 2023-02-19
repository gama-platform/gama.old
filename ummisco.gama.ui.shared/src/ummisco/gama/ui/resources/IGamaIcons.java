/*******************************************************************************************************
 *
 * IGamaIcons.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.resources;

/**
 * Class IStrings.
 *
 * @author drogoul
 * @since 13 sept. 2013
 *
 */
public interface IGamaIcons {

	// Display toolbar

	/** The display toolbar pause. */
	String DISPLAY_TOOLBAR_PAUSE = "display/action.pause";

	/** The display update. */
	String DISPLAY_UPDATE = "display/action.update";

	/** The display toolbar sync. */
	String DISPLAY_TOOLBAR_SYNC = "action/experiment.sync";

	/** The display toolbar snapshot. */
	String DISPLAY_TOOLBAR_SNAPSHOT = "display/action.snapshot";

	/** The display fullscreen enter. */
	String DISPLAY_FULLSCREEN_ENTER = "display/fullscreen.enter";

	/** The display fullscreen exit. */
	String DISPLAY_FULLSCREEN_EXIT = "display/fullscreen.exit";

	/** The display toolbar zoomin. */
	String DISPLAY_TOOLBAR_ZOOMIN = "zoom.in";

	/** The display toolbar csvexport. */
	String DISPLAY_TOOLBAR_CSVEXPORT = "generic/menu.saveas";

	/** The display toolbar zoomout. */
	String DISPLAY_TOOLBAR_ZOOMOUT = "zoom.out";

	/** The display toolbar zoomfit. */
	String DISPLAY_TOOLBAR_ZOOMFIT = "zoom.fit";

	/** The display toolbar camera. */
	String DISPLAY_TOOLBAR_CAMERA = "display/camera.full";

	// Menus

	/** The menu browse. */
	String MENU_BROWSE = "menu.browse2";

	/** The menu population. */
	String MENU_POPULATION = "menus/agents.submenu";

	/** The menu agent. */
	String MENU_AGENT = "menu.agent2";

	/** The menu inspect. */
	String MENU_INSPECT = "menu.inspect2";

	/** The menu highlight. */
	String MENU_HIGHLIGHT = "menu.highlight2";

	/** The menu kill. */
	String MENU_KILL = "menu.kill2";

	/** The menu focus. */
	String MENU_FOCUS = "menu.focus2";

	/** The menu add monitor. */
	String MENU_ADD_MONITOR = "views/open.monitor";

	/** The menu run action. */
	String MENU_RUN_ACTION = "menu.action2";

	/** The menu pause action. */
	String MENU_PAUSE_ACTION = "action/experiment.pause";

	// Layers

	/** The layer grid. */
	String LAYER_GRID = "layer/layer.grid";

	/** The layer species. */
	String LAYER_SPECIES = "layer/layer.species";

	/** The layer agents. */
	String LAYER_AGENTS = "layer/layer.agents";

	/** The layer graphics. */
	String LAYER_GRAPHICS = "layer/layer.graphics";

	/** The layer image. */
	String LAYER_IMAGE = "layer/layer.image";

	/** The layer chart. */
	String LAYER_CHART = "layer/layer.chart";

	// Actions

	/** The action revert. */
	String ACTION_REVERT = "generic/menu.undo";

	/** The action clear. */
	String ACTION_CLEAR = "console.clear";

	// User Panels

	/** The panel continue. */
	String PANEL_CONTINUE = "action/experiment.continue";

	/** The panel inspect. */
	String PANEL_INSPECT = MENU_INSPECT;

	// Preferences tabs. 24x24

	/** The prefs general. */
	String PREFS_GENERAL = "prefs/prefs.general2";

	/** The prefs editor. */
	String PREFS_EDITOR = "prefs/prefs.editor2";

	/** The prefs libs. */
	String PREFS_LIBS = "prefs/prefs.libraries2";

	// Navigator

	/** The folder builtin. */
	String FOLDER_BUILTIN = "navigator/folder.library";

	/** The folder plugin. */
	String FOLDER_PLUGIN = "navigator/folder.plugin";

	/** The folder test. */
	String FOLDER_TEST = "navigator/folder.test";

	/** The folder project. */
	String FOLDER_PROJECT = "navigator/folder.project";

	/** The folder model. */
	String FOLDER_MODEL = "navigator/folder.model";

	/** The folder resources. */
	String FOLDER_RESOURCES = "navigator/folder.resources";

	/** The file icon. */
	String FILE_ICON = "navigator/file.model";

	/** The folder user. */
	String FOLDER_USER = "navigator/folder.user";

	// Editor specific

	/** The button gui. */
	String BUTTON_GUI = "small.exp.run.white";

	/** The button batch. */
	String BUTTON_BATCH = "small.exp.batch.white";

	/** The button back. */
	String BUTTON_BACK = "small.exp.back.white";

	/** The button gui. */
	String MENU_GUI = "small.exp.run.green";

	/** The button batch. */
	String MENU_BATCH = "small.exp.batch.green";

	/** The button back. */
	String MENU_BACK = "small.exp.back.green";

	// Small Icons

	/** The small plus. */
	String SMALL_PLUS = "small.plus";

	/** The small minus. */
	String SMALL_MINUS = "small.minus";

	/** The small expand. */
	String SMALL_EXPAND = "small.expand2";

	/** The small collapse. */
	String SMALL_COLLAPSE = "small.collapse2";

	/** The small pause. */
	String SMALL_PAUSE = "small.pause";

	/** The small resume. */
	String SMALL_RESUME = "small.resume";

	/** The small close. */
	String SMALL_CLOSE = "small.close";

	// Overlays

	/** The overlay ok. */
	String OVERLAY_OK = "navigator/overlay.ok2";

}
