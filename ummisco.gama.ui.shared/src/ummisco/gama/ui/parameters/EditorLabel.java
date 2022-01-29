/*******************************************************************************************************
 *
 * EditorLabel.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

/**
 * The Class EditorLabel.
 */
public class EditorLabel {

	static {
		DEBUG.OFF();
	}

	/** The Constant ERROR. */
	public static final Color ERROR = IGamaColors.ERROR.color();

	/** The Constant CHANGED. */
	public static final Color CHANGED = IGamaColors.TOOLTIP.color();

	/** The Constant DARK_ACTIVE. */
	public static final Color DARK_ACTIVE = IGamaColors.VERY_LIGHT_GRAY.color();

	/** The Constant LIGHT_ACTIVE. */
	public static final Color LIGHT_ACTIVE = IGamaColors.BLACK.color();

	/** The Constant INACTIVE. */
	public static final Color INACTIVE = GamaColors.system(SWT.COLOR_GRAY);

	/**
	 * The Enum State.
	 */
	enum State {

		/** The active. */
		active,
		/** The errored. */
		errored,
		/** The changed. */
		changed;
	}

	/** The label. */
	@Nonnull private final Label label;

	/** The states. */
	EnumSet<State> states = EnumSet.of(State.active);

	/**
	 * Instantiates a new editor label.
	 *
	 * @param ed
	 *            the ed
	 * @param parent
	 *            the parent
	 * @param title
	 *            the title
	 * @param isSubParameter
	 *            the is sub parameter
	 */
	public EditorLabel(final AbstractEditor ed, final Composite parent, final String title,
			final boolean isSubParameter) {
		label = new Label(parent, SWT.WRAP | SWT.RIGHT);
		final var d = new GridData(SWT.END, SWT.CENTER, true, false);
		d.minimumWidth = 40;
		d.horizontalIndent = isSubParameter ? 30 : 0;
		label.setLayoutData(d);
		label.setText(title == null ? " " : title);
		label.setToolTipText(computeLabelTooltip(ed));
		setBackgroundColor(parent.getBackground());
		setTextColor(ThemeHelper.isDark() ? DARK_ACTIVE : LIGHT_ACTIVE);
	}

	/**
	 * Compute label tooltip.
	 *
	 * @param e
	 *            the e
	 * @return the string
	 */
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

	/**
	 * Text color for.
	 *
	 * @param background
	 *            the background
	 * @return the color
	 */
	private Color textColorFor(final Color background) {
		return GamaColors.getTextColorForBackground(background).color();
	}

	/**
	 * Signal changed.
	 *
	 * @param changed
	 *            the changed
	 */
	public void signalChanged(final boolean changed) {
		if (changed) {
			states.add(State.changed);
		} else {
			states.remove(State.changed);
		}
		redraw();
	}

	/**
	 * Signal errored.
	 */
	public void signalErrored() {
		if (!states.contains(State.errored)) {
			states.add(State.errored);
			redraw();
		}
	}

	/**
	 * Cancel errored.
	 */
	public void cancelErrored() {
		if (states.contains(State.errored)) {
			states.remove(State.errored);
			redraw();
		}
	}

	/**
	 * Sets the active.
	 *
	 * @param active
	 *            the new active
	 */
	public void setActive(final boolean active) {
		if (active) {
			states.add(State.active);
		} else {
			states.remove(State.active);
		}
		redraw();
	}

	/**
	 * Sets the text color.
	 *
	 * @param c
	 *            the new text color
	 */
	private void setTextColor(final Color c) {
		label.setForeground(c);
		// Necessary to override the CSS Theming Engine
		setCSSData();
	}

	/**
	 * Sets the background color.
	 *
	 * @param c
	 *            the new background color
	 */
	private void setBackgroundColor(final Color c) {
		label.setBackground(c);
		setCSSData();
	}

	/**
	 * Sets the CSS data.
	 */
	private void setCSSData() {
		GamaColors.setBackAndForeground(label, label.getBackground(), label.getForeground());
	}

	/**
	 * Redraw.
	 */
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

	/**
	 * Sets the menu.
	 *
	 * @param m
	 *            the new menu
	 */
	public void setMenu(final Menu m) {
		if (label.isDisposed()) return;
		label.setMenu(m);
	}

	/**
	 * Sets the horizontal alignment.
	 *
	 * @param lead
	 *            the new horizontal alignment
	 */
	public void setHorizontalAlignment(final int lead) {
		if (label.isDisposed()) return;
		((GridData) label.getLayoutData()).horizontalAlignment = lead;
	}

	/**
	 * Creates the menu.
	 *
	 * @return the menu
	 */
	public Menu createMenu() {
		if (label.isDisposed()) return null;
		return new Menu(label);
	}

}
