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

import static msi.gama.common.interfaces.IKeyword.GETTER;
import java.util.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.TypeFieldExpression;

/**
 * Written by drogoul Modified on 9 juin 2010
 * 
 * @todo Description
 * 
 */
public class Types {

	public final static HashMap<Integer, Set<String>> keywordsToVariableType = new HashMap();
	public final static IType[] typeToIType = new IType[256];
	public final static HashMap<String, IType> stringToIType = new HashMap();
	public final static HashMap<Class, IType> classToIType = new HashMap();

	public final static IType NO_TYPE = new GamaNoType();

	public static void initType(final String keyword, IType typeInstance, final short id,
		final int varKind, final Class ... wraps) {
		if ( keyword.equals(IType.NONE_STR) ) {
			typeInstance = NO_TYPE;
		}
		typeInstance.init(id, keyword, wraps);
		typeToIType[id] = typeInstance;
		stringToIType.put(keyword, typeInstance);
		for ( Class cc : wraps ) {
			classToIType.put(cc, typeInstance);
		}
		if ( !keywordsToVariableType.containsKey(varKind) ) {
			keywordsToVariableType.put(varKind, new HashSet());
		}
		keywordsToVariableType.get(varKind).add(keyword);
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

	public static <T> IType<T> get(final Class<T> type) {
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

	public static void initFieldGetters(final IType t) {
		List<IDescription> vars = AbstractGamlAdditions.getFieldDescriptions(t.toClass());
		if ( vars != null ) {
			for ( IDescription v : vars ) {
				String n = v.getName();
				IFieldGetter g =
					AbstractGamlAdditions.getFieldGetter(t.toClass(), v.getFacets()
						.getLabel(GETTER));
				t.addFieldGetter(n, new TypeFieldExpression(n, v.getType(), v.getContentType(), g));
			}
		}
	}

	public static void init() {
		for ( IType type : getSortedTypes() ) {
			if ( type != null ) {
				initFieldGetters(type);
			}
		}
	}

}