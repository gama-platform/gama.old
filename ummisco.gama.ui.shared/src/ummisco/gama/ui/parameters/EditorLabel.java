/*******************************************************************************************************
 *
 * EditorLabel.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import static msi.gama.common.util.StringUtils.toGaml;
import static ummisco.gama.ui.utils.PreferencesHelper.CORE_EDITORS_HIGHLIGHT;

import java.util.EnumSet;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * The Class EditorLabel.
 */
public class EditorLabel extends Label {

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
		super(parent, SWT.WRAP | SWT.RIGHT);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).grab(true, false).minSize(40, SWT.DEFAULT)
				.indent(isSubParameter ? 30 : 0, 0).applyTo(this);
		setText(title == null ? " " : title);
		setToolTipText(computeLabelTooltip(ed));
		setColors(parent.getBackground());
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
		reskin();
	}

	/**
	 * Signal errored.
	 */
	public void signalErrored() {
		if (!states.contains(State.errored)) {
			states.add(State.errored);
			reskin();
		}
	}

	/**
	 * Cancel errored.
	 */
	public void cancelErrored() {
		if (states.contains(State.errored)) {
			states.remove(State.errored);
			reskin();
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
		reskin();
	}

	/**
	 * Sets the color.
	 *
	 * @param c
	 *            the new text color
	 */
	private void setColors(final Color... colors) {
		Color back = colors[0];
		if (back == null) { back = getParent().getBackground(); }
		setBackground(back);
		if (colors.length <= 1) {
			final Color background = back;
			setForeground(GamaColors.getTextColorForBackground(background).color());
		} else {
			setForeground(colors[1]);
		}
		// Necessary to override the CSS Theming Engine
		setCSSData();
	}

	/**
	 * Sets the CSS data.
	 */
	private void setCSSData() {
		GamaColors.setBackAndForeground(getBackground(), getForeground(), this);
	}

	/**
	 * Redraw.
	 */
	private void reskin() {
		if (isDisposed()) return;
		if (states.contains(State.errored)) {
			setColors(ERROR);
		} else if (states.contains(State.changed) && CORE_EDITORS_HIGHLIGHT.getValue()) {
			setColors(CHANGED);
		} else if (states.contains(State.active)) {
			setColors(getParent().getBackground());
		} else { // Inactive
			setColors(getParent().getBackground(), INACTIVE);
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
	@Override
	public void setMenu(final Menu m) {
		if (isDisposed()) return;
		super.setMenu(m);
	}

	/**
	 * Sets the horizontal alignment.
	 *
	 * @param lead
	 *            the new horizontal alignment
	 */
	public void setHorizontalAlignment(final int lead) {
		if (isDisposed()) return;
		((GridData) getLayoutData()).horizontalAlignment = lead;
	}

	/**
	 * Creates the menu.
	 *
	 * @return the menu
	 */
	public Menu createMenu() {
		if (isDisposed()) return null;
		return new Menu(this);
	}

	@Override
	protected void checkSubclass() {}

}
