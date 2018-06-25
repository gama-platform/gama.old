/*********************************************************************************************
 *
 * 'GamaToolbar2.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolItem;

import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory.ToggleAction;

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
		final GridLayout layout = new GridLayout(2, false);
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
		data.minimumWidth = height * 2;
		left.setLayoutData(data);

		right = new GamaToolbarSimple(this, SWT.FLAT | SWT.HORIZONTAL | SWT.NO_FOCUS);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.verticalIndent = 0;
		data.horizontalAlignment = SWT.RIGHT;
		data.minimumWidth = height * 2;
		right.setLayoutData(data);

	}

	@Override
	protected void checkSubclass() {}

	public ToolItem sep(final int n, final int side /* SWT.LEFT or SWT.RIGHT */) {
		final GamaIcon icon = GamaIcons.createSizer(getBackground(), n, height);
		final ToolItem item = create(icon.getCode(), null, null, null, SWT.NONE, false, null, side);
		item.setDisabledImage(icon.image());
		item.setEnabled(false);
		return item;
	}

	public ToolItem status(final String image, final String s, final GamaUIColor color,
			final int side /* SWT.LEFT or SWT.RIGHT */) {
		return status(GamaIcons.create(image).image(), s, color, side);
	}

	public ToolItem status(final Image image, final String s, final GamaUIColor color,
			final int side /* SWT.LEFT or SWT.RIGHT */) {
		wipe(side, true);
		final ToolItem item = button(color, s, image, side);
		refresh(true);
		return item;
	}

	public ToolItem status(final Image image, final String s, final Selector l, final GamaUIColor color,
			final int side /* SWT.LEFT or SWT.RIGHT */) {
		wipe(side, true);
		final ToolItem item = button(color, s, image, side);
		((FlatButton) item.getControl()).addSelectionListener(l);
		refresh(true);
		return item;
	}

	public ToolItem tooltip(final String s, final GamaUIColor color, final int side /* SWT.LEFT or SWT.RIGHT */) {
		if (s == null) { return null; }
		hasTooltip = true;
		final GamaToolbarSimple tb = getToolbar(side);
		wipe(side, false);
		final GamaToolbarSimple other = tb == right ? left : right;
		final int mySize = getSize().x;
		final int remainingLeftSize = tb.getSize().x;
		final int rightSize = other.getSize().x;

		final int width = mySize - remainingLeftSize - rightSize - 100;
		// wipe(side, false);
		final Label label = new Label(tb, SWT.WRAP);
		label.setForeground(GamaColors.getTextColorForBackground(color).color());
		String newString = "";
		// java.util.List<String> result = new ArrayList<>();
		try {
			final BufferedReader reader = new BufferedReader(new StringReader(s));
			String line = reader.readLine();
			while (line != null) {
				if (!line.trim().isEmpty()) {
					newString += line + java.lang.System.getProperty("line.separator");
				}
				line = reader.readLine();
			}
		} catch (final IOException exc) {}
		label.setText(newString);
		label.setFont(GamaFonts.getSmallFont());
		label.setBackground(color.inactive());
		final ToolItem t = control(label, /* c.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10 */width, side);
		refresh(true);
		return t;
	}

	public ToolItem check(final String image, final String text, final String tip, final Selector listener,
			final int side /* SWT.LEFT or SWT.RIGHT */) {
		return create(image, text, tip, listener, SWT.CHECK, false, null, side);
	}

	public ToolItem check(final GamaCommand command, final int side) {
		return check(command.getImage(), command.getText(), command.getTooltip(), command.getListener(), side);
	}

	public ToolItem button(final String image, final String text, final String tip, final Selector listener,
			final int side /* SWT.LEFT or SWT.RIGHT */) {
		return create(image, text, tip, listener, SWT.PUSH, false, null, side);
	}

	public ToolItem button(final GamaCommand command, final int side) {
		return button(command.getImage(), command.getText(), command.getTooltip(), command.getListener(), side);
	}

	public ToolItem button(final GamaUIColor color, final String text, final Selector listener, final int side) {
		final FlatButton button = FlatButton.button(side == SWT.LEFT ? left : right, color, text, null);
		button.addSelectionListener(listener);
		return control(button, button.computeSize(SWT.DEFAULT, button.getHeight(), false).x + 4, side);
	}

	public ToolItem button(final GamaUIColor color, final String text, final Image image, final int side) {
		final FlatButton button = FlatButton.button(side == SWT.LEFT ? left : right, color, text, image);
		return control(button, button.computeSize(SWT.DEFAULT, button.getHeight(), false).x + 4, side);
	}

	public ToolItem button(final GamaUIColor color, final String text, final Image image, final Selector listener,
			final int side) {
		final FlatButton button = FlatButton.button(side == SWT.LEFT ? left : right, color, text, image);
		button.addSelectionListener(listener);
		return control(button, button.computeSize(SWT.DEFAULT, button.getHeight(), false).x + 4, side);
	}

	public ToolItem label(final GamaUIColor color, final String text, final Image image, final int side) {
		final FlatButton button = FlatButton.label(side == SWT.LEFT ? left : right, color, text, image);
		return control(button, button.computeSize(SWT.DEFAULT, button.getHeight(), false).x + 4, side);
	}

	public ToolItem menu(final GamaUIColor color, final String text, final int side) {
		final FlatButton button = FlatButton.menu(side == SWT.LEFT ? left : right, color, text);
		return control(button, button.computeSize(SWT.DEFAULT, button.getHeight(), false).x + 4, side);
	}

	public ToolItem menu(final String image, final String text, final String tip, final Selector listener,
			final int side /* SWT.LEFT or SWT.RIGHT */) {
		return create(image, text, tip, listener, SWT.DROP_DOWN, false, null, side);
	}

	public ToolItem menu(final GamaCommand command, final int side) {
		return menu(command.getImage(), command.getText(), command.getTooltip(), command.getListener(), side);
	}

	public ToolItem control(final Control c, final int width, final int side /* SWT.LEFT or SWT.RIGHT */) {
		final ToolItem control = create(null, null, null, null, SWT.SEPARATOR, false, c, side);
		// control.setControl(c);
		if (width == SWT.DEFAULT) {
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
	 * Wipes the toolbar (left or right), including or not the simple tool items. Retuns the width of the toolbar once
	 * wiped.
	 *
	 * @param side
	 * @param includingToolItems
	 * @return
	 */
	public void wipe(final int side /* SWT.LEFT or SWT.RIGHT */, final boolean includingToolItems) {

		final ToolItem[] items = getToolbar(side).getItems();
		for (final ToolItem t : items) {
			final Control c = t.getControl();
			if (c == null && includingToolItems || c != null) {
				if (c != null) {
					c.dispose();
				}
				t.dispose();
			}
		}
		normalizeToolbars();
		refresh(true);

	}

	public void item(final IContributionItem item, final int side) {
		item.fill(getToolbar(side), getToolbar(side).getItemCount());
	}

	private ToolItem create(final String image, final String text, final String tip, final SelectionListener listener,
			final int style, final boolean forceText, final Control control,
			final int side /* SWT.LEFT or SWT.RIGHT */) {
		final GamaToolbarSimple tb = getToolbar(side);
		final ToolItem button = new ToolItem(tb, style);
		if (text != null && forceText) {
			button.setText(text);
		}
		if (tip != null) {
			button.setToolTipText(tip);
		}
		if (image != null) {
			final Image im = GamaIcons.create(image).image();
			button.setImage(im);
		}
		if (listener != null) {
			button.addSelectionListener(listener);
		}
		if (control != null) {
			button.setControl(control);
		}
		normalizeToolbars();

		return button;
	}

	private void normalizeToolbars() {
		// final int n = right.getItemCount();
		int size = 0;
		for (final ToolItem t : right.getItems()) {
			size += t.getWidth();
		}
		((GridData) right.getLayoutData()).minimumWidth = size;
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

	public void updateItems() {

	}

	private ToggleAction toggle;

	public void setToogleAction(final ToggleAction toggle) {
		this.toggle = toggle;
	}

	public void hide() {
		toggle.show = true; // force to true
		toggle.run(); // will make it false
	}

	public void show() {
		toggle.show = true; // force to false
		toggle.run(); // will make it true
	}

}
