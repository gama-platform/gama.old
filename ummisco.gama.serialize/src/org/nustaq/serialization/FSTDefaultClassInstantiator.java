/*******************************************************************************************************
 *
 * FSTDefaultClassInstantiator.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

import org.nustaq.serialization.util.FSTUtil;

import sun.reflect.ReflectionFactory;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Created by ruedi on 12.12.14.
 *
 * Valid for common x86 JDK's (not android)
 *
 */
public class FSTDefaultClassInstantiator implements FSTClassInstantiator {

	/**
	 * reduce number of generated classes. Can be cleared riskless in case.
	 */
	public static ConcurrentHashMap<Class, Constructor> constructorMap = new ConcurrentHashMap<>();

	@Override
	public Object newInstance(final Class clazz, final Constructor cons, final boolean doesRequireInit,
			final boolean unsafeAsLastResort) {
		try {
			if (!doesRequireInit && FSTUtil.unFlaggedUnsafe != null)
				return FSTUtil.unFlaggedUnsafe.allocateInstance(clazz);
			if (cons == null && unsafeAsLastResort) {
				// best effort. use Unsafe to instantiate.
				// Warning: if class contains transient fields which have default values assigned ('transient int x =
				// 3'),
				// those will not be assigned after deserialization as unsafe instantiation does not execute any default
				// construction code.
				// Define a public no-arg constructor to avoid this behaviour (rarely an issue, but there are cases).
				if (FSTUtil.unFlaggedUnsafe != null) return FSTUtil.unFlaggedUnsafe.allocateInstance(clazz);
				throw new RuntimeException(
						"no suitable constructor found and no Unsafe instance avaiable. Can't instantiate "
								+ clazz.getName());
			}
			if (cons == null) return null;
			return cons.newInstance();
		} catch (Throwable ignored) {
			DEBUG.ERR("Failed to construct new instance", ignored);
			return null;
		}
	}

	@Override
	public Constructor findConstructorForExternalize(final Class clazz) {
		try {
			Constructor c = clazz.getDeclaredConstructor((Class[]) null);
			if (c == null) return null;
			c.setAccessible(true);
			if ((c.getModifiers() & Modifier.PUBLIC) != 0) return c;
			return null;
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	@Override
	public Constructor findConstructorForSerializable(final Class clazz) {
		if (!Serializable.class.isAssignableFrom(clazz)) // in case forceSerializable flag is present, just look for
															// no-arg constructor
			return findConstructorForExternalize(clazz);
		if (FSTClazzInfo.BufferConstructorMeta) {
			Constructor constructor = constructorMap.get(clazz);
			if (constructor != null) return constructor;
		}
		Class curCl = clazz;
		while (Serializable.class.isAssignableFrom(curCl)) { if ((curCl = curCl.getSuperclass()) == null) return null; }
		try {
			Constructor c = curCl.getDeclaredConstructor((Class[]) null);
			int mods = c.getModifiers();
			if ((mods & Modifier.PRIVATE) != 0
					|| (mods & (Modifier.PUBLIC | Modifier.PROTECTED)) == 0 && !FSTUtil.isPackEq(clazz, curCl))
				return null;
			c = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(clazz, c);
			c.setAccessible(true);

			if (FSTClazzInfo.BufferConstructorMeta) { constructorMap.put(clazz, c); }
			return c;
		} catch (NoClassDefFoundError | NoSuchMethodException ex) {
			return null;
		}
	}

}
