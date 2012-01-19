/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gaml.operators.Maths;

/**
 * The class SimulationSpeedContributionItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class SimulationSpeedContributionItem extends SpeedContributionItem {

	/**
	 * 
	 */
	public SimulationSpeedContributionItem() {
		super("Adjust simulation speed", SimulationClock.getDelay(), new IPositionChangeListener() {

			@Override
			public void positionChanged(final double position) {
				SimulationClock.setDelay(position);
			}

		}, new IToolTipProvider() {

			@Override
			public String getToolTipText(final double value) {
				return "Delay " + Maths.opTruncate((1000 - 1000 * value) / 1000, 2) + " s";
			}

		}, SwtGui.thumb, SwtGui.thumb_over, SwtGui.COLOR_OK);
	}

	/**
	 * @param id
	 */
	public SimulationSpeedContributionItem(final String id) {
		this();
		setId(id);
	}

}
