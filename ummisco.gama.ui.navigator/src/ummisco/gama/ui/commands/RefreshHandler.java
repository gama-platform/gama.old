/*********************************************************************************************
 *
 * 'RefreshHandler.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.core.resources.IResource.PROJECT;
import static org.eclipse.core.resources.IResource.ROOT;
import static ummisco.gama.ui.navigator.contents.ResourceManager.getInstance;
import static ummisco.gama.ui.utils.WorkbenchHelper.runInUI;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;

import msi.gama.application.workspace.WorkspaceModelsManager;
import msi.gama.common.interfaces.IGui;
import msi.gama.runtime.GAMA;
import msi.gama.util.file.IFileMetaDataProvider;
import ummisco.gama.ui.interfaces.IRefreshHandler;
import ummisco.gama.ui.metadata.FileMetaDataProvider;
import ummisco.gama.ui.navigator.GamaNavigator;
import ummisco.gama.ui.navigator.contents.NavigatorRoot;
import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class RefreshHandler implements IRefreshHandler {

	GamaNavigator navigator;

	private GamaNavigator getNavigator() {
		if (navigator == null) {
			final IWorkbenchPage page = WorkbenchHelper.getPage();
			if (page != null) {
				navigator = (GamaNavigator) page.findView(IGui.NAVIGATOR_VIEW_ID);
			}
		}
		return navigator;
	}
	//

	@Override
	public void refreshNavigator() {
		WorkbenchHelper.run(() -> getNavigator().getCommonViewer().refresh());
	}

	protected void simpleRefresh(final IResource resource, final IProgressMonitor monitor) throws CoreException {
		if (resource.getType() == IResource.PROJECT) {
			checkLocationDeleted((IProject) resource);
		} else if (resource.getType() == IResource.ROOT) {
			final IProject[] projects = ((IWorkspaceRoot) resource).getProjects();
			for (final IProject project : projects) {
				checkLocationDeleted(project);
			}
		}
		resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	@Override
	public void refreshResource(final IResource resource) {
		if (resource.getType() == PROJECT) {
			try {
				checkLocationDeleted((IProject) resource);
			} catch (final CoreException e) {
				e.printStackTrace();
				return;
			}
		} else if (resource.getType() == ROOT) {
			final IProject[] projects = ((IWorkspaceRoot) resource).getProjects();
			for (final IProject project : projects) {
				try {
					checkLocationDeleted(project);
				} catch (final CoreException e) {
					e.printStackTrace();
					return;
				}
			}
		}

		runInUI("Refreshing " + resource.getName(), 0, (m) -> {
			FileMetaDataProvider.getInstance().storeMetaData(resource, null, true);
			FileMetaDataProvider.getInstance().getMetaData(resource, false, true);
			getNavigator().getCommonViewer().refresh(getInstance().findWrappedInstanceOf(resource), true);
			final WorkspaceJob job = new WorkspaceJob("Refreshing " + resource.getName()) {

				@Override
				public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
					resource.refreshLocal(DEPTH_INFINITE, monitor);
					resource.getParent().refreshLocal(DEPTH_INFINITE, monitor);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		});
	}

	@Override
	public void completeRefresh(final List<? extends IResource> list) {
		final IStatus[] errorStatus = new IStatus[1];
		errorStatus[0] = Status.OK_STATUS;
		final List<? extends IResource> resources =
				list == null || list.isEmpty() ? Arrays.asList(ResourcesPlugin.getWorkspace().getRoot()) : list;
		final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			public void execute(final IProgressMonitor monitor) {
				final Iterator<? extends IResource> resourcesEnum = resources.iterator();
				try {
					while (resourcesEnum.hasNext()) {
						try {
							final IResource resource = resourcesEnum.next();
							simpleRefresh(resource, monitor);
							if (monitor != null) {
								monitor.worked(1);
							}
						} catch (final CoreException e) {}
						if (monitor != null && monitor.isCanceled()) { throw new OperationCanceledException(); }
					}
				} finally {
					if (monitor != null) {
						monitor.done();
					}
				}
			}
		};
		final WorkspaceJob job = new WorkspaceJob("Refreshing the GAMA Workspace") {

			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {

				try {
					ResourceManager.block();
					monitor.beginTask("Refreshing GAMA Workspace: updating the library of models", 100);
					WorkspaceModelsManager.loadModelsLibrary();
					monitor.beginTask("Refreshing GAMA Workspace: recreating files metadata", 1000);
					for (final IResource r : resources) {
						r.accept(proxy -> {
							final IFileMetaDataProvider provider = GAMA.getGui().getMetaDataProvider();
							final IResource file = proxy.requestResource();
							provider.storeMetaData(file, null, true);
							provider.getMetaData(file, false, true);
							monitor.worked(1);
							return true;
						}, IResource.NONE);

					}
					monitor.beginTask("Refreshing GAMA Workspace: refreshing resources", resources.size());
					op.run(monitor);
					monitor.beginTask("Refreshing GAMA Workspace: deleting virtual folders caches", 1);
					NavigatorRoot.getInstance().resetVirtualFolders(NavigatorRoot.getInstance().getManager());
					monitor.beginTask("Refreshing GAMA Workspace: refreshing the navigator", 1);
					final IWorkspace workspace = ResourcesPlugin.getWorkspace();
					refreshNavigator();
					monitor.beginTask("Refreshing GAMA Workspace: rebuilding models", 100);
					try {

						workspace.build(IncrementalProjectBuilder.CLEAN_BUILD, new ProgressMonitorWrapper(monitor) {

							@Override
							public void done() {
								super.done();
								refreshNavigator();

							}

						});

					} catch (final CoreException ex) {
						ex.printStackTrace();
					}
				} catch (final Exception e) {
					return Status.CANCEL_STATUS;
				} finally {
					ResourceManager.unblock(monitor);
					monitor.done();
				}
				return errorStatus[0];
			}

		};
		job.setUser(true);
		job.schedule();
	}

	void checkLocationDeleted(final IProject project) throws CoreException {
		if (!project.exists()) { return; }
		final IFileInfo location = IDEResourceInfoUtils.getFileInfo(project.getLocationURI());
		if (!location.exists()) {
			final String message = NLS.bind(IDEWorkbenchMessages.RefreshAction_locationDeletedMessage,
					project.getName(), location.toString());

			final MessageDialog dialog = new MessageDialog(WorkbenchHelper.getShell(),
					IDEWorkbenchMessages.RefreshAction_dialogTitle, null, message, MessageDialog.QUESTION,
					new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0) {
				@Override
				protected int getShellStyle() {
					return super.getShellStyle() | SWT.SHEET;
				}
			};
			WorkbenchHelper.run(() -> dialog.open());

			// Do the deletion back in the operation thread
			if (dialog.getReturnCode() == 0) { // yes was chosen
				project.delete(true, true, null);
			}
		}
	}

}
