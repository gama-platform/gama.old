/*******************************************************************************************************
 *
 * ParametricFileType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

	/** The id. */
	int id;

	/** The var kind. */
	int varKind;

	/** The support. */
	@SuppressWarnings ("rawtypes") final Class<IGamaFile> support;

	/** The buffer type. */
	final IContainerType<?> bufferType;

	/** The builder. */
	final GamaGetter<IGamaFile<?, ?>> builder;

	/** The alias. */
	final String alias;

	/** The plugin. */
	String plugin;

	/** The generic instance. */
	static ParametricFileType genericInstance;

	/**
	 * Instantiates a new parametric file type.
	 *
	 * @param name
	 *            the name
	 * @param class1
	 *            the class 1
	 * @param helper
	 *            the helper
	 * @param buffer
	 *            the buffer
	 * @param kt
	 *            the kt
	 * @param ct
	 *            the ct
	 */
	protected ParametricFileType(final String name, @SuppressWarnings ("rawtypes") final Class<IGamaFile> class1,
			final GamaGetter<IGamaFile<?, ?>> helper, final IType<?> buffer, final IType<?> kt, final IType<?> ct) {
		super(Types.FILE, kt, ct);
		support = class1;
		bufferType = (IContainerType<?>) buffer;
		builder = helper;
		alias = name;
	}

	@Override
	public int getNumberOfParameters() { return bufferType.getNumberOfParameters(); }

	@Override
	public boolean isDrawable() {
		return GamaGeometryFile.class.isAssignableFrom(support) || GamaImageFile.class.isAssignableFrom(support);
	}

	/**
	 * Gets the builder.
	 *
	 * @return the builder
	 */
	public GamaGetter<IGamaFile<?, ?>> getBuilder() { return builder; }

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
		if (c instanceof ParametricFileType) return ((ParametricFileType) c).id() == id();
		return super.equals(c);
	}

	@Override
	public IGamaFile<?, ?> cast(final IScope scope, final Object obj, final Object param, final IType<?> keyType,
			final IType<?> contentType, final boolean copy) {
		if (obj == null) return null;
		if (obj instanceof IGamaFile) {
			if (support.isInstance(obj)) return (IGamaFile<?, ?>) obj;
			return cast(scope, ((IGamaFile<?, ?>) obj).getPath(scope), param, keyType, contentType, copy);
		}
		if (obj instanceof String) {
			if (param == null) return createFile(scope, (String) obj, null);
			if (param instanceof IContainer.Modifiable)
				return createFile(scope, (String) obj, (IModifiableContainer<?, ?, ?, ?>) param);
		}
		return null;
	}

	/**
	 * Gets the generic instance.
	 *
	 * @return the generic instance
	 */
	public static ParametricFileType getGenericInstance() {

		if (genericInstance == null) {
			genericInstance = new ParametricFileType("generic_file", IGamaFile.class,
					(s, o) -> new GenericFile(s, (String) o[0]), Types.LIST, Types.NO_TYPE, Types.NO_TYPE);
		}
		return genericInstance;
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public Class toClass() {
		return support;
	}

	@Override
	public int getVarKind() { return varKind; }

	@Override
	public int id() {
		return id;
	}

	@Override
	public void setDefiningPlugin(final String plugin) { this.plugin = plugin; }

	@Override
	public String getDefiningPlugin() { return plugin; }

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

	/**
	 * Creates the file.
	 *
	 * @param scope
	 *            the scope
	 * @param path
	 *            the path
	 * @param contents
	 *            the contents
	 * @return the i gama file
	 */
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
	public IType<?> getWrappedType() { return bufferType; }

	@Override
	public boolean isAssignableFrom(final IType<?> l) {
		return l == this;
	}

	@Override
	public IContainerType<?> typeIfCasting(final IExpression exp) {

		return this;
	}

}