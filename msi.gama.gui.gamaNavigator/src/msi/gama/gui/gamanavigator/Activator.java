/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gama.gui.gamanavigator;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

// import java.io.BufferedReader;
// import java.io.DataOutputStream;
// import java.io.FileOutputStream;
// import java.io.InputStream;
// import java.io.InputStreamReader;
// import java.io.StringWriter;
// import java.net.URLConnection;
// import java.util.List;
// import msi.gama.gui.application.svn.SVNAccess;
// import org.eclipse.core.runtime.IStatus;
// import org.eclipse.core.runtime.Platform;
// import org.eclipse.core.runtime.Status;
// import org.eclipse.core.runtime.jobs.Job;
// import org.eclipse.ui.IWorkbenchPage;
// import org.tmatesoft.svn.core.SVNDirEntry;
// import org.tmatesoft.svn.core.SVNException;

/** The activator class controls the plug-in life cycle */
public class Activator extends AbstractUIPlugin {

	/* The plug-in ID */
	public static final String PLUGIN_ID = "msi.gama.gui.application.GamaNavigator";

	IViewPart view;

	/* The shared instance */
	private static Activator plugin;

	/** The constructor */
	public Activator() {}

	@Override
	public void start(final BundleContext context) throws Exception {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// ResourcesPlugin.getPlugin().getPluginPreferences()
		// .setValue(ResourcesPlugin.PREF_AUTO_REFRESH, true);
		// PlatformUI.getPreferenceStore().setValue(ResourcesPlugin.PREF_AUTO_REFRESH, true);
		super.start(context);
		plugin = this;

		// TODO dirty.. find another way to do that
		if ( workspace.getRoot().getProjects().length == 0 ) {
			linkSampleModelsToWorkspace(workspace);
		}

		// checkoutSVNModelsLibrary();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static Image getImage(final String imagePath) {
		ImageDescriptor imageDescriptor =
			AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, imagePath);
		Image image = imageDescriptor.createImage();

		return image;
	}

	private void linkSampleModelsToWorkspace(final IWorkspace workspace) {
		URL urlRep = null;
		try {
			urlRep =
				FileLocator.toFileURL(new URL("platform:/plugin/msi.gama.gui.application/models/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		File modelsRep = new File(urlRep.getPath());
		FileBean gFile = new FileBean(modelsRep);
		FileBean[] projects = gFile.getChildren();
		for ( FileBean project : projects ) {
			File dotFile = null;
			/* parcours des fils pour trouver le dot file et creer le lien vers le projet */
			FileBean[] children = project.getChildrenWithHiddenFiles();
			for ( int i = 0; i < children.length; i++ ) {
				if ( children[i].toString().equals(".project") ) {
					dotFile = new File(children[i].getPath());
				}
			}
			IProjectDescription tempDescription = null;
			/* If the '.project' doesn't exists we create one */
			if ( dotFile == null ) {
				/* Initialize file content */
				tempDescription = setProjectDescription(project);
			} else {
				final IPath location = new Path(dotFile.getAbsolutePath());
				try {
					tempDescription = workspace.loadProjectDescription(location);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			final IProjectDescription description = tempDescription;

			final IProject proj = workspace.getRoot().getProject(project.toString());
			WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

				@Override
				protected void execute(final IProgressMonitor monitor) throws CoreException,
					InvocationTargetException, InterruptedException {
					if ( !proj.exists() ) {
						proj.create(description, monitor);
					}
					proj.open(IResource.BACKGROUND_REFRESH, monitor);
				}
			};
			try {
				operation.run(null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			setValuesProjectDescription(proj);
		}
	}

	private void setValuesProjectDescription(final IProject proj) {
		/* Modify the project description */
		IProjectDescription desc = null;
		try {
			desc = proj.getDescription();
			/* Associate GamaNature et xtext nature to the project */
			String[] ids = desc.getNatureIds();
			String[] newIds = new String[ids.length + 2];
			System.arraycopy(ids, 0, newIds, 0, ids.length);
			newIds[ids.length] = "msi.gama.gui.application.gamaNature";
			newIds[ids.length + 1] = "org.eclipse.xtext.ui.shared.xtextNature";
			desc.setNatureIds(newIds);
			proj.setDescription(desc, IResource.FORCE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private IProjectDescription setProjectDescription(final FileBean project) {
		final IProjectDescription description =
			ResourcesPlugin.getWorkspace().newProjectDescription(project.toString());
		final IPath location = new Path(project.getPath());
		description.setLocation(location);
		return description;
	}

	// private void checkoutSVNModelsLibrary() {
	// IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	// final IViewPart view = page.findView("msi.gama.gui.view.GamaNavigator");
	// Job job = new Job("Updating the Models Library") {
	// @Override
	// protected IStatus run(final IProgressMonitor monitor) {
	// final SVNAccess access = new SVNAccess();
	// String url = "https://gama-models.googlecode.com/svn/trunk/";
	// monitor.beginTask("Import library of models from " + url, 5000);
	// try {
	// access.createAccessToRepositoryLocation(url);
	// monitor.worked(500);
	// URL platformUrl = Platform.getInstanceLocation().getURL();
	//
	// String pathFolderDocs = platformUrl.getPath() + ".svn_models";
	// String pathFolderSnapshots = pathFolderDocs + File.separator + "snapshots";
	// File folder = new File(pathFolderSnapshots);
	//
	// if (!folder.exists())
	// folder.mkdir();
	//
	// DataOutputStream dos = null;
	//
	// List<SVNDirEntry> entries = access.getDocsAndSnapshotsEntries(url, monitor);
	// for (SVNDirEntry entry : entries) {
	// String s = entry.getURL().toString();
	// URL urlFile = new URL(s);
	// URLConnection urlc = urlFile.openConnection();
	//
	// InputStream is = urlc.getInputStream();
	// /* html file */
	// if (entry.getName().endsWith(".html")) {
	// File htmlFile = new File(pathFolderDocs + File.separator
	// + entry.getName());
	// dos = new DataOutputStream(new FileOutputStream(htmlFile));
	//
	// String line = "";
	// StringWriter writer = new StringWriter();
	// try {
	// InputStreamReader streamReader = new InputStreamReader(is);
	// /* The buffer for the readline */
	// BufferedReader buffer = new BufferedReader(streamReader);
	// try {
	// writer.write("<!-- location:" + entry.getURL() + " -->");
	// writer.write(line + "\n");
	// while ((line = buffer.readLine()) != null) {
	// writer.write(line + "\n");
	// }
	// } finally {
	// buffer.close();
	// streamReader.close();
	// }
	// } catch (IOException ioe) {
	// ioe.printStackTrace();
	// }
	// dos.writeBytes(writer.toString());
	//
	// /* pic file */
	// } else {
	// File picFile = new File(folder + File.separator + entry.getName());
	// FileOutputStream fos = new FileOutputStream(picFile);
	// byte[] buffer = new byte[512];
	// int n = 0;
	// while (-1 != (n = is.read(buffer))) {
	// fos.write(buffer, 0, n);
	// }
	//
	// }
	// }
	//
	// } catch (SVNException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// if (!monitor.isCanceled()) {
	// monitor.done();
	// PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
	// @Override
	// public void run() {
	// ((GamaNavigator) view).getCommonViewer().refresh();
	// }
	// });
	// return Status.OK_STATUS;
	// } else {
	// PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
	// @Override
	// public void run() {
	// ((GamaNavigator) view).getCommonViewer().refresh();
	// }
	// });
	// return Status.CANCEL_STATUS;
	// }
	// }
	//
	// };
	// job.setUser(true);
	// job.schedule();
	// }
}
