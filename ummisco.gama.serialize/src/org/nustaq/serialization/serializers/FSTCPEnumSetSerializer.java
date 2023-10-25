/*******************************************************************************************************
 *
 * FSTCPEnumSetSerializer.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.serializers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.EnumSet;

import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.util.FSTUtil;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 11.11.12 Time: 04:09
 *
 * EnumSet Serializer for Cross Platform serialization. Writes full Strings instead of ordinals
 *
 */
@SuppressWarnings ("unchecked")
public class FSTCPEnumSetSerializer extends FSTBasicObjectSerializer {

	/** The elem type. */
	Field elemType;

	@Override
	public void writeObject(final FSTObjectOutput out, final Object toWrite, final FSTClazzInfo clzInfo,
			final FSTClazzInfo.FSTFieldInfo referencedBy, final int streamPosition) throws IOException {
		EnumSet enset = (EnumSet) toWrite;
		int count = 0;
		out.writeInt(enset.size());
		if (enset.isEmpty()) { // WTF only way to determine enumtype ..
			EnumSet compl = EnumSet.complementOf(enset);
			out.writeStringUTF(FSTUtil.getRealEnumClass(compl.iterator().next().getClass()).getName());
		} else {
			for (Object element : enset) {
				if (count == 0) { out.writeStringUTF(FSTUtil.getRealEnumClass(element.getClass()).getName()); }
				out.writeStringUTF(element.toString());
				count++;
			}
		}
	}

	/**
	 * @return true if FST can skip a search for same instances in the serialized ObjectGraph. This speeds up reading
	 *         and writing and makes sense for short immutable such as Integer, Short, Character, Date, .. . For those
	 *         classes it is more expensive (CPU, size) to do a lookup than to just write the Object twice in case.
	 */
	@Override
	public boolean alwaysCopy() {
		return false;
	}

	@Override
	public Object instantiate(final Class objectClass, final FSTObjectInput in, final FSTClazzInfo serializationInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int streamPosition) throws Exception {
		int len = in.readInt();
		Class elemCl = FSTUtil.getRealEnumClass(in.getClassForName(in.readStringUTF()));
		EnumSet enSet = EnumSet.noneOf(elemCl);
		in.registerObject(enSet, streamPosition, serializationInfo, referencee); // IMPORTANT, else tracking double
																					// objects will fail
		for (int i = 0; i < len; i++) {
			String val = in.readStringUTF();
			enSet.add(Enum.valueOf(elemCl, val));
		}
		return enSet;
	}
}
