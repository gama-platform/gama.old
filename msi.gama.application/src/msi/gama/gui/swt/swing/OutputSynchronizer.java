/*********************************************************************************************
 *
 *
 * 'OutputSynchronizer.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.swing;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.ui.IWorkbenchPage;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.GAMA;

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

	public static void incInitializingViews(final String view, final boolean isPermanent) {
		// scope.getGui().debug("GuiOutputManager.incInitializingViews: " + view);
		viewsScheduledToOpen.add(view);
		if ( !isPermanent ) {
			viewsScheduledToClose.add(view);
			NumberClosingViews.incrementAndGet();
		}
		viewsScheduledToBeActivated.add(0, view);
		NumberOpeningViews.incrementAndGet();

	}

	public static void decClosingViews(final String view) {
		// scope.getGui().debug("GuiOutputManager.decClosingViews: " + view);
		viewsScheduledToClose.remove(view);
		NumberClosingViews.decrementAndGet();
	}

	public static void decInitializingViews(final String view) {
		GAMA.getGui().debug("GuiOutputManager.decInitializingViews: " + view);
		viewsScheduledToOpen.remove(view);
		NumberOpeningViews.decrementAndGet();
		// scope.getGui().debug("Showing :" + view);
		// scope.getGui().showView(scope.getGui().LAYER_VIEW_ID, view, IWorkbenchPage.VIEW_VISIBLE);
	}

	public static void waitForViewsToBeInitialized() {
		while (getNumberOfViewsWaitingToOpen() > 0) {
			try {
				GAMA.getGui().waitStatus("Initializing " + getNumberOfViewsWaitingToOpen() + " display(s) :" +
					new HashSet(viewsScheduledToOpen));
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		cleanResize();
	}

	public static void waitForViewsToBeClosed() {
		while (getNumberOfViewsWaitingToClose() > 0) {
			GAMA.getGui().waitStatus("Closing previous displays");
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
		// final List<LayeredDisplayView> views = new ArrayList();
		// scope.getGui().debug("OutputSynchronizer.cleanResize called on " + names);
		if ( !GamaPreferences.CORE_DISPLAY_ORDER.getValue() ) {
			Collections.reverse(names);
		}
		viewsScheduledToBeActivated.clear();
		for ( String name : names ) {
			GAMA.getGui().debug("Activating :" + name);
			// GAMA.getGui();
			IGamaView view = GAMA.getGui().showView(IGui.LAYER_VIEW_ID, name, IWorkbenchPage.VIEW_ACTIVATE);
			// if ( view instanceof LayeredDisplayView ) {
			// views.add((LayeredDisplayView) view);
			// }
			// try {
			// Thread.sleep(100);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
		}

		// AD 17/01/16: For the moment, this runnable is ... not run. Not sure it is necessary anymore.

		Runnable job;
		job = new Runnable() {

			@Override
			public void run() {
				for ( Runnable r : cleanResizers ) {
					GAMA.getGui().asyncRun(r);
					// r.run();
				}
				cleanResizers.clear();
				boolean allRealized = false;
				while (!allRealized) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					allRealized = true;
					// for ( LayeredDisplayView view : views ) {
					// if ( !view.isRealized() ) {
					// allRealized = false;
					// break;
					// }
					// }
				}

			}

		};
		job.run();

	}

}
