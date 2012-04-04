package msi.gama.util.graph;

import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.species.ISpecies;

public class GraphGeneratorFromAlgorithmParameters extends
		GraphGeneratorParameters {

	public final Integer size;
	
	public final static String PARAMETER_SIZE_STR = "size";

	public GraphGeneratorFromAlgorithmParameters(ISpecies specyEdges,
			ISpecies specyVertices, Integer size) throws GamaRuntimeException {
		super(specyEdges, specyVertices);
		this.size = size;
		myEnsureIntegrity();
	}

	public GraphGeneratorFromAlgorithmParameters(GamaMap gamaMap)
			throws GamaRuntimeException {
		super(gamaMap);

		size = castParamInteger(gamaMap, PARAMETER_SIZE_STR);

		myEnsureIntegrity();

	}

	private final void myEnsureIntegrity() throws GamaRuntimeException {
		
		ensureNotNull(PARAMETER_SIZE_STR, size);
		ensurePositive(PARAMETER_SIZE_STR, size);
		
	}
	
	@Override
	protected void enqueueToString(StringBuffer sb) {
		super.enqueueToString(sb);
		
		sb
			.append(PARAMETER_SIZE_STR).append("=").append(size).append(", ")
			;
	}
	
	@Override
	protected void ensureIntegrity() throws GamaRuntimeException {
	
		super.ensureIntegrity();
		
		myEnsureIntegrity();
		
	}
}
