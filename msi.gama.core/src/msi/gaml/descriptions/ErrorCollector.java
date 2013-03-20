package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.util.IErrorCollector;
import msi.gaml.compilation.GamlCompilationError;

public class ErrorCollector implements IErrorCollector {

	private final int SIZE = 100;
	List<GamlCompilationError> errors = new ArrayList();
	List<GamlCompilationError> warnings = new ArrayList();

	@Override
	public void add(final GamlCompilationError error) {
		if ( error.isWarning() ) {
			if ( warnings.size() < SIZE ) {
				warnings.add(error);
			}
		} else {
			if ( errors.size() < SIZE ) {
				errors.add(error);
			}
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

	public void clear() {
		errors = new ArrayList();
		warnings = new ArrayList();
	}

}
