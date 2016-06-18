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
package ummisco.gama.ui.parameters;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaFont;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class FontEditor extends AbstractEditor<GamaFont> {

	private FlatButton edit;

	FontEditor(final IScope scope, final IParameter param) {
		super(scope, param);
	}

	FontEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener l) {
		super(scope, agent, param, l);
	}

	FontEditor(final IScope scope, final IAgent agent, final IParameter param) {
		this(scope, agent, param, null);
	}

	FontEditor(final IScope scope, final Composite parent, final String title, final Object value,
		final EditorListener<GamaFont> whenModified) {
		super(scope, new InputParameter(title, value), whenModified);
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
		final GamaFont data =
			currentValue != null ? currentValue : toGamaFont(GamaFonts.getSmallFont().getFontData()[0]);
		edit.setText(data.toString());
		edit.setFont(new Font(WorkbenchHelper.getDisplay(), toFontData(data)));
		internalModification = false;
	}

	private GamaFont toGamaFont(final FontData fd) {
		return new GamaFont(fd.getName(), fd.getStyle(), fd.getHeight());
	}

	private FontData toFontData(final GamaFont gf) {
		return new FontData(gf.getName(), gf.getSize(), gf.getStyle());
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
		final FontDialog dialog = new FontDialog(WorkbenchHelper.getShell());
		dialog.setEffectsVisible(false);
		FontData data = toFontData(currentValue);
		dialog.setFontList(new FontData[] { data });
		data = dialog.open();
		if ( data != null ) {
			modifyAndDisplayValue(toGamaFont(data));
		}

	}
}
