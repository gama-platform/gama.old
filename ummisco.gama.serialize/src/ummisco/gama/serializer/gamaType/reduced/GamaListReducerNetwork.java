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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@SuppressWarnings({ "rawtypes" })
public class GamaListReducerNetwork extends GamaListReducer{
	
	public GamaListReducerNetwork(final GamaList l)
	{
		super(l);
	}
	
	public GamaList constructObject(final IScope scope) {
	//	System.out.println("read "+contentTypeListReducer+ " "+valuesListReducer );
	//	scope.getAgent().getPopulationFor(speciesName)
	//	(microSpeciesName)getMicroSpecies(contentTypeListReducer);
		return (GamaList) GamaListFactory.create(scope, Types.NO_TYPE, this.getValuesListReducer());
	}
}
