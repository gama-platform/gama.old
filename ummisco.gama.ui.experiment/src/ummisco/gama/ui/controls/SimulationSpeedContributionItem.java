/*******************************************************************************************************
 *
 * ummisco.gama.ui.controls.SimulationSpeedContributionItem.java, in plugin ummisco.gama.ui.experiment, is part of the
 * source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Maths;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.interfaces.ISpeedDisplayer;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The class SimulationSpeedContributionItem.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 * @modification now obeys a cubic power law from 0 to BASE_UNIT milliseconds
 *
 */
public class SimulationSpeedContributionItem extends WorkbenchWindowControlContribution implements ISpeedDisplayer {

	static {
		DEBUG.OFF();
	}

	private static SimulationSpeedContributionItem instance;
	static double max = 1000;
	protected final static GamaUIColor popupColor = IGamaColors.OK;
	protected final static GamaUIColor sliderColor = IGamaColors.GRAY_LABEL;
	public final static int widthSize = 100;
	public final static int marginWidth = 16;
	public final static int heightSize = 16;
	protected static List<SimpleSlider> sliders = new ArrayList<>();

	/**
	 *
	 * @param v
	 *            in millisecondsmax
	 * @return
	 */
	public static double positionFromValue(final double v) {
		// returns a percentage between 0 and 1 (0 -> max milliseconds; 1 -> 0
		// milliseconds).
		return 1 - v / max;
	}

	@Override
	protected int computeWidth(final Control control) {
		return control.computeSize(widthSize, SWT.DEFAULT, true).x;
	}

	/**
	 * v between 0 and 1. Retuns a value in milliseconds
	 * 
	 * @param v
	 * @return
	 */
	public static double valueFromPosition(final double v) {
		return max - v * max;
	}

	public SimulationSpeedContributionItem() {
		instance = this;
	}

	public static int totalWidth() {
		return widthSize + 2 * marginWidth;
	}

	@Override
	public Control createControl(final Composite parent) {
		return create(parent);
	}

	public static Control create(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.DOUBLE_BUFFERED);
		final GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = marginWidth;
		composite.setLayout(layout);
		composite.setBackground(parent.getBackground());
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = widthSize;
		data.minimumWidth = widthSize;
		final SimpleSlider slider = new SimpleSlider(composite, sliderColor.color(), sliderColor.color(),
				GamaIcons.create(IGamaIcons.TOOLBAR_KNOB).image());
		slider.setTooltipInterperter(TOOLTIP_PROVIDER);
		slider.setLayoutData(data);
		slider.setSize(widthSize, heightSize);
		slider.specifyHeight(heightSize); // fix the problem of wrong position
		// for the tooltip. Certainly not the best way but it does the trick
		slider.addPositionChangeListener(POSITION_LISTENER);
		slider.setPopupBackground(popupColor);
		slider.updateSlider(getInitialValue(), false);
		slider.setBackground(parent.getBackground());
		slider.addDisposeListener(e -> {
			sliders.remove(slider);
			DEBUG.OUT("Slider " + slider + " is disposed");
		});
		sliders.add(slider);
		return composite;

	}

	protected static double getInitialValue() {
		final ExperimentAgent a = GAMA.getExperiment() == null ? null : GAMA.getExperiment().getAgent();
		double value = 0d;
		double maximum = 1000d;
		if (a != null) {
			value = a.getMinimumDuration() * 1000;
			maximum = a.getInitialMinimumDuration() * 1000;
		}
		if (maximum > max) {
			max = maximum;
		}
		return positionFromValue(value);
	}

	/*
	 * Parameter in milliseconds
	 */
	@Override
	public void setInit(final double i, final boolean notify) {
		if (i > max) {
			max = i;
		}
		for (final SimpleSlider slider : sliders) {
			if (slider == null) {
				continue;
			}
			if (slider.isDisposed()) {
				continue;
			}
			slider.updateSlider(i, notify);
		}
	}

	static IToolTipProvider TOOLTIP_PROVIDER =
			position -> "Minimum duration of a cycle " + Maths.opTruncate(valueFromPosition(position) / 1000, 3) + " s";

	static IPositionChangeListener POSITION_LISTENER = (s, position) -> {
		DEBUG.OUT("Position changed to " + position + " affects sliders: " + sliders);
		GAMA.getExperiment().getAgent().setMinimumDurationExternal(valueFromPosition(position) / 1000);
		for (final SimpleSlider slider2 : sliders) {
			if (slider2 == s) {
				continue;
			}
			slider2.updateSlider(position, false);
		}

	};

	public static SimulationSpeedContributionItem getInstance() {
		return instance;
	}

}
