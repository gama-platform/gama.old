/*********************************************************************************************
 *
 *
 * 'ParameterExpandItem.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import msi.gama.common.interfaces.ItemList;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.util.GamaColor;

/**
 * Instances of this class represent a selectable user interface object that represents a expandable
 * item in a expand bar.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see ExpandBar
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 *
 * @since 3.2
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ParameterExpandItem extends Item {

	private ParameterExpandBar parent;
	Control control;
	boolean expanded;
	int x, y, width, height;
	int pausePosition = -1;
	int visiblePosition = -1;
	int selectablePosition = -1;
	int closePosition = -1;
	Color backgroundColor = IGamaColors.PARAMETERS_BACKGROUND.color();

	private static int imageHeight = 16, imageWidth = 16;
	boolean isPaused = false;
	boolean isVisible = true;
	boolean isSelectable = true;
	private static final int TEXT_INSET = 4;
	private static final int SEPARATION = 3;
	static final int BORDER = 4;
	static final int CHEVRON_SIZE = 24;

	/**
	 * Constructs a new instance of this class given its parent and a style value describing its
	 * behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to instances of this class, or must be built by <em>bitwise OR</em>'ing together (that
	 * is, using the <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists the style constants that are applicable to the class. Style bits
	 * are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new instance (cannot be
	 * null)
	 * @param style the style of control to construct
	 *
	 * @exception IllegalArgumentException <ul>
	 * <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 * <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 * </ul>
	 *
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public ParameterExpandItem(final ParameterExpandBar parent, final Object data, final int style,
		final GamaUIColor color) {
		this(parent, data, style, parent.getItemCount(), color);
	}

	/**
	 * Constructs a new instance of this class given its parent, a style value describing its
	 * behavior and appearance, and the index at which to place it in the items maintained by its
	 * parent.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to instances of this class, or must be built by <em>bitwise OR</em>'ing together (that
	 * is, using the <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists the style constants that are applicable to the class. Style bits
	 * are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new instance (cannot be
	 * null)
	 * @param style the style of control to construct
	 * @param index the zero-relative index to store the receiver in its parent
	 *
	 * @exception IllegalArgumentException <ul>
	 * <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 * <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
	 * </ul>
	 * @exception SWTException <ul>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 * <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 * </ul>
	 *
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public ParameterExpandItem(final ParameterExpandBar parent, final Object data, final int style, final int index,
		final GamaUIColor color) {
		super(parent, style);
		if ( color != null ) {
			backgroundColor = color.color();
		}
		this.parent = parent;
		setData(data);
		parent.createItem(this, style, index);
	}

	@Override
	public void dispose() {
		if ( isDisposed() ) { return; }
		// GuiUtils.debug("ParameterItem being disposed");
		// if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
		if ( parent != null ) {
			parent.destroyItem(this);
		}
		super.dispose();
		if ( control != null ) {
			control.dispose();
			control = null;
		}
		parent = null;

	}

	void drawItem(final GC gc, final boolean drawHover) {
		if ( parent == null ) { return; }
		int headerHeight = parent.bandHeight;
		gc.setForeground(IGamaColors.PARAMETERS_BACKGROUND.color());
		gc.setBackground(IGamaColors.PARAMETERS_BACKGROUND.color());
		gc.fillRoundRectangle(x, y, width, headerHeight + (expanded ? height + ParameterExpandItem.BORDER : 0), 6, 6);
		gc.setBackground(backgroundColor);
		gc.fillRoundRectangle(x, y, width, headerHeight, 6, 6);
		if ( drawHover ) {
			gc.setForeground(IGamaColors.GRAY_LABEL.color());
			gc.drawRoundRectangle(x + 1, y + 1, width - 2, headerHeight - 2, 6, 6);
		}

		// gc.drawRoundRectangle(x, y, width, headerHeight + (expanded ? height : 0), 6, 6);
		int drawX = x;
		int imageY = y /*- 1*/ + (headerHeight - imageHeight) / 2;
		if ( getImage() != null ) {
			drawX += ParameterExpandItem.TEXT_INSET;
			gc.drawImage(getImage(), drawX, imageY);
			drawX += imageWidth;
		}
		int endX = x + width;
		if ( parent.hasClosableToggle ) {
			endX -= 2 * TEXT_INSET + imageWidth;
			closePosition = endX;
			gc.drawImage(IGamaIcons.SMALL_CLOSE.image(), endX, imageY);
		}
		if ( parent.hasPausableToggle ) {
			Image image = isPaused ? IGamaIcons.SMALL_RESUME.image() : IGamaIcons.SMALL_PAUSE.image();
			endX -= 2 * TEXT_INSET + imageWidth;
			pausePosition = endX;
			gc.drawImage(image, endX, imageY);
		}

		if ( parent.hasVisibleToggle ) {
			Image image =
				isVisible ? GamaIcons.create("small.inspect").image() : GamaIcons.create("small.hidden").image();
			endX -= 2 * TEXT_INSET + imageWidth;
			visiblePosition = endX;
			gc.drawImage(image, endX, imageY);
		}
		if ( parent.hasSelectableToggle ) {
			Image image = isSelectable ? GamaIcons.create("small.selectable").image()
				: GamaIcons.create("small.unselectable").image();
			endX -= 2 * TEXT_INSET + imageWidth;
			selectablePosition = endX;
			gc.drawImage(image, endX, imageY);
		}
		if ( getText().length() > 0 ) {
			String title, other = null;
			int i = getText().indexOf(ItemList.SEPARATION_CODE);
			if ( i != -1 ) {
				title = getText().substring(0, i) + ": ";
				other = getText().substring(i + 1);
			} else {
				title = getText();
			}
			gc.setFont(SwtGui.getExpandfont());
			drawX += 2 * ParameterExpandItem.TEXT_INSET;
			Point size = gc.stringExtent(title);
			gc.setForeground(IGamaColors.NEUTRAL.color());
			// gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			gc.drawString(title, drawX, y + (headerHeight - size.y) / 2, true);
			// gc.setFont(SwtGui.getUnitFont());
			if ( other != null ) {
				int j = other.indexOf(ItemList.ERROR_CODE);
				int k = other.indexOf(ItemList.INFO_CODE);
				int l = other.indexOf(ItemList.WARNING_CODE);
				if ( j != -1 ) {
					other = other.substring(j + 1);
					gc.setForeground(IGamaColors.ERROR.color());
				} else if ( k != -1 ) {
					other = other.substring(k + 1);
					gc.setForeground(IGamaColors.OK.color());
				} else if ( l != -1 ) {
					other = other.substring(l + 1);
					gc.setForeground(IGamaColors.WARNING.color());
				} else {
					gc.setForeground(GamaColors.get(backgroundColor.getRGB()).isDark()
						? GamaColors.system(SWT.COLOR_WHITE) : GamaColors.system(SWT.COLOR_BLACK));
				}
				// gc.setFont(SwtGui.getParameterEditorsFont());
				drawX += size.x + 2 * SEPARATION;
				size = gc.stringExtent(other);
				gc.setClipping(drawX, y, endX - drawX, headerHeight);
				gc.drawString(other, drawX, y + (headerHeight - size.y) / 2, true);
				gc.setClipping((Rectangle) null);
			}
		}
	}

	/**
	 * Returns the height of the receiver's header
	 *
	 * @return the height of the header
	 *
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public int getHeaderHeight() {
		// checkWidget();
		if ( parent == null ) { return imageHeight; }
		return Math.max(parent.bandHeight, imageHeight);
	}

	int getPreferredWidth(final GC gc) {
		int width = ParameterExpandItem.TEXT_INSET * 2 + ParameterExpandItem.CHEVRON_SIZE;
		if ( getImage() != null ) {
			width += ParameterExpandItem.TEXT_INSET + imageWidth;
		}
		if ( getText().length() > 0 ) {
			gc.setFont(SwtGui.getExpandfont());
			width += gc.stringExtent(getText()).x;
		}
		if ( control != null ) {
			width += control.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		}
		return width;
	}

	void redraw() {
		if ( parent == null ) { return; }
		int headerHeight = parent.bandHeight;
		if ( imageHeight > headerHeight ) {
			parent.redraw(x + TEXT_INSET, y + headerHeight - imageHeight, imageWidth, imageHeight, false);
		}
		parent.redraw(x, y, width, headerHeight + height, false);
	}

	void setBounds(final int x, final int y, final int width, final int height, final boolean move,
		final boolean size) {
		redraw();
		if ( parent == null ) { return; }
		int headerHeight = parent.bandHeight;
		int y1 = y;
		if ( move ) {
			if ( imageHeight > headerHeight ) {
				y1 += imageHeight - headerHeight;
			}
			this.x = x;
			this.y = y1;
			redraw();
		}
		if ( size ) {
			this.width = width;
			this.height = height;
			redraw();
		}
		if ( control != null && !control.isDisposed() ) {
			if ( move ) {
				control.setLocation(x + BORDER, y + headerHeight);
			}
			if ( size ) {
				control.setSize(control.computeSize(width - 2 * BORDER, height + BORDER /*- BORDER*/));
				((Composite) control).layout(true);
				// control.setSize(Math.max(0, width - 2 * BORDER), Math.max(0, height - BORDER));
			}
		}
	}

	/**
	 * Sets the control that is shown when the item is expanded.
	 *
	 * @param control the new control (or null)
	 *
	 * @exception IllegalArgumentException <ul>
	 * <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li>
	 * <li>ERROR_INVALID_PARENT - if the control is not in the same widget tree</li>
	 * </ul>
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setControl(final Control control) {
		// checkWidget();
		if ( control != null ) {
			if ( control.isDisposed() ) {
				SWT.error(SWT.ERROR_INVALID_ARGUMENT);
			}
			if ( control.getParent() != parent ) {
				SWT.error(SWT.ERROR_INVALID_PARENT);
			}
		}
		this.control = control;
		if ( control != null ) {
			control.setVisible(expanded);
			int headerHeight = parent.bandHeight;
			control.setBounds(x + BORDER, y + headerHeight, Math.max(0, width - 2 * BORDER),
				Math.max(0, height + BORDER));
			control.setBackground(IGamaColors.PARAMETERS_BACKGROUND.color());
		}
	}

	/**
	 * Sets the expanded state of the receiver.
	 *
	 * @param expanded the new expanded state
	 *
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setExpanded(final boolean expanded) {
		if ( parent == null ) { return; }
		// checkWidget();
		this.expanded = expanded;
		parent.showItem(this);
	}

	@Override
	public void setImage(final Image image) {
		super.setImage(image);
		int oldImageHeight = imageHeight;
		if ( image != null ) {
			Rectangle bounds = image.getBounds();
			imageHeight = bounds.height;
			imageWidth = bounds.width;
		} else {
			imageHeight = imageWidth = 0;
		}
		if ( oldImageHeight != imageHeight && parent != null ) {
			parent.layoutItems(parent.indexOf(this), true);
		} else {
			redraw();
		}
	}

	/**
	 * Sets the height of the receiver. This is height of the item when it is expanded, excluding
	 * the height of the header.
	 *
	 * @param height the new height
	 *
	 * @exception SWTException <ul>
	 * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setHeight(final int height) {
		// checkWidget();
		if ( height < 0 ) { return; }
		setBounds(0, 0, width, height, false, true);
		if ( expanded && parent != null ) {
			parent.layoutItems(parent.indexOf(this) + 1, true);
		}
	}

	@Override
	public void setText(final String string) {
		super.setText(string);
		redraw();
	}

	private boolean clickIn(final int x2, final int y2, final int xmin) {
		int xmax = xmin + imageWidth;
		int headerHeight = parent.bandHeight;
		int ymin = y + (headerHeight - imageHeight) / 2;
		int ymax = ymin + imageHeight;
		return x2 >= xmin && x2 <= xmax && y2 >= ymin && y2 <= ymax;
	}

	public boolean closeRequested(final int x2, final int y2) {
		if ( closePosition == -1 ) { return false; }
		return clickIn(x2, y2, x + closePosition);
	}

	public boolean pauseRequested(final int x2, final int y2) {
		if ( pausePosition == -1 ) { return false; }
		return clickIn(x2, y2, x + pausePosition);
	}

	public boolean visibleRequested(final int x2, final int y2) {
		if ( visiblePosition == -1 ) { return false; }
		return clickIn(x2, y2, x + visiblePosition);
	}

	public boolean selectableRequested(final int x2, final int y2) {
		if ( selectablePosition == -1 ) { return false; }
		return clickIn(x2, y2, x + selectablePosition);
	}

	/**
	 * @param itemDisplayColor
	 */
	public void setColor(final GamaColor color) {
		if ( color != null ) {
			backgroundColor = GamaColors.get(color).color();
		}
	}

}
