package ummisco.gama.serializer.experiment;

import com.thoughtworks.xstream.XStream;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
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

	public ExperimentBackwardAgent(final IPopulation s) throws GamaRuntimeException {
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

		return result;
	}

	@Override
	public boolean backward(final IScope scope) {
		// TODO : to change
		// clock.beginCycle();
		final boolean result = true;

		try {
			final int currentCycle = getSimulation().getCycle(scope);
			// TODO what is this executer ????
			// executer.executeBeginActions();

			if (canStepBack()) {
				currentNode = currentNode.getParent();
				final String previousState = currentNode.getData();

				if (previousState != null) {
					final ConverterScope cScope = new ConverterScope(scope);
					final XStream xstream = StreamConverter.loadAndBuild(cScope);

					// get the previous state
					final SavedAgent agt = (SavedAgent) xstream.fromXML(previousState);

					// Update of the simulation
					final SimulationAgent currentSimAgt = getSimulation();
					currentSimAgt.updateWith(scope, agt);

					// executer.executeEndActions();
					// executer.executeOneShotActions();

					final IOutputManager outputs = getSimulation().getOutputManager();
					if (outputs != null) {
						outputs.step(scope);
					}
				}
			}
		} finally {
			// TODO a remettre
			// clock.step(this.scope);
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
		return currentNode != null && currentNode.getParent() != null;
	}
}
