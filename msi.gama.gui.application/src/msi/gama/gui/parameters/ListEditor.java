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

import msi.gama.gui.application.GUI;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.util.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ListEditor extends AbstractEditor {

	Button listAdd;
	ExpressionControl expression;

	ListEditor(final IParameter param) {
		super(param);
	}

	ListEditor(final IAgent agent, final IParameter param) {
		super(agent, param, null);
	}

	ListEditor(final Composite parent, final String title, final Object value,
		final EditorListener<java.util.List> whenModified) {
		// Convenience method
		super(new SupportParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		currentValue = originalValue;
		Composite comp = new Composite(compo, SWT.None);
		comp.setLayoutData(getParameterGridData());
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		comp.setLayout(layout);
		expression = new ExpressionControl(comp, this);

		listAdd = new Button(comp, SWT.FLAT);
		listAdd.setAlignment(SWT.CENTER);
		listAdd.addSelectionListener(this);
		listAdd.setImage(GUI.editImage);
		listAdd.setText("Edit");

		GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		listAdd.setLayoutData(d);
		return expression.getControl();
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if ( currentValue instanceof GamaList ) {
			ListEditorDialog d =
				new ListEditorDialog(Display.getCurrent().getActiveShell(),
					(GamaList) currentValue, param.getName());
			if ( d.open() == IDialogConstants.OK_ID ) {
				modifyAndDisplayValue(d.getList(ListEditor.this));
			}
		}
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		expression.getControl().setText(Cast.toGaml(currentValue));
		internalModification = false;
		listAdd.setEnabled(currentValue instanceof GamaList);

	}

	@Override
	public Control getEditorControl() {
		return expression.getControl();
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.LIST);
	}

}
