/**
 * 
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
public interface IGamaFile<K, V> extends IGamaContainer<K, V> {

	public static final String NAME = "name";
	public static final String EXTENSION = "extension";
	public static final String PATH = "path";
	public static final String CONTENTS = "contents";
	public static final String READABLE = "readable";
	public static final String WRITABLE = "writable";
	public static final String EXISTS = "exists";
	public static final String ISFOLDER = "is_folder";

	public abstract void setWritable(final boolean w);

	public abstract void setContents(final IGamaContainer<K, V> cont) throws GamaRuntimeException;

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
	public abstract IGamaContainer getContents() throws GamaRuntimeException;

	@getter(var = ISFOLDER, initializer = true)
	public abstract Boolean isFolder();

	public abstract boolean isPgmFile();

	@getter(var = READABLE, initializer = true)
	public abstract Boolean isReadable();

	@getter(var = WRITABLE, initializer = true)
	public abstract Boolean isWritable();

	public abstract String getKeyword();

}