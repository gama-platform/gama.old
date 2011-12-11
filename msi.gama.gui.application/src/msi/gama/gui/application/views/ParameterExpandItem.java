/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.views;

import msi.gama.gui.application.GUI;
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

	ParameterExpandBar parent;
	Control control;
	boolean expanded;
	int x, y, width, height;
	static int imageHeight = 10, imageWidth = 10;
	// boolean isClosable;
	boolean isPaused;
	static final int TEXT_INSET = 4;
	static final int SEPARATION = 4;
	static final int BORDER = 1;

	static final int CHEVRON_SIZE = 20;

	/**
	 * Constructs a new instance of this class given its parent and a style value describing its
	 * behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which
	 * is applicable to instances of this class, or must be built by <em>bitwise OR</em>'ing
	 * together (that is, using the <code>int</code> "|" operator) two or more of those
	 * <code>SWT</code> style constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
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
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 *                </ul>
	 * 
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public ParameterExpandItem(final ParameterExpandBar parent, final Object data, final int style) {
		this(parent, style, data, checkNull(parent).getItemCount());
	}

	/**
	 * Constructs a new instance of this class given its parent, a style value describing its
	 * behavior and appearance, and the index at which to place it in the items maintained by its
	 * parent.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which
	 * is applicable to instances of this class, or must be built by <em>bitwise OR</em>'ing
	 * together (that is, using the <code>int</code> "|" operator) two or more of those
	 * <code>SWT</code> style constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * 
	 * @param parent a composite control which will be the parent of the new instance (cannot be
	 *            null)
	 * @param style the style of control to construct
	 * @param index the zero-relative index to store the receiver in its parent
	 * 
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of
	 *                elements in the parent (inclusive)</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 *                </ul>
	 * 
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public ParameterExpandItem(final ParameterExpandBar parent, final int style, final Object data,
		final int index) {
		super(parent, style);
		this.parent = parent;
		setData(data);
		// isClosable = parent.isClosable;
		parent.createItem(this, style, index);
	}

	static ParameterExpandBar checkNull(final ParameterExpandBar control) {
		if ( control == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		return control;
	}

	@Override
	public void dispose() {
		if ( isDisposed() ) { return; }
		// if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
		parent.destroyItem(this);
		super.dispose();
		disposeControl(false);
		parent = null;

	}

	public void disposeControl(final boolean display) {
		if ( control != null ) {
			control.dispose();
			control = null;
			if ( display ) {
				setHeight(0);
				setExpanded(false);
			}
		}
	}

	public boolean isControlDisposed() {
		return control == null || control.isDisposed();
	}

	void drawItem(final GC gc, final boolean drawFocus) {
		int headerHeight = parent.getBandHeight();
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
			gc.drawImage(GUI.close, endX, imageY);
		}
		if ( parent.isPausable ) {
			Image image = isPaused ? GUI.play : GUI.pause;
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
			gc.setFont(GUI.expandFont);
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
					gc.setForeground(GUI.COLOR_ERROR);
				} else if ( k != -1 ) {
					other = other.substring(k + 1);
					gc.setForeground(GUI.COLOR_OK);
				} else if ( l != -1 ) {
					other = other.substring(l + 1);
					gc.setForeground(GUI.COLOR_WARNING);
				} else {
					gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
				}
				gc.setFont(GUI.labelFont);
				drawX += size.x + SEPARATION;
				size = gc.stringExtent(other);
				gc.setClipping(drawX, y, endX - drawX, headerHeight);
				gc.drawString(other, drawX, y + (headerHeight - size.y) / 2, true);
				gc.setClipping((Rectangle) null);
			}
		}
	}

	/**
	 * Returns the control that is shown when the item is expanded. If no control has been set,
	 * return <code>null</code>.
	 * 
	 * @return the control
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public Control getControl() {
		checkWidget();
		return control;
	}

	/**
	 * Returns <code>true</code> if the receiver is expanded, and false otherwise.
	 * 
	 * @return the expanded state
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public boolean getExpanded() {
		checkWidget();
		return expanded;
	}

	/**
	 * Returns the height of the receiver's header
	 * 
	 * @return the height of the header
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public int getHeaderHeight() {
		checkWidget();
		return Math.max(parent.getBandHeight(), imageHeight);
	}

	/**
	 * Gets the height of the receiver.
	 * 
	 * @return the height
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public int getHeight() {
		checkWidget();
		return height;
	}

	/**
	 * Returns the receiver's parent, which must be a <code>ExpandBar</code>.
	 * 
	 * @return the receiver's parent
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public ParameterExpandBar getParent() {
		checkWidget();
		return parent;
	}

	int getPreferredWidth(final GC gc) {
		int width = ParameterExpandItem.TEXT_INSET * 2 + ParameterExpandItem.CHEVRON_SIZE;
		if ( getImage() != null ) {
			width += ParameterExpandItem.TEXT_INSET + imageWidth;
		}
		if ( getText().length() > 0 ) {
			gc.setFont(GUI.expandFont);
			width += gc.stringExtent(getText()).x;
		}
		return width;
	}

	void redraw() {
		int headerHeight = parent.getBandHeight();
		if ( imageHeight > headerHeight ) {
			parent.redraw(x + ParameterExpandItem.TEXT_INSET, y + headerHeight - imageHeight,
				imageWidth, imageHeight, false);
		}
		parent.redraw(x, y, width, headerHeight + height, false);
	}

	void setBounds(final int x, final int y, final int width, final int height, final boolean move,
		final boolean size) {
		redraw();
		int headerHeight = parent.getBandHeight();
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
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public void setControl(final Control control) {
		checkWidget();
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
			int headerHeight = parent.getBandHeight();
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
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public void setExpanded(final boolean expanded) {
		checkWidget();
		this.expanded = expanded;
		// setImage(expanded ? ParameterExpandBar.collapse : ParameterExpandBar.expand);
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
		if ( oldImageHeight != imageHeight ) {
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
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 */
	public void setHeight(final int height) {
		checkWidget();
		if ( height < 0 ) { return; }
		setBounds(0, 0, width, height, false, true);
		if ( expanded ) {
			parent.layoutItems(parent.indexOf(this) + 1, true);
		}
	}

	@Override
	public void setText(final String string) {
		super.setText(string);
		redraw();
	}

	public boolean closeRequested(final int x2, final int y2) {
		int xmin = x + width - ParameterExpandItem.TEXT_INSET - imageWidth;
		int xmax = xmin + imageWidth;
		int headerHeight = parent.getBandHeight();
		int ymin = y + (headerHeight - imageHeight) / 2;
		int ymax = ymin + imageHeight;
		return x2 >= xmin && x2 <= xmax && y2 >= ymin && y2 <= ymax;
	}

	public boolean pauseRequested(final int x2, final int y2) {
		int xmin = x + width - 2 * ParameterExpandItem.TEXT_INSET - 2 * imageWidth;
		int xmax = xmin + imageWidth;
		int headerHeight = parent.getBandHeight();
		int ymin = y + (headerHeight - imageHeight) / 2;
		int ymax = ymin + imageHeight;
		return x2 >= xmin && x2 <= xmax && y2 >= ymin && y2 <= ymax;
	}
}
