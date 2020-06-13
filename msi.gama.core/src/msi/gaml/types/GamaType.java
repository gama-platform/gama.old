/*******************************************************************************************************
 *
 * msi.gaml.types.GamaType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.ICollector;
import msi.gama.util.IContainer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 25 aout 2010
 *
 * The superclass of all types descriptions in GAMA. Provides convenience methods, as well as some basic definitions.
 * Types allow to manipulate any Java class as a type in GAML. To be recognized by GAML, subclasses must be annotated
 * with the @type annotation (see GamlAnnotations).
 *
 * Types are primarily used for conversions between values. They are also intended to support the operators specific to
 * the objects they encompass (but this is not mandatory, as these operators need to be defined as static ones (and thus
 * can be defined anywhere)
 *
 * Primary (simple) types also serve as the basis of parametric types (see ParametricType).
 *
 */
public abstract class GamaType<Support> implements IType<Support> {

	protected int id;
	protected String name;
	protected Class<Support> support;
	Map<String, OperatorProto> getters;
	protected IType<? super Support> parent;
	protected boolean parented;
	protected int varKind;
	protected String plugin;

	@Override
	public String getTitle() {
		return getName();
	}

	@Override
	public int getNumberOfParameters() {
		return 0;
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

	@Override
	public String getDocumentation() {
		doc documentation;
		documentation = getClass().getAnnotation(doc.class);
		if (documentation == null) {
			final type t = getClass().getAnnotation(type.class);
			if (t != null) {
				final doc[] docs = t.doc();
				if (docs != null && docs.length > 0) {
					documentation = docs[0];
				}
			}
		}
		String result;
		if (documentation == null) {
			result = "Type " + getName() + getSupportName();
		} else {
			result = documentation.value();
		}
		return result + getFieldDocumentation();
	}

	public String getSupportName() {
		return ", wraps Java objects of class " + support.getSimpleName();
	}

	public String getFieldDocumentation() {
		if (getters == null) { return ""; }
		final StringBuilder sb = new StringBuilder(200);
		sb.append("<b><br/>Fields :</b><ul>");
		for (final OperatorProto f : getters.values()) {
			sb.append("<li> ").append(f.getName()).append(" of type ").append(f.returnType)
					.append(getFieldDocumentation(f));
			sb.append("</li>");
		}

		sb.append("</ul>");
		return sb.toString();
	}

	private String getFieldDocumentation(final OperatorProto prototype) {
		final StringBuilder sb = new StringBuilder(200);

		final vars annot = prototype.getSupport().getAnnotation(vars.class);
		if (annot != null) {
			final variable[] allVars = annot.value();
			for (final variable v : allVars) {
				if (v.name().equals(prototype.getName())) {
					if (v.doc().length > 0) {
						sb.append(", ").append(v.doc()[0].value());
					}
					break;
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		// Nothing
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return name;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void init(final int varKind, final int id, final String name, final Class<Support> support) {
		this.varKind = varKind;
		this.id = id;
		this.name = name;
		this.support = support;
	}

	@Override
	public void setSupport(final Class<Support> clazz) {
		support = clazz;
		// supports = new Class[] { clazz };
	}

	@Override
	public int getVarKind() {
		return varKind;
	}

	@Override
	public void setParent(final IType<? super Support> p) {
		parented = true;
		parent = p;
	}

	@Override
	public IType<? super Support> getParent() {
		return parent;
	}

	@Override
	public void setFieldGetters(final Map<String, OperatorProto> map) {
		map.replaceAll((final String key, final OperatorProto each) -> each.copyWithSignature(this));

		getters = map;
		// AD 20/09/13 Added the initialization of the type containing the
		// fields

	}

	@Override
	public OperatorProto getGetter(final String field) {
		if (getters == null) { return null; }
		return getters.get(field);
	}

	@Override
	public Map<String, OperatorProto> getFieldGetters() {
		return getters == null ? Collections.EMPTY_MAP : getters;
	}

	@Override
	public abstract Support cast(IScope scope, final Object obj, final Object param, boolean copy)
			throws GamaRuntimeException;

	@Override
	public Support cast(final IScope scope, final Object obj, final Object param, final IType<?> keyType,
			final IType<?> contentType, final boolean copy) throws GamaRuntimeException {
		// by default
		return cast(scope, obj, param, copy);
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(final Object c) {
		if (c instanceof IType) { return ((IType<?>) c).id() == id; }
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String asPattern() {
		final boolean vowel = StringUtils.startsWithAny(name, vowels);
		return "${" + (vowel ? "an_" : "a_") + name + "}";
	}

	@Override
	public Class<? extends Support> toClass() {
		return support;
	}

	@Override
	public boolean isAgentType() {
		return false;
	}

	@Override
	public boolean isSkillType() {
		return false;
	}

	@Override
	public IType<?> getContentType() {
		return Types.NO_TYPE;
	}

	@Override
	public IType<?> getKeyType() {
		return Types.NO_TYPE;
	}

	@Override
	public String getSpeciesName() {
		return null;
	}

	@Override
	public SpeciesDescription getSpecies() {
		return null;
	}

	@Override
	public SpeciesDescription getDenotedSpecies() {
		return getSpecies();
	}

	@Override
	public boolean isParented() {
		return parented;
	}

	protected boolean isSuperTypeOf(final IType<?> type) {
		if (type == null) { return false; }
		if (parented && type.isParented()) { return type == this || isSuperTypeOf(type.getParent()); }
		final Class<?> remote = type.toClass();
		return support.isAssignableFrom(remote);
	}

	@Override
	public boolean isAssignableFrom(final IType<?> t) {
		return t == null ? false : this == t || isSuperTypeOf(t);
	}

	@Override
	public boolean isTranslatableInto(final IType<?> t) {
		return t.isAssignableFrom(this);
	}

	@Override
	public boolean canBeTypeOf(final IScope scope, final Object c) {
		if (c == null) { return acceptNullInstances(); }
		return support.isAssignableFrom(c.getClass());
	}

	@Override
	public boolean isParametricType() {
		return false;
	}

	@Override
	public boolean isParametricFormOf(final IType<?> l) {
		return false;
	}

	/**
	 * @return true if this type can have nil as an instance
	 */
	protected boolean acceptNullInstances() {
		return getDefault() == null;
	}

	@Override
	public IType<?> coerce(final IType<?> expr, final IDescription context) {
		// Nothing to do in the general case : we rely on Java polymorphism.
		return null;
	}

	@Override
	public int distanceTo(final IType<?> type) {
		if (type == this) { return 0; }
		if (type == null) { return Integer.MAX_VALUE; }
		if (parented) {
			if (isSuperTypeOf(type)) { return 1 + distanceTo(type.getParent()); }
			return 1 + getParent().distanceTo(type);
		}
		if (isTranslatableInto(type)) { return 1; }
		if (isAssignableFrom(type)) { return 1; }
		return Integer.MAX_VALUE;
	}

	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public IType<? super Support> findCommonSupertypeWith(final IType<?> type) {
		if (type == this) { return this; }
		if (type == Types.NO_TYPE) { return getDefault() == null ? this : (GamaNoType) type; }
		if (type.isTranslatableInto(this)) { return this; }
		if (this.isTranslatableInto(type)) { return (IType) type; }
		return getParent().findCommonSupertypeWith(type.getParent());
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean isNumber() {
		return false;
	}

	@Override
	public boolean isFixedLength() {
		return true;
	}

	public static Object toType(final IScope scope, final Object value, final IType<?> type, final boolean copy) {
		if (type == null || type.id() == IType.NONE) { return value; }
		return type.cast(scope, value, null, copy);
	}

	public IType<?> keyTypeIfCasting(final IExpression exp) {
		return getKeyType();
	}

	public IType<?> contentsTypeIfCasting(final IExpression exp) {
		return getContentType();
	}

	@Override
	public IType<Support> getGamlType() {
		return this;
	}

	@Override
	public IType<?> typeIfCasting(final IExpression exp) {
		return from(this, keyTypeIfCasting(exp), contentsTypeIfCasting(exp));
	}

	public static IType<?> from(final TypeDescription species) {
		return from(Types.SPECIES, Types.INT, species.getGamlType());
	}

	public static IContainerType<?> from(final IContainerType<IContainer<?, ?>> t, final IType<?> keyType,
			final IType<?> contentType) {
		if ((keyType == null || keyType == Types.NO_TYPE) && (contentType == null || contentType == Types.NO_TYPE)) {
			return t;
		}
		final IType<?> kt = keyType == Types.NO_TYPE ? t.getGamlType().getKeyType() : keyType;
		final IType<?> ct = contentType == Types.NO_TYPE ? t.getGamlType().getContentType() : contentType;
		return ParametricType.createParametricType(t.getGamlType(), kt, ct);
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	public static IType<?> from(final IType<?> t, final IType<?> keyType, final IType<?> contentType) {
		if (keyType == null || contentType == null) { return t; }
		if (t instanceof IContainerType) {
			if (!(t instanceof GamaSpeciesType)) {
				if (contentType.isAssignableFrom(t.getContentType()) && keyType.isAssignableFrom(t.getKeyType())) {
					return t;
				}
			}
			return from((IContainerType) t, keyType, contentType);
		}
		return t;
	}

	public static final int TYPE = 0;
	public static final int CONTENT = 1;
	public static final int KEY = 2;
	public static final int DENOTED = 3;

	public static IType<?> findCommonType(final IExpression[] elements, final int kind) {
		final IType<?> result = Types.NO_TYPE;
		if (elements.length == 0) { return result; }
		try (final ICollector<IType<?>> types = Collector.getOrderedSet()) {
			for (final IExpression e : elements) {
				// TODO Indicates a previous error in compiling expressions. Maybe
				// we should cut this
				// part
				if (e == null) {
					continue;
				}
				final IType<?> eType = e.getGamlType();
				types.add(kind == TYPE ? eType : kind == CONTENT ? eType.getContentType() : eType.getKeyType());
			}
			final IType<?>[] array = types.items().toArray(new IType[types.size()]);
			return findCommonType(array);
		}
	}

	public static IType<?> findCommonType(final IType<?>... types) {
		IType<?> result = Types.NO_TYPE;
		if (types.length == 0) { return result; }
		result = types[0];
		if (types.length == 1) { return result; }
		for (int i = 1; i < types.length; i++) {
			final IType<?> currentType = types[i];
			if (currentType == Types.NO_TYPE) {
				if (result.getDefault() != null) {
					result = Types.NO_TYPE;
				}
			} else {
				result = result.findCommonSupertypeWith(currentType);
			}
		}
		return result;
	}

	/**
	 * Return the type of the object passed in parameter
	 *
	 * @param obj
	 * @return
	 */
	public static IType<?> of(final Object obj) {
		if (obj instanceof IValue) { return ((IValue) obj).getGamlType(); }
		if (obj instanceof IExpression) { return ((IExpression) obj).getGamlType(); }
		if (obj == null) { return Types.NO_TYPE; }
		return Types.get(obj.getClass());
	}

	/**
	 * @return
	 */
	public static IType<?> findSpecificType(final IType<?> castingType, final IType<?> originalType) {
		return requiresCasting(castingType, originalType) ? castingType : originalType;
	}

	public static boolean requiresCasting(final IType<?> castingType, final IType<?> originalType) {
		if (castingType == null || castingType == Types.NO_TYPE || castingType.isAssignableFrom(originalType)) {
			return false;
		}
		return true;
	}

	@Override
	public void setDefiningPlugin(final String plugin) {
		this.plugin = plugin;
	}

	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	// if (plugin != null) {
	// meta.put(GamlProperties.PLUGINS, this.plugin);
	// meta.put(GamlProperties.TYPES, this.name);
	// }
	// }

	@Override
	public boolean isDrawable() {
		return false;
	}

	@Override
	public IType<?> getWrappedType() {
		return Types.NO_TYPE;
	}

	@Override
	public boolean isCompoundType() {
		return false;
	}

}
