/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.commands;

import java.awt.geom.Rectangle2D;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;

@symbol(name = { ISymbol.ASPECT }, kind = ISymbolKind.BEHAVIOR)
@inside(kinds = { ISymbolKind.SPECIES })
@facets(value = { @facet(name = ISymbol.WHEN, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.NAME, type = IType.ID, optional = true) })
public class AspectCommand extends AbstractCommandSequence implements IAspect {

	public AspectCommand(final IDescription desc) {
		super(desc);
		setName(getLiteral(ISymbol.NAME, ISymbol.DEFAULT));
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
