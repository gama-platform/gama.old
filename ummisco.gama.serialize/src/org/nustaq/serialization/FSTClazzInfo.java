/*******************************************************************************************************
 *
 * FSTClazzInfo.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import java.io.Externalizable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.nustaq.serialization.annotations.AnonymousTransient;
import org.nustaq.serialization.annotations.Conditional;
import org.nustaq.serialization.annotations.Flat;
import org.nustaq.serialization.annotations.OneOf;
import org.nustaq.serialization.annotations.Predict;
import org.nustaq.serialization.annotations.Serialize;
import org.nustaq.serialization.annotations.Transient;
import org.nustaq.serialization.annotations.Version;
import org.nustaq.serialization.util.FSTMap;
import org.nustaq.serialization.util.FSTUtil;

/**
 * Created with IntelliJ IDEA. User: Möller Date: 03.11.12 Time: 13:08 To change this template use File | Settings |
 * File Templates.
 */
public final class FSTClazzInfo {

	/** The Buffer constructor meta. */
	// cache constructor per class (big saving in permspace)
	public static boolean BufferConstructorMeta = true;

	/** The Buffer field meta. */
	// cache and share class.getDeclaredFields amongst all fstconfigs
	public static boolean BufferFieldMeta = true;

	/**
	 * cache + share j.reflect.Field. This can be cleared in case it gets too fat/leaks mem (e.g. class reloading)
	 */
	public static ConcurrentHashMap<Class, Field[]> sharedFieldSets = new ConcurrentHashMap<>();

	/** The Constant defFieldComparator. */
	public static final Comparator<FSTFieldInfo> defFieldComparator = (o1, o2) -> {
		int res = 0;

		if (o1.getVersion() != o2.getVersion()) return o1.getVersion() < o2.getVersion() ? -1 : 1;

		// order: version, boolean, primitives, conditionals, object references
		if (o1.getType() == boolean.class && o2.getType() != boolean.class) return -1;
		if (o1.getType() != boolean.class && o2.getType() == boolean.class) return 1;

		if (o1.isConditional() && !o2.isConditional()) {
			res = 1;
		} else if (!o1.isConditional() && o2.isConditional() || o1.isPrimitive() && !o2.isPrimitive()) {
			res = -1;
		} else if (!o1.isPrimitive() && o2.isPrimitive()) { res = 1; }
		// if (res == 0) // 64 bit / 32 bit issues
		// res = (int) (o1.getMemOffset() - o2.getMemOffset());
		if (res == 0) { res = o1.getType().getSimpleName().compareTo(o2.getType().getSimpleName()); }
		if (res == 0) { res = o1.getName().compareTo(o2.getName()); }
		if (res == 0)
			return o1.getField().getDeclaringClass().getName().compareTo(o2.getField().getDeclaringClass().getName());
		return res;
	};

	/** The predict. */
	Class[] predict;

	/** The ignore ann. */
	private final boolean ignoreAnn;

	/** The field map. */
	FSTMap<String, FSTFieldInfo> fieldMap;

	/** The read resolve method. */
	Method writeReplaceMethod, readResolveMethod;

	/** The comp info. */
	FSTMap<Class, FSTCompatibilityInfo> compInfo;

	/** The decoder attached. */
	Object decoderAttached; // for decoders

	/**
	 * Gets the decoder attached.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the decoder attached
	 * @date 30 sept. 2023
	 */
	public Object getDecoderAttached() { return decoderAttached; }

	/**
	 * Sets the decoder attached.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param decoderAttached
	 *            the new decoder attached
	 * @date 30 sept. 2023
	 */
	public void setDecoderAttached(final Object decoderAttached) { this.decoderAttached = decoderAttached; }

	/** The requires compatible mode. */
	boolean requiresCompatibleMode;

	/** The externalizable. */
	boolean externalizable;

	/** The flat. */
	boolean flat; // never share instances of this class

	/** The is ascii name short string. */
	boolean isAsciiNameShortString = false;

	/** The requires init. */
	boolean requiresInit = false;

	/** The has transient. */
	boolean hasTransient;

	/** The ser. */
	FSTObjectSerializer ser;

	/** The field info. */
	FSTFieldInfo fieldInfo[]; // serializable fields

	/** The clazz. */
	Class clazz;

	/** The enum constants. */
	Object[] enumConstants;

	/** The cons. */
	Constructor cons;

	/** The clz id. */
	int clzId = -1;

	/** The struct size. */
	int structSize = 0;

	/** The conf. */
	FSTConfiguration conf;

	/** The instantiator. */
	protected FSTClassInstantiator instantiator; // initialized from FSTConfiguration in constructor

	/** The cross platform. */
	boolean crossPlatform;

	/**
	 * Instantiates a new FST clazz info.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @param clazz
	 *            the clazz
	 * @param infoRegistry
	 *            the info registry
	 * @param ignoreAnnotations
	 *            the ignore annotations
	 * @date 30 sept. 2023
	 */
	public FSTClazzInfo(final FSTConfiguration conf, final Class<?> clazz, final FSTClazzInfoRegistry infoRegistry,
			final boolean ignoreAnnotations) {
		this.conf = conf; // fixme: historically was not bound to conf but now is. Remove redundant state + refs (note:
							// may still be useful because of less pointerchasing)
		crossPlatform = conf.isCrossPlatform();
		this.clazz = clazz;
		enumConstants = clazz.getEnumConstants();
		ignoreAnn = ignoreAnnotations;
		createFields(clazz);

		instantiator = conf.getInstantiator(clazz);
		if (Externalizable.class.isAssignableFrom(clazz)) {
			externalizable = true;
			cons = instantiator.findConstructorForExternalize(clazz);
		} else {
			if (Serializable.class.isAssignableFrom(clazz)
					|| clazz == Object.class) {} else if (!conf.isForceSerializable() && getSer() == null)
						throw new RuntimeException(
								"Class " + clazz.getName() + " does not implement Serializable or externalizable");
			externalizable = false;
			cons = instantiator.findConstructorForSerializable(clazz);
		}
		if (!ignoreAnnotations) {
			Predict annotation = clazz.getAnnotation(Predict.class);
			if (annotation != null) { predict = annotation.value(); }
			flat = clazz.isAnnotationPresent(Flat.class);
		}

		if (cons != null) { cons.setAccessible(true); }

		final String name = clazz.getName();
		if (name.length() < 127) {
			isAsciiNameShortString = true;
			for (int i = 0; i < name.length(); i++) {
				if (name.charAt(i) > 127) {
					isAsciiNameShortString = false;
					break;
				}
			}
		}

		requiresInit = isExternalizable() || useCompatibleMode() || hasTransient || conf.isForceClzInit();

		if (useCompatibleMode() && crossPlatform && getSer() == null && !clazz.isEnum()) throw new RuntimeException(
				"cannot support legacy JDK serialization methods in crossplatform mode. Define a serializer for this class "
						+ clazz.getName());
	}

	/** The buffered name. */
	byte[] bufferedName;

	/**
	 * Gets the buffered name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the buffered name
	 * @date 30 sept. 2023
	 */
	public byte[] getBufferedName() {
		if (bufferedName == null) { bufferedName = getClazz().getName().getBytes(); }
		return bufferedName;
	}

	@Override
	public String toString() {
		return "FSTClazzInfo{" + "clazz=" + clazz + '}';
	}

	/**
	 * Checks if is ascii name short string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is ascii name short string
	 * @date 30 sept. 2023
	 */
	public boolean isAsciiNameShortString() { return isAsciiNameShortString; }

	/**
	 * Gets the clz id.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the clz id
	 * @date 30 sept. 2023
	 */
	public int getClzId() { return clzId; }

	/**
	 * Sets the clz id.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param clzId
	 *            the new clz id
	 * @date 30 sept. 2023
	 */
	public void setClzId(final int clzId) { this.clzId = clzId; }

	/**
	 * Gets the num bool fields.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the num bool fields
	 * @date 30 sept. 2023
	 */
	public int getNumBoolFields() {
		FSTFieldInfo[] fis = getFieldInfo();
		for (int i = 0; i < fis.length; i++) {
			FSTFieldInfo fstFieldInfo = fis[i];
			if (fstFieldInfo.getType() != boolean.class) return i;
		}
		return fis.length;
	}

	/**
	 * Checks if is externalizable.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is externalizable
	 * @date 30 sept. 2023
	 */
	public boolean isExternalizable() { return externalizable; }

	/**
	 * Checks if is flat.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is flat
	 * @date 30 sept. 2023
	 */
	public boolean isFlat() { return flat; }

	/**
	 * Gets the predict.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the predict
	 * @date 30 sept. 2023
	 */
	public Class[] getPredict() { return predict; }

	/**
	 * New instance.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param doesRequireInit
	 *            the does require init
	 * @return the object
	 * @date 30 sept. 2023
	 */
	public Object newInstance(final boolean doesRequireInit) {
		return instantiator.newInstance(clazz, cons, doesRequireInit || requiresInit, conf.isForceSerializable());
	}

	/**
	 * Sideeffect: sets hasTransient
	 *
	 * @param c
	 * @param res
	 * @return
	 */
	static ReentrantLock shareLock = new ReentrantLock(false);

	/**
	 * Gets the all fields.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param res
	 *            the res
	 * @return the all fields
	 * @date 30 sept. 2023
	 */
	public List<Field> getAllFields(final Class c, List<Field> res) {
		try {
			if (BufferFieldMeta) { shareLock.lock(); }
			if (res == null) { res = new ArrayList<>(); }
			if (c == null) return res;
			Field[] declaredFields = BufferFieldMeta && !conf.isStructMode() ? sharedFieldSets.get(c) : null;
			if (declaredFields == null) {
				declaredFields = c.getDeclaredFields();
				if (BufferFieldMeta && !conf.isStructMode()) { sharedFieldSets.put(c, declaredFields); }
			}
			List<Field> c1 = Arrays.asList(declaredFields);
			Collections.reverse(c1);
			for (int i = 0; i < c1.size(); i++) {
				Field field = c1.get(i);
				res.add(0, field);
			}
			for (int i = 0; i < res.size(); i++) {
				Field field = res.get(i);
				if (Modifier.isStatic(field.getModifiers()) || isTransient(c, field)) {
					if (isTransient(c, field)) { hasTransient = true; }
					res.remove(i);
					i--;
				}
			}
			List<Field> allFields = getAllFields(c.getSuperclass(), res);
			return new ArrayList<>(allFields);
		} finally {
			if (BufferFieldMeta) { shareLock.unlock(); }
		}
	}

	/**
	 * Checks if is transient.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param field
	 *            the field
	 * @return true, if is transient
	 * @date 30 sept. 2023
	 */
	private boolean isTransient(Class<?> c, final Field field) {
		if (Modifier.isTransient(field.getModifiers())) return true;
		while (c.getName().indexOf("$") >= 0) {
			c = c.getSuperclass(); // patch fuer reallive queries, kontraktor spore
		}
		if (field.getName().startsWith("this$") && c.getAnnotation(AnonymousTransient.class) != null) return true;
		return c.getAnnotation(Transient.class) != null && field.getAnnotation(Serialize.class) == null;
	}

	/**
	 * Gets the field info.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the field info
	 * @date 30 sept. 2023
	 */
	public FSTFieldInfo[] getFieldInfo() { return fieldInfo; }

	/**
	 * Gets the field info filtered.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param toRemove
	 *            the to remove
	 * @return the field info filtered
	 * @date 30 sept. 2023
	 */
	public final FSTFieldInfo[] getFieldInfoFiltered(final Class... toRemove) {
		FSTFieldInfo[] fis = getFieldInfo();
		int count = 0;
		for (FSTFieldInfo fi : fis) {
			boolean skip = false;
			for (Class aClass : toRemove) {
				if (fi.getField().getDeclaringClass() == aClass) {
					skip = true;
					break;
				}
			}
			if (!skip) { count++; }
		}
		FSTFieldInfo res[] = new FSTFieldInfo[count];
		count = 0;
		for (FSTFieldInfo fi : fis) {
			boolean skip = false;
			for (Class aClass : toRemove) {
				if (fi.getField().getDeclaringClass() == aClass) {
					skip = true;
					break;
				}
			}
			if (!skip) { res[count++] = fi; }
		}
		return res;
	}

	/**
	 * Gets the field info.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @param declaringClass
	 *            the declaring class
	 * @return the field info
	 * @date 30 sept. 2023
	 */
	public FSTFieldInfo getFieldInfo(final String name, final Class declaringClass) {
		if (fieldMap != null) {
			if (declaringClass == null) return fieldMap.get(name);
			return fieldMap.get(declaringClass.getName() + "#" + name); // FIXME: THIS IS VERY SLOW (only used by JSON /
																		// compatibility mode)
		}
		synchronized (this) {
			fieldMap = buildFieldMap();
			return getFieldInfo(name, declaringClass);
		}
	}

	/**
	 * Builds the field map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the FST map
	 * @date 30 sept. 2023
	 */
	private FSTMap<String, FSTFieldInfo> buildFieldMap() {
		FSTMap<String, FSTFieldInfo> res = new FSTMap<>(fieldInfo.length);
		for (FSTFieldInfo element : fieldInfo) {
			Field field = element.getField();
			if (field != null) {
				res.put(field.getDeclaringClass().getName() + "#" + field.getName(), element);
				res.put(field.getName(), element);
			}
		}
		return res;
	}

	/**
	 * Creates the fields.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @date 30 sept. 2023
	 */
	private void createFields(final Class c) {
		if (c.isInterface() || c.isPrimitive()) return;
		List<Field> fields = getAllFields(c, null);
		fieldInfo = new FSTFieldInfo[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			fieldInfo[i] = createFieldInfo(field);
		}

		// compatibility info sort order
		Comparator<FSTFieldInfo> infocomp = (o1, o2) -> {
			int res = 0;
			res = o1.getType().getSimpleName().compareTo(o2.getType().getSimpleName());
			if (res == 0) { res = o1.getType().getName().compareTo(o2.getType().getName()); }
			if (res == 0) {
				Class declaringClass = o1.getType().getDeclaringClass();
				Class declaringClass1 = o2.getType().getDeclaringClass();
				if (declaringClass == null) return declaringClass1 == null ? 0 : -1;
				if (declaringClass1 == null) return 1;
				if (res == 0) return declaringClass.getName().compareTo(declaringClass1.getName());
			}
			return res;
		};

		// check if we actually need to build up compatibility info (memory intensive)
		boolean requiresCompatibilityData = false;
		if (!Externalizable.class.isAssignableFrom(c) && getSerNoStore() == null) {
			Class tmpCls = c;
			while (tmpCls != Object.class) {
				if (FSTUtil.findPrivateMethod(tmpCls, "writeObject", new Class<?>[] { ObjectOutputStream.class },
						Void.TYPE) != null
						|| FSTUtil.findPrivateMethod(tmpCls, "readObject", new Class<?>[] { ObjectInputStream.class },
								Void.TYPE) != null
						|| FSTUtil.findDerivedMethod(tmpCls, "writeReplace", null, Object.class) != null
						|| FSTUtil.findDerivedMethod(tmpCls, "readResolve", null, Object.class) != null) {
					requiresCompatibilityData = true;
					break;
				}
				tmpCls = tmpCls.getSuperclass();
			}
		}

		if (requiresCompatibilityData) {
			getCompInfo();
			fieldMap = buildFieldMap();
			Class curCl = c;
			fields.clear();
			while (curCl != Object.class) {
				ObjectStreamClass os = null;
				try {
					os = ObjectStreamClass.lookup(curCl);
				} catch (Exception e) {
					FSTUtil.<RuntimeException> rethrow(e);
				}
				if (os != null) {
					final ObjectStreamField[] fi = os.getFields();
					List<FSTFieldInfo> curClzFields = new ArrayList<>();
					if (fi != null) {
						for (ObjectStreamField objectStreamField : fi) {
							String ff = objectStreamField.getName();
							final FSTFieldInfo fstFieldInfo = fieldMap.get(curCl.getName() + "#" + ff);
							if (fstFieldInfo != null && fstFieldInfo.getField() != null) {
								curClzFields.add(fstFieldInfo);
								fields.add(fstFieldInfo.getField());
							} else {
								FSTFieldInfo fake = new FSTFieldInfo(null, null, true);
								fake.type = objectStreamField.getType();
								fake.fakeName = objectStreamField.getName();
								curClzFields.add(fake);
							}
						}
					}
					Collections.sort(curClzFields, infocomp);
					FSTCompatibilityInfo info = new FSTCompatibilityInfo(curClzFields, curCl);
					getCompInfo().put(curCl, info);
					if (info.needsCompatibleMode()) { requiresCompatibleMode = true; }
				}
				curCl = curCl.getSuperclass();
			}
		}

		// default sort order
		Comparator<FSTFieldInfo> comp = defFieldComparator;
		if (!conf.isStructMode()) { Arrays.sort(fieldInfo, comp); }
		int off = 8; // object header: length + clzId
		// for (FSTFieldInfo element : fieldInfo) {
		// FSTFieldInfo fstFieldInfo = element;
		// Align al = fstFieldInfo.getField().getAnnotation(Align.class);
		// if (al != null) {
		// fstFieldInfo.align = al.value();
		// int alignOff = fstFieldInfo.align(off);
		// fstFieldInfo.alignPad = alignOff - off;
		// off = alignOff;
		// }
		// fstFieldInfo.setStructOffset(off);
		// off += fstFieldInfo.getStructSize();
		// }
		structSize = off;
		writeReplaceMethod = FSTUtil.findDerivedMethod(c, "writeReplace", null, Object.class);
		readResolveMethod = FSTUtil.findDerivedMethod(c, "readResolve", null, Object.class);
		if (writeReplaceMethod != null) { writeReplaceMethod.setAccessible(true); }
		if (readResolveMethod != null) { readResolveMethod.setAccessible(true); }
		for (int i = 0; i < fieldInfo.length; i++) {
			FSTFieldInfo fstFieldInfo = fieldInfo[i];
			fstFieldInfo.indexId = i;
		}
	}

	/**
	 * Gets the struct size.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the struct size
	 * @date 30 sept. 2023
	 */
	public int getStructSize() { return structSize; }

	/**
	 * Use compatible mode.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if successful
	 * @date 30 sept. 2023
	 */
	public boolean useCompatibleMode() {
		return requiresCompatibleMode || writeReplaceMethod != null || readResolveMethod != null;
	}

	/** The fi count. */
	static AtomicInteger fiCount = new AtomicInteger(0);

	/** The miss count. */
	static AtomicInteger missCount = new AtomicInteger(0);

	/**
	 * Creates the field info.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param field
	 *            the field
	 * @return the FST field info
	 * @date 30 sept. 2023
	 */
	protected FSTFieldInfo createFieldInfo(final Field field) {
		FSTConfiguration.FieldKey key = null;
		if (conf.fieldInfoCache != null) {
			key = new FSTConfiguration.FieldKey(field.getDeclaringClass(), field.getName());
			FSTFieldInfo res = conf.fieldInfoCache.get(key);
			if (res != null) {
				fiCount.incrementAndGet();
				return res;
			}
		}
		field.setAccessible(true);
		Predict predict = crossPlatform ? null : field.getAnnotation(Predict.class);
		FSTFieldInfo result = new FSTFieldInfo(predict != null ? predict.value() : null, field, ignoreAnn);
		if (conf.fieldInfoCache != null && key != null) { conf.fieldInfoCache.put(key, result); }
		missCount.incrementAndGet();
		return result;
	}

	/**
	 * Gets the read resolve method.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the read resolve method
	 * @date 30 sept. 2023
	 */
	public Method getReadResolveMethod() { return readResolveMethod; }

	/**
	 * Gets the write replace method.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the write replace method
	 * @date 30 sept. 2023
	 */
	public Method getWriteReplaceMethod() { return writeReplaceMethod; }

	/**
	 * Gets the clazz.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the clazz
	 * @date 30 sept. 2023
	 */
	public Class getClazz() { return clazz; }

	/**
	 * Gets the enum constants.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the enum constants
	 * @date 30 sept. 2023
	 */
	public Object[] getEnumConstants() { return enumConstants; }

	/**
	 * Gets the comp info.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the comp info
	 * @date 30 sept. 2023
	 */
	public FSTMap<Class, FSTCompatibilityInfo> getCompInfo() {
		if (compInfo == null) {
			compInfo = new FSTMap<>(3); // just avoid edge case NPE's
		}
		return compInfo;
	}

	/**
	 * The Class FSTFieldInfo.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	public final static class FSTFieldInfo {

		/** The Constant BOOL. */
		final public static int BOOL = 1;

		/** The Constant BYTE. */
		final public static int BYTE = 2;

		/** The Constant CHAR. */
		final public static int CHAR = 3;

		/** The Constant SHORT. */
		final public static int SHORT = 4;

		/** The Constant INT. */
		final public static int INT = 5;

		/** The Constant LONG. */
		final public static int LONG = 6;

		/** The Constant FLOAT. */
		final public static int FLOAT = 7;

		/** The Constant DOUBLE. */
		final public static int DOUBLE = 8;

		/** The possible classes. */
		Class possibleClasses[];

		/** The last info. */
		FSTClazzInfo lastInfo; // cache last class stored (can save a hash lookup)

		/** The one of. */
		String oneOf[] = null;

		/** The array dim. */
		int arrayDim;

		/** The array type. */
		Class arrayType;

		/** The flat. */
		boolean flat = false;

		/** The is conditional. */
		boolean isConditional = false;

		/** The field. */
		final Field field;

		/** The type. */
		Class type;

		/** The integral. */
		boolean integral = false;

		/** The primitive. */
		boolean primitive = false;

		/** The is arr. */
		boolean isArr = false;

		/** The version. */
		byte version;

		/** The integral type. */
		int integralType;

		/** The mem offset. */
		long memOffset = -1;

		/** The struct offset. */
		int structOffset = 0;

		/** The index id. */
		int indexId; // position in serializable fields array

		/** The align. */
		int align = 0;

		/** The align pad. */
		int alignPad = 0;

		/** The buffered name. */
		Object bufferedName; // cache byte rep of field name (used for cross platform)

		// hack required for compatibility with ancient JDK mechanics (cross JDK, e.g. Android <=> OpenJDK ).
		// in rare cases, a field used in putField is not present as a real field
		/** The fake name. */
		// in this case only these of a fieldinfo are set
		public String fakeName;

		/**
		 * Instantiates a new FST field info.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param possibleClasses
		 *            the possible classes
		 * @param fi
		 *            the fi
		 * @param ignoreAnnotations
		 *            the ignore annotations
		 * @date 30 sept. 2023
		 */
		public FSTFieldInfo(final Class[] possibleClasses, final Field fi, final boolean ignoreAnnotations) {
			this.possibleClasses = possibleClasses;
			field = fi;
			if (fi == null) {
				isArr = false;
			} else {
				isArr = field.getType().isArray();
				type = fi.getType();
				primitive = type.isPrimitive();
				if (FSTUtil.unFlaggedUnsafe != null) {
					fi.setAccessible(true);
					if (!Modifier.isStatic(fi.getModifiers())) {
						try {
							memOffset = (int) FSTUtil.unFlaggedUnsafe.objectFieldOffset(fi);
						} catch (Throwable th) {
							// throw FSTUtil.rethrow(th);
						}
					}
				}
			}
			if (isArray()) {
				String clName = field.getType().getName();
				arrayDim = 1 + clName.lastIndexOf('[');
				arrayType = calcComponentType(field.getType());
			}
			calcIntegral();
			if (fi != null && !ignoreAnnotations) {
				version = fi.isAnnotationPresent(Version.class) ? fi.getAnnotation(Version.class).value() : 0;
				flat = fi.isAnnotationPresent(Flat.class);
				isConditional = fi.isAnnotationPresent(Conditional.class);
				if (isIntegral()) { isConditional = false; }
				OneOf annotation = fi.getAnnotation(OneOf.class);
				if (annotation != null) { oneOf = annotation.value(); }
			}

		}

		/**
		 * Gets the version.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the version
		 * @date 30 sept. 2023
		 */
		public byte getVersion() { return version; }

		/**
		 * Gets the buffered name.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the buffered name
		 * @date 30 sept. 2023
		 */
		public Object getBufferedName() { return bufferedName; }

		/**
		 * Sets the buffered name.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param bufferedName
		 *            the new buffered name
		 * @date 30 sept. 2023
		 */
		public void setBufferedName(final Object bufferedName) { this.bufferedName = bufferedName; }

		/**
		 * Align.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param off
		 *            the off
		 * @return the int
		 * @date 30 sept. 2023
		 */
		public int align(int off) {
			while (off / align * align != off) { off++; }
			return off;
		}

		/**
		 * Gets the index id.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the index id
		 * @date 30 sept. 2023
		 */
		public int getIndexId() { return indexId; }

		/**
		 * Gets the struct offset.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the struct offset
		 * @date 30 sept. 2023
		 */
		public int getStructOffset() { return structOffset; }

		/**
		 * Sets the struct offset.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param structOffset
		 *            the new struct offset
		 * @date 30 sept. 2023
		 */
		public void setStructOffset(final int structOffset) { this.structOffset = structOffset; }

		/**
		 * Gets the one of.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the one of
		 * @date 30 sept. 2023
		 */
		public String[] getOneOf() { return oneOf; }

		/**
		 * Gets the mem offset.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the mem offset
		 * @date 30 sept. 2023
		 */
		public long getMemOffset() { return memOffset; }

		/**
		 * Gets the align.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the align
		 * @date 30 sept. 2023
		 */
		public int getAlign() { return align; }

		/**
		 * Gets the align pad.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the align pad
		 * @date 30 sept. 2023
		 */
		public int getAlignPad() { return alignPad; }

		/**
		 * Checks if is conditional.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return true, if is conditional
		 * @date 30 sept. 2023
		 */
		public boolean isConditional() { return isConditional; }

		/**
		 * Gets the last info.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the last info
		 * @date 30 sept. 2023
		 */
		public FSTClazzInfo getLastInfo() { return lastInfo; }

		/**
		 * Sets the last info.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param lastInfo
		 *            the new last info
		 * @date 30 sept. 2023
		 */
		public void setLastInfo(final FSTClazzInfo lastInfo) { this.lastInfo = lastInfo; }

		/**
		 * Calc component type.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param c
		 *            the c
		 * @return the class
		 * @date 30 sept. 2023
		 */
		Class calcComponentType(final Class c) {
			if (c.isArray()) return calcComponentType(c.getComponentType());
			return c;
		}

		/**
		 * Checks if is volatile.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return true, if is volatile
		 * @date 30 sept. 2023
		 */
		public boolean isVolatile() { return Modifier.isVolatile(getField().getModifiers()); }

		/**
		 * Gets the type.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the type
		 * @date 30 sept. 2023
		 */
		public Class getType() { return type; }

		/**
		 * Checks if is array.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return true, if is array
		 * @date 30 sept. 2023
		 */
		public boolean isArray() { return isArr; }

		/**
		 * Gets the array depth.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the array depth
		 * @date 30 sept. 2023
		 */
		public int getArrayDepth() { return arrayDim; }

		/**
		 * Gets the array type.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the array type
		 * @date 30 sept. 2023
		 */
		public Class getArrayType() { return arrayType; }

		/**
		 * Gets the possible classes.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the possible classes
		 * @date 30 sept. 2023
		 */
		public Class[] getPossibleClasses() { return possibleClasses; }

		/**
		 * Sets the possible classes.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param possibleClasses
		 *            the new possible classes
		 * @date 30 sept. 2023
		 */
		void setPossibleClasses(final Class[] possibleClasses) { this.possibleClasses = possibleClasses; }

		/**
		 * Gets the field.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the field
		 * @date 30 sept. 2023
		 */
		public Field getField() { return field; }

		/**
		 * Calc integral.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @date 30 sept. 2023
		 */
		public void calcIntegral() {
			if (field == null) return;
			if (isArray()) {
				integral = isIntegral(getArrayType());
			} else {
				integral = isIntegral(field.getType());

				Class type = field.getType();
				integralType = getIntegralCode(type);
			}
		}

		/**
		 * Gets the integral code.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param type
		 *            the type
		 * @return the integral code
		 * @date 30 sept. 2023
		 */
		public static int getIntegralCode(final Class type) {
			if (type == boolean.class) return BOOL;
			if (type == byte.class) return BYTE;
			if (type == char.class) return CHAR;
			if (type == short.class) return SHORT;
			if (type == int.class) return INT;
			if (type == long.class) return LONG;
			if (type == float.class) return FLOAT;
			if (type == double.class) return DOUBLE;
			return 0;
		}

		/**
		 * only set if is not an array, but a direct native field type
		 *
		 * @return
		 */
		public int getIntegralType() { return integralType; }

		/**
		 * Checks if is integral.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param type
		 *            the type
		 * @return true, if is integral
		 * @date 30 sept. 2023
		 */
		public boolean isIntegral(final Class type) {
			return type.isPrimitive();
		}

		/**
		 * @return wether this is primitive or an array of primitives
		 */
		public boolean isIntegral() { return integral; }

		/**
		 * Gets the desc.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the desc
		 * @date 30 sept. 2023
		 */
		public String getDesc() {
			return field != null ? "<" + field.getName() + " of " + field.getDeclaringClass().getSimpleName() + ">"
					: "<undefined referencee>";
		}

		@Override
		public String toString() {
			return getDesc();
		}

		/**
		 * Checks if is flat.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return true, if is flat
		 * @date 30 sept. 2023
		 */
		public boolean isFlat() { return flat; }

		/**
		 * Gets the component struct size.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the component struct size
		 * @date 30 sept. 2023
		 */
		public int getComponentStructSize() {
			if (arrayType == boolean.class || arrayType == byte.class) return 1;
			if (arrayType == char.class || arrayType == short.class) return 2;
			if (arrayType == int.class || arrayType == float.class) return 4;
			if (arrayType == long.class || arrayType == double.class) return 8;
			return 0; // object => cannot decide
		}

		/**
		 * Gets the struct size.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the struct size
		 * @date 30 sept. 2023
		 */
		public int getStructSize() {
			if (type == boolean.class || type == byte.class) return 1;
			if (type == char.class || type == short.class) return 2;
			if (type == int.class || type == float.class) return 4;
			if (type == long.class || type == double.class) return 8;
			if (isArray()) {
				if (isIntegral()) return 8; // pointer+length
				return 16; // pointer+length+elemsiz+pointertype
			}
			return 4;
		}

		/**
		 * Checks if is primitive.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return true, if is primitive
		 * @date 30 sept. 2023
		 */
		public boolean isPrimitive() { return primitive; }

		/**
		 * Gets the byte value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param obj
		 *            the obj
		 * @return the byte value
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public int getByteValue(final Object obj) throws IllegalAccessException {
			if (memOffset >= 0) return FSTUtil.unFlaggedUnsafe.getByte(obj, memOffset);
			return field.getByte(obj);
		}

		/**
		 * Gets the char value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param obj
		 *            the obj
		 * @return the char value
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public int getCharValue(final Object obj) throws IllegalAccessException {
			if (memOffset >= 0) return FSTUtil.unFlaggedUnsafe.getChar(obj, memOffset);
			return field.getChar(obj);
		}

		/**
		 * Gets the short value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param obj
		 *            the obj
		 * @return the short value
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public int getShortValue(final Object obj) throws IllegalAccessException {
			if (memOffset >= 0) return FSTUtil.unFlaggedUnsafe.getShort(obj, memOffset);
			return field.getShort(obj);
		}

		/**
		 * Gets the int value unsafe.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param obj
		 *            the obj
		 * @return the int value unsafe
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public int getIntValueUnsafe(final Object obj) throws IllegalAccessException {
			return FSTUtil.unFlaggedUnsafe.getInt(obj, memOffset);
		}

		/**
		 * Gets the long value unsafe.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param obj
		 *            the obj
		 * @return the long value unsafe
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public long getLongValueUnsafe(final Object obj) throws IllegalAccessException {
			return FSTUtil.unFlaggedUnsafe.getLong(obj, memOffset);
		}

		/**
		 * Gets the boolean value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param obj
		 *            the obj
		 * @return the boolean value
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public boolean getBooleanValue(final Object obj) throws IllegalAccessException {
			if (memOffset >= 0) return FSTUtil.unFlaggedUnsafe.getBoolean(obj, memOffset);
			return field.getBoolean(obj);
		}

		/**
		 * Warning: crashes if not an object ref ! use getField().get() for a safe version ..
		 *
		 * @param obj
		 * @return
		 * @throws IllegalAccessException
		 */
		public Object getObjectValue(final Object obj) throws IllegalAccessException {
			if (memOffset >= 0) return FSTUtil.unFlaggedUnsafe.getObject(obj, memOffset);
			return field.get(obj);
		}

		/**
		 * Gets the float value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param obj
		 *            the obj
		 * @return the float value
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public float getFloatValue(final Object obj) throws IllegalAccessException {
			if (memOffset >= 0) return FSTUtil.unFlaggedUnsafe.getFloat(obj, memOffset);
			return field.getFloat(obj);
		}

		/**
		 * Sets the char value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param newObj
		 *            the new obj
		 * @param c
		 *            the c
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public void setCharValue(final Object newObj, final char c) throws IllegalAccessException {
			if (memOffset >= 0) {
				FSTUtil.unFlaggedUnsafe.putChar(newObj, memOffset, c);
				return;
			}
			field.setChar(newObj, c);
		}

		/**
		 * Sets the short value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param newObj
		 *            the new obj
		 * @param i1
		 *            the i 1
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public void setShortValue(final Object newObj, final short i1) throws IllegalAccessException {
			if (memOffset >= 0) {
				FSTUtil.unFlaggedUnsafe.putShort(newObj, memOffset, i1);
				return;
			}
			field.setShort(newObj, i1);
		}

		/**
		 * Sets the object value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param target
		 *            the target
		 * @param value
		 *            the value
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public void setObjectValue(final Object target, final Object value) throws IllegalAccessException {
			if (memOffset >= 0) {
				FSTUtil.unFlaggedUnsafe.putObject(target, memOffset, value);
				return;
			}
			field.set(target, value);
		}

		/**
		 * Sets the float value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param newObj
		 *            the new obj
		 * @param l
		 *            the l
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public void setFloatValue(final Object newObj, final float l) throws IllegalAccessException {
			if (memOffset >= 0) {
				FSTUtil.unFlaggedUnsafe.putFloat(newObj, memOffset, l);
				return;
			}
			field.setFloat(newObj, l);
		}

		/**
		 * Sets the double value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param newObj
		 *            the new obj
		 * @param l
		 *            the l
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public void setDoubleValue(final Object newObj, final double l) throws IllegalAccessException {
			if (memOffset >= 0) {
				FSTUtil.unFlaggedUnsafe.putDouble(newObj, memOffset, l);
				return;
			}
			field.setDouble(newObj, l);
		}

		/**
		 * Sets the long value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param newObj
		 *            the new obj
		 * @param i1
		 *            the i 1
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public void setLongValue(final Object newObj, final long i1) throws IllegalAccessException {
			if (memOffset >= 0) {
				FSTUtil.unFlaggedUnsafe.putLong(newObj, memOffset, i1);
				return;
			}
			field.setLong(newObj, i1);
		}

		/**
		 * Gets the long value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param obj
		 *            the obj
		 * @return the long value
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public long getLongValue(final Object obj) throws IllegalAccessException {
			if (memOffset >= 0) return FSTUtil.unFlaggedUnsafe.getLong(obj, memOffset);
			return field.getLong(obj);
		}

		/**
		 * Gets the double value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param obj
		 *            the obj
		 * @return the double value
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public double getDoubleValue(final Object obj) throws IllegalAccessException {
			if (memOffset >= 0) return FSTUtil.unFlaggedUnsafe.getDouble(obj, memOffset);
			return field.getDouble(obj);
		}

		/**
		 * Sets the int value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param newObj
		 *            the new obj
		 * @param i1
		 *            the i 1
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public void setIntValue(final Object newObj, final int i1) throws IllegalAccessException {
			if (memOffset >= 0) {
				FSTUtil.unFlaggedUnsafe.putInt(newObj, memOffset, i1);
				return;
			}
			field.setInt(newObj, i1);
		}

		/**
		 * Gets the int value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param obj
		 *            the obj
		 * @return the int value
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public int getIntValue(final Object obj) throws IllegalAccessException {
			if (memOffset >= 0) return FSTUtil.unFlaggedUnsafe.getInt(obj, memOffset);
			return field.getInt(obj);
		}

		/**
		 * Sets the boolean value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param newObj
		 *            the new obj
		 * @param i1
		 *            the i 1
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public void setBooleanValue(final Object newObj, final boolean i1) throws IllegalAccessException {
			if (memOffset >= 0) {
				FSTUtil.unFlaggedUnsafe.putBoolean(newObj, memOffset, i1);
				return;
			}
			field.setBoolean(newObj, i1);
		}

		/**
		 * Sets the byte value.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param newObj
		 *            the new obj
		 * @param b
		 *            the b
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @date 30 sept. 2023
		 */
		public void setByteValue(final Object newObj, final byte b) throws IllegalAccessException {
			if (memOffset >= 0) {
				FSTUtil.unFlaggedUnsafe.putByte(newObj, memOffset, b);
				return;
			}
			field.setByte(newObj, b);
		}

		/**
		 * Gets the name.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the name
		 * @date 30 sept. 2023
		 */
		public String getName() { return field != null ? field.getName() : fakeName; }
	}

	/**
	 * sideeffecting: if no ser is found, next lookup will return null immediate
	 *
	 * @return
	 */
	public FSTObjectSerializer getSer() {
		if (ser == null) {
			if (clazz == null) return null;
			ser = getSerNoStore();
			if (ser == null) { ser = FSTSerializerRegistry.NULL; }
		}
		if (ser == FSTSerializerRegistry.NULL) return null;
		return ser;
	}

	/**
	 * Gets the ser no store.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the ser no store
	 * @date 30 sept. 2023
	 */
	// no sideffecting lookup
	public FSTObjectSerializer getSerNoStore() {
		return conf.getCLInfoRegistry().getSerializerRegistry().getSerializer(clazz);
	}

	/**
	 * The Class FSTCompatibilityInfo.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	static class FSTCompatibilityInfo {

		/** The read method. */
		Method writeMethod, readMethod;

		/** The object stream class. */
		ObjectStreamClass objectStreamClass;

		/** The infos. */
		List<FSTFieldInfo> infos;

		/** The clazz. */
		Class clazz;

		/** The info arr. */
		FSTFieldInfo infoArr[];

		/**
		 * Instantiates a new FST compatibility info.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param inf
		 *            the inf
		 * @param c
		 *            the c
		 * @date 30 sept. 2023
		 */
		public FSTCompatibilityInfo(final List<FSTFieldInfo> inf, final Class c) {
			readClazz(c);
			infos = inf;
			clazz = c;
		}

		/**
		 * Gets the fields.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the fields
		 * @date 30 sept. 2023
		 */
		public List<FSTFieldInfo> getFields() { return infos; }

		/**
		 * Gets the field array.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the field array
		 * @date 30 sept. 2023
		 */
		public FSTFieldInfo[] getFieldArray() {
			if (infoArr == null) {
				List<FSTClazzInfo.FSTFieldInfo> fields = getFields();
				final FSTFieldInfo[] fstFieldInfos = new FSTFieldInfo[fields.size()];
				fields.toArray(fstFieldInfos);
				Arrays.sort(fstFieldInfos, defFieldComparator);
				infoArr = fstFieldInfos;
			}
			return infoArr;
		}

		/**
		 * Gets the clazz.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the clazz
		 * @date 30 sept. 2023
		 */
		public Class getClazz() { return clazz; }

		/**
		 * Needs compatible mode.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return true, if successful
		 * @date 30 sept. 2023
		 */
		public boolean needsCompatibleMode() {
			return writeMethod != null || readMethod != null;
		}

		/**
		 * Read clazz.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param c
		 *            the c
		 * @date 30 sept. 2023
		 */
		public void readClazz(final Class c) {
			writeMethod =
					FSTUtil.findPrivateMethod(c, "writeObject", new Class<?>[] { ObjectOutputStream.class }, Void.TYPE);
			readMethod =
					FSTUtil.findPrivateMethod(c, "readObject", new Class<?>[] { ObjectInputStream.class }, Void.TYPE);
			if (writeMethod != null) { writeMethod.setAccessible(true); }
			if (readMethod != null) { readMethod.setAccessible(true); }
		}

		/**
		 * Gets the read method.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the read method
		 * @date 30 sept. 2023
		 */
		public Method getReadMethod() { return readMethod; }

		/**
		 * Sets the read method.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param readMethod
		 *            the new read method
		 * @date 30 sept. 2023
		 */
		public void setReadMethod(final Method readMethod) { this.readMethod = readMethod; }

		/**
		 * Gets the write method.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the write method
		 * @date 30 sept. 2023
		 */
		public Method getWriteMethod() { return writeMethod; }

		/**
		 * Sets the write method.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param writeMethod
		 *            the new write method
		 * @date 30 sept. 2023
		 */
		public void setWriteMethod(final Method writeMethod) { this.writeMethod = writeMethod; }

		/**
		 * Checks if is asymmetric.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return true, if is asymmetric
		 * @date 30 sept. 2023
		 */
		public boolean isAsymmetric() {
			return getReadMethod() == null && getWriteMethod() != null
					|| getWriteMethod() == null && getReadMethod() != null;
		}

	}

}
