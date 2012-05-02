package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.util.IErrorCollector;
import msi.gaml.compilation.GamlCompilationError;

public class ErrorCollector implements IErrorCollector {

	List<GamlCompilationError> errors = new ArrayList();
	List<GamlCompilationError> warnings = new ArrayList();

	@Override
	public void add(final GamlCompilationError error) {
		if ( error.isWarning() ) {
			warnings.add(error);
		} else {
			errors.add(error);
		}
	}

	@Override
	public List<GamlCompilationError> getErrors() {
		return errors;
	}

	@Override
	public List<GamlCompilationError> getWarnings() {
		return warnings;
	}

}
