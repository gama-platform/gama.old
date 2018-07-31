/*********************************************************************************************
 *
 * 'VirtualContent.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import gnu.trove.map.hash.TIntObjectHashMap;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;

public abstract class VirtualContent<P extends VirtualContent<?>> {

	public static enum VirtualContentType {
		ROOT, VIRTUAL_FOLDER, PROJECT, FOLDER, FILE, FILE_REFERENCE, CATEGORY, GAML_ELEMENT
	}

	public static ILabelProvider DEFAULT_LABEL_PROVIDER = WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider();

	public static final TIntObjectHashMap<ImageDescriptor> DESCRIPTORS = new TIntObjectHashMap<ImageDescriptor>() {
		{

			put(CLOSED, GamaIcons.create("overlay.closed2").descriptor());
			put(NO_PROBLEM, GamaIcons.create("overlay.ok2").descriptor());
			put(IMarker.SEVERITY_INFO, GamaIcons.create("overlay.ok2").descriptor());
			put(IMarker.SEVERITY_WARNING, GamaIcons.create("overlay.warning2").descriptor());
			put(IMarker.SEVERITY_ERROR, GamaIcons.create("overlay.error2").descriptor());
			put(LINK_OK, GamaIcons.create("overlay.ok").descriptor());
			put(LINK_BROKEN, GamaIcons.create("overlay.link.broken").descriptor());
			put(WEBLINK_OK, GamaIcons.create("overlay.cloud").descriptor());
			put(WEBLINK_BROKEN, GamaIcons.create("overlay.link.broken").descriptor());
		}
	};

	public static final int NO_PROBLEM = -1;
	public static final int CLOSED = -2;
	public static final int LINK_OK = -3;
	public static final int LINK_BROKEN = -4;
	public static final int WEBLINK_OK = -5;
	public static final int WEBLINK_BROKEN = -6;
	public static Object[] EMPTY = new Object[0];
	public static WrappedProject[] EMPTY_PROJECTS = new WrappedProject[0];

	private final P root;
	private final String name;

	public VirtualContent(final P root, final String name) {
		this.root = root;
		this.name = name;
	}

	public ResourceManager getManager() {
		return NavigatorRoot.getInstance().getManager();
	}

	public abstract VirtualContentType getType();

	/**
	 * Should both perform something and answer whether or not it has performed it, so that the navigator knows whether
	 * it should handle double-clicks itself
	 * 
	 * @return
	 */
	public boolean handleDoubleClick() {
		return false;
	}

	public boolean handleSingleClick() {
		return false;
	}

	public String getName() {
		return name;
	}

	public P getParent() {
		return root;
	}

	public abstract boolean hasChildren();

	public abstract Object[] getNavigatorChildren();

	public abstract Image getImage();

	public abstract Color getColor();

	public abstract void getSuffix(StringBuilder sb);

	public Font getFont() {
		return GamaFonts.getNavigFolderFont(); // by default
	}

	public abstract int findMaxProblemSeverity();

	public abstract ImageDescriptor getOverlay();

	public TopLevelFolder getTopLevelFolder() {
		final Object p = getParent();
		if (p instanceof VirtualContent) { return ((VirtualContent<?>) p).getTopLevelFolder(); }
		return null;
	}

	public WrappedProject getProject() {
		final Object p = getParent();
		if (p instanceof VirtualContent) { return ((VirtualContent<?>) p).getProject(); }
		return null;
	}

	public String getStatusMessage() {
		return getName();
	}

	public String getStatusTooltip() {
		return null;
	}

	public GamaUIColor getStatusColor() {
		return IGamaColors.GRAY_LABEL;
	}

	public Image getStatusImage() {
		return getImage();
	}

	public boolean isContainedIn(final VirtualContent<?> current) {
		if (root == null) { return false; }
		return root == current || root.isContainedIn(current);
	}

}
