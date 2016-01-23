package ummisco.gama.serializer.gamaType.reduced;

import java.util.ArrayList;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMap.GamaPairList;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gaml.types.IType;

public class GamaListReducer {
//	private IType keysType;
	private IType contentType;
	private ArrayList<Object> values = new ArrayList<>();
	private IScope scope;
	
	public GamaListReducer(IScope scope, GamaList l)
	{		
		contentType = l .getType().getContentType();
		
		for(Object p : l) {
			values.add(p);
		}
	}
	
	public GamaList constructObject()
	{
		return (GamaList) GamaListFactory.create(scope, contentType, values);

	}
}
