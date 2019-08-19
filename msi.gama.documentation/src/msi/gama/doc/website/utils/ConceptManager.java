package msi.gama.doc.website.utils;

import java.lang.reflect.Field;
import java.text.*;
import java.util.*;
import msi.gama.precompiler.IConcept;
import ummisco.gama.dev.utils.DEBUG;

public class ConceptManager {

	public static String[] CONCEPTS_NOT_FOR_GAML_REF = {
		IConcept.AUTOSAVE,
		IConcept.BACKGROUND,
		IConcept.DISTRIBUTION,
		IConcept.ENUMERATION,
		IConcept.FACET,
		IConcept.GLOBAL,
		IConcept.HALT,
		IConcept.IMPORT,
		IConcept.INHERITANCE,
		IConcept.INIT,
		IConcept.LAYER,
		IConcept.MODEL,
		IConcept.OPENGL,
		IConcept.OPERATOR,
		IConcept.OUTPUT,
		IConcept.PAUSE,
		IConcept.PERMANENT,
		IConcept.PROBABILITY,
		IConcept.PSEUDO_VARIABLE,
		IConcept.REFLEX,
		IConcept.REFRESH,
		IConcept.SPORT,
		IConcept.TORUS,
		IConcept.UPDATE,
		IConcept.WRITE,
		IConcept.WORLD
	};

	public static final String[] CONCEPTS_NOT_FOR_MODEL_LIBRARY = {
		IConcept.ACTION,
		IConcept.ATTRIBUTE,
		IConcept.AUTOSAVE,
		IConcept.BACKGROUND,
		IConcept.BEHAVIOR,
		IConcept.CONSTANT,
		IConcept.CYCLE,
		IConcept.DIMENSION,
		IConcept.DISPLAY,
		IConcept.DISTRIBUTION,
		IConcept.ENUMERATION,
		IConcept.EXPERIMENT,
		IConcept.FACET,
		IConcept.FILE,
		IConcept.GLOBAL,
		IConcept.GRAPHIC_UNIT,
		IConcept.HALT,
		IConcept.IMPORT,	// concern the import of gaml files
		IConcept.INIT,
		IConcept.LAYER,
		IConcept.LENGTH_UNIT,
		IConcept.MODEL,
		IConcept.OPENGL,
		IConcept.OPERATOR,
		IConcept.OPTIMIZATION,
		IConcept.OUTPUT,
		IConcept.PARAMETER,
		IConcept.PAUSE,
		IConcept.PERMANENT,
		IConcept.PROBABILITY,
		IConcept.POINT,
		IConcept.PSEUDO_VARIABLE,
		IConcept.RANDOM,
		IConcept.RANDOM_OPERATOR,
		IConcept.REFLEX,
		IConcept.REFRESH,
		IConcept.SPECIES,
		IConcept.SURFACE_UNIT,
		IConcept.TIME,
		IConcept.TIME_UNIT,
		IConcept.TORUS,
		IConcept.TYPE,
		IConcept.UPDATE,
		IConcept.VOLUME_UNIT,
		IConcept.WEIGHT_UNIT,
		IConcept.WRITE,
		IConcept.WORLD
	};

	public static String[] CONCEPTS_DEDICATED_TO_SYNTAX = {
		IConcept.ARITHMETIC,
		IConcept.ATTRIBUTE,
		IConcept.CAST,
		IConcept.CONDITION,
		IConcept.CONTAINER,
		IConcept.FILTER,
		IConcept.LIST,
		IConcept.LOGICAL,
		IConcept.LOOP,
		IConcept.MAP,
		IConcept.MATRIX,
		IConcept.STRING,
		IConcept.TERNARY

	};

	public enum WebsitePart { DOCUMENTATION, GAML_REFERENCES, MODEL_LIBRARY }

	private static ArrayList<String> mConcepts;
	static HashMap<String,Integer> mOccurrenceOfConcept; // the key is the name of the concept, the value is the number of occurrences.

	static HashMap<String,Integer> mOccurrenceOfConceptInModelLibrary; // the key is the name of the concept, the value is the number of occurrences.
	static HashMap<String,Integer> mOccurrenceOfConceptInGamlRef; // the key is the name of the concept, the value is the number of occurrences.
	static HashMap<String,Integer> mOccurrenceOfConceptInDocumentation; // the key is the name of the concept, the value is the number of occurrences.

	public static void loadConcepts() throws IllegalAccessException {
		mConcepts = new ArrayList<>();
		mOccurrenceOfConcept = new HashMap<>();
		mOccurrenceOfConceptInModelLibrary = new HashMap<>();
		mOccurrenceOfConceptInGamlRef = new HashMap<>();
		mOccurrenceOfConceptInDocumentation = new HashMap<>();
		// get the list of predefined concept
		Field [] conceptFields = IConcept.class.getFields();
		for ( Field concept_field : conceptFields ) {
			String conceptName = concept_field.get(new Object()).toString();

			mConcepts.add(conceptName);
			mOccurrenceOfConcept.put(conceptName, 0);
			mOccurrenceOfConceptInModelLibrary.put(conceptName, 0);
			mOccurrenceOfConceptInGamlRef.put(conceptName, 0);
			mOccurrenceOfConceptInDocumentation.put(conceptName, 0);
		}
	}

	public static boolean conceptIsPossibleToAdd(final String concept) {
		return mConcepts.contains(concept);
	}

	public static void addOccurrenceOfConcept(final String concept) {
		addOccurrenceOfConcept(concept, "");
	}

	public static void addOccurrenceOfConcept(final String concept, final String websitePart) {
		if (mConcepts.contains(concept)) {
			// it is possible to add the concept. Update the number of occurrences of this concept in the library
			int oldValue = mOccurrenceOfConcept.get(concept);
			mOccurrenceOfConcept.put(concept, ++oldValue);
			if (websitePart.equals(WebsitePart.DOCUMENTATION.toString())) {
				oldValue = mOccurrenceOfConceptInDocumentation.get(concept);
				mOccurrenceOfConceptInDocumentation.put(concept, ++oldValue);
			}
			if (websitePart.equals(WebsitePart.GAML_REFERENCES.toString())) {
				oldValue = mOccurrenceOfConceptInGamlRef.get(concept);
				mOccurrenceOfConceptInGamlRef.put(concept, ++oldValue);
				if (Utils.isInList(concept, CONCEPTS_NOT_FOR_GAML_REF)) {
					DEBUG.LOG("WARNING : The concept "+concept+" is not supposed to be for GAML References !!");
				}
			}
			if (websitePart.equals(WebsitePart.MODEL_LIBRARY.toString())) {
				oldValue = mOccurrenceOfConceptInModelLibrary.get(concept);
				mOccurrenceOfConceptInModelLibrary.put(concept, ++oldValue);
				if (Utils.isInList(concept, CONCEPTS_NOT_FOR_MODEL_LIBRARY)) {
					DEBUG.LOG("WARNING : The concept "+concept+" is not supposed to be for the model library !!");
				}
			}
		}
	}

	public static void printStatistics() {
		Iterator<String> it = mOccurrenceOfConcept.keySet().iterator();
		List<String> conceptNotRepresented = new ArrayList<>();
		List<String> conceptTooMuchRepresented = new ArrayList<>();
		while (it.hasNext()) {
			String id = it.next();
			int numberOfOccurrences = mOccurrenceOfConcept.get(id);
			DEBUG.LOG("concept "+id+" : "+numberOfOccurrences+" occurrences.");
			if (numberOfOccurrences == 0) {
				conceptNotRepresented.add(id);
			}
			if (numberOfOccurrences > 20) {
				conceptTooMuchRepresented.add(id);
			}
		}
		DEBUG.LOG("_____________________________");
		for (String concept : conceptNotRepresented) {
			DEBUG.LOG("WARNING : No occurrence for concept "+concept+".");
		}
		for (String concept : conceptTooMuchRepresented) {
			DEBUG.LOG("WARNING : Too much occurrences ("+Integer.toString(mOccurrenceOfConcept.get(concept))+") for concept "+concept+".");
		}
	}

	public static String getExtendedStatistics() {
		String result = "";

		Collections.sort(mConcepts, (s1,s2) -> s1.compareToIgnoreCase(s2));		
		
		// write the header with the date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		result += "\n\n_The following text has been automatically generated from \"mainCheckConcepts\"_\n\n";
		result +="______________ _last update : "+dateFormat.format(date)+"_\n\n";

		// write the lists of concepts
		// write the list of concepts to use in model library (except Syntax)
		result += "**List of concepts to use for model library (except Syntax):**\n\n";
		boolean isFirstElement = true;
		for (String concept : mConcepts) {
			if (!Utils.isInList(concept,CONCEPTS_DEDICATED_TO_SYNTAX)
				&& !Utils.isInList(concept, CONCEPTS_NOT_FOR_MODEL_LIBRARY)) {
				if (isFirstElement) {
					result += concept;
					isFirstElement = false;
				}
				else {
					result += ", "+concept;
				}
			}
		}
		result += "\n\n";
		// write the list of concepts to use exclusively in Syntax
		result += "**List of concepts to use exclusively in Syntax models:**\n\n";
		isFirstElement = true;
		for (String concept : mConcepts) {
			if (Utils.isInList(concept,CONCEPTS_DEDICATED_TO_SYNTAX)) {
				if (isFirstElement) {
					result += concept;
					isFirstElement = false;
				}
				else {
					result += ", "+concept;
				}
			}
		}
		result += "\n\n";
		// write the list of concepts to use in GAML Reference
		result += "**List of concepts to use for GAML worlds:**\n\n";
		isFirstElement = true;
		for (String concept : mConcepts) {
			if (!Utils.isInList(concept,CONCEPTS_NOT_FOR_GAML_REF)) {
				if (isFirstElement) {
					result += concept;
					isFirstElement = false;
				}
				else {
					result += ", "+concept;
				}
			}
		}
		result += "\n\n";

		// write array
		result += "| **Concept name** | **in Doc** | **in GAML Ref** | **in Model Lib** | **TOTAL** |\n";
		result += "|:----------------------------|:-------------|:-------------|:-------------|:-------------|\n";
		for (int i = 0 ; i < mConcepts.size(); i++) {
			String id = mConcepts.get(i);
			String numberOfOccurrencesTotal = Integer.toString(mOccurrenceOfConcept.get(id));
			String numberOfOccurrencesInDoc = Integer.toString(mOccurrenceOfConceptInDocumentation.get(id));
			String numberOfOccurrencesInRef = Integer.toString(mOccurrenceOfConceptInGamlRef.get(id));
			if (Utils.isInList(id, CONCEPTS_NOT_FOR_GAML_REF)) {
				numberOfOccurrencesInRef = "_";
			}
			String numberOfOccurrencesInModel = Integer.toString(mOccurrenceOfConceptInModelLibrary.get(id));
			if (Utils.isInList(id, CONCEPTS_NOT_FOR_MODEL_LIBRARY)) {
				numberOfOccurrencesInModel = "_";
			}
			result += "| "+id+" | "+numberOfOccurrencesInDoc+" | "+numberOfOccurrencesInRef+" | "+numberOfOccurrencesInModel+" | "+numberOfOccurrencesTotal+" |\n";
		}

		return result;
	}
}
