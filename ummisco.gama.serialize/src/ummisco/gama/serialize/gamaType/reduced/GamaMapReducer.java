package ummisco.gama.serialize.gamaType.reduced;

import msi.gama.util.GamaMap;
import msi.gama.util.GamaMap.GamaPairList;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gaml.types.IType;

public class GamaMapReducer {
	private IType keysType;
	private IType dataType;
	private GamaPair[] values;
	
	public GamaMapReducer(GamaMap m)
	{
		keysType=m.getType().getKeyType();
		dataType=m.getType().getContentType();
		GamaPairList data = m.getPairs();
		values = (GamaPair[])data.toArray();
	}
	
	public GamaMap constructObject()
	{
		GamaMap mp = GamaMapFactory.create(keysType, dataType,values.length);
		for(GamaPair p:values)
		{
			mp.put(p.key, p.value);
		}
		return mp;
	}
}
