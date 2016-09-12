package msi.gama.lang.gaml.indexer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.impl.ModelImpl;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.util.TOrderedHashMap;

@Singleton
public class BaseIndexer implements IModelIndexer {

	public static final BaseIndexer INSTANCE = new BaseIndexer();

	protected DirectedGraph<URI, Edge> index = new SimpleDirectedGraph(Edge.class);

	protected final static TOrderedHashMap EMPTY_MAP = new TOrderedHashMap();

	protected TOrderedHashMap<URI, String> getImportsAsAbsoluteURIS(final URI baseURI, final Model m) {
		TOrderedHashMap<URI, String> result = EMPTY_MAP;
		if (((ModelImpl) m).eIsSet(GamlPackage.MODEL__IMPORTS)) {
			result = new TOrderedHashMap();
			for (final Import e : m.getImports()) {
				final String u = e.getImportURI();
				if (u != null) {
					URI uri = URI.createURI(u, true);
					uri = properlyEncodedURI(uri.resolve(baseURI));
					final String label = e.getName();
					result.put(uri, label);
				}
			}
		}
		return result;
	}

	@Override
	public TOrderedHashMap<URI, String> allLabeledImportsOf(final GamlResource r) {
		return r.getCache().get("ImportedURIs", r, new Provider<TOrderedHashMap<URI, String>>() {

			@Override
			public TOrderedHashMap<URI, String> get() {
				return allLabeledImportsOf(r.getURI());
			}
		});
	}

	private class Edge {
		String label;
		final URI target;

		Edge(final String l, final URI target) {
			this.label = l;
			this.target = target;
		}

		URI getTarget() {
			return target;
		}

		String getLabel() {
			return label;
		}

		public void setLabel(final String b) {
			label = b;
		}
	}

	private void addImport(final URI from, final URI to, final String label) {
		index.addVertex(to);
		index.addVertex(from);
		index.addEdge(from, to, new Edge(label, to));
	}

	public static void clearResourceSet(final ResourceSet resourceSet) {
		final boolean wasDeliver = resourceSet.eDeliver();
		try {
			resourceSet.eSetDeliver(false);
			resourceSet.getResources().clear();
		} finally {
			resourceSet.eSetDeliver(wasDeliver);
		}
	}

	@Override
	/**
	 * Synchronized method to avoid concurrent errors in the graph in case of a
	 * parallel resource loader
	 */
	public synchronized EObject updateImports(final GamlResource r) {
		final URI baseURI = properlyEncodedURI(r.getURI());
		final Set<Edge> nativeEdges = index.containsVertex(baseURI) ? index.outgoingEdgesOf(baseURI) : null;
		final Set<Edge> edges = nativeEdges == null || nativeEdges.isEmpty() ? Collections.EMPTY_SET
				: new HashSet(nativeEdges);
		final EObject contents = r.getContents().get(0);
		if (contents == null || !(contents instanceof Model))
			return null;
		final TOrderedHashMap<URI, String> added = getImportsAsAbsoluteURIS(baseURI, (Model) r.getContents().get(0));
		final EObject[] faulty = new EObject[1];
		if (added.forEachEntry(new TObjectObjectProcedure<URI, String>() {

			@Override
			public boolean execute(final URI uri, final String b) {
				if (baseURI.equals(uri))
					return true;
				final Iterator<Edge> iterator = edges.iterator();
				boolean found = false;
				while (iterator.hasNext()) {
					final Edge edge = iterator.next();
					if (edge.getTarget().equals(uri)) {
						found = true;
						if (!Objects.equal(edge.getLabel(), b)) {
							edge.setLabel(b);
						}
						iterator.remove();
						break;
					}
				}
				if (!found)
					if (EcoreUtil2.isValidUri(r, uri)) {
						final boolean alreadyThere = index.containsVertex(uri);
						addImport(baseURI, uri, b);
						if (shouldExpandDependencies() && !alreadyThere) {
							// This call should trigger the recursive call to
							// updateImports()
							final Resource imported = r.getResourceSet().getResource(uri, true);
						}
					} else {
						faulty[0] = findImport((Model) r.getContents().get(0), uri);
						return false;
					}
				return true;
			}

			private EObject findImport(final Model model, final URI uri) {
				for (final Import e : model.getImports()) {
					if (e.getImportURI().contains(URI.decode(uri.lastSegment())))
						return e;
					if (uri.equals(baseURI) && e.getImportURI().isEmpty())
						return e;
				}
				return null;
			}
		})) {
			index.removeAllEdges(edges);
			return null;
		}
		return faulty[0];

	}

	protected boolean shouldExpandDependencies() {
		return true;
	}

	class ResourceLoader implements TObjectObjectProcedure<URI, String> {

		final ResourceSet resourceSet;
		TOrderedHashMap<GamlResource, String> loaded;

		ResourceLoader(final ResourceSet resourceSet2) {
			this.resourceSet = resourceSet2;
		}

		@Override
		public boolean execute(final URI uri, final String label) {
			final GamlResource ir = (GamlResource) resourceSet.getResource(uri, true);
			if (ir != null) {
				if (loaded == null)
					loaded = new TOrderedHashMap(1);
				loaded.put(ir, label);
			}
			return true;

		}

	}

	@Override
	public TOrderedHashMap<GamlResource, String> validateImportsOf(final GamlResource resource) {

		TOrderedHashMap<GamlResource, String> imports = null;
		final TOrderedHashMap<URI, String> uris = allLabeledImportsOf(resource);
		uris.remove(properlyEncodedURI(resource.getURI()));
		if (!uris.isEmpty()) {
			final ResourceLoader loadResources = new ResourceLoader(resource.getResourceSet());
			uris.forEachEntry(loadResources);
			imports = loadResources.loaded;
			// If one of the resources has already errors, no need to validate
			final boolean importsOK = imports == null || imports.forEachKey(new TObjectProcedure<GamlResource>() {

				@Override
				public boolean execute(final GamlResource imported) {
					if (imported.hasErrors()) {
						resource.invalidate(imported, "Errors detected");
						return false;
					}
					return true;
				}

			});
			if (!importsOK) {
				return null;
			}
		}
		return imports == null ? EMPTY_MAP : imports;
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#directImportersOf(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Set<URI> directImportersOf(final URI uri) {
		final URI newURI = properlyEncodedURI(uri);
		if (index.containsVertex(newURI))
			return new HashSet(Graphs.predecessorListOf(index, newURI));
		return Collections.EMPTY_SET;
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#directImportsOf(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Set<URI> directImportsOf(final URI uri) {
		final URI newURI = properlyEncodedURI(uri);
		if (index.containsVertex(newURI))
			return new HashSet(Graphs.successorListOf(index, newURI));
		return Collections.EMPTY_SET;
	}

	private TOrderedHashMap<URI, String> allLabeledImportsOf(final URI uri) {
		final URI newURI = properlyEncodedURI(uri);
		final TOrderedHashMap<URI, String> result = new TOrderedHashMap();
		allLabeledImports(newURI, null, result);
		return result;
	}

	private void allLabeledImports(final URI uri, final String currentLabel, final Map<URI, String> result) {
		if (!result.containsKey(uri)) {
			result.put(uri, currentLabel);
			if (indexes(uri)) {
				final Collection<Edge> edges = index.outgoingEdgesOf(uri);
				for (final Edge e : edges) {
					allLabeledImports(index.getEdgeTarget(e), e.getLabel() == null ? currentLabel : e.getLabel(),
							result);
				}
			}
		}

	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#allImportsOf(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Iterator<URI> allImportsOf(final URI uri) {
		if (!indexes(uri))
			return Iterators.emptyIterator();
		final Iterator<URI> result = new BreadthFirstIterator(index, properlyEncodedURI(uri));
		result.next(); // to eliminate the uri
		return result;
	}

	public boolean indexes(final URI uri) {
		return index.containsVertex(properlyEncodedURI(uri));
	}

	@Override
	public URI properlyEncodedURI(final URI uri) {
		final URI result = URI.createURI(uri.toString(), true);
		return result;
	}

	@Override
	public boolean equals(final URI uri1, final URI uri2) {
		if (uri1 == null)
			return uri2 == null;
		if (uri2 == null)
			return false;
		return properlyEncodedURI(uri1).equals(properlyEncodedURI(uri2));
	}

	@Override
	public void eraseIndex() {
		index = new SimpleDirectedGraph(Edge.class);
	}

}
