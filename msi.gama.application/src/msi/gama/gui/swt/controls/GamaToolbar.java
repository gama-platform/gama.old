/**
 * Created by drogoul, 3 déc. 2014
 * 
 */
package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.*;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Class GamaToolbar. A declarative wrapper around toolbars
 * 
 * @author drogoul
 * @since 3 déc. 2014
 * 
 */
public class GamaToolbar extends ToolBar {

	public static class SizerToolItem extends ToolItem {

		public SizerToolItem(final ToolBar parent, final int height) {
			super(parent, SWT.FLAT);
			setImage(GamaIcons.createSizer(height).image());
			setEnabled(false);
		}

		@Override
		protected void checkSubclass() {}

		@Override
		public void dispose() {
			Image sizerImage = getImage();
			if ( sizerImage != null && !sizerImage.isDisposed() ) {
				// sizerItem.setImage(null);
				sizerImage.dispose();
			}
			super.dispose();
		}

	}

	SizerToolItem sizerItem;
	ControlListener widthListener;

	public GamaToolbar(final Composite parent, final int style) {
		super(parent, style);
	}

	@Override
	public void dispose() {
		super.dispose();
		disposeSizer();
	}

	private void disposeSizer() {
		if ( sizerItem != null && !sizerItem.isDisposed() ) {
			sizerItem.dispose();
			sizerItem = null;
		}

	}

	@Override
	protected void checkSubclass() {}

	public GamaToolbar color(final Color c) {
		setBackground(c);
		return this;
	}

	public ToolItem sep() {
		return create(null, null, null, null, SWT.SEPARATOR, false);
	}

	public GamaToolbar width(final Control parent) {
		if ( widthListener != null ) {
			removeControlListener(widthListener);
		}
		widthListener = new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				Rectangle r = getBounds();
				r.width = parent.getBounds().width;
				setBounds(r);
			}

		};
		addControlListener(widthListener);
		return this;
	}

	public ToolItem sep(final int n) {
		ToolItem item = control(new Label(this, SWT.NONE), n);
		item.getControl().setVisible(false);
		return item;
	}

	public ToolItem label(final String s) {
		ToolItem label = create(null, s, null, null, SWT.NONE, true);
		label.setEnabled(false);
		return label;
	}

	public ToolItem status(final String image, final String s, final GamaUIColor color) {
		return status(GamaIcons.create(image).image(), s, color);
	}

	public ToolItem status(final Image image, final String s, final GamaUIColor color) {
		wipe();
		FlatButton button;
		if ( image == null ) {
			button = FlatButton.label(this, color, s);
		} else {
			button = FlatButton.label(this, color, s, image);
		}
		button.light();
		ToolItem item = button.item();
		refresh();
		return item;
	}

	public ToolItem tooltip(final String s, final GamaUIColor color, final int width) {
		if ( s == null ) { return null; }
		final Composite c = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		c.setLayout(layout);
		c.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				if ( e.width == 0 || e.height == 0 ) { return; }
				GC gc = e.gc;
				gc.setAntialias(SWT.ON);
				gc.setBackground(IGamaColors.WHITE.color());
				gc.fillRectangle(e.x, e.y, e.width, e.height);
				gc.setBackground(color.inactive());
				gc.fillRoundRectangle(e.x + 4, e.y + 4, e.width - 8, e.height - 8, 5, 5);
			}
		});

		Label label = new Label(c, SWT.WRAP);
		label.setForeground(color.isDark() ? IGamaColors.WHITE.color() : IGamaColors.BLACK.color());
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		label.setLayoutData(data);
		label.setText(s);
		// label.setSize(width - 4, this.getBounds().height);
		return control(c, /* c.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10 */width);
	}

	public ToolItem check(final String image, final String text, final String tip, final SelectionListener listener) {
		return create(image, text, tip, listener, SWT.CHECK, false);
	}

	public ToolItem button(final String image, final String text, final String tip, final SelectionListener listener) {
		return create(image, text, tip, listener, SWT.PUSH, false);
	}

	public ToolItem menu(final String image, final String text, final String tip, final SelectionListener listener) {
		return create(image, text, tip, listener, SWT.DROP_DOWN, false);
	}

	public ToolItem control(final Control c, final int width) {
		final ToolItem control = create(null, null, null, null, SWT.SEPARATOR, false);
		control.setControl(c);
		if ( width == SWT.DEFAULT ) {
			control.setWidth(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
		} else {
			control.setWidth(width);
		}
		return control;
	}

	@Override
	public void layout() {
		super.layout();
	}

	public void refresh() {
		getParent().layout(true, true);
		// update();
	}

	public void wipe() {
		// Removes everything excluding the sizer item (if any)
		for ( ToolItem t : getItems() ) {
			if ( t != sizerItem ) {
				Control c = t.getControl();
				if ( c != null ) {
					c.dispose();
				}
				t.dispose();
			}
		}
	}

	public GamaToolbar height(final int n) {
		// height = n;
		// if ( sizerItem != null && !sizerItem.isDisposed() && sizerItem.getImage() != null &&
		// sizerItem.getImage().getBounds().height == n ) { return this; }
		// disposeSizer();
		// sizerItem = new SizerToolItem(this, n);
		return this;
	}

	public static void setHeight(final ToolBar bar, final int n) {
		int height = n == SWT.DEFAULT ? GamaToolbarFactory.TOOLBAR_HEIGHT : n;
		Image image = GamaIcons.create("editor.sizer2").image();
		Image sizerImage = new Image(bar.getDisplay(), image.getImageData().scaledTo(1, height));
		final ToolItem sizerItem = new SizerToolItem(bar, n);
	}

	private ToolItem create(final String i, final String text, final String tip, final SelectionListener listener,
		final int style, final boolean forceText) {
		ToolItem button;
		if ( sizerItem == null ) {
			button = new ToolItem(this, style, getItems().length);
		} else {
			button = new ToolItem(this, style, getItems().length - 1);
		}
		if ( text != null && forceText ) {
			button.setText(text);
		}
		if ( tip != null ) {
			button.setToolTipText(tip);
		}
		if ( i != null ) {
			Image image = GamaIcons.create(i).image();
			button.setImage(image);
		}
		if ( listener != null ) {
			button.addSelectionListener(listener);
		}
		return button;
	}

}
