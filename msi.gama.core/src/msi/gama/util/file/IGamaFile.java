/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.file;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
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
@vars({ @var(name = IKeyword.NAME, type = IType.STRING), @var(name = IKeyword.EXTENSION, type = IType.STRING),
	@var(name = IKeyword.PATH, type = IType.STRING), @var(name = IKeyword.EXISTS, type = IType.BOOL),
	@var(name = IKeyword.ISFOLDER, type = IType.BOOL), @var(name = IKeyword.READABLE, type = IType.BOOL),
	@var(name = IKeyword.WRITABLE, type = IType.BOOL), @var(name = IKeyword.CONTENTS, type = IType.CONTAINER) })
public interface IGamaFile<K, V> extends IContainer<K, V> {

	public abstract void setWritable(final boolean w);

	public abstract void setContents(final IContainer<K, V> cont) throws GamaRuntimeException;

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
	public abstract IContainer getContents(IScope scope) throws GamaRuntimeException;

	@getter(value = IKeyword.ISFOLDER, initializer = true)
	public abstract Boolean isFolder();

	@getter(value = IKeyword.READABLE, initializer = true)
	public abstract Boolean isReadable();

	@getter(value = IKeyword.WRITABLE, initializer = true)
	public abstract Boolean isWritable();

	public Envelope computeEnvelope(final IScope scope);

}