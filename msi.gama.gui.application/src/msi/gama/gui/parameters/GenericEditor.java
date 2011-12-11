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
import msi.gama.internal.types.Types;
import msi.gama.util.Cast;
import org.eclipse.swt.widgets.*;

public class GenericEditor extends AbstractEditor {

	ExpressionControl control;
	IType expectedType;

	GenericEditor(final IParameter param) {
		super(param);
		expectedType = param.type();
	}

	GenericEditor(final IAgent agent, final IParameter param) {
		super(agent, param, null);
		expectedType = param.type();
	}

	GenericEditor(final Composite parent, final String title, final Object value,
		final EditorListener whenModified) {
		// Convenience method
		super(new SupportParameter(title, value), whenModified);
		expectedType = value == null ? Types.NO_TYPE : Types.get(value.getClass());
		this.createComposite(parent);
	}

	@Override
	protected Control createCustomParameterControl(final Composite comp) {
		control = new ExpressionControl(comp, this);
		return control.getControl();

	}

	@Override
	public boolean isValueDifferent(final Object newVal) {
		return true;
		// Necessary since some objects (eg GamaGeometry) will not report any
		// modification.
	}

	@Override
	protected void displayParameterValue() {
		control.getControl().setText(Cast.toGaml(currentValue));
	}

	@Override
	public Control getEditorControl() {
		return control.getControl();
	}

	@Override
	public IType getExpectedType() {
		return expectedType;
	}

}
