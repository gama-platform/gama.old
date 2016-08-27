/*********************************************************************************************
 * 
 * 
 * 'ErrorCollector.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.descriptions;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.limit;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.GamaPreferences;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.GamlCompilationError;

public class ErrorCollector implements Iterable<GamlCompilationError> {

	final static int MAX_SIZE = 1000;
	public static final ErrorCollector BuiltIn = new ErrorCollector();
	boolean hasSyntaxErrors;
	final URI resourceURI;
	final TLinkedHashSet<GamlCompilationError> importedErrors = new TLinkedHashSet();
	final TLinkedHashSet<GamlCompilationError> internalErrors = new TLinkedHashSet();
	final TLinkedHashSet<GamlCompilationError> warnings = new TLinkedHashSet();
	final TLinkedHashSet<GamlCompilationError> infos = new TLinkedHashSet();
	private boolean noWarning, noInfo;

	public ErrorCollector() {
		this(null);
	}

	public ErrorCollector(final Resource resource) {
		this(resource, false);
	}

	public ErrorCollector(final Resource resource, final boolean syntax) {
		this.resourceURI = resource == null ? URI.createURI("builtin://gaml", false) : resource.getURI();
		hasSyntaxErrors = syntax;
	}

	// If the status of the error changes (for instance, it is discovered it
	// belongs to another resource
	public void refresh(final GamlCompilationError e) {
		remove(e);
		add(e);
	}

	public void remove(final GamlCompilationError e) {
		importedErrors.remove(e);
		internalErrors.remove(e);
		warnings.remove(e);
		infos.remove(e);
	}

	public void add(final GamlCompilationError error) {
		if (error.isWarning()) {
			if (!GamaPreferences.WARNINGS_ENABLED.getValue() || noWarning) {
				return;
			}
		} else if (error.isInfo()) {
			if (!GamaPreferences.INFO_ENABLED.getValue() || noInfo) {
				return;
			}
		}
		final EObject object = error.getStatement();
		final boolean sameResource = object == null || object.eResource().getURI().equals(resourceURI);
		if (sameResource && error.isInfo()) {
			infos.add(error);
		} else if (sameResource && error.isWarning()) {
			warnings.add(error);
		} else if (error.isError()) {
			if (sameResource) {
				internalErrors.add(error);
			} else {
				importedErrors.add(error);
			}
		}
	}

	public boolean hasInternalSyntaxErrors() {
		return hasSyntaxErrors;
	}

	public boolean hasErrors() {
		return hasSyntaxErrors || hasInternalErrors() || hasImportedErrors();
	}

	public boolean hasInternalErrors() {
		return internalErrors.size() > 0;
	}

	public boolean hasImportedErrors() {
		return importedErrors.size() > 0;
	}

	public Collection<GamlCompilationError> getInternalErrors() {
		return internalErrors;
	}

	public Collection<GamlCompilationError> getImportedErrors() {
		return importedErrors;
	}

	public Collection<GamlCompilationError> getWarnings() {
		return warnings;
	}

	public Collection<GamlCompilationError> getInfos() {
		return infos;
	}

	public void clear() {
		importedErrors.clear();
		internalErrors.clear();
		warnings.clear();
		infos.clear();
		hasSyntaxErrors = false;
	}

	/**
	 * Method iterator()
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<GamlCompilationError> iterator() {
		return limit(concat(getInternalErrors(), getImportedErrors(), getWarnings(), getInfos()), MAX_SIZE).iterator();
	}

	public Map<String, URI> getImportedErrorsAsStrings() {
		final Map<String, URI> result = new TOrderedHashMap();
		for (final GamlCompilationError error : importedErrors) {
			final EObject object = error.getStatement();
			final String resource = object == null ? "imported files"
					: URI.decode(object.eResource().getURI().lastSegment());
			result.put(error.toString() + " (" + resource + ")", object == null ? null : object.eResource().getURI());
		}
		return result;
	}

	public void setNoWarning() {
		noWarning = true;

	}

	public void setNoInfo() {
		noInfo = true;
	}

	public void resetInfoAndWarning() {
		noInfo = false;
		noWarning = false;

	}

}
