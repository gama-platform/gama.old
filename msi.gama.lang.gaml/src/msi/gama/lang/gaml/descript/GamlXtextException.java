/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gama.lang.gaml.descript;

import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.lang.utils.EGaml.LiteralExpression;
import msi.gama.lang.utils.*;
import msi.gaml.compilation.GamlException;
import org.eclipse.emf.ecore.EObject;

/**
 * The class GamlXtextException.
 * 
 * @author drogoul
 * @since 5 févr. 2012
 * 
 */
public class GamlXtextException extends GamlException {

	EObject statement;

	/**
	 * @param string
	 * @param sourceInformation
	 */
	public GamlXtextException(final String string, final ISyntacticElement sourceInformation) {
		super(string, sourceInformation);
		if ( sourceInformation != null ) {
			setStatement(sourceInformation.getUnderlyingElement(null));
		}
	}

	public GamlXtextException(final String string, final EObject gaml) {
		super(string);
		setStatement(gaml);
	}

	public GamlXtextException(final GamlException e) {
		this(e.getMessage(), (EObject) e.getStatement());
	}

	public GamlXtextException(final String string) {
		super(string);
		setStatement(GamlToSyntacticElements.currentStatement);
	}

	public GamlXtextException(final Throwable e) {
		super(e.getMessage());
		setStatement(GamlToSyntacticElements.currentStatement);
	}

	@Override
	public EObject getStatement() {
		return statement;
	}

	/**
	 * @param f
	 */
	public void setStatement(final Object f) {
		statement = f instanceof LiteralExpression ? null : f instanceof EObject ? (EObject) f : null;
	}

}
