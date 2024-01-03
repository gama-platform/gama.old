/*******************************************************************************************************
 *
 * ValidationContext.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.GAMA;
import msi.gama.util.Collector;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.interfaces.IGamlDescription;
import msi.gaml.interfaces.IGamlIssue;
import one.util.streamex.StreamEx;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ValidationContext.
 */
public class ValidationContext extends Collector.AsList<GamlCompilationError> {

	static {
		DEBUG.ON();
	}

	/** The Constant MAX_SIZE. */
	final static int MAX_SIZE = 1000;

	/** The should document. */
	boolean shouldDocument;

	/** The Constant NULL. */
	public static final ValidationContext NULL = new ValidationContext(null, false, IDocManager.NULL);

	/** The resource URI. */
	final URI resourceURI;

	/** The imported errors. */
	Set<GamlCompilationError> importedErrors;

	/** The no experiment. */
	private boolean noWarning, noInfo, hasSyntaxErrors, noExperiment;

	/** The doc delegate. */
	private final IDocManager docDelegate;

	/** The expressions to document. */
	private final Map<EObject, IGamlDescription> expressionsToDocument = new ConcurrentHashMap<>();

	/**
	 * Instantiates a new validation context.
	 *
	 * @param uri
	 *            the uri
	 * @param syntax
	 *            the syntax
	 * @param delegate
	 *            the delegate
	 */
	public ValidationContext(final URI uri, final boolean syntax, final IDocManager delegate) {
		this.resourceURI = uri;
		hasSyntaxErrors = syntax;
		docDelegate = delegate == null ? IDocManager.NULL : delegate;
	}

	@Override
	public boolean add(final GamlCompilationError error) {
		if (error.isWarning()) {
			if (!GamaPreferences.Modeling.WARNINGS_ENABLED.getValue() || noWarning) return false;
		} else if (error.isInfo() && (!GamaPreferences.Modeling.INFO_ENABLED.getValue() || noInfo)) return false;
		final URI uri = error.getURI();
		final boolean sameResource = uri.equals(resourceURI);
		if (sameResource) return super.add(error);
		if (error.isError()) {
			if (importedErrors == null) { importedErrors = new LinkedHashSet<>(); }
			importedErrors.add(error);
			return true;
		}
		return false;
	}

	/** The Constant IS_INFO. */
	public static final Predicate<GamlCompilationError> IS_INFO = GamlCompilationError::isInfo;

	/** The Constant IS_WARNING. */
	public static final Predicate<GamlCompilationError> IS_WARNING = GamlCompilationError::isWarning;

	/** The Constant IS_ERROR. */
	public static final Predicate<GamlCompilationError> IS_ERROR = GamlCompilationError::isError;

	/**
	 * Checks for internal syntax errors.
	 *
	 * @return true, if successful
	 */
	public boolean hasInternalSyntaxErrors() {
		return hasSyntaxErrors;
	}

	/**
	 * Checks for internal syntax errors.
	 *
	 * @param errors
	 *            the errors
	 */
	public void hasInternalSyntaxErrors(final boolean errors) {
		hasSyntaxErrors = errors;
	}

	/**
	 * Checks for errors.
	 *
	 * @return true, if successful
	 */
	public boolean hasErrors() {
		return hasSyntaxErrors || hasInternalErrors() || hasImportedErrors();
	}

	/**
	 * Checks for internal errors.
	 *
	 * @return true, if successful
	 */
	public boolean hasInternalErrors() {
		return Iterables.any(items(), IS_ERROR);
		// return !isEmpty() && StreamEx.of(items()).filter(IS_ERROR).count() > 0;
	}

	/**
	 * Checks for imported errors.
	 *
	 * @return true, if successful
	 */
	public boolean hasImportedErrors() {
		return importedErrors != null && !importedErrors.isEmpty();
	}

	/**
	 * Gets the internal errors.
	 *
	 * @return the internal errors
	 */
	public Iterable<GamlCompilationError> getInternalErrors() {
		return Iterables.filter(items(), IS_ERROR);
		// return StreamEx.of(items()).filter(IS_ERROR).toList();
	}

	/**
	 * Gets the imported errors.
	 *
	 * @return the imported errors
	 */
	public Collection<GamlCompilationError> getImportedErrors() {
		return importedErrors == null ? Collections.EMPTY_LIST : importedErrors;
	}

	/**
	 * Gets the warnings.
	 *
	 * @return the warnings
	 */
	public Iterable<GamlCompilationError> getWarnings() {
		return Iterables.filter(items(), IS_WARNING);
		// return StreamEx.of(items()).filter(IS_WARNING).toList();
	}

	/**
	 * Gets the infos.
	 *
	 * @return the infos
	 */
	public Iterable<GamlCompilationError> getInfos() {
		return Iterables.filter(items(), IS_INFO);
		// return StreamEx.of(items()).filter(IS_INFO).toList();
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
		return Iterables.limit(Iterables.concat(items(), getImportedErrors()), MAX_SIZE).iterator();
		// return StreamEx.of(items()).append(getImportedErrors()).limit(MAX_SIZE).toList().iterator();
	}

	/**
	 * Gets the imported errors as strings.
	 *
	 * @return the imported errors as strings
	 */
	public Map<String, URI> getImportedErrorsAsStrings() {
		if (importedErrors == null) return Collections.EMPTY_MAP;
		Map<String, URI> result = new LinkedHashMap<>();
		importedErrors.forEach(
				e -> result.put(e.toString() + " (in " + URI.decode(e.getURI().lastSegment()) + ")", e.getURI()));
		return result;
		// StreamEx.of(importedErrors).toMap(e -> e.toString() + " (" + URI.decode(e.getURI().lastSegment()) + ")",
		// GamlCompilationError::getURI, (t, u) -> t);
	}

	/**
	 * Sets the no warning.
	 */
	public void setNoWarning() {
		noWarning = true;

	}

	/**
	 * Sets the no info.
	 */
	public void setNoInfo() {
		noInfo = true;
	}

	/**
	 * Reset info and warning.
	 */
	public void resetInfoAndWarning() {
		noInfo = false;
		noWarning = false;

	}

	/**
	 * Do document.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param description
	 *            the description
	 * @date 31 déc. 2023
	 */
	public void doDocument(final ModelDescription description) {
		if (shouldDocument) {
			docDelegate.doDocument(resourceURI, description, expressionsToDocument);
			expressionsToDocument.forEach((e, d) -> {
				// DEBUG.OUT("Documenting Eobject " + e + " in resource "
				// + (e.eResource() == null ? "null" : e.eResource().getURI().lastSegment()) + " with description "
				// + d);
				docDelegate.setGamlDocumentation(resourceURI, e, d);
			});
		}
		expressionsToDocument.clear();
	}

	// /**
	// * Gets the gaml documentation.
	// *
	// * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	// * @param o
	// * the o
	// * @return the gaml documentation
	// * @date 31 déc. 2023
	// */
	// public IGamlDescription getGamlDocumentation(final EObject o) {
	// return docDelegate.getGamlDocumentation(o);
	// }

	/**
	 * Sets the gaml documentation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param e
	 *            the e
	 * @param d
	 *            the d
	 * @date 31 déc. 2023
	 */
	public void setGamlDocumentation(final EObject e, final IGamlDescription d) {
		// Called by SymbolDescription to document individual expressions -- they are kept in a Map<EObject,
		// IGamlDescription> and done when the whole model is documented
		if (shouldDocument) {
			// DEBUG.OUT("Queuing for documentation Eobject " + e + " in resource "
			// + (e.eResource() == null ? "null" : e.eResource().getURI().lastSegment()) + " with description "
			// + d);

			expressionsToDocument.put(e, d);
		}
	}

	/**
	 * Checks for error on.
	 *
	 * @param objects
	 *            the objects
	 * @return true, if successful
	 */
	public boolean hasErrorOn(final EObject... objects) {
		final List<EObject> list = Arrays.asList(objects);
		return StreamEx.of(items()).filter(IS_ERROR).findAny(p -> list.contains(p.getStatement())).isPresent();
	}

	/**
	 * Sets the no experiment.
	 */
	public void setNoExperiment() {
		noExperiment = true;
	}

	/**
	 * Gets the no experiment.
	 *
	 * @return the no experiment
	 */
	public boolean getNoExperiment() { return noExperiment; }

	/**
	 * Verify plugins. Returns true if all the plugins are present in the current platform
	 *
	 * @param list
	 *            the list
	 * @return true, if successful
	 */
	public boolean verifyPlugins(final List<String> list) {
		for (String s : list) {
			if (!GamaBundleLoader.gamlPluginExists(s)) {
				if (!GAMA.isInHeadLessMode() || !GamaBundleLoader.isDisplayPlugin(s)) {
					add(new GamlCompilationError("Missing plugin: " + s, IGamlIssue.MISSING_PLUGIN, resourceURI,
							GamaBundleLoader.isDisplayPlugin(s), false));
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Should document. True by default
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param document
	 * @return true, if successful
	 * @date 30 déc. 2023
	 */
	public boolean shouldDocument() {
		return shouldDocument;
	}

	/**
	 * Should document. Do nothing by default.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param document
	 *            the document
	 * @return true, if successful
	 * @date 30 déc. 2023
	 */
	public void shouldDocument(final boolean document) {
		shouldDocument = document;
	}
}
