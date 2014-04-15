/*********************************************************************************************
 * 
 * 
 * 'Neighborhood.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.batch;

import java.util.List;
import msi.gama.kernel.experiment.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public abstract class Neighborhood {

	protected final List<IParameter.Batch> variables;

	public Neighborhood(final List<IParameter.Batch> variables) {
		this.variables = variables;
	}

	public abstract List<ParametersSet> neighbor(IScope scope, final ParametersSet solution)
		throws GamaRuntimeException;

}
