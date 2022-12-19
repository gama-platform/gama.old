/*******************************************************************************************************
 *
 * ResourceManager.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import static org.eclipse.core.resources.IResourceChangeEvent.POST_CHANGE;
import static org.eclipse.core.resources.IResourceChangeEvent.PRE_CLOSE;
import static org.eclipse.core.resources.IResourceChangeEvent.PRE_DELETE;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.getContentTypeId;
import static ummisco.gama.ui.utils.WorkbenchHelper.BUILTIN_NATURE;
import static ummisco.gama.ui.utils.WorkbenchHelper.PLUGIN_NATURE;
import static ummisco.gama.ui.utils.WorkbenchHelper.TEST_NATURE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.navigator.CommonViewer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import msi.gama.application.workspace.WorkspaceModelsManager;
import msi.gama.common.GamlFileExtension;
import msi.gama.runtime.GAMA;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gaml.statements.test.CompoundSummary;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.commands.TestsRunner;
import ummisco.gama.ui.metadata.FileMetaDataProvider;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class ResourceManager.
 */
public class ResourceManager implements IResourceChangeListener, IResourceDeltaVisitor, ISelectionChangedListener {

	static {
		DEBUG.OFF();
	}

	/** The instance. */
	public static ResourceManager INSTANCE;
	
	/** The Constant cache. */
	public final static Cache<IResource, WrappedResource<?, ?>> cache =
			CacheBuilder.newBuilder().initialCapacity(1000).concurrencyLevel(4).build();
	
	/** The viewer. */
	final CommonViewer viewer;
	
	/** The delegate. */
	final IResourceChangeListener delegate;
	
	/** The blocked. */
	volatile static boolean BLOCKED = false;
	
	/** The in initialization phase. */
	private static volatile boolean IN_INITIALIZATION_PHASE = false;
	
	/** The post event actions. */
	private final List<Runnable> postEventActions = new ArrayList<>();
	
	/** The to refresh. */
	private final Set<VirtualContent<?>> toRefresh = Collections.synchronizedSet(new HashSet<>());
	
	/** The to update. */
	private final Set<VirtualContent<?>> toUpdate = Collections.synchronizedSet(new HashSet<>());
	
	/** The to reveal. */
	private volatile Object toReveal = null;
	
	/** The current selection. */
	private static IStructuredSelection currentSelection;

	/**
	 * Block.
	 */
	public static void block() {
		BLOCKED = true;
	}

	/**
	 * Unblock.
	 *
	 * @param monitor the monitor
	 */
	public static void unblock(final IProgressMonitor monitor) {
		BLOCKED = false;
		WorkbenchHelper.run(() -> {
			monitor.beginTask("Processing additions to the workspace", BLOCKED_EVENTS.size());
			try {
				NavigatorRoot.getInstance().resetVirtualFolders(null);
				NavigatorRoot.getInstance().recreateVirtualFolders();
				IN_INITIALIZATION_PHASE = true;
				for (final IResourceChangeEvent event : BLOCKED_EVENTS) {
					INSTANCE.resourceChanged(event);
					monitor.worked(1);
				}
			} finally {
				IN_INITIALIZATION_PHASE = false;
			}
		});

		BLOCKED_EVENTS.clear();
	}

	/**
	 * Gets the single instance of ResourceManager.
	 *
	 * @return single instance of ResourceManager
	 */
	public static ResourceManager getInstance() { return INSTANCE; }

	/**
	 * Finish tests.
	 */
	public static void finishTests() {
		INSTANCE.refreshResource(NavigatorRoot.getInstance().getTestFolder());
	}

	/**
	 * Instantiates a new resource manager.
	 *
	 * @param delegate the delegate
	 * @param navigator the navigator
	 */
	public ResourceManager(final IResourceChangeListener delegate, final CommonViewer navigator) {
		this.viewer = navigator;
		viewer.addSelectionChangedListener(this);
		this.delegate = delegate;
		INSTANCE = this;
	}

	/**
	 * Reveal.
	 *
	 * @param r the r
	 */
	public void reveal(final Object r) {
		toReveal = r;
	}

	/**
	 * Gets the tests summary.
	 *
	 * @return the tests summary
	 */
	public CompoundSummary<?, ?> getTestsSummary() { return TestsRunner.LAST_RUN; }

	/**
	 * Post.
	 *
	 * @param run the run
	 */
	public void post(final Runnable run) {
		postEventActions.add(run);
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		currentSelection = (IStructuredSelection) event.getSelection();
	}

	/**
	 * Sets the selected folder.
	 *
	 * @param o the new selected folder
	 */
	public static void setSelectedFolder(final Object o) { currentSelection = new StructuredSelection(o); }

	/**
	 * Run post event actions.
	 */
	void runPostEventActions() {

		WorkbenchHelper.runInUI("Resource changes", 5, m -> {
			if (viewer.getControl().isDisposed()) return;
			viewer.getControl().setRedraw(false);
			final List<Runnable> runnables;
			synchronized (postEventActions) {
				runnables = ImmutableList.copyOf(postEventActions);
				postEventActions.clear();
			}

			for (final Runnable r : runnables) { r.run(); }
			final Set<VirtualContent<?>> refreshables;
			synchronized (toRefresh) {
				refreshables = ImmutableSet.copyOf(toRefresh);
				toRefresh.clear();
			}
			for (final VirtualContent<?> r : refreshables) {
				if (!viewer.getControl().isDisposed()) { viewer.refresh(r); }
			}
			final Set<VirtualContent<?>> updatables;
			synchronized (toUpdate) {
				updatables = ImmutableSet.copyOf(toUpdate);
				toUpdate.clear();
			}
			for (final VirtualContent<?> r : updatables) {
				if (!viewer.getControl().isDisposed()) { viewer.update(r, null); }
			}
			if (toReveal != null) {
				final VirtualContent<?> vc = findWrappedInstanceOf(toReveal);
				if (!viewer.getControl().isDisposed()) { viewer.setSelection(new StructuredSelection(vc), true); }
				toReveal = null;
			}
			viewer.getControl().setRedraw(true);
			viewer.getControl().update();
		});

	}

	/**
	 * Gets the resource.
	 *
	 * @param target the target
	 * @return the resource
	 */
	public static IResource getResource(final Object target) {
		if (target instanceof IResource) return (IResource) target;
		if (target instanceof IAdaptable) {
			final IAdaptable adapter = (IAdaptable) target;
			final IResource r = adapter.getAdapter(IResource.class);
			if (r != null) return r;
		}
		return null;
	}

	/**
	 * Gets the file.
	 *
	 * @param target the target
	 * @return the file
	 */
	public static IFile getFile(final Object target) {
		if (target instanceof IFile) return (IFile) target;
		if (target instanceof IAdaptable) {
			final IAdaptable adapter = (IAdaptable) target;
			final IFile r = adapter.getAdapter(IFile.class);
			if (r != null) return r;
		}
		return null;
	}

	/**
	 * Checks if is file.
	 *
	 * @param target the target
	 * @return true, if is file
	 */
	public static boolean isFile(final Object target) {
		return getFile(target) != null;
	}

	/**
	 * Checks if is resource.
	 *
	 * @param target the target
	 * @return true, if is resource
	 */
	public static boolean isResource(final Object target) {
		return getResource(target) != null;
	}

	/** The Constant BLOCKED_EVENTS. */
	static final List<IResourceChangeEvent> BLOCKED_EVENTS = new ArrayList<>();

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		if (BLOCKED) {
			BLOCKED_EVENTS.add(event);
			return;
		}
		if (DEBUG.IS_ON()) { DEBUG.OUT("========= New Event ========="); }
		try {
			if (event == null) return;
			// begin();
			final int type = event.getType();
			switch (type) {
				case POST_CHANGE:
					if (viewer.isBusy()) {
						WorkbenchHelper.runInUI("Resource changes", 50, m -> delegate.resourceChanged(event));
					} else {
						delegate.resourceChanged(event);
					}
					try {
						event.getDelta().accept(this);
					} catch (final CoreException e) {
						e.printStackTrace();
					}
					break;
				case IResourceChangeEvent.PRE_REFRESH:
					if (DEBUG.IS_ON()) {
						DEBUG.OUT("Project " + event.getResource().getName() + " about to be refreshed");
					}
					break;
				case PRE_CLOSE:
				case PRE_DELETE:
					if (DEBUG.IS_ON()) {
						DEBUG.OUT("Project " + event.getResource().getName() + " about to be closed or deleted");
					}
					break;
				default:

			}
		} finally {
			runPostEventActions();
		}
	}

	/**
	 * Process addition.
	 *
	 * @param res the res
	 * @return true, if successful
	 */
	// Returns whether to update or not
	boolean processAddition(final IResource res) {
		boolean update = false;
		switch (res.getType()) {
			case IResource.FILE:
				if (GamlFileExtension.isAny(res.getName())) {
					invalidateModelsCountCache(res.getParent());
					invalidateSeverityCache(res.getParent());
					update = true;
				}
				fileAdded((IFile) res);
				break;
			case IResource.PROJECT:
				projectAdded((IProject) res);
				update = true;
				break;
			case IResource.FOLDER:
				folderAdded((IFolder) res);
		}
		final IFileMetaDataProvider provider = GAMA.getGui().getMetaDataProvider();
		provider.storeMetaData(res, null, true);
		provider.getMetaData(res, false, true);

		return update;
	}

	/**
	 * File added.
	 *
	 * @param file the file
	 */
	public void fileAdded(final IFile file) {
		if (DEBUG.IS_ON()) { DEBUG.OUT("File " + file.getName() + " has been added"); }
		final WrappedContainer<?> parent = findWrappedInstanceOf(file.getParent());
		wrap(parent, file);
		if (parent != null) {
			parent.initializeChildren();
			refreshResource(parent);
		}
	}

	/**
	 * Returns the top-level folder in which to paste/drop a project, based on its description and the current selection
	 *
	 * @param project
	 * @return
	 */
	public TopLevelFolder chooseFolderForPasting(final IProject project) {
		if (currentSelection != null && !currentSelection.isEmpty()) {
			final Object o = currentSelection.getFirstElement();
			if (o instanceof VirtualContent) return ((VirtualContent<?>) o).getTopLevelFolder();
		}
		return NavigatorRoot.getInstance().getUserFolder();
	}

	/**
	 * Project added.
	 *
	 * @param project the project
	 */
	public void projectAdded(final IProject project) {
		if (DEBUG.IS_ON()) { DEBUG.OUT("Project " + project.getName() + " has been added"); }
		if (!IN_INITIALIZATION_PHASE) {
			final TopLevelFolder root = chooseFolderForPasting(project);
			final String nature = root.getNature();
			final WrappedProject p = (WrappedProject) wrap(root, project);
			post(() -> {
				WorkspaceModelsManager.instance.setValuesProjectDescription(project, nature == BUILTIN_NATURE,
						nature == PLUGIN_NATURE, nature == TEST_NATURE, null);
				root.initializeChildren();
				refreshResource(root);
				reveal(p);
			});
		}
	}

	/**
	 * Project opened.
	 *
	 * @param res the res
	 * @return true, if successful
	 */
	private boolean projectOpened(final IProject res) {
		if (DEBUG.IS_ON()) { DEBUG.OUT("Project " + res.getName() + " has been opened"); }
		final WrappedProject p = findWrappedInstanceOf(res);
		if (p == null) {
			projectAdded(res);
			return false;
		}
		p.initializeChildren();
		refreshResource(p);
		updateResource(p);
		return false;
	}

	/**
	 * Project closed.
	 *
	 * @param res the res
	 * @return true, if successful
	 */
	private boolean projectClosed(final IProject res) {
		if (DEBUG.IS_ON()) { DEBUG.OUT("Project " + res.getName() + " has been closed"); }
		final WrappedProject p = findWrappedInstanceOf(res);
		p.initializeChildren();
		p.invalidateModelsCount();
		p.invalidateSeverity();
		refreshResource(p);
		updateResource(p);
		return false;
	}

	/**
	 * Folder added.
	 *
	 * @param folder the folder
	 */
	public void folderAdded(final IFolder folder) {
		if (DEBUG.IS_ON()) { DEBUG.OUT("Folder " + folder.getName() + " has been added"); }
		final WrappedContainer<?> parent = findWrappedInstanceOf(folder.getParent());
		// final WrappedFolder wrapped = (WrappedFolder) wrap(parent, folder);
		if (parent != null) {
			parent.initializeChildren();
			refreshResource(parent);
		}

	}

	/**
	 * Process removal.
	 *
	 * @param res the res
	 * @return true, if successful
	 */
	// Returns whether to update or not
	boolean processRemoval(final IResource res) {
		boolean update = false;
		switch (res.getType()) {
			case IResource.FILE:
				if (GamlFileExtension.isAny(res.getName())) {
					invalidateModelsCountCache(res.getParent());
					invalidateSeverityCache(res.getParent());
					update = true;
				}
				fileRemoved((IFile) res);
				break;
			case IResource.FOLDER:
				folderRemoved((IFolder) res);
				break;
			case IResource.PROJECT:
				projectRemoved((IProject) res);
				break;
		}

		return update;
	}

	/**
	 * File removed.
	 *
	 * @param file the file
	 */
	public void fileRemoved(final IFile file) {
		if (DEBUG.IS_ON()) { DEBUG.OUT("File " + file.getName() + " has been removed"); }
		cache.invalidate(file);
		final WrappedContainer<?> parent = findWrappedInstanceOf(file.getParent());
		if (parent != null) {
			parent.initializeChildren();
			refreshResource(parent);
		}
	}

	/**
	 * Folder removed.
	 *
	 * @param folder the folder
	 */
	public void folderRemoved(final IFolder folder) {
		if (DEBUG.IS_ON()) { DEBUG.OUT("Folder " + folder.getName() + " has been removed"); }
		final WrappedFolder wc = (WrappedFolder) findWrappedInstanceOf(folder);
		cache.invalidate(folder);
		if (wc != null) {
			wc.getParent().initializeChildren();
			refreshResource(wc.getParent());
		}
	}

	/**
	 * Project removed.
	 *
	 * @param project the project
	 */
	public void projectRemoved(final IProject project) {
		if (DEBUG.IS_ON()) { DEBUG.OUT("Project " + project.getName() + " has been removed"); }
		final WrappedProject wp = findWrappedInstanceOf(project);
		cache.invalidate(project);
		final TopLevelFolder tp = (TopLevelFolder) wp.getParent();
		tp.initializeChildren();
		refreshResource(tp);
	}

	@Override
	public boolean visit(final IResourceDelta delta) {
		final IResource res = delta.getResource();
		boolean update = false;
		switch (delta.getKind()) {
			case IResourceDelta.OPEN:
				if (res.isAccessible()) {
					update = projectOpened((IProject) res);
				} else {
					update = projectClosed((IProject) res);
				}
				break;
			case IResourceDelta.ADDED:
				update = processAddition(res);
				break;
			case IResourceDelta.REMOVED:
				update = processRemoval(res);
				break;
			case IResourceDelta.CHANGED:
				final int flags = delta.getFlags();
				if ((flags & IResourceDelta.MARKERS) != 0) { update = processMarkersChanged(res); }
				if (((flags & IResourceDelta.TYPE) != 0) && DEBUG.IS_ON()) {
					DEBUG.OUT("Resource type changed: " + res);
				}
				if (((flags & IResourceDelta.CONTENT) != 0) && DEBUG.IS_ON()) {
					DEBUG.OUT("Resource contents changed: " + res);
				}
				if (((flags & IResourceDelta.SYNC) != 0) && DEBUG.IS_ON()) {
					DEBUG.OUT("Resource sync info changed: " + res);
				}
				if ((flags & IResourceDelta.LOCAL_CHANGED) != 0) {
					if (DEBUG.IS_ON()) { DEBUG.OUT("Linked resource target info changed: " + res); }
					update = processLinkedTargerChanged(res);
				}
				break;
		}
		if (update) { updateResource(res); }
		return true; // visit the children
	}

	/**
	 * Process linked targer changed.
	 *
	 * @param res the res
	 * @return true, if successful
	 */
	private boolean processLinkedTargerChanged(final IResource res) {
		if (res.getType() == IResource.FILE) {
			invalidateSeverityCache(res);
			final WrappedFile file = (WrappedFile) findWrappedInstanceOf(res);
			refreshResource(file.getParent());
		}
		return true;
	}

	/**
	 * Process markers changed.
	 *
	 * @param res the res
	 * @return true, if successful
	 */
	private boolean processMarkersChanged(final IResource res) {
		if (DEBUG.IS_ON()) { DEBUG.OUT("File " + res.getName() + " markers have changed"); }
		invalidateSeverityCache(res);
		if (res.getType() == IResource.FILE) {
			final WrappedFile file = (WrappedFile) findWrappedInstanceOf(res);
			if (file != null && file.isGamaFile()) {
				((WrappedGamaFile) file).computeURIProblems();
				refreshResource(file);
			}
		}
		return true;
	}

	/**
	 * Update resource.
	 *
	 * @param res the res
	 */
	private void updateResource(final IResource res) {
		if (res == null) return;
		updateResource(findWrappedInstanceOf(res));
	}

	/**
	 * Update resource.
	 *
	 * @param res the res
	 */
	private void updateResource(final VirtualContent<?> res) {
		if (res == null) return;
		VirtualContent<?> resource = res;
		synchronized (toUpdate) {
			while (resource != null) {
				toUpdate.add(resource);
				resource = resource.getParent();
			}
		}
	}

	/**
	 * Refresh resource.
	 *
	 * @param res the res
	 */
	public void refreshResource(final VirtualContent<?> res) {
		// if (res == null) { return; }
		// Keep null to refresh all workspace
		synchronized (toRefresh) {
			toRefresh.add(res);
		}
	}

	/**
	 * Invalidate severity cache.
	 *
	 * @param resource the resource
	 */
	private void invalidateSeverityCache(final IResource resource) {
		final WrappedResource<?, ?> p = findWrappedInstanceOf(resource);
		if (p != null) {
			p.invalidateSeverity();
		} else {
			// If not found, maybe the parent is registered
			final IResource parent = resource.getParent();
			if (parent != null) { invalidateSeverityCache(parent); }
		}
	}

	/**
	 * Invalidate models count cache.
	 *
	 * @param container the container
	 */
	private void invalidateModelsCountCache(final IContainer container) {
		final WrappedContainer<?> p = findWrappedInstanceOf(container);
		if (p != null) { p.invalidateModelsCount(); }
	}

	/**
	 * Find wrapped instance of.
	 *
	 * @param resource the resource
	 * @return the wrapped resource
	 */
	public WrappedResource<?, ?> findWrappedInstanceOf(final Object resource) {
		if (resource == null) return null;
		if (resource instanceof WrappedResource) return (WrappedResource<?, ?>) resource;
		return cache.getIfPresent(resource);
	}

	/**
	 * Find wrapped instance of.
	 *
	 * @param shape the shape
	 * @return the wrapped container
	 */
	public WrappedContainer<?> findWrappedInstanceOf(final IContainer shape) {
		if (shape == null) return null;
		return (WrappedContainer<?>) cache.getIfPresent(shape);
	}

	/**
	 * Find wrapped instance of.
	 *
	 * @param parent the parent
	 * @return the wrapped project
	 */
	public WrappedProject findWrappedInstanceOf(final IProject parent) {
		if (parent == null) return null;
		return (WrappedProject) cache.getIfPresent(parent);
	}

	/**
	 * Wrap.
	 *
	 * @param parent the parent
	 * @param child the child
	 * @return the wrapped resource
	 */
	public WrappedResource<?, ?> wrap(final VirtualContent<?> parent, final IResource child) {
		if (parent == null || child == null) return null;
		try {
			return cache.get(child, () -> privateCreateWrapping(parent, child));
		} catch (final ExecutionException e) {
			return null;
		}
	}

	/**
	 * Private create wrapping.
	 *
	 * @param parent the parent
	 * @param child the child
	 * @return the wrapped resource
	 */
	private static WrappedResource<?, ?> privateCreateWrapping(final VirtualContent<?> parent, final IResource child) {
		if (DEBUG.IS_ON()) { DEBUG.OUT("Creation of the wrapped instance of " + child.getName()); }
		switch (child.getType()) {
			case IResource.FILE:
				if (FileMetaDataProvider.GAML_CT_ID.equals(getContentTypeId((IFile) child)))
					return new WrappedGamaFile((WrappedContainer<?>) parent, (IFile) child);
				if (child.isLinked()) return new WrappedLink((WrappedContainer<?>) parent, (IFile) child);
				return new WrappedFile((WrappedContainer<?>) parent, (IFile) child);
			case IResource.FOLDER:
				return new WrappedFolder((WrappedContainer<?>) parent, (IFolder) child);
			case IResource.PROJECT:
				return new WrappedProject((TopLevelFolder) parent, (IProject) child);
		}
		return null;
	}

	/**
	 * Validate location.
	 *
	 * @param resource the resource
	 * @return true, if successful
	 */
	public boolean validateLocation(final IFile resource) {
		if (!resource.isLinked()) return true;
		if (DEBUG.IS_ON()) { DEBUG.OUT("Validating link location of " + resource); }
		final boolean internal =
				ResourcesPlugin.getWorkspace().validateLinkLocation(resource, resource.getLocation()).isOK();
		if (!internal) return false;
		final IFileStore file = EFS.getLocalFileSystem().getStore(resource.getLocation());
		final IFileInfo info = file.fetchInfo();
		return info.exists();
	}

}
