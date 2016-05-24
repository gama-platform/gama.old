package msi.ummisco.modelLibraryGenerator;

import java.lang.reflect.Field;
import java.text.*;
import java.util.*;
import msi.gama.precompiler.IConcept;

public class ConceptManager {

	static public String[] CONCEPTS_NOT_FOR_GAML_REF = {
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

	static public String[] CONCEPTS_NOT_FOR_MODEL_LIBRARY = {
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

	static public String[] CONCEPTS_DEDICATED_TO_SYNTAX = {
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

	static public enum WebsitePart { DOCUMENTATION, GAML_REFERENCES, MODEL_LIBRARY };

	static private ArrayList<String> m_concepts;
	static HashMap<String,Integer> m_occurrence_of_concept; // the key is the name of the concept, the value is the number of occurrences.

	static HashMap<String,Integer> m_occurrence_of_concept_in_model_library; // the key is the name of the concept, the value is the number of occurrences.
	static HashMap<String,Integer> m_occurrence_of_concept_in_gaml_ref; // the key is the name of the concept, the value is the number of occurrences.
	static HashMap<String,Integer> m_occurrence_of_concept_in_documentation; // the key is the name of the concept, the value is the number of occurrences.

	static public void loadConcepts() throws IllegalArgumentException, IllegalAccessException {
		m_concepts = new ArrayList<String>();
		m_occurrence_of_concept = new HashMap<String,Integer>();
		m_occurrence_of_concept_in_model_library = new HashMap<String,Integer>();
		m_occurrence_of_concept_in_gaml_ref = new HashMap<String,Integer>();
		m_occurrence_of_concept_in_documentation = new HashMap<String,Integer>();
		// get the list of predefined concept
		Field [] concept_fields = IConcept.class.getFields();
		for ( Field concept_field : concept_fields ) {
			String conceptName = concept_field.get(new Object()).toString();

			m_concepts.add(conceptName);
			m_occurrence_of_concept.put(conceptName, 0);
			m_occurrence_of_concept_in_model_library.put(conceptName, 0);
			m_occurrence_of_concept_in_gaml_ref.put(conceptName, 0);
			m_occurrence_of_concept_in_documentation.put(conceptName, 0);
		}
	}

	static public boolean conceptIsPossibleToAdd(final String concept) {
		return m_concepts.contains(concept);
	}

	static public void addOccurrenceOfConcept(final String concept) {
		addOccurrenceOfConcept(concept, "");
	}

	static public void addOccurrenceOfConcept(final String concept, final String websitePart) {
		if (m_concepts.contains(concept)) {
			// it is possible to add the concept. Update the number of occurrences of this concept in the library
			int oldValue = m_occurrence_of_concept.get(concept);
			m_occurrence_of_concept.put(concept, ++oldValue);
			if (websitePart.equals(WebsitePart.DOCUMENTATION.toString())) {
				oldValue = m_occurrence_of_concept_in_documentation.get(concept);
				m_occurrence_of_concept_in_documentation.put(concept, ++oldValue);
			}
			if (websitePart.equals(WebsitePart.GAML_REFERENCES.toString())) {
				oldValue = m_occurrence_of_concept_in_gaml_ref.get(concept);
				m_occurrence_of_concept_in_gaml_ref.put(concept, ++oldValue);
				if (Utils.IsInList(concept, CONCEPTS_NOT_FOR_GAML_REF)) {
					System.out.println("WARNING : The concept "+concept+" is not supposed to be for GAML References !!");
				}
			}
			if (websitePart.equals(WebsitePart.MODEL_LIBRARY.toString())) {
				oldValue = m_occurrence_of_concept_in_model_library.get(concept);
				m_occurrence_of_concept_in_model_library.put(concept, ++oldValue);
				if (Utils.IsInList(concept, CONCEPTS_NOT_FOR_MODEL_LIBRARY)) {
					System.out.println("WARNING : The concept "+concept+" is not supposed to be for the model library !!");
				}
			}
		}
	}

	static public void printStatistics() {
		Iterator<String> it = m_occurrence_of_concept.keySet().iterator();
		ArrayList<String> concept_not_represented = new ArrayList<String>();
		ArrayList<String> concept_too_much_represented = new ArrayList<String>();
		while (it.hasNext()) {
			String id = it.next();
			int number_of_occurrences = m_occurrence_of_concept.get(id);
			System.out.println("concept "+id+" : "+number_of_occurrences+" occurrences.");
			if (number_of_occurrences == 0) {
				concept_not_represented.add(id);
			}
			if (number_of_occurrences > 20) {
				concept_too_much_represented.add(id);
			}
		}
		System.out.println("_____________________________");
		for (String concept : concept_not_represented) {
			System.out.println("WARNING : No occurrence for concept "+concept+".");
		}
		for (String concept : concept_too_much_represented) {
			System.out.println("WARNING : Too much occurrences ("+Integer.toString(m_occurrence_of_concept.get(concept))+") for concept "+concept+".");
		}
	}

	static public String getExtendedStatistics() {
		String result = "";
		Collections.sort(m_concepts, new Comparator<String>() {
			@Override
			public int compare(final String s1, final String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});

		// write the header with the date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		result += "\n\n_The following text has been automatically generated from \"mainCheckConcepts\"_\n\n";
		result +="______________ _last update : "+dateFormat.format(date)+"_\n\n";

		// write the lists of concepts
		// write the list of concepts to use in model library (except Syntax)
		result += "**List of concepts to use for model library (except Syntax):**\n\n";
		boolean isFirstElement = true;
		for (String concept : m_concepts) {
			if (!Utils.IsInList(concept,CONCEPTS_DEDICATED_TO_SYNTAX)
				&& !Utils.IsInList(concept, CONCEPTS_NOT_FOR_MODEL_LIBRARY)) {
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
		for (String concept : m_concepts) {
			if (Utils.IsInList(concept,CONCEPTS_DEDICATED_TO_SYNTAX)) {
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
		for (String concept : m_concepts) {
			if (!Utils.IsInList(concept,CONCEPTS_NOT_FOR_GAML_REF)) {
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
		for (int i = 0 ; i < m_concepts.size(); i++) {
			String id = m_concepts.get(i);
			String number_of_occurrences_total = Integer.toString(m_occurrence_of_concept.get(id));
			String number_of_occurrences_in_doc = Integer.toString(m_occurrence_of_concept_in_documentation.get(id));
			String number_of_occurrences_in_ref = Integer.toString(m_occurrence_of_concept_in_gaml_ref.get(id));
			if (Utils.IsInList(id, CONCEPTS_NOT_FOR_GAML_REF)) {
				number_of_occurrences_in_ref = "_";
			}
			String number_of_occurrences_in_model = Integer.toString(m_occurrence_of_concept_in_model_library.get(id));
			if (Utils.IsInList(id, CONCEPTS_NOT_FOR_MODEL_LIBRARY)) {
				number_of_occurrences_in_model = "_";
			}
			result += "| "+id+" | "+number_of_occurrences_in_doc+" | "+number_of_occurrences_in_ref+" | "+number_of_occurrences_in_model+" | "+number_of_occurrences_total+" |\n";
		}

		return result;
	}
}
