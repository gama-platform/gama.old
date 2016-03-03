package msi.gama.doc.websiteGen.utilClasses;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import msi.gama.precompiler.IConcept;

public class ConceptManager {
	
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
		for (int i = 0; i < concept_fields.length; i++) {
			String conceptName = (concept_fields[i].get(new Object())).toString();
			
			m_concepts.add(conceptName);
			m_occurrence_of_concept.put(conceptName, 0);
			m_occurrence_of_concept_in_model_library.put(conceptName, 0);
			m_occurrence_of_concept_in_gaml_ref.put(conceptName, 0);
			m_occurrence_of_concept_in_documentation.put(conceptName, 0);
		}
	}
	
	static public boolean conceptIsPossibleToAdd(String concept) {
		return m_concepts.contains(concept);
	}
	
	static public void addOccurrenceOfConcept(String concept) {
		addOccurrenceOfConcept(concept, "");
	}
	
	static public void addOccurrenceOfConcept(String concept, String websitePart) {
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
			}
			if (websitePart.equals(WebsitePart.MODEL_LIBRARY.toString())) {
				oldValue = m_occurrence_of_concept_in_model_library.get(concept);
				m_occurrence_of_concept_in_model_library.put(concept, ++oldValue);
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
}
