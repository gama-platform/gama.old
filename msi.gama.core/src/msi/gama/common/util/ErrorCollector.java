/**
 * Created by drogoul, 17 févr. 2012
 * 
 */
package msi.gama.common.util;

import java.util.*;
import msi.gaml.compilation.GamlException;

/**
 * The class ErrorCollector.
 * 
 * @author drogoul
 * @since 17 févr. 2012
 * 
 */
public class ErrorCollector {

	Set<GamlException> errors;

	public ErrorCollector() {
		errors = new HashSet();
	}

	public Set<GamlException> getErrors() {
		return errors;
	}

	public boolean add(final GamlException error) {
		return errors.add(error);
	}

}
