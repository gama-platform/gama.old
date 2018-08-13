package ummisco.gama.serializer.gaml;

import msi.gama.common.interfaces.ISaveDelegate;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class SaveSimulationDelegate implements ISaveDelegate {

	public final String GSIM_EXTENSION = "gsim";
	
	/**
	 * Returns whether or not this delegate accepts to save agents to this kind of file
	 * @param scope TODO
	 * @param source
	 * 
	 * @return
	 */

	public boolean acceptSource(IScope scope, String extension) {
		return GSIM_EXTENSION.equals(extension);
	}

	public String getExtension() {
		return GSIM_EXTENSION;
	}

	public int save(IScope scope, Object sim, String filePath) {
		SimulationAgent simAgt;
		if(sim instanceof SimulationAgent) {
			simAgt = (SimulationAgent) sim;
		} else {
			throw GamaRuntimeException.error("Unable to save non-simulation agent in a GSIM file", scope);
		}
		
		return ReverseOperators.saveAgent(scope, simAgt, filePath);
	}	
}
