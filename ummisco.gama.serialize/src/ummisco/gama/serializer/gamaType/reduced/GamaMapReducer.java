package ummisco.gama.serializer.gamaType.reduced;

import java.util.HashMap;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gaml.types.IType;

public class GamaMapReducer {
	private IType keysType;
	private IType dataType;
//	private ArrayList<GamaPair> values = new ArrayList<GamaPair>();
	private HashMap values = new HashMap();
	
	public GamaMapReducer( GamaMap m)
	{		
		keysType=m.getType().getKeyType();
		dataType=m.getType().getContentType();
		
		for(Object p : m.getPairs()) {
			GamaPair pair = (GamaPair) p;
			values.put(pair.key, pair.value);			
		//	values.add((GamaPair) p);
		}
		//Object[] o = m.getKeys()
		//values = (GamaPair[])data.toArray();
	}
	
	public GamaMap constructObject(IScope scope)
	{
		GamaMap mp = GamaMapFactory.create(scope, keysType, dataType, values);
		
	//	GamaMap mp = GamaMapFactory.create(keysType, dataType,values.size());
	//	for(GamaPair p:values)
	//	{
	//		mp.put(p.key, p.value);
	//	}
		return mp;
	}
	
	public IType getKeysType(){return keysType;}
	public IType getDataType(){return dataType;}
//	public ArrayList<GamaPair> getValues() {return values;}
	public HashMap getValues() {return values;}	
}
