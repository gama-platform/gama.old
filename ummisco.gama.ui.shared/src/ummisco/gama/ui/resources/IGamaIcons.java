/*********************************************************************************************
 *
 * 'IGamaIcons.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
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

	String DISPLAY_TOOLBAR_PAUSE = "display.pause3";
	String DISPLAY_TOOLBAR_SYNC = "display.sync3";
	String DISPLAY_TOOLBAR_SNAPSHOT = "display.snapshot2";
	String DISPLAY_TOOLBAR_ZOOMIN = "display.zoomin2";
	String DISPLAY_TOOLBAR_CSVEXPORT = "menu.saveas2";
	String DISPLAY_TOOLBAR_ZOOMOUT = "display.zoomout2";
	String DISPLAY_TOOLBAR_ZOOMFIT = "display.zoomfit2";

	// Menus

	String MENU_BROWSE = "menu.browse2";;
	String MENU_POPULATION = "display.agents2";
	String MENU_AGENT = "menu.agent2";
	String MENU_INSPECT = "menu.inspect2";;
	String MENU_HIGHLIGHT = "menu.highlight2";
	String MENU_KILL = "menu.kill2";
	String MENU_FOCUS = "menu.focus2";
	String MENU_FOLLOW = "menu.follow2";
	String MENU_ADD_MONITOR = "menu.monitor2";
	String MENU_RUN_ACTION = "menu.action2";

	// Layers

	String LAYER_GRID = "layer.grid2";
	String LAYER_SPECIES = "layer.species2";
	String LAYER_AGENTS = "layer.agents2";
	String LAYER_GRAPHICS = "layer.graphics2";
	String LAYER_IMAGE = "layer.image2";
	String LAYER_CHART = "layer.chart2";

	// Actions

	String ACTION_REVERT = "action.revert2";
	String ACTION_CLEAR = "action.clear2";

	// User Panels

	String PANEL_CONTINUE = "panel.continue2";
	String PANEL_INSPECT = MENU_INSPECT;

	// Preferences tabs. 24x24

	String PREFS_GENERAL = "prefs/prefs.general2";
	String PREFS_EDITOR = "prefs/prefs.editor2";
	String PREFS_LIBS = "prefs/prefs.libraries2";

	// Navigator

	String FOLDER_BUILTIN = "navigator/folder.library2";
	String FOLDER_PLUGIN = "navigator/folder.plugin2";
	String FOLDER_TEST = "navigator/folder.test2";
	String FOLDER_PROJECT = "navigator/folder.user2";
	String FOLDER_MODEL = "navigator/folder.model3";
	String FOLDER_RESOURCES = "navigator/folder.resources2";
	String FILE_ICON = "navigator/file.icon2";
	String FOLDER_USER = "navigator/folder.user";

	// Editor specific

	String BUTTON_GUI = "small.run";
	String BUTTON_BATCH = "small.batch";
	String BUTTON_BACK = "small.run.and.back";

	// Small Icons

	String SMALL_PLUS = "small.plus";
	String SMALL_MINUS = "small.minus";
	String SMALL_EXPAND = "small.expand";
	String SMALL_COLLAPSE = "small.collapse";
	String SMALL_PAUSE = "small.pause";
	String SMALL_RESUME = "small.resume";
	String SMALL_CLOSE = "small.close";

	// Overlays

	String OVERLAY_OK = "navigator/overlay.ok2";

	// Viewers

	String CHECKED = "viewers/checked";
	String UNCHECKED = "viewers/unchecked";
	String STYLE = "viewers/style";
	String FEATURE = "viewers/feature";
	String UP = "viewers/up";
	String DOWN = "viewers/down";

}
