/*******************************************************************************************************
 *
 * FSTClazzNameRegistry.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.nustaq.serialization.util.FSTIdentity2IdMap;
import org.nustaq.serialization.util.FSTObject2IntMap;
import org.nustaq.serialization.util.FSTUtil;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 10.11.12 Time: 00:34
 *
 * maps classnames => id and vice versa.
 *
 * FSTConfiguration holds a parent containing default mappings (e.g. created by registerClass ). A stream instance then
 * creates a temporary instance to record/id encode classes dynamically during serialization. This way a class name is
 * only written once per object graph.
 *
 * This class is thread safe,
 *
 */
public class FSTClazzNameRegistry {

	/** The Constant LOWEST_CLZ_ID. */
	public static final int LOWEST_CLZ_ID = 3;

	/** The Constant FIRST_USER_CLZ_ID. */
	public static final int FIRST_USER_CLZ_ID = 1000;

	/** The clz to id. */
	FSTIdentity2IdMap clzToId;

	/** The id to clz. */
	FSTClazzInfo idToClz[];

	/** The parent. */
	FSTClazzNameRegistry parent;

	/** The class id count. */
	int classIdCount = LOWEST_CLZ_ID;

	/**
	 * Instantiates a new FST clazz name registry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param par
	 *            the par
	 * @date 30 sept. 2023
	 */
	public FSTClazzNameRegistry(final FSTClazzNameRegistry par) {
		parent = par;
		if (parent != null) {
			classIdCount = Math.max(FIRST_USER_CLZ_ID, parent.classIdCount + 1);
			clzToId = new FSTIdentity2IdMap(13);
			idToClz = new FSTClazzInfo[31];
		} else {
			clzToId = new FSTIdentity2IdMap(FSTObject2IntMap.adjustSize(400));
			idToClz = new FSTClazzInfo[200];
		}
	}

	/**
	 * Clear.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	public void clear() {
		if (clzToId.size() > 0) {
			clzToId.clear();
			// idToClz.clear();
		}
		classIdCount = LOWEST_CLZ_ID;
		if (parent != null) { classIdCount = Math.max(FIRST_USER_CLZ_ID, parent.classIdCount + 1); }
	}

	/**
	 * Register class.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param conf
	 *            the conf
	 * @date 30 sept. 2023
	 */
	// for read => always increase handle (wg. replaceObject)
	public void registerClass(final Class c, final FSTConfiguration conf) {
		if (getIdFromClazz(c) != Integer.MIN_VALUE) return;
		registerClassNoLookup(c, null, conf);
	}

	/**
	 * Register class no lookup.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param cli
	 *            the cli
	 * @param conf
	 *            the conf
	 * @date 30 sept. 2023
	 */
	private void registerClassNoLookup(final Class c, final FSTClazzInfo cli, final FSTConfiguration conf) {
		addClassMapping(c, classIdCount++, cli, conf);
	}

	/**
	 * Register class.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param code
	 *            the code
	 * @param conf
	 *            the conf
	 * @date 30 sept. 2023
	 */
	public void registerClass(final Class c, final int code, final FSTConfiguration conf) {
		if (getIdFromClazz(c) != Integer.MIN_VALUE) return;
		addClassMapping(c, code, null, conf);
	}

	/**
	 * Adds the class mapping.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param id
	 *            the id
	 * @param clInfo
	 *            the cl info
	 * @param conf
	 *            the conf
	 * @date 30 sept. 2023
	 */
	protected void addClassMapping(final Class c, final int id, FSTClazzInfo clInfo, final FSTConfiguration conf) {
		clzToId.put(c, id);
		if (clInfo == null) { clInfo = conf.getCLInfoRegistry().getCLInfo(c, conf); }
		if (idToClz.length <= id) {
			final FSTClazzInfo[] tmp = new FSTClazzInfo[id + 100];
			System.arraycopy(idToClz, 0, tmp, 0, idToClz.length);
			idToClz = tmp;
		}
		idToClz[id] = clInfo;
		if (parent == null) { clInfo.setClzId(id); }
	}

	/**
	 * Gets the id from clazz.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @return the id from clazz
	 * @date 30 sept. 2023
	 */
	public int getIdFromClazz(final Class c) {
		int res = Integer.MIN_VALUE;
		if (parent != null) { res = parent.getIdFromClazz(c); }
		if (res == Integer.MIN_VALUE) { res = clzToId.get(c); }
		return res;
	}

	/**
	 * Encode class.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param out
	 *            the out
	 * @param ci
	 *            the ci
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 30 sept. 2023
	 */
	public void encodeClass(final FSTEncoder out, final FSTClazzInfo ci) throws IOException {
		int clzId = ci.getClzId();
		if (clzId >= 0) {
			out.writeFShort((short) clzId); // > 2 !!
		} else if (ci.isAsciiNameShortString) {
			final Class aClass = ci.getClazz();
			int clid = getIdFromClazz(aClass);
			if (clid != Integer.MIN_VALUE) {
				out.writeFShort((short) clid); // > 2 !!
			} else {
				// ugly hack, also making assumptions about
				// on how the encoder works internally
				final byte[] bufferedName = ci.getBufferedName();
				out.writeFShort((short) 1); // no direct cl id ascii enc
				out.writeFInt((char) bufferedName.length);
				out.writeRawBytes(bufferedName, 0, bufferedName.length);
				registerClassNoLookup(aClass, ci, ci.conf);
			}
		} else {
			encodeClass(out, ci.getClazz());
		}
	}

	/**
	 * Encode class.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param out
	 *            the out
	 * @param c
	 *            the c
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 30 sept. 2023
	 */
	public void encodeClass(final FSTEncoder out, final Class c) throws IOException {
		int clid = getIdFromClazz(c);
		if (clid != Integer.MIN_VALUE) {
			out.writeFShort((short) clid); // > 2 !!
		} else {
			encodeClassName(out, c, out.getConf());
		}
	}

	/**
	 * Encode class name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param out
	 *            the out
	 * @param c
	 *            the c
	 * @param conf
	 *            the conf
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 30 sept. 2023
	 */
	private void encodeClassName(final FSTEncoder out, final Class c, final FSTConfiguration conf) throws IOException {
		out.writeFShort((short) 0); // no direct cl id
		out.writeStringUTF(c.getName());
		registerClassNoLookup(c, null, conf);
	}

	/**
	 * Decode class.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param in
	 *            the in
	 * @param conf
	 *            the conf
	 * @return the FST clazz info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @date 30 sept. 2023
	 */
	public FSTClazzInfo decodeClass(final FSTDecoder in, final FSTConfiguration conf)
			throws IOException, ClassNotFoundException {
		short c = in.readFShort();
		if (c < LOWEST_CLZ_ID) {
			// full class name
			String clName;
			if (c == 0) {
				clName = in.readStringUTF();
			} else {
				clName = in.readStringAsc();
			}
			Class cl = classForName(clName, conf);
			final FSTClazzInfo clInfo = conf.getCLInfoRegistry().getCLInfo(cl, conf);
			registerClassNoLookup(cl, clInfo, conf);
			return clInfo;
		}
		FSTClazzInfo aClass = getClazzFromId(c);
		if (aClass == null) throw new RuntimeException("unable to find class for code " + c);
		return aClass;
	}

	/** The class cache. */
	HashMap<String, Class> classCache = new HashMap<>(200);

	/** The class cache lock. */
	AtomicBoolean classCacheLock = new AtomicBoolean(false);

	/**
	 * Class for name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param clName
	 *            the cl name
	 * @param conf
	 *            the conf
	 * @return the class
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @date 30 sept. 2023
	 */
	public Class classForName(String clName, final FSTConfiguration conf) throws ClassNotFoundException {
		if (parent != null) return parent.classForName(clName, conf);
		try {
			while (!classCacheLock.compareAndSet(false, true)) { ; }
			Class res = classCache.get(clName);
			if (res == null) {
				try {
					res = Class.forName(clName, false, conf.getClassLoader());
				} catch (Throwable th) {
					// if (clName.endsWith("_Struct")) // hack to define struct proxys on the fly if sent from another
					// process
					// {
					// try {
					// clName = clName.substring(0, clName.length() - "_Struct".length());
					// Class onHeapStructClz = classCache.get(clName);
					// if (onHeapStructClz == null)
					// onHeapStructClz = Class.forName(clName, false, conf.getClassLoader() );
					// res = FSTStructFactory.getInstance().getProxyClass(onHeapStructClz);
					// } catch (Throwable th1) {
					// FSTUtil.<RuntimeException>rethrow(th1);
					// }
					// } else

					if (!clName.endsWith("_ActorProxy")) {
						if (conf.getLastResortResolver() != null) {
							Class aClass = conf.getLastResortResolver().getClass(clName);
							if (aClass != null) return aClass;
						}
						throw new RuntimeException(
								"class not found CLASSNAME:" + clName + " loader:" + conf.getClassLoader(), th);
					}
					// same as above for actors. As there is a custom serializer defined for actors, just instantiate
					// actor clazz
					String clName0 = clName;
					clName = clName.substring(0, clName.length() - "_ActorProxy".length());
					Class actorClz = classCache.get(clName);
					if (actorClz == null) {
						try {
							actorClz = Class.forName(clName, false, conf.getClassLoader());
						} catch (ClassNotFoundException clf) {
							if (conf.getLastResortResolver() != null) {
								Class aClass = conf.getLastResortResolver().getClass(clName0);
								if (aClass != null) return aClass;
							}
							FSTUtil.<RuntimeException> rethrow(clf);
						}
					}
					return actorClz;
				}
				if (res != null) { classCache.put(clName, res); }
			}
			return res;
		} finally {
			classCacheLock.set(false);
		}
	}

	/**
	 * Register clazz from other loader.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param cl
	 *            the cl
	 * @date 30 sept. 2023
	 */
	public void registerClazzFromOtherLoader(final Class cl) {
		while (!classCacheLock.compareAndSet(false, true)) { ; }
		classCache.put(cl.getName(), cl);
		classCacheLock.set(false);
	}

	/**
	 * Gets the clazz from id.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @return the clazz from id
	 * @date 30 sept. 2023
	 */
	public FSTClazzInfo getClazzFromId(final int c) {
		FSTClazzInfo res = null;
		if (parent != null) { res = parent.getClazzFromId(c); }
		if (res != null) return res;
		if (c < 0 || c >= idToClz.length) return null;
		return idToClz[c];
	}

}
