/*******************************************************************************************************
 *
 * ParametersSet.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.Collection;
import java.util.Map;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.file.GamaFile;
import msi.gaml.types.Types;

/**
 * The Class ParametersSet.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class ParametersSet extends GamaMap<String, Object> {

	/**
	 * Instantiates a new parameters set. A GamaMap with some specialized constructors and a specialisation of put(..)
	 */
	public ParametersSet() {
		super(10, Types.STRING, Types.NO_TYPE);
	}

	/**
	 * Instantiates a new parameters set.
	 *
	 * @param solution
	 *            the solution
	 */
	public ParametersSet(final ParametersSet solution) {
		this();
		putAll(solution);
	}

	/**
	 * Instantiates a new parameters set.
	 *
	 * @param scope
	 *            the scope
	 * @param variables
	 *            the variables
	 * @param reinit
	 *            the reinit
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public ParametersSet(final IScope scope, final Map<String, IParameter> variables, final boolean reinit)
			throws GamaRuntimeException {
		this();
		for (final String var : variables.keySet()) {
			final IParameter varBat = variables.get(var);
			if (reinit && varBat instanceof IParameter.Batch) { ((IParameter.Batch) varBat).reinitRandomly(scope); }
			put(var, varBat.value(scope));
		}

	}

	/**
	 * Instantiates a new parameters set.
	 *
	 * @param scope
	 *            the scope
	 * @param parameters
	 *            the parameters
	 * @param reinit
	 *            the reinit
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public ParametersSet(final IScope scope, final Collection<? extends IParameter> parameters, final boolean reinit)
			throws GamaRuntimeException {
		this();
		for (final IParameter p : parameters) {
			if (reinit && p instanceof IParameter.Batch) { ((IParameter.Batch) p).reinitRandomly(scope); }
			put(p.getName(), p.value(scope));
		}
	}

	@Override
	public Object put(final String s, final Object o) {
		// Special case for files as they are not invariant. Their contents must
		// be invalidated before they are loaded
		// again in a simulation. See Issue 812.
		if (o instanceof GamaFile) { ((GamaFile) o).invalidateContents(); }
		return super.put(s, o);
	}

}
