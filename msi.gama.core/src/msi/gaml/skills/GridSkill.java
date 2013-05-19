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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.GamaColor;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 24 juin 2010
 * 
 * @todo Description
 * 
 */
@vars({
	@var(name = IKeyword.COLOR, type = IType.COLOR),
	// @var(name = IKeyword.AGENTS, type = IType.LIST, of = IType.AGENT, doc = @doc(deprecated =
	// "This variable is deprecated for grid agents. Use agents_inside(cell) or agents_overlapping(cell) instead")),
	@var(name = IKeyword.GRID_VALUE, type = IType.FLOAT),
	@var(name = IKeyword.GRID_X, type = IType.INT, constant = true),
	@var(name = IKeyword.GRID_Y, type = IType.INT, constant = true) })
@skill(name = GridSkill.SKILL_NAME)
public class GridSkill extends GeometricSkill {

	public static interface IGridAgent {

		public GamaColor getColor();

		public void setColor(final GamaColor color);

		public int getX();

		public int getY();

		public double getValue();

		public void setValue(final double d);
	}

	public static final String SKILL_NAME = "grid";

	protected final IGrid getGrid(final IAgent agent) {
		return (IGrid) agent.getPopulation().getTopology().getPlaces();
	}

	// @getter("agents")
	// @Deprecated
	// public final List<IAgent> getAgents(final IAgent agent) {
	// final List<IAgent> agents =
	// agent.getTopology().getAgentsIn(agent, new Not(In.population(agent.getPopulation())), false);
	// return agents;
	//
	// // TODO Remove this (to consider instead "agents_in" or "agents_intersecting")
	// }

	@getter("grid_x")
	public final int getX(final IAgent agent) {
		return ((IGridAgent) agent).getX();
	}

	@getter(value = "grid_value", initializer = true)
	public final double getValue(final IAgent agent) {
		return ((IGridAgent) agent).getValue();
	}

	@getter("grid_y")
	public final int getY(final IAgent agent) {
		return ((IGridAgent) agent).getY();
	}

	@setter("grid_x")
	public final void setX(final IAgent agent, final Integer i) {

	}

	@setter("grid_value")
	public final void setValue(final IAgent agent, final Double d) {
		((IGridAgent) agent).setValue(d);
	}

	@setter("grid_y")
	public final void setY(final IAgent agent, final Integer i) {

	}

	// @setter("agents")
	// public final void setAgents(final IAgent agent, final GamaList agents) {
	//
	// }

	@getter(value = "color", initializer = true)
	public GamaColor getColor(final IAgent agent) {
		return ((IGridAgent) agent).getColor();
	}

	@setter("color")
	public void setColor(final IAgent agent, final GamaColor color) {
		((IGridAgent) agent).setColor(color);
	}

	// @getter("shape")
	// public IShape getShape(final IAgent agent) {
	//
	// }

}
