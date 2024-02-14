/*******************************************************************************************************
 *
 * FSTClazzInfoRegistry.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.nustaq.serialization.util.FSTMap;

/**
 * Created with IntelliJ IDEA. User: Möller Date: 03.11.12 Time: 13:11 To change this template use File | Settings |
 * File Templates.
 */
public class FSTClazzInfoRegistry {

	/** The m infos. */
	FSTMap<Class<?>, FSTClazzInfo> mInfos = new FSTMap<>(97);

	/** The serializer registry. */
	FSTSerializerRegistry serializerRegistry = new FSTSerializerRegistry();

	/** The ignore annotations. */
	boolean ignoreAnnotations = false;

	/** The rw lock. */
	final AtomicBoolean rwLock = new AtomicBoolean(false);

	/** The struct mode. */
	private boolean structMode = false;

	/**
	 * Adds the all referenced classes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param cl
	 *            the cl
	 * @param names
	 *            the names
	 * @param filter
	 *            the filter
	 * @date 30 sept. 2023
	 */
	public static void addAllReferencedClasses(final Class<?> cl, final ArrayList<String> names, final String filter) {
		HashSet<String> names1 = new HashSet<>();
		addAllReferencedClasses(cl, names1, new HashSet<>(), filter);
		names.addAll(names1);
	}

	/**
	 * Adds the all referenced classes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param cl
	 *            the cl
	 * @param names
	 *            the names
	 * @param topLevelDone
	 *            the top level done
	 * @param filter
	 *            the filter
	 * @date 30 sept. 2023
	 */
	static void addAllReferencedClasses(final Class<?> cl, final HashSet<String> names,
			final HashSet<String> topLevelDone, final String filter) {
		if (cl == null || topLevelDone.contains(cl.getName()) || !cl.getName().startsWith(filter)) return;
		topLevelDone.add(cl.getName());
		Field[] declaredFields = cl.getDeclaredFields();
		for (Field declaredField : declaredFields) {
			Class<?> type = declaredField.getType();
			if (!type.isPrimitive() && !type.isArray()) {
				names.add(type.getName());
				addAllReferencedClasses(type, names, topLevelDone, filter);
			}
		}
		Class[] declaredClasses = cl.getDeclaredClasses();
		for (Class<?> declaredClass : declaredClasses) {
			if (!declaredClass.isPrimitive() && !declaredClass.isArray()) {
				names.add(declaredClass.getName());
				addAllReferencedClasses(declaredClass, names, topLevelDone, filter);
			}
		}
		Method[] declaredMethods = cl.getDeclaredMethods();
		for (Method declaredMethod : declaredMethods) {
			Class<?> returnType = declaredMethod.getReturnType();
			if (!returnType.isPrimitive() && !returnType.isArray()) {
				names.add(returnType.getName());
				addAllReferencedClasses(returnType, names, topLevelDone, filter);
			}
			Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
			for (Class<?> parameterType : parameterTypes) {
				if (!parameterType.isPrimitive() && !parameterType.isArray()) {
					names.add(parameterType.getName());
					addAllReferencedClasses(parameterType, names, topLevelDone, filter);
				}
			}
		}

		Class[] classes = cl.getDeclaredClasses();
		for (Class<?> aClass : classes) {
			if (!aClass.isPrimitive() && !aClass.isArray()) {
				names.add(aClass.getName());
				addAllReferencedClasses(aClass, names, topLevelDone, filter);
			}
		}

		Class<?> enclosingClass = cl.getEnclosingClass();
		if (enclosingClass != null) {
			names.add(enclosingClass.getName());
			addAllReferencedClasses(enclosingClass, names, topLevelDone, filter);
		}

		names.add(cl.getName());
		addAllReferencedClasses(cl.getSuperclass(), names, topLevelDone, filter);
		Class[] interfaces = cl.getInterfaces();
		for (Class<?> anInterface : interfaces) {
			if (!anInterface.isPrimitive() && !anInterface.isArray()) {
				names.add(anInterface.getName());
				addAllReferencedClasses(anInterface, names, topLevelDone, filter);
			}
		}
	}

	/**
	 * Instantiates a new FST clazz info registry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	public FSTClazzInfoRegistry() {}

	/**
	 * Gets the CL info.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param conf
	 *            the conf
	 * @return the CL info
	 * @date 30 sept. 2023
	 */
	public FSTClazzInfo getCLInfo(final Class<?> c, final FSTConfiguration conf) {
		while (!rwLock.compareAndSet(false, true)) {}
		try {
			FSTClazzInfo res = mInfos.get(c);
			if (res == null) {
				if (c == null) throw new NullPointerException("Class is null");
				if (conf.getVerifier() != null && !conf.getVerifier().allowClassDeserialization(c))
					throw new RuntimeException("tried to deserialize forbidden class " + c.getName());
				res = new FSTClazzInfo(conf, c, this, ignoreAnnotations);
				mInfos.put(c, res);
			}
			return res;
		} finally {
			rwLock.set(false);
		}
	}

	/**
	 * Gets the serializer registry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the serializer registry
	 * @date 30 sept. 2023
	 */
	public FSTSerializerRegistry getSerializerRegistry() { return serializerRegistry; }

	/**
	 * Checks if is ignore annotations.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is ignore annotations
	 * @date 30 sept. 2023
	 */
	public final boolean isIgnoreAnnotations() { return ignoreAnnotations; }

	/**
	 * Sets the ignore annotations.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param ignoreAnnotations
	 *            the new ignore annotations
	 * @date 30 sept. 2023
	 */
	public void setIgnoreAnnotations(final boolean ignoreAnnotations) { this.ignoreAnnotations = ignoreAnnotations; }

	/**
	 * Sets the serializer registry delegate.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param delegate
	 *            the new serializer registry delegate
	 * @date 30 sept. 2023
	 */
	public void setSerializerRegistryDelegate(final FSTSerializerRegistryDelegate delegate) {
		serializerRegistry.setDelegate(delegate);
	}

	/**
	 * Gets the serializer registry delegate.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the serializer registry delegate
	 * @date 30 sept. 2023
	 */
	public FSTSerializerRegistryDelegate getSerializerRegistryDelegate() { return serializerRegistry.getDelegate(); }

	/**
	 * Sets the struct mode.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param structMode
	 *            the new struct mode
	 * @date 30 sept. 2023
	 */
	public void setStructMode(final boolean structMode) { this.structMode = structMode; }

	/**
	 * Checks if is struct mode.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is struct mode
	 * @date 30 sept. 2023
	 */
	public boolean isStructMode() { return structMode; }
}
