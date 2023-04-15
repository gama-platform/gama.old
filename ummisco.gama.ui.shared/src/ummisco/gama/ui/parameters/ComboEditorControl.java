/*******************************************************************************************************
 *
 * ComboEditorControl.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import static msi.gama.common.util.StringUtils.toGaml;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import msi.gama.application.workbench.ThemeHelper;
import msi.gama.common.util.StringUtils;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * The Class ComboEditorControl.
 */
public class ComboEditorControl extends EditorControl<Combo> {

	/** The possible values. */
	List possibleValues;

	/**
	 * Instantiates a new combo editor control.
	 *
	 * @param editor
	 *            the editor
	 * @param parent
	 *            the parent
	 * @param expectedType
	 *            the expected type
	 * @param possibleValues
	 *            the possible values
	 */
	ComboEditorControl(final AbstractEditor editor, final Composite parent, final IType expectedType,
			final List possibleValues) {
		super(editor, new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN));
		// At least on macOS, the combo is colorized only in dark theme, otherwise it is white
		GamaColors.setForeground(
				ThemeHelper.isDark() ? GamaColors.getTextColorForBackground(control.getBackground()).color()
						: IGamaColors.VERY_DARK_GRAY.color(),
				control);

		buildValues(expectedType, possibleValues);
	}

	/**
	 * Builds the values.
	 *
	 * @param expectedType
	 *            the expected type
	 * @param possibleValues
	 *            the possible values
	 * @return the string[]
	 */
	public void buildValues(final IType expectedType, final List possibleValues) {
		this.possibleValues = possibleValues;
		final var valuesAsString = new String[possibleValues.size()];
		for (var i = 0; i < possibleValues.size(); i++) {
			if (expectedType == Types.STRING) {
				valuesAsString[i] = StringUtils.toJavaString(toGaml(possibleValues.get(i), false));
			} else {
				valuesAsString[i] = toGaml(possibleValues.get(i), false);
			}
		}
		control.setItems(valuesAsString);
		control.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent me) {
				editor.modifyValue(possibleValues.get(control.getSelectionIndex()));
				editor.updateToolbar();
			}
		});
		control.pack();
	}

	@Override
	public void displayParameterValue() {
		if (control.isDisposed()) return;
		Object val = editor.getCurrentValue();
		control.select(possibleValues.indexOf(val));
	}

	/**
	 * Update among values.
	 */
	@Override
	public void updateAmongValues(final List possibleValues) {
		buildValues(editor.getExpectedType(), possibleValues);
	}

}
