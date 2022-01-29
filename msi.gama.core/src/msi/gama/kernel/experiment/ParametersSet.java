/*******************************************************************************************************
 *
 * ParametersSet.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

	/** The fitness. */
	private Double fitness;
	
	/** The current index. */
	private int currentIndex;
	
	/**
	 * Instantiates a new parameters set.
	 */
	public ParametersSet() {
		fitness = Double.NaN;
		currentIndex = 0;
	}

	/**
	 * Instantiates a new parameters set.
	 *
	 * @param solution the solution
	 */
	public ParametersSet(final ParametersSet solution) {
		this.putAll(solution);
		fitness = solution.fitness;
		currentIndex = solution.currentIndex;
	}

	/**
	 * Instantiates a new parameters set.
	 *
	 * @param scope the scope
	 * @param variables the variables
	 * @param reinit the reinit
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Instantiates a new parameters set.
	 *
	 * @param scope the scope
	 * @param parameters the parameters
	 * @param reinit the reinit
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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
