/*******************************************************************************************************
 *
 * Application.java, in msi.gama.application, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.application;

import static java.lang.System.setProperty;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;
import static msi.gama.application.workspace.WorkspacePreferences.checkWorkspaceDirectory;
import static msi.gama.application.workspace.WorkspacePreferences.getLastSetWorkspaceDirectory;
import static msi.gama.application.workspace.WorkspacePreferences.getSelectedWorkspaceRootLocation;
import static msi.gama.application.workspace.WorkspacePreferences.isRememberWorkspace;
import static org.eclipse.e4.ui.workbench.IWorkbench.CLEAR_PERSISTED_STATE;
import static org.eclipse.jface.dialogs.MessageDialog.openConfirm;
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;
import static org.eclipse.ui.PlatformUI.RETURN_RESTART;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.PlatformUI.isWorkbenchRunning;
import static org.eclipse.ui.internal.util.PrefUtil.getInternalPreferenceStore;
import static org.eclipse.ui.internal.util.PrefUtil.saveInternalPrefs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.ide.application.DelayedEventsProcessor;

import msi.gama.application.workbench.ApplicationWorkbenchAdvisor;
import msi.gama.application.workspace.PickWorkspaceDialog;
import msi.gama.application.workspace.WorkspaceModelsManager;
import msi.gama.application.workspace.WorkspacePreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.FLAGS;

/** This class controls all aspects of the application's execution */
public class Application implements IApplication {

	static {
		DEBUG.OFF();
	}

	/** The processor. */
	private static OpenDocumentEventProcessor OPEN_DOCUMENT_PROCESSOR;

	/** The Constant CLEAR_WORKSPACE. */
	public static final String CLEAR_WORKSPACE = "clearWorkspace";

	/**
	 * Clear workspace.
	 *
	 * @param clear
	 *            the clear
	 */
	public static void clearWorkspace(final boolean clear) {
		getInternalPreferenceStore().setValue(CLEAR_WORKSPACE, Boolean.toString(clear));
		saveInternalPrefs();
	}

	/**
	 * The Class OpenDocumentEventProcessor.
	 */
	public static class OpenDocumentEventProcessor extends DelayedEventsProcessor {

		/**
		 * Instantiates a new open document event processor.
		 *
		 * @param display
		 *            the display
		 */
		OpenDocumentEventProcessor(final Display display) {
			super(display);
		}

		/** The files to open. */
		private final ArrayList<String> filesToOpen = new ArrayList<>(1);

		@Override
		public void handleEvent(final Event event) {
			if (event.text != null) {
				filesToOpen.add(event.text);
				DEBUG.OUT("RECEIVED FILE TO OPEN: " + event.text);
			}
		}

		@Override
		public void catchUp(final Display display) {
			if (filesToOpen.isEmpty()) return;
			final String[] filePaths = filesToOpen.toArray(new String[filesToOpen.size()]);
			filesToOpen.clear();
			for (final String path : filePaths) { WorkspaceModelsManager.instance.openModelPassedAsArgument(path); }
		}
	}

	/**
	 * Creates the processor.
	 *
	 * @param display2
	 */
	public static void createProcessor(final Display display) {
		// // DEBUG.OUT("System property swt.autoScale = " + System.getProperty("swt.autoScale"));
		// System.setProperty("swt.autoScale", FLAGS.USE_PRECISE_SCALING ? "quarter" : "integer"); // cf DPIUtil
		// // DEBUG.OUT("System property sun.java2d.uiScale.enabled = " +
		// System.getProperty("sun.java2d.uiScale.enabled"),
		// // false);
		// System.setProperty("sun.java2d.uiScale.enabled", String.valueOf(DPIUtil.getDeviceZoom() > 100));
		// // DEBUG.OUT(" -- changed to = " + System.getProperty("sun.java2d.uiScale.enabled"));
		if (display == null) return;
		OPEN_DOCUMENT_PROCESSOR = new OpenDocumentEventProcessor(display);
	}

	@Override
	public Object start(final IApplicationContext context) throws Exception {
		FLAGS.load();
		setDefaultUncaughtExceptionHandler((t, e) -> {
			if (e instanceof OutOfMemoryError) {
				final boolean close = openConfirm(null, "Out of memory",
						"GAMA is out of memory and will likely crash. Do you want to close now ?");
				if (close) { this.stop(); }
				e.printStackTrace();
			} else {
				DEBUG.ERR("Exception in Application", e);
			}

		});
		final Display display = configureDisplay();
		Object check = Display.getCurrent().syncCall(Application::checkWorkspace);
		if (!EXIT_OK.equals(check)) {
			try {
				createProcessor(display);
				if (getInternalPreferenceStore().getBoolean(CLEAR_WORKSPACE)) {
					setProperty(CLEAR_PERSISTED_STATE, "true");
					clearWorkspace(false);
				}
				try {
					final int returnCode = Workbench.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
					if (returnCode == RETURN_RESTART) return EXIT_RESTART;
				} catch (Exception t) {
					DEBUG.ERR("Error in application", t);
				}
			} finally {
				if (display != null) { display.dispose(); }
				final Location instanceLoc = Platform.getInstanceLocation();
				if (instanceLoc != null) { instanceLoc.release(); }
			}
		}
		return EXIT_OK;

	}

	/**
	 * Configure display. Issues #3596 #3308
	 *
	 * @return the display
	 */
	private Display configureDisplay() {
		// Important to do it *before* creating the display
		System.setProperty("swt.autoScale", FLAGS.USE_PRECISE_SCALING ? "quarter" : "integer"); // cf DPIUtil
		final Display display = PlatformUI.createDisplay();
		Display.setAppName("Gama Platform");
		Display.setAppVersion(GAMA.VERSION_NUMBER);

		DEBUG.LOG(DEBUG.PAD("> GAMA: Device zoom ", 45, ' ') + DEBUG.PAD(" set to", 15, '_') + " "
				+ DPIUtil.getDeviceZoom() + "%");
		DEBUG.LOG(DEBUG.PAD("> GAMA: Monitor zoom ", 45, ' ') + DEBUG.PAD(" set to", 15, '_') + " "
				+ display.getPrimaryMonitor().getZoom() + "%");

		// boolean isWindows = PlatformHelper.isWindows();

		// boolean hasHiDPI = DPIUtil.getDeviceZoom() > 100;
		// boolean hasCustomZoom = isWindows && display.getPrimaryMonitor().getZoom() > 100;
		//
		// System.setProperty("sun.java2d.uiScale.enabled", String.valueOf(!hasHiDPI && hasCustomZoom));
		return display;
	}

	/**
	 * Check workspace.
	 *
	 * @return the object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws MalformedURLException
	 *             the malformed URL exception
	 */
	public static Object checkWorkspace() throws IOException {
		final Location instanceLoc = Platform.getInstanceLocation();
		if (instanceLoc == null) {
			// -data @none was specified but GAMA requires a workspace
			openError(null, IKeyword.ERROR, "A workspace is required to run GAMA");
			return EXIT_OK;
		}
		boolean remember = false;
		String lastUsedWs = null;
		if (instanceLoc.isSet()) {
			lastUsedWs = instanceLoc.getURL().getFile();
			final String ret = WorkspacePreferences.checkWorkspaceDirectory(lastUsedWs, false, false, false);
			if (ret != null) {
				// if ( ret.equals("Restart") ) { return EXIT_RESTART; }
				/* If we dont or cant remember and the location is set, we cant do anything as we need a workspace */
				openError(null, IKeyword.ERROR, "The workspace provided cannot be used. Please change it");
				if (isWorkbenchRunning()) { getWorkbench().close(); }
				System.exit(0);
				return EXIT_OK;
			}
		} else {

			/* Get what the user last said about remembering the workspace location */
			remember = isRememberWorkspace();
			/* Get the last used workspace location */
			lastUsedWs = getLastSetWorkspaceDirectory();
			/* If we have a "remember" but no last used workspace, it's not much to remember */
			if (remember && (lastUsedWs == null || lastUsedWs.length() == 0)) { remember = false; }
			if (remember) {
				/*
				 * If there's any problem with the workspace, force a dialog
				 */
				final String ret = checkWorkspaceDirectory(lastUsedWs, false, false, false);
				// AD Added this check explicitly as the checkWorkspaceDirectory() was not supposed to return null at
				// this stage
				if (ret != null) {
					remember = "models".equals(ret) && !openQuestion(null, "Different version of the models library",
							"The workspace contains a different version of the models library. Do you want to use another workspace ?");
				}
			}
		}

		/* If we don't remember the workspace, show the dialog */
		if (!remember) {
			final int pick = new PickWorkspaceDialog(true).open();
			/* If the user cancelled, we can't do anything as we need a workspace */
			String wr = getSelectedWorkspaceRootLocation();
			if (pick == 1 /* Window.CANCEL */ || wr == null) {
				openError(null, IKeyword.ERROR, "GAMA can not start without a workspace and will now exit.");
				// System.exit(0);
				return IApplication.EXIT_OK;
			}
			/* Tell Eclipse what the selected location was and continue */
			instanceLoc.set(new URL("file", null, wr), false);
			// if ( applyPrefs() ) { applyEclipsePreferences(getSelectedWorkspaceRootLocation()); }
		} else if (!instanceLoc.isSet()) {
			/* Set the last used location and continue */
			instanceLoc.set(new URL("file", null, lastUsedWs), false);
		}

		return null;
	}

	@Override
	public void stop() {
		final IWorkbench workbench = getWorkbench();
		if (workbench == null) return;
		final Display display = workbench.getDisplay();
		display.syncExec(() -> { if (!display.isDisposed()) { workbench.close(); } });
	}

	/**
	 * Gets the open document processor.
	 *
	 * @return the open document processor
	 */
	public static OpenDocumentEventProcessor getOpenDocumentProcessor() { return OPEN_DOCUMENT_PROCESSOR; }

}
