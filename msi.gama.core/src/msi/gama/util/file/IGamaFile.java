/*********************************************************************************************
 * 
 *
 * 'IGamaFile.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util.file;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.types.IType;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul
 * Modified on 14 nov. 2011
 * 
 * @todo Description
 * 
 * @param <K>
 * @param <V>
 */
@vars({
	@var(name = IKeyword.NAME, type = IType.STRING),
	@var(name = IKeyword.EXTENSION, type = IType.STRING),
	@var(name = IKeyword.PATH, type = IType.STRING),
	@var(name = IKeyword.EXISTS, type = IType.BOOL),
	@var(name = IKeyword.ISFOLDER, type = IType.BOOL),
	@var(name = IKeyword.READABLE, type = IType.BOOL),
	@var(name = IKeyword.WRITABLE, type = IType.BOOL),
	@var(name = IKeyword.CONTENTS, type = IType.CONTAINER, of = ITypeProvider.FIRST_CONTENT_TYPE, index = ITypeProvider.FIRST_KEY_TYPE) })
public interface IGamaFile<C extends IModifiableContainer, ValueToAdd, K, V> extends
	IModifiableContainer<K, V, K, ValueToAdd>, IAddressableContainer<K, V, K, V> {

	public abstract void setWritable(final boolean w);

	public abstract void setContents(final C cont) throws GamaRuntimeException;

	@Override
	public abstract IGamaFile copy(IScope scope);

	@getter(value = IKeyword.EXISTS, initializer = true)
	public abstract Boolean exists();

	@getter(value = IKeyword.EXTENSION, initializer = true)
	public abstract String getExtension();

	@getter(value = IKeyword.NAME, initializer = true)
	public abstract String getName();

	@getter(value = IKeyword.PATH, initializer = true)
	public abstract String getPath();

	@getter(IKeyword.CONTENTS)
	public abstract C getContents(IScope scope) throws GamaRuntimeException;

	@getter(value = IKeyword.ISFOLDER, initializer = true)
	public abstract Boolean isFolder();

	@getter(value = IKeyword.READABLE, initializer = true)
	public abstract Boolean isReadable();

	@getter(value = IKeyword.WRITABLE, initializer = true)
	public abstract Boolean isWritable();

	public Envelope computeEnvelope(final IScope scope);

}