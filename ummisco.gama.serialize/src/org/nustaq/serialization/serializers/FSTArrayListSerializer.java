/*******************************************************************************************************
 *
 * FSTArrayListSerializer.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.serializers;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ruedi on 07.03.14.
 */
import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.util.FSTUtil;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 10.11.12 Time: 15:55 To change this template use File | Settings | File
 * Templates.
 */
public class FSTArrayListSerializer extends FSTBasicObjectSerializer {

	@Override
	public void writeObject(final FSTObjectOutput out, final Object toWrite, final FSTClazzInfo clzInfo,
			final FSTClazzInfo.FSTFieldInfo referencedBy, final int streamPosition) throws IOException {
		ArrayList col = (ArrayList) toWrite;
		int size = col.size();
		out.writeInt(size);
		Class lastClz = null;
		FSTClazzInfo lastInfo = null;
		for (int i = 0; i < size; i++) {
			Object o = col.get(i);
			if (o != null) {
				lastInfo = out.writeObjectInternal(o, o.getClass() == lastClz ? lastInfo : null, (Class[]) null);
				lastClz = o.getClass();
			} else {
				out.writeObjectInternal(o, null, (Class[]) null);
			}
		}
	}

	@Override
	public Object instantiate(final Class objectClass, final FSTObjectInput in, final FSTClazzInfo serializationInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int streamPosition) throws Exception {
		try {
			int len = in.readInt();
			ArrayList<Object> res = new ArrayList<>(len);
			in.registerObject(res, streamPosition, serializationInfo, referencee);
			for (int i = 0; i < len; i++) {
				final Object o = in.readObjectInternal();
				res.add(o);
			}
			return res;
		} catch (Throwable th) {
			FSTUtil.<RuntimeException> rethrow(th);
		}
		return null;
	}

}
