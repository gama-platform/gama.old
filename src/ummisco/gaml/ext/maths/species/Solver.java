package ummisco.gaml.ext.maths.species;



import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

//@species(name = "Solver")
public class Solver extends GamlAgent {


	static final boolean DEBUG = false; // Change DEBUG = false for release version

	public Solver(final ISimulation sim, final IPopulation s) throws GamaRuntimeException {
		super(sim, s);
	}
	
//	@action(name="solve_equation")
//	@args(names = {})
	public Object helloWorld(final IScope scope) throws GamaRuntimeException {
		GuiUtils.informConsole("Hello World");
		return null;
	}





}
