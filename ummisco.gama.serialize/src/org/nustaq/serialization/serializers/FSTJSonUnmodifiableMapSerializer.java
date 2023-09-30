/*******************************************************************************************************
 *
 * FSTJSonUnmodifiableMapSerializer.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.serializers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.util.FSTUtil;

/**
 * For JSON only, see {@link <a href="https://github.com/RuedigerMoeller/fast-serialization/issues/114">Unable to
 * deserialize unmodifiable collections from JSON</a>}.
 *
 * @author Jakub Kubrynski
 */
public class FSTJSonUnmodifiableMapSerializer extends FSTMapSerializer {

	/** The Constant UNMODIFIABLE_MAP_CLASS. */
	public static final Class<?> UNMODIFIABLE_MAP_CLASS;

	static {
		UNMODIFIABLE_MAP_CLASS = Collections.unmodifiableMap(new HashMap()).getClass();
	}

	@Override
	@SuppressWarnings ("unchecked")
	public Object instantiate(final Class objectClass, final FSTObjectInput in, final FSTClazzInfo serializationInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int streamPosition) throws Exception {
		try {
			// note: unlike with list's JDK uses a single wrapper for unmodifiable maps, so information regarding
			// ordering gets lost.
			// as the enclosed map is private, there is also no possibility to detect that case
			// we could always create a linkedhashmap here, but this would have major performance drawbacks.

			// this only hits JSON codec as JSON codec does not implement a full JDK-serialization fallback (like the
			// binary codecs)
			int len = in.readInt();
			if (UNMODIFIABLE_MAP_CLASS.isAssignableFrom(objectClass)) {
				Map res = new HashMap(len);
				in.registerObject(res, streamPosition, serializationInfo, referencee);
				for (int i = 0; i < len; i++) {
					Object key = in.readObjectInternal();
					Object val = in.readObjectInternal();
					res.put(key, val);
				}
				return Collections.unmodifiableMap(res);
			}
		} catch (Throwable th) {
			FSTUtil.<RuntimeException> rethrow(th);
		}
		return null;
	}

}
