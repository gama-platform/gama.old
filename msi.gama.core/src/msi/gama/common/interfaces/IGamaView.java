/*********************************************************************************************
 *
 * 'IGamaView.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.architecture.user.UserPanelStatement;

/**
 * @author drogoul
 */
public interface IGamaView {

	public interface Display {
		IDisplaySurface getDisplaySurface();

		/**
		 * For some views (esp. the ones based on Swing), it is necessary to wait a while after opening it, in order for
		 * the view to be fully realized
		 */
		public void waitToBeRealized();
	}

	public interface Error {

		public void displayErrors();

	}

	public interface Html {
		public void setUrl(String url);
	}

	public interface Parameters {
		public void addItem(IExperimentPlan exp);

		public void updateItemValues();
	}

	public interface Console {

		void append(String msg, ITopLevelAgent root, GamaColor color);

	}

	public interface User {
		public void initFor(final IScope scope, final UserPanelStatement panel);
	}

	public void update(IDisplayOutput output);

	public void addOutput(IDisplayOutput output);

	IDisplayOutput getOutput();

	public void close(IScope scope);

	public void removeOutput(IDisplayOutput putput);

	public void changePartNameWithSimulation(SimulationAgent agent);

	public void reset();

	public String getPartName();

	public void setName(String name);

	public void updateToolbarState();

}
