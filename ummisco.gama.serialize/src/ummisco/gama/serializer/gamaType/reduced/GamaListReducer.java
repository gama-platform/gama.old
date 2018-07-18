/*********************************************************************************************
 *
 * 'GamaListReducer.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import java.util.ArrayList;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gaml.types.IType;

@SuppressWarnings({ "rawtypes" })
public class GamaListReducer {
	private final ArrayList<Object> valuesListReducer = new ArrayList<>();
	private final IType contentTypeListReducer;

	public GamaListReducer(final GamaList l) {
		contentTypeListReducer = l.getType().getContentType();
		
		for (final Object p : l) {
			valuesListReducer.add(p);
		}
	}

	public GamaList constructObject(final IScope scope) {
	//	System.out.println("read "+contentTypeListReducer+ " "+valuesListReducer );
	//	scope.getAgent().getPopulationFor(speciesName)
	//	(microSpeciesName)getMicroSpecies(contentTypeListReducer);
		return (GamaList) GamaListFactory.create(scope, contentTypeListReducer, valuesListReducer);
	}

	public ArrayList<Object> getValuesListReducer() {
		return valuesListReducer;
	}

	public IType getContentTypeListReducer() {
		return contentTypeListReducer;
	}
	
	
	
}
