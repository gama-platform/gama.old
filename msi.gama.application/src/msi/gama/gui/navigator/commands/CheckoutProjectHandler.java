/*********************************************************************************************
 * 
 * 
 * 'CheckoutProjectHandler.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator.commands;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.navigator.*;
import msi.gama.gui.svn.SVNAccess;
import org.eclipse.core.commands.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.tmatesoft.svn.core.SVNURL;

public class CheckoutProjectHandler extends AbstractHandler {

	IWorkbenchPage page;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		/* Get the selection */
		ISelection activeSelection =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		final StructuredSelection currentSelection =
			new StructuredSelection(((TreeSelection) activeSelection).toArray());

		Job job = new Job("Checkout project ..") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("Please wait...", IProgressMonitor.UNKNOWN);

				/* Get the file */
				SVNProject fb = (SVNProject) ((IStructuredSelection) currentSelection).getFirstElement();
				File f = fb.getFile();
				try {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					try {
						String s = reader.readLine();
						int beginIndex = s.indexOf(":") + 1;
						int endIndex = s.indexOf("/doc");
						String urlRepo = s.substring(beginIndex, endIndex);
						String projectName = urlRepo.substring(urlRepo.lastIndexOf("/") + 1);
						monitor.setTaskName("Project " + projectName);
						final SVNAccess access = new SVNAccess();
						SVNURL repositoryURL = access.createAccessToRepositoryLocation(urlRepo);

						URL url = Platform.getInstanceLocation().getURL();
						String projectPath = url.getPath() + projectName;

						File file = new File(projectPath);

						boolean success = access.checkoutProjectFromSVN(repositoryURL, file, true);
						if ( !success ) { return Status.CANCEL_STATUS; }

						/*
						 * parcours des fils pour trouver le dot file et creer le lien vers le
						 * projet
						 */
						File dotFile = null;
						File[] children = file.listFiles();
						for ( int i = 0; i < children.length; i++ ) {
							if ( children[i].getName().equals(".project") ) {
								dotFile = children[i];
							}
						}
						// TODO use existing methods instead of hard coded
						// TODO tag svn doesn't show up every time
						IProjectDescription tempDescription = null;
						final IWorkspace workspace = ResourcesPlugin.getWorkspace();
						/* If the '.project' doesn't exists we create one */
						if ( dotFile == null ) {
							/* Initialize file content */
							FileWriter writer = null;
							try {
								dotFile = new File(file.getPath() + "/.project");
								writer = new FileWriter(dotFile);
								StringBuilder str = new StringBuilder();
								str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
								str.append("<projectDescription>");
								str.append("<name>" + projectName + "</name>");
								str.append("<comment></comment>");
								str.append("<projects>");
								str.append("</projects>");
								str.append("<buildSpec><buildCommand><name>org.eclipse.xtext.ui.shared.xtextBuilder</name><arguments></arguments></buildCommand></buildSpec>");
								str.append("<natures>");
								str.append("<nature>org.eclipse.xtext.ui.shared.xtextNature</nature>");
								str.append("<nature>msi.gama.application.gamaNature</nature>");
								str.append("</natures>");
								str.append("</projectDescription>");
								writer.write(str.toString());
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								if ( writer != null ) {
									writer.close();
								}
							}
						} else {
							final IPath location = new Path(dotFile.getAbsolutePath());
							try {
								tempDescription = workspace.loadProjectDescription(location);
							} catch (CoreException e) {
								e.printStackTrace();
							}
						}
						final IProjectDescription description = tempDescription;
						final IProject proj = workspace.getRoot().getProject(projectName);

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

					} finally {
						reader.close();
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
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				((GamaNavigator) view).getCommonViewer().refresh();
			}
		});
	}

	private void setValuesProjectDescription(final IProject proj) {
		/* Modify the project description */
		IProjectDescription desc = null;
		try {
			desc = proj.getDescription();
			/* Associate GamaNature et xtext nature to the project */
			// String[] ids = desc.getNatureIds();
			String[] newIds = new String[2];
			// System.arraycopy(ids, 0, newIds, 0, ids.length);
			newIds[1] = "msi.gama.application.gamaNature";
			newIds[0] = "org.eclipse.xtext.ui.shared.xtextNature";
			desc.setNatureIds(newIds);
			proj.setDescription(desc, IResource.FORCE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
