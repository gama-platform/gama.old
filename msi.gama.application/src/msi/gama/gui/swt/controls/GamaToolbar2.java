/**
 * Created by drogoul, 3 déc. 2014
 *
 */
package msi.gama.gui.swt.controls;

import java.io.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.*;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gaml.operators.Strings;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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
	private boolean hasTooltip;
	final int height;

	public GamaToolbar2(final Composite parent, final int style, final int height) {
		super(parent, SWT.NONE);
		this.height = height;
		createLayout();
		createToolbars();
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
		prepareToolbar(SWT.LEFT);

		right = new GamaToolbarSimple(this, SWT.FLAT | SWT.HORIZONTAL | SWT.NO_FOCUS);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.verticalIndent = 0;
		data.horizontalAlignment = SWT.RIGHT;
		data.minimumWidth = GamaToolbarFactory.TOOLBAR_HEIGHT * 2;
		right.setLayoutData(data);
		prepareToolbar(SWT.RIGHT);

	}

	@Override
	protected void checkSubclass() {}

	public ToolItem sep(final int n, final int side /* SWT.LEFT or SWT.RIGHT */) {
		GamaIcon icon = GamaIcons.createSizer(getBackground(), n, height);
		ToolItem item = create(icon.getCode(), null, null, null, SWT.NONE, false, null, side);
		item.setDisabledImage(icon.image());
		item.setEnabled(false);
		return item;
	}

	public
	ToolItem
	status(final String image, final String s, final GamaUIColor color, final int side /* SWT.LEFT or SWT.RIGHT */) {
		return status(GamaIcons.create(image).image(), s, color, side);
	}

	public ToolItem
	status(final Image image, final String s, final GamaUIColor color, final int side /* SWT.LEFT or SWT.RIGHT */) {
		wipe(side, true);
		ToolItem item = button(color, s, image, side);
		refresh(true);
		return item;
	}

	public ToolItem tooltip(final String s, final GamaUIColor color, final int side /* SWT.LEFT or SWT.RIGHT */) {
		if ( s == null ) { return null; }
		hasTooltip = true;
		final GamaToolbarSimple tb = getToolbar(side);
		wipe(side, false);
		final GamaToolbarSimple other = tb == right ? left : right;
		int mySize = getSize().x;
		int remainingLeftSize = tb.getSize().x;
		int rightSize = other.getSize().x;

		int width = mySize - remainingLeftSize - rightSize - 100;
		// wipe(side, false);
		Label label = new Label(tb, SWT.WRAP);
		label.setForeground(color.isDark() ? IGamaColors.WHITE.color() : IGamaColors.BLACK.color());
		String newString = "";
		// java.util.List<String> result = new ArrayList();
		try {
			BufferedReader reader = new BufferedReader(new StringReader(s));
			String line = reader.readLine();
			while (line != null) {
				if ( !StringUtils.isBlank(line) ) {
					newString += line + Strings.LN;
				}
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

	public ToolItem
	button(final GamaUIColor color, final String text, final SelectionListener listener, final int side) {
		FlatButton button = FlatButton.button(side == SWT.LEFT ? left : right, color, text, null);
		button.addSelectionListener(listener);
		return control(button, button.computeSize(SWT.DEFAULT, button.getHeight(), false).x + 4, side);
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

	/**
	 * Wipes the toolbar (left or right), including or not the simple tool items. Retuns the width of the toolbar once wiped.
	 *
	 * @param side
	 * @param includingToolItems
	 * @return
	 */
	public void wipe(final int side /* SWT.LEFT or SWT.RIGHT */, final boolean includingToolItems) {
		ToolItem[] items = getToolbar(side).getItems();
		for ( ToolItem t : items ) {
			Control c = t.getControl();
			if ( c == null && includingToolItems || c != null ) {
				if ( c != null ) {
					c.dispose();
				}
				t.dispose();
			}
		}
		prepareToolbar(side);
		refresh(true);
	}

	public void item(final IContributionItem item, final int side) {
		item.fill(getToolbar(side), getToolbar(side).getItemCount());
	}

	private ToolItem create(final String image, final String text, final String tip, final SelectionListener listener,
		final int style, final boolean forceText, final Control control, final int side /* SWT.LEFT or SWT.RIGHT */) {
		GamaToolbarSimple tb = getToolbar(side);
		ToolItem button = new ToolItem(tb, style);
		if ( text != null && forceText ) {
			button.setText(text);
		}
		if ( tip != null ) {
			button.setToolTipText(tip);
		}
		if ( image != null ) {
			Image im = GamaIcons.create(image).image();
			button.setImage(im);
		}
		if ( listener != null ) {
			button.addSelectionListener(listener);
		}
		if ( control != null ) {
			button.setControl(control);
		}
		return button;
	}

	private void prepareToolbar(final int side) {
		// GamaToolbarSimple tb = getToolbar(side);
		// if ( tb.getItemCount() > 0 ) { return; }
		// if ( side == SWT.LEFT ) {
		// sep(1, side);
		// }
	}

	/**
	 * @param right2
	 * @return
	 */
	public GamaToolbarSimple getToolbar(final int side) {
		return side == SWT.LEFT ? left : right;
	}

	/**
	 * @return
	 */
	public boolean hasTooltip() {
		return hasTooltip;
	}

}
