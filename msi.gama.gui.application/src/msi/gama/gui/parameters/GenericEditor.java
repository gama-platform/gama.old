/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
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
