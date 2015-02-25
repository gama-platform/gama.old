package msi.gama.gui.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class Separator extends WorkbenchWindowControlContribution {

	public Separator() {}

	public Separator(final String id) {
		super(id);
	}

	@Override
	protected Control createControl(final Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("       ");
		label.setVisible(false);
		return label;
	}

}
