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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.util.Collector;
import msi.gama.util.ICollector;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.GamlCompilationError;

public class ValidationContext extends Collector.Ordered<GamlCompilationError> implements IDocManager {

	final static int MAX_SIZE = 1000;
	public static final ValidationContext NULL = new ValidationContext(null, false, IDocManager.NULL);
	boolean hasSyntaxErrors;
	final URI resourceURI;
	final ICollector<GamlCompilationError> importedErrors = new Collector.Ordered<>();
	private boolean noWarning, noInfo;
	private final IDocManager docDelegate;

	public ValidationContext(final URI uri, final boolean syntax, final IDocManager delegate) {
		this.resourceURI = uri;
		hasSyntaxErrors = syntax;
		docDelegate = delegate == null ? IDocManager.NULL : delegate;
	}

	@Override
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
		if (sameResource) {
			super.add(error);
		} else if (error.isError()) {
			importedErrors.add(error);
		}
	}

	static Predicate<GamlCompilationError> IS_INFO = input -> input.isInfo();
	static Predicate<GamlCompilationError> IS_WARNING = input -> input.isWarning();
	static Predicate<GamlCompilationError> IS_ERROR = input -> input.isError();

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
		return !isEmpty() && Iterables.size(getInternalErrors()) > 0;
	}

	public boolean hasImportedErrors() {
		return !importedErrors.isEmpty();
	}

	public Iterable<GamlCompilationError> getInternalErrors() {
		return Iterables.filter(items(), IS_ERROR);
	}

	public Collection<GamlCompilationError> getImportedErrors() {
		return importedErrors.items();
	}

	public Iterable<GamlCompilationError> getWarnings() {
		return Iterables.filter(items(), IS_WARNING);
	}

	public Iterable<GamlCompilationError> getInfos() {
		return Iterables.filter(items(), IS_INFO);
	}

	@Override
	public void clear() {
		super.clear();
		importedErrors.clear();
		hasSyntaxErrors = false;
	}

	/**
	 * Method iterator()
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<GamlCompilationError> iterator() {
		return limit(concat(items(), getImportedErrors()), MAX_SIZE).iterator();
	}

	public Map<String, URI> getImportedErrorsAsStrings() {
		final Map<String, URI> result = new TOrderedHashMap<String, URI>();
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
