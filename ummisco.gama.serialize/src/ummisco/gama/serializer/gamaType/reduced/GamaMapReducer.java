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

import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gaml.types.IType;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaMapReducer {
	private final IType keysType;
	private final IType dataType;
	// private ArrayList<GamaPair> values = new ArrayList<GamaPair>();
	private final HashMap valuesMapReducer = new HashMap();

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
		final GamaMap mp = GamaMapFactory.create(scope, keysType, dataType, valuesMapReducer);

		// GamaMap mp = GamaMapFactory.create(keysType, dataType,values.size());
		// for(GamaPair p:values)
		// {
		// mp.put(p.key, p.value);
		// }
		return mp;
	}

	public IType getKeysType() {
		return keysType;
	}

	public IType getDataType() {
		return dataType;
	}

	// public ArrayList<GamaPair> getValues() {return values;}
	public HashMap getValues() {
		return valuesMapReducer;
	}
}
