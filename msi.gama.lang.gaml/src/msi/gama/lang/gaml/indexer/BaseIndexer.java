package msi.gama.lang.gaml.indexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterators;
import com.google.inject.Provider;

import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.impl.ModelImpl;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.IGamlBuilderListener.IGamlBuilderListener2;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.ModelDescription;

public abstract class BaseIndexer implements IModelIndexer {

	protected final static Map<URI, IGamlBuilderListener2> resourceListeners = new HashMap();

	protected final static TOrderedHashMap EMPTY_MAP = new TOrderedHashMap();

	protected TOrderedHashMap<URI, String> getImportsAsAbsoluteURIS(final URI baseURI, final Model m) {
		TOrderedHashMap<URI, String> result = EMPTY_MAP;
		if (((ModelImpl) m).eIsSet(GamlPackage.MODEL__IMPORTS)) {
			result = new TOrderedHashMap();
			for (final Import e : m.getImports()) {
				URI uri = URI.createURI(e.getImportURI(), true);
				uri = properlyEncodedURI(uri.resolve(baseURI));
				final String label = e.getName();
				result.put(uri, label);
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

	protected boolean validatePlugins(final GamlResource res) {
		return true;
	}

	@Override
	public boolean isEdited(final URI uri) {
		return resourceListeners.containsKey(properlyEncodedURI(uri));
	}

	@Override
	public void updateState(final URI uri, final ModelDescription model, final boolean newState,
			final ErrorCollector status) {
		final URI newURI = properlyEncodedURI(uri);
		final IGamlBuilderListener2 listener = resourceListeners.get(newURI);
		if (listener == null)
			return;
		final Collection exps = model == null ? newState ? Collections.EMPTY_SET : null : model.getExperiments();
		listener.validationEnded(exps, status);
	}

	@Override
	public void addResourceListener(final URI uri, final IGamlBuilderListener2 listener) {
		final URI newURI = properlyEncodedURI(uri);
		resourceListeners.put(newURI, listener);
	}

	@Override
	public void removeResourceListener(final IGamlBuilderListener2 listener) {
		URI toRemove = null;
		for (final Map.Entry<URI, IGamlBuilderListener2> entry : resourceListeners.entrySet()) {
			if (entry.getValue() == listener) {
				toRemove = entry.getKey();
			}
		}
		if (toRemove != null) {
			resourceListeners.remove(toRemove);
			removeDocumentation(toRemove);
		}

	}

	private class NullEdge {
		String getLabel() {
			return null;
		}
	}

	private class Edge extends NullEdge {
		final String label;

		Edge(final String l) {
			this.label = l;
		}

		@Override
		String getLabel() {
			return label;
		}
	}

	protected final DirectedGraph<URI, NullEdge> index = new SimpleDirectedGraph(NullEdge.class);
	protected volatile boolean hasFinishedIndexing;
	private final LoadingCache<URI, THashMap<EObject, IGamlDescription>> documentationCache = CacheBuilder.newBuilder()
			.build(new CacheLoader<URI, THashMap<EObject, IGamlDescription>>() {

				@Override
				public THashMap load(final URI key) throws Exception {
					return new THashMap();
				}
			});

	private final Set<Runnable> afterIndexationRunnables = new HashSet();

	private void addImport(final URI from, final URI to, final String label) {
		index.addVertex(to);
		index.addVertex(from);
		index.addEdge(from, to, label == null ? new NullEdge() : new Edge(label));
	}

	@Override
	public void buildIndex() {
		// Must be redefined & called by redefinitions
		for (final Runnable r : afterIndexationRunnables) {
			r.run();
		}
		afterIndexationRunnables.clear();
	}

	@Override
	public void runAfterIndexation(final Runnable runnable) {
		if (isReady())
			runnable.run();
		else
			afterIndexationRunnables.add(runnable);
	}

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
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#updateImports(msi.gama.lang.gaml.resource.GamlResource)
	 */
	@Override
	public boolean updateImports(final GamlResource r) {
		final URI baseURI = properlyEncodedURI(r.getURI());
		if (indexes(baseURI)) {
			final List<NullEdge> edges = new ArrayList(index.outgoingEdgesOf(baseURI));
			index.removeAllEdges(edges);
		}

		final TOrderedHashMap<URI, String> added = getImportsAsAbsoluteURIS(baseURI, (Model) r.getContents().get(0));
		// System.out.println("Updating imports of " + baseURI.lastSegment() + "
		// with " + added);
		return added.forEachEntry(new TObjectObjectProcedure<URI, String>() {

			@Override
			public boolean execute(final URI uri, final String b) {
				if (!baseURI.equals(uri) && EcoreUtil2.isValidUri(r, uri)) {
					addImport(baseURI, uri, b);
					return true;
				}
				r.getErrorCollector().add(new GamlCompilationError("Imported model could not be found.",
						IGamlIssue.GENERAL, findImport((Model) r.getContents().get(0), uri), false, false));
				return false;
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
		});

	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#directImportersOf(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Set<URI> directImportersOf(final URI uri) {
		final URI newURI = properlyEncodedURI(uri);
		if (indexes(newURI))
			return new HashSet(Graphs.predecessorListOf(index, newURI));
		return Collections.EMPTY_SET;
	}

	@Override
	public boolean isImported(final URI uri) {
		final URI newURI = properlyEncodedURI(uri);
		if (!indexes(newURI))
			return false;
		return index.inDegreeOf(newURI) > 0;
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#directImportsOf(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Set<URI> directImportsOf(final URI uri) {
		final URI newURI = properlyEncodedURI(uri);
		if (indexes(newURI))
			return new HashSet(Graphs.successorListOf(index, newURI));
		return Collections.EMPTY_SET;
	}

	public TOrderedHashMap<URI, String> labeledImportsOf(final URI uri) {
		final URI newURI = properlyEncodedURI(uri);
		if (indexes(newURI)) {
			final TOrderedHashMap<URI, String> map = new TOrderedHashMap();
			final Collection<NullEdge> edges = index.outgoingEdgesOf(newURI);
			for (final NullEdge e : edges) {
				map.put(index.getEdgeTarget(e), e.getLabel());
			}
			return map;
		}
		return EMPTY_MAP;
	}

	@Override
	public TOrderedHashMap<URI, String> allLabeledImportsOf(final URI uri) {
		final URI newURI = properlyEncodedURI(uri);
		final TOrderedHashMap<URI, String> result = new TOrderedHashMap();
		allLabeledImports(newURI, null, result);
		return result;
	}

	private void allLabeledImports(final URI uri, final String currentLabel, final Map<URI, String> result) {
		if (!result.containsKey(uri)) {
			result.put(uri, currentLabel);
			if (indexes(uri)) {
				final Collection<NullEdge> edges = index.outgoingEdgesOf(uri);
				for (final NullEdge e : edges) {
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
		final Iterator<URI> result = new BreadthFirstIterator(index, properlyEncodedURI(uri));
		result.next(); // to eliminate the uri
		return result;
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#allImporterssOf(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Iterator<URI> allImportersOf(final URI uri) {
		return Iterators.emptyIterator();
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#indexes(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public boolean indexes(final URI uri) {
		return index.containsVertex(properlyEncodedURI(uri));
	}

	@Override
	public URI properlyEncodedURI(final URI uri) {
		final URI result = URI.createURI(uri.toString(), true);
		// System.out.println("Converting " + uri.toString() + " to " +
		// result.toString());
		return result;
	}

	@Override
	public boolean isReady() {
		return hasFinishedIndexing;
	}

	@Override
	public THashMap<EObject, IGamlDescription> getDocumentationCache(final URI uri) {
		return documentationCache.getUnchecked(properlyEncodedURI(uri));
	}

	@Override
	public void removeDocumentation(final URI toRemove) {
		documentationCache.invalidate(properlyEncodedURI(toRemove));

	}

}
