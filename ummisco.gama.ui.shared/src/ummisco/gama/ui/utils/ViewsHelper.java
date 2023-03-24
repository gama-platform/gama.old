/*******************************************************************************************************
 *
 * ViewsHelper.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import static ummisco.gama.ui.utils.WorkbenchHelper.getPage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.base.Objects;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGamaView.Display.InnerComponent;
import msi.gama.common.interfaces.IGui;
import one.util.streamex.StreamEx;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ViewsHelper.
 */
public class ViewsHelper {

	/** The is requesting. */
	static volatile boolean isRequesting;

	static {
		DEBUG.OFF();
	}

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
		return WorkbenchHelper.run(() -> {
			IGamaView.Display view = findFrontmostGamaViewUnderMouse();
			if (view != null) return view.getDisplaySurface();
			List<IDisplaySurface> surfaces = allDisplaySurfaces();
			if (surfaces.size() == 0) return null;
			return surfaces.get(0);
		});
	}

	/**
	 * Hide view.
	 *
	 * @param gamaViewPart
	 *            the gama view part
	 */
	public static void hideView(final IViewPart part) {
		if (part == null) return;
		final IWorkbenchPage activePage = getPage();
		if (activePage == null) return;
		activePage.hideView(part);

	}

	/**
	 * @todo find a more robust way to find the view (maybe with the control ?)
	 * @return
	 */
	public static IGamaView.Display findFrontmostGamaViewUnderMouse() {
		// First the full screen view
		int m = WorkbenchHelper.run(WorkbenchHelper::getMonitorUnderCursor);
		// DEBUG.OUT("First try with fullscreen on monitor " + m + " -- " + FULLSCREEN_VIEWS);
		IGamaView.Display view = WorkbenchHelper.run(() -> FULLSCREEN_VIEWS.get(m));
		if (view != null) return view;
		Control c = WorkbenchHelper.run(() -> WorkbenchHelper.getDisplay().getCursorControl());
		// DEBUG.OUT("Second try with control under mouse -- " + c);
		if (c instanceof CTabFolder t) {

			// DEBUG.OUT("Tab detected ");

			CTabItem i = t.getSelection();
			if (i != null) {
				for (IDisplaySurface d : allDisplaySurfaces()) {
					if (d.getOutput().getName().equals(i.getText())) return d.getOutput().getView();
				}
			}
		}

		if (c instanceof InnerComponent i) return i.getView();
		final IWorkbenchPage page = getPage();
		if (page == null) return null;
		final Point p = WorkbenchHelper.getDisplay().getCursorLocation();
		final List<IGamaView.Display> displays =
				WorkbenchHelper.run(() -> getDisplayViews(part -> page.isPartVisible(part)));
		// DEBUG.OUT("Third try with view -- at coordinates " + p + " -- in " + new HashSet<>(displays));
		for (IGamaView.Display v : displays) { if (v.containsPoint(p.x, p.y)) return v; }
		// DEBUG.OUT("No view under mouse");
		return null;
	}

	/**
	 * Toggle full screen mode. Tries to put the frontmost display in full screen mode or in normal view mode if it is
	 * already in full screen
	 *
	 * @return true, if successful
	 */
	public static boolean toggleFullScreenMode() {
		// DEBUG.OUT("Trying to toggle full screen mode");
		final IGamaView.Display part = WorkbenchHelper.run(ViewsHelper::findFrontmostGamaViewUnderMouse);
		if (part != null && !part.isEscRedefined()) return toggleFullScreenMode(part);
		return false;
	}

	/**
	 * Toggle full screen mode. Tries to put the frontmost display in full screen mode or in normal view mode if it is
	 * already in full screen
	 *
	 * @return true, if successful
	 */
	public static boolean toggleFullScreenMode(final IGamaView.Display part) {
		WorkbenchHelper.run(() -> part.toggleFullScreen());
		return true;
	}

	/** The fullscreen views. */
	static Map<Integer, IGamaView.Display> FULLSCREEN_VIEWS = new HashMap<>();

	/**
	 * Returns false if the screen is already covered by a view
	 *
	 * @param screen
	 * @param view
	 * @return
	 */
	public static boolean registerFullScreenView(final Integer screen, final IGamaView.Display view) {
		boolean result = FULLSCREEN_VIEWS.putIfAbsent(screen, view) == null;
		if (result) {
			DEBUG.OUT("Registered " + view + " as fullscreen on Monitor " + screen);
		} else {
			DEBUG.OUT("Impossible to register " + view + " as fullscreen on Monitor " + screen);
		}
		return result;
	}

	/**
	 * Unregister full screen view.
	 *
	 * @param view
	 *            the view
	 */
	public static void unregisterFullScreenView(final IGamaView.Display view) {
		FULLSCREEN_VIEWS.entrySet().removeIf(e -> {
			boolean result = Objects.equal(e.getValue(), view);
			if (result) { DEBUG.OUT("Unregistered " + view + " as fullscreen on Monitor " + e.getKey()); }
			return result;
		});
	}

	/**
	 * Activate.
	 *
	 * @param view
	 *            the view
	 */
	public static void activate(final IWorkbenchPart view) {
		if (view == null) return;
		final IWorkbenchPage activePage = getPage();
		if (activePage == null) return;
		activePage.activate(view);
	}

	/**
	 * Bring to front.
	 *
	 * @param view
	 *            the view
	 */
	public static void bringToFront(final IWorkbenchPart view) {
		if (view == null) return;
		final IWorkbenchPage activePage = getPage();
		if (activePage == null) return;
		activePage.bringToTop(view);
	}

	/**
	 * Gets the monitor of.
	 *
	 * @param layeredDisplayView
	 *            the layered display view
	 * @return the monitor of
	 */
	public static Monitor getMonitorOf(final IWorkbenchPart view) {
		return WorkbenchHelper.run(() -> WorkbenchHelper.getDisplay().getMonitors()[WorkbenchHelper
				.getMonitorContaining(view.getSite().getShell().getBounds())]);
	}

}
