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

	Label text;

	public DisplayOverlay(final LayeredDisplayView view) {
		super(view);
	}

	@Override
	protected void createPopupControl() {
		text = new Label(getPopup(), SWT.None);
		text.setLayoutData(null);
		text.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		text.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	@Override
	public void update() {
		if ( !text.isDisposed() ) {
			text.setText(getView().getOverlayText());
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
