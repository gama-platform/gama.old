/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.swt.controls;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import org.apache.commons.lang.WordUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

/**
 * The class Popup.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class Popup {

	private static final Shell popup = new Shell(SwtGui.getDisplay(), SWT.ON_TOP);
	private static final Label popupText = new Label(popup, SWT.NONE);
	private static final Listener hide = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			hide();
		}
	};

	static {
		popup.setLayout(new FillLayout());
		popupText.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	private final MouseTrackListener mtl = new MouseTrackListener() {

		@Override
		public void mouseEnter(final MouseEvent e) {
			display();
			isVisible = true;
		}

		@Override
		public void mouseExit(final MouseEvent e) {
			hide();
			isVisible = false;
		}

		@Override
		public void mouseHover(final MouseEvent e) {
			display();
			isVisible = true;
		}

	};

	private final IPopupProvider provider;
	private boolean isVisible;

	/*
	 * 
	 */
	public Popup(final IPopupProvider provider, final Widget ... controls) {
		this.provider = provider;
		Shell parent = provider.getControllingShell();
		parent.addListener(SWT.Move, hide);
		parent.addListener(SWT.Resize, hide);
		parent.addListener(SWT.Close, hide);
		parent.addListener(SWT.Deactivate, hide);
		parent.addListener(SWT.Hide, hide);
		for ( Widget c : controls ) {
			if ( c == null ) {
				continue;
			}
			TypedListener typedListener = new TypedListener(mtl);
			c.addListener(SWT.MouseEnter, typedListener);
			c.addListener(SWT.MouseExit, typedListener);
			c.addListener(SWT.MouseHover, typedListener);
		}
	}

	public void display() {
		// We first verify that the popup is still ok
		Widget c = provider.getControllingShell();
		if ( c == null || c.isDisposed() ) {
			hide();
			return;
		}
		// We then grab the text and hide if it is null or empty
		String s = provider.getPopupText();
		if ( s == null || s.isEmpty() ) {
			hide();
			return;
		}

		// We set the background of the popup by asking the provider
		popupText.setBackground(provider.getPopupBackground());

		// We fix the max. width to 400
		int maxPopupWidth = 400;

		// We compute the width of the text (+ 5 pixels to accomodate for the border)
		GC gc = new GC(popupText);
		int textWidth = gc.textExtent(s).x + 5;
		gc.dispose();
		GuiUtils.debug("Popup.display: textWidth = " + textWidth);
		// We grab the location of the popup on the display
		Point point = provider.getAbsoluteOrigin();
		popup.setLocation(point.x, point.y);

		// We compute the available width on the display given this location (and a border of 5 pixels)
		final Rectangle screenArea = popup.getDisplay().getClientArea();
		int availableWidth = screenArea.x + screenArea.width - point.x - 5;
		GuiUtils.debug("Popup.display: availableWidth = " + availableWidth);
		// We compute the final width of the popup
		int popupWidth = Math.min(availableWidth, maxPopupWidth);
		GuiUtils.debug("Popup.display: popupWidth = " + popupWidth);

		// If the width of the text is greater than the computed width, we wrap the text accordingly, otherwise we
		// shrink the popup to the text width
		if ( textWidth > popupWidth ) {
			// We grab the longest line
			String[] lines = s.split("\\r?\\n");
			int maxLineChars = 0;
			for ( String line : lines ) {
				int lineWidth = line.length();
				maxLineChars = maxLineChars > lineWidth ? maxLineChars : lineWidth;
				GuiUtils.debug("Popup.display: maxLineCharts = " + maxLineChars);
			}
			int wrapLimit = (int) ((double) maxLineChars / (double) textWidth * popupWidth);
			GuiUtils.debug("Popup.display: wrapLimit = " + wrapLimit);

			s = WordUtils.wrap(s, wrapLimit);
		} else {
			popupWidth = textWidth;
		}

		// We set the text of the popup
		popupText.setText(s);

		// We ask the popup to compute its actual size given the width and to display itself
		final Point newPopupSize = popup.computeSize(popupWidth, SWT.DEFAULT);
		popup.setSize(newPopupSize);
		popup.layout();
		popup.setVisible(true);
	}

	public boolean isVisible() {
		return isVisible;
	}

	public static void hide() {
		if ( !popup.isDisposed() ) {
			popup.setVisible(false);
		}
	}
}
