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

	public static void incInitializingViews(String view) {
		GuiUtils.debug("GuiOutputManager.incInitializingViews: " + view);
		viewsScheduledToOpen.add(view);
		viewsScheduledToClose.add(view);
		NumberOpeningViews.incrementAndGet();
		NumberClosingViews.incrementAndGet();
	}

	public static void decClosingViews(String view) {
		GuiUtils.debug("GuiOutputManager.decClosingViews: " + view);
		viewsScheduledToClose.remove(view);
		NumberClosingViews.decrementAndGet();
	}

	public static void decInitializingViews(String view) {
		GuiUtils.debug("GuiOutputManager.decInitializingViews: " + view);
		viewsScheduledToOpen.remove(view);
		List<String> names = new ArrayList(viewsScheduledToOpen);
		NumberOpeningViews.decrementAndGet();
		if ( getNumberOfViewsWaitingToOpen() > 0 ) {
			GuiUtils.showView(GuiUtils.LAYER_VIEW_ID, names.get(0));
		}
	}

	public static void waitForViewsToBeInitialized() {
		while (getNumberOfViewsWaitingToOpen() > 0) {
			try {
				GuiUtils.waitStatus("Initializing " + getNumberOfViewsWaitingToOpen() + " display(s)");
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO What to do when interrupted ?
				e.printStackTrace();
			}
		}
	}

	public static void waitForViewsToBeClosed() {
		while (getNumberOfViewsWaitingToClose() > 0) {
			GuiUtils.waitStatus("Closing previous displays");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
