/*******************************************************************************************************
 *
 * EditorsGroup.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import static org.eclipse.jface.layout.GridLayoutFactory.fillDefaults;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * The Class EditorsGroup.
 */
public class EditorsGroup extends Composite {

	/**
	 * Instantiates a new editors group.
	 *
	 * @param parent
	 *            the parent
	 */
	public EditorsGroup(final Composite parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Instantiates a new editors group.
	 *
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public EditorsGroup(final Composite parent, final int style) {
		super(parent, style | SWT.INHERIT_DEFAULT);
		if (parent.getLayout() instanceof GridLayout) { GridDataFactory.fillDefaults().grab(true, true).applyTo(this); }
		fillDefaults().numColumns(3).spacing(0, 0).extendedMargins(5, 5, 5, 5).equalWidth(false).applyTo(this);
		// GamaColors.setBackground(this, parent.getBackground());
		// Necessary to force SWT to "reskin" and give the right background to the composite (issue in the CSS engine)
		computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
	}

}
