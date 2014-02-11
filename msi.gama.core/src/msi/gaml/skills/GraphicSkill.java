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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.skills;

import java.util.Map;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.IGraph;
import msi.gama.util.path.*;
import msi.gaml.operators.*;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.skills.GridSkill.IGridAgent;
import msi.gaml.types.*;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

/**
 * GraphicSkill : This class is intended to define the minimal set of behaviours required from a
 * graphical agent. Each member that has a meaning in GAML is annotated with the
 * respective tags (vars, getter, setter, init, action & args)
 * 
 * @author Grignard Feb 2014
 */

@doc("The graphic skill is intended to define the minimal set of behaviours required from a "
	+ "graphical agent")
@vars({
	@var(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, init = "1.0", doc = @doc("the transparency of the agent (between 0.0 and 1.0)")),})
@skill(name = IKeyword.GRAPHIC_SKILL)
public class GraphicSkill extends Skill {


	@getter(IKeyword.TRANSPARENCY)
	public double getTransparency(final IAgent agent) {
		return (Double) agent.getAttribute(IKeyword.TRANSPARENCY);
	}

	@setter(IKeyword.TRANSPARENCY)
	public void setTransparency(final IAgent agent, final double s) {
		agent.setAttribute(IKeyword.TRANSPARENCY, s);
	}


	@action(name = "twinkle", args = {
		@arg(name = "period", type = IType.INT, doc = @doc("make the agent twinkle with a given period")) }, doc = @doc(examples = { "do twinkle period: 10;" }, value = ""))
	
	public void twinkle(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		double curTrans = getTransparency(agent);
		double  curStep = scope.getSimulationScope().getTime(scope, agent);
		Integer period = (Integer) scope.getArg("period", IType.INT);
		
		if(period == 0){
			period = 360;		
		}
		GuiUtils.informConsole("curTrans " + curTrans + "period " + period + "curStep" + curStep);
		curTrans = Math.abs(Math.cos((curStep*(180/period)*(Math.PI/180))));
		setTransparency(agent, curTrans);
		return;
	}

}
