package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.util.GamaMap;
import msi.gama.util.GamaMap.GamaPairList;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gaml.types.GamaBoolType;
import msi.gaml.types.IType;

public class GamaPairReducer {
	private IType keyPairType;
	private IType dataPairType;
	private Object key;
	private Object value;
	
	public GamaPairReducer(GamaPair m)
	{
		keyPairType=m.getType().getKeyType();
		dataPairType=m.getType().getContentType();
		key=m.getKey();
		value=m.getValue();
	}
	
	public GamaPair constructObject()
	{
		return new GamaPair(key, value, keyPairType, dataPairType);
	}
}
