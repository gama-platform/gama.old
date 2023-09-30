/*******************************************************************************************************
 *
 * FSTMapSerializer.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.serializers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 10.11.12 Time: 17:47 To change this template use File | Settings | File
 * Templates.
 */
public class FSTMapSerializer extends FSTBasicObjectSerializer {

	@Override
	public void writeObject(final FSTObjectOutput out, final Object toWrite, final FSTClazzInfo clzInfo,
			final FSTClazzInfo.FSTFieldInfo referencedBy, final int streamPosition) throws IOException {
		Map col = (Map) toWrite;
		out.writeInt(col.size());
		FSTClazzInfo lastKClzI = null;
		FSTClazzInfo lastVClzI = null;
		Class lastKClz = null;
		Class lastVClz = null;
		for (Iterator iterator = col.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry next = (Map.Entry) iterator.next();
			Object key = next.getKey();
			Object value = next.getValue();
			if (key != null && value != null) {
				lastKClzI = out.writeObjectInternal(key, key.getClass() == lastKClz ? lastKClzI : null, (Class[]) null);
				lastVClzI =
						out.writeObjectInternal(value, value.getClass() == lastVClz ? lastVClzI : null, (Class[]) null);
				lastKClz = key.getClass();
				lastVClz = value.getClass();
			} else {
				out.writeObjectInternal(key, null, (Class[]) null);
				out.writeObjectInternal(value, null, (Class[]) null);
			}

		}
	}

	@Override
	public Object instantiate(final Class objectClass, final FSTObjectInput in, final FSTClazzInfo serializationInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int streamPosition) throws Exception {
		Object res = null;
		int len = in.readInt();
		if (objectClass == HashMap.class) {
			res = new HashMap(len);
		} else if (objectClass == Hashtable.class) {
			res = new Hashtable(len);
		} else {
			res = objectClass.newInstance();
		}
		in.registerObject(res, streamPosition, serializationInfo, referencee);
		Map col = (Map) res;
		for (int i = 0; i < len; i++) {
			Object key = in.readObjectInternal();
			Object val = in.readObjectInternal();
			col.put(key, val);
		}
		return res;
	}
}
