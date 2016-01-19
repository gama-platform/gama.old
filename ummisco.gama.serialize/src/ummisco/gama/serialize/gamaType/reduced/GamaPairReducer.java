package ummisco.gama.serialize.gamaType.reduced;

import msi.gama.util.GamaMap;
import msi.gama.util.GamaMap.GamaPairList;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gaml.types.GamaBoolType;
import msi.gaml.types.IType;

public class GamaPairReducer {
	private IType keysType;
	private IType dataType;
	private Object key;
	private Object value;
	
	public GamaPairReducer(GamaPair m)
	{
		keysType=m.getType().getKeyType();
		dataType=m.getType().getContentType();
		key=m.getKey();
		value=m.getValue();
	}
	
	public GamaPair constructObject()
	{
		return new GamaPair(key, value, keysType, dataType);
	}
}
