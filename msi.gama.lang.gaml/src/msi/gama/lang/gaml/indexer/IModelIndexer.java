package msi.gama.lang.gaml.indexer;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.URI;

import com.google.inject.ImplementedBy;

import msi.gama.lang.gaml.resource.GamlResource;

/**
 * the indexer is used to maintain information about the relationships between
 * models (imports, etc.).
 * 
 * @author drogoul
 *
 */
@ImplementedBy(MinimalIndexer.class)
public interface IModelIndexer {

	void updateImports(GamlResource r);

	Set<URI> directImportersOf(URI uri);

	Set<URI> directImportsOf(URI uri);

	Iterator<URI> allImportsOf(URI uri);

	Iterator<URI> allImportersOf(URI uri);

	boolean indexes(URI uri);

	URI properlyEncodedURI(URI uri);

	void buildIndex();

}