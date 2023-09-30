/*******************************************************************************************************
 *
 * FSTObjectInputNoShared.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Subclass optimized for "unshared mode". Cycles and Objects referenced more than once will not be detected.
 * Additionally JDK compatibility is not supported (read/writeObject and stuff). Use case is highperformance
 * serialization of plain cycle free data (e.g. messaging). Can perform significantly faster (20-40%).
 */
public class FSTObjectInputNoShared extends FSTObjectInput {

	/**
	 * Instantiates a new FST object input no shared.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @date 30 sept. 2023
	 */
	public FSTObjectInputNoShared(final FSTConfiguration conf) {
		super(conf);
		conf.setShareReferences(false);
	}

	@Override
	public void registerObject(final Object o, final int streamPosition, final FSTClazzInfo info,
			final FSTClazzInfo.FSTFieldInfo referencee) {}

	@Override
	public void resetForReuse(final InputStream in) throws IOException {
		if (closed) throw new RuntimeException("can't reuse closed stream");
		getCodec().reset();
		getCodec().setInputStream(in);
	}

	@Override
	public void resetForReuseCopyArray(final byte bytes[], final int off, final int len) throws IOException {
		if (closed) throw new RuntimeException("can't reuse closed stream");
		getCodec().reset();
		getCodec().resetToCopyOf(bytes, off, len);
	}

	@Override
	protected Object instantiateAndReadNoSer(final Class c, final FSTClazzInfo clzSerInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int readPos) throws Exception {
		Object newObj;
		newObj = clzSerInfo.newInstance(getCodec().isMapBased());
		if (newObj == null) throw new IOException(referencee.getDesc() + ":Failed to instantiate '" + c.getName()
				+ "'. Register a custom serializer implementing instantiate or define empty constructor..");
		if (clzSerInfo.isExternalizable()) {
			getCodec().ensureReadAhead(readExternalReadAHead);
			((Externalizable) newObj).readExternal(this);
			getCodec().readExternalEnd();
		} else {
			FSTClazzInfo.FSTFieldInfo[] fieldInfo = clzSerInfo.getFieldInfo();
			readObjectFields(referencee, clzSerInfo, fieldInfo, newObj, 0, 0);
		}
		return newObj;
	}

}
