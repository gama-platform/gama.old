/*******************************************************************************************************
 *
 * FontEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaFont;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class FontEditor.
 */
public class FontEditor extends AbstractEditor<GamaFont> {

	/** The edit. */
	private FlatButton edit;

	/**
	 * Instantiates a new font editor.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @param param
	 *            the param
	 * @param l
	 *            the l
	 */
	FontEditor(final IAgent agent, final IParameter param, final EditorListener<GamaFont> l) {
		super(agent, param, l);
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		edit = FlatButton.menu(compo, null, "").light().small();
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
		// Font old = edit.getFont();
		// if (old != null && old != WorkbenchHelper.getDisplay().getSystemFont()) { old.dispose(); }
		edit.setFont(new Font(WorkbenchHelper.getDisplay(), toFontData(data)));
		internalModification = false;
	}

	/**
	 * To gama font.
	 *
	 * @param fd
	 *            the fd
	 * @return the gama font
	 */
	private GamaFont toGamaFont(final FontData fd) {
		return new GamaFont(fd.getName(), fd.getStyle(), fd.getHeight());
	}

	/**
	 * To font data.
	 *
	 * @param gf
	 *            the gf
	 * @return the font data
	 */
	private FontData toFontData(final GamaFont gf) {
		return new FontData(gf.getName(), gf.getSize(), gf.getStyle());
	}

	@Override
	protected int[] getToolItems() { return new int[] { EDIT, REVERT }; }

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
