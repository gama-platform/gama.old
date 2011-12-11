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
package msi.gama.interfaces;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import msi.gama.gui.graphics.IGraphics;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 14 nov. 2010
 * 
 * @todo Description
 * 
 */
public interface IAspect {

	public static IAspect DEFAULT_ASPECT = new IAspect() {

		@Override
		public Rectangle2D draw(final IScope scope, final IAgent agent) throws GamaRuntimeException {
			GamaColor c = null;
			if ( agent.getSpecies().hasVar(ISymbol.COLOR) ) {
				c = Cast.asColor(scope.getAgentVarValue(agent, ISymbol.COLOR));
			}
			return ((IGraphics) scope.getContext()).drawGeometry(agent.getGeometry()
				.getInnerGeometry(), c == null ? Color.YELLOW : c, true, 0);
		}

	};

	public Rectangle2D draw(final IScope scope, final IAgent agent) throws GamaRuntimeException;

}
