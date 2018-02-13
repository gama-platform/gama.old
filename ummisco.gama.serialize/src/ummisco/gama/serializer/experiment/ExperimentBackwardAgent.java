/*********************************************************************************************
 *
 * 'ExperimentBackwardAgent.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.experiment;

import com.thoughtworks.xstream.XStream;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.IOutputManager;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.TypeNode;
import msi.gaml.types.TypeTree;
import ummisco.gama.serializer.factory.StreamConverter;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;
import ummisco.gama.serializer.gaml.ReverseOperators;

@experiment(IKeyword.MEMORIZE)
public class ExperimentBackwardAgent extends ExperimentAgent {

	TypeTree<String> historyTree;
	TypeNode<String> currentNode;

	public ExperimentBackwardAgent(final IPopulation<? extends IAgent> s) throws GamaRuntimeException {
		super(s);
		historyTree = new TypeTree<String>();
	}

	/**
	 * Redefinition of the callback method
	 * 
	 * @see msi.gama.metamodel.agent.GamlAgent#_init_(msi.gama.runtime.IScope)
	 */
	@Override
	public Object _init_(final IScope scope) {
		super._init_(scope);
		// Save simulation state in the history
		final String state = ReverseOperators.serializeAgent(scope, this.getSimulation());

		historyTree.setRoot(state);
		currentNode = historyTree.getRoot();

		return this;
	}

	@Override
	public boolean step(final IScope scope) {
		// Do a normal step
		final boolean result = super.step(scope);

		// Save simulation state in the history
		final String state = ReverseOperators.serializeAgent(scope, this.getSimulation());

		currentNode = currentNode.addChild(state);
		
//		scope.getGui().getConsole(scope).informConsole("step RNG " + getSimulation().getRandomGenerator().getUsage(), scope.getRoot(), new GamaColor(0, 0, 0));
		
		return result;
	}

	@Override
	public boolean backward(final IScope scope) {
		final boolean result = true;
		TypeNode<String> previousNode;

		try {
			if (canStepBack()) {
				previousNode = currentNode.getParent();
				final String previousState = previousNode.getData();

				if (previousState != null) {
					final ConverterScope cScope = new ConverterScope(scope);
					final XStream xstream = StreamConverter.loadAndBuild(cScope);

					// get the previous state
					final SavedAgent agt = (SavedAgent) xstream.fromXML(previousState);
					

					// Update of the simulation
					final SimulationAgent currentSimAgt = getSimulation();
					
					currentSimAgt.updateWith(scope, agt);
					
					// useful to recreate the random generator
					int rngUsage = currentSimAgt.getRandomGenerator().getUsage();
					String rngName = currentSimAgt.getRandomGenerator().getRngName();
					Double rngSeed = currentSimAgt.getRandomGenerator().getSeed();
					
					final IOutputManager outputs = getSimulation().getOutputManager();
					if (outputs != null) {
						outputs.step(scope);
					}
										
					// Recreate the random generator and set it to the same state as the saved one
					if( ((ExperimentPlan) this.getSpecies()).keepsSeed() ) {
						currentSimAgt.setRandomGenerator(new RandomUtils(rngSeed, rngName));	
						currentSimAgt.getRandomGenerator().setUsage(rngUsage);						
					} else {
						currentSimAgt.setRandomGenerator(new RandomUtils(super.random.next(), rngName));							
					}

					currentNode = currentNode.getParent();
				}
			}
		} finally {
			informStatus();

			// TODO a remettre
			// final int nbThreads =
			// this.getSimulationPopulation().getNumberOfActiveThreads();

			// if (!getSpecies().isBatch() && getSimulation() != null) {
			// scope.getGui().informStatus(
			// getSimulation().getClock().getInfo() + (nbThreads > 1 ? " (" +
			// nbThreads + " threads)" : ""));
			// }
		}
		return result;
	}

	@Override
	public boolean canStepBack() {
		
		int current_cycle = getSimulation().getCycle(this.getScope());
		return (current_cycle > 0 ) ? true : false;
	//	return currentNode != null && currentNode.getParent() != null;
	}
	
	@Override
	public boolean isMemorize() {
		return true;
	}	
}
