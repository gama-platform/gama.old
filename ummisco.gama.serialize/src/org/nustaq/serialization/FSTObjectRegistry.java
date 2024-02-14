/*******************************************************************************************************
 *
 * FSTObjectRegistry.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import org.nustaq.serialization.util.FSTIdentity2IdMap;
import org.nustaq.serialization.util.FSTInt2ObjectMap;
import org.nustaq.serialization.util.FSTUtil;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 11.11.12 Time: 15:34 To change this template use File | Settings | File
 * Templates.
 */
public final class FSTObjectRegistry {

	/** The Constant OBJ_DIVISOR. */
	public static final int OBJ_DIVISOR = 16;

	/** The pos map size. */
	public static int POS_MAP_SIZE = 1000; // reduce this for testing

	/** The disabled. */
	boolean disabled = false;

	/** The objects. */
	FSTIdentity2IdMap objects = new FSTIdentity2IdMap(11); // object => id

	/** The id to object. */
	FSTInt2ObjectMap<Object> idToObject;

	/** The reuse map. */
	Object reuseMap[] = new Object[POS_MAP_SIZE];

	/** The highest pos. */
	private int highestPos = -1;

	/**
	 * Instantiates a new FST object registry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @date 11 févr. 2024
	 */
	public FSTObjectRegistry(final FSTConfiguration conf) {

		disabled = !conf.isShareReferences();
		idToObject = conf.intToObjectMapFactory.createMap(11);
	}

	/**
	 * Clear for read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @date 11 févr. 2024
	 */
	public void clearForRead(final FSTConfiguration conf) {
		disabled = !conf.isShareReferences();
		if (!disabled) {
			idToObject.clear();
			if (highestPos > -1) { FSTUtil.clear(reuseMap, highestPos + 1); }
		}
		highestPos = -1;
	}

	/**
	 * Clear for write.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @date 11 févr. 2024
	 */
	public void clearForWrite(final FSTConfiguration conf) {
		disabled = !conf.isShareReferences();
		if (!disabled) {
			if (objects.size() > 0 && objects.keysLength() > 6 * objects.size()) {
				objects = new FSTIdentity2IdMap(objects.size());
			} else {
				objects.clear();
			}
		}
	}

	/**
	 * Gets the read registered object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param handle
	 *            the handle
	 * @return the read registered object
	 * @date 11 févr. 2024
	 */
	public Object getReadRegisteredObject(final int handle) {
		if (disabled) return null;
		int pos = handle / OBJ_DIVISOR;
		if (pos < reuseMap.length) {
			if (reuseMap[pos] == null) return null;
			Object candidate = idToObject.get(handle);
			if (candidate == null) return reuseMap[pos];
			return candidate;
		}
		return idToObject.get(handle);
	}

	/**
	 * Replace.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param old
	 *            the old
	 * @param replaced
	 *            the replaced
	 * @param streamPos
	 *            the stream pos
	 * @date 11 févr. 2024
	 */
	public void replace(final Object old, final Object replaced, final int streamPos) {
		int pos = streamPos / OBJ_DIVISOR;
		final Object[] reuseMap = this.reuseMap;
		if ((pos < reuseMap.length) && (this.reuseMap[pos] == old || this.reuseMap[pos] == null || reuseMap[pos] == old)) {
			this.reuseMap[pos] = replaced;
		} else {
			idToObject.put(streamPos, replaced);
		}
	}

	/**
	 * Register object for read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param o
	 *            the o
	 * @param streamPosition
	 *            the stream position
	 * @date 11 févr. 2024
	 */
	public void registerObjectForRead(final Object o, final int streamPosition) {
		if (disabled /* || streamPosition <= lastRegisteredReadPos */) return;
		// System.out.println("POK REGISTER AT READ:"+streamPosition+" : "+o);
		int pos = streamPosition / OBJ_DIVISOR;
		Object[] reuseMap = this.reuseMap;
		if (pos < reuseMap.length) {
			highestPos = pos > highestPos ? pos : highestPos;
			if (this.reuseMap[pos] == null) {
				this.reuseMap[pos] = o;
			} else {
				idToObject.put(streamPosition, o);
			}
		} else {
			idToObject.put(streamPosition, o);
		}
	}

	/**
	 * add an object to the register, return handle if already present. Called during write only
	 *
	 * @param o
	 * @param streamPosition
	 * @return 0 if added, handle if already present
	 */
	public int registerObjectForWrite(final Object o, final int streamPosition, final FSTClazzInfo clzInfo,
			final int reUseType[]) {
		if (disabled) return Integer.MIN_VALUE;
		// System.out.println("REGISTER AT WRITE:"+streamPosition+" "+o.getClass().getSimpleName());
		// final Class clazz = o.getClass();
		if (clzInfo == null) { // array oder enum oder primitive
			// unused ?
			// clzInfo = reg.getCLInfo(clazz);
		} else if (clzInfo.isFlat()) return Integer.MIN_VALUE;
		int handle = objects.putOrGet(o, streamPosition);
		if (handle >= 0) {
			// if ( idToObject.get(handle) == null ) { // (*) (can get improved)
			// idToObject.add(handle, o);
			// }
			reUseType[0] = 0;
			return handle;
		}
		return Integer.MIN_VALUE;
	}

	/**
	 * Gets the object size.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the object size
	 * @date 11 févr. 2024
	 */
	public int getObjectSize() { return objects.size(); }

}
