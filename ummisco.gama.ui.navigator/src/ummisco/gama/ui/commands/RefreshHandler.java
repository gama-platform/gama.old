/*********************************************************************************************
 *
 * 'RefreshHandler.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxyVisitor;
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.progress.UIJob;

import msi.gama.application.workspace.WorkspaceModelsManager;
import msi.gama.common.interfaces.IGui;
import msi.gama.runtime.GAMA;
import msi.gama.util.file.IFileMetaDataProvider;
import ummisco.gama.ui.interfaces.IRefreshHandler;
import ummisco.gama.ui.navigator.GamaNavigator;
import ummisco.gama.ui.navigator.contents.NavigatorRoot;
import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class RefreshHandler extends AbstractHandler implements IRefreshHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		run((IResource) null);
		return null;
	}

	@Override
	public void run(final IResource resource) {
		final Display d = PlatformUI.getWorkbench().getDisplay();
		if (d.isDisposed())
			return;
		final UIJob job = new UIJob("Refreshing navigator") {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				final IWorkbenchPage page = WorkbenchHelper.getPage();
				if (page == null)
					return Status.OK_STATUS;
				final IViewPart view = page.findView(IGui.NAVIGATOR_VIEW_ID);
				if (view == null) { return Status.OK_STATUS; }
				((GamaNavigator) view).safeRefresh(
						resource == null ? ResourcesPlugin.getWorkspace().getRoot() : resource.getParent());
				if (resource != null)
					((GamaNavigator) view).selectReveal(new StructuredSelection(resource));
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	public void run(final IResource resource, final IProgressMonitor monitor) {
		final Display d = PlatformUI.getWorkbench().getDisplay();
		if (d.isDisposed())
			return;
		final UIJob job = new UIJob("Refreshing navigator") {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				final IWorkbenchPage page = WorkbenchHelper.getPage();
				if (page == null)
					return Status.OK_STATUS;
				final IViewPart view = page.findView(IGui.NAVIGATOR_VIEW_ID);
				if (view == null) { return Status.OK_STATUS; }
				((GamaNavigator) view).safeRefresh(
						resource == null ? ResourcesPlugin.getWorkspace().getRoot() : resource.getParent());
				if (resource != null)
					((GamaNavigator) view).selectReveal(new StructuredSelection(resource));
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	protected void refreshResource(final IResource resource, final IProgressMonitor monitor) throws CoreException {
		if (resource.getType() == IResource.PROJECT) {
			checkLocationDeleted((IProject) resource);
		} else if (resource.getType() == IResource.ROOT) {
			final IProject[] projects = ((IWorkspaceRoot) resource).getProjects();
			for (int i = 0; i < projects.length; i++) {
				checkLocationDeleted(projects[i]);
			}
		}
		resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
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
							refreshResource(resource, monitor);
							if (monitor != null)
								monitor.worked(1);
						} catch (final CoreException e) {}
						if (monitor.isCanceled()) { throw new OperationCanceledException(); }
					}
				} finally {
					monitor.done();
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
					monitor.beginTask("Refreshing GAMA Workspace: recreating files metadata", resources.size());
					for (final IResource r : resources) {
						r.accept(METADATA_DISCARDING_VISITOR, IResource.NONE);
						monitor.worked(1);
					}
					monitor.beginTask("Refreshing GAMA Workspace: refreshing resources", resources.size());
					op.run(monitor);
					monitor.beginTask("Refreshing GAMA Workspace: deleting virtual folders caches", 1);
					NavigatorRoot.INSTANCE.initializeVirtualFolders(NavigatorRoot.INSTANCE.mapper);
					monitor.beginTask("Refreshing GAMA Workspace: refreshing the navigator", 1);
					try {
						final IWorkspace workspace = ResourcesPlugin.getWorkspace();
						workspace.build(IncrementalProjectBuilder.CLEAN_BUILD, new ProgressMonitorWrapper(monitor) {

							@Override
							public void done() {
								super.done();
								RefreshHandler.this.run(workspace.getRoot());

							}

						});

					} catch (final CoreException ex) {
						ex.printStackTrace();
					}
				} catch (final Exception e) {
					return Status.CANCEL_STATUS;
				} finally {
					ResourceManager.unblock();
				}
				return errorStatus[0];
			}

		};
		job.setUser(true);
		job.schedule();
	}

	public static final IResourceProxyVisitor METADATA_DISCARDING_VISITOR = proxy -> {
		final IFileMetaDataProvider provider = GAMA.getGui().getMetaDataProvider();
		final IResource file = proxy.requestResource();
		provider.storeMetaData(file, null, true);
		provider.getMetaData(file, false, true);
		return true;
	};

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
