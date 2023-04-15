/*******************************************************************************************************
 *
 * GamlFileExtension.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common;

/**
 * The Class GamlFileExtension.
 */
public class GamlFileExtension {

	/** The Constant GAML_FILE. */
	public final static String GAML_FILE = ".gaml";
	
	/** The Constant EXPERIMENT_FILE. */
	public final static String EXPERIMENT_FILE = ".experiment";
	
	/** The Constant MODEL_FILE. */
	public final static String MODEL_FILE = ".model";
	
	/** The Constant SPECIES_FILE. */
	public final static String SPECIES_FILE = ".species";

	/**
	 * Checks if is gaml.
	 *
	 * @param fileName the file name
	 * @return true, if is gaml
	 */
	public static final boolean isGaml(final String fileName) {
		return fileName != null && fileName.endsWith(GAML_FILE);
	}

	/**
	 * Checks if is experiment.
	 *
	 * @param fileName the file name
	 * @return true, if is experiment
	 */
	public static final boolean isExperiment(final String fileName) {
		return fileName != null && fileName.endsWith(EXPERIMENT_FILE);
	}

	/**
	 * Checks if is model.
	 *
	 * @param fileName the file name
	 * @return true, if is model
	 */
	public static final boolean isModel(final String fileName) {
		return fileName != null && fileName.endsWith(MODEL_FILE);
	}

	/**
	 * Checks if is species.
	 *
	 * @param fileName the file name
	 * @return true, if is species
	 */
	public static final boolean isSpecies(final String fileName) {
		return fileName != null && fileName.endsWith(SPECIES_FILE);
	}

	/**
	 * Checks if is any.
	 *
	 * @param fileName the file name
	 * @return true, if is any
	 */
	public static final boolean isAny(final String fileName) {
		return isGaml(fileName) || isExperiment(fileName) || isModel(fileName) || isSpecies(fileName);
	}
}
