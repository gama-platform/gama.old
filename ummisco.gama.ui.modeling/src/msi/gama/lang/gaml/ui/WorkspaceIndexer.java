/**
 * Created by drogoul, 29 août 2016
 * 
 */
package msi.gama.lang.gaml.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.emf.common.util.URI;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import msi.gama.lang.gaml.indexer.BaseIndexer;
import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.ModelDescription;

/**
 * The class GlobalIndexInitializer.
 *
 * @author drogoul
 * @since 29 août 2016
 *
 */
@Singleton
public class WorkspaceIndexer extends BaseIndexer implements IModelIndexer, IResourceChangeListener {

	public static final WorkspaceIndexer INSTANCE = new WorkspaceIndexer();
	// private static final boolean DONT_FORCE_COMPLETE_INDEXING = true;
	// XtextResourceSet indexingResourceSet = new XtextResourceSet();
	protected final Map<URI, IGamlBuilderListener> resourceListeners = new HashMap();
	// protected volatile boolean hasFinishedIndexing;

	@Inject
	private WorkspaceIndexer() {
		// ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	// protected void buildIndex(final IContainer resource) {
	//
	// try {
	//
	// try {
	// resource.accept(visitor, IContainer.NONE);
	// } catch (final CoreException e1) {
	// e1.printStackTrace();
	// }
	// } finally {
	// clearResourceSet(indexingResourceSet);
	//
	// }
	//
	// }

	@Override
	public boolean isEdited(final URI uri) {
		return resourceListeners.containsKey(properlyEncodedURI(uri));
	}

	@Override
	public void updateState(final URI uri, final ModelDescription model, final boolean newState,
			final ErrorCollector status) {
		final URI newURI = properlyEncodedURI(uri);
		final IGamlBuilderListener listener = resourceListeners.get(newURI);
		if (listener == null)
			return;
		final Collection exps = model == null ? newState ? Collections.EMPTY_SET : null : model.getExperiments();
		listener.validationEnded(exps, status);
	}

	@Override
	public void addResourceListener(final URI uri, final IGamlBuilderListener listener) {
		final URI newURI = properlyEncodedURI(uri);
		resourceListeners.put(newURI, listener);
	}

	@Override
	public void removeResourceListener(final IGamlBuilderListener listener) {
		URI toRemove = null;
		for (final Map.Entry<URI, IGamlBuilderListener> entry : resourceListeners.entrySet()) {
			if (entry.getValue() == listener) {
				toRemove = entry.getKey();
			}
		}
		if (toRemove != null) {
			resourceListeners.remove(toRemove);
			removeDocumentation(toRemove);
		}

	}
	//
	// @Override
	// protected boolean shouldExpandDependencies() {
	// return false;
	// }

	// @Override
	// public void buildIndex() {
	// if (DONT_FORCE_COMPLETE_INDEXING) {
	// hasFinishedIndexing = true;
	// return;
	// }
	// final long start = System.currentTimeMillis();
	// if (!index.vertexSet().isEmpty()) {
	// final Set<URI> vertices = new HashSet(index.vertexSet());
	// index.removeAllVertices(vertices);
	// }
	// resourceErrors.clear();
	// final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	// final IWorkspace workspace = ResourcesPlugin.getWorkspace();
	// final IProject[] projects = root.getProjects();
	// final int nb = projects.length;
	// hasFinishedIndexing = false;
	// final boolean wasDeliver = indexingResourceSet.eDeliver();
	// final Job job = new Job("Indexing models") {
	//
	// @Override
	// protected IStatus run(final IProgressMonitor monitor) {
	// try {
	//
	// indexingResourceSet.eSetDeliver(false);
	// workspace.run(new IWorkspaceRunnable() {
	//
	// @Override
	// public void run(final IProgressMonitor monitor) throws CoreException
	// {
	//
	// monitor.beginTask("Indexing models: ", nb);
	// for (final IProject p : projects) {
	// if (p.isAccessible()) {
	// monitor.subTask(p.getName());
	// buildIndex(p);
	// }
	// monitor.worked(1);
	// }
	//
	// monitor.done();
	//
	// }
	// }, root, IWorkspace.AVOID_UPDATE, monitor);
	// } catch (final CoreException e) {
	// e.printStackTrace();
	// } finally {
	// hasFinishedIndexing = true;
	// indexingResourceSet.eSetDeliver(wasDeliver);
	// System.out.print(">GAMA indexing workspace in " +
	// (System.currentTimeMillis() - start) + "ms.");
	// }
	// return Status.OK_STATUS;
	// }
	// };
	//
	// job.setPriority(Job.BUILD);
	// job.setUser(false);
	// job.schedule();
	// }

	// IResourceProxyVisitor visitor = new IResourceProxyVisitor() {
	//
	// @Override
	// public boolean visit(final IResourceProxy proxy) throws CoreException {
	// final int type = proxy.getType();
	// if (type == IResource.FILE && proxy.getName().endsWith(".gaml") &&
	// proxy.isAccessible()) {
	// final URI uri =
	// URI.createPlatformResourceURI(proxy.requestFullPath().toString(), true);
	// final Resource r = indexingResourceSet.getResource(uri, true);
	// updateImports((GamlResource) r);
	// r.unload();
	// }
	// return type != IResource.FILE;
	// }
	// };

	/**
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		// if (event.getType() != IResourceChangeEvent.POST_CHANGE)
		// return;
		// final IResourceDelta delta = event.getDelta();
		// if (delta == null)
		// return;
		// final Set<IContainer> projects = new HashSet();
		// try {
		// // delta.accept(new DeltaPrinter());
		// delta.accept(new IResourceDeltaVisitor() {
		//
		// @Override
		// public boolean visit(final IResourceDelta d) throws CoreException {
		// final int kind = d.getKind();
		// final int flags = d.getFlags();
		// if (kind == IResourceDelta.CHANGED) {
		// if (flags == IResourceDelta.OPEN) {
		// final IProject p = d.getResource().getProject();
		// if (p.isOpen()) {
		// projects.add(p);
		// } else
		// projects.add(ResourcesPlugin.getWorkspace().getRoot());
		// return false;
		// }
		// } else if (kind == IResourceDelta.ADDED || kind ==
		// IResourceDelta.REMOVED) {
		// final IResource r = d.getResource();
		// final String ext = r.getFileExtension();
		// if (d.getFlags() != IResourceDelta.MARKERS && !r.isDerived() &&
		// "gaml".equals(ext)) {
		// final IProject p = d.getResource().getProject();
		// if (p != null)
		// projects.add(p);
		// return false;
		// }
		// }
		// return true;
		// }
		// });
		// } catch (final CoreException e) {
		// e.printStackTrace();
		// }
		//
		// for (final IContainer p : projects)
		// buildIndex(p);
	}

}
