/**
 * Created by drogoul, 17 févr. 2012
 * 
 */
package msi.gama.headless.common;

import java.util.*;
import msi.gama.common.util.IErrorCollector;
import msi.gaml.compilation.GamlCompilationError;

/**
 * The class ErrorCollector. A simple implementation for headless compilation error handling.
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

	// @Override
	// public boolean hasErrors() {
	// return !errors.isEmpty();
	// }

	@Override
	public void add(final GamlCompilationError error) {
		if ( error.isWarning() ) {
			warnings.add(error);
		} else {
			errors.add(error);
		}
	}

}
