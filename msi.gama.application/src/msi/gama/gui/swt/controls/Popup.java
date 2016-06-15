/*********************************************************************************************
 *
 *
 * 'Popup.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;
import msi.gama.gui.swt.SwtGui;
import msi.gama.runtime.GAMA;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;

/**
 * The class Popup.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public class Popup {

	private static final Shell popup = new Shell(SwtGui.getDisplay(), SWT.ON_TOP | SWT.NO_TRIM);
	private static final Listener hide = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			hide();
		}
	};

	static {
		popup.setLayout(new GridLayout(1, true));
	}

	private final MouseTrackListener mtl = new MouseTrackListener() {

		@Override
		public void mouseEnter(final MouseEvent e) {
			GAMA.getGui().asyncRun(new Runnable() {

				@Override
				public void run() {
					// open();
					display();
					isVisible = true;
				}
			});

		}

		@Override
		public void mouseExit(final MouseEvent e) {
			hide();
			isVisible = false;
		}

		@Override
		public void mouseHover(final MouseEvent e) {
			GAMA.getGui().asyncRun(new Runnable() {

				@Override
				public void run() {
					display();
					isVisible = true;
				}
			});

		}

	};

	private final IPopupProvider provider;
	boolean isVisible;

	/*
	 *
	 */
	public Popup(final IPopupProvider provider, final Widget ... controls) {
		this.provider = provider;
		final Shell parent = provider.getControllingShell();
		parent.addListener(SWT.Move, hide);
		parent.addListener(SWT.Resize, hide);
		parent.addListener(SWT.Close, hide);
		parent.addListener(SWT.Deactivate, hide);
		parent.addListener(SWT.Hide, hide);
		for ( final Widget c : controls ) {
			if ( c == null ) {
				continue;
			}
			final TypedListener typedListener = new TypedListener(mtl);
			c.addListener(SWT.MouseEnter, typedListener);
			c.addListener(SWT.MouseExit, typedListener);
			c.addListener(SWT.MouseHover, typedListener);
		}
	}

	// public void open() {
	// // We first verify that the popup is still ok
	// final Shell c = provider.getControllingShell();
	// if ( c == null || c.isDisposed() ) {
	// hide();
	// return;
	// }
	//
	// // We then remove all existing controls if any
	// for ( final Control control : popup.getChildren() ) {
	// control.dispose();
	// }
	//
	// final Map<GamaUIColor, String> s = provider.getPopupText();
	// if ( s == null || s.isEmpty() ) {
	// hide();
	// return;
	// }
	//
	// // We create text controls in accordance to the text
	// for ( final Map.Entry<GamaUIColor, String> entry : s.entrySet() ) {
	// final Label label = new Label(popup, SWT.None);
	// label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	// label.setBackground(entry.getKey().color());
	// label.setForeground(GamaColors.getTextColorForBackground(entry.getKey()).color());
	// label.setText(entry.getValue());
	// }
	// }

	public void display() {
		// We first verify that the popup is still ok
		final Shell c = provider.getControllingShell();
		if ( c == null || c.isDisposed() ) {
			hide();
			return;
		}

		// We then grab the text and hide if it is null or empty
		final Map<GamaUIColor, String> s = provider.getPopupText();
		if ( s == null || s.isEmpty() ) {
			hide();
			return;
		}

		int index = 0;
		// int maxTextWidth = 0;
		Control[] labels = popup.getChildren();
		if ( labels.length != s.size() ) {
			for ( final Control control : labels ) {
				control.dispose();
			}
			labels = new Control[s.size()];
			int i = 0;
			for ( final Map.Entry<GamaUIColor, String> entry : s.entrySet() ) {
				final Label label = new Label(popup, SWT.None);
				label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				label.setBackground(entry.getKey().color());
				label.setForeground(GamaColors.getTextColorForBackground(entry.getKey()).color());
				label.setText(entry.getValue());
				labels[i++] = label;
			}
		} else for ( final Map.Entry<GamaUIColor, String> entry : s.entrySet() ) {
			final Label label = (Label) labels[index++];
			label.setText(entry.getValue());
		}

		// We fix the max. width to 400
		// final int maxPopupWidth = 800;

		// scope.getGui().debug("Popup.display: textWidth = " + textWidth);
		// We grab the location of the popup on the display
		final Point point = provider.getAbsoluteOrigin();
		popup.setLocation(point.x, point.y);

		// We compute the available width on the display given this location (and a border of 5 pixels)
		// final Rectangle screenArea = popup.getDisplay().getClientArea();
		// final int availableWidth = screenArea.x + screenArea.width - point.x - 5;
		// scope.getGui().debug("Popup.display: availableWidth = " + availableWidth);
		// We compute the final width of the popup
		// int popupWidth = CmnFastMath.min(availableWidth, maxPopupWidth);
		// scope.getGui().debug("Popup.display: popupWidth = " + popupWidth);

		// If the width of the text is greater than the computed width, we wrap the text accordingly, otherwise we
		// shrink the popup to the text width
		// if ( maxTextWidth > popupWidth ) {
		// // We grab the longest line
		// final String[] lines = s.split("\\r?\\n");
		// int maxLineChars = 0;
		// for ( final String line : lines ) {
		// final int lineWidth = line.length();
		// maxLineChars = maxLineChars > lineWidth ? maxLineChars : lineWidth;
		// // scope.getGui().debug("Popup.display: maxLineCharts = " + maxLineChars);
		// }
		// final int wrapLimit = (int) (maxLineChars / (double) textWidth * popupWidth);
		// // scope.getGui().debug("Popup.display: wrapLimit = " + wrapLimit);
		//
		// s = WordUtils.wrap(s, wrapLimit);
		// } else {
		// popupWidth = textWidth;
		// }

		// We set the text of the popup
		// popupText.setText(s);

		// We ask the popup to compute its actual size given the width and to display itself
		// final Point newPopupSize = popup.computeSize(popupWidth, SWT.DEFAULT);
		// popup.setSize(newPopupSize);
		popup.layout();
		popup.pack();
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
