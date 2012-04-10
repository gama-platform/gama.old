/**
 * Created by drogoul, 17 févr. 2012
 * 
 */
package msi.gama.common.util;

import java.util.*;
import msi.gaml.compilation.GamlCompilationError;

/**
 * The class ErrorCollector.
 * 
 * @author drogoul
 * @since 17 févr. 2012
 * 
 */
public class ErrorCollector implements IErrorCollector {

	Set<GamlCompilationError> errors;
	Set<GamlCompilationError> warnings;

	public ErrorCollector() {
		errors = new HashSet();
		warnings = new HashSet();
	}

	public Set<GamlCompilationError> getErrors() {
		return errors;
	}

	public Set<GamlCompilationError> getWarnings() {
		return warnings;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}

	@Override
	public void add(final GamlCompilationError error) {
		if ( error.isWarning() ) {
			warnings.add(error);
		} else {
			errors.add(error);
		}
	}

}
