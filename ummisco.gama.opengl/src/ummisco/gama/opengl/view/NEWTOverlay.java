/*******************************************************************************************************
 *
 * NEWTOverlay.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;

/**
 * A customizable overlay over a control.
 *
 * @author Loris Securo
 */
public class NEWTOverlay {

	/** The parents. */
	private final List<Composite> parents;

	/** The object to overlay. */
	private final Control objectToOverlay;

	/** The overlay. */
	private final Shell overlay;

	/** The label. */
	private final Label label;

	/** The control listener. */
	private final ControlListener controlListener;

	/** The dispose listener. */
	private final DisposeListener disposeListener;

	/** The paint listener. */
	private final PaintListener paintListener;

	/** The showing. */
	private boolean showing;

	/** The has client area. */
	private boolean hasClientArea;

	/** The scrollable to overlay. */
	private Scrollable scrollableToOverlay;

	/**
	 * Instantiates a new NEWT overlay.
	 *
	 * @param objectToOverlay
	 *            the object to overlay
	 */
	public NEWTOverlay(final Control objectToOverlay) {

		Objects.requireNonNull(objectToOverlay);

		this.objectToOverlay = objectToOverlay;

		// if the object to overlay is an instance of Scrollable (e.g. Shell) then it has
		// the getClientArea method, which is preferable over Control.getSize
		if (objectToOverlay instanceof Scrollable) {
			hasClientArea = true;
			scrollableToOverlay = (Scrollable) objectToOverlay;
		} else {
			hasClientArea = false;
			scrollableToOverlay = null;
		}

		// save the parents of the object, so we can add/remove listeners to them
		parents = new ArrayList<>();
		Composite parent = objectToOverlay.getParent();
		while (parent != null) {
			parents.add(parent);
			parent = parent.getParent();
		}

		// listener to track position and size changes in order to modify the overlay bounds as well
		controlListener = new ControlListener() {
			@Override
			public void controlMoved(final ControlEvent e) {
				reposition();
			}

			@Override
			public void controlResized(final ControlEvent e) {
				reposition();
			}
		};

		// listener to track paint changes, like when the object or its parents become not visible (for example changing
		// tab in a TabFolder)
		paintListener = arg0 -> reposition();

		// listener to remove the overlay if the object to overlay is disposed
		disposeListener = e -> remove();

		// create the overlay shell
		overlay = new Shell(objectToOverlay.getShell(), SWT.NO_TRIM | SWT.ON_TOP);

		// default values of the overlay
		overlay.setBackground(objectToOverlay.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		overlay.setAlpha(200);

		// so the label can inherit the background of the overlay
		overlay.setBackgroundMode(SWT.INHERIT_DEFAULT);

		// label to display a text
		// style WRAP so if it is too long the text get wrapped
		label = new Label(overlay, SWT.WRAP);

		// to center the label
		overlay.setLayout(new GridLayout());
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

		showing = false;
		overlay.open();
		overlay.setVisible(showing);
	}

	/**
	 * Show.
	 */
	public void show() {

		// if it's already visible we just exit
		if (showing) return;

		// set the overlay position over the object
		reposition();

		// show the overlay
		overlay.setVisible(true);

		// add listeners to the object to overlay
		objectToOverlay.addControlListener(controlListener);
		objectToOverlay.addDisposeListener(disposeListener);
		objectToOverlay.addPaintListener(paintListener);

		// add listeners also to the parents because if they change then also the visibility of our object could change
		for (Composite parent : parents) {
			parent.addControlListener(controlListener);
			parent.addPaintListener(paintListener);
		}

		showing = true;
	}

	/**
	 * Removes the.
	 */
	public void remove() {

		// if it's already not visible we just exit
		if (!showing) return;

		// remove the listeners
		if (!objectToOverlay.isDisposed()) {
			objectToOverlay.removeControlListener(controlListener);
			objectToOverlay.removeDisposeListener(disposeListener);
			objectToOverlay.removePaintListener(paintListener);
		}

		// remove the parents listeners
		for (Composite parent : parents) {
			if (!parent.isDisposed()) {
				parent.removeControlListener(controlListener);
				parent.removePaintListener(paintListener);
			}
		}

		// remove the overlay shell
		if (!overlay.isDisposed()) { overlay.setVisible(false); }

		showing = false;
	}

	/**
	 * Sets the background.
	 *
	 * @param background
	 *            the new background
	 */
	public void setBackground(final Color background) {
		overlay.setBackground(background);
	}

	/**
	 * Gets the background.
	 *
	 * @return the background
	 */
	public Color getBackground() { return overlay.getBackground(); }

	/**
	 * Sets the alpha.
	 *
	 * @param alpha
	 *            the new alpha
	 */
	public void setAlpha(final int alpha) {
		overlay.setAlpha(alpha);
	}

	/**
	 * Gets the alpha.
	 *
	 * @return the alpha
	 */
	public int getAlpha() { return overlay.getAlpha(); }

	/**
	 * Checks if is showing.
	 *
	 * @return true, if is showing
	 */
	public boolean isShowing() { return showing; }

	/**
	 * Sets the text.
	 *
	 * @param text
	 *            the new text
	 */
	public void setText(final String text) {
		label.setText(text);

		// to adjust the label size accordingly
		overlay.layout();
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() { return label.getText(); }

	/**
	 * Reposition.
	 */
	private void reposition() {

		if (objectToOverlay == null || objectToOverlay.isDisposed()) {
			remove();
			return;
		}
		// if the object is not visible, we hide the overlay and exit
		if (!objectToOverlay.isVisible()) {
			overlay.setBounds(new Rectangle(0, 0, 0, 0));
			return;
		}

		// if the object is visible we need to find the visible region in order to correctly place the overlay

		// get the display bounds of the object to overlay
		Point objectToOverlayDisplayLocation = objectToOverlay.toDisplay(0, 0);

		Point objectToOverlaySize;

		// if it has a client area, we prefer that instead of the size
		if (hasClientArea) {
			Rectangle clientArea = scrollableToOverlay.getClientArea();
			objectToOverlaySize = new Point(clientArea.width, clientArea.height);
		} else {
			objectToOverlaySize = objectToOverlay.getSize();
		}

		Rectangle objectToOverlayBounds = new Rectangle(objectToOverlayDisplayLocation.x,
				objectToOverlayDisplayLocation.y, objectToOverlaySize.x, objectToOverlaySize.y);

		Rectangle intersection = objectToOverlayBounds;

		// intersect the bounds of the object with its parents bounds so we get only the visible bounds
		for (Composite parent : parents) {

			Rectangle parentClientArea = parent.getClientArea();
			Point parentLocation = parent.toDisplay(parentClientArea.x, parentClientArea.y);
			Rectangle parentBounds =
					new Rectangle(parentLocation.x, parentLocation.y, parentClientArea.width, parentClientArea.height);

			intersection = intersection.intersection(parentBounds);

			// if intersection has no size then it would be a waste of time to continue
			if (intersection.width == 0 || intersection.height == 0) { break; }
		}

		overlay.setBounds(intersection);
	}

	/**
	 * Gets the shell.
	 *
	 * @return the shell
	 */
	public Control getShell() { return overlay; }

}