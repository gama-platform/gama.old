/*******************************************************************************************************
 *
 * StolenColorEditor.java, in ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.styling;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * A "button" of a certain color determined by the color picker.
 *
 *
 *
 * @source $URL$
 */
public class StolenColorEditor {

	/** The extent. */
	final Point fExtent;
	
	/** The image. */
	Image fImage;
	
	/** The color value. */
	RGB fColorValue;
	
	/** The color. */
	Color fColor;
	
	/** The button. */
	final Button fButton;
	
	/** The listener. */
	SelectionListener listener;

	/**
	 * Instantiates a new stolen color editor.
	 *
	 * @param parent the parent
	 * @param parentListener the parent listener
	 */
	public StolenColorEditor(final Composite parent, final SelectionListener parentListener) {
		this.listener = parentListener;
		fButton = new Button(parent, SWT.PUSH);
		fExtent = computeImageSize(parent);
		fImage = new Image(parent.getDisplay(), fExtent.x, fExtent.y);

		final GC gc = new GC(fImage);
		gc.setBackground(fButton.getBackground());
		gc.fillRectangle(0, 0, fExtent.x, fExtent.y);
		gc.dispose();

		fButton.setImage(fImage);
		fButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final ColorDialog colorDialog = new ColorDialog(fButton.getShell());
				colorDialog.setRGB(fColorValue);
				final RGB newColor = colorDialog.open();
				if (newColor != null) {
					fColorValue = newColor;
					updateColorImage();
				}
				notifyParent(event);
			}
		});

		fButton.addDisposeListener(event -> {
			if (fImage != null) {
				fImage.dispose();
				fImage = null;
			}
			if (fColor != null) {
				fColor.dispose();
				fColor = null;
			}
		});
	}

	/**
	 * Sets the listener.
	 *
	 * @param newListener the new listener
	 */
	public void setListener(final SelectionListener newListener) {
		listener = newListener;
	}

	/**
	 * Notify parent.
	 *
	 * @param event the event
	 */
	void notifyParent(final SelectionEvent event) {
		if (listener != null) {
			listener.widgetSelected(event);
		}
	}

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public java.awt.Color getColor() {
		final RGB rgb = getColorValue();
		return new java.awt.Color(rgb.red, rgb.green, rgb.blue);
	}

	/**
	 * Sets the color.
	 *
	 * @param color the new color
	 */
	public void setColor(final java.awt.Color color) {
		if (color == null) {
			setColorValue(null);
		} else {
			final RGB rgb = new RGB(color.getRed(), color.getGreen(), color.getBlue());
			setColorValue(rgb);
		}
	}

	/**
	 * Sets the enabled.
	 *
	 * @param isEnabled the new enabled
	 */
	public void setEnabled(final boolean isEnabled) {
		getButton().setEnabled(isEnabled);
	}

	/**
	 * Gets the color value.
	 *
	 * @return the color value
	 */
	public RGB getColorValue() {
		return fColorValue;
	}

	/**
	 * Sets the color value.
	 *
	 * @param color the new color value
	 */
	public void setColorValue(final RGB color) {
		fColorValue = color == null ? new RGB(0, 0, 0) : color;
		updateColorImage();
	}

	/**
	 * Gets the button.
	 *
	 * @return the button
	 */
	public Button getButton() {
		return fButton;
	}

	/**
	 * Update color image.
	 */
	protected void updateColorImage() {

		final Display display = fButton.getDisplay();

		final GC gc = new GC(fImage);
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(0, 2, fExtent.x - 1, fExtent.y - 4);

		if (fColor != null) {
			fColor.dispose();
		}

		fColor = new Color(display, fColorValue);
		gc.setBackground(fColor);
		gc.fillRectangle(1, 3, fExtent.x - 2, fExtent.y - 5);
		gc.dispose();

		fButton.setImage(fImage);
	}

	/**
	 * Compute image size.
	 *
	 * @param window the window
	 * @return the point
	 */
	protected Point computeImageSize(final Control window) {
		final GC gc = new GC(window);
		final Font f = JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT);
		gc.setFont(f);
		final int height = gc.getFontMetrics().getHeight();
		gc.dispose();
		final Point p = new Point(height * 3 - 6, height);
		return p;
	}
}
