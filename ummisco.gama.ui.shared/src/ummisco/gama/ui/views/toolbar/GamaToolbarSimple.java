/*******************************************************************************************************
 *
 * GamaToolbarSimple.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.GamaIcons;

/**
 * Class GamaToolbar. A declarative wrapper around toolbars
 *
 * @author drogoul
 * @since 3 déc. 2014
 *
 */
public class GamaToolbarSimple extends ToolBar {

	/**
	 * Instantiates a new gama toolbar simple.
	 *
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 * @param c
	 *            the c
	 */
	public GamaToolbarSimple(final Composite parent, final int style) {
		super(parent, style);
	}

	@Override
	protected void checkSubclass() {}

	/**
	 * Button.
	 *
	 * @param image
	 *            the image
	 * @param text
	 *            the text
	 * @param tip
	 *            the tip
	 * @param listener
	 *            the listener
	 * @return the tool item
	 */
	public ToolItem button(final String image, final String text, final String tip, final SelectionListener listener) {
		return create(image, text, tip, listener, SWT.PUSH);
	}

	/**
	 * Menu.
	 *
	 * @param image
	 *            the image
	 * @param text
	 *            the text
	 * @param tip
	 *            the tip
	 * @param listener
	 *            the listener
	 * @return the tool item
	 */
	public ToolItem menu(final String image, final String text, final String tip, final SelectionListener listener) {
		return create(image, text, tip, listener, SWT.DROP_DOWN);
	}

	/**
	 * Control.
	 *
	 * @param c
	 *            the c
	 * @param width
	 *            the width
	 * @return the tool item
	 */
	public ToolItem control(final Control c, final int width) {
		final ToolItem control = create(null, null, null, null, SWT.SEPARATOR);
		control.setControl(c);
		if (width == SWT.DEFAULT) {
			control.setWidth(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
		} else {
			control.setWidth(width);
		}
		return control;
	}

	/**
	 * Creates the.
	 *
	 * @param i
	 *            the i
	 * @param text
	 *            the text
	 * @param tip
	 *            the tip
	 * @param listener
	 *            the listener
	 * @param style
	 *            the style
	 * @return the tool item
	 */
	private ToolItem create(final String i, final String text, final String tip, final SelectionListener listener,
			final int style) {
		final ToolItem button = new ToolItem(this, style, getItems().length);
		if (tip != null) { button.setToolTipText(tip); }
		if (i != null) {
			GamaIcon icon = GamaIcons.create(i);
			button.setImage(icon.image());
			button.setDisabledImage(icon.disabled());
		}
		if (listener != null) { button.addSelectionListener(listener); }
		return button;
	}

}
