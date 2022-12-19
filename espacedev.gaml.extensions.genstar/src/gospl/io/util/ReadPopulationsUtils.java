/*******************************************************************************************************
 *
 * ReadPopulationsUtils.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.io.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import core.configuration.dictionary.IGenstarDictionary;
import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.io.IGSSurvey;
import core.metamodel.value.IValue;
import core.util.exception.GenstarException;
import gospl.GosplPopulation;
import gospl.distribution.GosplInputDataManager;
import gospl.io.CsvInputHandler;
import gospl.io.GosplSurveyFactory;
import gospl.io.exception.InvalidSurveyFormatException;

/**
 * Utilities to read populations. Provides APIs for users to easily load population from files;
 *
 * @author Samuel Thiriot
 *
 */
public class ReadPopulationsUtils {

	/**
	 * Instantiates a new read populations utils.
	 */
	private ReadPopulationsUtils() {}

	/**
	 * Read from CSV file.
	 *
	 * @param filename
	 *            the filename
	 * @param dictionnary
	 *            the dictionnary
	 * @return the gospl population
	 */
	public static GosplPopulation readFromCSVFile(final String filename,
			final IGenstarDictionary<Attribute<? extends IValue>> dictionnary) {
		File fileCSV = new File(filename);

		char separator;
		try {
			separator = CsvInputHandler.detectSeparator(fileCSV);
		} catch (IOException e) {
			throw new GenstarException("error while trying to detect separator from file " + fileCSV, e);
		}

		return readFromCSVFile(filename, dictionnary, separator);

	}

	/**
	 * Read from CSV file.
	 *
	 * @param filename
	 *            the filename
	 * @param dictionnary
	 *            the dictionnary
	 * @param separator
	 *            the separator
	 * @return the gospl population
	 */
	public static GosplPopulation readFromCSVFile(final String filename,
			final IGenstarDictionary<Attribute<? extends IValue>> dictionnary, final char separator) {

		// configure the survey factory with the right parameters
		GosplSurveyFactory factory = new GosplSurveyFactory();
		IGSSurvey survey;
		try {
			survey = factory.getSurvey(filename, 0, separator, 1, 0, GSSurveyType.Sample, GosplSurveyFactory.CSV_EXT);
		} catch (IOException e) {
			throw new GenstarException(e);
		} catch (InvalidSurveyFormatException e) {
			throw new IllegalArgumentException("Invalid survey format", e);
		}

		GosplPopulation pop = null;

		try {
			// Map<String,String> keepOnlyEqual = new HashMap<>();
			// keepOnlyEqual.put("DEPT", "75");
			// keepOnlyEqual.put("NAT13", "Marocains");

			pop = GosplInputDataManager.getSample(survey, dictionnary, null, // extract the entire sample
					Collections.emptyMap() // extract entity with no limitation of attribute content
			);
		} catch (IOException | InvalidSurveyFormatException e) {
			throw new GenstarException(e);
		}

		return pop;
	}

}
