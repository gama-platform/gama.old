/*********************************************************************************************
 *
 * 'ViewsHelper.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.utils;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGamaView.Display;
import msi.gama.common.interfaces.IGui;
import one.util.streamex.StreamEx;
import ummisco.gama.ui.views.IGamlEditor;

public class ViewsHelper {

	static volatile boolean isRequesting;

	public static void requestUserAttention(final IGamaView part, final String tempMessage) {
		if (isRequesting) { return; }
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
	 * @todo find a more robust way to find the view (maybe with the control ?)
	 * @return
	 */
	public static IViewPart findFrontmostGamaViewUnderMouse() {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) return null;
		final Point p = WorkbenchHelper.getDisplay().getCursorLocation();
		final List<IGamaView.Display> displays = StreamEx.of(page.getViewReferences()).map(r -> r.getView(false))
				.filter(part -> page.isPartVisible(part)).select(IGamaView.Display.class)
				.filter(display -> display.containsPoint(p.x, p.y)).toList();
		if (displays.isEmpty()) return null;
		if (displays.size() == 1) return (IViewPart) displays.get(0);
		for (final IGamaView.Display display : displays) { if (display.isFullScreen()) return (IViewPart) display; }
		// Strange: n views, none of them fullscreen, claiming to contain the mouse pointer...
		return (IViewPart) displays.get(0);
	}

	/**
	 * Gets the active editor.
	 *
	 * @return the active editor
	 */
	public static IGamlEditor getActiveEditor() {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page != null) {
			final IEditorPart editor = page.getActiveEditor();
			if (editor instanceof IGamlEditor) return (IGamlEditor) editor;
		}
		return null;
	}

	/**
	 * Gets the active part.
	 *
	 * @return the active part
	 */
	public static IWorkbenchPart getActivePart() {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page != null) return page.getActivePart();
		return null;
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
	public static List<IGamaView.Display> getDisplayViews() {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) return Collections.EMPTY_LIST;
		return StreamEx.of(page.getViewReferences()).map(v -> v.getView(false)).select(IGamaView.Display.class)
				.toList();
	}

	/**
	 * Hide view.
	 *
	 * @param id
	 *            the id
	 */
	public static void hideView(final String id) {
	
		WorkbenchHelper.run(() -> {
			final IWorkbenchPage activePage = WorkbenchHelper.getPage();
			if (activePage == null) return;
			final IWorkbenchPart part = activePage.findView(id);
			if (part != null && activePage.isPartVisible(part)) { activePage.hideView((IViewPart) part); }
		});
	
	}

	/**
	 * Hide view.
	 *
	 * @param gamaViewPart
	 *            the gama view part
	 */
	public static void hideView(final IViewPart gamaViewPart) {
		final IWorkbenchPage activePage = WorkbenchHelper.getPage();
		if (activePage == null) return;
		activePage.hideView(gamaViewPart);
	
	}

}
