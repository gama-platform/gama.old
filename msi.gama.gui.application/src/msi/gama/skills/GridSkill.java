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

import msi.gama.environment.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.*;
import msi.gama.util.matrix.GamaSpatialMatrix;

/**
 * Written by drogoul Modified on 24 juin 2010
 * 
 * @todo Description
 * 
 */
@vars({ @var(name = GridSkill.COLOR, type = IType.COLOR_STR),
	@var(name = GridSkill.AGENTS, type = IType.LIST_STR),
	@var(name = GridSkill.GRID_X, type = IType.INT_STR, constant = true),
	@var(name = GridSkill.GRID_Y, type = IType.INT_STR, constant = true) })
@skill({ GridSkill.SKILL_NAME })
public class GridSkill extends GeometricSkill {

	public static final String SKILL_NAME = "grid";

	public static final String NEIGHBOURS = "neighbours";

	public static final String COLOR = "color";

	public static final String AGENTS = "agents";

	public static final String GRID_X = "grid_x";

	public static final String GRID_Y = "grid_y";

	private final GamaSpatialMatrix getGrid(final IAgent agent) {
		return (GamaSpatialMatrix) agent.getPopulation().getTopology().getPlaces();
	}

	@getter(var = "agents")
	public final GamaList<IAgent> getAgents(final IAgent agent) {
		ModelEnvironment modelEnv = GAMA.getExperiment().getModelEnvironment();
		GamaList<IAgent> agents =
			modelEnv.getSpatialIndex()
				.allInEnvelope(agent.getGeometry(), agent.getEnvelope(), Different.with(), false)
				.clone();
		return agents;

		// TODO Remove this (to consider instead "agents_in" or "agents_intersecting")
	}

	@getter(var = "grid_x")
	public final int getX(final IAgent agent) {
		return getGrid(agent).getX(agent.getLocation().x);
	}

	@getter(var = "grid_y")
	public final int getY(final IAgent agent) {
		return getGrid(agent).getY(agent.getLocation().y);
	}

	@setter("grid_x")
	public final void setX(final IAgent agent, final Integer i) {

	}

	@setter("grid_y")
	public final void setY(final IAgent agent, final Integer i) {

	}

	@setter("agents")
	public final void setAgents(final IAgent agent, final GamaList agents) {

	}

	@getter(var = "color")
	public final GamaColor getColor(final IAgent agent) {
		return getGrid(agent).getColor(agent.getLocation());
	}

	@setter("color")
	public final void setColor(final IAgent agent, final GamaColor color) {
		getGrid(agent).setColor(agent.getLocation(), color);
	}

}
