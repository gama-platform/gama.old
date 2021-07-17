/*********************************************************************************************
 *
 * 'FontEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
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

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaFont;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class FontEditor extends AbstractEditor<GamaFont> {

	private FlatButton edit;

	FontEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<GamaFont> l) {
		super(scope, agent, param, l);
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
		final var data = currentValue != null ? currentValue
				: toGamaFont(WorkbenchHelper.getDisplay().getSystemFont().getFontData()[0]);
		edit.setText(currentValue == null ? "Default" : data.toString());
		//Font old = edit.getFont();
		//if (old != null && old != WorkbenchHelper.getDisplay().getSystemFont()) { old.dispose(); }
		edit.setFont(new Font(WorkbenchHelper.getDisplay(), toFontData(data)));
		internalModification = false;
	}

	private GamaFont toGamaFont(final FontData fd) {
		return new GamaFont(fd.getName(), fd.getStyle(), fd.getHeight());
	}

	private FontData toFontData(final GamaFont gf) {
		return new FontData(gf.getName(), gf.getSize(), gf.getStyle());
	}

	@Override
	public Control getEditorControl() {
		return edit;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { EDIT, REVERT };
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final var dialog = new FontDialog(WorkbenchHelper.getShell());
		dialog.setEffectsVisible(false);
		if (currentValue != null) {
			final var data = toFontData(currentValue);
			dialog.setFontList(new FontData[] { data });
		}
		final var data = dialog.open();
		if (data != null) { modifyAndDisplayValue(toGamaFont(data)); }

	}
}
