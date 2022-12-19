/*******************************************************************************************************
 *
 * ValidationContext.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.Collector;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import one.util.streamex.StreamEx;

/**
 * The Class ValidationContext.
 */
public class ValidationContext extends Collector.AsList<GamlCompilationError> implements IDocManager {

	/** The Constant MAX_SIZE. */
	final static int MAX_SIZE = 1000;

	/** The Constant NULL. */
	public static final ValidationContext NULL = new ValidationContext(null, false, IDocManager.NULL);

	/** The resource URI. */
	final URI resourceURI;

	/** The imported errors. */
	final Collector.AsList<GamlCompilationError> importedErrors = Collector.getList();

	/** The no experiment. */
	private boolean noWarning, noInfo, hasSyntaxErrors, noExperiment;

	/** The doc delegate. */
	private final IDocManager docDelegate;

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
		return !isEmpty() && StreamEx.of(items()).filter(IS_ERROR).count() > 0;
	}

	/**
	 * Checks for imported errors.
	 *
	 * @return true, if successful
	 */
	public boolean hasImportedErrors() {
		return !importedErrors.isEmpty();
	}

	/**
	 * Gets the internal errors.
	 *
	 * @return the internal errors
	 */
	public List<GamlCompilationError> getInternalErrors() { return StreamEx.of(items()).filter(IS_ERROR).toList(); }

	/**
	 * Gets the imported errors.
	 *
	 * @return the imported errors
	 */
	public Collection<GamlCompilationError> getImportedErrors() { return importedErrors.items(); }

	/**
	 * Gets the warnings.
	 *
	 * @return the warnings
	 */
	public Iterable<GamlCompilationError> getWarnings() { return StreamEx.of(items()).filter(IS_WARNING).toList(); }

	/**
	 * Gets the infos.
	 *
	 * @return the infos
	 */
	public Iterable<GamlCompilationError> getInfos() { return StreamEx.of(items()).filter(IS_INFO).toList(); }

	@Override
	public void clear() {
		super.clear();
		Collector.release(importedErrors);
		hasSyntaxErrors = false;
	}

	/**
	 * Method iterator()
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<GamlCompilationError> iterator() {
		return StreamEx.of(items()).append(getImportedErrors()).limit(MAX_SIZE).toList().iterator();
	}

	/**
	 * Gets the imported errors as strings.
	 *
	 * @return the imported errors as strings
	 */
	public Map<String, URI> getImportedErrorsAsStrings() {
		return StreamEx.of(importedErrors).toMap(e -> e.toString() + " (" + URI.decode(e.getURI().lastSegment()) + ")",
				GamlCompilationError::getURI, (t, u) -> t);
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
	public void setGamlDocumentation(final EObject object, final IGamlDescription description, final boolean replace,
			final boolean force) {
		docDelegate.setGamlDocumentation(object, description, replace, force);
	}

	@Override
	public void addCleanupTask(final ModelDescription model) {
		docDelegate.addCleanupTask(model);
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
				add(new GamlCompilationError("Missing plugin: " + s, IGamlIssue.MISSING_PLUGIN, resourceURI,
						GamaBundleLoader.isDisplayPlugin(s), false));
				return false;
			}
		}
		return true;
	}
}
