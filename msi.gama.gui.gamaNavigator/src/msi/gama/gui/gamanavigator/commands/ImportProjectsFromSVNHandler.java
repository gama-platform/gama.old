/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.gamanavigator.commands;

import java.io.*;
import java.net.*;
import java.util.List;
import msi.gama.gui.application.GUI;
import msi.gama.gui.application.svn.SVNAccess;
import msi.gama.gui.gamanavigator.GamaNavigator;
import org.eclipse.core.commands.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.tmatesoft.svn.core.SVNDirEntry;

public class ImportProjectsFromSVNHandler extends AbstractHandler {

	IWorkbenchPage	page;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Job job = new Job("Updating the Models Library") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final SVNAccess access = new SVNAccess();
				String url = "https://gama-models.googlecode.com/svn/trunk/";
				monitor.beginTask("Import library of models from " + url, 5000);
				try {
					access.createAccessToRepositoryLocation(url);
					monitor.worked(500);
					URL platformUrl = Platform.getInstanceLocation().getURL();

					String pathFolderDocs = platformUrl.getPath() + ".svn_models";
					String pathFolderSnapshots = pathFolderDocs + File.separator + "snapshots";
					File folder = new File(pathFolderSnapshots);

					if ( !folder.exists() ) {
						folder.mkdir();
					}

					DataOutputStream dos = null;

					List<SVNDirEntry> entries = access.getDocsAndSnapshotsEntries(url, monitor);
					if ( entries.isEmpty() ) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								MessageDialog
									.openError(Display.getDefault().getActiveShell(),
										"Error while updating library",
										"Check your internet connexion");
							}
						});
					}
					for ( SVNDirEntry entry : entries ) {
						String s = entry.getURL().toString();
						URL urlFile = new URL(s);
						URLConnection urlc = urlFile.openConnection();

						InputStream is = urlc.getInputStream();
						/* html file */
						if ( entry.getName().endsWith(".html") ) {
							File htmlFile =
								new File(pathFolderDocs + File.separator + entry.getName());
							dos = new DataOutputStream(new FileOutputStream(htmlFile));

							String line = "";
							StringWriter writer = new StringWriter();
							try {
								InputStreamReader streamReader = new InputStreamReader(is);
								/* The buffer for the readline */
								BufferedReader buffer = new BufferedReader(streamReader);
								try {
									writer.write("<!-- location:" + entry.getURL() + " -->");
									writer.write(line + "\n");
									while ((line = buffer.readLine()) != null) {
										writer.write(line + "\n");
									}
								} finally {
									buffer.close();
									streamReader.close();
								}
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
							dos.writeBytes(writer.toString());

							/* pic file */
						} else {
							File picFile = new File(folder + File.separator + entry.getName());
							FileOutputStream fos = new FileOutputStream(picFile);
							byte[] buffer = new byte[512];
							int n = 0;
							while (-1 != (n = is.read(buffer))) {
								fos.write(buffer, 0, n);
							}
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

				if ( !monitor.isCanceled() ) {
					monitor.done();
					return Status.OK_STATUS;
				}
				return Status.CANCEL_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();

		job.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(final IJobChangeEvent event) {
				handleJobFinished();
			}

		});

		return null;
	}

	protected void handleJobFinished() {
		final IViewPart view = page.findView("msi.gama.gui.view.GamaNavigator");
		GUI.run(new Runnable() {

			@Override
			public void run() {
				((GamaNavigator) view).getCommonViewer().refresh();
			}
		});
	}

}
