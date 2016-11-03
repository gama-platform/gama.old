/*********************************************************************************************
 *
 * 'PlatformAgent.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.kernel.root;

import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.IOutputManager;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.IType;

@species (
		name = "platform",
		internal = true,
		doc = { @doc ("The species of the unique platform agent, called 'gama'") })
@vars ({ @var (
		name = SimulationAgent.MACHINE_TIME,
		type = IType.FLOAT,
		doc = @doc (
				value = "Returns the current system time in milliseconds",
				comment = "The return value is a float number")),
		@var (
				name = ExperimentAgent.WORKSPACE_PATH,
				type = IType.STRING,
				constant = true,
				doc = @doc (
						value = "Contains the absolute path to the workspace of GAMA. Can be used to list all the projects and files present in the platform",
						comment = "Always terminated with a trailing separator",
						see = { "workspace" })),
		@var (
				name = "workspace",
				type = IType.FILE,
				constant = true,
				doc = @doc (
						value = "A folder representing the workspace of GAMA. Can be used to list all the projects and files present in the platform",
						see = { "workspace_path" })), })
public class PlatformAgent extends GamlAgent implements ITopLevelAgent {

	public PlatformAgent(final IPopulation<? extends IAgent> s) {
		super(s);
	}

	@Override
	public SimulationClock getClock() {
		return new SimulationClock(getScope());
	}

	@Override
	public GamaColor getColor() {
		return GamaColor.NamedGamaColor.colors.get("gamaorange");
	}

	@Override
	public RandomUtils getRandomGenerator() {
		return new RandomUtils();
	}

	@Override
	public IOutputManager getOutputManager() {
		if (getExperiment() != null)
			return getExperiment().getOutputManager();
		return null;
	}

	@Override
	public void postEndAction(final IExecutable executable) {}

	@Override
	public void postDisposeAction(final IExecutable executable) {}

	@Override
	public void postOneShotAction(final IExecutable executable) {}

	@Override
	public void executeAction(final IExecutable executable) {}

	@Override
	public boolean isOnUserHold() {
		return false;
	}

	@Override
	public void setOnUserHold(final boolean state) {}

	@Override
	public SimulationAgent getSimulation() {
		return GAMA.getSimulation();
	}

	@Override
	public IExperimentAgent getExperiment() {
		if (GAMA.getExperiment() != null)
			return GAMA.getExperiment().getAgent();
		return null;
	}

}
