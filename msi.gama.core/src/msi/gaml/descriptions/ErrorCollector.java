package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.util.IErrorCollector;
import msi.gaml.compilation.GamlCompilationError;

public class ErrorCollector implements IErrorCollector {

	private final int SIZE = 200;
	private boolean hasError = false;
	final List<GamlCompilationError> all = new ArrayList();

	// final List<GamlCompilationError> errors = new ArrayList();
	// final List<GamlCompilationError> warnings = new ArrayList();
	// final List<GamlCompilationError> infos = new ArrayList();

	@Override
	public void add(final GamlCompilationError error) {
		if ( !error.isInfo() && !error.isWarning() ) {
			hasError = true;
		}
		if ( all.size() < SIZE ) {
			all.add(error);
			// if ( error.isInfo() ) {
			// infos.add(error);
			// } else if ( error.isWarning() ) {
			// if ( warnings.size() < SIZE ) {
			// warnings.add(error);
			// }
			// } else {
			// if ( errors.size() < SIZE ) {
			// errors.add(error);
			// }
			// }
		}
	}

	@Override
	public boolean hasErrors() {
		return hasError;
	}

	@Override
	public List<GamlCompilationError> get() {
		return all;
	}

	//
	// @Override
	// public List<GamlCompilationError> getErrors() {
	// return errors;
	// }
	//
	// @Override
	// public List<GamlCompilationError> getWarnings() {
	// return warnings;
	// }
	//
	// @Override
	// public List<GamlCompilationError> getInfos() {
	// return infos;
	// }

	public void clear() {
		all.clear();
		// errors.clear();
		// warnings.clear();
		// infos.clear();
	}

}
