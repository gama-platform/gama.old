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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

public class BooleanEditor extends AbstractEditor {

	Button button;

	BooleanEditor(final IParameter param) {
		super(param);
	}

	BooleanEditor(final Composite parent, final String title, final boolean value,
		final EditorListener<Boolean> whenModified) {
		super(new SupportParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	BooleanEditor(final IAgent agent, final IParameter param) {
		super(agent, param, null);
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		if ( !internalModification ) {
			modifyAndDisplayValue(button.getSelection());
		}
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		button = new Button(comp, SWT.CHECK);
		button.addSelectionListener(this);
		return button;
	}

	@Override
	protected void displayParameterValue() {
		Boolean b = (Boolean) currentValue;
		button.setText(b ? "true" : "false");
		button.setSelection(b);
	}

	@Override
	public Control getEditorControl() {
		return button;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.BOOL);
	}

}
