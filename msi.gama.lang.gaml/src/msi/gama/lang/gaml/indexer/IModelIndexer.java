package msi.gama.lang.gaml.indexer;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.google.inject.ImplementedBy;

import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.util.TOrderedHashMap;

/**
 * the indexer is used to maintain information about the relationships between
 * models (imports, etc.).
 * 
 * @author drogoul
 *
 */
@ImplementedBy(BaseIndexer.class)
public interface IModelIndexer {

	/**
	 * Returns null or a faulty Eobject if an import has failed
	 * 
	 * @param r
	 * @return
	 */
	EObject updateImports(GamlResource r);

	/**
	 * Returns all the URIs that directly import this URI.
	 * 
	 * @param uri
	 * @return
	 */

	Set<URI> directImportersOf(URI uri);

	/**
	 * Returns all the URIs of resources directly imported by this URI
	 * 
	 * @param uri
	 * @return
	 */

	Set<URI> directImportsOf(URI uri);

	/**
	 * Retuns all the URIS of resources directly or undirectly imported by this
	 * URI
	 * 
	 * @param uri
	 * @return
	 */

	Iterator<URI> allImportsOf(URI uri);

	/**
	 * Returns a properly encoded URI
	 * 
	 * @param uri
	 * @return
	 */

	URI properlyEncodedURI(URI uri);

	void eraseIndex();

	TOrderedHashMap<URI, String> allLabeledImportsOf(GamlResource resource);

	public TOrderedHashMap<GamlResource, String> validateImportsOf(final GamlResource r);

	boolean equals(URI uri, URI uri2);

}