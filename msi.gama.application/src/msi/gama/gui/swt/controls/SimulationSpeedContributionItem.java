/*********************************************************************************************
 *
 *
 * 'SimulationSpeedContributionItem.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import org.eclipse.swt.graphics.Image;
import msi.gama.gui.swt.*;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Maths;

/**
 * The class SimulationSpeedContributionItem.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 * @modification now obeys a cubic power law from 0 to BASE_UNIT milliseconds
 *
 */
public class SimulationSpeedContributionItem extends SpeedContributionItem {

	double max = 1000;

	private static Image knob = GamaIcons.create("toolbar.knob4").image();

	/**
	 *
	 * @param v in milliseconds
	 * @return
	 */
	public double positionFromValue(final double v) {
		// returns a percentage between 0 and 1 (0 -> max milliseconds; 1 -> 0 milliseconds).
		return 1 - v / max;
	}

	/**
	 * v between 0 and 1. Retuns a value in milliseconds
	 * @param v
	 * @return
	 */
	public double valueFromPosition(final double v) {
		return max - v * max;
	}

	public SimulationSpeedContributionItem() {
		super(0, null, null, knob, IGamaColors.GRAY_LABEL, IGamaColors.OK);
		SwtGui.setSpeedControl(this);
	}


	@Override
	protected double getInitialValue() {
		ExperimentAgent a = GAMA.getExperiment() == null ? null : GAMA.getExperiment().getAgent();
		double value = 0d;
		double maximum = 1000d;
		if ( a != null ) {
			value = a.getMinimumDuration() * 1000;
			maximum = a.getInitialMinimumDuration() * 1000;
		}
		if ( maximum > max ) {
			max = maximum;
		}
		return positionFromValue(value);
	}

	/*
	 * Parameter in milliseconds
	 */
	@Override
	public void setInit(final double i, final boolean notify) {
		if ( i > max ) {
			max = i;
		}
		super.setInit(positionFromValue(i), notify);
	}

	/**
	 * Method getToolTipText()
	 * @see msi.gama.gui.swt.controls.IToolTipProvider#getToolTipText(double)
	 */
	@Override
	public String getToolTipText(final double position) {
		return "Minimum duration of a cycle " + Maths.opTruncate(valueFromPosition(position) / 1000, 3) + " s";
	}

	/**
	 * Method positionChanged()
	 * @see msi.gama.gui.swt.controls.IPositionChangeListener#positionChanged(double)
	 */
	@Override
	public void positionChanged(final double position) {
		GAMA.getExperiment().getAgent().setMinimumDurationExternal(valueFromPosition(position) / 1000);
	}

}
