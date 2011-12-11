/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gaml.batch;

import java.util.*;
import msi.gama.interfaces.IParameter;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;

/**
 * The Class Solution.
 */
public class Solution extends HashMap<String, Object> {

	public Solution(final Map<String, IParameter> variables, final boolean reinit)
		throws GamaRuntimeException {
		for ( final String var : variables.keySet() ) {
			final IParameter.Batch varBat = (IParameter.Batch) variables.get(var);
			if ( reinit ) {
				varBat.reinitRandomly();
			}
			put(var, varBat.value(GAMA.getDefaultScope()));
		}
	}

	public Solution(final List<? extends IParameter> parameters, final boolean reinit)
		throws GamaRuntimeException {
		for ( IParameter p : parameters ) {
			if ( reinit && p instanceof IParameter.Batch ) {
				((IParameter.Batch) p).reinitRandomly();
			}
			put(p.getName(), p.value(GAMA.getDefaultScope()));
		}
	}

	public Solution() {
		super();
	}

	public Solution(final Solution solution) {
		this.putAll(solution);
	}

}
