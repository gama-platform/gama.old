/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.experiment;

import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.ISimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.AbstractScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * A short-circuited scope that represents the scope of the experiment. If a simulation is
 * available, it refers to it and gains access to its global scope. If not, it throws the
 * appropriate runtime exceptions when a feature dependent on the existence of a simulation is
 * accessed
 * 
 * @author Alexis Drogoul
 * @since November 2011
 */
public class ExperimentScope extends AbstractScope {

	private final IExperimentSpecies experiment;

	public ExperimentScope(final IExperimentSpecies exp, final String name) {
		super(name);
		experiment = exp;
	}

	@Override
	public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
		if ( experiment.hasParameter(name) ) {
			experiment.setParameterValue(name, v);
			return;
		}
		experiment.getCurrentSimulation().setDirectVarValue(this, name, v);
	}

	@Override
	public Object getGlobalVarValue(final String name) throws GamaRuntimeException {
		if ( experiment.hasParameter(name) ) { return experiment.getParameterValue(name); }
		return experiment.getCurrentSimulation().getDirectVarValue(this, name);
	}

	@Override
	public ISimulationAgent getSimulationScope() {
		IAgent a = experiment.getAgent();
		if ( a == null ) { return null; }
		return a.getSimulation();
	}

	@Override
	public IModel getModel() {
		return experiment.getModel();
	}

}
