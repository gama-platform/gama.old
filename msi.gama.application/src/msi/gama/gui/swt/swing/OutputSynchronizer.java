package msi.gama.gui.swt.swing;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import msi.gama.common.util.GuiUtils;
import org.eclipse.ui.IWorkbenchPage;

public class OutputSynchronizer {

	// TODO Rewrite this with locks / semaphores

	private static volatile AtomicInteger NumberOpeningViews = new AtomicInteger(0);
	private static volatile AtomicInteger NumberClosingViews = new AtomicInteger(0);

	static List<String> viewsScheduledToOpen = new ArrayList();
	static List<String> viewsScheduledToClose = new ArrayList();
	static List<String> viewsScheduledToBeActivated = new ArrayList();

	public static int getNumberOfViewsWaitingToOpen() {
		return NumberOpeningViews.get();
	}

	public static int getNumberOfViewsWaitingToClose() {
		return NumberClosingViews.get();
	}

	public static void incInitializingViews(final String view) {
		GuiUtils.debug("GuiOutputManager.incInitializingViews: " + view);
		viewsScheduledToOpen.add(view);
		viewsScheduledToClose.add(view);
		viewsScheduledToBeActivated.add(0, view);
		NumberOpeningViews.incrementAndGet();
		NumberClosingViews.incrementAndGet();
	}

	public static void decClosingViews(final String view) {
		// GuiUtils.debug("GuiOutputManager.decClosingViews: " + view);
		viewsScheduledToClose.remove(view);
		NumberClosingViews.decrementAndGet();
	}

	public static void decInitializingViews(final String view) {
		// GuiUtils.debug("GuiOutputManager.decInitializingViews: " + view);
		viewsScheduledToOpen.remove(view);
		NumberOpeningViews.decrementAndGet();
		GuiUtils.debug("Briefly showing :" + view);
		GuiUtils.showView(GuiUtils.LAYER_VIEW_ID, view, IWorkbenchPage.VIEW_VISIBLE);
	}

	public static void waitForViewsToBeInitialized() {
		while (getNumberOfViewsWaitingToOpen() > 0) {
			try {
				GuiUtils.waitStatus("Initializing " + getNumberOfViewsWaitingToOpen() + " display(s)");
				if ( getNumberOfViewsWaitingToOpen() > 0 ) {
					// Workaround for OpenGL views. Necessary to "show" the view
					// even briefly so that OpenGL can call the init() method of the renderer
					final List<String> names = new ArrayList(viewsScheduledToOpen);
					GuiUtils.debug("Briefly showing :" + names.get(0));
					GuiUtils.showView(GuiUtils.LAYER_VIEW_ID, names.get(0), IWorkbenchPage.VIEW_ACTIVATE);
				}
				Thread.sleep(100);
			} catch (final InterruptedException e) {}
		}
		cleanResize();
	}

	public static void waitForViewsToBeClosed() {
		while (getNumberOfViewsWaitingToClose() > 0) {
			GuiUtils.waitStatus("Closing previous displays");
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static List<Runnable> cleanResizers = new ArrayList();

	/**
	 * @param runnable
	 */
	public static void cleanResize(final Runnable runnable) {
		cleanResizers.add(runnable);
	}

	public static void cleanResize() {
		final List<String> names = new ArrayList(viewsScheduledToBeActivated);
		viewsScheduledToBeActivated.clear();
		for ( String name : names ) {
			GuiUtils.debug("Activating :" + name);
			GuiUtils.showView(GuiUtils.LAYER_VIEW_ID, name, IWorkbenchPage.VIEW_ACTIVATE);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		for ( Runnable r : cleanResizers ) {
			GuiUtils.run(r);
		}
		cleanResizers.clear();
		// GuiUtils.showView(GuiUtils.LAYER_VIEW_ID, new ArrayList<String>(viewsScheduledToOpen).get(0));

	}

}
