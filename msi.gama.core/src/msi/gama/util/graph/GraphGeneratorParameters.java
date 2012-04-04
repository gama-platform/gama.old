package msi.gama.util.graph;

import java.io.File;

import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.species.ISpecies;

/**
 * Parameters for graph generators. 
 * Stores these parameters, ensures their integrity, displays them, enables simple 
 * transmission of them and facilities the development of more generic code. 
 * Especially of use for parsing parameters provided as Gama maps (because of a language limitation on 
 * the number of parameters for an operator)
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphGeneratorParameters {

	public final ISpecies specyEdges;
	public final ISpecies specyVertices;
	
	
	public final static String PARAMETER_SPECYEDGES_STR = "edges_specy";
	public final static String PARAMETER_SPECYNODES_STR = "vertices_specy";
	
	/**
	 * Creates parameter with these values.
	 * @param specyEdges
	 * @param specyVertices
	 * @param filename
	 * @param file
	 * @throws GamaRuntimeException
	 */
	public GraphGeneratorParameters(ISpecies specyEdges, ISpecies specyVertices) throws GamaRuntimeException {
		super();
		this.specyEdges = specyEdges;
		this.specyVertices = specyVertices;
		myEnsureIntegrity();
	}
	
	/**
	 * Creates parameters and initializes values from the map
	 * provided as parameter. Throws an exception if 
	 * parameters are not valid
	 * @param gamaMap
	 */
	public GraphGeneratorParameters(GamaMap gamaMap) throws GamaRuntimeException {
		
		try {
			specyEdges = (ISpecies) gamaMap.get(PARAMETER_SPECYEDGES_STR);
		} catch (RuntimeException e) {
			throw new GamaRuntimeException("parameter "+PARAMETER_SPECYEDGES_STR+" should be a specy");
		}
		try {
			specyVertices = (ISpecies) gamaMap.get(PARAMETER_SPECYNODES_STR);
		} catch (RuntimeException e) {
			throw new GamaRuntimeException("parameter "+PARAMETER_SPECYNODES_STR+" should be a specy");
		}
		myEnsureIntegrity();
	}
	
	
	/**
	 * Ensures the integrity of parameters (all values provided, etc.)
	 * @throws GamaRuntimeException
	 */
	private final void myEnsureIntegrity() throws GamaRuntimeException {
		
		ensureNotNull(PARAMETER_SPECYEDGES_STR, specyEdges);
		ensureNotNull(PARAMETER_SPECYNODES_STR, specyVertices);
		
	}
	
	/**
	 * Ensures the integrity of parameters (all values provided, etc.).
	 * Should be overloaded by inherited classes.
	 * @throws GamaRuntimeException
	 */
	protected void ensureIntegrity() throws GamaRuntimeException {
		
		myEnsureIntegrity();
		
	}
	
	/**
	 * Enqueues the values of parameters for constructing efficiently the toString representation.
	 * Should be overrident by children.
	 * @param sb
	 */
	protected void enqueueToString(StringBuffer sb) {
		sb
			.append(PARAMETER_SPECYEDGES_STR).append("=").append(specyEdges).append(", ")
			.append(PARAMETER_SPECYNODES_STR).append("=").append(specyVertices)
			;
	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		enqueueToString(sb);
		return sb.toString();
	}
	

	protected static void ensureNotNull(String parameterStr, Object variable) {
		if (variable == null)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be provided");
		
	}
	
	protected static void ensurePositive(String parameterStr, Integer variable) {
		if (variable < 0)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be >= 0");
		
	}
	
	protected static void ensurePositive(String parameterStr, Double variable) {
		if (variable < 0)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be >= 0");
		
	}
	
	protected static void ensurePositive(String parameterStr, Float variable) {
		if (variable < 0)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be >= 0");
		
	}
	
	protected static void ensureLower(String parameterStr, Integer max, Integer variable) {
		if (variable >= max)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be < "+max);
		
	}
	
	protected static void ensureLower(String parameterStr, Double max, Double variable) {
		if (variable >= max)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be < "+max);
		
	}
	
	protected static void ensureLower(String parameterStr, Float max, Float  variable) {
		if (variable >= max)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be < "+max);
		
	}
	
	protected static void ensureLowerEq(String parameterStr, Integer max, Integer variable) {
		if (variable > max)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be < "+max);
		
	}
	
	protected static void ensureLowerEq(String parameterStr, Double max, Double variable) {
		if (variable > max)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be < "+max);
		
	}
	
	protected static void ensureLowerEq(String parameterStr, Float max, Float  variable) {
		if (variable > max)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be < "+max);
		
	}
	
	protected static void ensureGreater(String parameterStr, Integer min, Integer variable) {
		if (variable <= min)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be > "+min);
		
	}
	
	protected static void ensureGreater(String parameterStr, Double min, Double variable) {
		if (variable <= min)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be > "+min);
		
	}
	
	protected static void ensureGreater(String parameterStr, Float min, Float  variable) {
		if (variable <= min)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be > "+min);
		
	}
	
	protected static void ensureGreaterEq(String parameterStr, Integer min, Integer variable) {
		if (variable < min)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be > "+min);
		
	}
	
	protected static void ensureGreaterEq(String parameterStr, Double min, Double variable) {
		if (variable < min)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be > "+min);
		
	}
	
	protected static void ensureGreaterEq(String parameterStr, Float min, Float  variable) {
		if (variable < min)
			throw new GamaRuntimeException("parameter "+parameterStr+" should be > "+min);
		
	}
	
	protected static Integer castParamInteger(GamaMap gamaMap, String parameterStr) {
		try {
			return (Integer) gamaMap.get(parameterStr);
		} catch (RuntimeException e) {
			throw new GamaRuntimeException("parameter "+parameterStr+" should be an integer value");
		}
		
	}
	
	protected static Double castParamDouble(GamaMap gamaMap, String parameterStr) {
		try {
			return (Double) gamaMap.get(parameterStr);
		} catch (RuntimeException e) {
			throw new GamaRuntimeException("parameter "+parameterStr+" should be a double value");
		}
		
	}
	
}
