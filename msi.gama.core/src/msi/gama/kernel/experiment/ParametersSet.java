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
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class ParametersSet.
 */
public class ParametersSet extends HashMap<String, Object> {

	public ParametersSet(final Map<String, IParameter> variables, final boolean reinit) throws GamaRuntimeException {
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				for ( final String var : variables.keySet() ) {
					final IParameter varBat = variables.get(var);
					if ( reinit && varBat instanceof IParameter.Batch ) {
						((IParameter.Batch) varBat).reinitRandomly();
					}
					put(var, varBat.value(scope));
				}
			}
		});

	}

	public ParametersSet(final Collection<? extends IParameter> parameters, final boolean reinit)
		throws GamaRuntimeException {
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				for ( IParameter p : parameters ) {
					if ( reinit && p instanceof IParameter.Batch ) {
						((IParameter.Batch) p).reinitRandomly();
					}
					put(p.getName(), p.value(scope));
				}
			}
		});

	}

	public ParametersSet() {
		super();
	}

	public ParametersSet(final ParametersSet solution) {
		this.putAll(solution);
	}

}
