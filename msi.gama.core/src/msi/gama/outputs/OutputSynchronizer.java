package msi.gama.outputs;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import msi.gama.common.util.GuiUtils;

public class OutputSynchronizer {

	// TODO Rewrite this with locks / semaphores

	private static volatile AtomicInteger NumberOpeningViews = new AtomicInteger(0);
	private static volatile AtomicInteger NumberClosingViews = new AtomicInteger(0);

	static Set<String> viewsScheduledToOpen = new LinkedHashSet();
	static Set<String> viewsScheduledToClose = new LinkedHashSet();

	public static int getNumberOfViewsWaitingToOpen() {
		return NumberOpeningViews.get();
	}

	public static int getNumberOfViewsWaitingToClose() {
		return NumberClosingViews.get();
	}

	public static void incInitializingViews(final String view) {
		// GuiUtils.debug("GuiOutputManager.incInitializingViews: " + view);
		viewsScheduledToOpen.add(view);
		viewsScheduledToClose.add(view);
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
	}

	public static void waitForViewsToBeInitialized() {
		while (getNumberOfViewsWaitingToOpen() > 0) {
			try {
				GuiUtils.waitStatus("Initializing " + getNumberOfViewsWaitingToOpen() + " display(s)");
				if ( getNumberOfViewsWaitingToOpen() > 0 ) {
					// Workaround for OpenGL views. Necessary to "show" the view even briefly so that OpenGL can call
					// the init() method of the renderer
					final List<String> names = new ArrayList(viewsScheduledToOpen);
					GuiUtils.showView(GuiUtils.LAYER_VIEW_ID, names.get(0));
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
		for ( Runnable r : cleanResizers ) {
			GuiUtils.run(r);
		}
		cleanResizers.clear();
	}

}
