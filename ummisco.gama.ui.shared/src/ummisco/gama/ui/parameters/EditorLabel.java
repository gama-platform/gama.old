package ummisco.gama.ui.parameters;

import static ummisco.gama.ui.utils.PreferencesHelper.CORE_EDITORS_HIGHLIGHT;

import java.util.EnumSet;

import javax.annotation.Nonnull;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

import msi.gama.application.workbench.ThemeHelper;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.IGamaColors;

public class EditorLabel {

	public static final Color ERROR = IGamaColors.ERROR.color();
	public static final Color CHANGED = IGamaColors.TOOLTIP.color();
	public static final Color DARK_ACTIVE = IGamaColors.VERY_LIGHT_GRAY.color();
	public static final Color LIGHT_ACTIVE = IGamaColors.BLACK.color();
	public static final Color INACTIVE = GamaColors.system(SWT.COLOR_GRAY);

	enum State {
		active, errored, changed;
	}

	@Nonnull private final Label label;
	EnumSet<State> states = EnumSet.of(State.active);

	public EditorLabel(final Composite parent, final String title, final String tooltip, final boolean isSubParameter) {
		label = new Label(parent, SWT.WRAP | SWT.RIGHT);
		final var d = new GridData(SWT.END, SWT.CENTER, true, false);
		d.minimumWidth = 70;
		d.horizontalIndent = isSubParameter ? 30 : 0;
		label.setLayoutData(d);
		label.setFont(GamaFonts.getLabelfont());
		label.setText(title);
		label.setToolTipText(tooltip);
		label.setBackground(parent.getBackground());
		setTextColor(ThemeHelper.isDark() ? DARK_ACTIVE : LIGHT_ACTIVE);
	}

	private Color textColorFor(final Color background) {
		return GamaColors.getTextColorForBackground(background).color();
	}

	public void signalChanged() {
		states.add(State.changed);
		redraw();
	}

	public void cancelChanged() {
		states.remove(State.changed);
		redraw();
	}

	public void signalErrored() {
		if (!states.contains(State.errored)) {
			states.add(State.errored);
			redraw();
		}
	}

	public void cancelErrored() {
		if (states.contains(State.errored)) {
			states.remove(State.errored);
			redraw();
		}
	}

	public void setActive() {
		states.add(State.active);
		redraw();
	}

	public void setInactive() {
		states.remove(State.active);
		redraw();
	}

	private void setTextColor(final Color c) {
		label.setForeground(c);
		// Necessary to override the CSS Theming Engine
		label.setData("style", "color: " + String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
	}

	private void redraw() {
		if (label.isDisposed()) return;
		if (states.contains(State.errored)) {
			label.setBackground(ERROR);
			setTextColor(textColorFor(ERROR));
		} else if (states.contains(State.changed) && CORE_EDITORS_HIGHLIGHT.getValue()) {
			label.setBackground(CHANGED);
			setTextColor(textColorFor(CHANGED));
		} else if (states.contains(State.active)) {
			label.setBackground(label.getParent().getBackground());
			setTextColor(ThemeHelper.isDark() ? DARK_ACTIVE : LIGHT_ACTIVE);
		} else { // Inactive
			label.setBackground(label.getParent().getBackground());
			setTextColor(INACTIVE);
		}
	}

	public void resize(final int width) {
		if (label.isDisposed()) return;
		((GridData) label.getLayoutData()).widthHint = width;
	}

	public void setMenu(final Menu m) {
		if (label.isDisposed()) return;
		label.setMenu(m);
	}

	public void setHorizontalAlignment(final int lead) {
		if (label.isDisposed()) return;
		((GridData) label.getLayoutData()).horizontalAlignment = lead;
	}

	public Menu createMenu() {
		if (label.isDisposed()) return null;
		return new Menu(label);
	}

}
