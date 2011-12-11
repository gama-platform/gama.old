/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.internal.types;

import static msi.gama.interfaces.IType.*;
import java.util.*;
import msi.gama.gui.application.Activator;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 9 juin 2010
 * 
 * @todo Description
 * 
 */
public class Types {

	public final static IType[] typeToIType;
	public final static HashMap<String, IType> stringToIType;
	public final static HashMap<Class, IType> classToIType;

	public final static IType NO_TYPE = new GamaNoType();
	// private final static Map<Short, GamlModelTypes> managers;

	static {
		// managers = new HashMap();
		typeToIType = new IType[256];
		classToIType = new HashMap();
		stringToIType = new HashMap();

		MultiProperties mp = new MultiProperties();
		try {
			mp = Activator.getGamaProperties(GamaProcessor.TYPES);
		} catch (GamlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Class c;
		for ( String className : mp.keySet() ) {
			try {
				c = Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			Set<Class> classes = new HashSet();
			type t = (type) c.getAnnotation(type.class);
			short theType = t.id();
			Class[] w = t.wraps();
			classes.addAll(Arrays.asList(w));
			for ( String keyword : mp.get(className) ) {
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
					classToIType.put(type, t);
					return t;
				}
			}
		}
		if ( !type.isInterface() ) {
			classToIType.put(type, Types.NO_TYPE);
		}
		return Types.NO_TYPE;
	}

	public static void initFieldGetters() {
		List<IType> types = new GamaList(typeToIType);
		types.removeAll(Collections.singleton(null));
		Collections.sort(types);
		// GUI.debug("Exploring built-in types: " + types);
		for ( IType type : types ) {
			if ( type != null ) {
				type.initFieldGetters();
			}
		}
	}

}