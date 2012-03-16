/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gama.lang.utils;

import msi.gaml.compilation.GamlCompilationError;
import org.eclipse.emf.ecore.EObject;

/**
 * The class GamlXtextException.
 * 
 * @author drogoul
 * @since 5 févr. 2012
 * 
 */
public class GamlParsingError extends GamlCompilationError {

	final EObject statement;

	public GamlParsingError(final String string, final EObject gaml) {
		super(string);
		statement = gaml;
	}

	public GamlParsingError(final Throwable e) {
		this(e.getMessage(), GamlToSyntacticElements.currentStatement);
	}

	@Override
	public EObject getStatement() {
		return statement;
	}

}
