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
package msi.gaml.commands;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.compilation.ISymbolKind;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = { IKeyword.ASPECT }, kind = ISymbolKind.BEHAVIOR)
@inside(kinds = { ISymbolKind.SPECIES })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = true) }, omissible = IKeyword.NAME)
public class AspectCommand extends AbstractCommandSequence implements IAspect {

	public static IAspect DEFAULT_ASPECT = new IAspect() {

		@Override
		public Rectangle2D draw(final IScope scope, final IAgent agent) throws GamaRuntimeException {
			GamaColor c = null;
			if ( agent.getSpecies().hasVar(IKeyword.COLOR) ) {
				c = Cast.asColor(scope, scope.getAgentVarValue(agent, IKeyword.COLOR));
			}
			Rectangle2D r =
				((IGraphics) scope.getContext()).drawGeometry(agent.getGeometry()
					.getInnerGeometry(), c == null ? Color.YELLOW : c, true, 0);
			// GuiUtils.debug("Agent " + agent.getIndex() + " with X ratio " +
			// agent.getLocation().getX() / r.getCenterX() + " and Y ratio " +
			// agent.getLocation().getY() / r.getCenterY());
			return r;

		}

	};

	public AspectCommand(final IDescription desc) {
		super(desc);
		setName(getLiteral(IKeyword.NAME, IKeyword.DEFAULT));
	}

	@Override
	public Rectangle2D draw(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		if ( agent != null && agent.acquireLock() ) {
			// synchronized (agent) {
			Object result;
			try {
				result = scope.execute(this, agent);
			} finally {
				agent.releaseLock();
			}
			return (Rectangle2D) result;
			// }
		}
		return null;

	}

	@Override
	public Rectangle2D privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		Rectangle2D result = null;
		for ( int i = 0; i < commands.length; i++ ) {
			Object c = commands[i].executeOn(stack);
			if ( result != null ) {
				if ( c instanceof Rectangle2D ) {
					result = result.createUnion((Rectangle2D) c);
				}
			} else if ( c instanceof Rectangle2D ) {
				result = (Rectangle2D) c;
			}
		}
		return result;
	}
}
