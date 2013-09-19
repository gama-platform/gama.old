/**
 * Created by drogoul, 12 sept. 2013
 * 
 */
package msi.gama.gui.swt;

import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.layers.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Class GamaIcons.
 * 
 * @author drogoul
 * @since 12 sept. 2013
 * 
 */
public class GamaIcons implements IGamaIcons {

	static String DEFAULT_PATH = "/icons/";

	static Map<String, GamaIcon> icons = new HashMap();

	static class GamaIcon {

		static Map<String, Image> cache = new HashMap();

		String code;
		String path;
		ImageDescriptor descriptor;

		ImageDescriptor getImageDescriptor() {
			if ( descriptor == null ) {
				descriptor = getDescriptor(path);
			}
			return descriptor;
		}

		Image getImage() {
			Image image = cache.get(code);
			if ( image == null ) {
				image = getImageDescriptor().createImage();
				cache.put(code, image);
			}
			return image;
		}
	}

	public static Map<Class, Image> layer_images = new HashMap();
	public static Map<String, Image> prefs_images = new LinkedHashMap();

	public static ImageDescriptor action_overlay = getDescriptor(DISPLAY_TOOLBAR_OVERLAY);
	public static ImageDescriptor action_sidebar = getDescriptor(DISPLAY_TOOLBAR_SIDEBAR);

	public static final ImageDescriptor action_drag = getDescriptor(DISPLAY_TOOLBAR_DRAG);
	public static final ImageDescriptor action_camera = getDescriptor(DISPLAY_TOOLBAR_CAMERA);
	public static final ImageDescriptor action_erase = getDescriptor(ACTION_CLEAR);
	public static final ImageDescriptor action_snapshot = getDescriptor(DISPLAY_TOOLBAR_SNAPSHOT);
	public static final ImageDescriptor action_split = getDescriptor(DISPLAY_TOOLBAR_SPLIT);
	public static final ImageDescriptor action_switch = getDescriptor(DISPLAY_TOOLBAR_CAMERA);
	public static final ImageDescriptor action_sync = getDescriptor(DISPLAY_TOOLBAR_SYNC);
	public static final ImageDescriptor action_zoomfit = getDescriptor(DISPLAY_TOOLBAR_ZOOMFIT);
	public static final ImageDescriptor action_zoomin = getDescriptor(DISPLAY_TOOLBAR_ZOOMIN);
	public static final ImageDescriptor action_zoomout = getDescriptor(DISPLAY_TOOLBAR_ZOOMOUT);
	public static final ImageDescriptor action_triangulate = getDescriptor(DISPLAY_TOOLBAR_TRIANGULATE);
	public static final ImageDescriptor action_inertia = getDescriptor(DISPLAY_TOOLBAR_INERTIA);
	public static final ImageDescriptor action_rotation = getDescriptor(DISPLAY_TOOLBAR_ROTATE);
	public static final ImageDescriptor menu_add_monitor = getDescriptor(MENU_ADD_MONITOR);
	public static final ImageDescriptor menu_open_gl = getDescriptor(DISPLAY_TOOLBAR_OPENGL);
	public static final ImageDescriptor action_view_pause = getDescriptor(DISPLAY_TOOLBAR_PAUSE);
	public static final ImageDescriptor action_view_revert = getDescriptor(ACTION_REVERT);
	public static final ImageDescriptor action_view_save = getDescriptor(ACTION_SAVE);
	public static final Image action_browse = getDescriptor(MENU_BROWSE).createImage();
	public static final Image action_focus = getDescriptor(MENU_FOCUS).createImage();
	public static final Image action_follow = getDescriptor(MENU_FOLLOW).createImage();
	public static final Image action_highlight = getDescriptor(MENU_HIGHLIGHT).createImage();
	public static final Image action_inspect = getDescriptor(MENU_INSPECT).createImage();
	public static final Image action_run = getDescriptor(MENU_RUN).createImage();
	public static final Image button_collapse = getDescriptor(SMALL_COLLAPSE).createImage();
	public static final Image button_edit = getDescriptor(BUTTON_EDIT).createImage();
	public static final Image button_expand = getDescriptor(SMALL_EXPAND).createImage();
	public static final Image button_minus = getDescriptor(SMALL_MINUS).createImage();
	public static final Image button_plus = getDescriptor(SMALL_PLUS).createImage();
	public static final Image button_small_close = getDescriptor(SMALL_CLOSE).createImage();
	public static final Image button_small_lock = getDescriptor(SMALL_LOCK).createImage();
	public static final Image button_small_pause = getDescriptor(SMALL_PAUSE).createImage();
	public static final Image button_small_play = getDescriptor(SMALL_RESUME).createImage();
	public static final Image button_small_unlock = getDescriptor(SMALL_UNLOCK).createImage();
	public static final Image button_thumb_blue = getDescriptor(DISPLAY_TOOLBAR_KNOB).createImage();
	public static final Image button_thumb_blue_over = getDescriptor(DISPLAY_TOOLBAR_KNOB).createImage();
	public static final Image button_thumb_green = getDescriptor(TOOLBAR_KNOB).createImage();
	public static final Image button_thumb_green_over = getDescriptor(TOOLBAR_KNOB).createImage();
	public static final Image menu_action = getDescriptor(MENU_RUN_ACTION).createImage();
	public static final Image menu_agent = getDescriptor(MENU_AGENT).createImage();
	public static final ImageDescriptor menu_population_desc = getDescriptor(MENU_POPULATION);
	public static final Image menu_population = menu_population_desc.createImage();
	public static final ImageDescriptor overlay_ok_desc = getDescriptor(OVERLAY_OK);
	public static final Image panel_action = menu_action;
	public static final Image panel_continue = getDescriptor(PANEL_CONTINUE).createImage();
	public static final Image panel_goto = getDescriptor(PANEL_GOTO).createImage();
	public static final Image panel_inspect = action_inspect;
	public static final Image panel_locate = getDescriptor(PANEL_LOCATE).createImage();
	public static final Image slider_left = getDescriptor(TOOLBAR_SLIDER).createImage();
	public static final Image slider_line = getDescriptor(TOOLBAR_SLIDER).createImage();
	public static final Image slider_right = getDescriptor(TOOLBAR_SLIDER).createImage();
	public static final Image icon_gama = getDescriptor("launcher_icons/icon205").createImage();
	public static final Image icon_virtual_folder = getDescriptor(FOLDER_SHARED).createImage();
	public static final Image icon_builtin_folder = getDescriptor(FOLDER_BUILTIN).createImage();
	public static final Image icon_user_folder = getDescriptor(FOLDER_USER).createImage();

	static {
		layer_images.put(GridLayer.class, getDescriptor(LAYER_GRID).createImage());
		layer_images.put(AgentLayer.class, getDescriptor(LAYER_AGENTS).createImage());
		layer_images.put(ImageLayer.class, getDescriptor(LAYER_IMAGE).createImage());
		layer_images.put(TextLayer.class, getDescriptor(LAYER_TEXT).createImage());
		layer_images.put(SpeciesLayer.class, getDescriptor(LAYER_SPECIES).createImage());
		layer_images.put(ChartLayer.class, getDescriptor(LAYER_CHART).createImage());
		layer_images.put(GraphicLayer.class, getDescriptor(LAYER_GRAPHICS).createImage());

		prefs_images.put(GamaPreferences.GENERAL, getDescriptor(PREFS_GENERAL).createImage());
		prefs_images.put(GamaPreferences.DISPLAY, getDescriptor(PREFS_DISPLAY).createImage());
		prefs_images.put(GamaPreferences.CODE, getDescriptor(PREFS_CODE).createImage());
		prefs_images.put(GamaPreferences.EDITOR, getDescriptor(PREFS_EDITOR).createImage());
		prefs_images.put(GamaPreferences.WORKSPACE, getDescriptor(PREFS_WORKSPACE).createImage());
		prefs_images.put(GamaPreferences.LIBRARIES, getDescriptor(PREFS_LIBS).createImage());

	}

	/*
	 * Use "ISharedImages.field"
	 */
	public static final ImageDescriptor getEclipseIconDescriptor(final String icon) {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(icon);
	}

	public static final Image getEclipseIcon(final String icon) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(icon);
	}

	/**
	 * Returns an image descriptor for the image file in the global plug-in
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getDescriptor(final String path) {
		ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(IGui.PLUGIN_ID, DEFAULT_PATH + path + ".png");
		if ( desc == null ) {
			GuiUtils.debug("ERROR: Cannot find icon " + path);
			return getEclipseIconDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK);
		}
		return desc;
	}

}
