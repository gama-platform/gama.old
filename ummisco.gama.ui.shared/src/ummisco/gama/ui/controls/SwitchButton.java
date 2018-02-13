/*********************************************************************************************
 *
 * 'SwitchButton.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.controls;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.IGamaColors;

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
	 * Text displayed for the selected value (default = "True")
	 */
	private String textForSelect;

	/**
	 * Text displayed for the unselected value (default = "False")
	 */
	private String textForUnselect;

	/**
	 * Text corresponding to the button (default is "")
	 */
	private String text;

	/**
	 * If true, display round rectangles instead of rectangles (default value is true)
	 */
	private boolean round;

	/**
	 * if not null, displays a rectangle (or a round rectangle) around the whole widget. Default value is null.
	 */
	private Color borderColor;

	/**
	 * if not null, displays a glow effect when the mouse is over the widget. Default value is null.
	 */
	private Color focusColor;

	/**
	 * Colors when the button is selected
	 */
	private Color/* selectedForegroundColor, */ selectedBackgroundColor;

	/**
	 * Colors when the button is not selected
	 */
	private Color /* unselectedForegroundColor, */ unselectedBackgroundColor;

	/**
	 * Colors for the button
	 */
	private Color buttonBorderColor;

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
	 * Constructs a new instance of this class given its parent and a style value describing its behavior and
	 * appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to
	 * instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists
	 * the style constants that are applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent
	 *            a composite control which will be the parent of the new instance (cannot be null)
	 * @param style
	 *            the style of control to construct
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *                </ul>
	 *
	 */
	public SwitchButton(final Composite parent, final int style, final String trueText, final String falseText,
			final String text) {
		this(parent, style);
		this.textForSelect = trueText;
		this.textForUnselect = falseText;
		this.text = text;
	}

	public SwitchButton(final Composite parent, final int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		this.textForSelect = " True ";
		this.textForUnselect = " False ";
		this.text = "";
		this.round = true;
		this.borderColor = null;
		this.focusColor = null;
		this.selectedBackgroundColor = IGamaColors.OK.color();
		this.unselectedBackgroundColor = IGamaColors.ERROR.color();
		this.buttonBorderColor = IGamaColors.NEUTRAL.color();
		setFont(GamaFonts.getSmallFont());

		this.gap = 10;

		this.listOfSelectionListeners = new HashSet<SelectionListener>();

		this.addPaintListener(event -> SwitchButton.this.onPaint(event));

		this.addMouseListener(new MouseAdapter() {

			/**
			 * @see org.eclipse.swt.events.MouseAdapter#mouseUp(org.eclipse.swt.events.MouseEvent)
			 */
			@Override
			public void mouseUp(final MouseEvent e) {
				setSelection(!selection);
				if (SwitchButton.this.fireSelectionListeners(e)) {
					// ?
				}
			}

		});

	}

	/**
	 * Paint the widget
	 *
	 * @param event
	 *            paint event
	 */
	private void onPaint(final PaintEvent event) {
		final Rectangle rect = this.getClientArea();
		if (rect.width == 0 || rect.height == 0) { return; }
		this.gc = event.gc;
		final Point buttonSize = this.computeButtonSize();
		this.drawSwitchButton(buttonSize);
		if (text != null && !text.isEmpty())
			this.drawText(buttonSize);

	}

	@Override
	public int getBackgroundMode() {
		return SWT.INHERIT_NONE;
	}

	@Override
	public void setBackground(final Color color) {
		super.setBackground(color);
	}

	/**
	 * Draw the switch button
	 *
	 * @param buttonSize
	 *            size of the button
	 */
	private void drawSwitchButton(final Point buttonSize) {
		// Draw the background of the button
		this.gc.setForeground(this.buttonBorderColor);
		if (this.round) {
			this.gc.drawRoundRectangle(2, 2, buttonSize.x, buttonSize.y, 5, 5);
		} else {
			this.gc.drawRectangle(2, 2, buttonSize.x, buttonSize.y);
		}
		if (selection)
			this.drawRightPart(buttonSize);
		else
			this.drawLeftPart(buttonSize);
		this.drawToggleButton(buttonSize);
	}

	/**
	 * Draw the right part of the button
	 *
	 * @param buttonSize
	 *            size of the button
	 */
	private void drawRightPart(final Point buttonSize) {
		this.gc.setForeground(this.selectedBackgroundColor);
		this.gc.setBackground(this.selectedBackgroundColor);
		if (this.round) {
			this.gc.fillRoundRectangle(2, 2, buttonSize.x, buttonSize.y, 5, 5);
		} else {
			this.gc.fillRectangle(2, 2, buttonSize.x, buttonSize.y);
		}
		this.gc.setForeground(GamaColors.getTextColorForBackground(selectedBackgroundColor).color());
		final Point textSize = this.gc.textExtent(this.textForSelect);
		this.gc.drawString(this.textForSelect, (buttonSize.x / 2 - textSize.x) / 2 + 3,
				(buttonSize.y - textSize.y) / 2 + 3);
	}

	/**
	 * Draw the left part of the button
	 *
	 * @param buttonSize
	 *            size of the button
	 */
	private void drawLeftPart(final Point buttonSize) {
		this.gc.setForeground(this.unselectedBackgroundColor);
		this.gc.setBackground(this.unselectedBackgroundColor);
		if (this.round) {
			this.gc.fillRoundRectangle(2, 2, buttonSize.x, buttonSize.y, 5, 5);
		} else {
			this.gc.fillRectangle(2, 2, buttonSize.x, buttonSize.y);
		}
		this.gc.setForeground(GamaColors.getTextColorForBackground(unselectedBackgroundColor).color());
		final Point textSize = this.gc.textExtent(this.textForUnselect);
		this.gc.drawString(this.textForUnselect, buttonSize.x / 2 + (buttonSize.x / 2 - textSize.x) / 2 + 3,
				(buttonSize.y - textSize.y) / 2 + 3);
	}

	/**
	 * Draw the toggle button
	 *
	 * @param buttonSize
	 *            size of the button
	 */
	private void drawToggleButton(final Point buttonSize) {
		this.gc.setBackground(IGamaColors.WHITE.color());
		if (!this.selection) {
			this.gc.fillRectangle(3, 3, buttonSize.x / 2, buttonSize.y);
		} else {
			this.gc.fillRectangle(buttonSize.x / 2, 3, buttonSize.x / 2 + 2, buttonSize.y - 1);
		}
		this.gc.setForeground(this.buttonBorderColor);
		if (!this.selection) {
			this.gc.drawRoundRectangle(2, 2, buttonSize.x / 2, buttonSize.y, 3, 3);
		} else {
			this.gc.drawRoundRectangle(buttonSize.x / 2, 2, buttonSize.x / 2 + 2, buttonSize.y, 3, 3);
		}
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

	/**
	 * Draws the text besides the button
	 *
	 * @param buttonSize
	 *            whole size of the button
	 */
	private void drawText(final Point buttonSize) {
		this.gc.setForeground(this.selection ? this.selectedBackgroundColor : this.unselectedBackgroundColor);
		this.gc.setBackground(IGamaColors.WHITE.color());

		final int widgetHeight = buttonSize.y + 6;
		final int textHeight = this.gc.stringExtent(this.text).y;
		final int x = 2 + buttonSize.x + this.gap;
		this.gc.drawText(text, x, (widgetHeight - textHeight) / 2);
	}

	/**
	 * Fire the selection listeners
	 *
	 * @param mouseEvent
	 *            mouse event
	 * @return true if the selection could be changed, false otherwise
	 */
	private boolean fireSelectionListeners(final MouseEvent mouseEvent) {
		for (final SelectionListener listener : this.listOfSelectionListeners) {
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
			if (!selEvent.doit) { return false; }
		}
		return true;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when the control is selected by the user,
	 * by sending it one of the messages defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the control is selected by the user.
	 * <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 *
	 * @param listener
	 *            the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(final SelectionListener listener) {
		this.checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.listOfSelectionListeners.add(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified when the control is selected by the
	 * user.
	 *
	 * @param listener
	 *            the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		this.checkWidget();
		if (listener == null) {
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
		if (this.gc == null || this.gc.isDisposed()) {
			this.gc = new GC(this);
			disposeGC = true;
		}
		final Point buttonSize = this.computeButtonSize();
		int width = buttonSize.x;
		int height = buttonSize.y;
		if (this.text != null && this.text.trim().length() > 0) {
			final Point textSize = this.gc.textExtent(this.text);
			width += textSize.x + this.gap + 1;
		}
		width += 6;
		height += 6;
		if (disposeGC) {
			this.gc.dispose();
		}
		return new Point(width, height);
	}

	/**
	 * @return the selection state of the button
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getSelection() {
		return this.selection;
	}

	/**
	 * @param selection
	 *            the selection state of the button
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setSelection(final boolean selection) {
		this.checkWidget();
		this.selection = selection;
		redraw();
	}

	/**
	 * @return the text used to display the selection
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public String getTextForSelect() {
		// this.checkWidget();
		return this.textForSelect;
	}

	/**
	 * @param textForSelect
	 *            the text used to display the selection
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setTextForSelect(final String textForSelect) {
		// this.checkWidget();
		this.textForSelect = textForSelect;
	}

	/**
	 * @return the text used to display the unselected option
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public String getTextForUnselect() {
		// this.checkWidget();
		return this.textForUnselect;
	}

	/**
	 * @param textForUnselect
	 *            the text used to display the unselected option
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setTextForUnselect(final String textForUnselect) {
		// this.checkWidget();
		this.textForUnselect = textForUnselect;
	}

	// /**
	// * @return the text displayed in the widget
	// * @exception SWTException
	// * <ul>
	// * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	// * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	// * </ul>
	// */
	public String getText() {
		// this.checkWidget();
		return this.text;
	}
	//
	// /**
	// * @param the
	// * text displayed in the widget
	// * @exception SWTException
	// * <ul>
	// * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	// * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	// * </ul>
	// */
	// public void setText(final String text) {
	// // this.checkWidget();
	// this.text = text;
	// }

	/**
	 * @return the round flag
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean isRound() {
		return this.round;
	}

	/**
	 * @param round
	 *            the round flag to set. If true, the widget is composed of round rectangle instead of rectangles
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setRound(final boolean round) {
		this.round = round;
	}

	/**
	 * @return the border's color. If null, no border is displayed
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getBorderColor() {
		return this.borderColor;
	}

	/**
	 * @param borderColor
	 *            the border's color. If null, no border is displayed.
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setBorderColor(final Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * @return the focus color. If null, no focus effect is displayed.
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getFocusColor() {
		return this.focusColor;
	}

	/**
	 * @param focusColor
	 *            the focus color to set. If null, no focus effect is displayed.
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setFocusColor(final Color focusColor) {
		this.focusColor = focusColor;
	}

	public Color getSelectedBackgroundColor() {
		return this.selectedBackgroundColor;
	}

	/**
	 * @param the
	 *            background color of the left part of the widget (selection is on)
	 */
	public void setSelectedBackgroundColor(final Color selectedBackgroundColor) {
		this.selectedBackgroundColor = selectedBackgroundColor;
	}

	/**
	 * @return the background color of the left part of the widget (selection is on)
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getUnselectedBackgroundColor() {
		return this.unselectedBackgroundColor;
	}

	/**
	 * @param unselectedBackgroundColor
	 *            the background color of the left part of the widget (selection is on)
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setUnselectedBackgroundColor(final Color unselectedBackgroundColor) {
		this.unselectedBackgroundColor = unselectedBackgroundColor;
	}

	/**
	 * @return the border color of the switch button
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getButtonBorderColor() {
		return this.buttonBorderColor;
	}

	/**
	 * @param buttonBorderColor
	 *            the border color of the switch button
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setButtonBorderColor(final Color buttonBorderColor) {
		this.buttonBorderColor = buttonBorderColor;
	}

}
