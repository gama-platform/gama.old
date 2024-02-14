/*******************************************************************************************************
 *
 * FSTCollectionSerializer.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.serializers;

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.util.FSTUtil;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 10.11.12 Time: 15:55 To change this template use File | Settings | File
 * Templates.
 */
public class FSTCollectionSerializer extends FSTBasicObjectSerializer {

	@Override
	public void writeObject(final FSTObjectOutput out, final Object toWrite, final FSTClazzInfo clzInfo,
			final FSTClazzInfo.FSTFieldInfo referencedBy, final int streamPosition) throws IOException {
		Collection col = (Collection) toWrite;
		int size = col.size();
		out.writeInt(size);
		Class lastClz = null;
		FSTClazzInfo lastInfo = null;
		if (col.getClass() == ArrayList.class) {
			List l = (List) col;
			for (int i = 0; i < size; i++) {
				Object o = l.get(i);
				if (o != null) {
					lastInfo = out.writeObjectInternal(o, o.getClass() == lastClz ? lastInfo : null, (Class[]) null);
					lastClz = o.getClass();
				} else {
					out.writeObjectInternal(o, null, (Class[]) null);
				}
			}
		} else {
			for (Object o : col) {
				if (o != null) {
					lastInfo = out.writeObjectInternal(o, o.getClass() == lastClz ? lastInfo : null, (Class[]) null);
					lastClz = o.getClass();
				} else {
					out.writeObjectInternal(o, null, (Class[]) null);
				}
			}
		}
	}

	@SuppressWarnings ("unchecked")
	@Override
	public Object instantiate(final Class objectClass, final FSTObjectInput in, final FSTClazzInfo serializationInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int streamPosition) throws Exception {
		try {
			Object res = null;
			int len = in.readInt();
			if (objectClass == ArrayList.class) {
				res = new ArrayList(len);
			} else if (objectClass == HashSet.class) {
				res = new HashSet(len);
			} else if (objectClass == Vector.class) {
				res = new Vector(len);
			} else if (objectClass == LinkedList.class) {
				res = new LinkedList();
			} else if (AbstractList.class.isAssignableFrom(objectClass)
					&& objectClass.getName().startsWith("java.util.Arrays")) {
				// some collections produced by JDK are not properly instantiable (e.g. Arrays.ArrayList), fall back to
				// arraylist then
				res = new ArrayList<>();
			} else {
				res = objectClass.newInstance();
			}
			in.registerObject(res, streamPosition, serializationInfo, referencee);
			Collection<Object> col = (Collection<Object>) res;
			if (col instanceof ArrayList) { ((ArrayList) col).ensureCapacity(len); }
			for (int i = 0; i < len; i++) {
				final Object o = in.readObjectInternal();
				col.add(o);
			}
			return res;
		} catch (Throwable th) {
			FSTUtil.<RuntimeException> rethrow(th);
		}
		return null;
	}
}
