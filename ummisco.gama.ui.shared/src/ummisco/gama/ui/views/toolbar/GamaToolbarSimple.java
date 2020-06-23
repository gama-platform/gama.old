/*********************************************************************************************
 *
 * 'GamaToolbarSimple.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * Class GamaToolbar. A declarative wrapper around toolbars
 *
 * @author drogoul
 * @since 3 déc. 2014
 *
 */
public class GamaToolbarSimple extends ToolBar {

	ControlListener widthListener;

	public GamaToolbarSimple(final Composite parent, final int style, final Color c) {
		super(parent, style);
		setBackground(c);
	}

	public GamaToolbarSimple(final Composite parent, final int style) {
		this(parent, style, IGamaColors.WHITE.color());
	}

	@Override
	protected void checkSubclass() {}

	public ToolItem button(final String image, final String text, final String tip, final SelectionListener listener) {
		return create(image, text, tip, listener, SWT.PUSH);
	}

	public ToolItem menu(final String image, final String text, final String tip, final SelectionListener listener) {
		return create(image, text, tip, listener, SWT.DROP_DOWN);
	}

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

	private ToolItem create(final String i, final String text, final String tip, final SelectionListener listener,
			final int style) {
		final ToolItem button = new ToolItem(this, style, getItems().length);
		if (tip != null) {
			button.setToolTipText(tip);
		}
		if (i != null) {
			final Image image = GamaIcons.create(i).image();
			button.setImage(image);
		}
		if (listener != null) {
			button.addSelectionListener(listener);
		}
		return button;
	}

}
