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
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 9 juin 2010
 * 
 * @todo Description
 * 
 */
public class Types {

	public final static IType[] typeToIType = new IType[256];
	public final static HashMap<String, IType> stringToIType = new HashMap();
	public final static HashMap<Class, IType> classToIType = new HashMap();

	public final static IType NO_TYPE = new GamaNoType();

	// private final static Map<Short, GamlModelTypes> managers;

	public static void initWith(final GamlProperties mp, final ClassLoader cl) {
		Class c;
		for ( String className : mp.keySet() ) {
			try {
				c = cl.loadClass(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			Set<Class> classes = new HashSet();
			type t = (type) c.getAnnotation(type.class);
			short theType = t.id();
			Class[] w = t.wraps();
			classes.addAll(Arrays.asList(w));
			List<String> kindAndName = new ArrayList(mp.get(className));
			String keyword = kindAndName.get(1);
			IType theTypeClass;
			try {
				theTypeClass = (IType) c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			typeToIType[theType] = theTypeClass;
			stringToIType.put(keyword, theTypeClass);
			for ( Class cc : classes ) {
				classToIType.put(cc, theTypeClass);
			}

		}
		// GUI.debug("Registered types: " + stringToIType);
		typeToIType[NONE] = NO_TYPE;
		classToIType.put(Object.class, NO_TYPE);
		stringToIType.put(NONE_STR, NO_TYPE);
	}

	public static Object coerce(final IScope scope, final Object value, final IType type,
		final Object param) throws GamaRuntimeException {
		return type.cast(scope, value, param);
	}

	public static IType get(final short type) {
		IType t = typeToIType[type];
		return t == null ? Types.NO_TYPE : t;
	}

	public static IType get(final String type) {
		IType t = stringToIType.get(type);
		return t == null ? Types.NO_TYPE : t;
	}

	public static IType get(final Class type) {
		IType t = classToIType.get(type);
		if ( t != null ) { return t; }
		// Set<Class> classes = classToIType.keySet();
		if ( !type.isInterface() ) {
			for ( Map.Entry<Class, IType> c : classToIType.entrySet() ) {
				if ( c.getKey() != Object.class && c.getKey().isAssignableFrom(type) ) {
					t = c.getValue();
					// classToIType.put(type, t);
					return t;
				}
			}
		}
		if ( !type.isInterface() ) {
			classToIType.put(type, Types.NO_TYPE);
		}
		return Types.NO_TYPE;
	}

	public static List<IType> getSortedTypes() {
		List<IType> types = new ArrayList(Arrays.asList(typeToIType));
		types.removeAll(Collections.singleton(null));
		Collections.sort(types);
		return types;
	}

}