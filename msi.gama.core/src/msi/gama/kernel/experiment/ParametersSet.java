/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.ParametersSet.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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
@SuppressWarnings({ "rawtypes" })
public class ParametersSet extends HashMap<String, Object> {

	private Double fitness;
	private int currentIndex;
	public ParametersSet() {
		fitness = Double.NaN;
		currentIndex = 0;
	}

	public ParametersSet(final ParametersSet solution) {
		this.putAll(solution);
		fitness = solution.fitness;
		currentIndex = solution.currentIndex;
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
		fitness = Double.NaN;
		currentIndex = 0;

	}

	public ParametersSet(final IScope scope, final Collection<? extends IParameter> parameters, final boolean reinit)
			throws GamaRuntimeException {
		for (final IParameter p : parameters) {
			if (reinit && p instanceof IParameter.Batch) {
				((IParameter.Batch) p).reinitRandomly(scope);
			}
			put(p.getName(), p.value(scope));
		}
		fitness = Double.NaN;
		currentIndex = 0;
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
