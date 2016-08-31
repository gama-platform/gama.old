/**
 * Created by drogoul, 29 août 2016
 * 
 */
package msi.gama.lang.gaml.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import msi.gama.lang.gaml.indexer.BaseIndexer;
import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.compilation.GamaBundleLoader;

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
	final XtextResourceSet indexingResourceSet = new XtextResourceSet();

	@Inject
	private WorkspaceIndexer() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	@Override
	protected boolean validatePlugins(final GamlResource res) {

		// If plugins, required for building this model, are missing in the
		// current version of GAMA
		// raise an error and abort the build.
		IPath path = res.getPath();
		Set<String> plugins = null;
		final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (resource != null) {
			final IProject project = resource.getProject();
			if (project != null) {
				path = resource.getProjectRelativePath();
				final String s = ".metadata/" + path.toPortableString() + ".meta";
				path = Path.fromPortableString(s);
				final IResource r = project.findMember(path);
				if (r != null && r instanceof IFile) {
					final IFile m = (IFile) r;
					BufferedReader in = null;
					try {
						in = new BufferedReader(new InputStreamReader(m.getContents()));
						final GamlProperties req = new GamlProperties(in);
						plugins = req.get(GamlProperties.PLUGINS);

					} catch (final CoreException e) {
						e.printStackTrace();
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (final IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		if (plugins != null && !plugins.isEmpty()) {
			for (final String plugin : plugins) {
				if (!GamaBundleLoader.contains(plugin)) {
					final Map<GamlResource, String> problems = new HashMap();
					problems.put(res, "The plugin " + plugin + " is required to run this model");
					res.invalidateBecauseOfImportedProblem(problems);
					return false;
				}
			}
		}

		return true;
	}

	protected void buildIndex(final IContainer resource) {

		final long start = System.currentTimeMillis();
		try {
			hasFinishedIndexing = false;
			if (!index.vertexSet().isEmpty()) {
				final Set<URI> vertices = new HashSet(index.vertexSet());
				index.removeAllVertices(vertices);
			}
			try {
				resource.accept(visitor, IContainer.NONE);
			} catch (final CoreException e1) {
				e1.printStackTrace();
			}
		} finally {
			clearResourceSet(indexingResourceSet);
			System.out.print(
					">GAMA  indexing [" + (resource.getName().isEmpty() ? "workspace" : resource.getName()) + "]");
			System.out.println(" in " + (System.currentTimeMillis() - start) + "ms.");
			hasFinishedIndexing = true;
		}

	}

	@Override
	public void buildIndex() {
		try {
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

				@Override
				public void run(final IProgressMonitor monitor) throws CoreException {
					buildIndex(ResourcesPlugin.getWorkspace().getRoot());
				}
			}, null, IWorkspace.AVOID_UPDATE, null);
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		super.buildIndex();
	}

	IResourceProxyVisitor visitor = new IResourceProxyVisitor() {

		@Override
		public boolean visit(final IResourceProxy proxy) throws CoreException {
			final int type = proxy.getType();
			if (type == IResource.FILE && proxy.getName().endsWith(".gaml") && proxy.isAccessible()) {
				final URI uri = URI.createPlatformResourceURI(proxy.requestFullPath().toString(), true);
				final Resource r = indexingResourceSet.getResource(uri, true);
				updateImports((GamlResource) r);
				// We validate the plugins only once when building (or
				// rebuilding) the index
				validatePlugins((GamlResource) r);
			}
			return type != IResource.FILE;
		}
	};

	@Override
	protected void clearResourceSet(final ResourceSet resourceSet) {
		final boolean wasDeliver = resourceSet.eDeliver();
		try {
			resourceSet.eSetDeliver(false);
			resourceSet.getResources().clear();
		} finally {
			resourceSet.eSetDeliver(wasDeliver);
		}
	}

	/**
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() != IResourceChangeEvent.POST_CHANGE)
			return;
		final IResourceDelta delta = event.getDelta();
		if (delta == null)
			return;
		final Set<IContainer> projects = new HashSet();
		try {
			// delta.accept(new DeltaPrinter());
			delta.accept(new IResourceDeltaVisitor() {

				@Override
				public boolean visit(final IResourceDelta d) throws CoreException {
					final int kind = d.getKind();
					final int flags = d.getFlags();
					if (kind == IResourceDelta.CHANGED) {
						if (flags == IResourceDelta.OPEN) {
							final IProject p = d.getResource().getProject();
							if (p.isOpen()) {
								projects.add(p);
							} else
								projects.add(ResourcesPlugin.getWorkspace().getRoot());
							return false;
						}
					} else if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
						final IResource r = d.getResource();
						final String ext = r.getFileExtension();
						if (d.getFlags() != IResourceDelta.MARKERS && !r.isDerived() && "gaml".equals(ext)) {
							final IProject p = d.getResource().getProject();
							if (p != null)
								projects.add(p);
							return false;
						}
					}
					return true;
				}
			});
		} catch (final CoreException e) {
			e.printStackTrace();
		}

		for (final IContainer p : projects)
			buildIndex(p);
	}

	class DeltaPrinter implements IResourceDeltaVisitor {
		@Override
		public boolean visit(final IResourceDelta delta) {
			final IResource res = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				System.out.print("Resource ");
				System.out.print(res.getFullPath());
				System.out.println(" was added.");
				break;
			case IResourceDelta.REMOVED:
				System.out.print("Resource ");
				System.out.print(res.getFullPath());
				System.out.println(" was removed.");
				break;
			case IResourceDelta.CHANGED:
				System.out.print("Resource ");
				System.out.print(res.getFullPath());
				System.out.print(" has changed. ");
				final int flags = delta.getFlags();
				switch (flags) {
				case IResourceDelta.CONTENT:
					System.out.println("CONTENT");
					break;
				case IResourceDelta.COPIED_FROM:
					System.out.println("COPIED_FROM");
					break;
				case IResourceDelta.DERIVED_CHANGED:
					System.out.println("DERIVED_CHANGED");
					break;
				case IResourceDelta.DESCRIPTION:
					System.out.println("DESCRIPTION");
					break;
				case IResourceDelta.LOCAL_CHANGED:
					System.out.println("LOCAL_CHANGED");
					break;
				case IResourceDelta.MARKERS:
					System.out.println("MARKERS");
					break;
				case IResourceDelta.MOVED_FROM:
					System.out.println("MOVED_FROM");
					break;
				case IResourceDelta.MOVED_TO:
					System.out.println("MOVED_TO");
					break;
				case IResourceDelta.OPEN:
					System.out.println("OPEN");
					break;
				case IResourceDelta.REPLACED:
					System.out.println("REPLACED");
					break;
				}

				break;
			}
			return true; // visit the children
		}
	}

}
