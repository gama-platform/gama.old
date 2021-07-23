package ummisco.gama.ui.parameters;

import static msi.gama.common.util.StringUtils.toGaml;
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
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;

public class EditorLabel {

	static {
		DEBUG.OFF();
	}

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

	public EditorLabel(final AbstractEditor ed, final Composite parent, final String title,
			final boolean isSubParameter) {
		label = new Label(parent, SWT.WRAP | SWT.RIGHT);
		final var d = new GridData(SWT.END, SWT.CENTER, true, false);
		d.minimumWidth = 70;
		d.horizontalIndent = isSubParameter ? 30 : 0;
		label.setLayoutData(d);
		label.setText(title == null ? " " : title);
		label.setToolTipText(computeLabelTooltip(ed));
		setBackgroundColor(parent.getBackground());
		setTextColor(ThemeHelper.isDark() ? DARK_ACTIVE : LIGHT_ACTIVE);
	}

	protected String computeLabelTooltip(final AbstractEditor e) {
		boolean isBatch = GAMA.getExperiment() != null && GAMA.getExperiment().isBatch();
		IParameter param = e.getParam();
		if (param == null) return "";
		boolean isExperiment = param.isDefinedInExperiment();
		StringBuilder s = new StringBuilder();
		if (param.isEditable()) {
			s.append("Parameter of type ").append(param.getType().serialize(false)).append(" that represents the ");
		} else {
			s.append("Read-only ");
		}
		s.append("value of " + (isExperiment ? "experiment" : "model") + " attribute " + param.getName());
		if (!isBatch) {
			if (e.getMinValue() != null) {
				final var min = StringUtils.toGaml(e.getMinValue(), false);
				if (e.getMaxValue() != null) {
					s.append(" [").append(min).append("..").append(toGaml(e.getMaxValue(), false)).append("]");
				} else {
					s.append(">= ").append(min);
				}
			} else if (e.getMaxValue() != null) { s.append("<=").append(toGaml(e.getMaxValue(), false)); }
			if ((e.getMinValue() != null || e.getMaxValue() != null) && e.getStepValue() != null) {
				s.append(" every ").append(e.getStepValue());
			}
		} else {
			final var u = param.getUnitLabel(e.getScope());
			if (u != null) { s.append(" ").append(u); }
		}
		return s.toString();
	}

	private Color textColorFor(final Color background) {
		return GamaColors.getTextColorForBackground(background).color();
	}

	public void signalChanged(final boolean changed) {
		if (changed) {
			states.add(State.changed);
		} else {
			states.remove(State.changed);
		}
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

	public void setActive(final boolean active) {
		if (active) {
			states.add(State.active);
		} else {
			states.remove(State.active);
		}
		redraw();
	}

	private void setTextColor(final Color c) {
		label.setForeground(c);
		// Necessary to override the CSS Theming Engine
		setCSSData();
	}

	private void setBackgroundColor(final Color c) {
		label.setBackground(c);
		setCSSData();
	}

	private void setCSSData() {
		Color c = label.getForeground();
		String foreground = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
		c = label.getBackground();
		String background = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
		label.setData("style", "color: " + foreground + "; background-color: " + background + ";");
	}

	private void redraw() {
		if (label.isDisposed()) return;
		if (states.contains(State.errored)) {
			setBackgroundColor(ERROR);
			setTextColor(textColorFor(ERROR));
		} else if (states.contains(State.changed) && CORE_EDITORS_HIGHLIGHT.getValue()) {
			setBackgroundColor(CHANGED);
			setTextColor(textColorFor(CHANGED));
		} else if (states.contains(State.active)) {
			setBackgroundColor(label.getParent().getBackground());
			setTextColor(ThemeHelper.isDark() ? DARK_ACTIVE : LIGHT_ACTIVE);
		} else { // Inactive
			setBackgroundColor(label.getParent().getBackground());
			setTextColor(INACTIVE);
		}
	}

	// public void resize(final int width) {
	//
	// // if (label.isDisposed()) return;
	// // label.setSize(width, label.getSize().y);
	// // ((GridData) label.getLayoutData()).widthHint = width;
	// }

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
