/*********************************************************************************************
 *
 *
 * 'Application.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.internal.app.CommandLineArgs;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import msi.gama.application.projects.WorkspaceModelsManager;
import msi.gama.common.GamaPreferences;
import msi.gama.common.GamaPreferences.IPreferenceChangeListener;
import msi.gama.gui.swt.ApplicationWorkbenchAdvisor;
import msi.gama.gui.swt.dialogs.PickWorkspaceDialog;
import msi.gama.gui.views.GamaPreferencesView;
import msi.gama.runtime.GAMA;
import msi.gaml.types.IType;

/** This class controls all aspects of the application's execution */
public class Application implements IApplication {

	@Override
	public Object start(final IApplicationContext context) throws Exception {
		System.out.println(Arrays.toString(CommandLineArgs.getAllArgs()));
		// System.out.println(Platform.getProduct() == null ? "No product" : Platform.getProduct().getId() + " version " +
		// Platform.getProduct().getDefiningBundle().getVersion());

		System.err.println(
			"If you are running the developer version of GAMA, be sure to perform a clean build of your projects before launching it. Unexpected compilation errors can occur if the annotations are somehow out of sync with the code.");

		final Display display = PlatformUI.createDisplay();
		WorkspaceModelsManager.createProcessor(display);
		// OpenDocumentEventProcessor openDocProcessor = new OpenDocumentEventProcessor(display);
		// display.addListener(SWT.OpenDocument, openDocProcessor);
		// DelayedEventsProcessor delayedProcessor = new DelayedEventsProcessor(display);
		/* Fetch the Location that we will be modifying */
		Location instanceLoc = Platform.getInstanceLocation();
		if ( instanceLoc == null ) {
			// -data @none was specified but GAMA requires a workspace
			MessageDialog.openError(display.getActiveShell(),
				IDEWorkbenchMessages.IDEApplication_workspaceMandatoryTitle,
				IDEWorkbenchMessages.IDEApplication_workspaceMandatoryMessage);
			return EXIT_OK;
		}
		boolean remember = false;
		String lastUsedWs = null;
		if ( instanceLoc.isSet() ) {
			lastUsedWs = instanceLoc.getURL().getFile();
			final String ret = PickWorkspaceDialog.checkWorkspaceDirectory(Display.getDefault().getActiveShell(),
				lastUsedWs, false, false, false);
			if ( ret != null ) {
				GAMA.getGui().debug(ret);
				// remember = false;
				/* If we dont or cant remember and the location is set, we cant do anything as we need a workspace */
				MessageDialog.openError(display.getActiveShell(), "Error",
					"The workspace provided as argument cannot be used. Please change or remove it");
				PlatformUI.getWorkbench().close();
				System.exit(0);
				return IApplication.EXIT_OK;
			}
		} else {

			/* Get what the user last said about remembering the workspace location */
			remember = PickWorkspaceDialog.isRememberWorkspace();
			/* Get the last used workspace location */
			lastUsedWs = PickWorkspaceDialog.getLastSetWorkspaceDirectory();
			/* If we have a "remember" but no last used workspace, it's not much to remember */
			if ( remember && (lastUsedWs == null || lastUsedWs.length() == 0) ) {
				remember = false;
			}
			if ( remember ) {
				/*
				 * If there's any problem with the workspace, force a dialog
				 */
				final String ret = PickWorkspaceDialog.checkWorkspaceDirectory(Display.getDefault().getActiveShell(),
					lastUsedWs, false, false, false);
				if ( ret != null ) {
					if ( ret.equals("models") ) {
						remember = !MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
							"Outdated version of the models library",
							"The workspace contains an old version of the models library. Do you want to create a new workspace ?");

					} else {
						GAMA.getGui().debug(ret);
						remember = false;
					}
				}
			}
		}

		/* If we don't remember the workspace, show the dialog */
		if ( !remember ) {
			final PickWorkspaceDialog pwd = new PickWorkspaceDialog();
			final int pick = pwd.open();
			/* If the user cancelled, we can't do anything as we need a workspace */
			if ( pick == Window.CANCEL && pwd.getSelectedWorkspaceLocation() == null ) {
				MessageDialog.openError(display.getActiveShell(), "Error",
					"The application can not start without a workspace and will now exit.");
				// PlatformUI.getWorkbench().close();
				System.exit(0);
				return IApplication.EXIT_OK;
			}
			/* Tell Eclipse what the selected location was and continue */
			instanceLoc.set(new URL("file", null, pwd.getSelectedWorkspaceLocation()), false);
			if ( pwd.applyPrefs() ) {
				pwd.applyEclipsePreferences(pwd.getSelectedWorkspaceLocation());
			}
		} else {
			if ( !instanceLoc.isSet() ) {
				/* Set the last used location and continue */
				instanceLoc.set(new URL("file", null, lastUsedWs), false);
			}

		}

		final int memory = readMaxMemoryInMegabytes();
		if ( memory > 0 ) {
			final GamaPreferences.Entry<Integer> p = GamaPreferences
				.create("core_max_memory", "Maximum memory allocated to GAMA in megabytes", memory, IType.INT)
				.in(GamaPreferences.EXPERIMENTAL).group("Memory (restart GAMA for it to take effect)");
			p.addChangeListener(new IPreferenceChangeListener<Integer>() {

				@Override
				public boolean beforeValueChange(final Integer newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final Integer newValue) {
					changeMaxMemory(newValue);
					GamaPreferencesView.setRestartRequired();
				}
			});
		}

		try {
			final int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if ( returnCode == PlatformUI.RETURN_RESTART ) { return IApplication.EXIT_RESTART; }
			return IApplication.EXIT_OK;
		} finally {
			if ( display != null ) {
				display.dispose();
			}
			instanceLoc = Platform.getInstanceLocation();
			if ( instanceLoc != null ) {
				instanceLoc.release();
			}
		}

	}

	@Override
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if ( workbench == null ) { return; }
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				if ( !display.isDisposed() ) {
					workbench.close();
				}
			}
		});
	}

	public static int readMaxMemoryInMegabytes() {
		String loc;
		try {
			loc = Platform.getConfigurationLocation().getURL().getPath();
			File dir = new File(loc);
			dir = dir.getParentFile();
			final File ini = new File(dir.getAbsolutePath() + "/Gama.ini");
			System.out.println("ini file " + ini.getAbsolutePath());
			if ( ini.exists() ) {
				try (final FileInputStream stream = new FileInputStream(ini);
					final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));) {
					String s = reader.readLine();
					while (s != null) {
						if ( s.startsWith("-Xmx") ) {
							final char last = s.charAt(s.length() - 1);
							double divider = 1000000;
							boolean unit = false;
							switch (last) {
								case 'k':
								case 'K':
									unit = true;
									divider = 1000;
									break;
								case 'm':
								case 'M':
									unit = true;
									divider = 1;
									break;
								case 'g':
								case 'G':
									unit = true;
									divider = 0.001;
									break;
							}
							String trim = s;
							trim = trim.replace("-Xmx", "");
							if ( unit )
								trim = trim.substring(0, trim.length() - 1);
							final int result = Integer.parseInt(trim);
							return (int) (result / divider);

						}
						s = reader.readLine();
					}
				}
			}
		} catch (final IOException e) {}
		return 0;

	}

	public static void changeMaxMemory(final int memory) {
		final int mem = memory < 128 ? 128 : memory;
		String loc;
		try {
			loc = Platform.getConfigurationLocation().getURL().getPath();
			File dir = new File(loc);
			dir = dir.getParentFile();
			final File ini = new File(dir.getAbsolutePath() + "/Gama.ini");
			final List<String> contents = new ArrayList();
			if ( ini.exists() ) {
				try (final FileInputStream stream = new FileInputStream(ini);
					final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));) {
					String s = reader.readLine();
					while (s != null) {
						if ( s.startsWith("-Xmx") ) {
							s = "-Xmx" + mem + "m";
						}
						contents.add(s);
						s = reader.readLine();
					}
				}
				try (final FileOutputStream os = new FileOutputStream(ini);
					final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));) {
					for ( final String line : contents ) {
						writer.write(line);
						writer.newLine();
					}
					writer.flush();
				}
			}
		} catch (final IOException e) {}

	}

}
