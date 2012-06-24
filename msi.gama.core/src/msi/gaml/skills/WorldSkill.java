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
package msi.gaml.skills;

import java.net.URL;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import org.eclipse.core.runtime.Platform;

/**
 * Written by drogoul Modified on 24 juin 2010
 * 
 * @todo Description
 * 
 */

@vars({
	@var(name = GAMA._FATAL, type = IType.BOOL_STR, init = "false"),
	@var(name = GAMA._WARNINGS, type = IType.BOOL_STR, init = "false"),
	@var(name = IKeyword.TIME, type = IType.FLOAT_STR, doc = @doc(value = "Represents the total time passed, in model time, since the beginning of the simulation", comment = "Equal to cycle * step if the user does not arbitrarily initialize it.")),
	@var(name = IKeyword.SEED, type = IType.FLOAT_STR, doc = @doc(value = "The seed of the random number generator", comment = "Each time it is set, the random number generator is reinitialized")),
	@var(name = WorldSkill.CYCLE, type = IType.INT_STR, doc = @doc("Returns the current cycle of the simulation")),
	@var(name = IKeyword.RNG, type = IType.STRING_STR, init = "'" + IKeyword.MERSENNE + "'", doc = @doc("The random number generator to use for this simulation. Four different ones are at the disposal of the modeler: " +
		IKeyword.MERSENNE +
		" represents the default generator, based on the Mersenne-Twister algorithm. Very reliable; " +
		IKeyword.CELLULAR +
		" is a cellular automaton based generator that should be a bit faster, but less reliable; " +
		IKeyword.XOR +
		" is another choice. Much faster than the previous ones, but with short sequences; and " +
		IKeyword.JAVA + " invokes the standard Java generator")),
	@var(name = IKeyword.STEP, type = IType.FLOAT_STR, doc = @doc(value = "Represents the value of the interval, in model time, between two simulation cycles", comment = "If not set, its value is equal to 1.0 and, since the default time unit is the second, to 1 second")),
	@var(name = WorldSkill.MACHINE_TIME, type = IType.FLOAT_STR, doc = @doc(value = "Returns the current system time in milliseconds", comment = "The return value is a float number")),

	@var(name = WorldSkill.DURATION, type = IType.STRING_STR, doc = @doc("Returns a string containing the duration, in milliseconds, of the previous simulation cycle")),
	@var(name = WorldSkill.TOTAL_DURATION, type = IType.STRING_STR, doc = @doc("Returns a string containing the total duration, in milliseconds, of the simulation since it has been launched ")),
	@var(name = WorldSkill.AVERAGE_DURATION, type = IType.STRING_STR, doc = @doc("Returns a string containing the average duration, in milliseconds, of a simulation cycle.")),
	@var(name = WorldSkill.MODEL_PATH, type = IType.STRING_STR, constant = true, doc = @doc(value = "Contains the absolute path to the folder in which the current model is located", comment = "Always terminated with a trailing separator")),
	@var(name = WorldSkill.WORKSPACE_PATH, type = IType.STRING_STR, constant = true, doc = @doc(value = "Contains the absolute path to the workspace of GAMA", comment = "Always terminated with a trailing separator")),
	@var(name = WorldSkill.PROJECT_PATH, type = IType.STRING_STR, constant = true, doc = @doc(value = "Contains the absolute path to the project in which the current model is located", comment = "Always terminated with a trailing separator")) })
@skill(name = IKeyword.GLOBAL, attach_to = IKeyword.WORLD_SPECIES)
public class WorldSkill extends GeometricSkill {

	public static final String CYCLE = "cycle";
	public static final String DURATION = "duration";
	public static final String MACHINE_TIME = "machine_time";
	public static final String TOTAL_DURATION = "total_duration";
	public static final String AVERAGE_DURATION = "average_duration";
	public static final String MODEL_PATH = "model_path";
	public static final String WORKSPACE_PATH = "workspace_path";
	public static final String PROJECT_PATH = "project_path";

	@getter(MODEL_PATH)
	public String getModelPath(final IAgent agent) {
		return GAMA.getModel().getFolderPath() + "/";
	}

	@getter(value = WORKSPACE_PATH, initializer = true)
	public String getWorkspacePath(final IAgent agent) {
		URL url = Platform.getInstanceLocation().getURL();
		return url.getPath();
	}

	@getter(PROJECT_PATH)
	public String getProjectPath(final IAgent agent) {
		return GAMA.getModel().getProjectPath() + "/";
	}

	@getter(DURATION)
	public String getDuration(final IAgent agent) {
		return Long.toString(SimulationClock.getDuration());
	}

	@getter(TOTAL_DURATION)
	public String getTotalDuration(final IAgent agent) {
		return Long.toString(SimulationClock.getTotalDuration());
	}

	@getter(AVERAGE_DURATION)
	public String getAverageDuration(final IAgent agent) {
		return Double.toString(SimulationClock.getAverageDuration());
	}

	@getter(MACHINE_TIME)
	public Double getMachineTime(final IAgent agent) {
		return (double) System.currentTimeMillis();
	}

	@setter(MACHINE_TIME)
	public void setMachineTime(final IAgent agent, final Double t) throws GamaRuntimeException {
		// NOTHING
	}

	@getter(IKeyword.TIME)
	public double getTime(final IAgent agent) {
		return SimulationClock.getTime();
	}

	@setter(IKeyword.TIME)
	public void setTime(final IAgent agent, final double t) throws GamaRuntimeException {
		SimulationClock.setTime(t);
	}

	@getter(CYCLE)
	public Integer getCycle(final IAgent agent) {
		return SimulationClock.getCycle();
	}

	@getter(value = GAMA._FATAL, initializer = true)
	public Boolean getFatalErrors(final IAgent agent) {
		return SimulationClock.TREAT_ERRORS_AS_FATAL;
	}

	@setter(GAMA._FATAL)
	public void setFatalErrors(final IAgent agent, final boolean t) {
		SimulationClock.TREAT_ERRORS_AS_FATAL = t;
	}

	@getter(value = GAMA._WARNINGS, initializer = true)
	public Boolean getWarningsAsErrors(final IAgent agent) {
		return SimulationClock.TREAT_WARNINGS_AS_ERRORS;
	}

	@setter(GAMA._WARNINGS)
	public void setWarningsAsErrors(final IAgent agent, final boolean t) {
		SimulationClock.TREAT_WARNINGS_AS_ERRORS = t;
	}

	@getter(IKeyword.STEP)
	public double getTimeStep(final IAgent agent) {
		return SimulationClock.getStep();
	}

	@setter(IKeyword.STEP)
	public void setTimeStep(final IAgent agent, final double t) throws GamaRuntimeException {
		SimulationClock.setStep(t);
	}

	@getter(value = IKeyword.SEED, initializer = true)
	public Double getSeed(final IAgent agent) {
		return (double) GAMA.getRandom().getSeed();
	}

	@setter(IKeyword.SEED)
	public void setSeed(final IAgent agent, final Double s) {
		GAMA.getRandom().setSeed(s);
	}

	@getter(value = IKeyword.RNG, initializer = true)
	public String getRng(final IAgent agent) {
		return GAMA.getRandom().getGeneratorName();
	}

	@setter(IKeyword.RNG)
	public void setRng(final IAgent agent, final String newRng) {
		GAMA.getRandom().setGenerator(newRng);
	}

	@action(name = "pause", doc = @doc("Allows to pause the current simulation. It can be set to continue with the manual intervention of the user."))
	@args(names = {})
	public Object primPause(final IScope scope) {
		if ( !getSimulation(scope).isPaused() ) {
			getSimulation(scope).pause();
		}
		return null;
	}

	@action(name = "halt", doc = @doc("Allows to stop the current simulation. It cannot be continued after"))
	@args(names = {})
	public Object primHalt(final IScope scope) {
		if ( getSimulation(scope).isAlive() ) {
			getSimulation(scope)./* stop() */pause();
		}
		return null;
	}

	private ISimulation getSimulation(final IScope scope) {
		return scope.getSimulationScope();
	}

}
