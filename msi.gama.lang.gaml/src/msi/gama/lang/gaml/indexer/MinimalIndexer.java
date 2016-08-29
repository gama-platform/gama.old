package msi.gama.lang.gaml.indexer;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.URI;

import com.google.common.collect.Iterators;

import msi.gama.lang.gaml.resource.GamlResource;

/**
 * Default implementation in a non-workspace based context. Does not index
 * anything
 * 
 * @author drogoul
 *
 */
public class MinimalIndexer implements IModelIndexer {

	@Override
	public void updateImports(final GamlResource r) {
	}

	@Override
	public Set<URI> directImportersOf(final URI uri) {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<URI> directImportsOf(final URI uri) {
		return Collections.EMPTY_SET;
	}

	@Override
	public Iterator<URI> allImportsOf(final URI uri) {
		return Iterators.emptyIterator();
	}

	@Override
	public Iterator<URI> allImportersOf(final URI uri) {
		return Iterators.emptyIterator();
	}

	@Override
	public boolean indexes(final URI uri) {
		return false;
	}

	@Override
	public URI properlyEncodedURI(final URI uri) {
		return uri;
	}

	@Override
	public void buildIndex() {
	}

}
