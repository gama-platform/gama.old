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

import msi.gama.gui.swt.*;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Maths;
import org.eclipse.swt.graphics.Image;

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

	private static final double BASE_UNIT = 1000;

	private static Image knob = GamaIcons.create("toolbar.knob4").image(); // IGamaIcons.TOOLBAR_KNOB.image(), IGamaIcons.TOOLBAR_KNOB_HOVER.image()

	/**
	 * 
	 * @param v in milliseconds
	 * @return
	 */
	public static double positionFromValue(final double v) {
		// returns a percentage between 0 and 1 (0 -> BASE_UNIT milliseconds; 1 -> 0 milliseconds).
		return 1 - v / BASE_UNIT;
	}

	/**
	 * v between 0 and 1. Retuns a value in milliseconds
	 * @param v
	 * @return
	 */
	public static double valueFromPosition(final double v) {
		return BASE_UNIT - v * BASE_UNIT;
	}

	public SimulationSpeedContributionItem() {
		super(positionFromValue(GAMA.getDelayInMilliseconds()), new IPositionChangeListener() {

			@Override
			public void positionChanged(final double position) {
				GAMA.setDelayFromUI(valueFromPosition(position));
			}

		}, new IToolTipProvider() {

			@Override
			public String getToolTipText(final double value) {
				return "Minimum duration of a cycle " + Maths.opTruncate(GAMA.getDelayInMilliseconds() / 1000, 3) +
					" s";
			}

		}, knob, IGamaColors.GRAY_LABEL, IGamaColors.OK);
		SwtGui.setSpeedControl(this);
	}

	/**
	 * @param id
	 */
	// public SimulationSpeedContributionItem(final String id) {
	// this();
	// setId(id);
	// }

	@Override
	protected double getInitialValue() {
		return positionFromValue(GAMA.getDelayInMilliseconds());
	}

	/*
	 * Parameter in milliseconds
	 */
	@Override
	public void setInit(final double i, final boolean notify) {
		super.setInit(positionFromValue(i), notify);
	}

}
