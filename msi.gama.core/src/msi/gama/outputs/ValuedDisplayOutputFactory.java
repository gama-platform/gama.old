/*******************************************************************************************************
 *
 * msi.gama.outputs.ValuedDisplayOutputFactory.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import java.util.Collection;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.model.GamlModelSpecies;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;

public class ValuedDisplayOutputFactory {

	public static void browse(final Collection<? extends IAgent> agents) {
		IPopulation<? extends IAgent> pop = null;
		IMacroAgent root = null;
		if (agents instanceof IPopulation) {
			pop = (IPopulation<? extends IAgent>) agents;
			browse(pop.getHost(), pop.getSpecies());
		} else {
			for (final IAgent agent : agents) {
				final IPopulation<?> agentPop = agent.getPopulation();
				root = agentPop.getHost();
				if (root != null) {
					break;
				}
			}
			if (root == null) { return; }
			browse(root, agents);
		}
	}

	public static void browse(final IMacroAgent root, final Collection<? extends IAgent> agents) {
		final IMacroAgent realRoot = findRootOf(root, agents);
		if (realRoot == null) {
			GamaRuntimeException.error("Impossible to find a common host agent for " + agents, root.getScope());
			return;
		}
		new InspectDisplayOutput(realRoot, agents).launch(realRoot.getScope());
	}

	private static IMacroAgent findRootOf(final IMacroAgent root, final Collection<? extends IAgent> agents) {
		if (agents instanceof IPopulation) { return ((IPopulation<? extends IAgent>) agents).getHost(); }
		IMacroAgent result = null;
		for (final IAgent a : agents) {
			if (result == null) {
				result = a.getHost();
			} else {
				if (a.getHost() != result) { return null; }
			}
		}
		return result;

	}

	public static void browse(final IMacroAgent root, final ISpecies species) {
		if (root instanceof IExperimentAgent && species instanceof GamlModelSpecies) {
			// special case to be able to browse simulations, as their species is not contained in the experiment
			// species
			new InspectDisplayOutput(root, species).launch(root.getScope());
			return;
		}
		if (!root.getSpecies().getMicroSpecies().contains(species)) {
			if (root instanceof ExperimentAgent) {
				final IMacroAgent realRoot = ((ExperimentAgent) root).getSimulation();
				browse(realRoot, species);
			} else {
				GamaRuntimeException.error("Agent " + root + " has no access to populations of " + species.getName(),
						root.getScope());
			}
			return;
		}
		new InspectDisplayOutput(root, species).launch(root.getScope());
	}

	public static void browse(final IMacroAgent root, final IExpression expr) {
		final SpeciesDescription species = expr.getGamlType().isContainer()
				? expr.getGamlType().getContentType().getSpecies() : expr.getGamlType().getSpecies();
		if (species == null) {
			GamaRuntimeException.error("Expression '" + expr.serialize(true) + "' does not reference agents",
					root.getScope());
			return;
		}
		final ISpecies rootSpecies = root.getSpecies();
		if (rootSpecies.getMicroSpecies(species.getName()) == null) {
			if (root instanceof ExperimentAgent) {
				final IMacroAgent realRoot = ((ExperimentAgent) root).getSimulation();
				browse(realRoot, expr);
			} else {
				GamaRuntimeException.error("Agent " + root + " has no access to populations of " + species.getName(),
						root.getScope());
			}
			return;
		}
		new InspectDisplayOutput(root, expr).launch(root.getScope());
	}

	public static void browseSimulations(final ExperimentAgent host) {
		new InspectDisplayOutput(host).launch(host.getScope());

	}

}
