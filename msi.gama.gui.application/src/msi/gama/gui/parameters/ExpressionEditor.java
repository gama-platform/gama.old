/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import msi.gama.interfaces.*;
import org.eclipse.swt.widgets.*;

public class ExpressionEditor extends GenericEditor {

	String expressionText;

	ExpressionEditor(final Composite parent, final String title, final Object value,
		final EditorListener<IExpression> whenModified, final IType expectedType) {
		super(parent, title, value, whenModified);
		this.expectedType = expectedType;
	}

	@Override
	protected Control createCustomParameterControl(final Composite comp) {
		if ( currentValue instanceof String ) {
			expressionText = (String) currentValue;
		} else if ( currentValue instanceof IExpression ) {
			expressionText = ((IExpression) currentValue).toGaml();
		}
		return super.createCustomParameterControl(comp);
	}

	@Override
	protected void displayParameterValue() {
		control.getControl().setText(expressionText);
	}

	@Override
	protected boolean acceptTooltip() {
		return false;
	}

	@Override
	public boolean evaluateExpression() {
		return false;
	}

}
