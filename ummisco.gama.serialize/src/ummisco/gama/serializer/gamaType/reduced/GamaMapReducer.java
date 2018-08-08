/*********************************************************************************************
 *
 * 'GamaMapReducer.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IReference;
import msi.gama.util.graph.IGraph;
import msi.gaml.types.IType;
import ummisco.gama.serializer.gamaType.reference.ReferenceMap;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaMapReducer {
	private final IType keysType;
	private final IType dataType;
	// private ArrayList<GamaPair> values = new ArrayList<GamaPair>();
	private Map<Object,Object> valuesMapReducer = new HashMap();

	public GamaMapReducer(final GamaMap m) {
		keysType = m.getGamlType().getKeyType();
		dataType = m.getGamlType().getContentType();

		for (final Object p : m.getPairs()) {
			final GamaPair pair = (GamaPair) p;
			valuesMapReducer.put(pair.key, pair.value);
			// values.add((GamaPair) p);
		}
		// Object[] o = m.getKeys()
		// values = (GamaPair[])data.toArray();
	}

	public GamaMap constructObject(final IScope scope) {
		
		boolean isReference = false;		
		Iterator ite = valuesMapReducer.entrySet().iterator();
		while(!isReference && ite.hasNext()) {
			Entry e = (Entry) ite.next();
			isReference = IReference.isReference(e.getKey()) || IReference.isReference(e.getValue());
		}
	
		return (isReference) ? 
			new ReferenceMap(this) :
			GamaMapFactory.create(scope, keysType, dataType, valuesMapReducer);		
		
	//	final GamaMap mp = GamaMapFactory.create(scope, keysType, dataType, valuesMapReducer);


	//	return mp;
	}

	public void unreferenceReducer(SimulationAgent sim) {
		
		HashMap<Object,Object> mapWithoutReferences = new HashMap<>();
		
		for(Entry e : valuesMapReducer.entrySet()) {
			mapWithoutReferences.put(
					IReference.getObjectWithoutReference(e.getKey(),sim), 
					IReference.getObjectWithoutReference(e.getValue(),sim));
		}
		
		valuesMapReducer = mapWithoutReferences;		
	}
		
	
	public IType getKeysType() { return keysType; }

	public IType getDataType() {
		return dataType;
	}

	public Map<Object,Object> getValues() {
		return valuesMapReducer;
	}
	
	public void setValues(HashMap m) {valuesMapReducer = m;}
}
