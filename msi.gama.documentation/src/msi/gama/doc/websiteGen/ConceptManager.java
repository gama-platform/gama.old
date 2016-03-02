package msi.gama.doc.websiteGen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import msi.gama.precompiler.IConcept;

public class ConceptManager {
	
	static private String[] ConceptsToIgnore =
		{
				IConcept.ACTION,
				IConcept.ARITHMETIC,
				IConcept.ATTRIBUTE,
				IConcept.BEHAVIOR,
				IConcept.CAST,
				IConcept.CONDITION,
				IConcept.CONSTANT,
				IConcept.CONTAINER,
				IConcept.CYCLE,
				IConcept.DIMENSION,
				IConcept.DISPLAY,
				IConcept.ENUMERATION,
				IConcept.EXPERIMENT,
				IConcept.FACET,
				IConcept.GLOBAL,
				IConcept.GRAPHIC_UNIT,
				IConcept.HALT,
				IConcept.IMPORT,
				IConcept.INHERITANCE,
				IConcept.INIT,
				IConcept.LOOP,
				IConcept.OPENGL,
				IConcept.OPERATOR,
				IConcept.PARAMETER,
				IConcept.PAUSE,
				IConcept.REFLEX,
				IConcept.SPECIES,
				IConcept.STATIC,
				IConcept.STRING,
				IConcept.WRITE
		}; // this is the list of concepts that only concerns the "documentation" part, and not models.
	
	static private ArrayList<String> m_concepts;
	static HashMap<String,Integer> m_occurrence_of_concept; // the key is the name of the concept, the value is the number of occurrences.
	
	static public void loadConcepts() throws IllegalArgumentException, IllegalAccessException {
		m_concepts = new ArrayList<String>();
		m_occurrence_of_concept = new HashMap<String,Integer>();
		// get the list of predefined concept
		Field [] concept_fields = IConcept.class.getFields();
		for (int i = 0; i < concept_fields.length; i++) {
			String conceptName = (concept_fields[i].get(new Object())).toString();
			
			boolean conceptToBeIgnore = false;
			for (String str : ConceptsToIgnore) {
				if (str.equals(conceptName)) {
					conceptToBeIgnore = true;
				}
			}
			
			if (!conceptToBeIgnore) {
				m_concepts.add(conceptName);
				m_occurrence_of_concept.put(conceptName, 0);
			}
		}
	}
	
	static public boolean conceptIsPossibleToAdd(String concept) {
		if (m_concepts.contains(concept)) {
			// it is possible to add the concept. Update the number of occurrences of this concept in the library
			int oldValue = m_occurrence_of_concept.get(concept);
			m_occurrence_of_concept.put(concept, ++oldValue);
			return true;
		}
		else return false;
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
