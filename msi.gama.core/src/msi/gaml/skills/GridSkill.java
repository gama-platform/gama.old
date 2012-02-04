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

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.*;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 24 juin 2010
 * 
 * @todo Description
 * 
 */
@vars({ @var(name = IKeyword.COLOR, type = IType.COLOR_STR),
	@var(name = IKeyword.AGENTS, type = IType.LIST_STR),
	@var(name = IKeyword.GRID_X, type = IType.INT_STR, constant = true),
	@var(name = IKeyword.GRID_Y, type = IType.INT_STR, constant = true) })
@skill({ GridSkill.SKILL_NAME })
public class GridSkill extends GeometricSkill {

	public static final String SKILL_NAME = "grid";

	private final GamaSpatialMatrix getGrid(final IAgent agent) {
		return (GamaSpatialMatrix) agent.getPopulation().getTopology().getPlaces();
	}

	@getter(var = "agents")
	public final List<IAgent> getAgents(final IAgent agent) {
		return agent.getTopology().getAgentsIn(agent.getGeometry(), Different.with(), false);

		// TODO Remove this (to consider instead "agents_in" or "agents_intersecting")
	}

	@getter(var = "grid_x")
	public final int getX(final IAgent agent) {
		return getGrid(agent).getX(agent.getLocation().getX());
	}

	@getter(var = "grid_y")
	public final int getY(final IAgent agent) {
		return getGrid(agent).getY(agent.getLocation().getY());
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
