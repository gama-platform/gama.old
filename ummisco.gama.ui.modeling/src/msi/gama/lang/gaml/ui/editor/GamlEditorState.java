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

import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;

class GamlEditorState {

	boolean hasInternalErrors;
	boolean hasImportedErrors;
	boolean hasExperiments;
	boolean hasNullStatus;
	final List<String> experiments;
	final List<String> abbreviations;
	final List<Boolean> types;
	final Map<String, URI> importedErrors;

	public GamlEditorState(final ErrorCollector status, final Collection<? extends IDescription> descriptions) {

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
			experiments = new ArrayList(n);
			abbreviations = new ArrayList(n);
			types = new ArrayList(n);
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
		if (hasInternalErrors) {
			return IGamaColors.ERROR;
		}
		if (hasImportedErrors) {
			return IGamaColors.IMPORTED;
		}
		if (!hasExperiments) {
			return IGamaColors.WARNING;
		}
		return IGamaColors.OK;
	}

	public String getStatus() {
		String msg = null;
		if (hasInternalErrors) {
			msg = "Error(s) were detected";
		} else if (hasImportedErrors) {
			msg = "This model is functional, but error(s) were detected when importing files";
		} else if (!hasExperiments) {
			return "This model is functional, but no experiments have been defined";
		} else {
			return null;
		}
		if (hasExperiments) {
			msg += ". Impossible to run any experiment";
		}
		return msg;
	}

	public Map<String, URI> getImportedErrors() {
		return importedErrors;
	}
}