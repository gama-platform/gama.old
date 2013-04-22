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
	private static int imageHeight = 10, imageWidth = 10;
	boolean isPaused;
	private static final int TEXT_INSET = 4;
	private static final int SEPARATION = 4;
	private static final int BORDER = 1;
	static final int CHEVRON_SIZE = 20;

	/**
	 * Constructs a new instance of this class given its parent and a style value describing its
	 * behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to
	 * instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists
	 * the style constants that are applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * 
	 * @param parent a composite control which will be the parent of the new instance (cannot be
	 *            null)
	 * @param style the style of control to construct
	 * 
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 *                </ul>
	 * 
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public ParameterExpandItem(final ParameterExpandBar parent, final Object data, final int style) {
		this(parent, style, data, parent.getItemCount());
	}

	/**
	 * Constructs a new instance of this class given its parent, a style value describing its
	 * behavior and appearance, and the index at which to place it in the items maintained by its
	 * parent.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to
	 * instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists
	 * the style constants that are applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * 
	 * @param parent a composite control which will be the parent of the new instance (cannot be
	 *            null)
	 * @param style the style of control to construct
	 * @param index the zero-relative index to store the receiver in its parent
	 * 
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent
	 *                (inclusive)</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 *                </ul>
	 * 
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public ParameterExpandItem(final ParameterExpandBar parent, final int style, final Object data, final int index) {
		super(parent, style);
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

	void drawItem(final GC gc, final boolean drawFocus) {
		if ( parent == null ) { return; }
		int headerHeight = parent.bandHeight;
		Display display = getDisplay();
		gc.setForeground(display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		gc.drawRoundRectangle(x, y, width - 1, headerHeight + (expanded ? height - 1 : 0), 6, 6);
		// if ( expanded ) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.fillRoundRectangle(x + 1, y + 1, width - 2, headerHeight - 2, 6, 6);
		// }
		int drawX = x;
		int imageY = y + (headerHeight - imageHeight) / 2;
		if ( getImage() != null ) {
			drawX += ParameterExpandItem.TEXT_INSET;
			gc.drawImage(getImage(), drawX, imageY);
			drawX += imageWidth;
		}
		int endX = x + width;
		if ( parent.isClosable ) {
			endX -= ParameterExpandItem.TEXT_INSET + imageWidth;
			gc.drawImage(SwtGui.close, endX, imageY);
		}
		if ( parent.isPausable ) {
			Image image = isPaused ? SwtGui.play : SwtGui.pause;
			endX -= ParameterExpandItem.SEPARATION + imageWidth;
			gc.drawImage(image, endX, imageY);
		}
		if ( getText().length() > 0 ) {
			String title, other = null;
			int i = getText().indexOf(ItemList.SEPARATION_CODE);
			if ( i != -1 ) {
				title = getText().substring(0, i);
				other = getText().substring(i + 1);
			} else {
				title = getText();
			}
			gc.setFont(SwtGui.expandFont);
			drawX += ParameterExpandItem.SEPARATION;
			Point size = gc.stringExtent(title);
			gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.drawString(title, drawX, y + (headerHeight - size.y) / 2, true);
			if ( other != null ) {
				int j = other.indexOf(ItemList.ERROR_CODE);
				int k = other.indexOf(ItemList.INFO_CODE);
				int l = other.indexOf(ItemList.WARNING_CODE);
				if ( j != -1 ) {
					other = other.substring(j + 1);
					gc.setForeground(SwtGui.COLOR_ERROR);
				} else if ( k != -1 ) {
					other = other.substring(k + 1);
					gc.setForeground(SwtGui.COLOR_OK);
				} else if ( l != -1 ) {
					other = other.substring(l + 1);
					gc.setForeground(SwtGui.COLOR_WARNING);
				} else {
					gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
				}
				gc.setFont(SwtGui.labelFont);
				drawX += size.x + SEPARATION;
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
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
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
			gc.setFont(SwtGui.expandFont);
			width += gc.stringExtent(getText()).x;
		}
		return width;
	}

	void redraw() {
		if ( parent == null ) { return; }
		int headerHeight = parent.bandHeight;
		if ( imageHeight > headerHeight ) {
			parent.redraw(x + ParameterExpandItem.TEXT_INSET, y + headerHeight - imageHeight, imageWidth, imageHeight,
				false);
		}
		parent.redraw(x, y, width, headerHeight + height, false);
	}

	void setBounds(final int x, final int y, final int width, final int height, final boolean move, final boolean size) {
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
				control.setSize(Math.max(0, width - 2 * BORDER), Math.max(0, height - BORDER));
			}
		}
	}

	/**
	 * Sets the control that is shown when the item is expanded.
	 * 
	 * @param control the new control (or null)
	 * 
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li>
	 *                <li>ERROR_INVALID_PARENT - if the control is not in the same widget tree</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
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
				Math.max(0, height - BORDER));
		}
	}

	/**
	 * Sets the expanded state of the receiver.
	 * 
	 * @param expanded the new expanded state
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
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
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
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
		return clickIn(x2, y2, x + width - ParameterExpandItem.TEXT_INSET - imageWidth);
	}

	public boolean pauseRequested(final int x2, final int y2) {
		return clickIn(x2, y2, x + width - 2 * ParameterExpandItem.TEXT_INSET - 2 * imageWidth);
	}
}
