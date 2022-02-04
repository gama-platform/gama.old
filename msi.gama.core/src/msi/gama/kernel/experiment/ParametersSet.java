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
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.file.GamaFile;

/**
 * The Class ParametersSet.
 */
@SuppressWarnings({ "rawtypes" })
public class ParametersSet {

	/** The fitness. */
	private Double fitness;
	
	/** The current index. */
	private int currentIndex;
	
	private GamaMap<String, Object> elements;
	
	
	/**
	 * Instantiates a new parameters set.
	 */
	public ParametersSet() {
		fitness = Double.NaN;
		currentIndex = 0;
		elements = (GamaMap<String, Object>) GamaMapFactory.create();
	}

	/**
	 * Instantiates a new parameters set.
	 *
	 * @param solution the solution
	 */
	public ParametersSet(final ParametersSet solution) {
		elements = (GamaMap<String, Object>) GamaMapFactory.create();
		elements.putAll(solution.getElements());
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

		elements = (GamaMap<String, Object>) GamaMapFactory.create();
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
		elements = (GamaMap<String, Object>) GamaMapFactory.create();
		for (final IParameter p : parameters) {
			if (reinit && p instanceof IParameter.Batch) {
				((IParameter.Batch) p).reinitRandomly(scope);
			}
			put(p.getName(), p.value(scope));
		}
		fitness = Double.NaN;
		currentIndex = 0;
	}

	public Object put(final String s, final Object o) {
		// Special case for files as they are not invariant. Their contents must
		// be invalidated before they are loaded
		// again in a simulation. See Issue 812.
		if (o instanceof GamaFile) {
			((GamaFile) o).invalidateContents();
		}
		return elements.put(s, o);
	}
	
	public void putAll(final ParametersSet sol) {
		elements.putAll(sol.elements);
	}

	public Object get(final String s) {
		return elements.get(s);
	}
	public GamaMap<String, Object> getElements() {
		return elements;
	}
	
	public Set<Map.Entry<String, Object>> entrySet() {
		return elements.entrySet();
	}
	
	public Set<String> keySet() {
		return elements.keySet();
	}
	
	public boolean containsKey(String k ) {
		return elements.containsKey(k);
	}
	
	public int size() {
		return elements.size();
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(elements);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParametersSet other = (ParametersSet) obj;
		return Objects.equals(elements, other.elements);
	}

	
}
