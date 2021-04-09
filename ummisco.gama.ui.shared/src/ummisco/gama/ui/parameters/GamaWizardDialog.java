package ummisco.gama.ui.parameters;


import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import msi.gama.util.IList;
import msi.gama.util.IMap;

public class GamaWizardDialog extends WizardDialog{

	GamaWizard wizard;
	public GamaWizardDialog(Shell parentShell, GamaWizard newWizard) {
		super(parentShell, newWizard);
		this.wizard = newWizard;
	}
	
	/*@Override
	protected Point getInitialSize() {
		final var p = super.getInitialSize();
		return new Point(p.x * 2, p.y);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}*/

	public IList<IMap<String, Object>> getValues() {
		return wizard.getValues();
	}
}
