package ummisco.gama.ui.parameters;

import static org.eclipse.jface.layout.GridLayoutFactory.fillDefaults;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class EditorsGroup extends Composite {

	public EditorsGroup(final Composite parent) {
		this(parent, SWT.NONE);
	}

	public EditorsGroup(final Composite parent, final int style) {
		super(parent, style);
		if (parent.getLayout() instanceof GridLayout) { GridDataFactory.fillDefaults().grab(true, true).applyTo(this); }
		fillDefaults().numColumns(3).spacing(0, 0).extendedMargins(5, 5, 10, 5).equalWidth(false).applyTo(this);
		setBackground(parent.getBackground());
		// Necessary to force SWT to "reskin" and give the right background to the composite (issue in the CSS engine)
		computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
	}

}
