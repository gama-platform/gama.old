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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

public class MatrixEditor extends AbstractEditor {

	Button button;

	MatrixEditor(final IParameter param) {
		super(param);
	}

	MatrixEditor(final IAgent agent, final IParameter param) {
		super(agent, param, null);
	}

	MatrixEditor(final Composite parent, final String title, final Object value,
		final EditorListener<IMatrix> whenModified) {
		// Convenience method
		super(new SupportParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		button = new Button(comp, SWT.FLAT + SWT.CENTER);
		button.setAlignment(SWT.LEFT);
		button.addSelectionListener(this);

		currentValue = originalValue;
		return button;

	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		MatrixEditorDialog d =
			new MatrixEditorDialog(Display.getCurrent().getActiveShell(), (IMatrix) currentValue);
		if ( d.open() == IDialogConstants.OK_ID ) {
			modifyValue(d.getMatrix());
		}
	}

	@Override
	protected void displayParameterValue() {
		button.setText(Cast.toGaml(currentValue));
	}

	@Override
	public Control getEditorControl() {
		return button;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.MATRIX);
	}

}
