package msi.gama.util.graph;

import java.io.File;

import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.species.ISpecies;

/**
 * Parameters for graph generator that require a file (either filename or file).
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphGeneratorFromFileParameters extends GraphGeneratorParameters {

	public final String filename;

	public final File file;
	
	public final static String PARAMETER_FILENAME_STR = "filename";
	public final static String PARAMETER_FILE_STR = "file";

	public GraphGeneratorFromFileParameters(ISpecies specyEdges,
			ISpecies specyVertices, String filename, File file)
			throws GamaRuntimeException {
		
		super(specyEdges, specyVertices);
		this.filename = filename;
		this.file = file;
		myEnsureIntegrity();
	}

	public GraphGeneratorFromFileParameters(GamaMap gamaMap)
			throws GamaRuntimeException {
		super(gamaMap);
		
		try {
			filename = (String) gamaMap.get(PARAMETER_FILENAME_STR);
		} catch (RuntimeException e) {
			throw new GamaRuntimeException("parameter "+PARAMETER_FILENAME_STR+" should be a String");
		}
		try {
			file = (File) gamaMap.get(PARAMETER_FILE_STR);
		} catch (RuntimeException e) {
			throw new GamaRuntimeException("parameter "+PARAMETER_FILE_STR+" should be a file");
		}
		
		myEnsureIntegrity();
		
	}

	/**
	 * Ensures the integrity of parameters (all values provided, etc.)
	 * @throws GamaRuntimeException
	 */
	private final void myEnsureIntegrity() throws GamaRuntimeException {
		
		if ((file == null) && (filename == null))
			throw new GamaRuntimeException("either "+PARAMETER_FILE_STR+" or "+PARAMETER_FILENAME_STR+" should be provided");
		
	}
	
	@Override
	protected void enqueueToString(StringBuffer sb) {
		super.enqueueToString(sb);
		
		sb
			.append(PARAMETER_FILE_STR).append("=").append(file).append(", ")
			.append(PARAMETER_FILENAME_STR).append("=").append(filename).append(", ")
			;
	}
	
	@Override
	protected void ensureIntegrity() throws GamaRuntimeException {
	
		super.ensureIntegrity();
		
		myEnsureIntegrity();
		
	}
	
}
