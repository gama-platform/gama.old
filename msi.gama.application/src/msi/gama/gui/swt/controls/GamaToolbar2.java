/**
 * Created by drogoul, 3 déc. 2014
 * 
 */
package msi.gama.gui.swt.controls;

import java.io.*;
import java.util.ArrayList;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.*;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Class GamaToolbar. A declarative wrapper around 2 toolbars (left, right).
 * 
 * @author drogoul
 * @since 3 déc. 2014
 * 
 */
public class GamaToolbar2 extends Composite {

	private GamaToolbarSimple left, right;

	private class SizerToolItem extends ToolItem {

		public SizerToolItem(final GamaToolbarSimple tb, final int width, final int height) {
			super(tb, SWT.FLAT);
			setImage(GamaIcons.createSizer(GamaToolbar2.this.getBackground(), width, height).image());
			// setEnabled(false);
		}

		@Override
		protected void checkSubclass() {}

	}

	final SizerToolItem iconSizer, toolbarSizer;
	final int height;

	public GamaToolbar2(final Composite parent, final int style, final int height) {
		super(parent, SWT.NONE);
		this.height = height;
		createLayout();
		createToolbars();
		int square = GamaIcons.CORE_ICONS_HEIGHT.getValue();
		iconSizer = null;
		// iconSizer = new SizerToolItem(right, square, square);
		toolbarSizer = new SizerToolItem(left, 1, height);
	}

	public void createLayout() {
		setBackground(IGamaColors.WHITE.color());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 5;
		layout.marginHeight = 0;
		setLayout(layout);
	}

	public void createToolbars() {
		left = new GamaToolbarSimple(this, SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP | SWT.NO_FOCUS);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.verticalIndent = 0;
		data.horizontalAlignment = SWT.LEFT;
		data.minimumWidth = GamaToolbarFactory.TOOLBAR_HEIGHT * 2;
		left.setLayoutData(data);

		right = new GamaToolbarSimple(this, SWT.FLAT | SWT.HORIZONTAL | SWT.NO_FOCUS);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.verticalIndent = 0;
		data.horizontalAlignment = SWT.RIGHT;
		data.minimumWidth = GamaToolbarFactory.TOOLBAR_HEIGHT * 2;
		right.setLayoutData(data);

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
		GamaIcon icon = GamaIcons.createSizer(getBackground(), n, height);
		ToolItem item = create(icon.getCode(), null, null, null, SWT.NONE, false, null, side);
		// item.getControl().setVisible(false);
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
		ToolItem item = button(color, s, image, side);
		refresh(true);
		return item;
	}

	public ToolItem tooltip(final String s, final GamaUIColor color, final int side /* SWT.LEFT or SWT.RIGHT */) {
		if ( s == null ) { return null; }
		final GamaToolbarSimple tb = side == SWT.LEFT ? left : right;
		final GamaToolbarSimple other = side == SWT.LEFT ? right : left;
		int width = getSize().x - other.getSize().x - 30;
		wipe(side);
		Label label = new Label(tb, SWT.WRAP);
		label.setForeground(color.isDark() ? IGamaColors.WHITE.color() : IGamaColors.BLACK.color());
		String newString = "";
		java.util.List<String> result = new ArrayList();
		try {
			BufferedReader reader = new BufferedReader(new StringReader(s));
			String line = reader.readLine();
			while (line != null) {
				newString += line;
				line = reader.readLine();
			}
		} catch (IOException exc) {}
		label.setText(newString);
		label.setFont(SwtGui.getSmallFont());
		label.setBackground(color.inactive());
		ToolItem t = control(label, /* c.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10 */width, side);
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

	public ToolItem button(final GamaUIColor color, final String text, final Image image, final int side) {
		FlatButton button = FlatButton.button(side == SWT.LEFT ? left : right, color, text, image);
		return control(button, button.computeSize(SWT.DEFAULT, button.getHeight(), false).x + 4, side);
	}

	public ToolItem label(final GamaUIColor color, final String text, final Image image, final int side) {
		FlatButton button = FlatButton.label(side == SWT.LEFT ? left : right, color, text, image);
		return control(button, button.computeSize(SWT.DEFAULT, button.getHeight(), false).x + 4, side);
	}

	public ToolItem menu(final GamaUIColor color, final String text, final int side) {
		FlatButton button = FlatButton.menu(side == SWT.LEFT ? left : right, color, text);
		return control(button, button.computeSize(SWT.DEFAULT, button.getHeight(), false).x + 4, side);
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
		layout(true, true);
	}

	public void wipe(final int side /* SWT.LEFT or SWT.RIGHT */) {
		// Removes everything excluding the sizer item and the separator, after or before the separator depending on 'side'
		if ( side == SWT.LEFT ) {
			for ( ToolItem t : left.getItems() ) {
				if ( t == iconSizer ) {
					break;
				}
				Control c = t.getControl();
				if ( c != null ) {
					c.dispose();
				}
				t.dispose();
			}
		} else {
			for ( ToolItem t : right.getItems() ) {
				if ( t != toolbarSizer ) {
					Control c = t.getControl();
					if ( c != null ) {
						c.dispose();
					}
					t.dispose();
				}
			}
		}
	}

	public GamaToolbar2 height(final int n) {
		return this; // fixed height
	}

	public void item(final IContributionItem item, final int side) {
		if ( side == SWT.LEFT ) {
			item.fill(left, left.getItemCount());
		} else {
			item.fill(right, right.getItemCount());
		}
	}

	private ToolItem create(final String i, final String text, final String tip, final SelectionListener listener,
		final int style, final boolean forceText, final Control control, final int side /* SWT.LEFT or SWT.RIGHT */) {
		ToolItem button;
		GamaToolbarSimple tb;
		if ( side == SWT.LEFT ) {
			button = new ToolItem(left, style);
		} else {
			button = new ToolItem(right, style);
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
		return button;
	}

	/**
	 * @param right2
	 * @return
	 */
	public GamaToolbarSimple getToolbar(final int side) {
		return side == SWT.LEFT ? left : right;
	}

}
