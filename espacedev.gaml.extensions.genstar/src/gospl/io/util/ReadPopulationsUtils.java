package gospl.io.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import core.configuration.dictionary.IGenstarDictionary;
import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.io.IGSSurvey;
import core.metamodel.value.IValue;
import gospl.GosplPopulation;
import gospl.distribution.GosplInputDataManager;
import gospl.io.CsvInputHandler;
import gospl.io.GosplSurveyFactory;
import gospl.io.exception.InvalidSurveyFormatException;

/**
 * Utilities to read populations.
 * Provides APIs for users to easily load population from files;
 * 
 * @author Samuel Thiriot
 *
 */
public class ReadPopulationsUtils {

	public ReadPopulationsUtils() {
	}


	public static GosplPopulation readFromCSVFile(
			String filename,
			IGenstarDictionary<Attribute<? extends IValue>> dictionnary
			) {
		File fileCSV = new File(filename);
		
		char separator;
		try {
			separator = CsvInputHandler.detectSeparator(fileCSV);
		} catch (IOException e) {
			throw new RuntimeException("error while trying to detect separator from file "+fileCSV, e);
		}
		
		return readFromCSVFile(filename, dictionnary, separator);
		
	}
	
	public static GosplPopulation readFromCSVFile(
			String filename,
			IGenstarDictionary<Attribute<? extends IValue>> dictionnary,
			char separator
			) {
	
		// configure the survey factory with the right parameters
		GosplSurveyFactory factory = new GosplSurveyFactory();
		IGSSurvey survey;
		try {
			survey = factory.getSurvey(
					filename, 
					0,
					separator,
					1,
					0,
					GSSurveyType.Sample,
					GosplSurveyFactory.CSV_EXT
					);
		} catch (InvalidFormatException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InvalidSurveyFormatException e) {
			throw new IllegalArgumentException("Invalid survey format", e);
		}	
		

		GosplPopulation pop = null;
	
		try {
			//Map<String,String> keepOnlyEqual = new HashMap<>();
			//keepOnlyEqual.put("DEPT", "75");
			//keepOnlyEqual.put("NAT13", "Marocains");
			
			pop = GosplInputDataManager.getSample(
					survey, 
					dictionnary, 
					null, // extract the entire sample
					Collections.emptyMap() // extract entity with no limitation of attribute content
					);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InvalidSurveyFormatException e) {
			throw new RuntimeException(e);
		}
		
		return pop;
	}
	
}
