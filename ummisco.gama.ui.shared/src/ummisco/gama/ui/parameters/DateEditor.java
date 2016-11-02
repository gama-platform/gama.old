/*********************************************************************************************
 *
 * 'DateEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import java.awt.Color;
import java.time.LocalDateTime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaDate;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.resources.IGamaColors;

public class DateEditor extends AbstractEditor<GamaDate> {

	private Composite edit;
	private DateTime date;
	private DateTime time;

	DateEditor(final IScope scope, final IParameter param) {
		super(scope, param);
	}

	DateEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<GamaDate> l) {
		super(scope, agent, param, l);
	}

	DateEditor(final IScope scope, final IAgent agent, final IParameter param) {
		this(scope, agent, param, null);
	}

	DateEditor(final IScope scope, final Composite parent, final String title, final Object value,
			final EditorListener<GamaDate> whenModified) {
		super(scope, new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		modifyAndDisplayValue(GamaDate.of(LocalDateTime.of(date.getYear(), date.getMonth() + 1, date.getDay(),
				time.getHours(), time.getMinutes(), time.getSeconds())));
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		edit = new Composite(compo, SWT.NONE);
		final GridLayout pointEditorLayout = new GridLayout(2, true);
		pointEditorLayout.horizontalSpacing = 10;
		pointEditorLayout.verticalSpacing = 0;
		pointEditorLayout.marginHeight = 0;
		pointEditorLayout.marginWidth = 0;
		edit.setLayout(pointEditorLayout);
		date = new DateTime(edit, SWT.DROP_DOWN | SWT.BORDER | SWT.DATE | SWT.LONG);
		time = new DateTime(edit, SWT.DROP_DOWN | SWT.BORDER | SWT.TIME | SWT.LONG);
		date.setBackground(IGamaColors.PARAMETERS_BACKGROUND.color());
		date.addSelectionListener(this);
		time.setBackground(IGamaColors.PARAMETERS_BACKGROUND.color());
		time.addSelectionListener(this);
		edit.setBackground(IGamaColors.PARAMETERS_BACKGROUND.color());
		displayParameterValue();
		return edit;
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		final GamaDate d = getCurrentValue();
		date.setDate(d.getYear(), d.getMonth() - 1, d.getDay());
		time.setTime(d.getHour(), d.getMinute(), d.getSecond());
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return edit;
	}

	@Override
	public IType<Color> getExpectedType() {
		return Types.DATE;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { REVERT };
	}

}
