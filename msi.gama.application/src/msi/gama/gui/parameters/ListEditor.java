/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.StringUtils;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaList;
import msi.gaml.types.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ListEditor extends AbstractEditor {

	private Button listAdd;
	private ExpressionControl expression;

	ListEditor(final IParameter param) {
		super(param);
	}

	ListEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	ListEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	ListEditor(final Composite parent, final String title, final Object value,
		final EditorListener<java.util.List> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		currentValue = getOriginalValue();
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
		listAdd.setImage(IGamaIcons.BUTTON_EDIT.image());
		listAdd.setText("Edit");

		GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		listAdd.setLayoutData(d);
		return expression.getControl();
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if ( currentValue instanceof GamaList ) {
			ListEditorDialog d =
				new ListEditorDialog(Display.getCurrent().getActiveShell(), (GamaList) currentValue, param.getName());
			if ( d.open() == IDialogConstants.OK_ID ) {
				modifyAndDisplayValue(d.getList(ListEditor.this));
			}
		}
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		expression.getControl().setText(StringUtils.toGaml(currentValue));
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
