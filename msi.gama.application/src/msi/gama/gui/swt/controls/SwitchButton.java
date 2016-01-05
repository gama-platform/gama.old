/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package msi.gama.gui.swt.controls;

import java.util.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import msi.gama.gui.swt.*;

/**
 * Instances of this class are simple switch button.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 */
public class SwitchButton extends Canvas {

	/**
	 * Selection
	 */
	private boolean selection;

	/**
	 * Text displayed for the selected value (default = "On")
	 */
	private String textForSelect;

	/**
	 * Text displayed for the unselected value (default = "Off")
	 */
	private String textForUnselect;

	/**
	 * Text corresponding to the button (default is "")
	 */
	private String text;

	/**
	 * If true, display round rectangles instead of rectangles (default value is
	 * true)
	 */
	private boolean round;

	/**
	 * if not null, displays a rectangle (or a round rectangle) around the whole
	 * widget. Default value is null.
	 */
	private Color borderColor;

	/**
	 * if not null, displays a glow effect when the mouse is over the widget.
	 * Default value is null.
	 */
	private Color focusColor;

	/**
	 * Colors when the button is selected
	 */
	private Color selectedForegroundColor, selectedBackgroundColor;

	/**
	 * Colors when the button is not selected
	 */
	private Color unselectedForegroundColor, unselectedBackgroundColor;

	/**
	 * Colors for the button
	 */
	private Color buttonBorderColor;
	// private Color buttonBackgroundColor1, buttonBackgroundColor2;

	/**
	 * Gap between the button and the text (default value is 5)
	 */
	private final int gap;

	/**
	 * Margin inside the button
	 */
	private static final int INSIDE_BUTTON_MARGIN = 3;

	/**
	 * Graphical context for this button
	 */
	private GC gc;

	/**
	 * List of selection listeners
	 */
	private final Set<SelectionListener> listOfSelectionListeners;

	/**
	 * True when the mouse entered the widget
	 */
	// private boolean mouseInside;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to instances of this class, or must be built by <em>bitwise OR</em>'ing together (that
	 * is, using the <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists the style constants that are applicable to the class. Style bits
	 * are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new
	 * instance (cannot be null)
	 * @param style the style of control to construct
	 *
	 * @exception IllegalArgumentException <ul>
	 * <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 * </ul>
	 *
	 */
	public SwitchButton(final Composite parent, final int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);

		// this.selection = false;
		this.textForSelect = " True ";
		this.textForUnselect = " False ";
		this.text = "";
		this.round = true;
		this.borderColor = null;
		this.focusColor = null;
		this.selectedForegroundColor = this.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.selectedBackgroundColor = IGamaColors.OK.color();
		this.unselectedForegroundColor = IGamaColors.WHITE.color();
		this.unselectedBackgroundColor = IGamaColors.ERROR.color();
		this.buttonBorderColor = IGamaColors.NEUTRAL.color();
		// this.buttonBackgroundColor1 = IGamaColors.WHITE.color();
		// this.buttonBackgroundColor2 = IGamaColors.WHITE.color();
		setFont(SwtGui.getSmallFont());

		this.gap = 5;

		this.listOfSelectionListeners = new HashSet<SelectionListener>();

		this.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent event) {
				SwitchButton.this.onPaint(event);
			}
		});

		this.addMouseListener(new MouseAdapter() {

			/**
			 * @see org.eclipse.swt.events.MouseAdapter#mouseUp(org.eclipse.swt.events.MouseEvent)
			 */
			@Override
			public void mouseUp(final MouseEvent e) {
				setSelection(!selection);
				if ( SwitchButton.this.fireSelectionListeners(e) ) {
					// ?
				}
			}

		});

		// this.mouseInside = false;
		// this.addMouseTrackListener(new MouseTrackListener() {
		//
		// @Override
		// public void mouseHover(final MouseEvent e) {
		// SwitchButton.this.mouseInside = true;
		// SwitchButton.this.redraw();
		// }
		//
		// @Override
		// public void mouseExit(final MouseEvent e) {
		// SwitchButton.this.mouseInside = false;
		// SwitchButton.this.redraw();
		// }
		//
		// @Override
		// public void mouseEnter(final MouseEvent e) {
		// SwitchButton.this.mouseInside = true;
		// SwitchButton.this.redraw();
		// }
		// });

	}

	/**
	 * Paint the widget
	 *
	 * @param event paint event
	 */
	private void onPaint(final PaintEvent event) {
		final Rectangle rect = this.getClientArea();
		if ( rect.width == 0 || rect.height == 0 ) { return; }
		this.gc = event.gc;
		this.gc.setAntialias(SWT.ON);

		final Point buttonSize = this.computeButtonSize();
		this.drawSwitchButton(buttonSize);
		// this.drawText(buttonSize);

		// if ( this.borderColor != null ) {
		// this.drawBorder();
		// }

	}

	/**
	 * Draw the switch button
	 *
	 * @param buttonSize size of the button
	 */
	private void drawSwitchButton(final Point buttonSize) {
		// Draw the background of the button
		this.gc.setForeground(this.buttonBorderColor);
		if ( this.round ) {
			this.gc.drawRoundRectangle(2, 2, buttonSize.x, buttonSize.y, 5, 5);
		} else {
			this.gc.drawRectangle(2, 2, buttonSize.x, buttonSize.y);
		}

		this.drawRightPart(buttonSize);
		this.drawLeftPart(buttonSize);
		this.gc.setClipping(this.getClientArea());
		this.drawToggleButton(buttonSize);
	}

	/**
	 * Draw the right part of the button
	 *
	 * @param buttonSize size of the button
	 */
	private void drawRightPart(final Point buttonSize) {
		this.gc.setForeground(this.selectedBackgroundColor);
		this.gc.setBackground(this.selectedBackgroundColor);
		this.gc.setClipping(3, 3, buttonSize.x / 2, buttonSize.y - 1);
		if ( this.round ) {
			this.gc.fillRoundRectangle(2, 2, buttonSize.x, buttonSize.y, 5, 5);
		} else {
			this.gc.fillRectangle(2, 2, buttonSize.x, buttonSize.y);
		}
		this.gc.setForeground(this.selectedForegroundColor);
		final Point textSize = this.gc.textExtent(this.textForSelect);
		this.gc.drawString(this.textForSelect, (buttonSize.x / 2 - textSize.x) / 2 + 3,
			(buttonSize.y - textSize.y) / 2 + 3);
	}

	/**
	 * Draw the left part of the button
	 *
	 * @param buttonSize size of the button
	 */
	private void drawLeftPart(final Point buttonSize) {
		this.gc.setForeground(this.unselectedBackgroundColor);
		this.gc.setBackground(this.unselectedBackgroundColor);
		this.gc.setClipping(buttonSize.x / 2 + 3, 3, buttonSize.x / 2, buttonSize.y - 1);
		if ( this.round ) {
			this.gc.fillRoundRectangle(2, 2, buttonSize.x, buttonSize.y, 5, 5);
		} else {
			this.gc.fillRectangle(2, 2, buttonSize.x, buttonSize.y);
		}
		this.gc.setForeground(this.unselectedForegroundColor);
		final Point textSize = this.gc.textExtent(this.textForUnselect);

		this.gc.drawString(this.textForUnselect, buttonSize.x / 2 + (buttonSize.x / 2 - textSize.x) / 2 + 3,
			(buttonSize.y - textSize.y) / 2 + 3);
	}

	/**
	 * Draw the toggle button
	 *
	 * @param buttonSize size of the button
	 */
	private void drawToggleButton(final Point buttonSize) {
		// this.gc.setForeground(this.buttonBackgroundColor1);
		this.gc.setBackground(IGamaColors.WHITE.color());
		if ( !this.selection ) {
			this.gc.fillRectangle(3, 3, buttonSize.x / 2, buttonSize.y);
		} else {
			this.gc.fillRectangle(buttonSize.x / 2, 3, buttonSize.x / 2 + 2, buttonSize.y - 1);
		}

		this.gc.setForeground(this.buttonBorderColor);
		if ( !this.selection ) {
			this.gc.drawRoundRectangle(2, 2, buttonSize.x / 2, buttonSize.y, 3, 3);
		} else {
			this.gc.drawRoundRectangle(buttonSize.x / 2, 2, buttonSize.x / 2 + 2, buttonSize.y, 3, 3);
		}

		// if ( this.focusColor != null && this.mouseInside ) {
		// this.gc.setForeground(this.focusColor);
		// this.gc.setLineWidth(2);
		// if ( !this.selection ) {
		// this.gc.drawRoundRectangle(3, 3, buttonSize.x / 2, buttonSize.y - 1, 3, 3);
		// } else {
		// this.gc.drawRoundRectangle(buttonSize.x / 2 + 1, 3, buttonSize.x / 2, buttonSize.y - 2, 3, 3);
		// }
		// this.gc.setLineWidth(1);
		// }

	}

	/**
	 * @return the button size
	 */
	private Point computeButtonSize() {
		// Compute size for the left part
		gc.setFont(getFont());
		final Point sizeForLeftPart = this.gc.stringExtent(this.textForSelect);
		// Compute size for the right part
		final Point sizeForRightPart = this.gc.stringExtent(this.textForUnselect);

		// Compute whole size
		final int width = Math.max(sizeForLeftPart.x, sizeForRightPart.x) * 2 + 2 * INSIDE_BUTTON_MARGIN;
		final int height = Math.max(sizeForLeftPart.y, sizeForRightPart.y) + INSIDE_BUTTON_MARGIN;

		return new Point(width, height);
	}

	// /**
	// * Draws the text besides the button
	// *
	// * @param buttonSize whole size of the button
	// */
	// private void drawText(final Point buttonSize) {
	// this.gc.setForeground(this.getForeground());
	// this.gc.setBackground(this.getBackground());
	//
	// final int widgetHeight = this.computeSize(0, 0, true).y;
	// final int textHeight = this.gc.stringExtent(this.text).y;
	// final int x = 2 + buttonSize.x + this.gap;
	//
	// this.gc.drawString(this.text, x, (widgetHeight - textHeight) / 2);
	// }
	//
	// /**
	// * Draw (eventually) the border around the button
	// */
	// private void drawBorder() {
	// if ( this.borderColor == null ) { return; }
	//
	// this.gc.setForeground(this.borderColor);
	// final Point temp = this.computeSize(0, 0, false);
	// if ( this.round ) {
	// this.gc.drawRoundRectangle(0, 0, temp.x - 2, temp.y - 2, 3, 3);
	// } else {
	// this.gc.drawRectangle(0, 0, temp.x - 2, temp.y - 2);
	// }
	//
	// }

	/**
	 * Fire the selection listeners
	 *
	 * @param mouseEvent mouse event
	 * @return true if the selection could be changed, false otherwise
	 */
	private boolean fireSelectionListeners(final MouseEvent mouseEvent) {
		for ( final SelectionListener listener : this.listOfSelectionListeners ) {
			final Event event = new Event();

			event.button = mouseEvent.button;
			event.display = this.getDisplay();
			event.item = null;
			event.widget = this;
			event.data = null;
			event.time = mouseEvent.time;
			event.x = mouseEvent.x;
			event.y = mouseEvent.y;

			final SelectionEvent selEvent = new SelectionEvent(event);
			listener.widgetSelected(selEvent);
			if ( !selEvent.doit ) { return false; }
		}
		return true;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the control is selected by the user, by sending it one of the
	 * messages defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the control is selected by the user. <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 * <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(final SelectionListener listener) {
		this.checkWidget();
		if ( listener == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.listOfSelectionListeners.add(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the control is selected by the user.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 * <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		this.checkWidget();
		if ( listener == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.listOfSelectionListeners.remove(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		this.checkWidget();
		boolean disposeGC = false;
		if ( this.gc == null || this.gc.isDisposed() ) {
			this.gc = new GC(this);
			disposeGC = true;
		}

		final Point buttonSize = this.computeButtonSize();
		int width = buttonSize.x;
		int height = buttonSize.y;

		if ( this.text != null && this.text.trim().length() > 0 ) {
			final Point textSize = this.gc.textExtent(this.text);
			width += textSize.x + this.gap + 1;
		}

		width += 4;
		height += 6;

		if ( disposeGC ) {
			this.gc.dispose();
		}

		return new Point(width, height);
	}

	/**
	 * @return the selection state of the button
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public boolean getSelection() {
		// this.checkWidget();
		return this.selection;
	}

	/**
	 * @param selection the selection state of the button
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setSelection(final boolean selection) {
		this.checkWidget();
		this.selection = selection;
		redraw();
	}

	/**
	 * @return the text used to display the selection
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public String getTextForSelect() {
		// this.checkWidget();
		return this.textForSelect;
	}

	/**
	 * @param textForSelect the text used to display the selection
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setTextForSelect(final String textForSelect) {
		// this.checkWidget();
		this.textForSelect = textForSelect;
	}

	/**
	 * @return the text used to display the unselected option
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public String getTextForUnselect() {
		// this.checkWidget();
		return this.textForUnselect;
	}

	/**
	 * @param textForUnselect the text used to display the unselected option
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setTextForUnselect(final String textForUnselect) {
		// this.checkWidget();
		this.textForUnselect = textForUnselect;
	}

	/**
	 * @return the text displayed in the widget
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public String getText() {
		// this.checkWidget();
		return this.text;
	}

	/**
	 * @param the text displayed in the widget
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setText(final String text) {
		// this.checkWidget();
		this.text = text;
	}

	/**
	 * @return the round flag
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public boolean isRound() {
		// this.checkWidget();
		return this.round;
	}

	/**
	 * @param round the round flag to set. If true, the widget is composed of
	 * round rectangle instead of rectangles
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setRound(final boolean round) {
		// this.checkWidget();
		this.round = round;
	}

	/**
	 * @return the border's color. If null, no border is displayed
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public Color getBorderColor() {
		// this.checkWidget();
		return this.borderColor;
	}

	/**
	 * @param borderColor the border's color. If null, no border is displayed.
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setBorderColor(final Color borderColor) {
		// this.checkWidget();
		this.borderColor = borderColor;
	}

	/**
	 * @return the focus color. If null, no focus effect is displayed.
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public Color getFocusColor() {
		// this.checkWidget();
		return this.focusColor;
	}

	/**
	 * @param focusColor the focus color to set. If null, no focus effect is
	 * displayed.
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setFocusColor(final Color focusColor) {
		// this.checkWidget();
		this.focusColor = focusColor;
	}

	/**
	 * @return the foreground color of the left part of the widget (selection is
	 * on)
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public Color getSelectedForegroundColor() {
		// this.checkWidget();
		return this.selectedForegroundColor;
	}

	/**
	 * @param the foreground color of the left part of the widget (selection is
	 * on)
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setSelectedForegroundColor(final Color selectedForegroundColor) {
		// this.checkWidget();
		this.selectedForegroundColor = selectedForegroundColor;
	}

	/**
	 * @return the background color of the left part of the widget (selection is
	 * on)
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public Color getSelectedBackgroundColor() {
		// this.checkWidget();
		return this.selectedBackgroundColor;
	}

	/**
	 * @param the background color of the left part of the widget (selection is
	 * on)
	 */
	public void setSelectedBackgroundColor(final Color selectedBackgroundColor) {
		// this.checkWidget();
		this.selectedBackgroundColor = selectedBackgroundColor;
	}

	/**
	 * @return the foreground color of the left part of the widget (selection is
	 * on)
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public Color getUnselectedForegroundColor() {
		// this.checkWidget();
		return this.unselectedForegroundColor;
	}

	/**
	 * @param unselectedForegroundColor the foreground color of the left part of
	 * the widget (selection is on)
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setUnselectedForegroundColor(final Color unselectedForegroundColor) {
		// this.checkWidget();
		this.unselectedForegroundColor = unselectedForegroundColor;
	}

	/**
	 * @return the background color of the left part of the widget (selection is
	 * on)
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public Color getUnselectedBackgroundColor() {
		// this.checkWidget();
		return this.unselectedBackgroundColor;
	}

	/**
	 * @param unselectedBackgroundColor the background color of the left part of
	 * the widget (selection is on)
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setUnselectedBackgroundColor(final Color unselectedBackgroundColor) {
		// this.checkWidget();
		this.unselectedBackgroundColor = unselectedBackgroundColor;
	}

	/**
	 * @return the border color of the switch button
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public Color getButtonBorderColor() {
		// this.checkWidget();
		return this.buttonBorderColor;
	}

	/**
	 * @param buttonBorderColor the border color of the switch button
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setButtonBorderColor(final Color buttonBorderColor) {
		// this.checkWidget();
		this.buttonBorderColor = buttonBorderColor;
	}

}
