/**
 * Created by drogoul, 3 déc. 2014
 * 
 */
package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.*;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * Class GamaToolbar. A declarative wrapper around toolbars
 * 
 * @author drogoul
 * @since 3 déc. 2014
 * 
 */
public class GamaToolbarSimple extends ToolBar {

	ControlListener widthListener;

	public GamaToolbarSimple(final Composite parent, final int style) {
		super(parent, style);
		setBackground(IGamaColors.WHITE.color());
	}

	@Override
	protected void checkSubclass() {}

	public GamaToolbarSimple width(final Control parent) {
		if ( widthListener != null ) {
			removeControlListener(widthListener);
		}
		widthListener = new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				Rectangle r = getBounds();
				r.width = parent.getBounds().width;
				setBounds(r);
			}

		};
		addControlListener(widthListener);
		return this;
	}

	public ToolItem sep(final int n) {
		ToolItem item = control(new Label(this, SWT.NONE), n);
		item.getControl().setVisible(false);
		return item;
	}

	public ToolItem check(final String image, final String text, final String tip, final SelectionListener listener) {
		return create(image, text, tip, listener, SWT.CHECK);
	}

	public ToolItem button(final String image, final String text, final String tip, final SelectionListener listener) {
		return create(image, text, tip, listener, SWT.PUSH);
	}

	public ToolItem menu(final String image, final String text, final String tip, final SelectionListener listener) {
		return create(image, text, tip, listener, SWT.DROP_DOWN);
	}

	public ToolItem control(final Control c, final int width) {
		final ToolItem control = create(null, null, null, null, SWT.SEPARATOR);
		control.setControl(c);
		if ( width == SWT.DEFAULT ) {
			control.setWidth(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
		} else {
			control.setWidth(width);
		}
		return control;
	}

	private ToolItem create(final String i, final String text, final String tip, final SelectionListener listener,
		final int style) {
		ToolItem button = new ToolItem(this, style, getItems().length);
		if ( tip != null ) {
			button.setToolTipText(tip);
		}
		if ( i != null ) {
			Image image = GamaIcons.create(i).image();
			button.setImage(image);
		}
		if ( listener != null ) {
			button.addSelectionListener(listener);
		}
		return button;
	}

}
