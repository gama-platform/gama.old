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
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.views.LayeredDisplayView;

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
		// GuiUtils.debug("GuiOutputManager.incInitializingViews: " + view);
		viewsScheduledToOpen.add(view);
		if ( !isPermanent ) {
			viewsScheduledToClose.add(view);
			NumberClosingViews.incrementAndGet();
		}
		viewsScheduledToBeActivated.add(0, view);
		NumberOpeningViews.incrementAndGet();

	}

	public static void decClosingViews(final String view) {
		// GuiUtils.debug("GuiOutputManager.decClosingViews: " + view);
		viewsScheduledToClose.remove(view);
		NumberClosingViews.decrementAndGet();
	}

	public static void decInitializingViews(final String view) {
		GuiUtils.debug("GuiOutputManager.decInitializingViews: " + view);
		viewsScheduledToOpen.remove(view);
		NumberOpeningViews.decrementAndGet();
		// GuiUtils.debug("Showing :" + view);
		// GuiUtils.showView(GuiUtils.LAYER_VIEW_ID, view, IWorkbenchPage.VIEW_VISIBLE);
	}

	public static void waitForViewsToBeInitialized() {
		while (getNumberOfViewsWaitingToOpen() > 0) {
			try {
				GuiUtils.waitStatus("Initializing " + getNumberOfViewsWaitingToOpen() + " display(s) :" +
					new HashSet(viewsScheduledToOpen));
				if ( getNumberOfViewsWaitingToOpen() > 0 ) {
					// Workaround for OpenGL views. Necessary to "show" the view
					// even briefly so that OpenGL can call the init() method of the renderer
					final List<String> names = new ArrayList(viewsScheduledToOpen);
					// GuiUtils.debug("Briefly showing :" + names.get(0));
					final LayeredDisplayView view = (LayeredDisplayView) GuiUtils.showView(GuiUtils.LAYER_VIEW_ID,
						names.get(0), IWorkbenchPage.VIEW_ACTIVATE);

					if ( view != null ) {
						GuiUtils.run(new Runnable() {

							@Override
							public void run() {
								view.getSurfaceComposite().getParent().layout(true, true);
							}

						});

					}
				}
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
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
		// final List<LayeredDisplayView> views = new ArrayList();
		// GuiUtils.debug("OutputSynchronizer.cleanResize called on " + names);
		if ( !GamaPreferences.CORE_DISPLAY_ORDER.getValue() ) {
			Collections.reverse(names);
		}
		viewsScheduledToBeActivated.clear();
		for ( String name : names ) {
			GuiUtils.debug("Activating :" + name);
			IGamaView view = GuiUtils.showView(GuiUtils.LAYER_VIEW_ID, name, IWorkbenchPage.VIEW_ACTIVATE);
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
					r.run();
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

	}

}
