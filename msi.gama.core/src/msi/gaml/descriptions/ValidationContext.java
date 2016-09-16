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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.GamlCompilationError;

public class ValidationContext implements Iterable<GamlCompilationError>, IDocManager {

	final static int MAX_SIZE = 1000;
	public static final ValidationContext NULL = new ValidationContext(null, false, IDocManager.NULL);
	boolean hasSyntaxErrors;
	final URI resourceURI;
	ArrayList<GamlCompilationError> importedErrors;
	ArrayList<GamlCompilationError> internalErrors;
	ArrayList<GamlCompilationError> warnings;
	ArrayList<GamlCompilationError> infos;
	private boolean noWarning, noInfo;
	private final IDocManager docDelegate;

	public ValidationContext(final URI uri, final boolean syntax, final IDocManager delegate) {
		this.resourceURI = uri;
		hasSyntaxErrors = syntax;
		docDelegate = delegate == null ? IDocManager.NULL : delegate;
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
		final URI uri = error.getURI();
		final boolean sameResource = uri.equals(resourceURI);
		if (sameResource && error.isInfo()) {
			if (infos == null)
				infos = new ArrayList();
			infos.add(error);
		} else if (sameResource && error.isWarning()) {
			if (warnings == null)
				warnings = new ArrayList();
			warnings.add(error);
		} else if (error.isError()) {
			if (sameResource) {
				if (internalErrors == null)
					internalErrors = new ArrayList();
				internalErrors.add(error);
			} else {
				if (importedErrors == null)
					importedErrors = new ArrayList();
				importedErrors.add(error);
			}
		}
	}

	public boolean hasInternalSyntaxErrors() {
		return hasSyntaxErrors;
	}

	public void hasInternalSyntaxErrors(final boolean errors) {
		hasSyntaxErrors = errors;
	}

	public boolean hasErrors() {
		return hasSyntaxErrors || hasInternalErrors() || hasImportedErrors();
	}

	public boolean hasInternalErrors() {
		return internalErrors != null && internalErrors.size() > 0;
	}

	public boolean hasImportedErrors() {
		return importedErrors != null && importedErrors.size() > 0;
	}

	public Collection<GamlCompilationError> getInternalErrors() {
		return internalErrors == null ? Collections.EMPTY_LIST : internalErrors;
	}

	public Collection<GamlCompilationError> getImportedErrors() {
		return importedErrors == null ? Collections.EMPTY_LIST : importedErrors;
	}

	public Collection<GamlCompilationError> getWarnings() {
		return warnings == null ? Collections.EMPTY_LIST : warnings;
	}

	public Collection<GamlCompilationError> getInfos() {
		return infos == null ? Collections.EMPTY_LIST : infos;
	}

	public void clear() {
		importedErrors = null;
		internalErrors = null;
		warnings = null;
		infos = null;
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
		final Map<String, URI> result = new TOrderedHashMap(importedErrors.size());
		if (importedErrors != null)
			for (final GamlCompilationError error : importedErrors) {
				final URI uri = error.getURI();
				final String resource = URI.decode(uri.lastSegment());
				result.put(error.toString() + " (" + resource + ")", uri);
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

	@Override
	public void document(final IDescription description) {
		docDelegate.document(description);
	}

	@Override
	public IGamlDescription getGamlDocumentation(final EObject o) {
		return docDelegate.getGamlDocumentation(o);
	}

	@Override
	public IGamlDescription getGamlDocumentation(final IGamlDescription o) {
		return docDelegate.getGamlDocumentation(o);
	}

	@Override
	public void setGamlDocumentation(final EObject object, final IGamlDescription description, final boolean replace) {
		docDelegate.setGamlDocumentation(object, description, replace);
	}

	@Override
	public void addCleanupTask(final ModelDescription model) {
		docDelegate.addCleanupTask(model);
	}

}
