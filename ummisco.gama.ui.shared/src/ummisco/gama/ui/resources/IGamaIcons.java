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

	/** The experiment run. */
	String EXPERIMENT_RUN = "experiment/experiment.run";

	/** The display update. */
	String DISPLAY_UPDATE = "display/action.update";

	/** The display toolbar sync. */
	String DISPLAY_TOOLBAR_SYNC = "experiment/experiment.sync";

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
	String MENU_BROWSE = "views/open.browser";

	/** The menu population. */
	String MENU_POPULATION = "agents/agents.submenu";

	/** The menu agent. */
	String MENU_AGENT = "agents/agent.submenu";

	/** The menu inspect. */
	String MENU_INSPECT = "views/open.inspector";

	/** The menu highlight. */
	String MENU_HIGHLIGHT = "display/action.highlight";

	/** The menu kill. */
	String MENU_KILL = "agents/agent.kill";

	/** The menu focus. */
	String MENU_FOCUS = "display/action.focus";

	/** The menu add monitor. */
	String MENU_ADD_MONITOR = "views/open.monitor";

	/** The menu run action. */
	String MENU_RUN_ACTION = "agents/agent.actions";

	/** The menu pause action. */
	String MENU_PAUSE_ACTION = "experiment/experiment.pause";

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

	/** The layer selection. */
	String LAYER_SELECTION = "layer/layer.selection";

	/** The layer transparency. */
	String LAYER_TRANSPARENCY = "layer/layer.transparency";

	// Actions

	/** The action revert. */
	String ACTION_REVERT = "generic/menu.undo";

	/** The action clear. */
	String ACTION_CLEAR = "console/console.clear";

	// User Panels

	/** The panel continue. */
	String PANEL_CONTINUE = "experiment/experiment.continue";

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
	String BUTTON_GUI = "overlays/small.exp.run.white";

	/** The button batch. */
	String BUTTON_BATCH = "overlays/small.exp.batch.white";

	/** The button back. */
	String BUTTON_BACK = "overlays/small.exp.back.white";

	/** The button gui. */
	String MENU_GUI = "overlays/small.exp.run.green";

	/** The button batch. */
	String MENU_BATCH = "overlays/small.exp.batch.green";

	/** The button back. */
	String MENU_BACK = "overlays/small.exp.back.green";

	// Small Icons

	/** The small plus. */
	String SMALL_PLUS = "overlays/small.plus";

	/** The small minus. */
	String SMALL_MINUS = "overlays/small.minus";

	/** The small expand. */
	String SMALL_EXPAND = "overlays/small.expand";

	/** The small collapse. */
	String SMALL_COLLAPSE = "overlays/small.collapse";

	/** The small pause. */
	String SMALL_PAUSE = "overlays/small.pause";

	/** The small resume. */
	String SMALL_RESUME = "overlays/small.resume";

	/** The small close. */
	String SMALL_CLOSE = "overlays/small.close";

	// Navigator

	/** The overlay ok. */
	String OVERLAY_OK = "navigator/overlay.ok2";

	/** The overlay error. */
	String OVERLAY_ERROR = "navigator/overlay.error2";

	/** The overlay warning. */
	String OVERLAY_WARNING = "navigator/overlay.warning2";

	/** The chart parameters. */
	String CHART_PARAMETERS = "layer/chart.parameters";

	/** The toggle antialias. */
	String TOGGLE_ANTIALIAS = "display/toggle.antialias";

	/** The toggle overlay. */
	String TOGGLE_OVERLAY = "display/toggle.overlay";

	/** The experiment step. */
	String EXPERIMENT_STEP = "experiment/experiment.step";

	/** The experiment stop. */
	String EXPERIMENT_STOP = "experiment/experiment.stop";

	/** The experiment reload. */
	String EXPERIMENT_RELOAD = "experiment/experiment.reload";

	/** The presentation menu. */
	String PRESENTATION_MENU = "display/menu.presentation";

	/** The add simulation. */
	String ADD_SIMULATION = "experiment/add.simulation";

	/** The lock population. */
	String LOCK_POPULATION = "agents/population.lock";

	/** The browse populations. */
	String BROWSE_POPULATIONS = "agents/population.list";

	/** The save as. */
	String SAVE_AS = "generic/menu.saveas";

	/** The marker deleted. */
	String MARKER_DELETED = "markers/marker.deleted";

	/** The marker error dark. */
	String MARKER_ERROR_DARK = "markers/marker.error.dark";

	/** The marker error. */
	String MARKER_ERROR = "markers/marker.error";

	/** The marker warning. */
	String MARKER_WARNING = "markers/marker.warning";

	/** The marker info dark. */
	String MARKER_INFO_DARK = "markers/marker.info.dark";

	/** The marker info. */
	String MARKER_INFO = "markers/marker.info";

	/** The marker task. */
	String MARKER_TASK = "markers/marker.task";

	/** The add experiment. */
	String ADD_EXPERIMENT = "overlays/small.exp.plus";

	/** The status clock. */
	String STATUS_CLOCK = "overlays/status.clock";

	/** The small dropdown. */
	String SMALL_DROPDOWN = "overlays/small.dropdown";

	/** The editor link. */
	String EDITOR_LINK = "navigator/editor.link";

	/** The lexical sort. */
	String LEXICAL_SORT = "editor/lexical.sort";

	/** The reference builtin. */
	String REFERENCE_BUILTIN = "editor/reference.builtin";

	/** The reference colors. */
	String REFERENCE_COLORS = "editor/reference.colors";

	/** The reference operators. */
	String REFERENCE_OPERATORS = "editor/reference.operators";

	/** The reference templates. */
	String REFERENCE_TEMPLATES = "editor/reference.templates";

}
