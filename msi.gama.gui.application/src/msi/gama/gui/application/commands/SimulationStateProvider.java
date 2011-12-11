/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.commands;

import java.util.*;
import msi.gama.kernel.GAMA;
import org.eclipse.ui.*;
import org.eclipse.ui.services.IServiceLocator;

public class SimulationStateProvider extends AbstractSourceProvider {

	public final static String SIMULATION_RUNNING_STATE =
		"msi.gama.gui.application.commands.SimulationRunningState";
	private final static Map<String, String> map = new HashMap<String, String>(1);

	@Override
	public void dispose() {}

	@Override
	public void initialize(final IServiceLocator locator) {
		GAMA.state = this;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { SIMULATION_RUNNING_STATE };
	}

	@Override
	public Map<String, String> getCurrentState() {
		map.put(SIMULATION_RUNNING_STATE, GAMA.getFrontmostSimulationState());
		return map;
	}

	/**
	 * Change the UI state based on the state of the simulation (none, stopped, running or notready)
	 */
	public void updateStateTo(final String state) {
		fireSourceChanged(ISources.WORKBENCH, SIMULATION_RUNNING_STATE, state);
	}

}