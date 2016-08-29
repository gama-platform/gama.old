/**
 * Created by drogoul, 29 août 2016
 * 
 */
package msi.gama.lang.gaml.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
import org.eclipse.xtext.util.Tuples;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.impl.ModelImpl;
import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.utils.EGaml;

/**
 * The class GlobalIndexInitializer.
 *
 * @author drogoul
 * @since 29 août 2016
 *
 */
@Singleton
public class WorkspaceIndexer implements IModelIndexer {

	public static final WorkspaceIndexer INSTANCE = new WorkspaceIndexer();

	private final DirectedGraph<URI, Pair<URI, URI>> graph = new SimpleDirectedGraph(
			new EdgeFactory<URI, Pair<URI, URI>>() {

				@Override
				public Pair<URI, URI> createEdge(final URI sourceVertex, final URI targetVertex) {
					return Tuples.create(sourceVertex, targetVertex);
				}
			});

	@Inject
	private WorkspaceIndexer() {
	}

	private void addImport(final URI from, final URI to) {
		graph.addVertex(to);
		graph.addVertex(from);
		graph.addEdge(from, to);
	}

	private Set<URI> getImportsAsAbsoluteURIS(final URI baseURI, final Model m) {
		Set<URI> result = Collections.EMPTY_SET;
		if (((ModelImpl) m).eIsSet(GamlPackage.MODEL__IMPORTS)) {
			result = new HashSet();
			for (final Import e : m.getImports()) {
				URI uri = URI.createURI(URI.decode(e.getImportURI()));
				uri = properlyEncodedURI(uri.resolve(baseURI));
				// System.out.println(uri);
				result.add(uri);
			}
		}
		return result;
	}

	@Override
	public void buildIndex() {
		final long start = System.currentTimeMillis();
		String action = "building";
		if (!graph.vertexSet().isEmpty()) {
			action = "rebuilding";
			final Set<URI> vertices = new HashSet(graph.vertexSet());
			graph.removeAllVertices(vertices);
		}
		System.out.print(">GAMA " + action + " workspace models index");
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
		System.out.println(" in " + (System.currentTimeMillis() - start) + "ms.");
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#updateImports(msi.gama.lang.gaml.resource.GamlResource)
	 */
	@Override
	public void updateImports(final GamlResource r) {
		final URI baseURI = properlyEncodedURI(r.getURI());
		if (indexes(baseURI)) {
			final List<Pair<URI, URI>> edges = new ArrayList(graph.outgoingEdgesOf(baseURI));
			graph.removeAllEdges(edges);
		}
		final Set<URI> added = getImportsAsAbsoluteURIS(baseURI, (Model) r.getContents().get(0));
		// System.out.println("Updating imports of " + baseURI.lastSegment() + "
		// with " + added);
		for (final URI uri : added) {
			addImport(baseURI, uri);
		}

	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#directImportersOf(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Set<URI> directImportersOf(final URI uri) {
		final URI newURI = properlyEncodedURI(uri);
		if (indexes(newURI))
			return new HashSet(Graphs.predecessorListOf(graph, newURI));
		return Collections.EMPTY_SET;
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#directImportsOf(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Set<URI> directImportsOf(final URI uri) {
		final URI newURI = properlyEncodedURI(uri);
		if (indexes(newURI))
			return new HashSet(Graphs.successorListOf(graph, newURI));
		return Collections.EMPTY_SET;
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#allImportsOf(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Iterator<URI> allImportsOf(final URI uri) {
		final Iterator<URI> result = new BreadthFirstIterator(graph, properlyEncodedURI(uri));
		result.next(); // to eliminate the uri
		return result;
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#allImporterssOf(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Iterator<URI> allImportersOf(final URI uri) {
		return null;
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#indexes(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public boolean indexes(final URI uri) {
		return graph.containsVertex(properlyEncodedURI(uri));
	}

	@Override
	public URI properlyEncodedURI(final URI uri) {
		if (uri.isPlatform()) {
			return URI.createURI(URI.decode(uri.toString()));
		} else
			return URI.createPlatformResourceURI(URI.decode(uri.toString()), false);
	}

}
