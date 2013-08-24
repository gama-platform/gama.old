/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Label;

/**
 * The class DisplayOverlay.
 * 
 * @author drogoul
 * @since 19 august 2013
 * 
 */
public class DisplayOverlay extends AbstractOverlay {

	public DisplayOverlay(final LayeredDisplayView view) {
		super(view);
		// getPopup().setCapture(false);
		// getPopup().setEnabled(false);
	}

	@Override
	protected Label createControl() {
		Label l = new Label(getPopup(), SWT.None);
		l.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		return l;
	}

	@Override
	protected Label getControl() {
		return (Label) super.getControl();
	}

	@Override
	protected void populateControl() {
		if ( !getControl().isDisposed() ) {
			getControl().setText(getView().getOverlayText());
		}
	}

	@Override
	protected Point getLocation() {
		return getView().getOverlayPosition();
	}

	@Override
	protected Point getSize() {
		return getView().getOverlaySize();
	}
}
