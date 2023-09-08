/*******************************************************************************************************
 *
 * GamlEditorState.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.common.util.URI;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IKeyword;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ValidationContext;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * The Class GamlEditorState.
 */
public class GamlEditorState {

	/** The has internal errors. */
	boolean hasInternalErrors;

	/** The has imported errors. */
	boolean hasImportedErrors;

	/** The has experiments. */
	boolean hasExperiments;

	/** The has null status. */
	boolean hasNullStatus;

	/** The show experiments. */
	final boolean showExperiments;

	/** The experiments. */
	public final List<String> experiments;

	/** The abbreviations. */
	public final List<String> abbreviations;

	/** The types. */
	final List<String> types;

	/** The imported errors. */
	final Map<String, URI> importedErrors;

	/**
	 * Instantiates a new gaml editor state.
	 *
	 * @param status
	 *            the status
	 * @param descriptions
	 *            the descriptions
	 */
	public GamlEditorState(final ValidationContext status, final Iterable<? extends IDescription> descriptions) {

		if (status != null) {
			hasImportedErrors = status.hasImportedErrors();
			importedErrors = hasImportedErrors ? status.getImportedErrorsAsStrings() : Collections.EMPTY_MAP;
			hasInternalErrors = status.hasInternalErrors() || status.hasInternalSyntaxErrors();
			hasNullStatus = false;
			showExperiments = !status.getNoExperiment();
		} else {
			hasNullStatus = true;
			importedErrors = Collections.EMPTY_MAP;
			showExperiments = true;
		}
		final int n = Iterables.size(descriptions);
		if (n > 0) {
			hasExperiments = true;
			experiments = new ArrayList<>(n);
			abbreviations = new ArrayList<>(n);
			types = new ArrayList<>(n);
			for (final IDescription ep : descriptions) {
				final String name = ep.getName();
				experiments.add(name);
				abbreviations.add(name);
				// abbreviations.add(name.replaceFirst("Experiment ", ""));
				ExperimentDescription r = (ExperimentDescription) ep;
				types.add(r.isBatch() ? IKeyword.BATCH : r.isMemorize() ? IKeyword.RECORD : IKeyword.GUI_);
			}
		} else {
			experiments = Collections.EMPTY_LIST;
			abbreviations = Collections.EMPTY_LIST;
			types = Collections.EMPTY_LIST;
		}
	}

	/**
	 * Equals 2.
	 *
	 * @param old
	 *            the old
	 * @return true, if successful
	 */
	public boolean equals2(final Object old) {
		if (!(old instanceof final GamlEditorState state)) return false;
		return state.hasNullStatus == hasNullStatus && state.hasImportedErrors == hasImportedErrors
				&& state.hasInternalErrors == hasInternalErrors && state.experiments.equals(experiments)
				&& state.showExperiments == showExperiments && state.types.equals(types);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(experiments, hasImportedErrors, hasInternalErrors, hasNullStatus, showExperiments, types);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		final GamlEditorState other = (GamlEditorState) obj;
		if (!Objects.equals(experiments, other.experiments) || hasImportedErrors != other.hasImportedErrors
				|| hasInternalErrors != other.hasInternalErrors || hasNullStatus != other.hasNullStatus)
			return false;
		if ((showExperiments != other.showExperiments) || !Objects.equals(types, other.types)) return false;
		return true;
	}

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public GamaUIColor getColor() {
		if (hasImportedErrors || hasInternalErrors) return IGamaColors.ERROR;
		if (!hasExperiments) return IGamaColors.WARNING;
		return IGamaColors.OK;
	}

	/** The Constant NO_EXP_DEFINED. */
	public final static String NO_EXP_DEFINED = "No experiments defined";

	/** The Constant ERRORS_DETECTED. */
	public final static String ERRORS_DETECTED = "Error(s) detected";

	/** The Constant IN_IMPORTED_FILES. */
	public final static String IN_IMPORTED_FILES = "Error(s) in imported files";

	/** The Constant IMPOSSIBLE_TO_RUN. */
	public final static String IMPOSSIBLE_TO_RUN = "Impossible to run any experiment";

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		String msg = null;
		if (hasInternalErrors) {
			msg = ERRORS_DETECTED;
			if (hasImportedErrors) { msg = IN_IMPORTED_FILES; }
		} else if (hasImportedErrors) {
			msg = IN_IMPORTED_FILES;
		} else if (!hasExperiments)
			return NO_EXP_DEFINED;
		else
			return null;
		if (hasExperiments) { msg += ". " + IMPOSSIBLE_TO_RUN; }
		return msg;
	}

	/**
	 * Gets the imported errors.
	 *
	 * @return the imported errors
	 */
	public Map<String, URI> getImportedErrors() { return importedErrors; }
}