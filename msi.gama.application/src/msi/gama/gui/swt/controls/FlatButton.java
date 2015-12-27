/**
 * Created by drogoul, 22 nov. 2014
 *
 */
package msi.gama.gui.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;

public class FlatButton extends Canvas implements PaintListener, Listener {

	public static FlatButton create(final Composite comp, final int style) {
		return new FlatButton(comp, style);
	}

	public static FlatButton label(final Composite comp, final GamaUIColor color, final String text) {
		return button(comp, color, text).disabled();
	}

	public static FlatButton label(final Composite comp, final GamaUIColor color, final String text,
		final Image image) {
		return label(comp, color, text).setImage(image);
	}

	public static FlatButton button(final Composite comp, final GamaUIColor color, final String text) {
		return create(comp, SWT.None).setText(text).setColor(color);
	}

	public static FlatButton button(final Composite comp, final GamaUIColor color, final String text,
		final Image image) {
		return button(comp, color, text).setImage(image);
	}

	public static FlatButton menu(final Composite comp, final GamaUIColor color, final String text) {
		return button(comp, color, text).setImageStyle(IMAGE_RIGHT)
			.setImage(GamaIcons.create("small.dropdown").image());
	}

	private static int FIXED_HEIGHT = 20;

	private int height = FIXED_HEIGHT;
	private Image image;
	private String text;
	private RGB colorCode;
	private static final int innerMarginWidth = 5;
	private static final int imagePadding = 5;
	private boolean enabled = true;
	private boolean hovered = false;

	public static int IMAGE_LEFT = 0;
	public static int IMAGE_RIGHT = 1;
	private int imageStyle = IMAGE_LEFT;

	private FlatButton(final Composite parent, final int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		setFont(SwtGui.getLabelfont());
		addPaintListener(this);
		addListeners();
	}

	@Override
	public void handleEvent(final Event e) {
		switch (e.type) {
			case SWT.MouseExit:
				doHover(false);
				break;
			case SWT.MouseEnter:
			case SWT.MouseHover:
				doHover(true);
				e.doit = true;
				break;
			case SWT.MouseUp:
				if ( e.button == 1 && e.count == 1 && getClientArea().contains(e.x, e.y) ) {
					doButtonClicked();
				}
				break;
			case SWT.MouseDown:
				if ( e.button == 1 ) {
					doHover(true);
				}
				break;
			default:;
		}
	}

	/**
	 * SelectionListeners are notified when the button is clicked
	 *
	 * @param listener
	 */
	public void addSelectionListener(final SelectionListener listener) {
		addListener(SWT.Selection, new TypedListener(listener));
	}

	public void removeSelectionListener(final SelectionListener listener) {
		removeListener(SWT.Selection, listener);
	}

	private void doButtonClicked() {
		if ( !enabled ) { return; }
		Event e = new Event();
		e.item = this;
		e.widget = this;
		e.type = SWT.Selection;
		notifyListeners(SWT.Selection, e);
	}

	private void doHover(final boolean hover) {
		// if ( hover && hovered || !hover && !hovered ) { return; }
		hovered = hover;
		redraw();
	}

	private void drawBackground(final GC gc, final Rectangle rect) {
		setBackground(getParent().getBackground());
		// gc.setBackground(getParent().getBackground());
		// gc.fillRectangle(getBounds());

		final Path path = createClipping(rect);
		GamaUIColor color = GamaColors.get(colorCode);
		Color background = hovered ? color.lighter() : color.color();
		Color foreground = GamaColors.isDark(background) ? IGamaColors.WHITE.color() : IGamaColors.BLACK.color();

		gc.setClipping(path);
		gc.setForeground(foreground);
		gc.setBackground(background);
		gc.fillRectangle(rect);
		gc.setClipping((Rectangle) null);
		path.dispose();
	}

	private Path createClipping(final Rectangle rect) {
		final AdvancedPath path = new AdvancedPath(SwtGui.getDisplay());
		path.addRoundRectangle(rect.x, rect.y, rect.width, rect.height, 8, 8);
		return path;
	}

	@Override
	public void paintControl(final PaintEvent e) {
		// Init GC
		GC gc = e.gc;
		gc.setAntialias(SWT.ON);
		gc.setAdvanced(true);
		gc.setFont(getFont());
		int width = getSize().x;
		int v_inset = (getBounds().height - height) / 2;
		// width = Math.min(width, getParent().getBounds().width);
		Rectangle rect = new Rectangle(0, v_inset, width, height);
		drawBackground(gc, rect);

		int x = FlatButton.innerMarginWidth;
		int y_image = 0;
		Image image = getImage();
		if ( image != null ) {
			y_image += (getBounds().height - image.getBounds().height) / 2;
		}
		int y_text = 0;
		String text = newText();
		if ( text != null ) {
			y_text += (getBounds().height - gc.textExtent(text).y) / 2;
		}

		if ( imageStyle == IMAGE_RIGHT ) {
			gc.drawText(text, x, y_text, SWT.DRAW_TRANSPARENT);
			if ( image != null ) {
				x = rect.width - x - image.getBounds().width;
				drawImage(gc, x, y_image);
			}
		} else {
			x = drawImage(gc, x, y_image);
			gc.drawText(text, x, y_text, SWT.DRAW_TRANSPARENT);
		}
	}

	private int drawImage(final GC gc, final int x, final int y) {
		if ( image == null ) { return x; }
		gc.drawImage(image, x, y);
		return x + image.getBounds().width + imagePadding;
	}

	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		int width = 0;
		if ( wHint != SWT.DEFAULT ) {
			width = wHint;
		} else {
			width = computeMinWidth();
		}
		// int clientWidth = getClientArea().width;
		Point result = new Point(width, height);
		// System.out.println(text + ": wHint " + wHint + "; client area " + getClientArea() + "; result " + result);
		return result;
	}

	public int computeMinWidth() {
		int width = 0;
		Image image = getImage();
		if ( image != null ) {
			Rectangle bounds = image.getBounds();
			width = bounds.width + imagePadding * 2;
		}
		if ( text != null ) {
			GC gc = new GC(this);
			gc.setFont(getFont());
			Point extent = gc.textExtent(text);
			gc.dispose();
			width += extent.x + FlatButton.innerMarginWidth;
		}
		return width;
	}

	public String newText() {
		if ( text == null ) { return null; }
		int parentWidth = getParent().getBounds().width;
		int width = computeMinWidth();
		if ( parentWidth < width ) {
			int imageWidth = 0;
			Image image = getImage();
			if ( image != null ) {
				Rectangle bounds = image.getBounds();
				imageWidth = bounds.width + imagePadding;
			}
			float r = (float) (parentWidth - imageWidth) / (float) width;
			int nbChars = text.length();
			int newNbChars = Math.max(0, (int) (nbChars * r));
			String newText =
				text.substring(0, newNbChars / 2) + "..." + text.substring(nbChars - newNbChars / 2, nbChars);
			// System.out.println("Parent width =" + parentWidth + "; new nb chars = " + newNbChars + "; new text = " +
			// newText);
			return newText;
		}
		return text;
	}

	/**
	 * This is an image that will be displayed to the side of the
	 * text inside the button (if any). By default the image will be
	 * to the left of the text; however, setImageStyle can be used to
	 * specify that it's either to the right or left. If there is no
	 * text, the image will be centered inside the button.
	 *
	 * @param image
	 */
	public FlatButton setImage(final Image image) {
		this.image = image;
		redraw();
		return this;
	}

	/**
	 * Set the style with which the side image is drawn, either IMAGE_LEFT
	 * or IMAGE_RIGHT (default is IMAGE_LEFT).
	 *
	 * @param imageStyle
	 */
	public FlatButton setImageStyle(final int imageStyle) {
		this.imageStyle = imageStyle;
		return this;
	}

	public int getImageStyle() {
		return imageStyle;
	}

	public String getText() {
		return text;
	}

	public FlatButton setText(final String text) {
		if ( text == null ) { return this; }
		if ( text.equals(this.text) ) { return this; }
		this.text = text;
		redraw();
		return this;
	}

	private void addListeners() {
		addListener(SWT.MouseDown, this);
		addListener(SWT.MouseExit, this);
		addListener(SWT.MouseEnter, this);
		addListener(SWT.MouseHover, this);
		addListener(SWT.MouseUp, this);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		boolean oldSetting = this.enabled;
		this.enabled = enabled;
		// boolean oldSetting = super.getEnabled();
		// super.setEnabled(enabled);
		if ( oldSetting != enabled ) {
			if ( enabled ) {
				addListeners();
			} else {
				removeListener(SWT.MouseDown, (Listener) this);
				removeListener(SWT.MouseExit, (Listener) this);
				removeListener(SWT.MouseEnter, (Listener) this);
				removeListener(SWT.MouseHover, (Listener) this);
				removeListener(SWT.MouseUp, (Listener) this);
			}
			redraw();
		}
	}

	public ToolItem item() {
		if ( getParent() instanceof GamaToolbarSimple ) {
			GamaToolbarSimple p = (GamaToolbarSimple) getParent();
			return p.control(this, computeSize(SWT.DEFAULT, height, false).x + 4);
		}
		ToolItem t = new ToolItem((ToolBar) getParent(), SWT.SEPARATOR);
		int width = this.computeSize(SWT.DEFAULT, height, false).x + 4;
		t.setControl(this);
		t.setWidth(width);
		return t;
	}

	public FlatButton disabled() {
		setEnabled(false);
		return this;
	}

	public FlatButton enabled() {
		setEnabled(true);
		return this;
	}

	public FlatButton light() {
		// if ( getFont().equals(SwtGui.getParameterEditorsFont()) ) { return this; }
		// setFont(SwtGui.getParameterEditorsFont());
		// redraw();
		return this;
	}

	public FlatButton small() {
		if ( height == 20 ) { return this; }
		height = 20;
		redraw();
		return this;
	}

	public FlatButton setColor(final GamaUIColor c) {
		RGB oldColorCode = colorCode;
		RGB newColorCode = c.getRGB();
		if ( newColorCode.equals(oldColorCode) ) { return this; }
		colorCode = c.getRGB();
		redraw();
		return this;
	}

	public int getHeight() {
		return height;
	}

	public GamaUIColor getColor() {
		return GamaColors.get(colorCode);
	}

	public Image getImage() {
		return image;
	}
}