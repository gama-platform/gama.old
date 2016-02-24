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
	private ArrayList<Object> valuesListReducer = new ArrayList<>();
	private IType contentTypeListReducer;
	
	public GamaListReducer(GamaList l)
	{		
		contentTypeListReducer = l .getType().getContentType();
		
		for(Object p : l) {
			valuesListReducer.add(p);
		}
	}
	
	public GamaList constructObject(IScope scope)
	{
		return (GamaList) GamaListFactory.create(scope, contentTypeListReducer, valuesListReducer);
	}
}
