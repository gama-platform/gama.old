/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.internal.types;

import static msi.gama.interfaces.IType.AGENT;
import static msi.gama.interfaces.IType.SPECIES_TYPES;

import java.util.HashMap;
import java.util.List;

import msi.gama.interfaces.IType;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.util.GamaList;

public class TypesManager {
	private short CURRENT_INDEX;
	
	private final HashMap<Short, IType> typeToIType;
	private final HashMap<String, IType> stringToIType;
	private final HashMap<Class, IType> classToIType;

	/**
	 * To initialize TypesManager of ModelDescription.
	 */
	public TypesManager() {
		CURRENT_INDEX = SPECIES_TYPES;
		
		typeToIType = new HashMap<Short, IType>();
		for (short typeId = 0; typeId < Types.typeToIType.length; typeId++) {
			if (Types.typeToIType[typeId] != null) { typeToIType.put(typeId, Types.typeToIType[typeId]); }
		}
		
		classToIType = new HashMap<Class, IType>();
		classToIType.putAll(Types.classToIType);
		stringToIType = new HashMap<String, IType>();
		stringToIType.putAll(Types.stringToIType);
	}
	
	/**
	 * 
	 * @param name species name
	 * @param base
	 * @return
	 */
	public IType addType(final String name, final Class base) throws GamlException {
		/*
		 * why do we need this code?
		if ( stringToIType.containsKey(name) ) {
			IType theType = stringToIType.get(name);
			return theType;
		}
		*/
		if ( stringToIType.containsKey(name) ) {
			throw new GamlException("Species " + name + " already declared. Species name must be unique!");
		}
		
		short newId = ++CURRENT_INDEX;
		IType newType = new GamaAgentType(name, newId, base);
		typeToIType.put(newId, newType);
		stringToIType.put(name, newType);
		classToIType.put(base == null ? Types.get(AGENT).toClass() : base, newType);
		return newType;
	}

	public List<String> getTypeNames() {
		return new GamaList(stringToIType.keySet());
	}

	public IType get(final short type) {
		IType t = typeToIType.get(type);
		
		return t == null ? Types.NO_TYPE : t;
	}

	public IType get(final String type) {
		IType t = stringToIType.get(type);
		
		return t == null ? Types.NO_TYPE : t;
	}

	public IType get(final Class type) {
		IType t = classToIType.get(type);
		
		return t == null ? Types.NO_TYPE : t;
	}
}
