package ummisco.gama.serializer.gamaType.reduced;

import java.util.ArrayList;

import msi.gama.util.GamaMap;
import msi.gama.util.GamaMap.GamaPairList;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gaml.types.IType;

public class GamaMapReducer {
	private IType keysType;
	private IType dataType;
	private ArrayList<GamaPair> values = new ArrayList<GamaPair>();
	
	public GamaMapReducer(GamaMap m)
	{		
		keysType=m.getType().getKeyType();
		dataType=m.getType().getContentType();
		
		for(Object p : m.getPairs()) {
			values.add((GamaPair) p);
		}
		//Object[] o = m.getKeys()
		//values = (GamaPair[])data.toArray();
	}
	
	public GamaMap constructObject()
	{
		GamaMap mp = GamaMapFactory.create(keysType, dataType,values.size());
		for(GamaPair p:values)
		{
			mp.put(p.key, p.value);
		}
		return mp;
	}
	
	public IType getKeysType(){return keysType;}
	public IType getDataType(){return dataType;}
	public ArrayList<GamaPair> getValues() {return values;}
	
}
