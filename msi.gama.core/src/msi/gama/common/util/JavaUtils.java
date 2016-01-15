/*********************************************************************************************
 *
 *
 * 'JavaUtils.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.util;

import java.util.*;
import com.google.common.collect.*;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import msi.gaml.skills.ISkill;

/**
 * Written by drogoul Modified on 28 d�c. 2010
 *
 * Provides some utilities for dealing with reflection.
 *
 */
public class JavaUtils {

	public final static Map<Set<Class>, List<Class>> IMPLEMENTATION_CLASSES = new THashMap();
	private static Map<Class, Set<Class>> allInterfaces = new THashMap();
	private static Map<Class, Set<Class>> allSuperclasses = new THashMap();

	private static void addAllInterfaces(final Class clazz, final Set allInterfaces, final Set<Class> in) {
		if ( clazz == null ) { return; }
		final Class[] interfaces = clazz.getInterfaces();
		for ( final Class c : interfaces ) {
			if ( in.contains(c) ) {
				allInterfaces.add(c);
			}
		}
		addAllInterfaces(interfaces, allInterfaces, in);
		addAllInterfaces(clazz.getSuperclass(), allInterfaces, in);
	}

	private static void addAllInterfaces(final Class[] clazzes, final Set allInterfaces, final Set<Class> in) {
		if ( clazzes != null ) {
			for ( int i = 0; i < clazzes.length; i++ ) {
				addAllInterfaces(clazzes[i], allInterfaces, in);
			}
		}
	}

	public static final Set<Class> allInterfacesOf(final Class c, final Set<Class> in) {
		if ( allInterfaces.containsKey(c) ) { return allInterfaces.get(c); }
		final Set<Class> result = new THashSet<Class>();
		addAllInterfaces(c, result, in);
		allInterfaces.put(c, result);
		return result;
	}

	public static final Set<Class> allSuperclassesOf(final Class c, final Set<Class> in) {
		if ( allSuperclasses.containsKey(c) ) { return allSuperclasses.get(c); }
		final THashSet<Class> result = new THashSet();
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
		final Set<Class<? extends ISkill>> skillClasses, final Set<Class> in) {
		final Set<Class> classes = new THashSet();
		if ( baseClass != null ) {
			classes.add(baseClass);
		}
		classes.addAll(skillClasses);
		final Set<Class> key = new THashSet(classes);
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

	public static <F> Iterator<F> iterator(final Object[] array) {
		if ( array != null ) { return (Iterator<F>) Iterators.forArray(array); }
		return new UnmodifiableIterator<F>() {

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public F next() {
				return null;
			}
		};
	}

}
