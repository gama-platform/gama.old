/*********************************************************************************************
 *
 * 'Popup.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.controls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

import msi.gama.util.GAML;
import ummisco.gama.ui.controls.IPopupProvider.PopupText;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The class Popup.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public class Popup {

	private static final Shell popup = new Shell(WorkbenchHelper.getDisplay(), SWT.ON_TOP | SWT.NO_TRIM);
	private static final Listener hide = event -> hide();

	static {
		popup.setLayout(new GridLayout(1, true));
	}

	private final MouseTrackListener mtl = new MouseTrackListener() {

		@Override
		public void mouseEnter(final MouseEvent e) {
			Display.getCurrent().asyncExec(() -> {
				// open();
				display();
				isVisible = true;
			});

		}

		@Override
		public void mouseExit(final MouseEvent e) {
			hide();
			isVisible = false;
		}

		@Override
		public void mouseHover(final MouseEvent e) {
			Display.getCurrent().asyncExec(() -> {
				display();
				isVisible = true;
			});

		}

	};

	private final IPopupProvider provider;
	boolean isVisible;

	/*
	 *
	 */
	public Popup(final IPopupProvider provider, final Widget... controls) {
		this.provider = provider;
		final Shell parent = provider.getControllingShell();
		parent.addListener(SWT.Move, hide);
		parent.addListener(SWT.Resize, hide);
		parent.addListener(SWT.Close, hide);
		parent.addListener(SWT.Deactivate, hide);
		parent.addListener(SWT.Hide, hide);
		for (final Widget c : controls) {
			if (c == null) {
				continue;
			}
			final TypedListener typedListener = new TypedListener(mtl);
			c.addListener(SWT.MouseEnter, typedListener);
			c.addListener(SWT.MouseExit, typedListener);
			c.addListener(SWT.MouseHover, typedListener);
		}
	}

	public void display() {
		// We first verify that the popup is still ok
		final Shell c = provider.getControllingShell();
		if (c == null || c.isDisposed()) {
			hide();
			return;
		}

		// We then grab the text and hide if it is null or empty
		final PopupText s = provider.getPopupText();
		if (s == null || s.isEmpty()) {
			hide();
			return;
		}

		final Control[] array = popup.getChildren();
		final int labelsSize = s.size();
		final List<Control> labels = new ArrayList<Control>(Arrays.asList(array));
		final int controlsSize = array.length;
		if (controlsSize > labelsSize) {
			for (int i = labelsSize; i < controlsSize; i++) {
				labels.get(i).dispose();
			}
		} else if (labelsSize > controlsSize) {
			for (int i = 0; i < labelsSize - controlsSize; i++) {
				final Label label = new Label(popup, SWT.None);
				label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				labels.add(label);
			}
		}

		final Iterator<Control> it = labels.iterator();
		s.forEach((text, color) -> {
			final Label label = (Label) it.next();
			label.setBackground(color.color());
			label.setForeground(GamaColors.getTextColorForBackground(color.color()).color());
			label.setText(GAML.toText(text));
		});

		final Point point = provider.getAbsoluteOrigin();
		popup.setLocation(point.x, point.y);

		popup.layout();
		popup.pack();
		popup.setVisible(true);
	}

	public boolean isVisible() {
		return isVisible;
	}

	public static void hide() {
		if (!popup.isDisposed()) {
			popup.setVisible(false);
		}
	}
}
