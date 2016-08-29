/**
 * Created by drogoul, 29 août 2016
 * 
 */
package msi.gama.lang.gaml.ui;

import static msi.gama.lang.gaml.validation.GamlJavaValidator.IMPORTS_GRAPH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.resource.XtextResourceSetProvider;
import org.eclipse.xtext.util.Pair;

import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.impl.ModelImpl;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gama.lang.gaml.validation.GamlJavaValidator.IGlobalIndexInitializer;
import msi.gama.lang.utils.EGaml;

/**
 * The class GlobalIndexInitializer.
 *
 * @author drogoul
 * @since 29 août 2016
 *
 */
public class GlobalIndexInitializer implements IGlobalIndexInitializer {

	// public final static Multimap<URI, URI> DEPENDENCIES =
	// HashMultimap.create(300, 5);

	static GlobalIndexInitializer instance = new GlobalIndexInitializer();

	private GlobalIndexInitializer() {
		GamlJavaValidator.setIndexInitializer(this);
	}

	public static GlobalIndexInitializer getInstance() {
		return instance;
	}

	private static void addImport(final URI from, final URI to) {
		IMPORTS_GRAPH.addVertex(to);
		IMPORTS_GRAPH.addVertex(from);
		IMPORTS_GRAPH.addEdge(from, to);
	}

	public Set<URI> getImportsAsAbsoluteURIS(final URI baseURI, final Model m) {
		Set<URI> result = Collections.EMPTY_SET;
		if (((ModelImpl) m).eIsSet(GamlPackage.MODEL__IMPORTS)) {
			result = new HashSet();
			for (final Import e : m.getImports()) {
				URI uri = URI.createURI(URI.decode(e.getImportURI()));
				uri = uri.resolve(baseURI);
				result.add(uri);
			}
		}
		return result;
	}

	public void run() {
		final long start = System.currentTimeMillis();
		final Set<URI> vertices = new HashSet(IMPORTS_GRAPH.vertexSet());
		IMPORTS_GRAPH.removeAllVertices(vertices);
		System.out.print(">GAMA building workspace models index");
		final XtextResourceSetProvider provider = EGaml.getInstance(XtextResourceSetProvider.class);
		final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (final IProject p : projects) {
			final XtextResourceSet set = (XtextResourceSet) provider.get(p);
			try {
				p.accept(new IResourceVisitor() {

					@Override
					public boolean visit(final IResource resource) throws CoreException {
						if ("gaml".equals(resource.getFileExtension())) {
							final URI uri = URI.createPlatformResourceURI(resource.getFullPath().toString(), false);
							final Resource r = set.getResource(uri, true);
							updateImports((GamlResource) r);
						}
						return true;
					}
				});

			} catch (final CoreException e) {
				e.printStackTrace();
			}

			set.getResources().clear();
		}
		// expandDependencies();
		// try {
		// ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD,
		// null);
		// } catch (final CoreException e) {
		// e.printStackTrace();
		// }
		System.out.println(" in " + (System.currentTimeMillis() - start) + "ms.");
	}

	/**
	 * @see msi.gama.lang.gaml.validation.GamlJavaValidator.IGlobalIndexInitializer#updateImports(msi.gama.lang.gaml.resource.GamlResource)
	 */
	@Override
	public void updateImports(final GamlResource r) {
		final URI baseURI = r.getURI().isPlatform() ? URI.createURI(URI.decode(r.getURI().toString()), false)
				: URI.createPlatformResourceURI(URI.decode(r.getURI().toString()), false);
		if (IMPORTS_GRAPH.containsVertex(baseURI)) {
			final List<Pair<URI, URI>> edges = new ArrayList(IMPORTS_GRAPH.outgoingEdgesOf(baseURI));
			IMPORTS_GRAPH.removeAllEdges(edges);
		}
		final Set<URI> added = getImportsAsAbsoluteURIS(baseURI, (Model) r.getContents().get(0));
		// System.out.println("Updating imports of " + baseURI.lastSegment() + "
		// with " + added);
		for (final URI uri : added) {
			addImport(baseURI, uri);
		}

	}

}
