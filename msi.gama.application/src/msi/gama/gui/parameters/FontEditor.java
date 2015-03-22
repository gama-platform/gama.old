/*********************************************************************************************
 * 
 * 
 * 'ColorEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.FlatButton;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class FontEditor extends AbstractEditor<FontData> {

	private FlatButton edit;

	FontEditor(final IParameter param) {
		super(param);
	}

	FontEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	FontEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	FontEditor(final Composite parent, final String title, final Object value,
		final EditorListener<FontData> whenModified) {
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		edit = FlatButton.menu(compo, IGamaColors.GRAY_LABEL, "").light().small();
		edit.addSelectionListener(this);
		displayParameterValue();
		return edit;
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		FontData data = currentValue != null ? currentValue : SwtGui.getSmallFont().getFontData()[0];
		edit.setText(data.toString());
		edit.setFont(new Font(SwtGui.getDisplay(), data));
		internalModification = false;
	}

	public void modifyValue() {

	}

	@Override
	public Control getEditorControl() {
		return edit;
	}

	// @Override
	// public IType getExpectedType() {
	// return Types.STRING;
	// }

	@Override
	protected int[] getToolItems() {
		return new int[] { EDIT, REVERT };
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		FontDialog dialog = new FontDialog(SwtGui.getShell());
		dialog.setEffectsVisible(false);
		FontData data = currentValue;
		dialog.setFontList(new FontData[] { data });
		data = dialog.open();
		if ( data != null ) {
			modifyAndDisplayValue(data);
		}

	}
}
