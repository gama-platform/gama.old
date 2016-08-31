package msi.gama.lang.gaml.indexer;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.google.inject.ImplementedBy;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.IGamlBuilderListener.IGamlBuilderListener2;
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
	 * Returns false if an import has failed
	 * 
	 * @param r
	 * @return
	 */
	boolean updateImports(GamlResource r);

	Set<URI> directImportersOf(URI uri);

	Set<URI> directImportsOf(URI uri);

	Iterator<URI> allImportsOf(URI uri);

	Iterator<URI> allImportersOf(URI uri);

	boolean indexes(URI uri);

	URI properlyEncodedURI(URI uri);

	void buildIndex();

	TOrderedHashMap<URI, String> allLabeledImportsOf(GamlResource resource);

	boolean isImported(URI uri);

	boolean isReady();

	/**
	 * Whether the document at the current URI is edited or not
	 * 
	 * @param uri
	 * @return
	 */
	boolean isEdited(URI uri);

	public void updateState(final URI uri, final ModelDescription model, final boolean newState,
			final ErrorCollector status);

	public void addResourceListener(final URI uri, final IGamlBuilderListener2 listener);

	public void removeResourceListener(final IGamlBuilderListener2 listener);

	public THashMap<EObject, IGamlDescription> getDocumentationCache(final URI uri);

	void removeDocumentation(URI toRemove);

	TOrderedHashMap<URI, String> allLabeledImportsOf(URI uri);

	void runAfterIndexation(Runnable runnable);

}