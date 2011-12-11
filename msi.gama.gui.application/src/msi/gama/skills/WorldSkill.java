/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.skills;

import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;

/**
 * Written by drogoul Modified on 24 juin 2010
 * 
 * @todo Description
 * 
 */

@vars({ @var(name = GAMA._FATAL, type = IType.BOOL_STR, init = "false"),
	@var(name = GAMA._WARNINGS, type = IType.BOOL_STR, init = "false"),
	@var(name = ISymbol.TIME, type = IType.INT_STR),
	@var(name = ISymbol.SEED, type = IType.FLOAT_STR),
	@var(name = ISymbol.RNG, type = IType.STRING_STR, init = "'" + ISymbol.MERSENNE + "'"),
	@var(name = ISymbol.STEP, type = IType.INT_STR, constant = true),
	@var(name = GAMA._UQR, type = IType.BOOL_STR, init = "true"),
	@var(name = WorldSkill.STEP_LENGTH, type = IType.STRING_STR) })
@skill({ ISymbol.GLOBAL, ISymbol.WORLD_SPECIES_NAME })
@species(ISymbol.WORLD_SPECIES_NAME)
@SuppressWarnings("static-method")
public class WorldSkill extends GeometricSkill {

	public static final String STEP_LENGTH = "step_length";

	@getter(var = STEP_LENGTH)
	public String getStepLength(final IAgent agent) {
		return Double.toString(agent.getSimulation().getScheduler().getStepLength());
	}

	@getter(var = ISymbol.TIME)
	public int getTime(final IAgent agent) {
		return agent.getSimulation().getScheduler().getTime();
	}

	@setter(ISymbol.TIME)
	public void setTime(final IAgent agent, final int t) {
		agent.getSimulation().getScheduler().setTime(t);
	}

	@getter(var = GAMA._UQR, initializer = true)
	public Boolean getQualityRendering(final IAgent agent) {
		return GAMA.USE_QUALITY_RENDERING;
	}

	@setter(GAMA._UQR)
	public void setQualityRendering(final IAgent agent, final boolean t) {
		GAMA.USE_QUALITY_RENDERING = t;
	}

	@getter(var = GAMA._FATAL, initializer = true)
	public Boolean getFatalErrors(final IAgent agent) {
		return GAMA.TREAT_ERRORS_AS_FATAL;
	}

	@setter(GAMA._FATAL)
	public void setFatalErrors(final IAgent agent, final boolean t) {
		GAMA.TREAT_ERRORS_AS_FATAL = t;
	}

	@getter(var = GAMA._WARNINGS, initializer = true)
	public Boolean getWarningsAsErrors(final IAgent agent) {
		return GAMA.TREAT_WARNINGS_AS_ERRORS;
	}

	@setter(GAMA._WARNINGS)
	public void setWarningsAsErrors(final IAgent agent, final boolean t) {
		GAMA.TREAT_WARNINGS_AS_ERRORS = t;
	}

	@getter(var = ISymbol.STEP)
	public int getTimeStep(final IAgent agent) {
		return agent.getSimulation().getScheduler().getStep();
	}

	@setter(ISymbol.STEP)
	public void setTimeStep(final IAgent agent, final int t) {
		agent.getSimulation().getScheduler().setStep(t);
	}

	@getter(var = ISymbol.SEED, initializer = true)
	public Double getSeed(final IAgent agent) {
		return (double) GAMA.getRandom().getSeed();
	}

	@setter(ISymbol.SEED)
	public void setSeed(final IAgent agent, final Double s) {
		GAMA.getRandom().setSeed(s);
	}

	@getter(var = ISymbol.RNG, initializer = true)
	public String getRng(final IAgent agent) {
		return GAMA.getRandom().getGeneratorName();
	}

	@setter(ISymbol.RNG)
	public void setRng(final IAgent agent, final String newRng) {
		GAMA.getRandom().setGenerator(newRng);
	}

	@action("pause")
	@args({})
	public Object primPause(final IScope scope) {
		if ( !getSimulation(scope).isPaused() ) {
			getSimulation(scope).pause();
		}
		return null;
	}

	@action("halt")
	@args({})
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
