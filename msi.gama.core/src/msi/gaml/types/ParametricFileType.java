/*******************************************************************************************************
 *
 * msi.gaml.types.ParametricFileType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gama.util.IModifiableContainer;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.file.GenericFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.GamaGetter;
import msi.gaml.expressions.IExpression;

/**
 * @author drogoul
 *
 */
public class ParametricFileType extends ParametricType {

	int id;
	int varKind;
	@SuppressWarnings ("rawtypes") final Class<IGamaFile> support;
	final IContainerType<?> bufferType;
	final GamaGetter.Unary<IGamaFile<?, ?>> builder;
	final String alias;
	String plugin;
	static ParametricFileType genericInstance;

	protected ParametricFileType(final String name, @SuppressWarnings ("rawtypes") final Class<IGamaFile> class1,
			final GamaGetter.Unary<IGamaFile<?, ?>> helper, final IType<?> buffer, final IType<?> kt,
			final IType<?> ct) {
		super(Types.FILE, kt, ct);
		support = class1;
		bufferType = (IContainerType<?>) buffer;
		builder = helper;
		alias = name;
	}

	@Override
	public int getNumberOfParameters() {
		return bufferType.getNumberOfParameters();
	}

	@Override
	public boolean isDrawable() {
		return GamaGeometryFile.class.isAssignableFrom(support) || GamaImageFile.class.isAssignableFrom(support);
	}

	public GamaGetter<IGamaFile<?, ?>> getBuilder() {
		return builder;
	}

	@Override
	public void init(final int kind, final int index, final String name, final Class<IContainer<?, ?>> clazz) {
		this.id = index;
		this.varKind = kind;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(final Object c) {
		if (c instanceof ParametricFileType) { return ((ParametricFileType) c).id() == id(); }
		return super.equals(c);
	}

	@Override
	public IGamaFile<?, ?> cast(final IScope scope, final Object obj, final Object param, final IType<?> keyType,
			final IType<?> contentType, final boolean copy) {
		if (obj == null) { return null; }
		if (obj instanceof IGamaFile) {
			if (support.isInstance(obj)) {
				return (IGamaFile<?, ?>) obj;
			} else {
				return cast(scope, ((IGamaFile<?, ?>) obj).getPath(scope), param, keyType, contentType, copy);
			}
		}
		if (obj instanceof String) {
			if (param == null) { return createFile(scope, (String) obj, null); }
			if (param instanceof IContainer.Modifiable) {
				return createFile(scope, (String) obj, (IModifiableContainer<?, ?, ?, ?>) param);
			}
		}
		return null;
	}

	public static ParametricFileType getGenericInstance() {

		if (genericInstance == null) {
			genericInstance = new ParametricFileType("generic_file", IGamaFile.class,
					(s, o) -> new GenericFile(s, (String) o), Types.LIST, Types.NO_TYPE, Types.NO_TYPE);
		}
		return genericInstance;
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public Class toClass() {
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
	public String getDefiningPlugin() {
		return plugin;
	}

	@Override
	public String toString() {
		return alias;
	}

	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	// if (plugin != null) {
	// meta.put(GamlProperties.PLUGINS, this.plugin);
	// meta.put(GamlProperties.TYPES, this.getName());
	// }
	// }

	@SuppressWarnings ({ "rawtypes", "unchecked" })
	public IGamaFile createFile(final IScope scope, final String path, final IModifiableContainer contents) {
		final IGamaFile file = builder.get(scope, path);
		if (contents != null) {
			file.setWritable(scope, true);
			file.setContents(contents);
		}
		return file;
	}

	@Override
	public IType<?> getWrappedType() {
		return bufferType;
	}

	@Override
	public boolean isAssignableFrom(final IType<?> l) {
		return l == this;
	}

	@Override
	public IContainerType<?> typeIfCasting(final IExpression exp) {

		return this;
	}

}