/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.controls;

import msi.gama.common.interfaces.ItemList;
import msi.gama.gui.swt.SwtGui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * Instances of this class support the layout of selectable expand bar items.
 * <p>
 * The item children that may be added to instances of this class must be of type
 * <code>ExpandItem</code>.
 * </p>
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>V_SCROLL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Expand, Collapse</dd>
 * </dl>
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see ExpandItem
 * @see ExpandEvent
 * @see ExpandListener
 * @see ExpandAdapter
 * @see <a href="http://www.eclipse.org/swt/snippets/#expandbar">ExpandBar snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example: ControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 * 
 * @since 3.2
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ParameterExpandBar extends Composite {

	ParameterExpandItem[] items;
	private ParameterExpandItem focusItem;
	int spacing, yCurrentScroll, itemCount;
	Font font;
	Color foreground;
	Listener listener;
	boolean inDispose, isClosable, isPausable;
	ItemList underlyingObjects;

	/**
	 * @param underlyingObjects Constructs a new instance of this class given its parent and a style
	 *            value describing its behavior and appearance.
	 *            <p>
	 *            The style value is either one of the style constants defined in class
	 *            <code>SWT</code> which is applicable to instances of this class, or must be built
	 *            by <em>bitwise OR</em>'ing together (that is, using the <code>int</code> "|"
	 *            operator) two or more of those <code>SWT</code> style constants. The class
	 *            description lists the style constants that are applicable to the class. Style bits
	 *            are also inherited from superclasses.
	 *            </p>
	 * 
	 * @param parent a composite control which will be the parent of the new instance (cannot be
	 *            null)
	 * @param style the style of control to construct
	 * 
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 *                </ul>
	 * 
	 * @see SWT#V_SCROLL
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public ParameterExpandBar(final Composite parent, final int style, final boolean isClosable,
		final boolean isPausable, final ItemList underlyingObjects) {
		super(parent, checkStyle(style));
		items = new ParameterExpandItem[4];
		this.isClosable = isClosable;
		this.isPausable = isPausable;
		this.underlyingObjects = underlyingObjects;
		listener = new Listener() {

			@Override
			public void handleEvent(final Event event) {
				switch (event.type) {
					case SWT.Dispose:
						onDispose(event);
						break;
					case SWT.MouseDown:
						onMouseDown(event);
						break;
					case SWT.MouseUp:
						onMouseUp(event);
						break;

					case SWT.Paint:
						onPaint(event);
						break;
					case SWT.Resize:
						onResize();
						break;
					case SWT.KeyDown:
						onKeyDown(event);
						break;
					case SWT.FocusIn:
						onFocus();
						break;
					case SWT.FocusOut:
						onFocus();
						break;
					case SWT.Traverse:
						onTraverse(event);
						break;
				}
			}
		};
		addListener(SWT.Dispose, listener);
		addListener(SWT.MouseDown, listener);
		addListener(SWT.MouseUp, listener);
		addListener(SWT.Paint, listener);
		addListener(SWT.Resize, listener);
		addListener(SWT.KeyDown, listener);
		addListener(SWT.FocusIn, listener);
		addListener(SWT.FocusOut, listener);
		addListener(SWT.Traverse, listener);

		ScrollBar verticalBar = getVerticalBar();
		if ( verticalBar != null ) {
			verticalBar.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(final Event event) {
					onScroll(event);
				}
			});
		}
		// this.setBackground(getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
	}

	static int checkStyle(final int style) {
		return style & ~SWT.H_SCROLL;
	}

	@Override
	protected void checkSubclass() {

	}

	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		checkWidget();
		int height = 0, width = 0;
		if ( wHint == SWT.DEFAULT || hHint == SWT.DEFAULT ) {
			if ( itemCount > 0 ) {
				height += spacing;
				GC gc = new GC(this);
				for ( int i = 0; i < itemCount; i++ ) {
					ParameterExpandItem item = items[i];
					height += item.getHeaderHeight();
					if ( item.expanded ) {
						height += item.height;
					}
					height += spacing;
					width = Math.max(width, item.getPreferredWidth(gc));
				}
				gc.dispose();
			}
		}
		if ( width == 0 ) {
			width = 64;
		}
		if ( height == 0 ) {
			height = 64;
		}
		if ( wHint != SWT.DEFAULT ) {
			width = wHint;
		}
		if ( hHint != SWT.DEFAULT ) {
			height = hHint;
		}
		Rectangle trim = computeTrim(0, 0, width, height);
		return new Point(trim.width, trim.height);
	}

	void createItem(final ParameterExpandItem item, final int style, final int index) {
		if ( !(0 <= index && index <= itemCount) ) {
			SWT.error(SWT.ERROR_INVALID_RANGE);
		}
		if ( itemCount == items.length ) {
			ParameterExpandItem[] newItems = new ParameterExpandItem[itemCount + 4];
			System.arraycopy(items, 0, newItems, 0, items.length);
			items = newItems;
		}
		System.arraycopy(items, index, items, index + 1, itemCount - index);
		items[index] = item;
		itemCount++;
		if ( getFocusItem() == null ) {
			setFocusItem(item);
		}
		item.width = Math.max(0, getClientArea().width - spacing * 2);
		layoutItems(index, true);
	}

	void destroyItem(final ParameterExpandItem item) {

		if ( inDispose ) { return; }
		int index = 0;
		while (index < itemCount) {
			if ( items[index] == item ) {
				break;
			}
			index++;
		}
		if ( index == itemCount ) { return; }
		if ( item == getFocusItem() ) {
			int focusIndex = index > 0 ? index - 1 : 1;
			if ( focusIndex < itemCount ) {
				setFocusItem(items[focusIndex]);
				getFocusItem().redraw();
			} else {
				setFocusItem(null);
			}
		}
		System.arraycopy(items, index + 1, items, index, --itemCount - index);
		items[itemCount] = null;
		// item.redraw();
		underlyingObjects.removeItem(item.getData());
		layoutItems(index, true);
		if ( this.isDisposed() ) { return; }
		this.redraw();
		this.update();
	}

	int getBandHeight() {
		if ( font == null ) { return ParameterExpandItem.CHEVRON_SIZE; }
		GC gc = new GC(this);
		FontMetrics metrics = gc.getFontMetrics();
		gc.dispose();
		return Math.max(ParameterExpandItem.CHEVRON_SIZE, metrics.getHeight());
	}

	@Override
	public Color getForeground() {
		checkWidget();
		if ( foreground == null ) {
			Display display = getDisplay();
			return display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND);
		}
		return foreground;
	}

	/**
	 * Returns the item at the given, zero-relative index in the receiver. Throws an exception if
	 * the index is out of range.
	 * 
	 * @param index the index of the item to return
	 * @return the item at the given index
	 * 
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of
	 *                elements in the list minus 1 (inclusive)</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public ParameterExpandItem getItem(final int index) {
		checkWidget();
		if ( !(0 <= index && index < itemCount) ) {
			SWT.error(SWT.ERROR_INVALID_RANGE);
		}
		return items[index];
	}

	/**
	 * Returns the number of items contained in the receiver.
	 * 
	 * @return the number of items
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public int getItemCount() {
		return itemCount;
	}

	/**
	 * Returns an array of <code>ExpandItem</code>s which are the items in the receiver.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its list of items, so
	 * modifying the array will not affect the receiver.
	 * </p>
	 * 
	 * @return the items in the receiver
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public ParameterExpandItem[] getItems() {
		ParameterExpandItem[] result = new ParameterExpandItem[itemCount];
		System.arraycopy(items, 0, result, 0, itemCount);
		return result;
	}

	/**
	 * Returns the receiver's spacing.
	 * 
	 * @return the spacing
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public int getSpacing() {
		return spacing;
	}

	/**
	 * Searches the receiver's list starting at the first item (index 0) until an item is found that
	 * is equal to the argument, and returns the index of that item. If no item is found, returns
	 * -1.
	 * 
	 * @param item the search item
	 * @return the index of the item
	 * 
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public int indexOf(final ParameterExpandItem item) {
		if ( item == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		for ( int i = 0; i < itemCount; i++ ) {
			if ( items[i] == item ) { return i; }
		}
		return -1;
	}

	void layoutItems(final int index, final boolean setScrollbar) {
		if ( index < itemCount ) {
			int y = spacing - yCurrentScroll;
			for ( int i = 0; i < index; i++ ) {
				ParameterExpandItem item = items[i];
				if ( item.expanded ) {
					y += item.height;
				}
				y += item.getHeaderHeight() + spacing;
			}
			for ( int i = index; i < itemCount; i++ ) {
				ParameterExpandItem item = items[i];
				item.setBounds(spacing, y, 0, 0, true, false);
				if ( item.expanded ) {
					y += item.height;
				}
				y += item.getHeaderHeight() + spacing;
			}
		}
		if ( setScrollbar ) {
			setScrollbar();
		}
	}

	public void updateItemNames() {
		for ( int i = 0; i < itemCount; i++ ) {
			items[i].setText(underlyingObjects.getItemDisplayName(items[i].getData(),
				items[i].getText()));
		}
	}

	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		this.font = font;
		layoutItems(0, true);
	}

	@Override
	public void setForeground(final Color color) {
		super.setForeground(color);
		foreground = color;
	}

	void setScrollbar() {
		if ( itemCount == 0 ) { return; }
		ScrollBar verticalBar = getVerticalBar();
		if ( verticalBar == null ) { return; }
		int height = getClientArea().height;
		ParameterExpandItem item = items[itemCount - 1];
		int maxHeight = item.y + getBandHeight() + spacing;
		if ( item.expanded ) {
			maxHeight += item.height;
		}

		// claim bottom free space
		if ( yCurrentScroll > 0 && height > maxHeight ) {
			yCurrentScroll = Math.max(0, yCurrentScroll + maxHeight - height);
			layoutItems(0, false);
		}
		maxHeight += yCurrentScroll;

		int selection = Math.min(yCurrentScroll, maxHeight);
		int increment = verticalBar.getIncrement();
		int pageIncrement = verticalBar.getPageIncrement();
		verticalBar.setValues(selection, 0, maxHeight, height, increment, pageIncrement);
		verticalBar.setVisible(maxHeight > height);
	}

	/**
	 * Sets the receiver's spacing. Spacing specifies the number of pixels allocated around each
	 * item.
	 * 
	 * @param spacing the spacing around each item
	 * 
	 */
	public void setSpacing(final int spacing) {
		if ( spacing < 0 ) { return; }
		if ( spacing == this.spacing ) { return; }
		this.spacing = spacing;
		int width = Math.max(0, getClientArea().width - spacing * 2);
		for ( int i = 0; i < itemCount; i++ ) {
			ParameterExpandItem item = items[i];
			if ( item.width != width ) {
				item.setBounds(0, 0, width, item.height, false, true);
			}
		}
		layoutItems(0, true);
		redraw();
	}

	void showItem(final ParameterExpandItem item) {
		Control control = item.control;
		if ( control != null && !control.isDisposed() ) {
			item.setImage(item.expanded ? SwtGui.collapse : SwtGui.expand);
			control.setVisible(item.expanded);
		}
		item.redraw();
		int index = indexOf(item);
		layoutItems(index + 1, true);
	}

	void onDispose(final Event event) {
		removeListener(SWT.Dispose, listener);
		notifyListeners(SWT.Dispose, event);
		event.type = SWT.None;
		/*
		 * Usually when an item is disposed, destroyItem will change the size of the items array,
		 * reset the bounds of all the tabs and manage the widget associated with the tab. Since the
		 * whole folder is being disposed, this is not necessary. For speed the inDispose flag is
		 * used to skip over this part of the item dispose.
		 */
		inDispose = true;

		for ( int i = 0; i < itemCount; i++ ) {
			items[i].dispose();
		}
		items = null;
		font = null;
		foreground = null;
		setFocusItem(null);
	}

	void onFocus() {
		if ( getFocusItem() != null ) {
			getFocusItem().redraw();
		}
	}

	void onKeyDown(final Event event) {
		if ( getFocusItem() == null ) { return; }
		switch (event.keyCode) {
			case 13: /* Return */
			case 32: /* Space */
				Event ev = new Event();
				ev.item = getFocusItem();
				// sendEvent(focusItem.expanded ? SWT.Collapse : SWT.Expand, ev);
				getFocusItem().expanded = !getFocusItem().expanded;
				showItem(getFocusItem());
				break;
			case SWT.ARROW_UP: {
				int focusIndex = indexOf(getFocusItem());
				if ( focusIndex > 0 ) {
					getFocusItem().redraw();
					setFocusItem(items[focusIndex - 1]);
					getFocusItem().redraw();
				}
				break;
			}
			case SWT.ARROW_DOWN: {
				int focusIndex = indexOf(getFocusItem());
				if ( focusIndex < itemCount - 1 ) {
					getFocusItem().redraw();
					setFocusItem(items[focusIndex + 1]);
					getFocusItem().redraw();
				}
				break;
			}
		}
	}

	void onMouseDown(final Event event) {
		if ( event.button != 1 ) { return; }
		int x = event.x;
		int y = event.y;
		for ( int i = 0; i < itemCount; i++ ) {
			ParameterExpandItem item = items[i];
			boolean hover =
				item.x <= x && x < item.x + item.width && item.y <= y &&
					y < item.y + getBandHeight();
			if ( !hover ) {
				continue;
			}
			if ( isPausable && item.pauseRequested(x, y) ) {
				if ( item.isPaused ) {
					underlyingObjects.resumeItem(item.getData());
					item.isPaused = false;
				} else {
					underlyingObjects.pauseItem(item.getData());
					item.isPaused = true;
				}
				showItem(item);
				return;
			}
			if ( isClosable && item.closeRequested(x, y) ) {
				item.dispose();
				return;
			}
			if ( item != getFocusItem() ) {
				if ( getFocusItem() != null ) {
					getFocusItem().redraw();
				}
				setFocusItem(item);
				getFocusItem().redraw();
				forceFocus();
				break;
			}
		}
	}

	void onMouseUp(final Event event) {
		if ( event.button != 1 ) { return; }
		if ( getFocusItem() == null ) { return; }
		int x = event.x;
		int y = event.y;
		boolean hover =
			getFocusItem().x <= x && x < getFocusItem().x + getFocusItem().width &&
				getFocusItem().y <= y && y < getFocusItem().y + getBandHeight();
		if ( hover ) {
			if ( isPausable && getFocusItem().pauseRequested(x, y) ) { return; }

			Event ev = new Event();
			ev.item = getFocusItem();
			notifyListeners(getFocusItem().expanded ? SWT.Collapse : SWT.Expand, ev);
			getFocusItem().expanded = !getFocusItem().expanded;
			showItem(getFocusItem());
		}
	}

	void onPaint(final Event event) {
		boolean hasFocus = isFocusControl();
		for ( int i = 0; i < itemCount; i++ ) {
			ParameterExpandItem item = items[i];
			item.drawItem(event.gc, hasFocus && item == getFocusItem());
		}
	}

	void onResize() {
		Rectangle rect = getClientArea();
		int width = Math.max(0, rect.width - spacing * 2);
		for ( int i = 0; i < itemCount; i++ ) {
			ParameterExpandItem item = items[i];
			item.setBounds(0, 0, width, item.height, false, true);
		}
		setScrollbar();
	}

	void onScroll(final Event event) {
		ScrollBar verticalBar = getVerticalBar();
		if ( verticalBar != null ) {
			yCurrentScroll = verticalBar.getSelection();
			layoutItems(0, false);
		}
	}

	void onTraverse(final Event event) {
		switch (event.detail) {
			case SWT.TRAVERSE_TAB_NEXT:
			case SWT.TRAVERSE_TAB_PREVIOUS:
				event.doit = true;
				break;
		}
	}

	void setFocusItem(final ParameterExpandItem focusItem) {
		this.focusItem = focusItem;
		if ( focusItem != null ) {
			underlyingObjects.focusItem(focusItem.getData());
		}
	}

	ParameterExpandItem getFocusItem() {
		return focusItem;
	}

	public void collapseItemWithData(final Object data) {
		if ( data == null ) { return; }
		for ( ParameterExpandItem i : items ) {
			if ( data.equals(i.getData()) ) {
				i.expanded = false;
				showItem(i);
				return;
			}
		}
	}

}
