/*******************************************************************************************************
 *
 * SwitchButton.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.controls;

import static msi.gama.application.workbench.ThemeHelper.isDark;
import static ummisco.gama.ui.resources.GamaColors.get;
import static ummisco.gama.ui.resources.GamaColors.getTextColorForBackground;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import msi.gama.kernel.experiment.IParameter;
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
	boolean selection;

	/**
	 * Text displayed for the selected value (default = "True")
	 */
	private String trueText;

	/**
	 * Text displayed for the unselected value (default = "False")
	 */
	private String falseText;

	/**
	 * Text corresponding to the button (default is "")
	 */
	private String text;

	/**
	 * Colors when the button is selected
	 */
	private final Color trueBackgroundColor;

	/**
	 * Colors when the button is not selected
	 */
	private final Color falseBackgroundColor;

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
	 */
	public SwitchButton(final Composite parent, final int style, final String trueText, final String falseText,
			final String text) {
		this(parent, style, IGamaColors.OK.color(), IGamaColors.ERROR.color(), new String[] { trueText, falseText });
		this.trueText = trueText;
		this.falseText = falseText;
		this.text = text;
	}

	/**
	 * Instantiates a new switch button.
	 *
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 * @param selectedBackgroundColor
	 *            the selected background color
	 * @param unselectedBackgroundColor
	 *            the unselected background color
	 */
	public SwitchButton(final Composite parent, final int style, final Color selectedBackgroundColor,
			final Color unselectedBackgroundColor, final String[] strings) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		String[] labels = strings == null || strings.length < 2 ? IParameter.SWITCH_STRINGS : strings;
		this.trueText = " " + labels[0] + " ";
		this.falseText = " " + labels[1] + " ";
		this.text = "";
		this.trueBackgroundColor = selectedBackgroundColor;
		this.falseBackgroundColor = unselectedBackgroundColor;

		// setFont(GamaFonts.getSmallFont());
		this.gap = 10;
		this.listOfSelectionListeners = new HashSet<>();
		this.addPaintListener(SwitchButton.this::onPaint);
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
		final var rect = this.getClientArea();
		if (rect.width == 0 || rect.height == 0) return;
		this.gc = event.gc;
		final var buttonSize = this.computeButtonSize();
		this.drawSwitchButton(buttonSize);
		if (text != null && !text.isEmpty()) { this.drawText(buttonSize); }

	}

	@Override
	public int getBackgroundMode() { return SWT.INHERIT_NONE; }

	/**
	 * Draw the switch button
	 *
	 * @param buttonSize
	 *            size of the button
	 */
	private void drawSwitchButton(final Point buttonSize) {
		Color backgroundColor = getParent().getBackground();
		this.gc.setBackground(isDark() ? get(backgroundColor).lighter() : get(backgroundColor).darker());
		this.gc.fillRoundRectangle(2, 2, buttonSize.x - 1, buttonSize.y + 1, 5, 5);
		gc.setBackground(selection ? trueBackgroundColor : falseBackgroundColor);
		gc.setForeground(getTextColorForBackground(gc.getBackground()).color());
		String textToDraw = selection ? trueText : falseText;
		final var textSize = this.gc.textExtent(textToDraw);
		int offsetX = (buttonSize.x / 2 - textSize.x) / 2 + 2;
		int offsetY = (buttonSize.y - textSize.y) / 2 + 2;
		int offsetB = selection ? 2 : buttonSize.x / 2 + 1;
		gc.fillRoundRectangle(offsetB, 2, buttonSize.x / 2 + 1, buttonSize.y + 1, 5, 5);
		gc.drawString(textToDraw, offsetB + offsetX, offsetY);
	}

	/**
	 * @return the button size
	 */
	Point computeButtonSize() {
		// Compute size for the left part
		gc.setFont(getFont());
		final var sizeForLeftPart = this.gc.stringExtent(this.trueText);
		// Compute size for the right part
		final var sizeForRightPart = this.gc.stringExtent(this.falseText);
		// Compute whole size
		final var width = Math.max(sizeForLeftPart.x, sizeForRightPart.x) * 2 + 2 * INSIDE_BUTTON_MARGIN;
		final var height = Math.max(sizeForLeftPart.y, sizeForRightPart.y) + INSIDE_BUTTON_MARGIN;
		return new Point(width, height);
	}

	/**
	 * Draws the text besides the button
	 *
	 * @param buttonSize
	 *            whole size of the button
	 */
	void drawText(final Point buttonSize) {
		this.gc.setForeground(this.selection ? this.trueBackgroundColor : this.falseBackgroundColor);
		this.gc.setBackground(getParent().getBackground());
		final var widgetHeight = buttonSize.y + 6;
		final var textHeight = this.gc.stringExtent(this.text).y;
		final var x = 2 + buttonSize.x + this.gap;
		this.gc.drawText(text, x, (widgetHeight - textHeight) / 2);
	}

	/**
	 * Fire the selection listeners
	 *
	 * @param mouseEvent
	 *            mouse event
	 * @return true if the selection could be changed, false otherwise
	 */
	boolean fireSelectionListeners(final MouseEvent mouseEvent) {
		for (final SelectionListener listener : this.listOfSelectionListeners) {
			final var event = new Event();
			event.button = mouseEvent.button;
			event.display = this.getDisplay();
			event.item = null;
			event.widget = this;
			event.data = null;
			event.time = mouseEvent.time;
			event.x = mouseEvent.x;
			event.y = mouseEvent.y;

			final var selEvent = new SelectionEvent(event);
			listener.widgetSelected(selEvent);
			if (!selEvent.doit) return false;
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
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(final SelectionListener listener) {
		if (listener == null) { SWT.error(SWT.ERROR_NULL_ARGUMENT); }
		this.listOfSelectionListeners.add(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		this.checkWidget();
		var disposeGC = false;
		if (this.gc == null || this.gc.isDisposed()) {
			this.gc = new GC(this);
			disposeGC = true;
		}
		final var buttonSize = this.computeButtonSize();
		var width = buttonSize.x;
		var height = buttonSize.y;
		if (this.text != null && this.text.trim().length() > 0) {
			final var textSize = this.gc.textExtent(this.text);
			width += textSize.x + this.gap + 1;
		}
		width += 6;
		height += 6;
		if (disposeGC) { this.gc.dispose(); }
		return new Point(width, height);
	}

	/**
	 * @return the selection state of the button
	 */
	public boolean getSelection() { return this.selection; }

	/**
	 * @param selection
	 *            the selection state of the button
	 */
	public void setSelection(final boolean selection) {
		this.selection = selection;
		redraw();
	}

	/**
	 * Gets the text corresponding to the button (default is "").
	 *
	 * @return the text corresponding to the button (default is "")
	 */
	public String getText() { return this.text; }

}
