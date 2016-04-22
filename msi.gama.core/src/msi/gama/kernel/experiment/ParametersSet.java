/*********************************************************************************************
 * 
 * 
 * 'ParametersSet.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;

/**
 * The Class ParametersSet.
 */
public class ParametersSet extends HashMap<String, Object> {

	public ParametersSet() {
	}

	public ParametersSet(final ParametersSet solution) {
		this.putAll(solution);
	}

	public ParametersSet(final IScope scope, final Map<String, IParameter> variables, final boolean reinit)
			throws GamaRuntimeException {

		for (final String var : variables.keySet()) {
			final IParameter varBat = variables.get(var);
			if (reinit && varBat instanceof IParameter.Batch) {
				((IParameter.Batch) varBat).reinitRandomly(scope);
			}
			put(var, varBat.value(scope));
		}

	}

	public ParametersSet(final IScope scope, final Collection<? extends IParameter> parameters, final boolean reinit)
			throws GamaRuntimeException {
		for (final IParameter p : parameters) {
			if (reinit && p instanceof IParameter.Batch) {
				((IParameter.Batch) p).reinitRandomly(scope);
			}
			put(p.getName(), p.value(scope));
		}
	}

	@Override
	public Object put(final String s, final Object o) {
		// Special case for files as they are not invariant. Their contents must
		// be invalidated before they are loaded
		// again in a simulation. See Issue 812.
		if (o instanceof GamaFile) {
			((GamaFile) o).invalidateContents();
		}
		return super.put(s, o);
	}

}
