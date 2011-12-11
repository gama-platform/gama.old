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
package msi.gama.kernel.stack;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.kernel.experiment.IExperiment;

/*
 * A short-circuited scope that represents the scope of the experiment. If a simulation is
 * available, it refers to it and gains access to its global scope. If not, it throws the
 * appropriate runtime exceptions when a feature dependent on the existence of a simulation is
 * accessed
 */
public class ExperimentStack extends AbstractStack {

	private final IExperiment experiment;

	public ExperimentStack(final IExperiment exp) {
		experiment = exp;
	}

	@Override
	public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
		if ( experiment.hasParameter(name) ) {
			experiment.setParameterValue(name, v);
			return;
		}
		checkSimulation().getGlobalScope().setGlobalVarValue(name, v);
	}

	@Override
	public Object getGlobalVarValue(final String name) throws GamaRuntimeException {
		if ( experiment.hasParameter(name) ) { return experiment.getParameterValue(name); }
		return checkSimulation().getGlobalScope().getGlobalVarValue(name);
	}

	@Override
	public IAgent getWorldScope() {
		ISimulation sim = getSimulationScope();
		return sim == null ? null : sim.getWorld();
	}

	@Override
	public ISimulation getSimulationScope() {
		return experiment.getCurrentSimulation();
	}

	private ISimulation checkSimulation() throws GamaRuntimeException {
		ISimulation sim = getSimulationScope();
		if ( sim == null ) { throw new GamaRuntimeException("No simulation running"); }
		return sim;
	}

}
