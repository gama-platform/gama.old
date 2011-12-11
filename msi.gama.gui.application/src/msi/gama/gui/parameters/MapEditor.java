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

import java.util.Map;
import msi.gama.gui.application.GUI;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.util.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class MapEditor extends AbstractEditor {

	MapEditor(final IParameter param) {
		super(param);
	}

	MapEditor(final IAgent agent, final IParameter param) {
		super(agent, param, null);
	}

	MapEditor(final Composite parent, final String title, final Object value,
		final EditorListener<Map> whenModified) {
		// Convenience method
		super(new SupportParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	private Button mapAdd = null;
	ExpressionControl expression;

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		Composite comp = new Composite(compo, SWT.None);
		comp.setLayoutData(getParameterGridData());
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		comp.setLayout(layout);
		expression = new ExpressionControl(comp, this);
		mapAdd = new Button(comp, SWT.NONE);
		mapAdd.setImage(GUI.editImage);
		mapAdd.setText("Edit");
		mapAdd.setAlignment(SWT.LEFT);
		mapAdd.addSelectionListener(this);
		GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		mapAdd.setLayoutData(d);
		return expression.getControl();
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		MapEditorDialog mapParameterDialog =
			new MapEditorDialog(getScope(), Display.getCurrent().getActiveShell(),
				(GamaMap) currentValue);
		if ( mapParameterDialog.open() == IDialogConstants.OK_ID ) {
			modifyValue(mapParameterDialog.getMap());
		}
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		expression.getControl().setText(Cast.toGaml(currentValue));
		internalModification = false;
		mapAdd.setEnabled(currentValue instanceof GamaMap);
	}

	@Override
	public Control getEditorControl() {
		return expression.getControl();
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.MAP);
	}

}
