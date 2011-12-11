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
