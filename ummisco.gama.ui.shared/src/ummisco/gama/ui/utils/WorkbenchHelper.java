/*********************************************************************************************
 *
 * 'WorkbenchHelper.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.utils;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import msi.gama.application.workspace.WorkspaceModelsManager;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.preferences.GamaPreferences;
import ummisco.gama.ui.views.IGamlEditor;

public class WorkbenchHelper {

	private static final Object NULL = new Object();

	public final static LoadingCache<Class<?>, Object> SERVICES =
			CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Object>() {

				@Override
				public Object load(final Class<?> key) throws Exception {
					final Object o = getWorkbench().getService(key);
					if (o == null)
						return NULL;
					return o;
				}
			});

	public final static String GAMA_NATURE = WorkspaceModelsManager.GAMA_NATURE;
	public final static String XTEXT_NATURE = WorkspaceModelsManager.XTEXT_NATURE;
	public final static String PLUGIN_NATURE = WorkspaceModelsManager.PLUGIN_NATURE;
	public final static String TEST_NATURE = WorkspaceModelsManager.TEST_NATURE;
	public final static String BUILTIN_NATURE = WorkspaceModelsManager.BUILTIN_NATURE;

	public final static Clipboard CLIPBOARD = new Clipboard(getDisplay());
	public final static Transfer[] TRANSFERS = new Transfer[] { TextTransfer.getInstance() };

	public static void asyncRun(final Runnable r) {
		final Display d = getDisplay();
		if (d != null && !d.isDisposed()) {
			d.asyncExec(r);
		} else
			r.run();
	}

	public static void run(final Runnable r) {
		final Display d = getDisplay();
		if (d != null && !d.isDisposed()) {
			d.syncExec(r);
		} else
			r.run();
	}

	public static Display getDisplay() {
		return getWorkbench().getDisplay();
	}

	public static IWorkbenchPage getPage() {
		final IWorkbenchWindow w = getWindow();
		if (w == null) { return null; }
		final IWorkbenchPage p = w.getActivePage();
		return p;
	}

	public static IWorkbenchPage getPage(final String perspectiveId) {
		IWorkbenchPage p = getPage();
		if (p == null && perspectiveId != null) {
			try {
				p = getWindow().openPage(perspectiveId, null);

			} catch (final WorkbenchException e) {
				e.printStackTrace();
			}
		}
		return p;
	}

	public static Shell getShell() {

		return getDisplay().getActiveShell();
	}

	public static IWorkbenchWindow getWindow() {
		final IWorkbenchWindow w = getWorkbench().getActiveWorkbenchWindow();

		if (w == null) {
			final IWorkbenchWindow[] windows = getWorkbench().getWorkbenchWindows();
			if (windows != null && windows.length > 0) { return windows[0]; }
		}
		return w;
	}

	public static IGamlEditor getActiveEditor() {
		final IWorkbenchPage page = getPage();
		if (page != null) {
			final IEditorPart editor = page.getActiveEditor();
			if (editor instanceof IGamlEditor)
				return (IGamlEditor) editor;
		}
		return null;
	}

	public static IWorkbenchPart getActivePart() {
		final IWorkbenchPage page = getPage();
		if (page != null) { return page.getActivePart(); }
		return null;
	}

	public static IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}

	public static IViewPart findView(final String id, final String second, final boolean restore) {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) { return null; } // Closing the workbench
		final IViewReference ref = page.findViewReference(id, second);
		if (ref == null) { return null; }
		final IViewPart part = ref.getView(restore);
		return part;
	}

	public static void setWorkbenchWindowTitle(final String title) {
		run(() -> {
			if (WorkbenchHelper.getShell() != null)
				WorkbenchHelper.getShell().setText(title);
		});

	}

	public static void hideView(final String id) {

		run(() -> {
			final IWorkbenchPage activePage = getPage();
			if (activePage == null) { return; } // Closing the workbench
			final IWorkbenchPart part = activePage.findView(id);
			if (part != null && activePage.isPartVisible(part)) {
				activePage.hideView((IViewPart) part);
			}
		});

	}

	public static void hideView(final IViewPart gamaViewPart) {
		final IWorkbenchPage activePage = getPage();
		if (activePage == null) { return; } // Closing the workbenc
		activePage.hideView(gamaViewPart);

	}

	@SuppressWarnings ("unchecked")
	public static <T> T getService(final Class<T> class1) {
		final Object o = SERVICES.getUnchecked(class1);
		if (o == NULL) {
			SERVICES.invalidate(class1);
			return null;
		}
		return (T) o;
	}

	public static void copy(final String o) {
		CLIPBOARD.setContents(new String[] { o }, TRANSFERS);
	}

	/**
	 * @todo find a more robust way to find the view (maybe with the control ?)
	 * @return
	 */
	public static IViewPart findGamaViewUnderMouse() {
		final IWorkbenchPage page = getPage();
		if (page == null) { return null; }
		final Point p = getDisplay().getCursorLocation();
		final java.awt.geom.Point2D p2D = new Point2D.Double(p.x, p.y);
		for (final IViewPart part : page.getViews()) {
			if (part instanceof IGamaView) {
				final Rectangle2D r = ((IGamaView) part).getBounds();
				if (r.contains(p2D))
					return part;
				else if (part instanceof IGamaView.Display) {
					if (((IGamaView.Display) part).isFullScreen())
						return part;
				}
			}
		}
		return null;
	}

	public static Shell obtainFullScreenShell(int monitorId) {
		final Monitor[] monitors = WorkbenchHelper.getDisplay().getMonitors();

		if (monitorId < 0)
			monitorId = 0;
		if (monitorId > monitors.length - 1)
			monitorId = monitors.length - 1;
		final Rectangle bounds = monitors[monitorId].getBounds();

		Shell fullScreenShell =
				new Shell(WorkbenchHelper.getDisplay(), (GamaPreferences.Displays.DISPLAY_MODAL_FULLSCREEN.getValue()
						? SWT.ON_TOP | SWT.SYSTEM_MODAL : SWT.APPLICATION_MODAL) | SWT.NO_TRIM);
		fullScreenShell.setBounds(bounds);
		if (GamaPreferences.Displays.DISPLAY_NATIVE_FULLSCREEN.getValue()) {
			fullScreenShell = new Shell(SWT.NO_TRIM | SWT.ON_TOP);
			fullScreenShell.setMaximized(true);
			fullScreenShell.setBounds(bounds);
			fullScreenShell.setFullScreen(true);
		}
		final GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		fullScreenShell.setLayout(gl);
		return fullScreenShell;
	}

	public static Rectangle displaySizeOf(final Control composite) {
		final Rectangle[] result = new Rectangle[1];
		run(() -> result[0] = getDisplay().map(composite, null, composite.getBounds()));
		return result[0];
	}

}
