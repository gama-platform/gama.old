/*********************************************************************************************
 *
 * 'SimulationSpeedContributionItem.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Maths;
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
public class SimulationSpeedContributionItem extends WorkbenchWindowControlContribution
		implements ISpeedDisplayer, IPositionChangeListener, IToolTipProvider {

	private static SimulationSpeedContributionItem instance;
	double max = 1000;
	protected final GamaUIColor popupColor;
	protected final GamaUIColor sliderColor;
	public final static int widthSize = 100;
	public final static int heightSize = 16;
	protected SimpleSlider slider;

	/**
	 *
	 * @param v
	 *            in milliseconds
	 * @return
	 */
	public double positionFromValue(final double v) {
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
	public double valueFromPosition(final double v) {
		return max - v * max;
	}

	public SimulationSpeedContributionItem() {
		popupColor = IGamaColors.OK;
		sliderColor = IGamaColors.GRAY_LABEL;
		instance = this;
	}

	@Override
	public Control createControl(final Composite parent) {

		final Composite composite = new Composite(parent, SWT.DOUBLE_BUFFERED);
		final GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 16;
		composite.setLayout(layout);
		composite.setBackground(parent.getBackground());
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = widthSize;
		data.minimumWidth = widthSize;
		slider = new SimpleSlider(composite, sliderColor.color(), sliderColor.color(),
				GamaIcons.create(IGamaIcons.TOOLBAR_KNOB).image());
		slider.setTooltipInterperter(this);
		slider.setLayoutData(data);
		slider.setSize(widthSize, heightSize);
		slider.specifyHeight(heightSize); // fix the problem of wrong position
		// for the tooltip. Certainly not the best way but it does the trick
		slider.addPositionChangeListener(this);
		slider.setPopupBackground(popupColor);
		slider.updateSlider(getInitialValue(), false);
		slider.setBackground(parent.getBackground());
		return composite;
	}

	protected double getInitialValue() {
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
		if (slider == null) {
			return;
		}
		if (slider.isDisposed()) {
			return;
		}
		slider.updateSlider(i, notify);
	}

	/**
	 * Method getToolTipText()
	 * 
	 * @see ummisco.gama.ui.controls.IToolTipProvider#getToolTipText(double)
	 */
	@Override
	public String getToolTipText(final double position) {
		return "Minimum duration of a cycle " + Maths.opTruncate(valueFromPosition(position) / 1000, 3) + " s";
	}

	/**
	 * Method positionChanged()
	 * 
	 * @see ummisco.gama.ui.controls.IPositionChangeListener#positionChanged(double)
	 */
	@Override
	public void positionChanged(final double position) {
		GAMA.getExperiment().getAgent().setMinimumDurationExternal(valueFromPosition(position) / 1000);
	}

	public static SimulationSpeedContributionItem getInstance() {
		return instance;
	}

}
