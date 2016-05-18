package ummisco.gama.serializer.experiment;

import java.util.ArrayList;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.serializer.gaml.ReverseOperators;

@experiment("memorize")
public class ExperimentBackwardAgent extends ExperimentAgent{

	ArrayList<String> history;
	
	public ExperimentBackwardAgent(IPopulation s) throws GamaRuntimeException {
		super(s);
		
		history = new ArrayList<String>();
	}
	
	// TODO !!!!!!!!!!!!
	public ExperimentBackwardAgent(Object[] args) {
		super((IPopulation) args[0]);
	}

	@Override
	public boolean step(final IScope scope) {
		// Do a normal step
		boolean result = super.step(scope);
		
		// Save simulation state in the history
		String state = ReverseOperators.serializeAgent( scope, this.getSimulation()) ;
		history.add(state);
		
		return result;
	}
	
	public boolean backward(final IScope scope) {
		return true;
	}

}
