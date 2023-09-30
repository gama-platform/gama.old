/*******************************************************************************************************
 *
 * FSTProxySerializer.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.serializers;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * The Class FSTProxySerializer.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 30 sept. 2023
 */
public class FSTProxySerializer extends FSTBasicObjectSerializer {

	@Override
	public void writeObject(final FSTObjectOutput out, final Object toWrite, final FSTClazzInfo clzInfo,
			final FSTClazzInfo.FSTFieldInfo referencedBy, final int streamPosition) throws IOException {
		Class<?>[] ifaces = clzInfo.getClazz().getInterfaces();
		// ClassLoader cl = out.getConf().getClassLoader();
		out.writeInt(ifaces.length);
		for (Class i : ifaces) { out.writeUTF(i.getName()); }
		out.writeObject(Proxy.getInvocationHandler(toWrite));
	}

	@Override
	public Object instantiate(final Class objectClass, final FSTObjectInput in, final FSTClazzInfo serializationInfo,
			final FSTClazzInfo.FSTFieldInfo referencee, final int streamPositioin)
			throws IOException, ClassNotFoundException {
		ClassLoader cl = in.getConf().getClassLoader();
		int numIfaces = in.readInt();
		String[] interfaces = new String[numIfaces];
		for (int i = 0; i < numIfaces; i++) { interfaces[i] = in.readUTF(); }
		Class[] classObjs = new Class[interfaces.length];

		for (int i = 0; i < interfaces.length; ++i) {
			try {
				classObjs[i] = Class.forName(interfaces[i], false, cl);
			} catch (ClassNotFoundException e) {
				classObjs[i] = Class.forName(interfaces[i], false, this.getClass().getClassLoader());
			}
		}
		InvocationHandler ih = (InvocationHandler) in.readObject();
		Object res = Proxy.newProxyInstance(in.getConf().getClassLoader(), classObjs, ih);
		in.registerObject(res, streamPositioin, serializationInfo, referencee);
		return res;
	}
}