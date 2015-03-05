/**
 * Created by drogoul, 3 déc. 2014
 * 
 */
package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.*;
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
public class GamaToolbar2 extends ToolBar {

	private class SizerToolItem extends ToolItem {

		public SizerToolItem(final int width, final int height) {
			super(GamaToolbar2.this, SWT.FLAT);
			setImage(GamaIcons.createSizer(GamaToolbar2.this.getBackground(), width, height).image());
			// setEnabled(false);
		}

		@Override
		protected void checkSubclass() {}

	}

	private class SeparatorToolItem extends ToolItem implements ControlListener {

		Control c;

		public SeparatorToolItem() {
			super(GamaToolbar2.this, SWT.FLAT | SWT.SEPARATOR);
			c = new Label(GamaToolbar2.this, SWT.None);
			setControl(c);
			c.setVisible(false);
			setWidth(4);
			// setEnabled(false);
		}

		@Override
		protected void checkSubclass() {}

		@Override
		public void controlMoved(final ControlEvent e) {}

		@Override
		public void controlResized(final ControlEvent e) {
			int w = computeRemainingWidth();
			if ( w < 0 ) {
				setWidth(1);
				return;
			}
			setWidth(w);
			refresh(false);
		}

	}

	final SeparatorToolItem separator;
	final SizerToolItem sizer;

	public GamaToolbar2(final Composite parent, final int style, final int height) {
		super(parent, style); // No wrapping allowed
		setBackground(IGamaColors.WHITE.color());
		sizer = new SizerToolItem(height, height);
		separator = new SeparatorToolItem();
		// addControlListener(separator);
		parent.addControlListener(separator);
	}

	public int computeRemainingWidth() {
		// layout(true, true);
		int width = getParent().getClientArea().width;
		// System.out.println("Available width : " + width + ";  number of items: " + getItemCount());
		width -= 8; // right margin
		// java.util.Map<ToolItem, Integer> widths = new LinkedHashMap();
		for ( int i = 0; i < getItemCount(); i++ ) {
			ToolItem item = getItem(i);
			if ( item != separator ) {
				// widths.put(item, item.getBounds().width);
				width -= item.getBounds().width;
			}
		}
		// for ( ToolItem item : this.getItems() ) {
		// if ( item != separator ) {
		// widths.put(item.getClass().getSimpleName(), item.getWidth());
		// width -= item.getWidth();
		// }
		// }
		// System.out.println("Widths of items :" + widths);
		// System.out.println("Remaining width : " + width);
		return width;
	}

	@Override
	protected void checkSubclass() {}

	public ToolItem sep(final int side) {
		return create(null, null, null, null, SWT.SEPARATOR, false, null, side);
	}

	public GamaToolbar2 width(final Control parent) {
		ControlListener widthListener = new ControlAdapter() {

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

	public ToolItem sep(final int n, final int side /* SWT.LEFT or SWT.RIGHT */) {
		ToolItem item = control(new Label(this, SWT.NONE), n, side);
		item.getControl().setVisible(false);
		return item;
	}

	public ToolItem label(final String s, final int side /* SWT.LEFT or SWT.RIGHT */) {
		ToolItem label = create(null, s, null, null, SWT.NONE, true, null, side);
		label.setEnabled(false);
		return label;
	}

	public
		ToolItem
		status(final String image, final String s, final GamaUIColor color, final int side /* SWT.LEFT or SWT.RIGHT */) {
		return status(GamaIcons.create(image).image(), s, color, side);
	}

	public ToolItem
		status(final Image image, final String s, final GamaUIColor color, final int side /* SWT.LEFT or SWT.RIGHT */) {
		wipe(side);
		FlatButton button;
		if ( image == null ) {
			button = FlatButton.label(this, color, s);
		} else {
			button = FlatButton.label(this, color, s, image);
		}
		button.light();
		ToolItem item = button.item(side);
		refresh(true);
		return item;
	}

	public ToolItem
		tooltip(final String s, final GamaUIColor color, final int width, final int side /* SWT.LEFT or SWT.RIGHT */) {
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
		ToolItem t = control(c, /* c.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10 */width, side);
		refresh(true);
		return t;
	}

	public ToolItem check(final String image, final String text, final String tip, final SelectionListener listener,
		final int side /* SWT.LEFT or SWT.RIGHT */) {
		return create(image, text, tip, listener, SWT.CHECK, false, null, side);
	}

	public ToolItem button(final String image, final String text, final String tip, final SelectionListener listener,
		final int side /* SWT.LEFT or SWT.RIGHT */) {
		return create(image, text, tip, listener, SWT.PUSH, false, null, side);
	}

	public ToolItem menu(final String image, final String text, final String tip, final SelectionListener listener,
		final int side /* SWT.LEFT or SWT.RIGHT */) {
		return create(image, text, tip, listener, SWT.DROP_DOWN, false, null, side);
	}

	public ToolItem control(final Control c, final int width, final int side /* SWT.LEFT or SWT.RIGHT */) {
		final ToolItem control = create(null, null, null, null, SWT.SEPARATOR, false, c, side);
		// control.setControl(c);
		if ( width == SWT.DEFAULT ) {
			control.setWidth(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
		} else {
			control.setWidth(width);
		}
		return control;
	}

	public void refresh(final boolean layout) {

		if ( layout ) {
			separator.controlResized(null);
		}
		// layout(true, true);
		// getParent().layout(true, true);
		getParent().layout(true, true);
		layout(true, false);
		update();
	}

	public void wipe(final int side /* SWT.LEFT or SWT.RIGHT */) {
		// Removes everything excluding the sizer item and the separator, after or before the separator depending on 'side'
		if ( side == SWT.LEFT ) {
			for ( ToolItem t : getItems() ) {
				if ( t == sizer ) {
					break;
				}
				Control c = t.getControl();
				if ( c != null ) {
					c.dispose();
				}
				t.dispose();
			}
		} else {
			boolean afterSeparator = false;
			for ( ToolItem t : getItems() ) {
				if ( t == separator ) {
					afterSeparator = true;
				} else if ( afterSeparator ) {
					Control c = t.getControl();
					if ( c != null ) {
						c.dispose();
					}
					t.dispose();
				}
			}
		}
		separator.controlResized(null);
	}

	public GamaToolbar2 height(final int n) {
		return this; // fixed height
	}

	private ToolItem create(final String i, final String text, final String tip, final SelectionListener listener,
		final int style, final boolean forceText, final Control control, final int side /* SWT.LEFT or SWT.RIGHT */) {
		ToolItem button;
		if ( side == SWT.LEFT ) {
			button = new ToolItem(this, style, indexOf(sizer));
		} else {
			button = new ToolItem(this, style);
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
		if ( control != null ) {
			button.setControl(control);
		}
		// System.out.println("Item created (index " +
		// indexOf(button) +
		// ") : " +
		// (text == null || text.isEmpty() ? tip == null ? control != null ? control.toString() : button.getClass()
		// .getSimpleName() : tip : text) + " on the " + (side == SWT.LEFT ? "left" : "right"));
		return button;
	}

}
