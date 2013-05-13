/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.common.util.GuiUtils;
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
		((IViewWithZoom) view).setIndicator(this);
	}

	public void setText(String s) {
		GuiUtils.debug("ZoomIndicatorItem.setText " + s);
		if ( text != null && !text.isDisposed() ) {
			text.setText(s);
			text.redraw();
		}
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		return new ControlContribution("zoom.indicator") {

			@Override
			protected Control createControl(Composite parent) {
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
