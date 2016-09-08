package msi.gama.lang.gaml.indexer;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.google.inject.ImplementedBy;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.ModelDescription;

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
	 * Whether or not this URI is being indexed
	 * 
	 * @param uri
	 * @return
	 */

	boolean indexes(URI uri);

	/**
	 * Returns a properly encoded URI
	 * 
	 * @param uri
	 * @return
	 */

	URI properlyEncodedURI(URI uri);

	void eraseIndex();

	TOrderedHashMap<URI, String> allLabeledImportsOf(GamlResource resource);

	boolean isImported(URI uri);

	boolean isReady();

	public TOrderedHashMap<GamlResource, String> validateImportsOf(final GamlResource r);

	/**
	 * Whether the document at the current URI is edited or not
	 * 
	 * @param uri
	 * @return
	 */
	boolean isEdited(URI uri);

	public void updateState(final URI uri, final ModelDescription model, final boolean newState,
			final ErrorCollector status);

	public THashMap<EObject, IGamlDescription> getDocumentationCache(final URI uri);

	void removeDocumentation(URI toRemove);

	TOrderedHashMap<URI, String> allLabeledImportsOf(URI uri);

	ErrorCollector getErrorCollector(GamlResource gamlResource);

	boolean equals(URI uri, URI uri2);

	// void waitToBeReady();

	void addResourceListener(URI uri, IGamlBuilderListener listener);

	void removeResourceListener(IGamlBuilderListener listener);

}