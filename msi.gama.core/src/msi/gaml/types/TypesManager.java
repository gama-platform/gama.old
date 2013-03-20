/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import static msi.gaml.types.IType.*;
import java.util.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.util.GuiUtils;
import msi.gaml.descriptions.*;

public class TypesManager {

	private short CURRENT_INDEX;

	private final HashMap<Short, IType> typeToIType;
	private final HashMap<String, IType> stringToIType;
	private final HashMap<Class, IType> classToIType;
	private final ModelDescription model;

	/**
	 * To initialize TypesManager of ModelDescription.
	 */
	public TypesManager(final ModelDescription md) {
		CURRENT_INDEX = SPECIES_TYPES;
		model = md;

		typeToIType = new HashMap<Short, IType>();
		for ( short typeId = 0; typeId < Types.typeToIType.length; typeId++ ) {
			IType toAdd = Types.typeToIType[typeId];
			if ( toAdd != null ) {
				typeToIType.put(typeId, toAdd);
				if ( toAdd.toString().equals(AGENT_STR) ) {
					toAdd.clearSubTypes();
				}
			}
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
	public IType addType(final TypeDescription species) {
		String name = species.getName();
		Class base = species.getJavaBase();
		if ( stringToIType.containsKey(name) ) {
			if ( name.equals(AGENT_STR) ) { return stringToIType.get(AGENT_STR); }
			species.error("Species " + name + " already declared. Species name must be unique",
				IGamlIssue.DUPLICATE_NAME, null, name);
		}
		short newId = ++CURRENT_INDEX;
		IType newType = new GamaAgentType(name, newId, base);
		typeToIType.put(newId, newType);
		stringToIType.put(name, newType);
		classToIType.put(base == null ? Types.get(AGENT).toClass() : base, newType);
		return newType;
	}

	public void addAll(final Map<String, TypeDescription> species) {
		Map<String, IType> map = new HashMap();
		for ( Map.Entry<String, TypeDescription> entry : species.entrySet() ) {
			map.put(entry.getKey(), addType(entry.getValue()));
		}
		map.remove(IType.AGENT_STR);
		for ( Map.Entry<String, IType> entry : map.entrySet() ) {
			TypeDescription s = species.get(entry.getKey());
			// String parentName = s.getParentName();
			TypeDescription parentSpecies = s.getParent();
			IType parentType =
				parentSpecies == null ? get(IType.AGENT_STR) : map.get(parentSpecies.getName());
			entry.getValue().setParent(parentType);
		}
	}

	public ModelDescription getModel() {
		return model;
	}

	public List<String> getTypeNames() {
		return new ArrayList(stringToIType.keySet());
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

	public void dispose() {
		for ( short i = SPECIES_TYPES + 1; i <= CURRENT_INDEX; i++ ) {
			IType t = typeToIType.get(i);
			if ( t != null ) {
				t.clearSubTypes();
			}
		}
		typeToIType.get(AGENT).clearSubTypes();
		// stringToIType.get("experimentator").clearChildren();
		// FIXME : do the same for all built in species ?
		typeToIType.clear();
		stringToIType.clear();
		classToIType.clear();
	}

	public void printTypeHierarchy() {
		Set<IType> roots = new HashSet();
		roots.add(Types.NO_TYPE);
		GuiUtils.debug("Type Hierarchy");

		while (!roots.isEmpty()) {
			String s = "";
			Set<IType> current = new HashSet();
			for ( IType t : roots ) {
				s +=
					t.toString() + "(" + t.id() + ", " +
						(t.getParent() == null ? "" : t.getParent().toString()) + ") ";

				current.addAll(t.getSubTypes());
			}
			GuiUtils.debug(s);
			roots = current;
		}

	}
}
