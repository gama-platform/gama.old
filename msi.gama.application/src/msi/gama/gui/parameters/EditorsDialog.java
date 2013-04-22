/**
 * Created by drogoul, 10 mai 2012
 * 
 */
package msi.gama.gui.parameters;

import java.util.*;
import msi.gama.gui.swt.SwtGui;
import msi.gaml.types.IType;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * The class EditorsDialog.
 * 
 * @author drogoul
 * @since 10 mai 2012
 * 
 */
public class EditorsDialog extends Dialog {

	private final Map<String, Object> values;
	private final Map<String, IType> types;
	private final String title;

	public EditorsDialog(final Shell parentShell, final Map<String, Object> values, final Map<String, IType> types,
		final String title) {
		super(parentShell);
		this.title = title;
		setShellStyle(SWT.RESIZE | SWT.BORDER);
		this.values = new LinkedHashMap(values);
		this.types = types;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		// getShell().setText(title);
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout layout = (GridLayout) composite.getLayout();
		layout.numColumns = 2;
		Label text = new Label(composite, SWT.None);
		text.setBackground(SwtGui.COLOR_OK);
		text.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		text.setText(title);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		text.setLayoutData(data);
		Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		data.heightHint = 20;
		sep.setLayoutData(data);
		for ( Map.Entry<String, Object> entry : values.entrySet() ) {
			EditorFactory.create(composite,
				new InputParameter(entry.getKey(), entry.getValue(), types.get(entry.getKey())) {

					@Override
					public void setValue(final Object value) {
						super.setValue(value);
						values.put(getTitle(), value);
					}

				});
		}
		composite.layout();
		composite.pack();

		return composite;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	public Map<String, Object> getValues() {
		return values;
	}

}
