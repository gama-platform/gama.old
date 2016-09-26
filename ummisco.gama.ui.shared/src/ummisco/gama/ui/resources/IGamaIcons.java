/*********************************************************************************************
 *
 *
 * 'IStrings.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
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

	public static final String DISPLAY_TOOLBAR_PAUSE = "display.pause2";
	public static final String DISPLAY_TOOLBAR_KNOB = "display.knob2";
	public static final String DISPLAY_TOOLBAR_SLIDER = "display.slider2";
	public static final String DISPLAY_TOOLBAR_SYNC = "display.sync2";
	public static final String DISPLAY_TOOLBAR_SNAPSHOT = "display.snapshot2";
	public static final String DISPLAY_TOOLBAR_ZOOMIN = "display.zoomin2";
	public static final String DISPLAY_TOOLBAR_CSVEXPORT = "menu.saveas2";
	public static final String DISPLAY_TOOLBAR_ZOOMOUT = "display.zoomout2";
	public static final String DISPLAY_TOOLBAR_ZOOMFIT = "display.zoomfit2";
	public static final String DISPLAY_TOOLBAR_AGENTS = "display.agents2";
	public static final String DISPLAY_TOOLBAR_OPENGL = "display.opengl2";
	public static final String DISPLAY_TOOLBAR_CAMERA = "display.camera2";
	public static final String DISPLAY_TOOLBAR_ROTATE = "display.rotate2";
	public static final String DISPLAY_TOOLBAR_SPLIT = "display.split2";
	public static final String DISPLAY_TOOLBAR_TRIANGULATE = "display.triangulate2";

	// Menus

	public static final String MENU_BROWSE = "menu.browse2";;
	public static final String MENU_POPULATION = DISPLAY_TOOLBAR_AGENTS;
	public static final String MENU_AGENT = "menu.agent2";
	public static final String MENU_INSPECT = "menu.inspect2";;
	public static final String MENU_HIGHLIGHT = "menu.highlight2";
	public static final String MENU_KILL = "menu.kill2";
	public static final String MENU_FOCUS = "menu.focus2";
	public static final String MENU_FOLLOW = "menu.follow2";
	public static final String MENU_ADD_MONITOR = "menu.monitor2";
	public static final String MENU_RUN_ACTION = "menu.action2";

	// Layers

	public static final String LAYER_GRID = "layer.grid2";
	public static final String LAYER_SPECIES = "layer.species2";
	public static final String LAYER_AGENTS = "layer.agents2";
	public static final String LAYER_GRAPHICS = "layer.graphics2";
	public static final String LAYER_TEXT = "layer.text2";
	public static final String LAYER_IMAGE = "layer.image2";
	public static final String LAYER_CHART = "layer.chart2";

	// Actions

	public static final String ACTION_REVERT = "action.revert2";
	public static final String ACTION_CLEAR = "action.clear2";

	// General toolbar

	public static final String TOOLBAR_KNOB = "toolbar.knob2";

	// User Panels

	public static final String PANEL_CONTINUE = "panel.continue2";
	public static final String PANEL_INSPECT = MENU_INSPECT;

	// Preferences tabs. 24x24

	public static final String PREFS_GENERAL = "prefs.general2";
	public static final String PREFS_DISPLAY = "prefs.display2";
	public static final String PREFS_EDITOR = "prefs.editor2";
	public static final String PREFS_LIBS = "prefs.libraries2";

	// Navigator

	public static final String FOLDER_BUILTIN = "navigator/folder.library2";
	public static final String FOLDER_BUILTIN_16 = "navigator/folder.library2.16";
	public static final String FOLDER_PLUGIN = "navigator/folder.plugin2";
	public static final String FOLDER_PLUGIN_16 = "navigator/folder.plugin2.16";
	public static final String FOLDER_PROJECT = "folder.user2";
	public static final String FOLDER_MODEL = "folder.model3";
	public static final String FOLDER_RESOURCES = "folder.resources2";
	public static final String FILE_ICON = "file.icon2";
	public static final String FOLDER_USER = "navigator/folder.user";
	public static final String FOLDER_USER_16 = "navigator/folder.user.16";

	// Editor specific

	public static final String BUTTON_GUI = "small.run";
	public static final String BUTTON_BATCH = "small.batch";

	// Small Icons

	public static final String SMALL_PLUS = "small.plus";
	public static final String SMALL_MINUS = "small.minus";
	public static final String SMALL_EXPAND = "small.expand";
	public static final String SMALL_COLLAPSE = "small.collapse";
	public static final String SMALL_PAUSE = "small.pause";
	public static final String SMALL_RESUME = "small.resume";
	public static final String SMALL_CLOSE = "small.close";

	// Overlays

	public static final String OVERLAY_OK = "overlay.ok2";

	// Viewers

	public static final String CHECKED = "viewers/checked";
	public static final String UNCHECKED = "viewers/unchecked";
	public static final String STYLE = "viewers/style";
	public static final String GRID = "viewers/grid";
	public static final String FEATURE = "viewers/feature";
	public static final String UP = "viewers/up";
	public static final String DOWN = "viewers/down";
	public static final String OPEN = "viewers/open";
	public static final String IMAGE_INFO = "viewers/info_mode";
	public static final String IMAGE_PAN = "viewers/pan_mode";
	public static final String IMAGE_ZOOMIN = "viewers/zoom_in_co";
	public static final String IMAGE_ZOOMOUT = "viewers/zoom_out_co";
	public static final String IMAGE_FULLEXTENT = "viewers/zoom_extent_co";

}
