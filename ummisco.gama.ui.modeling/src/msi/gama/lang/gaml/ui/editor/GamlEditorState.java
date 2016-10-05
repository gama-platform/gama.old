/**
 * Created by drogoul, 7 mars 2015
 * 
 */
package msi.gama.lang.gaml.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;

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
	public final List<String> experiments;
	public final List<String> abbreviations;
	final List<Boolean> types;
	final Map<String, URI> importedErrors;

	public GamlEditorState(final ValidationContext status, final Collection<? extends IDescription> descriptions) {

		if (status != null) {
			hasImportedErrors = status.hasImportedErrors();
			importedErrors = hasImportedErrors ? status.getImportedErrorsAsStrings() : Collections.EMPTY_MAP;
			hasInternalErrors = status.hasInternalErrors() || status.hasInternalSyntaxErrors();
			hasNullStatus = false;
		} else {
			hasNullStatus = true;
			importedErrors = Collections.EMPTY_MAP;
		}
		final int n = descriptions.size();
		if (n > 0) {
			hasExperiments = true;
			experiments = new ArrayList<>(n);
			abbreviations = new ArrayList<>(n);
			types = new ArrayList<>(n);
			for (final IDescription ep : descriptions) {
				final String name = ep.getName();
				experiments.add(name);
				abbreviations.add(name.replaceFirst("Experiment ", ""));
				types.add(((ExperimentDescription) ep).isBatch());
			}
		} else {
			experiments = Collections.EMPTY_LIST;
			abbreviations = Collections.EMPTY_LIST;
			types = Collections.EMPTY_LIST;
		}
	}

	@Override
	public boolean equals(final Object old) {
		if (!(old instanceof GamlEditorState)) {
			return false;
		}
		final GamlEditorState state = (GamlEditorState) old;
		return state.hasNullStatus == hasNullStatus && state.hasImportedErrors == hasImportedErrors
				&& state.hasInternalErrors == hasInternalErrors && state.experiments.equals(experiments)
				&& state.types.equals(types);
	}

	public GamaUIColor getColor() {
		if (hasImportedErrors) {
			return IGamaColors.ERROR;
		}
		if (hasInternalErrors) {
			return IGamaColors.ERROR;
		}
		if (!hasExperiments) {
			return IGamaColors.WARNING;
		}
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