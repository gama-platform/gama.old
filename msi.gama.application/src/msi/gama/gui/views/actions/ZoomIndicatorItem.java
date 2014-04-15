/*********************************************************************************************
 * 
 *
 * 'ZoomIndicatorItem.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views.actions;

import msi.gama.gui.views.*;
import org.eclipse.jface.action.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class ZoomIndicatorItem extends GamaViewItem {

	Button text;

	/**
	 * @param view
	 */
	ZoomIndicatorItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof IViewWithZoom) ) { throw new IllegalArgumentException(); }
		// ((IViewWithZoom) view).setIndicator(this);
	}

	public void setText(final String s) {
		// GuiUtils.debug("ZoomIndicatorItem.setText " + s);
		if ( text != null && !text.isDisposed() ) {
			text.setText(s);
			text.redraw();
		}
	}

	public String getText() {
		if ( text == null || text.isDisposed() ) { return "N/A"; }
		return text.getText();
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		return new ControlContribution("zoom.indicator") {

			@Override
			protected Control createControl(final Composite parent) {
				text = new Button(parent, SWT.FLAT | SWT.CENTER | SWT.NONE);
				text.setText("100%");
				// text.setEditable(false);
				text.setBackground(parent.getBackground());
				// text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				text.setEnabled(false);
				return text;
			}
		};

	}
}
