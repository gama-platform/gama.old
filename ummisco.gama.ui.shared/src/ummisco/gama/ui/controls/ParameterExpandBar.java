/*********************************************************************************************
 *
 * 'ParameterExpandBar.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.controls;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import com.google.common.base.Objects;

import msi.gama.common.interfaces.ItemList;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Instances of this class support the layout of selectable expand bar items.
 * <p>
 * The item children that may be added to instances of this class must be of type <code>ExpandItem</code>.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class ParameterExpandBar extends Composite {

	static {
		DEBUG.OFF();
	}

	/**
	 * Method setFocus()
	 *
	 * @see org.eclipse.swt.widgets.Composite#setFocus()
	 */
	@Override
	public boolean setFocus() {
		// AD 27/12/2015 Added to fix issue #1248
		return false;
		// return super.setFocus();
	}

	private ParameterExpandItem[] items;
	private ParameterExpandItem focusItem, hoverItem;
	private int spacing, yCurrentScroll, itemCount;
	private final Listener listener;
	private boolean inDispose;
	final boolean hasClosableToggle;
	final boolean hasPausableToggle;
	final boolean hasSelectableToggle;
	final boolean hasVisibleToggle;
	private final ItemList underlyingObjects;
	int bandHeight = ParameterExpandItem.CHEVRON_SIZE;
	private boolean ignoreMouseUp;

	/**
	 * @param underlyingObjects
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
	 * @see SWT#V_SCROLL
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */

	public ParameterExpandBar(final Composite parent, final int style) {
		this(parent, style, false, false, false, false, null);
	}

	public ParameterExpandBar(final Composite parent, final int style, final boolean isClosable,
			final boolean isPausable, final boolean isSelectable, final boolean isVisible,
			final ItemList underlyingObjects) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		items = new ParameterExpandItem[4];
		this.hasClosableToggle = isClosable;
		this.hasPausableToggle = isPausable;
		this.hasSelectableToggle = isSelectable;
		this.hasVisibleToggle = isVisible;
		this.underlyingObjects = underlyingObjects;
		listener = event -> {
			switch (event.type) {
				case SWT.Dispose:
					onDispose(event);
					break;
				case SWT.MenuDetect:
					onContextualMenu(event);
					break;
				case SWT.MouseDown:
					if ((event.stateMask & SWT.CTRL) != 0 || event.button == 3) {
						onContextualMenu(event);
					} else {
						onMouseDown(event);
					}
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
				case SWT.FocusIn:
				case SWT.FocusOut:
					onFocus();
					break;
			}
		};
		addListener(SWT.Dispose, listener);
		addListener(SWT.MouseDown, listener);
		addListener(SWT.MouseUp, listener);
		addListener(SWT.Paint, listener);
		addListener(SWT.Resize, listener);
		addListener(SWT.MenuDetect, listener);
		addListener(SWT.FocusIn, listener);
		addListener(SWT.FocusOut, listener);
		addMouseTrackListener(new MouseTrackAdapter() {

			@Override
			public void mouseExit(final MouseEvent e) {
				changeHoverTo(null);
			}
		});
		addMouseMoveListener(this::onHover);

		final var verticalBar = getVerticalBar();
		if (verticalBar != null) { verticalBar.addListener(SWT.Selection, this::onScroll); }
	}

	@Override
	protected void checkSubclass() {}

	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		checkWidget();
		// Necessary to force SWT to "skin" the widget and determine the color of the viewer
		super.computeSize(wHint, hHint, changed);
		int height = 0, width = 0;
		if ((wHint == SWT.DEFAULT || hHint == SWT.DEFAULT) && itemCount > 0) {
			height += spacing;
			final var gc = new GC(this);
			for (var i = 0; i < itemCount; i++) {
				final var item = items[i];
				height += item.getHeaderHeight();
				if (item.expanded) { height += item.height + 2; }
				height += spacing;
				width = Math.max(width, item.getPreferredWidth(gc));
			}
			gc.dispose();
			height += ParameterExpandItem.BORDER;
		}
		if (width == 0) { width = 64; }
		if (height == 0) { height = 64; }
		if (wHint != SWT.DEFAULT) { width = wHint; }
		if (hHint != SWT.DEFAULT) { height = hHint; }
		final var trim = computeTrim(0, 0, width, height);
		return new Point(trim.width, trim.height);
	}

	void createItem(final ParameterExpandItem item, final int style, final int index) {
		if (0 > index || index > itemCount) { SWT.error(SWT.ERROR_INVALID_RANGE); }
		if (itemCount == items.length) {
			final var newItems = new ParameterExpandItem[itemCount + 4];
			System.arraycopy(items, 0, newItems, 0, items.length);
			items = newItems;
		}
		System.arraycopy(items, index, items, index + 1, itemCount - index);
		items[index] = item;
		itemCount++;
		if (getFocusItem() == null) { setFocusItem(item); }
		item.width = Math.max(0, getClientArea().width - spacing * 2);
		layoutItems(index, true);
	}

	public void destroyItem(final ParameterExpandItem item) {

		if (inDispose) return;
		var index = 0;
		while (index < itemCount) {
			if (items[index] == item) { break; }
			index++;
		}
		if (index == itemCount) return;
		if (item == getFocusItem()) {
			final var focusIndex = index > 0 ? index - 1 : 1;
			if (focusIndex < itemCount) {
				setFocusItem(items[focusIndex]);
				getFocusItem().redraw();
			} else {
				setFocusItem(null);
			}
		}
		System.arraycopy(items, index + 1, items, index, --itemCount - index);
		items[itemCount] = null;
		// item.redraw();
		if (underlyingObjects != null) { underlyingObjects.removeItem(item.getData()); }
		layoutItems(index, true);
		if (this.isDisposed()) return;
		this.redraw();
		this.update();
	}

	void computeBandHeight() {
		if (getFont() == null) return;
		final var gc = new GC(this);
		final var metrics = gc.getFontMetrics();
		gc.dispose();
		bandHeight = Math.max(ParameterExpandItem.CHEVRON_SIZE, metrics.getHeight());
	}

	public ParameterExpandItem getItem(final Object data) {
		for (final ParameterExpandItem item : items) {
			if (item != null && Objects.equal(item.getData(), data)) return item;
		}
		return null;
	}

	/**
	 * Returns the number of items contained in the receiver.
	 *
	 * @return the number of items
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getItemCount() {
		return itemCount;
	}

	/**
	 * Returns an array of <code>ExpandItem</code>s which are the items in the receiver.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its list of items, so modifying the array
	 * will not affect the receiver.
	 * </p>
	 *
	 * @return the items in the receiver
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public ParameterExpandItem[] getItems() {
		final var result = new ParameterExpandItem[itemCount];
		System.arraycopy(items, 0, result, 0, itemCount);
		return result;
	}

	/**
	 * Searches the receiver's list starting at the first item (index 0) until an item is found that is equal to the
	 * argument, and returns the index of that item. If no item is found, returns -1.
	 *
	 * @param item
	 *            the search item
	 * @return the index of the item
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int indexOf(final ParameterExpandItem item) {
		if (item == null) { SWT.error(SWT.ERROR_NULL_ARGUMENT); }
		for (var i = 0; i < itemCount; i++) {
			if (items[i] == item) return i;
		}
		return -1;
	}

	void layoutItems(final int index, final boolean setScrollbar) {
		if (index < itemCount) {
			var y = spacing - yCurrentScroll;
			for (var i = 0; i < index; i++) {
				final var item = items[i];
				if (item.expanded) { y += item.height + 2; }
				y += item.getHeaderHeight() + spacing;
			}
			for (var i = index; i < itemCount; i++) {
				final var item = items[i];
				item.setBounds(spacing, y, 0, 0, true, false);
				if (item.expanded) { y += item.height + 2; }
				y += item.getHeaderHeight() + spacing;
			}
		}
		if (setScrollbar) { setScrollbar(); }
	}

	public void updateItemNames() {
		if (underlyingObjects == null) return;
		for (var i = 0; i < itemCount; i++) {
			items[i].setText(underlyingObjects.getItemDisplayName(items[i].getData(), items[i].getText()));
		}
	}

	public void updateItemColors() {
		if (underlyingObjects == null) return;
		for (var i = 0; i < itemCount; i++) {
			items[i].setColor(underlyingObjects.getItemDisplayColor(items[i].getData()));
		}

	}

	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		computeBandHeight();
		layoutItems(0, true);
	}

	void setScrollbar() {
		if (itemCount == 0) return;
		final var verticalBar = getVerticalBar();
		if (verticalBar == null) return;
		final var height = getClientArea().height;
		final var item = items[itemCount - 1];
		var maxHeight = item.y + bandHeight + spacing;
		if (item.expanded) { maxHeight += item.height; }

		// claim bottom free space
		if (yCurrentScroll > 0 && height > maxHeight) {
			yCurrentScroll = Math.max(0, yCurrentScroll + maxHeight - height);
			layoutItems(0, false);
		}
		maxHeight += yCurrentScroll;

		final var selection = Math.min(yCurrentScroll, maxHeight);
		final var increment = verticalBar.getIncrement();
		final var pageIncrement = verticalBar.getPageIncrement();
		verticalBar.setValues(selection, 0, maxHeight, height, increment, pageIncrement);
		verticalBar.setVisible(maxHeight > height);
	}

	/**
	 * Sets the receiver's spacing. Spacing specifies the number of pixels allocated around each item.
	 *
	 * @param spacing
	 *            the spacing around each item
	 *
	 */
	public void setSpacing(final int spacing) {
		if (spacing < 0 || spacing == this.spacing) return;
		this.spacing = spacing;
		final var width = Math.max(0, getClientArea().width - spacing * 2);
		for (var i = 0; i < itemCount; i++) {
			final var item = items[i];
			if (item.width != width) { item.setBounds(0, 0, width, item.height, false, true); }
		}
		layoutItems(0, true);
		redraw();
	}

	void showItem(final ParameterExpandItem item) {
		final var control = item.control;
		if (control != null && !control.isDisposed()) {
			item.setImage(item.expanded ? GamaIcons.create(IGamaIcons.SMALL_COLLAPSE).image()
					: GamaIcons.create(IGamaIcons.SMALL_EXPAND).image());
			control.setVisible(item.expanded);
		}
		item.redraw();
		final var index = indexOf(item);
		layoutItems(index + 1, true);
		final var ev = new Event();
		ev.item = this;
		notifyListeners(SWT.Resize, ev);
	}

	void onDispose(final Event event) {
		removeListener(SWT.Dispose, listener);
		notifyListeners(SWT.Dispose, event);
		event.type = SWT.None;
		/*
		 * Usually when an item is disposed, destroyItem will change the size of the items array, reset the bounds of
		 * all the tabs and manage the widget associated with the tab. Since the whole folder is being disposed, this is
		 * not necessary. For speed the inDispose flag is used to skip over this part of the item dispose.
		 */
		inDispose = true;

		for (var i = 0; i < itemCount; i++) {
			items[i].dispose();
		}
		items = null;
		// foreground = null;
		setFocusItem(null);
		// hoverItem = null;
		// popup = null;
	}

	void onFocus() {
		if (getFocusItem() != null) { getFocusItem().redraw(); }
	}

	void onHover(final MouseEvent event) {
		final var x = event.x;
		final var y = event.y;
		var hover = false;
		for (var i = 0; i < itemCount; i++) {
			final var item = items[i];
			hover = item.x <= x && x < item.x + item.width && item.y <= y && y < item.y + bandHeight;
			if (hover) {
				changeHoverTo(item);
				return;
			}
		}
		if (!hover) { changeHoverTo(null); }
	}

	void changeHoverTo(final ParameterExpandItem item) {
		if (hoverItem == item) return;
		final var oldHoverItem = hoverItem;
		hoverItem = item;
		if (oldHoverItem != null) { oldHoverItem.redraw(); }
		if (item != null) { item.redraw(); }
	}

	void onContextualMenu(final Event event) {
		final var x = event.x;
		final var y = event.y;
		for (var i = 0; i < itemCount; i++) {
			final var item = items[i];
			final var hover = item.x <= x && x < item.x + item.width && item.y <= y && y < item.y + bandHeight;
			if (!hover) { continue; }
			if (underlyingObjects != null) {
				ignoreMouseUp = true;
				final var p = toDisplay(x, y);
				final Map<String, Runnable> menuContents = underlyingObjects.handleMenu(item.getData(), p.x, p.y);
				if (menuContents == null)
					return;
				else {
					final var menu = new Menu(getShell(), SWT.POP_UP);

					for (final Map.Entry<String, Runnable> entry : menuContents.entrySet()) {
						final var menuItem = new MenuItem(menu, SWT.PUSH);
						menuItem.setText(entry.getKey());
						menuItem.addListener(SWT.Selection, e -> entry.getValue().run());
					}
					menu.setLocation(p.x, p.y);
					menu.setVisible(true);
					while (!menu.isDisposed() && menu.isVisible()) {
						if (!WorkbenchHelper.getDisplay().readAndDispatch()) { WorkbenchHelper.getDisplay().sleep(); }
					}
					menu.dispose();
				}
			}
		}
	}

	void onMouseDown(final Event event) {
		if (event.button != 1) return;
		final var x = event.x;
		final var y = event.y;
		for (var i = 0; i < itemCount; i++) {
			final var item = items[i];
			final var hover = item.x <= x && x < item.x + item.width && item.y <= y && y < item.y + bandHeight;
			if (!hover) { continue; }
			if (hasPausableToggle && item.pauseRequested(x, y)) {
				ignoreMouseUp = true;
				if (item.isPaused) {
					if (underlyingObjects != null) { underlyingObjects.resumeItem(item.getData()); }
					item.isPaused = false;
				} else {
					if (underlyingObjects != null) { underlyingObjects.pauseItem(item.getData()); }
					item.isPaused = true;
				}
				showItem(item);
				return;
			}
			if (hasVisibleToggle && item.visibleRequested(x, y)) {
				ignoreMouseUp = true;
				if (item.isVisible) {
					if (underlyingObjects != null) { underlyingObjects.makeItemVisible(item.getData(), false); }
					item.isVisible = false;
				} else {
					if (underlyingObjects != null) { underlyingObjects.makeItemVisible(item.getData(), true); }
					item.isVisible = true;
				}
				showItem(item);
				return;
			}
			if (hasSelectableToggle && item.selectableRequested(x, y)) {
				ignoreMouseUp = true;
				if (item.isSelectable) {
					if (underlyingObjects != null) { underlyingObjects.makeItemSelectable(item.getData(), false); }
					item.isSelectable = false;
				} else {
					if (underlyingObjects != null) { underlyingObjects.makeItemSelectable(item.getData(), true); }
					item.isSelectable = true;
				}
				showItem(item);
				return;

			}
			if (hasClosableToggle && item.closeRequested(x, y)) {
				ignoreMouseUp = true;
				item.dispose();
				return;
			}
			if (item != getFocusItem()) {
				if (getFocusItem() != null) { getFocusItem().redraw(); }
				setFocusItem(item);
				getFocusItem().redraw();
				forceFocus();
				break;
			}
		}
	}

	void onMouseUp(final Event event) {
		if (ignoreMouseUp) {
			ignoreMouseUp = false;
			return;
		}
		if (event.button != 1 || getFocusItem() == null) return;
		final var x = event.x;
		final var y = event.y;
		final var hover = getFocusItem().x <= x && x < getFocusItem().x + getFocusItem().width && getFocusItem().y <= y
				&& y < getFocusItem().y + bandHeight;
		if (hover) {
			final var ev = new Event();
			ev.item = getFocusItem();
			final var wasExpanded = getFocusItem().expanded;
			getFocusItem().setExpanded(!getFocusItem().expanded);
			notifyListeners(wasExpanded ? SWT.Collapse : SWT.Expand, ev);
			showItem(getFocusItem());
			WorkbenchHelper.copy(getFocusItem().getText());
		}
	}

	void onPaint(final Event event) {
		for (var i = 0; i < itemCount; i++) {
			final var item = items[i];
			event.gc.setAlpha(255);
			item.drawItem(event.gc, item == hoverItem);
		}
	}

	void onResize() {
		final var rect = getClientArea();
		final var width = Math.max(0, rect.width - spacing * 2);
		for (var i = 0; i < itemCount; i++) {
			final var item = items[i];
			if (item.getControl() != null) { item.setHeight(item.getControl().computeSize(width, SWT.DEFAULT).y); }
			item.setBounds(0, 0, width, item.height, false, true);
		}
		setScrollbar();
	}

	public void onScroll(final Event event) {
		final var verticalBar = getVerticalBar();
		if (verticalBar != null) {
			yCurrentScroll = verticalBar.getSelection();
			layoutItems(0, false);
		}
	}

	void setFocusItem(final ParameterExpandItem focusItem) {
		this.focusItem = focusItem;
		if (focusItem != null && underlyingObjects != null) { underlyingObjects.focusItem(focusItem.getData()); }
	}

	ParameterExpandItem getFocusItem() {
		return focusItem;
	}

	public void collapseItemWithData(final Object data) {
		if (data == null) return;
		for (final ParameterExpandItem i : items) {
			if (data.equals(i.getData())) {
				i.setExpanded(false);
				return;
			}
		}
	}

	// final Color backgroundColor =
	// ThemeHelper.isDark() ? IGamaColors.DARK_GRAY.color() : IGamaColors.VERY_LIGHT_GRAY.color();
	//
	// @Override
	// public Color getBackground() {
	// return backgroundColor;
	// }
	//
	// @Override
	// public void setBackground(final Color c) {
	// DEBUG.OUT("Viewer set with background: " + c);
	// // String background = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	// // setData("style", " background-color: " + background + ";");
	// super.setBackground(c);
	// }

}
