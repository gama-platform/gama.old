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

	private static void declareIcons() {

	}

	public static Map<Class, Image> layer_images = new HashMap();
	public static Map<String, Image> prefs_images = new LinkedHashMap();

	public static ImageDescriptor action_overlay = getDescriptor("display.overlay.png");
	public static ImageDescriptor action_sidebar = getDescriptor("display.sidebar.png");

	public static final ImageDescriptor action_drag = getDescriptor("blender_drag.png");
	public static final ImageDescriptor action_camera = getDescriptor("blender_camera.png");
	public static final ImageDescriptor action_erase = getDescriptor("button_clear.png");
	public static final ImageDescriptor action_snapshot = getDescriptor("blender_snapshot.png");
	public static final ImageDescriptor action_split = getDescriptor("blender_split.png");
	public static final ImageDescriptor action_switch = getDescriptor("blender_camera.png");
	public static final ImageDescriptor action_sync = getDescriptor("blender_sync.png");
	public static final ImageDescriptor action_zoomfit = getDescriptor("blender_zoomfit.png");
	public static final ImageDescriptor action_zoomin = getDescriptor("blender_zoomin.png");
	public static final ImageDescriptor action_zoomout = getDescriptor("blender_zoomout.png");
	public static final ImageDescriptor action_triangulate = getDescriptor("blender_triangulate.png");
	public static final ImageDescriptor action_inertia = getDescriptor("blender_inertia.png");
	public static final ImageDescriptor action_rotation = getDescriptor("blender_rotate.png");
	public static final ImageDescriptor menu_add_monitor = getDescriptor("menu_add_monitor.png");
	public static final ImageDescriptor menu_open_gl = getDescriptor("blender_3dmenu.png");
	public static final ImageDescriptor action_view_pause = getDescriptor("blender_pause.png");
	public static final ImageDescriptor action_view_revert = getDescriptor("blender_revert.png");
	public static final ImageDescriptor action_view_save = getDescriptor("button_save.png");
	public static final Image action_browse = getDescriptor("display_grid.png").createImage();
	public static final Image action_focus = getDescriptor("blender_focus.png").createImage();
	public static final Image action_follow = getDescriptor("blender_follow.png").createImage();
	public static final Image action_highlight = getDescriptor("blender_highlight.png").createImage();
	public static final Image action_inspect = getDescriptor("blender_inspect.png").createImage();
	public static final Image action_run = getDescriptor("menu_run.png").createImage();
	public static final Image button_collapse = getDescriptor("bullet_toggle_minus.png").createImage();
	public static final Image button_edit = getDescriptor("button_edit.png").createImage();
	public static final Image button_expand = getDescriptor("bullet_toggle_plus.png").createImage();
	public static final Image button_minus = getDescriptor("small_button_minus.png").createImage();
	public static final Image button_plus = getDescriptor("small_button_plus.png").createImage();
	public static final Image button_small_close = getDescriptor("small_button_close.png").createImage();
	public static final Image button_small_lock = getDescriptor("small_button_lock.png").createImage();
	public static final Image button_small_pause = getDescriptor("small_button_pause.png").createImage();
	public static final Image button_small_play = getDescriptor("small_button_play.png").createImage();
	public static final Image button_small_unlock = getDescriptor("small_button_unlock.png").createImage();
	public static final Image button_thumb_blue = getDescriptor("blender_knob.png").createImage();
	public static final Image button_thumb_blue_over = getDescriptor("blender_knob.png").createImage();
	public static final Image button_thumb_green = getDescriptor("knobNormal.png").createImage();
	public static final Image button_thumb_green_over = getDescriptor("knobHover.png").createImage();
	public static final Image menu_action = getDescriptor("action_run.png").createImage();
	public static final Image menu_agent = getDescriptor("blender_agent.png").createImage();
	public static final ImageDescriptor menu_population_desc = getDescriptor("blender_population.png");
	public static final Image menu_population = menu_population_desc.createImage();
	public static final ImageDescriptor overlay_ok_desc = getDescriptor("bullet_tick.png");
	public static final Image panel_action = menu_action;
	public static final Image panel_continue = getDescriptor("panel_continue.png").createImage();
	public static final Image panel_goto = getDescriptor("panel_goto.png").createImage();
	public static final Image panel_inspect = action_inspect;
	public static final Image panel_locate = getDescriptor("panel_locate.png").createImage();
	public static final Image slider_left = getDescriptor("trackCapLeft.png").createImage();
	public static final Image slider_line = getDescriptor("trackFill.png").createImage();
	public static final Image slider_right = getDescriptor("trackCapRight.png").createImage();
	public static final Image icon_gama = getDescriptor("launcher_icons/icon205.png").createImage();
	public static final Image icon_virtual_folder = getDescriptor("folder_library.png").createImage();
	public static final Image icon_builtin_folder = getDescriptor("folder_samples.png").createImage();
	public static final Image icon_user_folder = getDescriptor("folder_workspace.png").createImage();

	static {
		layer_images.put(GridLayer.class, getDescriptor("blender_display_grid.png").createImage());
		layer_images.put(AgentLayer.class, getDescriptor("blender_display_agents.png").createImage());
		layer_images.put(ImageLayer.class, getDescriptor("blender_display_image.png").createImage());
		layer_images.put(TextLayer.class, getDescriptor("blender_display_text.png").createImage());
		layer_images.put(SpeciesLayer.class, getDescriptor("blender_display_species.png").createImage());
		layer_images.put(ChartLayer.class, getDescriptor("blender_display_chart.png").createImage());
		layer_images.put(GraphicLayer.class, getDescriptor("blender_display_graphics.png").createImage());

		prefs_images.put(GamaPreferences.GENERAL, getDescriptor("prefs.general.png").createImage());
		prefs_images.put(GamaPreferences.DISPLAY, getDescriptor("prefs.display.png").createImage());
		prefs_images.put(GamaPreferences.CODE, getDescriptor("prefs.code.png").createImage());
		prefs_images.put(GamaPreferences.EDITOR, getDescriptor("prefs.editors.png").createImage());
		prefs_images.put(GamaPreferences.WORKSPACE, getDescriptor("prefs.startup.png").createImage());

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
		ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(IGui.PLUGIN_ID, DEFAULT_PATH + path);
		if ( desc == null ) {
			GuiUtils.debug("ERROR: Cannot find icon " + path);
			return getEclipseIconDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK);
		}
		return desc;
	}

}
