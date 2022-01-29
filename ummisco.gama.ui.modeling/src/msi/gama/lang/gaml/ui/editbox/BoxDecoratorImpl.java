/*******************************************************************************************************
 *
 * BoxDecoratorImpl.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ColorDialog;

/**
 * The Class BoxDecoratorImpl.
 */
public class BoxDecoratorImpl implements IBoxDecorator {

	/** The Constant ROUND_BOX_ARC. */
	protected static final int ROUND_BOX_ARC = 5;
	
	/** The provider. */
	protected IBoxProvider provider;
	
	/** The visible. */
	protected boolean visible;
	
	/** The settings. */
	protected IBoxSettings settings;
	
	/** The box text. */
	protected StyledText boxText;
	
	/** The box key. */
	protected BoxKeyListener boxKey;
	
	/** The box modify. */
	protected BoxModifyListener boxModify;
	
	/** The box paint. */
	protected BoxPaintListener boxPaint;
	
	/** The box mouse move. */
	protected BoxMouseMoveListener boxMouseMove;
	
	/** The box mouse track. */
	protected BoxMouseTrackListener boxMouseTrack;
	
	/** The box text change. */
	protected BoxTextChangeListener boxTextChange;
	
	/** The box mouse click. */
	protected BoxMouseClickListener boxMouseClick;
	
	/** The fill mouse click. */
	protected FillBoxMouseClick fillMouseClick;
	
	/** The settings change listener. */
	protected SettingsChangeListener settingsChangeListener;
	
	/** The old background. */
	protected RGB oldBackground;
	
	/** The old indent. */
	protected int oldIndent;
	
	/** The decorated. */
	protected boolean decorated;
	
	/** The boxes. */
	protected List<Box> boxes;
	
	/** The set caret offset. */
	protected boolean setCaretOffset;
	
	/** The builder name. */
	protected String builderName;
	
	/** The builder. */
	protected IBoxBuilder builder;
	
	/** The current box. */
	protected Box currentBox;
	
	/** The old caret loc. */
	protected Point oldCaretLoc;
	
	/** The old X offset. */
	protected int oldXOffset = -1;
	
	/** The old Y offset. */
	protected int oldYOffset = -1;
	
	/** The old client area. */
	protected Rectangle oldClientArea;
	
	/** The fill box start. */
	protected int fillBoxStart = -1;
	
	/** The fill box end. */
	protected int fillBoxEnd = -1;
	
	/** The fill box level. */
	protected int fillBoxLevel = -1;
	
	/** The state mask. */
	protected int stateMask;
	
	/** The key pressed. */
	public boolean keyPressed;
	
	/** The char count. */
	protected int charCount;

	@Override
	public void enableUpdates(final boolean flag) {
		final boolean update = flag && !this.visible;
		this.visible = flag;
		if (update) {
			boxes = null;
			update();
		}
	}

	@Override
	public IBoxProvider getProvider() {
		return provider;
	}

	@Override
	public void setProvider(final IBoxProvider newProvider) {
		this.provider = newProvider;
	}

	@Override
	public void setSettings(final IBoxSettings newSettings) {
		this.settings = newSettings;
		settingsChangeListener = new SettingsChangeListener();
		this.settings.addPropertyChangeListener(settingsChangeListener);
	}

	@Override
	public void setStyledText(final StyledText newSt) {
		this.boxText = newSt;
	}

	/**
	 * Builds the boxes.
	 */
	protected void buildBoxes() {
		final IBoxBuilder boxBuilder = getBuilder();
		if (boxBuilder == null) { return; }

		builder.setTabSize(boxText.getTabs());
		builder.setCaretOffset(setCaretOffset ? boxText.getCaretOffset() : -1);
		setCaretOffset = false;

		final StringBuilder text = new StringBuilder(boxText.getText());

		if (text.length() > 0 && text.charAt(text.length() - 1) != '\n') {
			text.append(".");
		}

		boxBuilder.setText(text);
		boxes = boxBuilder.build();

		charCount = boxText.getCharCount();
	}

	/**
	 * Gets the builder.
	 *
	 * @return the builder
	 */
	protected IBoxBuilder getBuilder() {
		if (settings.getBuilder() == null) { return null; }
		if (builder == null || builderName == null || !builderName.equals(settings.getBuilder())) {
			builderName = settings.getBuilder();
			builder = provider.createBoxBuilder(builderName);
		}
		return builder;
	}

	@Override
	public void forceUpdate() {
		boxes = null;
		update();
	}

	/**
	 * Update.
	 */
	public void update() {
		if (decorated && visible) {
			if (builder != null
					&& (!builderName.equals(settings.getBuilder()) || builder.getTabSize() != boxText.getTabs())) {
				boxes = null;
			}

			if (boxes == null) {
				buildBoxes();
			}

			offsetMoved();
			updateCaret();
			drawBackgroundBoxes();
		}
	}

	/**
	 * Draw background boxes.
	 */
	void drawBackgroundBoxes() {
		if (boxes == null || !visible) { return; }

		final Rectangle r0 = boxText.getClientArea();

		if (r0.width < 1 || r0.height < 1) { return; }

		final int xOffset = boxText.getHorizontalPixel();
		final int yOffset = boxText.getTopPixel();

		final Image newImage = new Image(null, r0.width, r0.height);
		final GC gc = new GC(newImage);

		// fill background
		Color bc = settings.getColor(0);
		if (settings.getNoBackground() && oldBackground != null) {
			bc = new Color(null, oldBackground);
		}
		if (bc != null) {
			final Rectangle rec = newImage.getBounds();
			fillRectangle(bc, gc, rec.x, rec.y, rec.width, rec.height);
		}

		if (settings.getAlpha() > 0) {
			gc.setAlpha(settings.getAlpha());
		}

		// fill boxes
		Box fillBox = null;
		final boolean checkFillbox = !settings.getFillOnMove();
		final Collection<Box> visibleBoxes = visibleBoxes();

		final boolean ex = settings.getExpandBox();

		for (final Box b : visibleBoxes) {
			if (checkFillbox && b.level == fillBoxLevel && b.start <= fillBoxStart && b.end >= fillBoxEnd) {
				fillBox = b;
			}
			fillRectangle(settings.getColor(b.level + 1), gc, b.rec.x - xOffset, b.rec.y - yOffset,
					ex ? r0.width : b.rec.width, b.rec.height);
		}

		// fill selected
		if (settings.getFillSelected()) {
			if (settings.getFillOnMove() && currentBox != null && stateMask == settings.getFillKeyModifierSWTInt()) {
				fillRectangle(settings.getFillSelectedColor(), gc, currentBox.rec.x - xOffset,
						currentBox.rec.y - yOffset, ex ? r0.width : currentBox.rec.width + 1,
						currentBox.rec.height + 1);
			} else if (fillBox != null) {
				fillRectangle(settings.getFillSelectedColor(), gc, fillBox.rec.x - xOffset, fillBox.rec.y - yOffset,
						ex ? r0.width : fillBox.rec.width + 1, fillBox.rec.height + 1);
			}
		}

		for (final Box b : visibleBoxes) {
			if (!b.isOn) {
				drawBox(gc, yOffset, xOffset, b, r0.width);
			}
		}

		for (final Box b : visibleBoxes) {
			if (b.isOn) {
				drawBox(gc, yOffset, xOffset, b, r0.width);
			}
		}

		final Image oldImage = boxText.getBackgroundImage();
		boxText.setBackgroundImage(newImage);
		if (oldImage != null) {
			oldImage.dispose();
		}
		gc.dispose();

		oldClientArea = r0;
		oldXOffset = xOffset;
		oldYOffset = yOffset;
	}

	/**
	 * Draw box.
	 *
	 * @param gc the gc
	 * @param yOffset the y offset
	 * @param xOffset the x offset
	 * @param b the b
	 * @param exWidth the ex width
	 */
	protected void drawBox(final GC gc, final int yOffset, final int xOffset, final Box b, final int exWidth) {
		drawRect(gc, b, b.rec.x - xOffset, b.rec.y - yOffset, settings.getExpandBox() ? exWidth : b.rec.width,
				b.rec.height);
	}

	/**
	 * Draw rect.
	 *
	 * @param gc the gc
	 * @param b the b
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	private void drawRect(final GC gc, final Box b, final int x, final int y, final int width, final int height) {
		if (b.isOn && settings.getHighlightWidth() > 0 && settings.getHighlightColor(b.level) != null) {
			gc.setLineStyle(settings.getHighlightLineStyleSWTInt());
			gc.setLineWidth(settings.getHighlightWidth());
			gc.setForeground(settings.getHighlightColor(b.level));
			if (settings.getHighlightDrawLine()) {
				gc.drawLine(x, y, x, y + b.rec.height);
			} else {
				// 3D
				// gc.drawLine(x-1, y+3, x-1, y + b.rec.height+1);
				// gc.drawLine(x-1, y + b.rec.height +1, x+b.rec.width-1, y +
				// b.rec.height +1);
				// gc.drawPoint(x, y+b.rec.height);
				drawRectangle(gc, x, y, width, height);
			}
		} else if (!b.isOn && settings.getBorderWidth() > 0 && settings.getBorderColor(b.level) != null) {
			gc.setLineStyle(settings.getBorderLineStyleSWTInt());
			gc.setLineWidth(settings.getBorderWidth());
			gc.setForeground(settings.getBorderColor(b.level));
			if (settings.getBorderDrawLine()) {
				gc.drawLine(x, y + 1, x, y + b.rec.height - 1);
			} else {
				drawRectangle(gc, x, y, width, height);
			}
		}
	}

	/**
	 * Draw rectangle.
	 *
	 * @param gc the gc
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	void drawRectangle(final GC gc, final int x, final int y, final int width, final int height) {
		if (settings.getRoundBox()) {
			gc.drawRoundRectangle(x, y, width, height, ROUND_BOX_ARC, ROUND_BOX_ARC);
		} else {
			gc.drawRectangle(x, y, width, height);
		}
	}

	/**
	 * Fill rectangle.
	 *
	 * @param c the c
	 * @param gc the gc
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	void fillRectangle(final Color c, final GC gc, final int x, final int y, final int width, final int height) {
		if (c == null) { return; }

		gc.setBackground(c);
		if (settings.getRoundBox()) {
			gc.fillRoundRectangle(x, y, width, height, ROUND_BOX_ARC, ROUND_BOX_ARC);
		} else {
			if (settings.getFillGradient() && settings.getFillGradientColor() != null) {
				gc.setBackground(settings.getFillGradientColor());
				gc.setForeground(c);
				gc.fillGradientRectangle(x, y, width, height, false);
			} else {
				gc.fillRectangle(x, y, width, height);
			}
		}
	}

	@Override
	public void decorate(final boolean mouseDbClickColorChange) {
		decorated = false;
		if (boxText == null || settings == null) { return; }

		boxPaint = new BoxPaintListener();
		boxMouseMove = new BoxMouseMoveListener();
		boxMouseTrack = new BoxMouseTrackListener();
		boxTextChange = new BoxTextChangeListener();
		fillMouseClick = new FillBoxMouseClick();
		boxKey = new BoxKeyListener();
		boxModify = new BoxModifyListener();

		if (mouseDbClickColorChange) {
			boxMouseClick = new BoxMouseClickListener();
		}

		final Color c = boxText.getBackground();
		if (c != null) {
			oldBackground = c.getRGB();
		}
		oldIndent = boxText.getIndent();
		if (oldIndent < 3) {
			boxText.setIndent(3);
		}
		boxText.addPaintListener(boxPaint);
		boxText.addMouseMoveListener(boxMouseMove);
		boxText.addMouseTrackListener(boxMouseTrack);
		boxText.getContent().addTextChangeListener(boxTextChange);
		boxText.addMouseListener(fillMouseClick);
		boxText.addModifyListener(boxModify);
		boxText.addKeyListener(boxKey);

		if (mouseDbClickColorChange) {
			boxText.addMouseListener(boxMouseClick);
		}

		decorated = true;
	}

	@Override
	public void undecorate() {
		if (boxText == null && !decorated) { return; }
		if (settingsChangeListener != null) {
			settings.removePropertyChangeListener(settingsChangeListener);
		}
		if (boxText == null || boxText.isDisposed()) { return; }
		decorated = false;
		if (boxMouseClick != null) {
			boxText.removeMouseListener(boxMouseClick);
		}
		if (boxTextChange != null) {
			boxText.getContent().removeTextChangeListener(boxTextChange);
		}
		if (boxMouseTrack != null) {
			boxText.removeMouseTrackListener(boxMouseTrack);
		}
		if (boxMouseMove != null) {
			boxText.removeMouseMoveListener(boxMouseMove);
		}
		if (boxPaint != null) {
			boxText.removePaintListener(boxPaint);
		}
		if (fillMouseClick != null) {
			boxText.removeMouseListener(fillMouseClick);
		}
		if (boxModify != null) {
			boxText.removeModifyListener(boxModify);
		}
		if (boxKey != null) {
			boxText.removeKeyListener(boxKey);
		}
		boxText.setIndent(oldIndent);
		boxText.setBackgroundImage(null);
		if (oldBackground != null) {
			boxText.setBackground(new Color(null, oldBackground));
		} else {
			boxText.setBackground(null);
		}

	}

	/**
	 * Visible boxes.
	 *
	 * @return the collection
	 */
	protected Collection<Box> visibleBoxes() {
		final Rectangle r0 = boxText.getClientArea();
		final int start = boxText.getHorizontalIndex() + boxText.getOffsetAtLine(boxText.getTopIndex());
		int end = boxText.getCharCount() - 1;
		final int lineIndex = boxText.getLineIndex(r0.height);
		if (lineIndex < boxText.getLineCount() - 1) {
			end = boxText.getOffsetAtLine(lineIndex);
		}

		final List<Box> result = new ArrayList<>();
		for (final Box b : boxes) {
			if (b.intersects(start, end)) {
				result.add(b);
			}
		}

		calcBounds(result);
		return result;
	}

	/**
	 * Calc bounds.
	 *
	 * @param boxes0 the boxes 0
	 */
	protected void calcBounds(final Collection<Box> boxes0) {
		final int yOffset = boxText.getTopPixel();
		final int xOffset = boxText.getHorizontalPixel();
		for (final Box b : boxes0) {
			if (b.rec == null) {
				final Point s = boxText.getLocationAtOffset(b.start);
				if (b.tabsStart > -1 && b.tabsStart != b.start) {
					final Point s1 = boxText.getLocationAtOffset(b.tabsStart);
					if (s1.x < s.x) {
						s.x = s1.x;
					}
				}
				final Point e = boxText.getLocationAtOffset(b.end);
				if (b.end != b.maxEndOffset) {
					final Point e1 = boxText.getLocationAtOffset(b.maxEndOffset);
					e.x = e1.x;
				}
				final Rectangle rec2 = new Rectangle(s.x + xOffset - 2, s.y + yOffset - 1, e.x - s.x + 6,
						e.y - s.y + boxText.getLineHeight(b.end));
				b.rec = rec2;
				updateWidth(b);
				updateWidth3(b);
			}
		}
	}

	/**
	 * Update width.
	 *
	 * @param box the box
	 */
	void updateWidth(final Box box) {
		Box b = box;
		Box p = b.parent;
		while (p != null && p.rec != null && p.rec.x + p.rec.width <= b.rec.x + b.rec.width) {
			p.rec.width += 5;
			b = p;
			p = p.parent;
		}
	}

	/**
	 * Update width 3.
	 *
	 * @param box the box
	 */
	void updateWidth3(final Box box) {
		Box b = box;
		Box p = b.parent;
		while (p != null && p.rec != null && p.rec.x >= b.rec.x) {
			p.rec.width += p.rec.x - b.rec.x + 3;
			p.rec.x = b.rec.x - 3 > 0 ? b.rec.x - 3 : 0;
			b = p;
			p = p.parent;
		}
	}

	/**
	 * Turn on box.
	 *
	 * @param x0 the x 0
	 * @param y0 the y 0
	 * @return true, if successful
	 */
	protected boolean turnOnBox(final int x0, final int y0) {
		if (boxes == null || !visible) { return false; }

		final int x = x0 + boxText.getHorizontalPixel();
		final int y = y0 + boxText.getTopPixel();

		return settings.getHighlightOne() ? turnOnOne(x, y) : turnOnAll(x, y);
	}

	/**
	 * Turn on all.
	 *
	 * @param x the x
	 * @param y the y
	 * @return true, if successful
	 */
	protected boolean turnOnAll(final int x, final int y) {
		boolean redraw = false;

		Box newCurrent = null;
		for (final Box b : visibleBoxes()) {
			if (contains(b.rec, x, y)) {
				if (!b.isOn) {
					b.isOn = true;
					redraw = true;
				}
				if (newCurrent == null || newCurrent.offset < b.offset) {
					newCurrent = b;
				}
			} else if (b.isOn) {
				b.isOn = false;
				redraw = true;
			}
		}
		if (!redraw) {
			redraw = newCurrent != currentBox;
		}
		currentBox = newCurrent;

		return redraw;
	}

	/**
	 * Turn on one.
	 *
	 * @param x the x
	 * @param y the y
	 * @return true, if successful
	 */
	protected boolean turnOnOne(final int x, final int y) {
		Box newCurrent = null;
		for (final Box b : visibleBoxes()) {
			if (contains(b.rec, x, y)) {
				newCurrent = b;
			}
			b.isOn = false;
		}
		if (newCurrent != null) {
			newCurrent.isOn = true;
		}
		final boolean redraw = newCurrent != currentBox;
		currentBox = newCurrent;
		return redraw;
	}

	/**
	 * Contains.
	 *
	 * @param rec the rec
	 * @param x the x
	 * @param y the y
	 * @return true, if successful
	 */
	boolean contains(final Rectangle rec, final int x, final int y) {
		return x >= rec.x && y >= rec.y && x - rec.x < rec.width && y - rec.y < rec.height;
	}

	/**
	 * Redraw if client area changed.
	 *
	 * @return true, if successful
	 */
	boolean redrawIfClientAreaChanged() {
		if (oldClientArea == null || !oldClientArea.equals(boxText.getClientArea())) {
			drawBackgroundBoxes();
			return true;
		}
		return false;
	}

	/**
	 * Update caret.
	 */
	void updateCaret() {
		oldCaretLoc = boxText.getLocationAtOffset(boxText.getCaretOffset());
		turnOnBox(oldCaretLoc.x > 0 ? oldCaretLoc.x - 1 : oldCaretLoc.x, oldCaretLoc.y);
	}

	/**
	 * Offset moved.
	 *
	 * @return true, if successful
	 */
	public boolean offsetMoved() {
		final int yOffset = boxText.getTopPixel();
		final int xOffset = boxText.getHorizontalPixel();
		if (xOffset != oldXOffset || yOffset != oldYOffset) {
			oldXOffset = xOffset;
			oldYOffset = yOffset;
			return true;
		}
		return false;
	}

	/**
	 * Carret moved.
	 */
	protected void carretMoved() {
		final Point newLoc = boxText.getLocationAtOffset(boxText.getCaretOffset());
		if (boxes != null && (oldCaretLoc == null || !oldCaretLoc.equals(newLoc))) {
			oldCaretLoc = newLoc;
			boolean build = false;
			if (!setCaretOffset && builder != null && builder.getCaretOffset() > -1
					&& builder.getCaretOffset() != boxText.getCaretOffset()) {
				buildBoxes();
				build = true;
			}
			if (turnOnBox(oldCaretLoc.x > 0 ? oldCaretLoc.x - 1 : oldCaretLoc.x, oldCaretLoc.y) || build) {
				drawBackgroundBoxes();
			}
		}
	}

	/**
	 * The listener interface for receiving boxModify events.
	 * The class that is interested in processing a boxModify
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addBoxModifyListener<code> method. When
	 * the boxModify event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see BoxModifyEvent
	 */
	private final class BoxModifyListener implements ModifyListener {

		/**
		 *
		 */
		public BoxModifyListener() {}

		@Override
		public void modifyText(final ModifyEvent e) {
			// it is more efficient to not draw boxes in PaintListner
			// (especially on Linux)
			// and in this event caret offset is correct
			if (boxes == null) {
				buildBoxes();
				updateCaret();
				drawBackgroundBoxes();
			}
		}
	}

	/**
	 * The listener interface for receiving boxKey events.
	 * The class that is interested in processing a boxKey
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addBoxKeyListener<code> method. When
	 * the boxKey event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see BoxKeyEvent
	 */
	private final class BoxKeyListener implements KeyListener {

		/**
		 *
		 */
		public BoxKeyListener() {}

		@Override
		public void keyReleased(final KeyEvent e) {
			keyPressed = true;
			carretMoved();
		}

		@Override
		public void keyPressed(final KeyEvent e) {}
	}

	/**
	 * The listener interface for receiving settingsChange events.
	 * The class that is interested in processing a settingsChange
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addSettingsChangeListener<code> method. When
	 * the settingsChange event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see SettingsChangeEvent
	 */
	class SettingsChangeListener implements IPropertyChangeListener {

		@Override
		public void propertyChange(final PropertyChangeEvent event) {
			update();
		}
	}

	/**
	 * The listener interface for receiving boxPaint events.
	 * The class that is interested in processing a boxPaint
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addBoxPaintListener<code> method. When
	 * the boxPaint event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see BoxPaintEvent
	 */
	class BoxPaintListener implements PaintListener {

		/** The paint mode. */
		volatile boolean paintMode;

		@Override
		public void paintControl(final PaintEvent e) {
			if (paintMode) { return; }
			paintMode = true;
			try {
				// check charCount as workaround for no event when
				// StyledText.setContent()
				if (boxes == null || charCount != boxText.getCharCount()) {
					buildBoxes();
					updateCaret();
					drawBackgroundBoxes();
				} else if (offsetMoved()) {
					updateCaret();
					drawBackgroundBoxes();
				} else {
					redrawIfClientAreaChanged();
				}
			} catch (final Throwable t) {
				// EditBox.logError(this, "Box paint error", t);
			} finally {
				paintMode = false;
			}
		}
	}

	/**
	 * The listener interface for receiving boxMouseMove events.
	 * The class that is interested in processing a boxMouseMove
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addBoxMouseMoveListener<code> method. When
	 * the boxMouseMove event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see BoxMouseMoveEvent
	 */
	class BoxMouseMoveListener implements MouseMoveListener {

		@Override
		public void mouseMove(final MouseEvent e) {
			stateMask = e.stateMask;
			if (turnOnBox(e.x, e.y)) {
				drawBackgroundBoxes();
			}
		}
	}

	/**
	 * The listener interface for receiving boxMouseTrack events.
	 * The class that is interested in processing a boxMouseTrack
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addBoxMouseTrackListener<code> method. When
	 * the boxMouseTrack event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see BoxMouseTrackEvent
	 */
	class BoxMouseTrackListener implements MouseTrackListener {

		@Override
		public void mouseEnter(final MouseEvent e) {}

		@Override
		public void mouseExit(final MouseEvent e) {
			boolean redraw = false;
			if (boxes != null) {
				for (final Box b : boxes) {
					if (b.isOn) {
						redraw = true;
						b.isOn = false;
					}
				}
			}
			if (redraw) {
				drawBackgroundBoxes();
			}
		}

		@Override
		public void mouseHover(final MouseEvent e) {}
	}

	/**
	 * The listener interface for receiving boxTextChange events.
	 * The class that is interested in processing a boxTextChange
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addBoxTextChangeListener<code> method. When
	 * the boxTextChange event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see BoxTextChangeEvent
	 */
	class BoxTextChangeListener implements TextChangeListener {

		/**
		 * Change.
		 */
		private void change() {
			boxes = null;
			setCaretOffset = true;
		}

		@Override
		public void textChanged(final TextChangedEvent event) {
			change();
		}

		@Override
		public void textChanging(final TextChangingEvent event) {}

		@Override
		public void textSet(final TextChangedEvent event) {
			change();
		}
	}

	/**
	 * The listener interface for receiving boxMouseClick events.
	 * The class that is interested in processing a boxMouseClick
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addBoxMouseClickListener<code> method. When
	 * the boxMouseClick event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see BoxMouseClickEvent
	 */
	class BoxMouseClickListener extends MouseAdapter {

		@Override
		public void mouseDoubleClick(final MouseEvent e) {
			final int x = e.x + boxText.getHorizontalPixel();
			final int y = e.y + boxText.getTopPixel();

			int level = -1;
			for (final Box b : visibleBoxes()) {
				if (contains(b.rec, x, y)) {
					if (level < b.level) {
						level = b.level;
					}
				}
			}
			level++;

			final ColorDialog colorDialog = new ColorDialog(boxText.getShell());
			final Color oldColor1 = settings.getColor(level);
			if (oldColor1 != null) {
				colorDialog.setRGB(oldColor1.getRGB());
			}

			settings.setColor(level, colorDialog.open());
		}

	}

	/**
	 * The Class FillBoxMouseClick.
	 */
	class FillBoxMouseClick extends MouseAdapter {

		@Override
		public void mouseDown(final MouseEvent e) {

			if (e.button != 1 || settings.getFillOnMove() || e.stateMask != settings.getFillKeyModifierSWTInt()) {
				if (keyPressed) {
					keyPressed = false;
					carretMoved();
				}
				return;
			}

			final int x = e.x + boxText.getHorizontalPixel();
			final int y = e.y + boxText.getTopPixel();

			Box fillBox = null;
			for (final Box b : visibleBoxes()) {
				if (contains(b.rec, x, y)) {
					fillBox = b;
				}
			}

			if (fillBox != null
					&& (fillBox.end != fillBoxEnd || fillBox.start != fillBoxStart || fillBox.level != fillBoxLevel)) {
				fillBoxEnd = fillBox.end;
				fillBoxLevel = fillBox.level;
				fillBoxStart = fillBox.start;
			} else {
				fillBoxEnd = -1;
				fillBoxStart = -1;
				fillBoxLevel = -1;
			}

			if (keyPressed) {
				keyPressed = false;
				final Point newLoc = boxText.getLocationAtOffset(boxText.getCaretOffset());
				if (oldCaretLoc == null || !oldCaretLoc.equals(newLoc)) {
					buildBoxes();
					oldCaretLoc = newLoc;
				}
			}

			drawBackgroundBoxes();
		}
	}

	// @Override
	// public void selectCurrentBox() {
	// if ( decorated && visible && boxes != null ) {
	// Box b = null;
	// Point p = boxText.getSelection();
	// if ( p == null || p.x == p.y ) {
	// b = currentBox;
	// } else {
	// for ( Box box : boxes ) {
	// if ( p.x <= box.start && p.y >= box.end - 1 ) {
	// b = box.parent;
	// break;
	// }
	// }
	// }
	// if ( b != null ) {
	// int end = Character.isWhitespace(boxText.getText(b.end - 1, b.end -
	// 1).charAt(0)) ? b.end - 1 : b.end;
	// boxText.setSelection(b.start, end);
	// Event event = new Event();
	// event.x = b.start;
	// event.y = end;
	// boxText.notifyListeners(SWT.Selection, event);
	// }
	// }
	// }
	//
	// @Override
	// public void unselectCurrentBox() {
	// boxText.setSelection(boxText.getCaretOffset());
	// }

}
