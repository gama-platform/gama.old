/**
 *
 */
package msi.gaml.types;

import msi.gama.common.GamaPreferences.GenericFile;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gaml.compilation.GamaHelper;

/**
 * @author drogoul
 *
 */
public class ParametricFileType extends ParametricType {

	int id;
	int varKind;
	final Class<IGamaFile> support;
	final IContainerType bufferType;
	final GamaHelper<IGamaFile> builder;
	final String alias;
	String plugin;
	static ParametricFileType genericInstance;

	protected ParametricFileType(final String name, final Class<IGamaFile> fileClass,
		final GamaHelper<IGamaFile> helper,
		final IType buffer, final IType kt, final IType ct) {
		super(Types.FILE, kt, ct);
		support = fileClass;
		bufferType = (IContainerType) buffer;
		builder = helper;
		alias = name;
	}

	@Override
	public boolean isDrawable() {
		return GamaGeometryFile.class.isAssignableFrom(support) || GamaImageFile.class.isAssignableFrom(support);
	}

	public GamaHelper<IGamaFile> getBuilder() {
		return builder;
	}

	@Override
	public void init(final int kind, final int index, final String name, final Class ... supports) {
		this.id = index;
		this.varKind = kind;
	}

	@Override
	public IGamaFile cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentType, final boolean copy) {
		if ( obj == null ) { return null; }
		if ( obj instanceof IGamaFile && support.isInstance(obj) ) { return (IGamaFile) obj; }
		if ( obj instanceof String ) {
			if ( param == null ) { return createFile(scope, (String) obj, null); }
			if ( param instanceof IContainer.Modifiable ) { return createFile(scope, (String) obj,
				(IModifiableContainer) param); }
		}
		return null;
	}

	public static ParametricFileType getGenericInstance() {
		if ( genericInstance == null ) {
			genericInstance = new ParametricFileType("generic", IGamaFile.class, new GamaHelper<IGamaFile>() {

				@Override
				public IGamaFile run(final IScope s, final Object ... o) {
					return new GenericFile((String) o[0]);
				}
			},
				Types.LIST,
				Types.NO_TYPE, Types.NO_TYPE);
		}
		return genericInstance;
	}

	@Override
	public Class<IGamaFile> toClass() {
		return support;
	}

	@Override
	public int getVarKind() {
		return varKind;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public void setDefiningPlugin(final String plugin) {
		this.plugin = plugin;
	}

	@Override
	public String toString() {
		return alias;
	}



	public IGamaFile createFile(final IScope scope, final String path, final IModifiableContainer contents) {
		IGamaFile file = builder.run(scope, path);
		if ( contents != null ) {
			file.setWritable(true);
			file.setContents(contents);
		}
		return file;
	}

	@Override
	public IType getWrappedType() {
		return bufferType;
	}

}
