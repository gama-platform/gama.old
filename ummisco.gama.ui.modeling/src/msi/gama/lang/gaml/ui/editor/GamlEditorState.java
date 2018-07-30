/*********************************************************************************************
 *
 * 'GamlEditorState.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;

import com.google.common.collect.Iterables;

import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ValidationContext;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;

public class GamlEditorState {

	boolean hasInternalErrors;
	boolean hasImportedErrors;
	boolean hasExperiments;
	boolean hasNullStatus;
	final boolean showExperiments;
	public final List<String> experiments;
	public final List<String> abbreviations;
	final List<String> types;
	final Map<String, URI> importedErrors;

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
				abbreviations.add(name.replaceFirst("Experiment ", ""));
				types.add(((ExperimentDescription) ep).getExperimentType());
			}
		} else {
			experiments = Collections.EMPTY_LIST;
			abbreviations = Collections.EMPTY_LIST;
			types = Collections.EMPTY_LIST;
		}
	}

	public boolean equals2(final Object old) {
		if (!(old instanceof GamlEditorState)) { return false; }
		final GamlEditorState state = (GamlEditorState) old;
		return state.hasNullStatus == hasNullStatus && state.hasImportedErrors == hasImportedErrors
				&& state.hasInternalErrors == hasInternalErrors && state.experiments.equals(experiments)
				&& state.showExperiments == showExperiments && state.types.equals(types);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (experiments == null ? 0 : experiments.hashCode());
		result = prime * result + (hasImportedErrors ? 1231 : 1237);
		result = prime * result + (hasInternalErrors ? 1231 : 1237);
		result = prime * result + (hasNullStatus ? 1231 : 1237);
		result = prime * result + (showExperiments ? 1231 : 1237);
		result = prime * result + (types == null ? 0 : types.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final GamlEditorState other = (GamlEditorState) obj;
		if (experiments == null) {
			if (other.experiments != null) { return false; }
		} else if (!experiments.equals(other.experiments)) { return false; }
		if (hasImportedErrors != other.hasImportedErrors) { return false; }
		if (hasInternalErrors != other.hasInternalErrors) { return false; }
		if (hasNullStatus != other.hasNullStatus) { return false; }
		if (showExperiments != other.showExperiments) { return false; }
		if (types == null) {
			if (other.types != null) { return false; }
		} else if (!types.equals(other.types)) { return false; }
		return true;
	}

	public GamaUIColor getColor() {
		if (hasImportedErrors) { return IGamaColors.ERROR; }
		if (hasInternalErrors) { return IGamaColors.ERROR; }
		if (!hasExperiments) { return IGamaColors.WARNING; }
		return IGamaColors.OK;
	}

	public final static String NO_EXP_DEFINED = "No experiments defined";
	public final static String ERRORS_DETECTED = "Error(s) detected";
	public final static String IN_IMPORTED_FILES = "Error(s) in imported files";
	public final static String IMPOSSIBLE_TO_RUN = "Impossible to run any experiment";

	public String getStatus() {
		String msg = null;
		if (hasInternalErrors) {
			msg = ERRORS_DETECTED;
			if (hasImportedErrors) {
				msg = IN_IMPORTED_FILES;
			}
		} else if (hasImportedErrors) {
			msg = IN_IMPORTED_FILES;
		} else if (!hasExperiments) {
			return NO_EXP_DEFINED;
		} else {
			return null;
		}
		if (hasExperiments) {
			msg += ". " + IMPOSSIBLE_TO_RUN;
		}
		return msg;
	}

	public Map<String, URI> getImportedErrors() {
		return importedErrors;
	}
}