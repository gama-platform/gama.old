/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.file;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;

/**
 * Written by drogoul
 * Modified on 14 nov. 2011
 * 
 * @todo Description
 * 
 * @param <K>
 * @param <V>
 */
@vars({ @var(name = IGamaFile.NAME, type = IType.STRING_STR),
	@var(name = IGamaFile.EXTENSION, type = IType.STRING_STR),
	@var(name = IGamaFile.PATH, type = IType.STRING_STR),
	@var(name = IGamaFile.EXISTS, type = IType.BOOL_STR),
	@var(name = IGamaFile.ISFOLDER, type = IType.BOOL_STR),
	@var(name = IGamaFile.READABLE, type = IType.BOOL_STR),
	@var(name = IGamaFile.WRITABLE, type = IType.BOOL_STR),
	@var(name = IGamaFile.CONTENTS, type = IType.CONTAINER_STR) })
public interface IGamaFile<K, V> extends IContainer<K, V> {

	public static final String NAME = "name";
	public static final String EXTENSION = "extension";
	public static final String PATH = "path";
	public static final String CONTENTS = "contents";
	public static final String READABLE = "readable";
	public static final String WRITABLE = "writable";
	public static final String EXISTS = "exists";
	public static final String ISFOLDER = "is_folder";

	public abstract void setWritable(final boolean w);

	public abstract void setContents(final IContainer<K, V> cont) throws GamaRuntimeException;

	@Override
	public abstract IGamaFile copy();

	@getter(var = EXISTS, initializer = true)
	public abstract Boolean exists();

	@getter(var = EXTENSION, initializer = true)
	public abstract String getExtension();

	@getter(var = NAME, initializer = true)
	public abstract String getName();

	@getter(var = PATH, initializer = true)
	public abstract String getPath();

	@getter(var = CONTENTS)
	public abstract IContainer getContents() throws GamaRuntimeException;

	@getter(var = ISFOLDER, initializer = true)
	public abstract Boolean isFolder();

	public abstract boolean isPgmFile();

	@getter(var = READABLE, initializer = true)
	public abstract Boolean isReadable();

	@getter(var = WRITABLE, initializer = true)
	public abstract Boolean isWritable();

	public abstract String getKeyword();

}