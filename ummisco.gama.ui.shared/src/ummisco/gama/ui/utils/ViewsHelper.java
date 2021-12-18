/*******************************************************************************************************
 *
 * ViewsHelper.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import static ummisco.gama.ui.utils.WorkbenchHelper.getPage;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import one.util.streamex.StreamEx;

/**
 * The Class ViewsHelper.
 */
public class ViewsHelper {

	/** The is requesting. */
	static volatile boolean isRequesting;

	/**
	 * Request user attention.
	 *
	 * @param part
	 *            the part
	 * @param tempMessage
	 *            the temp message
	 */
	public static void requestUserAttention(final IGamaView part, final String tempMessage) {
		if (isRequesting) return;
		// rate at which the title will change in milliseconds
		final int rateOfChange = 200;
		final int numberOfTimes = 2;

		// flash n times and thats it
		final String orgText = part.getPartName();

		for (int x = 0; x < numberOfTimes; x++) {
			WorkbenchHelper.getDisplay().timerExec(2 * rateOfChange * x - rateOfChange, () -> {
				isRequesting = true;
				part.setName(tempMessage);
			});
			WorkbenchHelper.getDisplay().timerExec(2 * rateOfChange * x, () -> {
				part.setName(orgText);
				isRequesting = false;
			});
		}
	}

	/**
	 * Find display.
	 *
	 * @param id
	 *            the id
	 * @return the i gama view. display
	 */
	public static IGamaView.Display findDisplay(final String id) {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) return null;
		final IViewReference ref = page.findViewReference(id);
		if (ref == null) return null;
		final IViewPart view = ref.getView(false);
		if (view instanceof IGamaView.Display) return (IGamaView.Display) view;
		return null;
	}

	/**
	 * Checks if is display.
	 *
	 * @param id
	 *            the id
	 * @return true, if is display
	 */
	public static boolean isDisplay(final String id) {
		if (!id.startsWith(IGui.GL_LAYER_VIEW_ID) && !id.startsWith(IGui.LAYER_VIEW_ID)) return false;
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) return false;
		final IViewReference ref = page.findViewReference(id);
		return ref != null;
		// final IViewPart view = ref.getView(false);
		// if (view instanceof IGamaView.Display) { return (IGamaView.Display) view; }
		// return <
	}

	/**
	 * Find view.
	 *
	 * @param id
	 *            the id
	 * @param second
	 *            the second
	 * @param restore
	 *            the restore
	 * @return the i view part
	 */
	public static IViewPart findView(final String id, final String second, final boolean restore) {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) return null;
		final IViewReference ref = page.findViewReference(id, second);
		if (ref == null) return null;
		return ref.getView(restore);
	}

	/**
	 * Gets the display views.
	 *
	 * @return the display views
	 */
	public static List<IGamaView.Display> getDisplayViews(final Predicate<IViewPart> p) {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) return Collections.EMPTY_LIST;
		return StreamEx.of(page.getViewReferences()).map(r -> r.getView(false)).filter(p == null ? v -> true : p)
				.select(IGamaView.Display.class).toList();
	}

	/**
	 * Hide view.
	 *
	 * @param id
	 *            the id
	 */
	public static void hideView(final String id) {
		// See if asyncRun would not be more appropriate ?
		WorkbenchHelper.run(() -> {
			final IWorkbenchPage activePage = getPage();
			if (activePage == null) return;
			final IViewReference view = activePage.findViewReference(id);
			if (view != null) {
				IWorkbenchPart part = view.getPart(false);
				if (part != null && activePage.isPartVisible(part)) { activePage.hideView((IViewPart) part); }
			}

		});

	}

	/**
	 * All display surfaces.
	 *
	 * @return the list
	 */
	public static List<IDisplaySurface> allDisplaySurfaces() {
		return StreamEx.of(getDisplayViews(null)).map(IGamaView.Display::getDisplaySurface).toList();
	}

	/**
	 * Frontmost display surface.
	 *
	 * @return the i display surface
	 */
	public static IDisplaySurface frontmostDisplaySurface() {
		IViewPart view = findFrontmostGamaViewUnderMouse();
		if (view instanceof IGamaView.Display) return ((IGamaView.Display) view).getDisplaySurface();
		List<IDisplaySurface> surfaces = allDisplaySurfaces();
		if (surfaces.size() == 0) return null;
		return surfaces.get(0);
	}

	/**
	 * Hide view.
	 *
	 * @param gamaViewPart
	 *            the gama view part
	 */
	public static void hideView(final IViewPart gamaViewPart) {
		final IWorkbenchPage activePage = getPage();
		if (activePage == null) return;
		activePage.hideView(gamaViewPart);

	}

	/**
	 * @todo find a more robust way to find the view (maybe with the control ?)
	 * @return
	 */
	public static IViewPart findFrontmostGamaViewUnderMouse() {
		final IWorkbenchPage page = getPage();
		if (page == null) return null;
		final Point p = WorkbenchHelper.getDisplay().getCursorLocation();
		final List<IGamaView.Display> displays = getDisplayViews(part -> page.isPartVisible(part));
		for (IGamaView.Display v : displays) {
			if (v.isFullScreen() || v.containsPoint(p.x, p.y)) return (IViewPart) v;
		}
		return null;
	}

}
