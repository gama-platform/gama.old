/*******************************************************************************************************
 *
 * ParameterExpandItem.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

import msi.gama.application.workbench.ThemeHelper;
import msi.gama.common.interfaces.ItemList;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * Instances of this class represent a selectable user interface object that represents a expandable item in a expand
 * bar.
 */
public class ParameterExpandItem extends Item {

	/** The parent. */
	private ParameterExpandBar parent;

	/** The control. */
	Composite control;

	/** The expanded. */
	boolean expanded;

	/** The height. */
	int x, y, width, height;

	/** The pause position. */
	int pausePosition = -1;

	/** The visible position. */
	int visiblePosition = -1;

	/** The selectable position. */
	int selectablePosition = -1;

	/** The close position. */
	int closePosition = -1;

	/** The header color. */
	Color headerColor = ThemeHelper.isDark() ? IGamaColors.DARK_GRAY.color() : IGamaColors.VERY_LIGHT_GRAY.color();

	/** The image width. */
	private static int imageHeight = 16, imageWidth = 16;

	/** The is paused. */
	boolean isPaused = false;

	/** The is selectable. */
	boolean isSelectable = true;

	/** The on expand block. */
	private Runnable onExpandBlock;

	/** The Constant TEXT_INSET. */
	private static final int TEXT_INSET = 4;

	/** The Constant SEPARATION. */
	private static final int SEPARATION = 3;

	/** The Constant BORDER. */
	static final int BORDER = 4;

	/** The Constant CHEVRON_SIZE. */
	static final int CHEVRON_SIZE = 20;

	/**
	 * Instantiates a new parameter expand item.
	 *
	 * @param parent
	 *            the parent
	 * @param data
	 *            the data
	 * @param style
	 *            the style
	 * @param color
	 *            the color
	 */
	public ParameterExpandItem(final ParameterExpandBar parent, final Object data, final int style,
			final GamaUIColor color) {
		this(parent, data, style, parent.getItemCount(), color);
	}

	/**
	 * Instantiates a new parameter expand item.
	 *
	 * @param parent
	 *            the parent
	 * @param data
	 *            the data
	 * @param style
	 *            the style
	 * @param index
	 *            the index
	 * @param color
	 *            the color
	 */
	public ParameterExpandItem(final ParameterExpandBar parent, final Object data, final int style, final int index,
			final GamaUIColor color) {
		super(parent, style);
		if (color != null) { headerColor = color.color(); }
		this.parent = parent;
		setData(data);
		parent.createItem(this, style, index);
	}

	@Override
	public void dispose() {
		if (isDisposed()) return;
		if (parent != null) { parent.destroyItem(this); }
		super.dispose();
		if (control != null) {
			control.dispose();
			control = null;
		}
		parent = null;

	}

	/**
	 * Draw item.
	 *
	 * @param gc
	 *            the gc
	 * @param drawHover
	 *            the draw hover
	 */
	void drawItem(final GC gc, final boolean drawHover) {
		if (parent == null) return;
		final var headerHeight = parent.bandHeight;
		control.setBackground(this.parent.getBackground());
		gc.setBackground(headerColor);
		gc.fillRoundRectangle(x + 1, y, width - 2, headerHeight, 6, 6);
		if (drawHover) {
			gc.setForeground(IGamaColors.GRAY_LABEL.color());
			gc.drawRoundRectangle(x + 1, y + 1, width - 2, headerHeight - 2, 6, 6);
		}

		var drawX = x;
		final var imageY = y /*- 1*/ + (headerHeight - imageHeight) / 2;
		if (getImage() != null/* && drawHover */) {
			drawX += ParameterExpandItem.TEXT_INSET;
			gc.drawImage(getImage(), drawX, imageY);
			drawX += imageWidth;
		}
		var endX = x + width;
		if (parent.hasClosableToggle) {
			endX -= 2 * TEXT_INSET + imageWidth;
			closePosition = endX;
			gc.drawImage(GamaIcon.named(IGamaIcons.SMALL_CLOSE).image(), endX, imageY);
		}
		if (parent.hasPausableToggle) {
			final var image = isPaused ? GamaIcon.named(IGamaIcons.SMALL_RESUME).image()
					: GamaIcon.named(IGamaIcons.SMALL_PAUSE).image();
			endX -= 2 * TEXT_INSET + imageWidth;
			pausePosition = endX;
			gc.drawImage(image, endX, imageY);
		}

		if (parent.hasVisibleToggle) {
			final var image = parent.isVisible(this) ? GamaIcon.named(IGamaIcons.SMALL_INSPECT).image()
					: GamaIcon.named(IGamaIcons.SMALL_HIDDEN).image();
			endX -= 2 * TEXT_INSET + imageWidth;
			visiblePosition = endX;
			gc.drawImage(image, endX, imageY);
		}
		if (parent.hasSelectableToggle) {
			final var image = isSelectable ? GamaIcon.named(IGamaIcons.SMALL_SELECTABLE).image()
					: GamaIcon.named(IGamaIcons.SMALL_UNSELECTABLE).image();
			endX -= 2 * TEXT_INSET + imageWidth;
			selectablePosition = endX;
			gc.drawImage(image, endX, imageY);
		}
		if (getText().length() > 0) {
			String title, other = null;
			final var i = getText().indexOf(ItemList.SEPARATION_CODE);
			if (i != -1) {
				title = getText().substring(0, i) + ": ";
				other = getText().substring(i + 1);
			} else {
				title = getText();
			}
			drawX += 2 * ParameterExpandItem.TEXT_INSET;
			var size = gc.stringExtent(title);
			gc.setForeground(GamaColors.getTextColorForBackground(headerColor).color());
			gc.drawString(title, drawX, y + (headerHeight - size.y) / 2, true);
			if (other != null) {
				final var j = other.indexOf(ItemList.ERROR_CODE);
				final var k = other.indexOf(ItemList.INFO_CODE);
				final var l = other.indexOf(ItemList.WARNING_CODE);
				if (j != -1) {
					other = other.substring(j + 1);
					gc.setForeground(IGamaColors.ERROR.color());
				} else if (k != -1) {
					other = other.substring(k + 1);
					gc.setForeground(IGamaColors.OK.color());
				} else if (l != -1) {
					other = other.substring(l + 1);
					gc.setForeground(IGamaColors.WARNING.color());
				} else {
					gc.setForeground(GamaColors.getTextColorForBackground(headerColor).color());
				}
				drawX += size.x + 2 * SEPARATION;
				size = gc.stringExtent(other);

				gc.drawString(other, drawX, y + (headerHeight - size.y) / 2, true);
			}
		}
	}

	/**
	 * Returns the height of the receiver's header
	 *
	 * @return the height of the header
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getHeaderHeight() {
		if (parent == null) return imageHeight;
		return Math.max(parent.bandHeight, imageHeight);
	}

	/**
	 * Gets the preferred width.
	 *
	 * @param gc
	 *            the gc
	 * @return the preferred width
	 */
	int getPreferredWidth(final GC gc) {
		var width = ParameterExpandItem.TEXT_INSET * 2 + ParameterExpandItem.CHEVRON_SIZE;
		if (getImage() != null) { width += ParameterExpandItem.TEXT_INSET + imageWidth; }
		if (getText().length() > 0) {
			// gc.setFont(GamaFonts.getExpandfont());
			width += gc.stringExtent(getText()).x;
		}
		if (control != null) { width += control.computeSize(SWT.DEFAULT, SWT.DEFAULT).x; }
		return width;
	}

	/**
	 * Redraw.
	 */
	void redraw() {
		if (parent == null) return;
		final var headerHeight = parent.bandHeight;
		if (imageHeight > headerHeight) {
			parent.redraw(x + TEXT_INSET, y + headerHeight - imageHeight, imageWidth, imageHeight, false);
		}
		parent.redraw(x, y, width, headerHeight + height, false);
	}

	/**
	 * Sets the bounds.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param move
	 *            the move
	 * @param size
	 *            the size
	 */
	void setBounds(final int x, final int y, final int width, final int height, final boolean move,
			final boolean size) {
		redraw();
		if (parent == null) return;
		final var headerHeight = parent.bandHeight;
		var y1 = y;
		if (move) {
			if (imageHeight > headerHeight) { y1 += imageHeight - headerHeight; }
			this.x = x;
			this.y = y1;
			redraw();
		}
		if (size) {
			this.width = width;
			this.height = height;
			redraw();
		}
		if (control != null && !control.isDisposed()) {
			if (move) { control.setLocation(x + BORDER, y + headerHeight); }
			if (size) {
				var w = width - 2 * BORDER;
				var h = height + BORDER;
				if (control.getVerticalBar() != null) { w = w - control.getVerticalBar().getSize().x; }
				if (control.getHorizontalBar() != null && control.getHorizontalBar().isVisible()) {

					h = h - 2 * control.getHorizontalBar().getSize().y;
				}
				control.setSize(control.computeSize(w, h));
				control.layout(true);
			}
		}
	}

	/**
	 * Sets the control that is shown when the item is expanded.
	 *
	 * @param control
	 *            the new control (or null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li>
	 *                <li>ERROR_INVALID_PARENT - if the control is not in the same widget tree</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setControl(final Composite control) {
		if (control != null) {
			if (control.isDisposed()) { SWT.error(SWT.ERROR_INVALID_ARGUMENT); }
			if (control.getParent() != parent) { SWT.error(SWT.ERROR_INVALID_PARENT); }
		}
		this.control = control;
		if (control != null) {
			control.setVisible(expanded);
			final var headerHeight = parent.bandHeight;
			control.setBounds(x + BORDER, y + headerHeight, Math.max(0, width - 2 * BORDER),
					Math.max(0, height + BORDER));

		}
	}

	/**
	 * Sets the expanded state of the receiver.
	 *
	 * @param expanded
	 *            the new expanded state
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setExpanded(final boolean expanded) {
		if (parent == null) return;
		// checkWidget();
		this.expanded = expanded;
		if (onExpandBlock != null) {
			if (expanded) {
				onExpandBlock.run();
				setHeight(control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			} else {
				for (final Control c : control.getChildren()) { c.dispose(); }
				if (control instanceof ScrolledComposite) { ((ScrolledComposite) control).setContent(null); }
			}
		}
		parent.showItem(this);
	}

	@Override
	public void setImage(final Image image) {
		super.setImage(image);
		final var oldImageHeight = imageHeight;
		if (image != null) {
			final var bounds = image.getBounds();
			imageHeight = bounds.height;
			imageWidth = bounds.width;
		} else {
			imageHeight = imageWidth = 0;
		}
		if (oldImageHeight != imageHeight && parent != null) {
			parent.layoutItems(parent.indexOf(this), true);
		} else {
			redraw();
		}
	}

	/**
	 * Sets the height of the receiver. This is height of the item when it is expanded, excluding the height of the
	 * header.
	 *
	 * @param height
	 *            the new height
	 */
	public void setHeight(final int height) {
		if (height < 0) return;
		setBounds(0, 0, width, height, false, true);
		if (expanded && parent != null) { parent.layoutItems(parent.indexOf(this) + 1, true); }
	}

	@Override
	public void setText(final String string) {
		super.setText(string);
		redraw();
	}

	/**
	 * Click in.
	 *
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @param xmin
	 *            the xmin
	 * @return true, if successful
	 */
	private boolean clickIn(final int x2, final int y2, final int xmin) {
		final var xmax = xmin + imageWidth;
		final var headerHeight = parent.bandHeight;
		final var ymin = y + (headerHeight - imageHeight) / 2;
		final var ymax = ymin + imageHeight;
		return x2 >= xmin && x2 <= xmax && y2 >= ymin && y2 <= ymax;
	}

	/**
	 * Close requested.
	 *
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @return true, if successful
	 */
	public boolean closeRequested(final int x2, final int y2) {
		if (closePosition == -1) return false;
		return clickIn(x2, y2, x + closePosition);
	}

	/**
	 * Pause requested.
	 *
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @return true, if successful
	 */
	public boolean pauseRequested(final int x2, final int y2) {
		if (pausePosition == -1) return false;
		return clickIn(x2, y2, x + pausePosition);
	}

	/**
	 * Visible requested.
	 *
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @return true, if successful
	 */
	public boolean visibleRequested(final int x2, final int y2) {
		if (visiblePosition == -1) return false;
		return clickIn(x2, y2, x + visiblePosition);
	}

	/**
	 * Selectable requested.
	 *
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @return true, if successful
	 */
	public boolean selectableRequested(final int x2, final int y2) {
		if (selectablePosition == -1) return false;
		return clickIn(x2, y2, x + selectablePosition);
	}

	/**
	 * @param itemDisplayColor
	 */
	public void setColor(final java.awt.Color color) {
		if (color != null) { headerColor = GamaColors.get(color).color(); }
	}

	/**
	 * On expand.
	 *
	 * @param r
	 *            the r
	 */
	public void onExpand(final Runnable r) {
		onExpandBlock = r;
	}

	/**
	 * Gets the control.
	 *
	 * @return the control
	 */
	public Control getControl() { return control; }

}
