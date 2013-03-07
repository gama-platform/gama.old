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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.common.util;

import java.util.*;

/**
 * Written by drogoul Modified on 28 déc. 2010
 * 
 * Provides some utilities for dealing with reflection.
 * 
 */
public class JavaUtils {

	public final static Map<Set<Class>, List<Class>> IMPLEMENTATION_CLASSES = new HashMap();
	private static Map<Class, Set<Class>> allInterfaces = new HashMap();
	private static Map<Class, Set<Class>> allSuperclasses = new HashMap();

	private static void addAllInterfaces(final Class clazz, final Set allInterfaces,
		final Set<Class> in) {
		if ( clazz == null ) { return; }
		final Class[] interfaces = clazz.getInterfaces();
		for ( Class c : interfaces ) {
			if ( in.contains(c) ) {
				allInterfaces.add(c);
			}
		}
		addAllInterfaces(interfaces, allInterfaces, in);
		addAllInterfaces(clazz.getSuperclass(), allInterfaces, in);
	}

	private static void addAllInterfaces(final Class[] clazzes, final Set allInterfaces,
		final Set<Class> in) {
		if ( clazzes != null ) {
			for ( int i = 0; i < clazzes.length; i++ ) {
				addAllInterfaces(clazzes[i], allInterfaces, in);
			}
		}
	}

	public static final Set<Class> allInterfacesOf(final Class c, final Set<Class> in) {
		if ( allInterfaces.containsKey(c) ) { return allInterfaces.get(c); }
		final Set<Class> result = new HashSet<Class>();
		addAllInterfaces(c, result, in);
		allInterfaces.put(c, result);
		return result;
	}

	public static final Set<Class> allSuperclassesOf(final Class c, final Set<Class> in) {
		if ( allSuperclasses.containsKey(c) ) { return allSuperclasses.get(c); }
		final HashSet<Class> result = new HashSet();
		if ( c == null ) { return result; }
		Class c2 = c.getSuperclass();
		while (c2 != null) {
			if ( in.contains(c2) ) {
				result.add(c2);
			}
			c2 = c2.getSuperclass();
		}
		allSuperclasses.put(c, result);

		return result;
	}

	public static List<Class> collectImplementationClasses(final Class baseClass,
		final Set<Class> skillClasses, final Set<Class> in) {
		Set<Class> classes = new HashSet();
		classes.add(baseClass);
		classes.addAll(skillClasses);
		Set<Class> key = new HashSet(classes);
		if ( IMPLEMENTATION_CLASSES.containsKey(key) ) { return IMPLEMENTATION_CLASSES.get(key); }
		classes.addAll(allInterfacesOf(baseClass, in));
		for ( final Class classi : new ArrayList<Class>(classes) ) {
			classes.addAll(allSuperclassesOf(classi, in));
		}
		final ArrayList<Class> classes2 = new ArrayList(classes);
		Collections.sort(classes2, new Comparator<Class>() {

			@Override
			public int compare(final Class o1, final Class o2) {
				if ( o1 == o2 ) { return 0; }
				if ( o1.isAssignableFrom(o2) ) { return -1; }
				if ( o2.isAssignableFrom(o1) ) { return 1; }
				if ( o1.isInterface() && !o2.isInterface() ) { return -1; }
				if ( o2.isInterface() && !o1.isInterface() ) { return 1; }
				return 1;
			}
		});

		IMPLEMENTATION_CLASSES.put(key, classes2);
		return classes2;
	}

}
