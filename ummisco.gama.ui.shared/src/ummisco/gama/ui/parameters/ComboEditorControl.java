package ummisco.gama.ui.parameters;

import static msi.gama.application.workbench.ThemeHelper.isDark;
import static msi.gama.common.util.StringUtils.toGaml;
import static ummisco.gama.ui.resources.IGamaColors.BLACK;
import static ummisco.gama.ui.resources.IGamaColors.VERY_LIGHT_GRAY;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.util.StringUtils;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class ComboEditorControl extends EditorControl<Combo> {

	final List possibleValues;

	ComboEditorControl(final AbstractEditor editor, final Composite parent, final IType expectedType,
			final List possibleValues) {
		super(editor, new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN));
		this.possibleValues = possibleValues;
		final var valuesAsString = new String[possibleValues.size()];
		for (var i = 0; i < possibleValues.size(); i++) {
			if (expectedType == Types.STRING) {
				valuesAsString[i] = StringUtils.toJavaString(toGaml(possibleValues.get(i), false));
			} else {
				valuesAsString[i] = toGaml(possibleValues.get(i), false);
			}
		}
		setForeground(isDark() ? VERY_LIGHT_GRAY.color() : BLACK.color());
		// force text color, see #2601
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

}
