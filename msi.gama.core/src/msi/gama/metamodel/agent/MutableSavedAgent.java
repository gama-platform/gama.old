package msi.gama.metamodel.agent;

import java.util.List;
import java.util.Map;

public class MutableSavedAgent extends SavedAgent {
	
	public MutableSavedAgent() {
		super();
		
	}
	
	public List<SavedAgent> putInnerPop(String key, List<SavedAgent> value) {
		return innerPopulations.put(key, value);
	}
	

	public void setInnerPop(Map<String, List<SavedAgent>> innerPop) {
		innerPopulations = innerPop;
	}
	
	public void setIndex(int idx) {
		index = idx;
	}
	
}